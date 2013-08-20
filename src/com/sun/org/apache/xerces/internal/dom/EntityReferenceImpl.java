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

import org.w3c.dom.DocumentType;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * EntityReference models the XML &entityname; syntax, when used for
 * entities defined by the DOM. Entities hardcoded into XML, such as
 * character entities, should instead have been translated into text
 * by the code which generated the DOM tree.
 * <P>
 * An XML processor has the alternative of fully expanding Entities
 * into the normal document tree. If it does so, no EntityReference nodes
 * will appear.
 * <P>
 * Similarly, non-validating XML processors are not required to read
 * or process entity declarations made in the external subset or
 * declared in external parameter entities. Hence, some applications
 * may not make the replacement value available for Parsed Entities 
 * of these types.
 * <P>
 * EntityReference behaves as a read-only node, and the children of 
 * the EntityReference (which reflect those of the Entity, and should
 * also be read-only) give its replacement value, if any. They are 
 * supposed to automagically stay in synch if the DocumentType is 
 * updated with new values for the Entity.
 * <P>
 * The defined behavior makes efficient storage difficult for the DOM
 * implementor. We can't just look aside to the Entity's definition
 * in the DocumentType since those nodes have the wrong parent (unless
 * we can come up with a clever "imaginary parent" mechanism). We
 * must at least appear to clone those children... which raises the
 * issue of keeping the reference synchronized with its parent.
 * This leads me back to the "cached image of centrally defined data"
 * solution, much as I dislike it.
 * <P>
 * For now I have decided, since REC-DOM-Level-1-19980818 doesn't
 * cover this in much detail, that synchronization doesn't have to be
 * considered while the user is deep in the tree. That is, if you're
 * looking within one of the EntityReferennce's children and the Entity
 * changes, you won't be informed; instead, you will continue to access
 * the same object -- which may or may not still be part of the tree.
 * This is the same behavior that obtains elsewhere in the DOM if the
 * subtree you're looking at is deleted from its parent, so it's
 * acceptable here. (If it really bothers folks, we could set things
 * up so deleted subtrees are walked and marked invalid, but that's
 * not part of the DOM's defined behavior.)
 * <P>
 * As a result, only the EntityReference itself has to be aware of
 * changes in the Entity. And it can take advantage of the same
 * structure-change-monitoring code I implemented to support
 * DeepNodeList.
 * 
 * @author Arnaud  Le Hors, IBM
 * @author Joe Kesselman, IBM
 * @author Andy Clark, IBM
 * @author Ralf Pfeiffer, IBM
 * @version $Id: EntityReferenceImpl.java,v 1.23 2003/05/08 19:52:40 elena Exp $
 * @since  PR-DOM-Level-1-19980818.
 */
public class EntityReferenceImpl 
extends ParentNode
implements EntityReference {

    //
    // Constants
    //

    /** Serialization version. */
    static final long serialVersionUID = -7381452955687102062L;

    //
    // Data
    //

    /** Name of Entity referenced */
    protected String name;
    /** Base URI*/
    protected String baseURI;
    

    /** Entity changes. */
    //protected int entityChanges = -1;	

    /** Enable synchronize. */
    //protected boolean fEnableSynchronize = false;

    //
    // Constructors
    //

    /** Factory constructor. */
    public EntityReferenceImpl(CoreDocumentImpl ownerDoc, String name) {
        super(ownerDoc);
        this.name = name;
        isReadOnly(true);
        needsSyncChildren(true);
    }

    //
    // Node methods
    //

    /** 
     * A short integer indicating what type of node this is. The named
     * constants for this value are defined in the org.w3c.dom.Node interface.
     */
    public short getNodeType() {
        return Node.ENTITY_REFERENCE_NODE;
    }

    /**
     * Returns the name of the entity referenced
     */
    public String getNodeName() {
        if (needsSyncData()) {
            synchronizeData();
        }
        return name;
    }

    /** Clone node. */
    public Node cloneNode(boolean deep) {
        EntityReferenceImpl er = (EntityReferenceImpl)super.cloneNode(deep);
        er.setReadOnly(true, deep);
        return er;
    }

    /**
     * DOM Level 3 WD - Experimental.
     * Retrieve baseURI
     */
    public String getBaseURI() {
        if (needsSyncData()) {
            synchronizeData();
        }
        if (baseURI == null) {
            DocumentType doctype;
            NamedNodeMap entities;
            EntityImpl entDef;
            if (null != (doctype = getOwnerDocument().getDoctype()) && 
                null != (entities = doctype.getEntities())) {

                entDef = (EntityImpl)entities.getNamedItem(getNodeName());
                if (entDef !=null) {
                    return entDef.getBaseURI();
                }
            }
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
    
	/**
	 * NON-DOM: compute string representation of the entity reference.
     * This method is used to retrieve a string value for an attribute node that has child nodes. 
	 * @return String representing a value of this entity ref. or 
     *          null if any node other than EntityReference, Text is encountered
     *          during computation
	 */
    protected String getEntityRefValue (){
        if (needsSyncChildren()){
            synchronizeChildren();
        }
       
        String value = "";
        if (firstChild != null){
          if (firstChild.getNodeType() == Node.ENTITY_REFERENCE_NODE){
              value = ((EntityReferenceImpl)firstChild).getEntityRefValue();
          }
          else if (firstChild.getNodeType() == Node.TEXT_NODE){
            value = firstChild.getNodeValue();
          }
          else {
             // invalid to have other types of nodes in attr value
            return null;
          }
          
          if (firstChild.nextSibling == null){
            return value;
          }
          else {
            StringBuffer buff = new StringBuffer(value);
            ChildNode next = firstChild.nextSibling;
            while (next != null){   
            
                if (next.getNodeType() == Node.ENTITY_REFERENCE_NODE){
                   value = ((EntityReferenceImpl)next).getEntityRefValue();
                }
                else if (next.getNodeType() == Node.TEXT_NODE){
                  value = next.getNodeValue();
                }
                else {
                    // invalid to have other types of nodes in attr value
                    return null;
                }
                buff.append(value);
                next = next.nextSibling;

            }
            return buff.toString();  
          }   
        } 
        return "";
    }

    /**
     * EntityReference's children are a reflection of those defined in the
     * named Entity. This method creates them if they haven't been created yet.
     * This doesn't support editing the Entity though, since this only called
     * once for all.
     */
    protected void synchronizeChildren() {
        // no need to synchronize again
        needsSyncChildren(false);

        DocumentType doctype;
        NamedNodeMap entities;
        EntityImpl entDef;
        if (null != (doctype = getOwnerDocument().getDoctype()) && 
            null != (entities = doctype.getEntities())) {

            entDef = (EntityImpl)entities.getNamedItem(getNodeName());

            // No Entity by this name, stop here.
            if (entDef == null)
                return;

            // If entity's definition exists, clone its kids
            isReadOnly(false);
            for (Node defkid = entDef.getFirstChild();
                defkid != null;
                defkid = defkid.getNextSibling()) {
                Node newkid = defkid.cloneNode(true);
                insertBefore(newkid, null);
            }
            setReadOnly(true, true);
        }
    }


    /**
     * NON-DOM: sets the node and its children value.     
     * <P>
     * Note: make sure that entity reference and its kids could be set readonly.
     */
    public void setReadOnly(boolean readOnly, boolean deep) {

        if (needsSyncData()) {
            synchronizeData();
        }
        if (deep) {

            if (needsSyncChildren()) {
                synchronizeChildren();
            }
            // Recursively set kids
            for (ChildNode mykid = firstChild;
                 mykid != null;
                 mykid = mykid.nextSibling) {
                     
                mykid.setReadOnly(readOnly,true);
                
            }
        }
        isReadOnly(readOnly);
    } // setReadOnly(boolean,boolean)


    /**
     * Enable the synchronize method which may do cloning. This method is enabled
     * when the parser is done with an EntityReference.
    /***
    // revisit: enable editing of Entity
    public void enableSynchronize(boolean enableSynchronize) {
        fEnableSynchronize= enableSynchronize;
    }
    /***/

    /**
     * EntityReference's children are a reflection of those defined in the
     * named Entity. This method updates them if the Entity is changed.
     * <P>
     * It is unclear what the least-cost resynch mechanism is.
     * If we expect the kids to be shallow, and/or expect changes
     * to the Entity contents to be rare, wiping them all out
     * and recloning is simplest.
     * <P>
     * If we expect them to be deep,
     * it might be better to first decide which kids (if any)
     * persist, and keep the ones (if any) that are unchanged
     * rather than doing all the work of cloning them again.
     * But that latter gets into having to convolve the two child lists,
     * insert new information in the right order (and possibly reorder
     * the existing kids), and a few other complexities that I really
     * don't want to deal with in this implementation.
     * <P>
     * Note that if we decide that we need to update the EntityReference's
     * contents, we have to turn off the readOnly flag temporarily to do so.
     * When we get around to adding multitasking support, this whole method
     * should probably be an atomic operation.
     * 
     * @see DocumentTypeImpl
     * @see EntityImpl
     */
    // The Xerces parser invokes callbacks for startEnityReference
    // the parsed value of the entity EACH TIME, so it is actually 
    // easier to create the nodes through the callbacks rather than
    // clone the Entity.
    /***
    // revisit: enable editing of Entity
    private void synchronize() {
        if (!fEnableSynchronize) {
            return;
        }
        DocumentType doctype;
        NamedNodeMap entities;
        EntityImpl entDef;
        if (null != (doctype = getOwnerDocument().getDoctype()) && 
            null != (entities = doctype.getEntities())) {
            
            entDef = (EntityImpl)entities.getNamedItem(getNodeName());

            // No Entity by this name. If we had a change count, reset it.
            if(null==entDef)
                entityChanges=-1;

            // If no kids availalble, wipe any pre-existing children.
            // (See discussion above.)
            // Note that we have to use the superclass to avoid recursion
            // through Synchronize.
            readOnly=false;
            if(null==entDef || !entDef.hasChildNodes())
                for(Node kid=super.getFirstChild();
                    kid!=null;
                    kid=super.getFirstChild())
                    removeChild(kid);

            // If entity's definition changed, clone its kids
            // (See discussion above.)
            if(null!=entDef && entDef.changes!=entityChanges) {
                for(Node defkid=entDef.getFirstChild();
                    defkid!=null;
                    defkid=defkid.getNextSibling()) {
                    
                    NodeImpl newkid=(NodeImpl) defkid.cloneNode(true);
                    newkid.setReadOnly(true,true);
                    insertBefore(newkid,null);
                }
                entityChanges=entDef.changes;
            }
            readOnly=true;
        }
    }
     /***/


} // class EntityReferenceImpl
