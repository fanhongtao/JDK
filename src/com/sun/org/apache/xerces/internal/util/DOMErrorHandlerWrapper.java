/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001, 2002 The Apache Software Foundation.  
 * All rights reserved.
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

import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
                             
import org.w3c.dom.Node;
import org.w3c.dom.DOMError;
import org.w3c.dom.DOMLocator;
import org.w3c.dom.DOMErrorHandler;
import com.sun.org.apache.xerces.internal.dom.DOMErrorImpl;
import com.sun.org.apache.xerces.internal.dom.DOMLocatorImpl;
import com.sun.org.apache.xerces.internal.impl.msg.XMLMessageFormatter;

import java.io.PrintWriter;
import java.util.Hashtable;

/**
 * This class handles DOM errors .
 *
 * @see DOMErrorHandler
 *
 * @author Gopal Sharma, SUN Microsystems Inc.
 * @version $Id: DOMErrorHandlerWrapper.java,v 1.12 2004/04/23 04:40:38 mrglavas Exp $
 */

// REVISIT: current implementations wraps error several times:
//          XMLErrorReport.reportError creates XMLParserException (by wrapping all info)
//          and goes via switch to send errors.
//          DOMErrorHandlerWrapper catches calls, copies info from XMLParserException and
//          sends one call back to the application
//          I think we can avoid this indirection if we modify XMLErrorReporter. --el

