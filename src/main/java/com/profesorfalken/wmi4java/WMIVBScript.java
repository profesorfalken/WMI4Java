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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * WMI Stub implementation based in VBS
 * 
 * @author Javier Garcia Alonso
 */
class WMIVBScript implements WMIStub {
    
    private static final String ROOT_CIMV2 = "root/cimv2";
    private static final String IMPERSONATION_VARIABLE = "Set objWMIService=GetObject(\"winmgmts:{impersonationLevel=impersonate}!\\\\";

    private static final String CRLF = "\r\n";

    private static String executeScript(String scriptCode) throws WMIException {
        String scriptResponse = "";
        File tmpFile = null;
        FileWriter writer = null;
        BufferedReader errorOutput = null;

        try {
            tmpFile = File.createTempFile("wmi4java" + new Date().getTime(), ".vbs");
            writer = new FileWriter(tmpFile);
            writer.write(scriptCode);
            writer.flush();
            writer.close();

            Process process = Runtime.getRuntime().exec(
                    new String[]{"cmd.exe", "/C", "cscript.exe", "/NoLogo", tmpFile.getAbsolutePath()});
            BufferedReader processOutput
                    = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = processOutput.readLine()) != null) {
                if (!line.isEmpty()) {
                    scriptResponse += line + CRLF;
                }
            }

            if (scriptResponse.isEmpty()) {
                errorOutput
                        = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String errorResponse = "";
                while ((line = errorOutput.readLine()) != null) {
                    if (!line.isEmpty()) {
                        errorResponse += line + CRLF;
                    }
                }
                if (!errorResponse.isEmpty()) {
                    throw new WMIException("WMI operation finished in error: "
                            + errorResponse);
                }
            }

        } catch (Exception ex) {
            throw new WMIException(ex.getMessage(), ex);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (tmpFile != null) {
                    tmpFile.delete();
                }
                if (errorOutput != null) {
                    errorOutput.close();
                }
            } catch (IOException ioe) {
                Logger.getLogger(WMI4Java.class.getName()).log(Level.SEVERE, "Exception closing in finally", ioe);
            }
        }
        return scriptResponse.trim();
    }

    @Override
    public String listClasses(String namespace, String computerName) throws WMIException {

        try {
            StringBuilder scriptCode = new StringBuilder(200);

            String namespaceCommand = ROOT_CIMV2;
            if (!"*".equals(namespace)) {
                namespaceCommand = namespace;
            }

            scriptCode.append(IMPERSONATION_VARIABLE)
                    .append(computerName).append("/").append(namespaceCommand).append("\")").append(CRLF);

            scriptCode.append("Set colClasses = objWMIService.SubclassesOf()").append(CRLF);

            scriptCode.append("For Each objClass in colClasses").append(CRLF);
            scriptCode.append("For Each objClassQualifier In objClass.Qualifiers_").append(CRLF);
            scriptCode.append("WScript.Echo objClass.Path_.Class").append(CRLF);
            scriptCode.append("Next").append(CRLF);
            scriptCode.append("Next").append(CRLF);

            return executeScript(scriptCode.toString());
        } catch (Exception ex) {
            throw new WMIException(ex.getMessage(), ex);
        }
    }

    @Override
    public String listProperties(String wmiClass, String namespace, String computerName) throws WMIException {
        try {
            StringBuilder scriptCode = new StringBuilder(200);

            String namespaceCommand = ROOT_CIMV2;
            if (!"*".equals(namespace)) {
                namespaceCommand = namespace;
            }

            scriptCode.append(IMPERSONATION_VARIABLE)
                    .append(computerName).append("/").append(namespaceCommand).append(":")
                    .append(wmiClass).append("\")").append(CRLF);

            scriptCode.append("For Each objClassProperty In objWMIService.Properties_").append(CRLF);
            scriptCode.append("WScript.Echo objClassProperty.Name").append(CRLF);
            scriptCode.append("Next").append(CRLF);

            return executeScript(scriptCode.toString());

        } catch (Exception ex) {
            throw new WMIException(ex.getMessage(), ex);
        }
    }

    @Override
    public String listObject(String wmiClass, String namespace, String computerName) throws WMIException {
         List<String> wmiProperties = WMI4Java.get().VBSEngine().computerName(computerName).namespace(namespace).listProperties(wmiClass);
        try {
            StringBuilder scriptCode = new StringBuilder(200);

            String namespaceCommand = ROOT_CIMV2;
            if (!"*".equals(namespace)) {
                namespaceCommand = namespace;
            }

            scriptCode.append(IMPERSONATION_VARIABLE)
                    .append(computerName).append("/").append(namespaceCommand).append("\")").append(CRLF);

            scriptCode.append("Set colClasses = objWMIService.SubclassesOf()").append(CRLF);

            scriptCode.append("Set wmiQueryData = objWMIService.ExecQuery(\"Select * from ")
                    .append(wmiClass).append("\")").append(CRLF);
            scriptCode.append("For Each element In wmiQueryData").append(CRLF);
            for (final String wmiProperty : wmiProperties) {
                scriptCode.append("Wscript.Echo \"").append(wmiProperty)
                        .append(": \" & ").append("element.").append(wmiProperty).append(CRLF);
            }
            scriptCode.append("Next").append(CRLF);

            return executeScript(scriptCode.toString());
        } catch (Exception ex) {
            throw new WMIException(ex.getMessage(), ex);
        }
    }

}
