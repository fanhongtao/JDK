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

import org.w3c.dom.Entity;
import org.w3c.dom.Node;

/**
 * Entity nodes hold the reference data for an XML Entity -- either
 * parsed or unparsed. The nodeName (inherited from Node) will contain
 * the name (if any) of the Entity. Its data will be contained in the
 * Entity's children, in exactly the structure which an
 * EntityReference to this name will present within the document's
 * body.
 * <P>
 * Note that this object models the actual entity, _not_ the entity
 * declaration or the entity reference.
 * <P>
 * An XML processor may choose to completely expand entities before
 * the structure model is passed to the DOM; in this case, there will
 * be no EntityReferences in the DOM tree.
 * <P>
 * Quoting the 10/01 DOM Proposal,
 * <BLOCKQUOTE>
 * "The DOM Level 1 does not support editing Entity nodes; if a user
 * wants to make changes to the contents of an Entity, every related
 * EntityReference node has to be replaced in the structure model by
 * a clone of the Entity's contents, and then the desired changes
 * must be made to each of those clones instead. All the
 * descendants of an Entity node are readonly."
 * </BLOCKQUOTE>
 * I'm interpreting this as: It is the parser's responsibilty to call
 * the non-DOM operation setReadOnly(true,true) after it constructs
 * the Entity. Since the DOM explicitly decided not to deal with this,
 * _any_ answer will involve a non-DOM operation, and this is the
 * simplest solution.
 * 
 * @author Elena Litani, IBM
 * @version $Id: EntityImpl.java,v 1.23 2003/11/13 22:47:15 elena Exp $
 * @since PR-DOM-Level-1-19980818.
 */
public class EntityImpl 
    extends ParentNode
    implements Entity {

    //
    // Constants
    //

    /** Serialization version. */
    static final long serialVersionUID = -3575760943444303423L;
    
    //
    // Data
    //

    /** Entity name. */
    protected String name;

    /** Public identifier. */
    protected String publicId;

    /** System identifier. */
    protected String systemId;

    /** Encoding */
    protected String encoding;


    /** Input Encoding */
    protected String inputEncoding;
    
    /** Version */
    protected String version;


    /** Notation name. */
    protected String notationName;

    /** base uri*/
    protected String baseURI;

    //
    // Constructors
    //

    /** Factory constructor. */
    public EntityImpl(CoreDocumentImpl ownerDoc, String name) {
    	super(ownerDoc);
        this.name = name;
        isReadOnly(true);
    }
    
    //
    // Node methods
    //

    /** 
     * A short integer indicating what type of node this is. The named
     * constants for this value are defined in the org.w3c.dom.Node interface.
     */
    public short getNodeType() {
        return Node.ENTITY_NODE;
    }

    /**
     * Returns the entity name
     */
    public String getNodeName() {
        if (needsSyncData()) {
            synchronizeData();
        }
        return name;
    }

    /** Clone node. */
    public Node cloneNode(boolean deep) {
        EntityImpl newentity = (EntityImpl)super.cloneNode(deep);
        newentity.setReadOnly(true, deep);
        return newentity;
    }

    //
    // Entity methods
    //

    /** 
     * The public identifier associated with the entity. If not specified,
     * this will be null. 
     */
    public String getPublicId() {
        
        if (needsSyncData()) {
            synchronizeData();
        }
        return publicId;

    } // getPublicId():String

    /** 
     * The system identifier associated with the entity. If not specified,
     * this will be null. 
     */
    public String getSystemId() {

        if (needsSyncData()) {
            synchronizeData();
        }
        return systemId;

    } // getSystemId():String

    /** 
      * DOM Level 3 WD - experimental
      * the version number of this entity, when it is an external parsed entity. 
      */
    public String getXmlVersion() {

       if (needsSyncData()) {
           synchronizeData();
       }
       return version;

   } // getVersion():String


    /**
     * DOM Level 3 WD - experimental 
     * the encoding of this entity, when it is an external parsed entity. 
     */
    public String getXmlEncoding() {

       if (needsSyncData()) {
           synchronizeData();
       }

       return encoding;

   } // getVersion():String





    /** 
     * Unparsed entities -- which contain non-XML data -- have a
     * "notation name" which tells applications how to deal with them.
     * Parsed entities, which <em>are</em> in XML format, don't need this and
     * set it to null.  
     */
    public String getNotationName() {

        if (needsSyncData()) {
            synchronizeData();
        }
        return notationName;

    } // getNotationName():String

    //
    // Public methods
    //

    /**
     * DOM Level 2: The public identifier associated with the entity. If not specified,
     * this will be null. */
    public void setPublicId(String id) {
        
        if (needsSyncData()) {
            synchronizeData();
        }
    	publicId = id;

    } // setPublicId(String)

    /**
     * NON-DOM 
     * encoding - An attribute specifying, as part of the text declaration, 
     * the encoding of this entity, when it is an external parsed entity. 
     * This is null otherwise
     *
     */
    public void setXmlEncoding(String value) {
        if (needsSyncData()) {
            synchronizeData();
        }
        encoding = value;
    } // setEncoding (String)


    /**
     * An attribute specifying the encoding used for this entity at the tiome 
     * of parsing, when it is an external parsed entity. This is 
     * <code>null</code> if it an entity from the internal subset or if it 
     * is not known..
     * @since DOM Level 3
     */
    public String getInputEncoding(){
        if (needsSyncData()) {
            synchronizeData();
        }
        return inputEncoding;
    }
    
    /**
     * NON-DOM, used to set the input encoding.
     */
    public void setInputEncoding(String inputEncoding){
        if (needsSyncData()) {
            synchronizeData();
        }
        this.inputEncoding = inputEncoding;
    }

    /** 
      * NON-DOM
      * version - An attribute specifying, as part of the text declaration, 
      * the version number of this entity, when it is an external parsed entity. 
      * This is null otherwise
      */
    public void setXmlVersion(String value) {       
        if (needsSyncData()) {
            synchronizeData();
        }
        version = value;
    } // setVersion (String)


    /**
     * DOM Level 2: The system identifier associated with the entity. If not
     * specified, this will be null. 
     */
    public void setSystemId(String id) {
        if (needsSyncData()) {
            synchronizeData();
        }
    	systemId = id;

    } // setSystemId(String)

    /** 
     * DOM Level 2: Unparsed entities -- which contain non-XML data -- have a
     * "notation name" which tells applications how to deal with them.
     * Parsed entities, which <em>are</em> in XML format, don't need this and
     * set it to null.  
     */
    public void setNotationName(String name) {        
        if (needsSyncData()) {
            synchronizeData();
        }
    	notationName = name;

    } // setNotationName(String)
    


    /**
     * DOM Level 3 WD - Experimental.
     * Retrieve baseURI
     */
    public String getBaseURI() {

        if (needsSyncData()) {
            synchronizeData();
        }
        return (baseURI!=null)?baseURI:((CoreDocumentImpl)getOwnerDocument()).getBaseURI();
    }

    /** NON-DOM: set base uri*/
    public void setBaseURI(String uri){
        if (needsSyncData()) {
            synchronizeData();
        }
        baseURI = uri;
    }



} // class EntityImpl