public class DOMErrorHandlerWrapper
    implements XMLErrorHandler, DOMErrorHandler {

    /** Map for converting internal error codes to DOM error types. **/
    private static Hashtable fgDOMErrorTypeTable;
    
    // It keeps the reference of DOMErrorHandler of application
    protected DOMErrorHandler fDomErrorHandler;

    // Error Status
    boolean eStatus = true ;

    // Print writer
    protected PrintWriter fOut;

    // some components may set error node
    // @see DOMNormalizer.
    public Node fCurrentNode;

    /** Error code for comparisons. **/
    protected final XMLErrorCode fErrorCode = new XMLErrorCode(null, null);
    
    protected final DOMErrorImpl fDOMError = new DOMErrorImpl();
    
    static {
        // initialize error type table: internal error codes (represented by domain and key) need to be mapped to a DOM error type.
        fgDOMErrorTypeTable = new Hashtable();   
        fgDOMErrorTypeTable.put(new XMLErrorCode(XMLMessageFormatter.XML_DOMAIN, "DoctypeNotAllowed"), "doctype-not-allowed");
        fgDOMErrorTypeTable.put(new XMLErrorCode(XMLMessageFormatter.XML_DOMAIN, "ElementUnterminated"), "wf-invalid-character-in-node-name");
        fgDOMErrorTypeTable.put(new XMLErrorCode(XMLMessageFormatter.XML_DOMAIN, "EncodingDeclInvalid"), "unsupported-encoding");
        fgDOMErrorTypeTable.put(new XMLErrorCode(XMLMessageFormatter.XML_DOMAIN, "EqRequiredInAttribute"), "wf-invalid-character-in-node-name");
        fgDOMErrorTypeTable.put(new XMLErrorCode(XMLMessageFormatter.XML_DOMAIN, "LessthanInAttValue"), "wf-invalid-character");
    }

    //
    // Constructors
    //

    // Default constructor /

    public DOMErrorHandlerWrapper() {
        fOut = new PrintWriter(System.err);
    }


    public DOMErrorHandlerWrapper(DOMErrorHandler domErrorHandler) {
        fDomErrorHandler = domErrorHandler;     
    } // DOMErrorHandlerWrapper(DOMErrorHandler domErrorHandler)


    //
    // Public methods
    //

    /** Sets the DOM error handler. */
    public void setErrorHandler(DOMErrorHandler errorHandler) {
        fDomErrorHandler = errorHandler;
    } // setErrorHandler(ErrorHandler)


    public DOMErrorHandler getErrorHandler(){
        return fDomErrorHandler;    
    } //getErrorHandler()

    //
    // XMLErrorHandler methods
    //

    /**
     * Reports a warning. Warnings are non-fatal and can be safely ignored
     * by most applications.
     *
     * @param domain    The domain of the warning. The domain can be any
     *                  string but is suggested to be a valid URI. The
     *                  domain can be used to conveniently specify a web
     *                  site location of the relevent specification or
     *                  document pertaining to this warning.
     * @param key       The warning key. This key can be any string and
     *                  is implementation dependent.
     * @param exception Exception.
     *
     * @throws XNIException Thrown to signal that the parser should stop
     *                      parsing the document.
     */

    public void warning(String domain, String key, 
                        XMLParseException exception) throws XNIException {
        fDOMError.fSeverity = DOMError.SEVERITY_WARNING;
        fDOMError.fException = exception;
        fDOMError.fType = key;         
        fDOMError.fRelatedData = fDOMError.fMessage = exception.getMessage();
        DOMLocatorImpl locator = fDOMError.fLocator;
        if (locator != null) {
            locator.fColumnNumber = exception.getColumnNumber();
            locator.fLineNumber = exception.getLineNumber();
            locator.fUri = exception.getExpandedSystemId();
            locator.fRelatedNode = fCurrentNode;
        }
        if (fDomErrorHandler != null) {
            fDomErrorHandler.handleError(fDOMError);
        } 
    } // warning(String,String,XMLParseException)

    /**
     * Reports an error. Errors are non-fatal and usually signify that the
     * document is invalid with respect to its grammar(s).
     *
     * @param domain    The domain of the error. The domain can be any
     *                  string but is suggested to be a valid URI. The
     *                  domain can be used to conveniently specify a web
     *                  site location of the relevent specification or
     *                  document pertaining to this error.
     * @param key       The error key. This key can be any string and
     *                  is implementation dependent.
     * @param exception Exception.
     *
     * @throws XNIException Thrown to signal that the parser should stop
     *                      parsing the document.
     */
    public void error(String domain, String key, 
                      XMLParseException exception) throws XNIException {
        fDOMError.fSeverity = DOMError.SEVERITY_ERROR;
        fDOMError.fException = exception;
        fDOMError.fType = key;         
        fDOMError.fRelatedData = fDOMError.fMessage = exception.getMessage();
        DOMLocatorImpl locator = fDOMError.fLocator;
        if (locator != null) {
            locator.fColumnNumber = exception.getColumnNumber();
            locator.fLineNumber = exception.getLineNumber();
            locator.fUri = exception.getExpandedSystemId();
            locator.fRelatedNode= fCurrentNode;
        }
        if (fDomErrorHandler != null) {
            fDomErrorHandler.handleError(fDOMError);
        }
    } // error(String,String,XMLParseException)

    /**
     * Report a fatal error. Fatal errors usually occur when the document
     * is not well-formed and signifies that the parser cannot continue
     * normal operation.
     * <p>
     * <strong>Note:</strong> The error handler should <em>always</em>
     * throw an <code>XNIException</code> from this method. This exception
     * can either be the same exception that is passed as a parameter to
     * the method or a new XNI exception object. If the registered error
     * handler fails to throw an exception, the continuing operation of
     * the parser is undetermined.
     *
     * @param domain    The domain of the fatal error. The domain can be 
     *                  any string but is suggested to be a valid URI. The
     *                  domain can be used to conveniently specify a web
     *                  site location of the relevent specification or
     *                  document pertaining to this fatal error.
     * @param key       The fatal error key. This key can be any string 
     *                  and is implementation dependent.
     * @param exception Exception.
     *
     * @throws XNIException Thrown to signal that the parser should stop
     *                      parsing the document.
     */
    public void fatalError(String domain, String key, 
                           XMLParseException exception) throws XNIException {
        fDOMError.fSeverity = DOMError.SEVERITY_FATAL_ERROR;
        fDOMError.fException = exception;
        fErrorCode.setValues(domain, key);
        String domErrorType = (String) fgDOMErrorTypeTable.get(fErrorCode);
        fDOMError.fType = (domErrorType != null) ? domErrorType : key;
        fDOMError.fRelatedData = fDOMError.fMessage = exception.getMessage();
        DOMLocatorImpl locator = fDOMError.fLocator;
        if (locator != null) {
            locator.fColumnNumber = exception.getColumnNumber();
            locator.fLineNumber = exception.getLineNumber();
            locator.fUri = exception.getExpandedSystemId();
            locator.fRelatedNode = fCurrentNode;
        }
        if (fDomErrorHandler != null) {
            fDomErrorHandler.handleError(fDOMError);
        } 
    } // fatalError(String,String,XMLParseException)


    public boolean handleError(DOMError error) {
        printError(error);
        return eStatus;
    }

    /** Prints the error message. */

    private void printError(DOMError error) {
        int severity = error.getSeverity();
        fOut.print("[");
        if ( severity == DOMError.SEVERITY_WARNING) {
            fOut.print("Warning");
        } else if ( severity == DOMError.SEVERITY_ERROR) {
            fOut.print("Error");
        } else {
            fOut.print("FatalError");
            eStatus = false ; //REVISIT: Abort processing if fatal error, do we need to??
        }
        fOut.print("] ");
        DOMLocator locator = error.getLocation();
        if (locator != null) {
            fOut.print(locator.getLineNumber());
            fOut.print(":");
            fOut.print(locator.getColumnNumber());
            fOut.print(":");
            fOut.print(locator.getByteOffset());
            fOut.print(",");            
            fOut.print(locator.getUtf16Offset());
            Node node = locator.getRelatedNode();
            if (node != null) {
                fOut.print("[");
                fOut.print(node.getNodeName());
                fOut.print("]");
            }
            String systemId = locator.getUri();
            if (systemId != null) {
                int index = systemId.lastIndexOf('/');
                if (index != -1)
                    systemId = systemId.substring(index + 1);
                fOut.print(": ");
                fOut.print(systemId);
            }

        }

        fOut.print(":");
        fOut.print(error.getMessage());
        fOut.println();
        fOut.flush();

    } // printError(DOMError)

} // class DOMErrorHandlerWrapper
