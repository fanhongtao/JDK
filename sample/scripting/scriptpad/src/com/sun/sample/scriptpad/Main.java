/*
* @(#)Main.java	1.1 06/08/06
*
* Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions are met:
*
* -Redistribution of source code must retain the above copyright notice, this
*  list of conditions and the following disclaimer.
*
* -Redistribution in binary form must reproduce the above copyright notice,
*  this list of conditions and the following disclaimer in the documentation
*  and/or other materials provided with the distribution.
*
* Neither the name of Sun Microsystems, Inc. or the names of contributors may
* be used to endorse or promote products derived from this software without
* specific prior written permission.
*
* This software is provided "AS IS," without a warranty of any kind. ALL
* EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
* ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
* OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN")
* AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
* AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
* DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
* REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
* INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
* OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE,
* EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
*
* You acknowledge that this software is not designed, licensed or intended
* for use in the design, construction, operation or maintenance of any
* nuclear facility.
*/

package com.sun.sample.scriptpad;

import javax.script.*;
import java.io.*;

/**
 * This is the entry point of "Scriptpad" sample. This class creates 
 * ScriptEngine and evaluates few JavaScript "files" -- which are stored 
 * as resources (please refer to src/resources/*.js). Actual code for the 
 * scriptpad's main functionality lives in these JavaScript files.
 */
public class Main {
    public static void main(String[] args) throws Exception {

        // create a ScriptEngineManager
        ScriptEngineManager m = new ScriptEngineManager();
        // get an instance of JavaScript script engine
        ScriptEngine engine = m.getEngineByName("js");

        // expose the current script engine as a global variable
        engine.put("engine", engine);

        // evaluate few scripts that are bundled in "resources"
        eval(engine, "conc.js");
        eval(engine, "gui.js");
        eval(engine, "scriptpad.js");
        eval(engine, "mm.js");
    }

    private static void eval(ScriptEngine engine, String name)
                            throws Exception {
        /*
         * This class is compiled into a jar file. The jar file
         * contains few scripts under /resources URL.
         */
        InputStream is = Main.class.getResourceAsStream("/resources/" + name);     
        // current script file name for better error messages
        engine.put(ScriptEngine.NAME, name);
        // evaluate the script in the InputStream
        engine.eval(new InputStreamReader(is));
    }
}
