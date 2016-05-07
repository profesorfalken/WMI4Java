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

import com.google.common.base.Joiner;
import com.profesorfalken.jpowershell.PowerShell;
import com.profesorfalken.jpowershell.PowerShellNotAvailableException;
import com.profesorfalken.jpowershell.PowerShellResponse;

import java.util.Collections;
import java.util.List;

/**
 * WMI Stub implementation based in PowerShell (jPowerShell)
 *
 * @author Javier Garcia Alonso
 */
class WMIPowerShell implements WMIStub {

    private static final String NAMESPACE_PARAM = "-Namespace ";
    private static final String GETWMIOBJECT_COMMAND = "Get-WMIObject ";

    private static String executeCommand(String command) throws WMIException {
        String commandResponse = null;
        PowerShell powerShell = null;
        try {
            powerShell = PowerShell.openSession();
            PowerShellResponse psResponse = powerShell.executeCommand(command);

            if (psResponse.isError()) {
                throw new WMIException("WMI operation finished in error: "
                        + psResponse.getCommandOutput());
            }

            commandResponse = psResponse.getCommandOutput().trim();

            powerShell.close();
        } catch (PowerShellNotAvailableException ex) {
            throw new WMIException(ex.getMessage(), ex);
        } finally {
            if (powerShell != null) {
                powerShell.close();
            }
        }

        return commandResponse;
    }

    @Override
    public String listClasses(String namespace, String computerName) throws WMIException {
        String namespaceString = "";
        if (!"*".equals(namespace)) {
            namespaceString += NAMESPACE_PARAM + namespace;
        }

        return executeCommand(GETWMIOBJECT_COMMAND
                + namespaceString + " -List | Sort Name");
    }

    @Override
    public String listProperties(String wmiClass, String namespace, String computerName) throws WMIException {
        String command = GETWMIOBJECT_COMMAND + wmiClass + " ";

        if (!"*".equals(namespace)) {
            command += NAMESPACE_PARAM + namespace;
        }

        command += " | ";

        command += "Select-Object * -excludeproperty \"_*\" | ";

        command += "Get-Member | select name | format-table -hidetableheader";

        return executeCommand(command);
    }

    @Override
    public String listObject(String wmiClass, String namespace, String computerName) throws WMIException {
        String command = GETWMIOBJECT_COMMAND + wmiClass + " ";

        if (!"*".equals(namespace)) {
            command += NAMESPACE_PARAM + namespace;
        }

        command += " | ";

        command += "Select-Object * -excludeproperty \"_*\" | ";

        command += "Format-List *";

        return executeCommand(command);
    }

    @Override
    public String queryObject(String wmiClass, List<String> wmiProperties, List<String> conditions, String namespace, String computerName) throws WMIException {
        String command = GETWMIOBJECT_COMMAND + wmiClass + " ";

        if (!"*".equals(namespace)) {
            command += NAMESPACE_PARAM + namespace;
        }
        List<String> usedWMIProperties;
        if (wmiProperties == null || wmiProperties.isEmpty()) {
            usedWMIProperties = Collections.singletonList("*");
        } else {
            usedWMIProperties = wmiProperties;
        }

        command += " | ";

        command += "Select-Object " + Joiner.on(", ").join(usedWMIProperties) + " -excludeproperty \"_*\" | ";

        if (conditions != null && !conditions.isEmpty()) {
            for (String condition : conditions) {
                command += "Where-Object -FilterScript {" + condition + "} | ";
            }
        }

        command += "Format-List *";

        return executeCommand(command);
    }
}
