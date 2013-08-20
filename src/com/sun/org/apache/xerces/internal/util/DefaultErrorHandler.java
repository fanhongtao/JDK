/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000-2002 The Apache Software Foundation.  All rights 
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
 * 4. The names "Xerces" and "Apache Software Foundation" must
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
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package com.sun.org.apache.xerces.internal.util;

import java.io.PrintWriter;

import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;

/**
 * Default error handler.
 *
 * @author Andy Clark, IBM
 *
 * @version $Id: DefaultErrorHandler.java,v 1.4 2002/01/29 01:15:18 lehors Exp $
 */
public class DefaultErrorHandler
    implements XMLErrorHandler {

    //
    // Data
    //

    /** Print writer. */
    protected PrintWriter fOut;

    //
    // Constructors
    //

    /** 
     * Constructs an error handler that prints error messages to 
     * <code>System.err</code>. 
     */
    public DefaultErrorHandler() {
        this(new PrintWriter(System.err));
    } // <init>()

    /** 
     * Constructs an error handler that prints error messages to the
     * specified <code>PrintWriter</code. 
     */
    public DefaultErrorHandler(PrintWriter out) {
        fOut = out;
    } // <init>(PrintWriter)

    //
    // ErrorHandler methods
    //

    /** Warning. */
    public void warning(String domain, String key, XMLParseException ex) 
        throws XNIException {
        printError("Warning", ex);
    } // warning(XMLParseException)

    /** Error. */
    public void error(String domain, String key, XMLParseException ex)
        throws XNIException {
        printError("Error", ex);
    } // error(XMLParseException)

    /** Fatal error. */
    public void fatalError(String domain, String key, XMLParseException ex)
        throws XNIException {
        printError("Fatal Error", ex);
        throw ex;
    } // fatalError(XMLParseException)

    //
    // Private methods
    //

    /** Prints the error message. */
    private void printError(String type, XMLParseException ex) {

        fOut.print("[");
        fOut.print(type);
        fOut.print("] ");
        String systemId = ex.getExpandedSystemId();
        if (systemId != null) {
            int index = systemId.lastIndexOf('/');
            if (index != -1)
                systemId = systemId.substring(index + 1);
            fOut.print(systemId);
        }
        fOut.print(':');
        fOut.print(ex.getLineNumber());
        fOut.print(':');
        fOut.print(ex.getColumnNumber());
        fOut.print(": ");
        fOut.print(ex.getMessage());
        fOut.println();
        fOut.flush();

    } // printError(String,SAXParseException)

} // class DefaultErrorHandler
