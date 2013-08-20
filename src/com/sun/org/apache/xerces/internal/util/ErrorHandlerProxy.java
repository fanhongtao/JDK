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

import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Wraps {@link XMLErrorHandler} and make it look like a SAX {@link ErrorHandler}.  
 * 
 * <p>
 * The derived class should override the {@link #getErrorHandler()} method
 * so that it will return the correct {@link XMLErrorHandler} instance.
 * This method will be called whenever an error/warning is found.
 * 
 * <p>
 * Experience shows that it is better to store the actual
 * {@link XMLErrorHandler} in one place and looks up that variable,
 * rather than copying it into every component that needs an error handler
 * and update all of them whenever it is changed, IMO.
 * 
 * @author
 *     Kohsuke Kawaguchi (kohsuke.kawaguchi@sun.com)
 */
public abstract class ErrorHandlerProxy implements ErrorHandler {
    
    public void error(SAXParseException e) throws SAXException {
        XMLErrorHandler eh = getErrorHandler();
        if(eh instanceof ErrorHandlerWrapper) {
            ((ErrorHandlerWrapper)eh).fErrorHandler.error(e);
        }
        eh.error("","",ErrorHandlerWrapper.createXMLParseException(e));
        // if an XNIException is thrown, just let it go.
        // REVISIT: is this OK? or should we try to wrap it into SAXException?
    }

    public void fatalError(SAXParseException e) throws SAXException {
        XMLErrorHandler eh = getErrorHandler();
        if(eh instanceof ErrorHandlerWrapper) {
            ((ErrorHandlerWrapper)eh).fErrorHandler.fatalError(e);
        }
        eh.fatalError("","",ErrorHandlerWrapper.createXMLParseException(e));
    }

    public void warning(SAXParseException e) throws SAXException {
        XMLErrorHandler eh = getErrorHandler();
        if(eh instanceof ErrorHandlerWrapper) {
            ((ErrorHandlerWrapper)eh).fErrorHandler.warning(e);
        }
        eh.warning("","",ErrorHandlerWrapper.createXMLParseException(e));
    }

    protected abstract XMLErrorHandler getErrorHandler();
}
