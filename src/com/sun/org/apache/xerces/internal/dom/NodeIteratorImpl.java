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
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;


/** DefaultNodeIterator implements a NodeIterator, which iterates a 
 *  DOM tree in the expected depth first way. 
 *
 *  <p>The whatToShow and filter functionality is implemented as expected.
 *  
 *  <p>This class also has method removeNode to enable iterator "fix-up" 
 *  on DOM remove. It is expected that the DOM implementation call removeNode
 *  right before the actual DOM transformation. If not called by the DOM,
 *  the client could call it before doing the removal.
 *
 * @version $Id: NodeIteratorImpl.java,v 1.11 2002/11/06 22:58:28 elena Exp $
 */
public class NodeIteratorImpl implements NodeIterator {
    
    //
    // Data
    //
    
    /** The DocumentImpl which created this iterator, so it can be detached. */
    private DocumentImpl fDocument;
    /** The root. */
    private Node fRoot;
    /** The whatToShow mask. */
    private int fWhatToShow = NodeFilter.SHOW_ALL;
    /** The NodeFilter reference. */
    private NodeFilter fNodeFilter;
    /** If detach is called, the fDetach flag is true, otherwise flase. */
    private boolean fDetach = false;
    
    // 
    // Iterator state - current node and direction.
    //
    // Note: The current node and direction are sufficient to implement
    // the desired behaviour of the current pointer being _between_
    // two nodes. The fCurrentNode is actually the last node returned, 
    // and the
    // direction is whether the pointer is in front or behind this node.
    // (usually akin to whether the node was returned via nextNode()) 
    // (eg fForward = true) or previousNode() (eg fForward = false).
    // Note also, if removing a Node, the fCurrentNode
    // can be placed on a Node which would not pass filters. 
    
    /** The last Node returned. */
    private Node fCurrentNode;
    
    /** The direction of the iterator on the fCurrentNode.
     *  <pre>
     *  nextNode()  ==      fForward = true;
     *  previousNode() ==   fForward = false;
     *  </pre>
     */
    private boolean fForward = true;
    
    /** When TRUE, the children of entites references are returned in the iterator. */
    private boolean fEntityReferenceExpansion;
    
    // 
    // Constructor
    //
    
    /** Public constructor */
    public NodeIteratorImpl( DocumentImpl document,
                             Node root, 
                             int whatToShow, 
                             NodeFilter nodeFilter,
                             boolean entityReferenceExpansion) {
        fDocument = document;
        fRoot = root;
        fCurrentNode = null;
        fWhatToShow = whatToShow;
        fNodeFilter = nodeFilter;
        fEntityReferenceExpansion = entityReferenceExpansion;
    }
    
    public Node getRoot() {
	return fRoot;
    }

    // Implementation Note: Note that the iterator looks at whatToShow
    // and filter values at each call, and therefore one _could_ add
    // setters for these values and alter them while iterating!
    
    /** Return the whatToShow value */
    public int                getWhatToShow() {
        return fWhatToShow;
    }

    /** Return the filter */
    public NodeFilter         getFilter() {
        return fNodeFilter;
    }
    
    /** Return whether children entity references are included in the iterator. */
    public boolean            getExpandEntityReferences() {
        return fEntityReferenceExpansion;
    }
            
