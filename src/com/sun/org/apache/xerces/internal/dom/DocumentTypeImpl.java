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
import org.w3c.dom.DocumentType;
import org.w3c.dom.Node;
import org.w3c.dom.NamedNodeMap;
import java.util.Hashtable;
import org.w3c.dom.UserDataHandler;

/**
 * This class represents a Document Type <em>declaraction</em> in
 * the document itself, <em>not</em> a Document Type Definition (DTD).
 * An XML document may (or may not) have such a reference.
 * <P>
 * DocumentType is an Extended DOM feature, used in XML documents but
 * not in HTML.
 * <P>
 * Note that Entities and Notations are no longer children of the
 * DocumentType, but are parentless nodes hung only in their
 * appropriate NamedNodeMaps.
 * <P>
 * This area is UNDERSPECIFIED IN REC-DOM-Level-1-19981001
 * Most notably, absolutely no provision was made for storing
 * and using Element and Attribute information. Nor was the linkage
 * between Entities and Entity References nailed down solidly.
 *
 * @author Arnaud  Le Hors, IBM
 * @author Joe Kesselman, IBM
 * @author Andy Clark, IBM
 * @version $Id: DocumentTypeImpl.java,v 1.25 2004/01/16 16:23:50 elena Exp $
 * @since  PR-DOM-Level-1-19980818.
 */
