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

/**
 * Custom exception for WMI4Java.<p>
 * 
 * This is an unchecked exception so it can be catched optionally
 *  
 * @author Javier Garcia Alonso
 */
public class WMIException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5992635300188042890L;

	public WMIException(String message) {
        super(message);
    }

    public WMIException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public WMIException(Throwable cause) {
        super(cause);
    }
    
}
