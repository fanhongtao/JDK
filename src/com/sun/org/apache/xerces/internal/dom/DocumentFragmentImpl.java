/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights 
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

import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * DocumentFragment is a "lightweight" or "minimal" Document
 * object. It is very common to want to be able to extract a portion
 * of a document's tree or to create a new fragment of a
 * document. Imagine implementing a user command like cut or
 * rearranging a document by moving fragments around. It is desirable
 * to have an object which can hold such fragments and it is quite
 * natural to use a Node for this purpose. While it is true that a
 * Document object could fulfil this role, a Document object can
 * potentially be a heavyweight object, depending on the underlying
 * implementation... and in DOM Level 1, nodes aren't allowed to cross
 * Document boundaries anyway. What is really needed for this is a
 * very lightweight object.  DocumentFragment is such an object.
 * <P>
 * Furthermore, various operations -- such as inserting nodes as
 * children of another Node -- may take DocumentFragment objects as
 * arguments; this results in all the child nodes of the
 * DocumentFragment being moved to the child list of this node.
 * <P>
 * The children of a DocumentFragment node are zero or more nodes
 * representing the tops of any sub-trees defining the structure of
 * the document.  DocumentFragment do not need to be well-formed XML
 * documents (although they do need to follow the rules imposed upon
 * well-formed XML parsed entities, which can have multiple top
 * nodes). For example, a DocumentFragment might have only one child
 * and that child node could be a Text node. Such a structure model
 * represents neither an HTML document nor a well-formed XML document.
 * <P>
 * When a DocumentFragment is inserted into a Document (or indeed any
 * other Node that may take children) the children of the
 * DocumentFragment and not the DocumentFragment itself are inserted
 * into the Node. This makes the DocumentFragment very useful when the
 * user wishes to create nodes that are siblings; the DocumentFragment
 * acts as the parent of these nodes so that the user can use the
 * standard methods from the Node interface, such as insertBefore()
 * and appendChild().
 *
 * @version $Id: DocumentFragmentImpl.java,v 1.11 2003/02/11 22:20:33 neilg Exp $
 * @since  PR-DOM-Level-1-19980818.
 */
public class DocumentFragmentImpl 
    extends ParentNode
    implements DocumentFragment {

    //
    // Constants
    //

    /** Serialization version. */
    static final long serialVersionUID = -7596449967279236746L;
    
    //
    // Constructors
    //

    /** Factory constructor. */
    public DocumentFragmentImpl(CoreDocumentImpl ownerDoc) {
        super(ownerDoc);
    }  
  
    /** Constructor for serialization. */
    public DocumentFragmentImpl() {}

    //
    // Node methods
    //

    /** 
     * A short integer indicating what type of node this is. The named
     * constants for this value are defined in the org.w3c.dom.Node interface.
     */
    public short getNodeType() {
        return Node.DOCUMENT_FRAGMENT_NODE;
    }

    /** Returns the node name. */
    public String getNodeName() {
        return "#document-fragment";
    }
    
    /**
     * Override default behavior to call normalize() on this Node's
     * children. It is up to implementors or Node to override normalize()
     * to take action.
     */
    public void normalize() {
        // No need to normalize if already normalized.
        if (isNormalized()) {
            return;
        }
        if (needsSyncChildren()) {
            synchronizeChildren();
        }
        ChildNode kid, next;

        for (kid = firstChild; kid != null; kid = next) {
            next = kid.nextSibling;

            // If kid is a text node, we need to check for one of two
            // conditions:
            //   1) There is an adjacent text node
            //   2) There is no adjacent text node, but kid is
            //      an empty text node.
            if ( kid.getNodeType() == Node.TEXT_NODE )
            {
                // If an adjacent text node, merge it with kid
                if ( next!=null && next.getNodeType() == Node.TEXT_NODE )
                {
                    ((Text)kid).appendData(next.getNodeValue());
                    removeChild( next );
                    next = kid; // Don't advance; there might be another.
                }
                else
                {
                    // If kid is empty, remove it
                    if ( kid.getNodeValue().length()==0 )
                        removeChild( kid );
                }
            }

            kid.normalize();
        }

        isNormalized(true);
    }

} // class DocumentFragmentImpl
