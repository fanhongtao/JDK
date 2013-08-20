/*
 * @(#)$Id: JavaCupRedirect.java,v 1.2 2003/01/27 18:44:55 mkwan Exp $
 *
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2003 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xalan" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation and was
 * originally based on software copyright (c) 2001, Sun
 * Microsystems., http://www.sun.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package com.sun.org.apache.xalan.internal.xsltc.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Utility class to redirect input to JavaCup program.
 *
 * Usage-command line: 
 * <code>java com.sun.org.apache.xalan.internal.xsltc.utils.JavaCupRedirect [args] -stdin filename.ext</code>
 *
 * @author Morten Jorgensen
 * @version $Id: JavaCupRedirect.java,v 1.2 2003/01/27 18:44:55 mkwan Exp $
 */
public class JavaCupRedirect {

    private final static String ERRMSG = 
		 "You must supply a filename with the -stdin option.";

    public static void _main (String args[]) {

		 // If we should call System.exit or not
         //@todo make this settable for use inside other java progs
		 boolean systemExitOK = true;

		 // This is the stream we'll set as our System.in
		 InputStream input = null;

		 // The number of arguments
		 final int argc = args.length;

		 // The arguments we'll pass to the real '_main()'
		 String[] new_args = new String[argc - 2];
		 int new_argc = 0;

		 // Parse all parameters passed to this class
		 for (int i = 0; i < argc; i++) {
		     // Parse option '-stdin <filename>'
		     if (args[i].equals("-stdin")) {
		 		 // This option must have an argument
		 		 if ((++i >= argc) || (args[i].startsWith("-"))) {
		 		     System.err.println(ERRMSG);
		 		     doSystemExit(systemExitOK);
		 		 }
		 		 try {
		 		     input = new FileInputStream(args[i]);
		 		 }
		 		 catch (FileNotFoundException e) {
		 		     System.err.println("Could not open file "+args[i]);
		 		     doSystemExit(systemExitOK);
		 		 }
		 		 catch (SecurityException e) {
		 		     System.err.println("No permission to file "+args[i]);
		 		     doSystemExit(systemExitOK);
		 		 }
		     }
		     else {
		 		 if (new_argc == new_args.length) {
		 		     System.err.println("Missing -stdin option!");
		 		     doSystemExit(systemExitOK);
		 		 }
		 		 new_args[new_argc++] = args[i];
		     }
		 }

		 System.setIn(input);
		 try {
		     com.sun.java_cup.internal.Main.main(new_args);
		 }
		 catch (Exception e) {
		     System.err.println("Error running JavaCUP:");
		     e.printStackTrace();
		     doSystemExit(systemExitOK);
		 }
    }
    public static void doSystemExit (boolean doExit) {
        if (doExit)
            System.exit(-1);
    }
}

