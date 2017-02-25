package com.profesorfalken.wmi4java;

public final class WMI4JavaUtil {
	 
    public static String join(String delimiter, Iterable<?> parts) {
    	StringBuilder joinedString = new StringBuilder();
    	
    	for (final Object part : parts) {
    		joinedString.append(part);
    		joinedString.append(delimiter);
    	}
    	joinedString.delete(joinedString.length() - delimiter.length(), joinedString.length());
    	
    	return joinedString.toString();
    }
}