    /** Return the next Node in the Iterator. The node is the next node in 
     *  depth-first order which also passes the filter, and whatToShow. 
     *  If there is no next node which passes these criteria, then return null.
     */
    public Node               nextNode() {
        
    	if( fDetach) {
    		throw new DOMException(
    		DOMException.INVALID_STATE_ERR, 
                DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_STATE_ERR", null));
        }
        
        // if root is null there is no next node.
        if (fRoot == null) return null;
        
        Node nextNode = fCurrentNode;
        boolean accepted = false; // the next node has not been accepted.
     
        accepted_loop:
        while (!accepted) {
            
            // if last direction is not forward, repeat node.
            if (!fForward && nextNode!=null) {
                //System.out.println("nextNode():!fForward:"+fCurrentNode.getNodeName());
                nextNode = fCurrentNode;
            } else { 
            // else get the next node via depth-first
                if (!fEntityReferenceExpansion
                    && nextNode != null
                    && nextNode.getNodeType() == Node.ENTITY_REFERENCE_NODE) {
                    nextNode = nextNode(nextNode, false);
                } else {
                    nextNode = nextNode(nextNode, true);
                }
            }
   
            fForward = true; //REVIST: should direction be set forward before null check?
            
            // nothing in the list. return null.
            if (nextNode == null) return null; 
            
            // does node pass the filters and whatToShow?
            accepted = acceptNode(nextNode);
            if (accepted) {
                // if so, then the node is the current node.
                fCurrentNode = nextNode;
                return fCurrentNode;
            } else 
                continue accepted_loop;
            
        } // while (!accepted) {
        
        // no nodes, or no accepted nodes.
        return null;
            
    }
    
    /** Return the previous Node in the Iterator. The node is the next node in 
     *  _backwards_ depth-first order which also passes the filter, and whatToShow. 
     */
    public Node               previousNode() {
        
    	if( fDetach) {
    		throw new DOMException(
    		DOMException.INVALID_STATE_ERR, 
                DOMMessageFormatter.formatMessage(DOMMessageFormatter.DOM_DOMAIN, "INVALID_STATE_ERR", null));
        }
 
        // if the root is null, or the current node is null, return null.
        if (fRoot == null || fCurrentNode == null) return null;
       
        Node previousNode = fCurrentNode;
        boolean accepted = false;
        
        accepted_loop:
        while (!accepted) {
            
            if (fForward && previousNode != null) {
                //repeat last node.
                previousNode = fCurrentNode;
            } else { 
                // get previous node in backwards depth first order.
                previousNode = previousNode(previousNode);
            }
            
            // we are going backwards
            fForward = false;
            
            // if the new previous node is null, we're at head or past the root,
            // so return null. 
            if (previousNode == null) return null;
            
            // check if node passes filters and whatToShow.
            accepted = acceptNode(previousNode);
            if (accepted) {
                // if accepted, update the current node, and return it.
                fCurrentNode = previousNode;
                return fCurrentNode;
            } else 
                continue accepted_loop;
        }
        // there are no nodes?
        return null;
    }
                
    /** The node is accepted if it passes the whatToShow and the filter. */
    boolean acceptNode(Node node) {
                
        if (fNodeFilter == null) {            
            return ( fWhatToShow & (1 << node.getNodeType()-1)) != 0 ;
        } else {
            return ((fWhatToShow & (1 << node.getNodeType()-1)) != 0 ) 
                && fNodeFilter.acceptNode(node) == NodeFilter.FILTER_ACCEPT;
        }
    } 
    
    /** Return node, if matches or any parent if matches. */
    Node matchNodeOrParent(Node node) {
        // Additions and removals in the underlying data structure may occur
        // before any iterations, and in this case the reference_node is null.
        if (fCurrentNode == null) return null;
        
        // check if the removed node is an _ancestor_ of the 
        // reference node
        for (Node n = fCurrentNode; n != fRoot; n = n.getParentNode()) {
            if (node == n) return n;
        }
        return null;
    }
    
    /** The method nextNode(Node, boolean) returns the next node 
     *  from the actual DOM tree.
     * 
     *  The boolean visitChildren determines whether to visit the children.
     *  The result is the nextNode.
     */
    Node nextNode(Node node, boolean visitChildren) {
            
        if (node == null) return fRoot;

        Node result;
        // only check children if we visit children.
        if (visitChildren) {
            //if hasChildren, return 1st child.
            if (node.hasChildNodes()) {
                result = node.getFirstChild();
                return result;
            }
        }
            
        if (node == fRoot) { //if Root has no kids
            return null;
        }

        // if hasSibling, return sibling
        result = node.getNextSibling();
        if (result != null) return result;
        
                
        // return parent's 1st sibling.
        Node parent = node.getParentNode();
        while (parent != null && parent != fRoot) {
            result = parent.getNextSibling();
            if (result != null) {
                return result;
            } else {
                parent = parent.getParentNode();
            }
                            
        } // while (parent != null && parent != fRoot) {
        
        // end of list, return null
        return null;            
    }
    
    /** The method previousNode(Node) returns the previous node 
     *  from the actual DOM tree.
     */
    Node previousNode(Node node) {
        
        Node result;
        
        // if we're at the root, return null.
        if (node == fRoot) return null;
        
        // get sibling
        result = node.getPreviousSibling();
        if (result == null) {
            //if 1st sibling, return parent
            result = node.getParentNode();
            return result;
        }
        
        // if sibling has children, keep getting last child of child.
        if (result.hasChildNodes()
            && !(!fEntityReferenceExpansion
                && result != null
                && result.getNodeType() == Node.ENTITY_REFERENCE_NODE)) 
       
        {
            while (result.hasChildNodes()) {
                result = result.getLastChild();
            }
        }          
            
        return result;
    }
    
    /** Fix-up the iterator on a remove. Called by DOM or otherwise,
     *  before an actual DOM remove.   
     */
    public void removeNode(Node node) {
        
        // Implementation note: Fix-up means setting the current node properly
        // after a remove.
        
        if (node == null) return;
        
        Node deleted = matchNodeOrParent(node);
        
        if (deleted == null) return;
        
        if (fForward) {
            fCurrentNode = previousNode(deleted);
        } else
        // if (!fForward) 
        {
            Node next = nextNode(deleted, false);
            if (next!=null) {
                // normal case: there _are_ nodes following this in the iterator.
                fCurrentNode = next;
            } else {
                // the last node in the iterator is to be removed, 
                // so we set the current node to be the previous one.
                fCurrentNode = previousNode(deleted);
                fForward = true;
            }
                
        }
        
    }
    
    public void               detach() {
        fDetach = true;
        fDocument.removeNodeIterator(this);
    }
    
}
