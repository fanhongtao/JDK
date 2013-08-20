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

package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.DOMError;
import org.w3c.dom.DOMLocator;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;


/**
 * <code>DOMErrorImpl</code> is an implementation that describes an error.
 * <strong>Note:</strong> The error object that describes the error 
 * might be reused by Xerces implementation, across multiple calls to the 
 * handleEvent method on DOMErrorHandler interface.
 * 
 * 
 * <p>See also the <a href='http://www.w3.org/TR/2001/WD-DOM-Level-3-Core-20010913'>Document Object Model (DOM) Level 3 Core Specification</a>.
 * 
 * @author Gopal Sharma, SUN Microsystems Inc.
 * @author Elena Litani, IBM
 *
 * @version $Id: DOMErrorImpl.java,v 1.10 2003/05/08 19:52:40 elena Exp $
 */

// REVISIT: the implementation of ErrorReporter. 
//          we probably should not pass XMLParseException
//

public class DOMErrorImpl implements DOMError {

    //
    // Data
    //

    public short fSeverity = DOMError.SEVERITY_WARNING;
    public String fMessage = null;
    public DOMLocatorImpl fLocator = new DOMLocatorImpl();
    public Exception fException = null;
    public String fType;
    public Object fRelatedData;
   


    //
    // Constructors
    //

    /** Default constructor. */    
    public DOMErrorImpl () {
    }

    /** Exctracts information from XMLParserException) */
    public DOMErrorImpl (short severity, XMLParseException exception) {
        fSeverity = severity;
        fException = exception;
        fLocator = createDOMLocator (exception);
    }

    /**
     * The severity of the error, either <code>SEVERITY_WARNING</code>, 
     * <code>SEVERITY_ERROR</code>, or <code>SEVERITY_FATAL_ERROR</code>.
     */

    public short getSeverity() {
        return fSeverity;
    }

    /**
     * An implementation specific string describing the error that occured.
     */

    public String getMessage() {
        return fMessage;
    }

    /**
     * The location of the error.
     */

    public DOMLocator getLocation() {
        return fLocator;
    }

    // method to get the DOMLocator Object
    private DOMLocatorImpl createDOMLocator(XMLParseException exception) {
        // assuming DOMLocator wants the *expanded*, not the literal, URI of the doc... - neilg
        return new DOMLocatorImpl(exception.getLineNumber(),
                                  exception.getColumnNumber(),
                                  exception.getExpandedSystemId()
                                 );
    } // createDOMLocator()


    /**
     * The related platform dependent exception if any.exception is a reserved 
     * word, we need to rename it.Change to "relatedException". (F2F 26 Sep 
     * 2001)
     */
    public Object getRelatedException(){
        return fException;
    }

    public void reset(){
        fSeverity = DOMError.SEVERITY_WARNING; 
        fException = null;
    }
    
    public String getType(){
        return fType;
    }
    
    public Object getRelatedData(){
        return fRelatedData;
    }


}// class DOMErrorImpl
