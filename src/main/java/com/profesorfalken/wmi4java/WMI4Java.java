/*
 * Copyright 2016 Javier Garcia Alonso.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.profesorfalken.wmi4java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class that allows to get WMI information. <br>
 * It should be instantiated using an static method and can be easily configured
 * using chained methods.
 * <p>
 *
 * Ex:
 * <code>WMI4Java.get().computerName(".").namespace("root/cimv2").getWMIObject("Win32_BaseBoard");</code>
 * <p>
 * The default computername will be . and the default namespace root/cimv2
 * <p>
 *
 * It supports two implementations: <br>
 * -One based on PowerShell console (see project jPowerShell)<br>
 * -The other based on a VB script (many thanks to Scriptomatic tool!)
 * <p>
 *
 * But default it will use PowerShell but we can force an specific engine
 * easily.
 *
 * @see <a href="https://github.com/profesorfalken/jPowerShell">jPowerShell</a>
 * @see <a href=
 *      "https://technet.microsoft.com/fr-fr/scriptcenter/dd939957.aspx">Scriptomatic
 *      v2</a>
 *
 * @author Javier Garcia Alonso
 */
public class WMI4Java {

	private static final String NEWLINE_REGEX = "\\r?\\n";
	private static final String SPACE_REGEX = "\\s+";

	private static final String GENERIC_ERROR_MSG = "Error calling WMI4Java";

	private String namespace = "*";
	private String computerName = ".";
	private boolean forceVBEngine = false;

	List<String> properties = null;
	List<String> filters = null;

	// Private constructor. Must be instantiated statically
	private WMI4Java() {
	}

	// Get the engine used to retrieve WMI data
	private WMIStub getWMIStub() {
		if (this.forceVBEngine) {
			return new WMIVBScript();
		} else {
			return new WMIPowerShell();
		}
	}

	/**
	 * Static creation of instance
	 *
	 * @return WMI4Java
	 */
	public static WMI4Java get() {
		return new WMI4Java();
	}

	/**
	 * Set an specific namespace <br>
	 *
	 * By default it uses root/cimv2 namespace
	 *
	 * @param namespace
	 *            used namespace. Ex "root/WMI"
	 * @return object instance used to chain calls
	 */
	public WMI4Java namespace(String namespace) {
		this.namespace = namespace;
		return this;
	}

	/**
	 * Set an specific computer name <br>
	 *
	 * By default it uses .
	 *
	 * @param computerName
	 * @return object instance used to chain calls
	 */
	public WMI4Java computerName(String computerName) {
		this.computerName = computerName;
		return this;
	}

	/**
	 * Forces the use of PowerShell engine in order to query WMI
	 *
	 * @return object instance used to chain calls
	 */
	public WMI4Java PowerShellEngine() {
		this.forceVBEngine = false;
		return this;
	}

	/**
	 * Forces the use of VBS engine in order to query WMI
	 *
	 * @return object instance used to chain calls
	 */
	public WMI4Java VBSEngine() {
		this.forceVBEngine = true;
		return this;
	}

	/**
	 * Sets the list of required object properties.
	 *
	 * @param properties
	 *            list with the name of the properties we want to extract
	 * @return object instance used to chain calls
	 */
	public WMI4Java properties(List<String> properties) {
		this.properties = properties;
		return this;
	}

	/**
	 * Sets the list of used filters when performing WMI query.
	 *
	 * @param filters
	 *            list with the different filters to apply
	 * @return object instance used to chain calls
	 */
	public WMI4Java filters(List<String> filters) {
		this.filters = filters;
		return this;
	}

