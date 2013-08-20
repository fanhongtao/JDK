/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
 * originally based on software copyright (c) 2003, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package com.sun.org.apache.xerces.internal.xs;

import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.ls.LSInput;

/**
 * An interface that provides a method to load XML Schema documents. This 
 * interface uses the DOM Level 3 Core and Load and Save interfaces.
 */
public interface XSLoader {
    /**
     *  The configuration of a document. It maintains a table of recognized 
     * parameters. Using the configuration, it is possible to change the 
     * behavior of the load methods. The configuration may support the 
     * setting of and the retrieval of the following non-boolean parameters 
     * defined on the <code>DOMConfiguration</code> interface: 
     * <code>error-handler</code> (<code>DOMErrorHandler</code>) and 
     * <code>resource-resolver</code> (<code>LSResourceResolver</code>). 
     * <br> The following list of boolean parameters is defined: 
     * <dl>
     * <dt>
     * <code>"validate"</code></dt>
     * <dd>
     * <dl>
     * <dt><code>true</code></dt>
     * <dd>[required] (default) Validate an XML 
     * Schema during loading. If validation errors are found, the error 
     * handler is notified. </dd>
     * <dt><code>false</code></dt>
     * <dd>[optional] Do not 
     * report errors during the loading of an XML Schema document. </dd>
     * </dl></dd>
     * </dl>
     */
    public DOMConfiguration getConfig();

    /**
     * Parses the content of XML Schema documents specified as the list of URI 
     * references. If the URI contains a fragment identifier, the behavior 
     * is not defined by this specification. 
     * @param uri The list of URI locations.
     * @return An XSModel representing the schema documents.
     */
    public XSModel loadURIList(StringList uriList);

    /**
     *  Parses the content of XML Schema documents specified as a list of 
     * <code>LSInput</code>s. 
     * @param is  The list of <code>LSInput</code>s from which the XML 
     *   Schema documents are to be read. 
     * @return An XSModel representing schema documents.
     */
    public XSModel loadInputList(LSInputList is);

    /**
     * Parse an XML Schema document from a location identified by a URI 
     * reference. If the URI contains a fragment identifier, the behavior is 
     * not defined by this specification. 
     * @param uri The location of the XML Schema document to be read.
     * @return An XSModel representing this schema.
     */
    public XSModel loadURI(String uri);

    /**
     *  Parse an XML Schema document from a resource identified by a 
     * <code>LSInput</code> . 
     * @param is  The <code>DOMInputSource</code> from which the source 
     *   document is to be read. 
     * @return An XSModel representing this schema.
     */
    public XSModel load(LSInput is);

}
