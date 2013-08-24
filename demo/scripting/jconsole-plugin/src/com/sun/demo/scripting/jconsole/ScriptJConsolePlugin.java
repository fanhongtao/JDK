/*
 * @(#)ScriptJConsolePlugin.java	1.4 06/07/19 04:44:52
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
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
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

package com.sun.demo.scripting.jconsole;

import com.sun.tools.jconsole.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.concurrent.CountDownLatch;
import javax.script.*;
import javax.swing.*;
import javax.swing.event.*;
import java.util.*;

/**
 * This is script console plugin. This class uses javax.script API to create
 * interactive read-eval-print script shell within the jconsole GUI.
 */
public class ScriptJConsolePlugin extends JConsolePlugin 
                     implements ScriptShellPanel.CommandProcessor {
    // Panel for our tab
    private volatile ScriptShellPanel window;
    // Tabs that we add to jconsole GUI
    private Map<String, JPanel> tabs;

    // Script engine that evaluates scripts
    private volatile ScriptEngine engine;

    // script engine initialization occurs in background.
    // This latch is used to coorrdinate engine init and eval.
    private CountDownLatch engineReady = new CountDownLatch(1);

    // File extension used for scripts of chosen language.
    // For eg. ".js" for JavaScript, ".bsh" for BeanShell.
    private String extension;

    // Prompt to print in the read-eval-print loop. This is 
    // derived from the script file extension.
    private volatile String prompt;

    /**
     * Constructor to create this plugin
     */ 
    public ScriptJConsolePlugin() {
    }

    @Override public Map<String, JPanel> getTabs() {
        // create ScriptEngine
        createScriptEngine();

        // create panel for tab
        window = new ScriptShellPanel(this);

        // add tab to tabs map
        tabs = new HashMap<String, JPanel>();
        tabs.put("Script Shell", window);

        new Thread(new Runnable() {
            public void run() {
                // initialize the script engine
                initScriptEngine();
                engineReady.countDown();
            }
        }).start();
        return tabs;
    }

    @Override public SwingWorker<?,?> newSwingWorker() {
        return null;
    }
    
    @Override public void dispose() {
        window.dispose();
    }

    public String getPrompt() {
        return prompt;
    }

    public String executeCommand(String cmd) {
        String res;
        try {
           engineReady.await();
           Object tmp = engine.eval(cmd);
           res = (tmp == null)? null : tmp.toString();
        } catch (InterruptedException ie) {
           res = ie.getMessage();
        } catch (ScriptException se) {
           res = se.getMessage();
        }
        return res;
    }

    //-- Internals only below this point
    private void createScriptEngine() {
        ScriptEngineManager manager = new ScriptEngineManager();
        String language = getScriptLanguage();
        engine = manager.getEngineByName(language);
        if (engine == null) {
            throw new RuntimeException("cannot load " + language + " engine");
        }
        extension = engine.getFactory().getExtensions().get(0);
        prompt = extension + ">";
        engine.setBindings(createBindings(), ScriptContext.ENGINE_SCOPE);
    }

    // Name of the System property used to select scripting language
    private static final String LANGUAGE_KEY = "com.sun.demo.jconsole.console.language";

    private String getScriptLanguage() {
        // check whether explicit System property is set
        String lang = System.getProperty(LANGUAGE_KEY);
        if (lang == null) {
            // default is JavaScript
            lang = "JavaScript";
        }
        return lang;
    }

    // create Bindings that is backed by a synchronized HashMap
    private Bindings createBindings() {
        Map<String, Object> map =
                Collections.synchronizedMap(new HashMap<String, Object>());
        return new SimpleBindings(map);
    }

    // create and initialize script engine
    private void initScriptEngine() {
        // set pre-defined global variables
        setGlobals();
        // load pre-defined initialization file
        loadInitFile();
        // load current user's initialization file
        loadUserInitFile();
    }

    // set pre-defined global variables for script
    private void setGlobals() {
        engine.put("engine", engine);
        engine.put("window", window);
        engine.put("plugin", this);
    }

    // load initial script file (jconsole.<extension>)
    private void loadInitFile() {
        String oldFilename = (String) engine.get(ScriptEngine.FILENAME);
        engine.put(ScriptEngine.FILENAME, "<built-in jconsole." + extension + ">");
        try {
            Class myClass = this.getClass();
            InputStream stream = myClass.getResourceAsStream("/resources/jconsole." +
                                       extension);
            if (stream != null) {
                engine.eval(new InputStreamReader(new BufferedInputStream(stream)));
            }
        } catch (Exception exp) {
            exp.printStackTrace();
            // FIXME: What else I can do here??
        } finally {
            engine.put(ScriptEngine.FILENAME, oldFilename);
        }
    }

    // load user's initial script file (~/jconsole.<extension>)
    private void loadUserInitFile() {
        String oldFilename = (String) engine.get(ScriptEngine.FILENAME);
        String home = System.getProperty("user.home");
        if (home == null) {
            // no user.home?? should not happen??
            return;
        }
        String fileName = home + File.separator + "jconsole." + extension;
        if (! (new File(fileName).exists())) {
            // user does not have ~/jconsole.<extension>
            return;
        }
        engine.put(ScriptEngine.FILENAME, fileName);
        try {
            engine.eval(new FileReader(fileName));
        } catch (Exception exp) {
            exp.printStackTrace();
            // FIXME: What else I can do here??
        } finally {
            engine.put(ScriptEngine.FILENAME, oldFilename);
        }
    }
}
