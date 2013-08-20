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
 * originally based on software copyright (c) 1999, Sun Microsystems, Inc.,
 * http://www.sun.com.  For more information on the Apache Software
 * Foundation, please see <http://www.apache.org/>.
 */

package com.sun.org.apache.xerces.internal.jaxp;

import java.util.Locale;
import com.sun.org.apache.xerces.internal.util.SAXMessageFormatter;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @version $Id: DefaultValidationErrorHandler.java,v 1.5 2004/02/24 23:15:58 mrglavas Exp $
 */

class DefaultValidationErrorHandler extends DefaultHandler {
    static private int ERROR_COUNT_LIMIT = 10;
    private int errorCount = 0;
    
    
    public void error(SAXParseException e) throws SAXException {
        if (errorCount >= ERROR_COUNT_LIMIT) {
            // Ignore all errors after reaching the limit
            return;
        } else if (errorCount == 0) {
            // Print a warning before the first error
            
            System.err.println(SAXMessageFormatter.formatMessage(Locale.getDefault(), "errorHandlerNotSet", new Object[]{String.valueOf(ERROR_COUNT_LIMIT)}));
            
        }
        
        String systemId = e.getSystemId();
        if (systemId == null) {
            systemId = "null";
        }
        
        System.err.println(SAXMessageFormatter.formatMessage(Locale.getDefault(),"errorHandlerDebugMsg", new Object[] {
            systemId,String.valueOf(e.getLineNumber()), e.getMessage()}));
            errorCount++;
    }
}
