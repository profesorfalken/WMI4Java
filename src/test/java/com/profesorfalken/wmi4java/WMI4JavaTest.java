/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.wmi4java;

import com.profesorfalken.wmi4java.WMI4Java;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test for WMI4Java
 *
 * @author Javier Garcia Alonso
 */
public class WMI4JavaTest {

    public WMI4JavaTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of main method, of class WMI4Java.
     */
    @Test
    public void testWMIClassList() throws Exception {
        System.out.println("start testWMIClassList");

        if (OSDetector.isWindows()) {
            WMI4Java wmi4java = WMI4Java.get();

            //Default class root/CIMV2
            List<String> wmiClassesList = wmi4java.listClasses();
            assertTrue("Returned WMI Classes list is empty! ", !wmiClassesList.isEmpty());
            assertTrue("WMI Classes list content not valid! ", wmiClassesList.contains("Win32_BaseBoard"));
            assertTrue("WMI Classes list content not valid! ", !wmiClassesList.contains("AspNetStart"));
            StringBuilder powerShellResultString = new StringBuilder();
            for (final String wmiClass : wmiClassesList) {
                powerShellResultString.append(wmiClass).append("\r\n");
            }
            List<String> wmiClassesListRootWMI = wmi4java.namespace("root/WMI").listClasses();
            assertTrue("Returned WMI Classes list is empty! ", !wmiClassesListRootWMI.isEmpty());
            assertTrue("WMI Classes list content not valid! ", !wmiClassesListRootWMI.contains("Win32_BaseBoard"));
            assertTrue("WMI Classes list content not valid! ", wmiClassesListRootWMI.contains("AspNetStart"));

            //Now test with VB
            List<String> wmiClassesVBList = wmi4java.VBSEngine().namespace("root/cimv2").listClasses();
            assertTrue("Returned WMI Classes list is empty! ", !wmiClassesVBList.isEmpty());
            assertTrue("WMI Classes list content not valid! ", wmiClassesVBList.contains("Win32_BaseBoard"));
            assertTrue("WMI Classes list content not valid! ", !wmiClassesVBList.contains("AspNetStart"));

            List<String> wmiClassesVBListRootWMI = wmi4java.namespace("root/WMI").listClasses();
            assertTrue("Returned WMI Classes list is empty! ", !wmiClassesVBListRootWMI.isEmpty());
            assertTrue("WMI Classes list content not valid! ", !wmiClassesVBListRootWMI.contains("Win32_BaseBoard"));
            assertTrue("WMI Classes list content not valid! ", wmiClassesVBListRootWMI.contains("AspNetStart"));

            //Calculate differences
            assertFalse("PowerShell result differs from VBS! ", areDifferent(wmiClassesList, wmiClassesVBList));
            assertFalse("PowerShell result differs from VBS! ", areDifferent(wmiClassesListRootWMI, wmiClassesVBListRootWMI));
        }
        System.out.println("end testWMIClassList");
    }

    private boolean areDifferent(List<String> firstList, List<String> secondList) {

        //Look at first element
        if (!(firstList.get(0).equals(secondList.get(0)))) {
            return true;
        }

        //Look at last element
        if (!(firstList.get(firstList.size() - 1).equals(secondList.get(secondList.size() - 1)))) {
            return true;
        }

        return Math.abs(firstList.size() - secondList.size()) > (int) (firstList.size() * 0.1);
    }

    /**
     * Test of main method, of class WMI4Java.
     */
    @Test
    public void testWMIClassPropertiesList() throws Exception {
        System.out.println("test testWMIClassPropertiesList");

        if (OSDetector.isWindows()) {
            WMI4Java wmi4java = WMI4Java.get();

            List<String> wmiClassPropertiesList = wmi4java.listProperties("Win32_BaseBoard");

            assertTrue("Returned WMI class properties list is empty! ",
                    !wmiClassPropertiesList.isEmpty());
            assertTrue("WMI class properties list content not valid! ",
                    wmiClassPropertiesList.contains("Version"));
            assertTrue("WMI class properties list content not valid! Not correctly filtered ",
                    !wmiClassPropertiesList.contains("__CLASS"));

            List<String> wmiClassPropertiesRootNamespaceList = wmi4java.namespace("root/WMI").listProperties("Win32_BaseBoard");
            assertTrue("Returned WMI class properties list should be empty! ",
                    wmiClassPropertiesRootNamespaceList.isEmpty());

            //Now test with VB
            List<String> wmiClassPropertiesVBList = wmi4java.VBSEngine().namespace("root/cimv2").listProperties("Win32_BaseBoard");
            assertTrue("Returned WMI class properties list is empty! ",
                    !wmiClassPropertiesVBList.isEmpty());
            assertTrue("WMI class properties list content not valid! ",
                    wmiClassPropertiesVBList.contains("Version"));
            assertTrue("WMI class properties list content not valid! Not correctly filtered ",
                    !wmiClassPropertiesVBList.contains("__CLASS"));

            List<String> wmiClassPropertiesRootNamespaceVBList = wmi4java.namespace("root/WMI").listProperties("Win32_BaseBoard");
            assertTrue("Returned WMI class properties list should be empty! ",
                    wmiClassPropertiesRootNamespaceVBList.isEmpty());

            //Calculate differences
            assertFalse("PowerShell result differs from VBS! ", areDifferent(wmiClassPropertiesList, wmiClassPropertiesVBList));
        }
        System.out.println("end testWMIClassPropertiesList");
    }

    /**
     * Test of main method, of class WMI4Java.
     */
    @Test
    public void testWMIObject() throws Exception {
        System.out.println("testWMIObject");

        if (OSDetector.isWindows()) {
            WMI4Java wmi4java = WMI4Java.get();

            Map<String, String> wmiObjectProperties = wmi4java.getWMIObject("Win32_BaseBoard");

            assertTrue("Returned WMI object list is empty! ",
                    !wmiObjectProperties.isEmpty());
            assertNotNull("Name property should not be null! ",
                    wmiObjectProperties.get("Name"));
            assertNotNull("Manufacturer property should not be null ",
                    wmiObjectProperties.get("Manufacturer"));

            Map<String, String> wmiObjectPropertiesVB = wmi4java.VBSEngine().getWMIObject("Win32_BaseBoard");

            assertTrue("Returned WMI object list is empty! ",
                    !wmiObjectPropertiesVB.isEmpty());
            assertNotNull("Name property should not be null! ",
                    wmiObjectPropertiesVB.get("Name"));
            assertNotNull("Manufacturer property should not be null ",
                    wmiObjectPropertiesVB.get("Manufacturer"));

            //Differences
            assertTrue(wmiObjectProperties.get("Description").equals(wmiObjectPropertiesVB.get("Description")));
            assertTrue(Math.abs(wmiObjectProperties.size() - wmiObjectPropertiesVB.size())
                    < (int) (wmiObjectProperties.size() * 0.1));
        }
        System.out.println("end testWMIObject");
    }
}
