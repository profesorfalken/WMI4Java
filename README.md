![](https://img.shields.io/maven-central/v/com.profesorfalken/WMI4Java.svg)
![](https://img.shields.io/github/license/profesorfalken/WMI4Java.svg)

# WMI4Java
Java API to perform WMI queries

## Installation ##

To install WMI4Java you can add the dependecy to your software project management tool: http://mvnrepository.com/artifact/com.profesorfalken/WMI4Java/1.0

For example, for Maven you have just to add to your pom.xml: 

      <dependency>
	        <groupId>com.profesorfalken</groupId>
	        <artifactId>WMI4Java</artifactId>
	        <version>1.0</version>
        </dependency>

Instead, you can direct download the JAR file and add it to your classpath. 
https://repo1.maven.org/maven2/com/profesorfalken/WMI4Java/1.0/WMI4Java-1.0.jar

## Basic Usage ##

#### List all WMI classes names from root/CIMv2 (default namespace) ####

```java
    List<String> wmiClassesList = WMI4Java.get().listClasses();
```

#### List all WMI classes names from root/WMI ####

```java
    List<String> wmiClassesList = WMI4Java.get().namespace("root/WMI").listClasses();
```

#### Get all the properties of a WMI Object ####

```java
    //Example win32_BIOS
    Map<String, String> wmiObjectProperties = WMI4Java.get().getWMIObject("Win32_BIOS");
```

## How it works ##

WMI4Java uses two different mechanism in order to retrieve WMI information.

-By default, it uses jPowerShell (https://github.com/profesorfalken/jPowerShell) to open a PowerShell session and invoke the command.

-Instead, we can use an implementation based on VBScript that creates and launches an script on background.

Change the used implementation is really simple. For example, to reproduce the last example using VBScript instead of PowerShell:

```java
    //Example win32_BIOS
    Map<String, String> wmiObjectProperties = WMI4Java.get().VBSEngine().getWMIObject("Win32_BIOS");
```
