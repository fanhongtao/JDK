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

package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.Node;

/**
 * ChildNode inherits from NodeImpl and adds the capability of being a child by
 * having references to its previous and next siblings.
 *
 * @version $Id: ChildNode.java,v 1.8 2004/02/10 17:09:45 elena Exp $
 */
public abstract class ChildNode
    extends NodeImpl {

    //
    // Constants
    //

    /** Serialization version. */
    static final long serialVersionUID = -6112455738802414002L;

    transient StringBuffer fBufferStr = null;
    
    //
    // Data
    //
    
    /** Previous sibling. */
    protected ChildNode previousSibling;

    /** Next sibling. */
    protected ChildNode nextSibling;

    //
    // Constructors
    //

    /**
     * No public constructor; only subclasses of Node should be
     * instantiated, and those normally via a Document's factory methods
     * <p>
     * Every Node knows what Document it belongs to.
     */
    protected ChildNode(CoreDocumentImpl ownerDocument) {
        super(ownerDocument);
    } // <init>(CoreDocumentImpl)

    /** Constructor for serialization. */
    public ChildNode() {}

    //
    // Node methods
    //

    /**
     * Returns a duplicate of a given node. You can consider this a
     * generic "copy constructor" for nodes. The newly returned object should
     * be completely independent of the source object's subtree, so changes
     * in one after the clone has been made will not affect the other.
     * <P>
     * Note: since we never have any children deep is meaningless here,
     * ParentNode overrides this behavior.
     * @see ParentNode
     *
     * <p>
     * Example: Cloning a Text node will copy both the node and the text it
     * contains.
     * <p>
     * Example: Cloning something that has children -- Element or Attr, for
     * example -- will _not_ clone those children unless a "deep clone"
     * has been requested. A shallow clone of an Attr node will yield an
     * empty Attr of the same name.
     * <p>
     * NOTE: Clones will always be read/write, even if the node being cloned
     * is read-only, to permit applications using only the DOM API to obtain
     * editable copies of locked portions of the tree.
     */
    public Node cloneNode(boolean deep) {

    	ChildNode newnode = (ChildNode) super.cloneNode(deep);
    	
        // Need to break the association w/ original kids
    	newnode.previousSibling = null;
        newnode.nextSibling     = null;
        newnode.isFirstChild(false);

    	return newnode;

    } // cloneNode(boolean):Node

    /**
     * Returns the parent node of this node
     */
    public Node getParentNode() {
        // if we have an owner, ownerNode is our parent, otherwise it's
        // our ownerDocument and we don't have a parent
        return isOwned() ? ownerNode : null;
    }

    /*
     * same as above but returns internal type
     */
    final NodeImpl parentNode() {
        // if we have an owner, ownerNode is our parent, otherwise it's
        // our ownerDocument and we don't have a parent
        return isOwned() ? ownerNode : null;
    }

    /** The next child of this node's parent, or null if none */
    public Node getNextSibling() {
        return nextSibling;
    }

    /** The previous child of this node's parent, or null if none */
    public Node getPreviousSibling() {
        // if we are the firstChild, previousSibling actually refers to our
        // parent's lastChild, but we hide that
        return isFirstChild() ? null : previousSibling;
    }

    /*
     * same as above but returns internal type
     */
    final ChildNode previousSibling() {
        // if we are the firstChild, previousSibling actually refers to our
        // parent's lastChild, but we hide that
        return isFirstChild() ? null : previousSibling;
    }

} // class ChildNode