	/**
	 * Query and list the WMI classes
	 *
	 * @see <a href=
	 *      "https://msdn.microsoft.com/fr-fr/library/windows/desktop/aa394554(v=vs.85).aspx">WMI
	 *      Classes - MSDN</a>
	 * @return a list with the name of existing classes in the system
	 */
	public List<String> listClasses() throws WMIException {
		List<String> wmiClasses = new ArrayList<String>();
		String rawData;
		try {
			rawData = getWMIStub().listClasses(this.namespace, this.computerName);

			String[] dataStringLines = rawData.split(NEWLINE_REGEX);

			for (String line : dataStringLines) {
				if (!line.isEmpty() && !line.startsWith("_")) {
					String[] infos = line.split(SPACE_REGEX);
					wmiClasses.addAll(Arrays.asList(infos));
				}
			}

			// Normalize results: remove duplicates and sort the list
			Set<String> hs = new HashSet<String>();
			hs.addAll(wmiClasses);
			wmiClasses.clear();
			wmiClasses.addAll(hs);

		} catch (Exception ex) {
			Logger.getLogger(WMI4Java.class.getName()).log(Level.SEVERE, GENERIC_ERROR_MSG, ex);
			throw new WMIException(ex);
		}

		return wmiClasses;
	}

	/**
	 * Query a WMI class and return all the available properties
	 *
	 * @param wmiClass
	 *            the WMI class to query
	 * @return a list with the name of existing properties in the class
	 */
	public List<String> listProperties(String wmiClass) throws WMIException {
		List<String> foundPropertiesList = new ArrayList<String>();
		try {
			String rawData = getWMIStub().listProperties(wmiClass, this.namespace, this.computerName);

			String[] dataStringLines = rawData.split(NEWLINE_REGEX);

			for (final String line : dataStringLines) {
				if (!line.isEmpty()) {
					foundPropertiesList.add(line.trim());
				}
			}

			List<String> notAllowed = Arrays.asList(new String[] { "Equals", "GetHashCode", "GetType", "ToString" });
			foundPropertiesList.removeAll(notAllowed);

		} catch (Exception ex) {
			Logger.getLogger(WMI4Java.class.getName()).log(Level.SEVERE, GENERIC_ERROR_MSG, ex);
			throw new WMIException(ex);
		}
		return foundPropertiesList;
	}

	/**
	 * Query all the object data for an specific class <br>
	 * 
	 * <b>WARNINGN</b> Notice that this method return a flat object. That means
	 * that if you need to retrieve a list of objects it will not work as
	 * expected. Every time it find an existing object key it overrides it.
	 * <p>
	 * 
	 * In order to retrieve a list of objects, use instead
	 * {@link #getWMIObjectList(WMIClass)}
	 *
	 * @param wmiClass
	 *            Enum that contains the most used classes (root/cimv2)
	 * @return map with the key and the value of all the properties of the
	 *         object
	 */
	public Map<String, String> getWMIObject(WMIClass wmiClass) {
		return getWMIObject(wmiClass.getName());
	}

	/**
	 * Query all the object data for an specific class <br>
	 * 
	 * <b>WARNINGN</b> Notice that this method return a flat object. That means
	 * that if you need to retrieve a list of objects it will not work as
	 * expected. Every time it find an existing object key it overrides it.
	 * <p>
	 * 
	 * In order to retrieve a list of objects, use instead
	 * {@link #getWMIObjectList(String)}
	 *
	 * @param wmiClass
	 *            Enum that contains the most used classes (root/cimv2)
	 * @return map with the key and the value of all the properties of the
	 *         object
	 */
	public Map<String, String> getWMIObject(String wmiClass) throws WMIException {
		Map<String, String> foundWMIClassProperties = new HashMap<String, String>();
		try {
			String rawData;
			if (this.properties != null || this.filters != null) {
				rawData = getWMIStub().queryObject(wmiClass, this.properties, this.filters, this.namespace,
						this.computerName);
			} else {
				rawData = getWMIStub().listObject(wmiClass, this.namespace, this.computerName);
			}

			String[] dataStringLines = rawData.split(NEWLINE_REGEX);

			for (final String line : dataStringLines) {
				if (!line.isEmpty()) {
					String[] entry = line.split(":");
					if (entry != null && entry.length == 2) {
						foundWMIClassProperties.put(entry[0].trim(), entry[1].trim());
					}
				}
			}
		} catch (WMIException ex) {
			Logger.getLogger(WMI4Java.class.getName()).log(Level.SEVERE, GENERIC_ERROR_MSG, ex);
			throw new WMIException(ex);
		}
		return foundWMIClassProperties;
	}

