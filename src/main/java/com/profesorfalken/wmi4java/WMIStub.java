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

import java.util.List;

/**
 * Interface for the stub that performs the operations to query WMI in order to
 * retrieve the classes, properties and object details
 * 
 * @author Javier Garcia Alonso
 */
interface WMIStub {

    /**
     * List all the WMI classes for the required namespace/computername
     * 
     * @param namespace the namespace 
     * @param computerName the computer name
     * @return String with all the raw information returned after the query
     * @throws WMIException 
     */
    String listClasses(String namespace, String computerName) throws WMIException;

    /**
     * List all elements of the selected WMI class for the required namespace/computername
     * 
     * @param wmiClass the name of the queried wmi class
     * @param namespace the namespace 
     * @param computerName the computer name     
     * @return String with all the raw information returned after the query
     * @throws WMIException 
     */
    String listObject(String wmiClass, String namespace, String computerName) throws WMIException;

    /**
     * Queries a WMI class getting specific properties and applying filters
     *
     * @param wmiClass the name of the queried wmi class
     * @param wmiProperties properties to return
     * @param conditions conditions to meet
     * @param namespace the namespace
     * @param computerName the computer name     
     * @return String with all the raw information returned after the query
     * @throws WMIException
     */
    String queryObject(String wmiClass, List<String> wmiProperties, List<String> conditions, String namespace, String computerName) throws WMIException;

    /**
     * List all properties of the selected WMI class for the required namespace/computername
     * 
     * @param wmiClass the name of the queried wmi class
     * @param namespace the namespace 
     * @param computerName the computer name     
     * @return String with all the raw information returned after the query
     * @throws WMIException 
     */
    String listProperties(String wmiClass, String namespace, String computerName) throws WMIException;
}
