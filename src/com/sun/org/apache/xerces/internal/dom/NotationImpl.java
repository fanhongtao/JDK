/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights 
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

package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.w3c.dom.Notation;

/**
 * Notations are how the Document Type Description (DTD) records hints
 * about the format of an XML "unparsed entity" -- in other words,
 * non-XML data bound to this document type, which some applications
 * may wish to consult when manipulating the document. A Notation
 * represents a name-value pair, with its nodeName being set to the
 * declared name of the notation.
 * <P>
 * Notations are also used to formally declare the "targets" of
 * Processing Instructions.
 * <P>
 * Note that the Notation's data is non-DOM information; the DOM only
 * records what and where it is.
 * <P>
 * See the XML 1.0 spec, sections 4.7 and 2.6, for more info.
 * <P>
 * Level 1 of the DOM does not support editing Notation contents.
 *
 * @version $Id: NotationImpl.java,v 1.16 2002/08/24 20:39:22 lmartin Exp $
 * @since  PR-DOM-Level-1-19980818.
 */
public class NotationImpl 
    extends NodeImpl 
    implements Notation {

    //
    // Constants
    //

    /** Serialization version. */
    static final long serialVersionUID = -764632195890658402L;
    
    //
    // Data
    //

    /** Notation name. */
    protected String name;

    /** Public identifier. */
    protected String publicId;

    /** System identifier. */
    protected String systemId;

    /** Base URI*/
    protected String baseURI;

    //
    // Constructors
    //

    /** Factory constructor. */
    public NotationImpl(CoreDocumentImpl ownerDoc, String name) {
    	super(ownerDoc);
        this.name = name;
    }
    
    //
    // Node methods
    //

    /** 
     * A short integer indicating what type of node this is. The named
     * constants for this value are defined in the org.w3c.dom.Node interface.
     */
    public short getNodeType() {
        return Node.NOTATION_NODE;
    }

    /**
     * Returns the notation name
     */
    public String getNodeName() {
        if (needsSyncData()) {
            synchronizeData();
        }
        return name;
    }

    //
    // Notation methods
    //

    /**
     * The Public Identifier for this Notation. If no public identifier
     * was specified, this will be null.  
     */
    public String getPublicId() {

        if (needsSyncData()) {
            synchronizeData();
        }
    	return publicId;

    } // getPublicId():String

    /**
     * The System Identifier for this Notation. If no system identifier
     * was specified, this will be null.  
     */
    public String getSystemId() {

        if (needsSyncData()) {
            synchronizeData();
        }
    	return systemId;

    } // getSystemId():String

    //
    // Public methods
    //

    /** 
     * NON-DOM: The Public Identifier for this Notation. If no public
     * identifier was specified, this will be null.  
     */
    public void setPublicId(String id) {

    	if (isReadOnly()) {
    		throw new DOMException(
    		DOMException.NO_MODIFICATION_ALLOWED_ERR,
                DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NO_MODIFICATION_ALLOWED_ERR", null));
        }
        if (needsSyncData()) {
            synchronizeData();
        }
        publicId = id;

    } // setPublicId(String)

    /** 
     * NON-DOM: The System Identifier for this Notation. If no system
     * identifier was specified, this will be null.  
     */
    public void setSystemId(String id) {

    	if(isReadOnly()) {
    		throw new DOMException(
    		DOMException.NO_MODIFICATION_ALLOWED_ERR,
                DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "NO_MODIFICATION_ALLOWED_ERR", null));
        }
        if (needsSyncData()) {
            synchronizeData();
        }
    	systemId = id;

    } // setSystemId(String)
    

    /**
     * DOM Level 3 WD - Experimental.
     * Retrieve baseURI
     */
    public String getBaseURI() {
        if (needsSyncData()) {
            synchronizeData();
        }
        return baseURI;
    }

    /** NON-DOM: set base uri*/
    public void setBaseURI(String uri){
        if (needsSyncData()) {
            synchronizeData();
        }
        baseURI = uri;
    }

} // class NotationImpl