	/**
	 * Query a list of object data for an specific class <br>
	 * 
	 * This method should be used to retrieve a list of objects instead of a
	 * flat key/value object. <br>
	 * For example, you can use it to retrieve hardware elements information
	 * (processors, printers, screens, etc)
	 *
	 * @param wmiClass
	 *            Enum that contains the most used classes (root/cimv2)
	 * @return List of key/value elements. Each element in the list is a found
	 *         object
	 */
	public List<Map<String, String>> getWMIObjectList(WMIClass wmiClass) {
		return getWMIObjectList(wmiClass.getName());
	}

	/**
	 * Query a list of object data for an specific class <br>
	 * 
	 * This method should be used to retrieve a list of objects instead of a
	 * flat key/value object. <br>
	 * For example, you can use it to retrieve hardware elements information
	 * (processors, printers, screens, etc)
	 *
	 * @param wmiClass
	 *            Enum that contains the most used classes (root/cimv2)
	 * @return List of key/value elements. Each element in the list is a found
	 *         object
	 */
	public List<Map<String, String>> getWMIObjectList(String wmiClass) throws WMIException {
		List<Map<String, String>> foundWMIClassProperties = new ArrayList<Map<String, String>>();
		try {
			String rawData;
			if (this.properties != null || this.filters != null) {
				rawData = getWMIStub().queryObject(wmiClass, this.properties, this.filters, this.namespace,
						this.computerName);
			} else {
				rawData = getWMIStub().listObject(wmiClass, this.namespace, this.computerName);
			}

			String[] dataStringObjects = rawData.split(NEWLINE_REGEX + NEWLINE_REGEX);
			for (String dataStringObject : dataStringObjects) {
				String[] dataStringLines = dataStringObject.split(NEWLINE_REGEX);
				Map<String, String> objectProperties = new HashMap<String, String>();
				for (final String line : dataStringLines) {
					if (!line.isEmpty()) {
						String[] entry = line.split(":");
						if (entry.length == 2) {
							objectProperties.put(entry[0].trim(), entry[1].trim());
						}
					}
				}
				foundWMIClassProperties.add(objectProperties);
			}
		} catch (WMIException ex) {
			Logger.getLogger(WMI4Java.class.getName()).log(Level.SEVERE, GENERIC_ERROR_MSG, ex);
			throw new WMIException(ex);

		}
		return foundWMIClassProperties;
	}

	/**
	 * Query all the raw object data for an specific class
	 *
	 * @param wmiClass
	 *            Enum that contains the most used classes (root/cimv2)
	 * @return string with all the properties of the object
	 */
	public String getRawWMIObjectOutput(WMIClass wmiClass) {
		return getRawWMIObjectOutput(wmiClass.getName());
	}

	/**
	 * Query all the raw object data for an specific class
	 *
	 * @param wmiClass
	 *            string with the name of the class to query
	 * @return string with all the properties of the object
	 */
	public String getRawWMIObjectOutput(String wmiClass) throws WMIException {
		String rawData;
		try {
			if (this.properties != null || this.filters != null) {
				rawData = getWMIStub().queryObject(wmiClass, this.properties, this.filters, this.namespace,
						this.computerName);
			} else {
				rawData = getWMIStub().listObject(wmiClass, this.namespace, this.computerName);
			}
		} catch (WMIException ex) {
			Logger.getLogger(WMI4Java.class.getName()).log(Level.SEVERE, GENERIC_ERROR_MSG, ex);
			throw new WMIException(ex);
		}
		return rawData;
	}
}
