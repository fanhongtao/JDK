/*
 * Copyright 2001-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id: CompilerException.java,v 1.3 2004/02/16 22:24:29 minchau Exp $
 */

package com.sun.org.apache.xalan.internal.xsltc.compiler;

/**
 * @author Morten Jorgensen
 */
public final class CompilerException extends Exception {

    private String _msg;

    public CompilerException() {
	super();
    }
    
    public CompilerException(Exception e) {
	super(e.toString());
	_msg = e.toString(); 
    }
    
    public CompilerException(String message) {
	super(message);
	_msg = message;
    }

    public String getMessage() {
	final int col = _msg.indexOf(':');

	if (col > -1)
	    return(_msg.substring(col));
	else
	    return(_msg);
    }
}
