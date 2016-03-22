package com.profesorfalken.wmi4java;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Javier
 */
public class MainGetInfo {

    public static void main(String[] args) {
        List<String> classesToFilter
                = Arrays.asList(new String[]{
                    "Win32_CheckCheck",
                    "Win32_Perf",
                    "Win32_RemoveFileAction",
                    "Win32_DirectorySpecification",
                    "Win32_ActionCheck",
                    "Win32_SofwareFeatureParent",
                    "Win32_NTLogEventLog",
                    "Win32_FileSpecification",
                    "Win32_PerfRawData",
                    "Win32_NTLogEventComputer",
                    "Win32_PerfFormattedData",
                    "Win32_FileSpecification",
                    "Win32_ClassicCOMClassSetting",
                    "Win32_Directory",
                    "Win32_SubDirectory",
                    "Win32_RegistryAction"
                });
        List<String> CIMClassesToFilter
                = Arrays.asList(new String[]{
                    "CIM_ManagedSystemElement",
                    "CIM_LogicalElement",
                    "CIM_LogicalFile",
                    "CIM_ServiceAccessBySAP",
                    "CIM_ElementSetting",
                    "CIM_FileSpecification",
                    "CIM_StatisticalInformation",
                    "CIM_DirectorySpecification",
                    "CIM_Action",
                    "CIM_ProcessExecutable",
                    "CIM_RemoveFileAction",
                    "CIM_Dependency",
                    "CIM_Service",
                    "CIM_DirectoryAction",
                    "CIM_Setting",
                    "CIM_LogicalDevice",
                    "CIM_FileAction",
                    "CIM_Component",
                    "CIM_CopyFileAction",
                    "CIM_ServiceAccessPoint",
                    "CIM_Check",
                    "CIM_Product",
                    "CIM_CreateDirectoryAction",
                    "CIM_DirectoryContainsFile",
                    "CIM_Directory",
                    "CIM_DataFile",
                    "CIM_WmiThreadPoolEvent"
                });
        List<String> classesTooLongToQuery
                = Arrays.asList(new String[]{
                    "Win32_ProductResource",
                    "Win32_SecuritySetting",
                    "Win32_COMClass",
                    "Win32_LogicalFileSecuritySetting",
                    "Win32_SettingCheck",
                    "Win32_PerfRawData_PerfProc_Image_Costly",
                    "Win32_PerfFormattedData_PerfProc_FullImage_Costly",
                    "Win32_Property",
                    "Win32_PerfFormattedData_PerfProc_Image_Costly",
                    "Win32_PerfRawData_PerfProc_FullImage_Costly",
                    "Win32_ClassicCOMClass",
                    "Win32_ClassicCOMClassSettings",
                    "Win32_MSIResource"});
        List<String> classesLittleBitSlow
                = Arrays.asList(new String[]{
                    "Win32_ODBCDataSourceSpecification",
                    "Win32_PublishComponentAction",
                    "Win32_BindImageAction",
                    "Win32_ODBCDriverSpecification",
                    "Win32_DuplicateFileAction",
                    "Win32_Patch",
                    "Win32_ExtensionInfoAction",
                    "Win32_MIMEInfoAction",
                    "Win32_PerfRawData_PerfProc_Thread",
                    "Win32_ODBCTranslatorSpecification",
                    "Win32_RemoveIniAction",
                    "Win32_IniFileSpecification",
                    "Win32_ProgIDSpecification",
                    "Win32_COMSetting",
                    "Win32_UserInDomain",
                    "Win32_ShortcutAction",
                    "Win32_ApplicationCommandLine",
                    "Win32_ServiceSpecificationService",
                    "Win32_PatchFile",
                    "Win32_ServiceControl",
                    "Win32_ODBCDriverAttribute",
                    "Win32_ReserveCost",
                    "Win32_ApplicationService",
                    "Win32_ShortcutSAP",
                    "Win32_CommandLineAccess",
                    "Win32_CreateFolderAction",
                    "Win32_SelfRegModuleAction",
                    "Win32_CDROMDrive",
                    "Win32_ServiceSpecification",
                    "Win32_NTDomain",
                    "Win32_GroupInDomain",
                    "Win32_PnPSignedDriverCIMDataFile",
                    "Win32_ClassInfoAction",
                    "Win32_ODBCDataSourceAttribute",
                    "Win32_LaunchCondition",
                    "Win32_PerfFormattedData_PerfProc_Thread",
                    "Win32_PnPSignedDriver",
                    "Win32_Product",
                    "Win32_ImplementedCategory",
                    "Win32_SystemTimeZone",
                    "Win32_Condition",
                    "Win32_EnvironmentSpecification",
                    "Win32_Thread",
                    "Win32_MoveFileAction",
                    "Win32_TSSessionDirectorySetting",
                    "Win32_ODBCSourceAttribute",
                    "Win32_ProductCheck",
                    "Win32_TypeLibraryAction",
                    "Win32_Binary",
                    "Win32_ODBCAttribute",
                    "Win32_FontInfoAction",
                    "Win32_ManagedSystemElementResource",
                    "Win32_ShortcutFile",
                    "Win32_SecuritySettingOfObject",
                    "Win32_PerfFormattedData_PerfProc_ThreadDetails_Costly",
                    "Win32_PerfRawData_PerfProc_ThreadDetails_Costly",
                    "Win32_SecuritySettingOfLogicalFile",
                    "Win32_NTLogEventUser",
                    "Win32_BaseService",
                    "Win32_NamedJobObjectProcess",
                    "Win32_Condition",
                    "Win32_ODBCAttribute"});
        System.out.println(
                "testGetAllInfo");
        WMI4Java wmi4java = WMI4Java.get().VBSEngine();

        List<String> wmiClassesList = wmi4java.VBSEngine().listClasses();

        //Filter classes that provoques hangs
        wmiClassesList.removeAll(classesToFilter);

        //Filter classes too long to query
        wmiClassesList.removeAll(classesTooLongToQuery);

        //Filter classes little bit slow to query
        wmiClassesList.removeAll(classesLittleBitSlow);

        //Filter CIM blocking classes
        wmiClassesList.removeAll(CIMClassesToFilter);

        for (final String wmiClass : wmiClassesList) {
            if (!wmiClass.startsWith("Win32") && !wmiClass.contains("SoftwareElement") && !wmiClass.contains("SoftwareFeature")) {
                long initTime = System.currentTimeMillis();
                System.out.println("==========Class " + wmiClass + " ==============");

                Map<String, String> wmiObjectProperties = wmi4java.VBSEngine().VBSEngine().getWMIObject(wmiClass);

                Set<Map.Entry<String, String>> properties = wmiObjectProperties.entrySet();
                for (Map.Entry<String, String> property : properties) {
                    System.out.println(property.getKey() + ": " + property.getValue());
                }
                long usedTime = (System.currentTimeMillis() - initTime) / 1000;
                String tooLong = "";
                if (usedTime > 1) {
                    tooLong = "[QUERY_TOO_LONG]";
                }
                System.out.println("==========End " + wmiClass + " Time: " + usedTime + " seconds " + tooLong + "==============");
            }

        }
    }
}
