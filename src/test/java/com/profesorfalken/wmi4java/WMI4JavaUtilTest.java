/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.profesorfalken.wmi4java;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Test for WMI4Java
 *
 * @author Javier Garcia Alonso
 */
public class WMI4JavaUtilTest {

    public WMI4JavaUtilTest() {
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
     * Test of queryWMIObject method, of class WMI4Java.
     */
    @Test
    public void testJoinMethod() {
    	List <String> paramsToJoin = Arrays.asList("one", "two", "three");
    	
    	assertEquals("one, two, three", WMI4JavaUtil.join(", ", paramsToJoin));
    	
    	assertEquals("one|two|three", WMI4JavaUtil.join("|", paramsToJoin));
    	
    	List <Integer> numbersToJoin = Arrays.asList(1,2,3,18);
    	
    	assertEquals("1-2-3-18", WMI4JavaUtil.join("-", numbersToJoin));
    }
}