public class DocumentTypeImpl
extends ParentNode
implements DocumentType {
    
    //
    // Constants
    //
    
    /** Serialization version. */
    static final long serialVersionUID = 7751299192316526485L;
    
    //
    // Data
    //
    
    /** Document type name. */
    protected String name;
    
    /** Entities. */
    protected NamedNodeMapImpl entities;
    
    /** Notations. */
    protected NamedNodeMapImpl notations;
    
    // NON-DOM
    
    /** Elements. */
    protected NamedNodeMapImpl elements;
    
    // DOM2: support public ID.
    protected String publicID;
    
    // DOM2: support system ID.
    protected String systemID;
    
    // DOM2: support internal subset.
    protected String internalSubset;
    
    /** The following are required for compareDocumentPosition
     */
    // Doctype number.   Doc types which have no owner may be assigned
    // a number, on demand, for ordering purposes for compareDocumentPosition
    private int doctypeNumber=0;
    
    //
    // Constructors
    //
    private Hashtable userData =  null;
    
    /** Factory method for creating a document type node. */
    public DocumentTypeImpl(CoreDocumentImpl ownerDocument, String name) {
        super(ownerDocument);
        
        this.name = name;
        // DOM
        entities  = new NamedNodeMapImpl(this);
        notations = new NamedNodeMapImpl(this);
        
        // NON-DOM
        elements = new NamedNodeMapImpl(this);
        
    } // <init>(CoreDocumentImpl,String)
    
    /** Factory method for creating a document type node. */
    public DocumentTypeImpl(CoreDocumentImpl ownerDocument,
    String qualifiedName,
    String publicID, String systemID) {
        this(ownerDocument, qualifiedName);
        this.publicID = publicID;
        this.systemID = systemID;
        
    } // <init>(CoreDocumentImpl,String)
    
    //
    // DOM2: methods.
    //
    
    /**
     * Introduced in DOM Level 2. <p>
     *
     * Return the public identifier of this Document type.
     * @since WD-DOM-Level-2-19990923
     */
    public String getPublicId() {
        if (needsSyncData()) {
            synchronizeData();
        }
        return publicID;
    }
    /**
     * Introduced in DOM Level 2. <p>
     *
     * Return the system identifier of this Document type.
     * @since WD-DOM-Level-2-19990923
     */
    public String getSystemId() {
        if (needsSyncData()) {
            synchronizeData();
        }
        return systemID;
    }
    
    /**
     * NON-DOM. <p>
     *
     * Set the internalSubset given as a string.
     */
    public void setInternalSubset(String internalSubset) {
        if (needsSyncData()) {
            synchronizeData();
        }
        this.internalSubset = internalSubset;
    }
    
    /**
     * Introduced in DOM Level 2. <p>
     *
     * Return the internalSubset given as a string.
     * @since WD-DOM-Level-2-19990923
     */
    public String getInternalSubset() {
        if (needsSyncData()) {
            synchronizeData();
        }
        return internalSubset;
    }
    
    //
    // Node methods
    //
    
    /**
     * A short integer indicating what type of node this is. The named
     * constants for this value are defined in the org.w3c.dom.Node interface.
     */
    public short getNodeType() {
        return Node.DOCUMENT_TYPE_NODE;
    }
    
    /**
     * Returns the document type name
     */
    public String getNodeName() {
        if (needsSyncData()) {
            synchronizeData();
        }
        return name;
    }
    
    /** Clones the node. */
    public Node cloneNode(boolean deep) {
        
        DocumentTypeImpl newnode = (DocumentTypeImpl)super.cloneNode(deep);
        // NamedNodeMaps must be cloned explicitly, to avoid sharing them.
        newnode.entities  = entities.cloneMap(newnode);
        newnode.notations = notations.cloneMap(newnode);
        newnode.elements  = elements.cloneMap(newnode);
        
        return newnode;
        
    } // cloneNode(boolean):Node
    
    /*
     * Get Node text content
     * @since DOM Level 3
     */
    public String getTextContent() throws DOMException {
        return null;
    }
    
    /*
     * Set Node text content
     * @since DOM Level 3
     */
    public void setTextContent(String textContent)
    throws DOMException {
        // no-op
    }
    
    /**
     * DOM Level 3 WD- Experimental.
     * Override inherited behavior from ParentNodeImpl to support deep equal.
     */
    public boolean isEqualNode(Node arg) {
        
        if (!super.isEqualNode(arg)) {
            return false;
        }
        
        if (needsSyncData()) {
            synchronizeData();
        }
        DocumentTypeImpl argDocType = (DocumentTypeImpl) arg;
        
        //test if the following string attributes are equal: publicId,
        //systemId, internalSubset.
        if ((getPublicId() == null && argDocType.getPublicId() != null)
        || (getPublicId() != null && argDocType.getPublicId() == null)
        || (getSystemId() == null && argDocType.getSystemId() != null)
        || (getSystemId() != null && argDocType.getSystemId() == null)
        || (getInternalSubset() == null
        && argDocType.getInternalSubset() != null)
        || (getInternalSubset() != null
        && argDocType.getInternalSubset() == null)) {
            return false;
        }
        
        if (getPublicId() != null) {
            if (!getPublicId().equals(argDocType.getPublicId())) {
                return false;
            }
        }
        
        if (getSystemId() != null) {
            if (!getSystemId().equals(argDocType.getSystemId())) {
                return false;
            }
        }
        
        if (getInternalSubset() != null) {
            if (!getInternalSubset().equals(argDocType.getInternalSubset())) {
                return false;
            }
        }
        
        //test if NamedNodeMaps entities and notations are equal
        NamedNodeMapImpl argEntities = argDocType.entities;
        
        if ((entities == null && argEntities != null)
        || (entities != null && argEntities == null))
            return false;
        
        if (entities != null && argEntities != null) {
            if (entities.getLength() != argEntities.getLength())
                return false;
            
            for (int index = 0; entities.item(index) != null; index++) {
                Node entNode1 = entities.item(index);
                Node entNode2 =
                argEntities.getNamedItem(entNode1.getNodeName());
                
                if (!((NodeImpl) entNode1).isEqualNode((NodeImpl) entNode2))
                    return false;
            }
        }
        
        NamedNodeMapImpl argNotations = argDocType.notations;
        
        if ((notations == null && argNotations != null)
        || (notations != null && argNotations == null))
            return false;
        
        if (notations != null && argNotations != null) {
            if (notations.getLength() != argNotations.getLength())
                return false;
            
            for (int index = 0; notations.item(index) != null; index++) {
                Node noteNode1 = notations.item(index);
                Node noteNode2 =
                argNotations.getNamedItem(noteNode1.getNodeName());
                
                if (!((NodeImpl) noteNode1).isEqualNode((NodeImpl) noteNode2))
                    return false;
            }
        }
        
        return true;
    } //end isEqualNode
    
    
    /**
     * NON-DOM
     * set the ownerDocument of this node and its children
     */
    void setOwnerDocument(CoreDocumentImpl doc) {
        super.setOwnerDocument(doc);
        entities.setOwnerDocument(doc);
        notations.setOwnerDocument(doc);
        elements.setOwnerDocument(doc);
    }
    
    /** NON-DOM
     * Get the number associated with this doctype.
     */
    protected int getNodeNumber() {
        // If the doctype has a document owner, get the node number
        // relative to the owner doc
        if (getOwnerDocument()!=null)
            return super.getNodeNumber();
        
        // The doctype is disconnected and not associated with any document.
        // Assign the doctype a number relative to the implementation.
        if (doctypeNumber==0) {
            
            CoreDOMImplementationImpl cd = (CoreDOMImplementationImpl)CoreDOMImplementationImpl.getDOMImplementation();
            doctypeNumber = cd.assignDocTypeNumber();
        }
        return doctypeNumber;
    }
    
    //
    // DocumentType methods
    //
    
    /**
     * Name of this document type. If we loaded from a DTD, this should
     * be the name immediately following the DOCTYPE keyword.
     */
    public String getName() {
        
        if (needsSyncData()) {
            synchronizeData();
        }
        return name;
        
    } // getName():String
    
    /**
     * Access the collection of general Entities, both external and
     * internal, defined in the DTD. For example, in:
     * <p>
     * <pre>
     *   &lt;!doctype example SYSTEM "ex.dtd" [
     *     &lt;!ENTITY foo "foo"&gt;
     *     &lt;!ENTITY bar "bar"&gt;
     *     &lt;!ENTITY % baz "baz"&gt;
     *     ]&gt;
     * </pre>
     * <p>
     * The Entities map includes foo and bar, but not baz. It is promised that
     * only Nodes which are Entities will exist in this NamedNodeMap.
     * <p>
     * For HTML, this will always be null.
     * <p>
     * Note that "built in" entities such as &amp; and &lt; should be
     * converted to their actual characters before being placed in the DOM's
     * contained text, and should be converted back when the DOM is rendered
     * as XML or HTML, and hence DO NOT appear here.
     */
    public NamedNodeMap getEntities() {
        if (needsSyncChildren()) {
            synchronizeChildren();
        }
        return entities;
    }
    
    /**
     * Access the collection of Notations defined in the DTD.  A
     * notation declares, by name, the format of an XML unparsed entity
     * or is used to formally declare a Processing Instruction target.
     */
    public NamedNodeMap getNotations() {
        if (needsSyncChildren()) {
            synchronizeChildren();
        }
        return notations;
    }
    
    //
    // Public methods
    //
    
    /**
     * NON-DOM: Subclassed to flip the entities' and notations' readonly switch
     * as well.
     * @see NodeImpl#setReadOnly
     */
    public void setReadOnly(boolean readOnly, boolean deep) {
        
        if (needsSyncChildren()) {
            synchronizeChildren();
        }
        super.setReadOnly(readOnly, deep);
        
        // set read-only property
        elements.setReadOnly(readOnly, true);
        entities.setReadOnly(readOnly, true);
        notations.setReadOnly(readOnly, true);
        
    } // setReadOnly(boolean,boolean)
    
    /**
     * NON-DOM: Access the collection of ElementDefinitions.
     * @see ElementDefinitionImpl
     */
    public NamedNodeMap getElements() {
        if (needsSyncChildren()) {
            synchronizeChildren();
        }
        return elements;
    }
    
    public Object setUserData(String key,
    Object data, UserDataHandler handler) {
        if(userData == null)
            userData = new Hashtable();
        if (data == null) {
            if (userData != null) {
                Object o = userData.remove(key);
                if (o != null) {
                    UserDataRecord r = (UserDataRecord) o;
                    return r.fData;
                }
            }
            return null;
        }
        else {
            Object o = userData.put(key, new UserDataRecord(data, handler));
            if (o != null) {
                UserDataRecord r = (UserDataRecord) o;
                return r.fData;
            }
        }
        return null;
    }
    
    public Object getUserData(String key) {
        if (userData == null) {
            return null;
        }
        Object o = userData.get(key);
        if (o != null) {
            UserDataRecord r = (UserDataRecord) o;
            return r.fData;
        }
        return null;
    }
    
    protected Hashtable getUserDataRecord(){
        return userData;
    }
    
} // class DocumentTypeImpl
