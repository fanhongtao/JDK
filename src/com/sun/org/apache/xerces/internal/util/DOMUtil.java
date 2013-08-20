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

package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.dom.AttrImpl;
import com.sun.org.apache.xerces.internal.dom.DocumentImpl;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Some useful utility methods.
 * This class was modified in Xerces2 with a view to abstracting as
 * much as possible away from the representation of the underlying
 * parsed structure (i.e., the DOM).  This was done so that, if Xerces
 * ever adopts an in-memory representation more efficient than the DOM
 * (such as a DTM), we should easily be able to convert our schema
 * parsing to utilize it.
 *
 * @version $ID DOMUtil
 */
public class DOMUtil {

    //
    // Constructors
    //

    /** This class cannot be instantiated. */
    protected DOMUtil() {}

    //
    // Public static methods
    //

    /**
     * Copies the source tree into the specified place in a destination
     * tree. The source node and its children are appended as children
     * of the destination node.
     * <p>
     * <em>Note:</em> This is an iterative implementation.
     */
    public static void copyInto(Node src, Node dest) throws DOMException {

        // get node factory
        Document factory = dest.getOwnerDocument();
        boolean domimpl = factory instanceof DocumentImpl;

        // placement variables
        Node start  = src;
        Node parent = src;
        Node place  = src;

        // traverse source tree
        while (place != null) {

            // copy this node
            Node node = null;
            int  type = place.getNodeType();
            switch (type) {
                case Node.CDATA_SECTION_NODE: {
                    node = factory.createCDATASection(place.getNodeValue());
                    break;
                }
                case Node.COMMENT_NODE: {
                    node = factory.createComment(place.getNodeValue());
                    break;
                }
                case Node.ELEMENT_NODE: {
                    Element element = factory.createElement(place.getNodeName());
                    node = element;
                    NamedNodeMap attrs  = place.getAttributes();
                    int attrCount = attrs.getLength();
                    for (int i = 0; i < attrCount; i++) {
                        Attr attr = (Attr)attrs.item(i);
                        String attrName = attr.getNodeName();
                        String attrValue = attr.getNodeValue();
                        element.setAttribute(attrName, attrValue);
                        if (domimpl && !attr.getSpecified()) {
                            ((AttrImpl)element.getAttributeNode(attrName)).setSpecified(false);
                        }
                    }
                    break;
                }
                case Node.ENTITY_REFERENCE_NODE: {
                    node = factory.createEntityReference(place.getNodeName());
                    break;
                }
                case Node.PROCESSING_INSTRUCTION_NODE: {
                    node = factory.createProcessingInstruction(place.getNodeName(),
                                                               place.getNodeValue());
                    break;
                }
                case Node.TEXT_NODE: {
                    node = factory.createTextNode(place.getNodeValue());
                    break;
                }
                default: {
                    throw new IllegalArgumentException("can't copy node type, "+
                                                       type+" ("+
                                                       node.getNodeName()+')');
                }
            }
            dest.appendChild(node);

            // iterate over children
            if (place.hasChildNodes()) {
                parent = place;
                place  = place.getFirstChild();
                dest   = node;
            }

            // advance
            else {
                place = place.getNextSibling();
                while (place == null && parent != start) {
                    place  = parent.getNextSibling();
                    parent = parent.getParentNode();
                    dest   = dest.getParentNode();
                }
            }

        }

    } // copyInto(Node,Node)

    /** Finds and returns the first child element node. */
    public static Element getFirstChildElement(Node parent) {

        // search for node
        Node child = parent.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                return (Element)child;
            }
            child = child.getNextSibling();
        }

        // not found
        return null;

    } // getFirstChildElement(Node):Element

    /** Finds and returns the first visible child element node. */
    public static Element getFirstVisibleChildElement(Node parent) {

        // search for node
        Node child = parent.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE &&
                !isHidden(child)) {
                return (Element)child;
            }
            child = child.getNextSibling();
        }

        // not found
        return null;

    } // getFirstChildElement(Node):Element

    /** Finds and returns the last child element node. */
    public static Element getLastChildElement(Node parent) {

        // search for node
        Node child = parent.getLastChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                return (Element)child;
            }
            child = child.getPreviousSibling();
        }

        // not found
        return null;

    } // getLastChildElement(Node):Element

    /** Finds and returns the last visible child element node. */
    public static Element getLastVisibleChildElement(Node parent) {

        // search for node
        Node child = parent.getLastChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE &&
                !isHidden(child)) {
                return (Element)child;
            }
            child = child.getPreviousSibling();
        }

        // not found
        return null;

    } // getLastChildElement(Node):Element

    /** Finds and returns the next sibling element node. */
    public static Element getNextSiblingElement(Node node) {

        // search for node
        Node sibling = node.getNextSibling();
        while (sibling != null) {
            if (sibling.getNodeType() == Node.ELEMENT_NODE) {
                return (Element)sibling;
            }
            sibling = sibling.getNextSibling();
        }

        // not found
        return null;

    } // getNextSiblingElement(Node):Element

    // get next visible (un-hidden) node.
    public static Element getNextVisibleSiblingElement(Node node) {

        // search for node
        Node sibling = node.getNextSibling();
        while (sibling != null) {
            if (sibling.getNodeType() == Node.ELEMENT_NODE &&
                !isHidden(sibling)) {
                return (Element)sibling;
            }
            sibling = sibling.getNextSibling();
        }

        // not found
        return null;

    } // getNextSiblingdElement(Node):Element

    // set this Node as being hidden
    public static void setHidden(Node node) {
        if (node instanceof com.sun.org.apache.xerces.internal.impl.xs.opti.NodeImpl)
            ((com.sun.org.apache.xerces.internal.impl.xs.opti.NodeImpl)node).setReadOnly(true, false);
        else if (node instanceof com.sun.org.apache.xerces.internal.dom.NodeImpl)
            ((com.sun.org.apache.xerces.internal.dom.NodeImpl)node).setReadOnly(true, false);
    } // setHidden(node):void

    // set this Node as being visible
    public static void setVisible(Node node) {
        if (node instanceof com.sun.org.apache.xerces.internal.impl.xs.opti.NodeImpl)
            ((com.sun.org.apache.xerces.internal.impl.xs.opti.NodeImpl)node).setReadOnly(false, false);
        else if (node instanceof com.sun.org.apache.xerces.internal.dom.NodeImpl)
            ((com.sun.org.apache.xerces.internal.dom.NodeImpl)node).setReadOnly(false, false);
    } // setVisible(node):void

    // is this node hidden?
    public static boolean isHidden(Node node) {
        if (node instanceof com.sun.org.apache.xerces.internal.impl.xs.opti.NodeImpl)
            return ((com.sun.org.apache.xerces.internal.impl.xs.opti.NodeImpl)node).getReadOnly();
        else if (node instanceof com.sun.org.apache.xerces.internal.dom.NodeImpl)
            return ((com.sun.org.apache.xerces.internal.dom.NodeImpl)node).getReadOnly();
        return false;
    } // isHidden(Node):boolean

    /** Finds and returns the first child node with the given name. */
    public static Element getFirstChildElement(Node parent, String elemName) {

        // search for node
        Node child = parent.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if (child.getNodeName().equals(elemName)) {
                    return (Element)child;
                }
            }
            child = child.getNextSibling();
        }

        // not found
        return null;

    } // getFirstChildElement(Node,String):Element

    /** Finds and returns the last child node with the given name. */
    public static Element getLastChildElement(Node parent, String elemName) {

        // search for node
        Node child = parent.getLastChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                if (child.getNodeName().equals(elemName)) {
                    return (Element)child;
                }
            }
            child = child.getPreviousSibling();
        }

        // not found
        return null;

    } // getLastChildElement(Node,String):Element

    /** Finds and returns the next sibling node with the given name. */
    public static Element getNextSiblingElement(Node node, String elemName) {

        // search for node
        Node sibling = node.getNextSibling();
        while (sibling != null) {
            if (sibling.getNodeType() == Node.ELEMENT_NODE) {
                if (sibling.getNodeName().equals(elemName)) {
                    return (Element)sibling;
                }
            }
            sibling = sibling.getNextSibling();
        }

        // not found
        return null;

    } // getNextSiblingdElement(Node,String):Element

    /** Finds and returns the first child node with the given qualified name. */
    public static Element getFirstChildElementNS(Node parent,
                                                 String uri, String localpart) {

        // search for node
        Node child = parent.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String childURI = child.getNamespaceURI();
                if (childURI != null && childURI.equals(uri) &&
                    child.getLocalName().equals(localpart)) {
                    return (Element)child;
                }
            }
            child = child.getNextSibling();
        }

        // not found
        return null;

    } // getFirstChildElementNS(Node,String,String):Element

    /** Finds and returns the last child node with the given qualified name. */
    public static Element getLastChildElementNS(Node parent,
                                                String uri, String localpart) {

        // search for node
        Node child = parent.getLastChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                String childURI = child.getNamespaceURI();
                if (childURI != null && childURI.equals(uri) &&
                    child.getLocalName().equals(localpart)) {
                    return (Element)child;
                }
            }
            child = child.getPreviousSibling();
        }

        // not found
        return null;

    } // getLastChildElementNS(Node,String,String):Element

    /** Finds and returns the next sibling node with the given qualified name. */
    public static Element getNextSiblingElementNS(Node node,
                                                  String uri, String localpart) {

        // search for node
        Node sibling = node.getNextSibling();
        while (sibling != null) {
            if (sibling.getNodeType() == Node.ELEMENT_NODE) {
                String siblingURI = sibling.getNamespaceURI();
                if (siblingURI != null && siblingURI.equals(uri) &&
                    sibling.getLocalName().equals(localpart)) {
                    return (Element)sibling;
                }
            }
            sibling = sibling.getNextSibling();
        }

        // not found
        return null;

    } // getNextSiblingdElementNS(Node,String,String):Element

    /** Finds and returns the first child node with the given name. */
    public static Element getFirstChildElement(Node parent, String elemNames[]) {

        // search for node
        Node child = parent.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                for (int i = 0; i < elemNames.length; i++) {
                    if (child.getNodeName().equals(elemNames[i])) {
                        return (Element)child;
                    }
                }
            }
            child = child.getNextSibling();
        }

        // not found
        return null;

    } // getFirstChildElement(Node,String[]):Element

    /** Finds and returns the last child node with the given name. */
    public static Element getLastChildElement(Node parent, String elemNames[]) {

        // search for node
        Node child = parent.getLastChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                for (int i = 0; i < elemNames.length; i++) {
                    if (child.getNodeName().equals(elemNames[i])) {
                        return (Element)child;
                    }
                }
            }
            child = child.getPreviousSibling();
        }

        // not found
        return null;

    } // getLastChildElement(Node,String[]):Element

    /** Finds and returns the next sibling node with the given name. */
    public static Element getNextSiblingElement(Node node, String elemNames[]) {

        // search for node
        Node sibling = node.getNextSibling();
        while (sibling != null) {
            if (sibling.getNodeType() == Node.ELEMENT_NODE) {
                for (int i = 0; i < elemNames.length; i++) {
                    if (sibling.getNodeName().equals(elemNames[i])) {
                        return (Element)sibling;
                    }
                }
            }
            sibling = sibling.getNextSibling();
        }

        // not found
        return null;

    } // getNextSiblingdElement(Node,String[]):Element

    /** Finds and returns the first child node with the given qualified name. */
    public static Element getFirstChildElementNS(Node parent,
                                                 String[][] elemNames) {

        // search for node
        Node child = parent.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                for (int i = 0; i < elemNames.length; i++) {
                    String uri = child.getNamespaceURI();
                    if (uri != null && uri.equals(elemNames[i][0]) &&
                        child.getLocalName().equals(elemNames[i][1])) {
                        return (Element)child;
                    }
                }
            }
            child = child.getNextSibling();
        }

        // not found
        return null;

    } // getFirstChildElementNS(Node,String[][]):Element

    /** Finds and returns the last child node with the given qualified name. */
    public static Element getLastChildElementNS(Node parent,
                                                String[][] elemNames) {

        // search for node
        Node child = parent.getLastChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                for (int i = 0; i < elemNames.length; i++) {
                    String uri = child.getNamespaceURI();
                    if (uri != null && uri.equals(elemNames[i][0]) &&
                        child.getLocalName().equals(elemNames[i][1])) {
                        return (Element)child;
                    }
                }
            }
            child = child.getPreviousSibling();
        }

        // not found
        return null;

    } // getLastChildElementNS(Node,String[][]):Element

    /** Finds and returns the next sibling node with the given qualified name. */
    public static Element getNextSiblingElementNS(Node node,
                                                  String[][] elemNames) {

        // search for node
        Node sibling = node.getNextSibling();
        while (sibling != null) {
            if (sibling.getNodeType() == Node.ELEMENT_NODE) {
                for (int i = 0; i < elemNames.length; i++) {
                    String uri = sibling.getNamespaceURI();
                    if (uri != null && uri.equals(elemNames[i][0]) &&
                        sibling.getLocalName().equals(elemNames[i][1])) {
                        return (Element)sibling;
                    }
                }
            }
            sibling = sibling.getNextSibling();
        }

        // not found
        return null;

    } // getNextSiblingdElementNS(Node,String[][]):Element

    /**
     * Finds and returns the first child node with the given name and
     * attribute name, value pair.
     */
    public static Element getFirstChildElement(Node   parent,
                                               String elemName,
                                               String attrName,
                                               String attrValue) {

        // search for node
        Node child = parent.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element)child;
                if (element.getNodeName().equals(elemName) &&
                    element.getAttribute(attrName).equals(attrValue)) {
                    return element;
                }
            }
            child = child.getNextSibling();
        }

        // not found
        return null;

    } // getFirstChildElement(Node,String,String,String):Element

    /**
     * Finds and returns the last child node with the given name and
     * attribute name, value pair.
     */
    public static Element getLastChildElement(Node   parent,
                                               String elemName,
                                               String attrName,
                                               String attrValue) {

        // search for node
        Node child = parent.getLastChild();
        while (child != null) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element)child;
                if (element.getNodeName().equals(elemName) &&
                    element.getAttribute(attrName).equals(attrValue)) {
                    return element;
                }
            }
            child = child.getPreviousSibling();
        }

        // not found
        return null;

    } // getLastChildElement(Node,String,String,String):Element

    /**
     * Finds and returns the next sibling node with the given name and
     * attribute name, value pair. Since only elements have attributes,
     * the node returned will be of type Node.ELEMENT_NODE.
     */
    public static Element getNextSiblingElement(Node   node,
                                                String elemName,
                                                String attrName,
                                                String attrValue) {

        // search for node
        Node sibling = node.getNextSibling();
        while (sibling != null) {
            if (sibling.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element)sibling;
                if (element.getNodeName().equals(elemName) &&
                    element.getAttribute(attrName).equals(attrValue)) {
                    return element;
                }
            }
            sibling = sibling.getNextSibling();
        }

        // not found
        return null;

    } // getNextSiblingElement(Node,String,String,String):Element

    /**
     * Returns the concatenated child text of the specified node.
     * This method only looks at the immediate children of type
     * <code>Node.TEXT_NODE</code> or the children of any child
     * node that is of type <code>Node.CDATA_SECTION_NODE</code>
     * for the concatenation.
     *
     * @param node The node to look at.
     */
    public static String getChildText(Node node) {

        // is there anything to do?
        if (node == null) {
            return null;
        }

        // concatenate children text
        StringBuffer str = new StringBuffer();
        Node child = node.getFirstChild();
        while (child != null) {
            short type = child.getNodeType();
            if (type == Node.TEXT_NODE) {
                str.append(child.getNodeValue());
            }
            else if (type == Node.CDATA_SECTION_NODE) {
                str.append(getChildText(child));
            }
            child = child.getNextSibling();
        }

        // return text value
        return str.toString();

    } // getChildText(Node):String

    // return the name of this element
    public static String getName(Node node) {
        return node.getNodeName();
    } // getLocalName(Element):  String

    /** returns local name of this element if not null, otherwise
        returns the name of the node
    */
    public static String getLocalName(Node node) {
        String name = node.getLocalName();
        return (name!=null)? name:node.getNodeName();
    } // getLocalName(Element):  String

    public static Element getParent(Element elem) {
        Node parent = elem.getParentNode();
        if (parent instanceof Element)
            return (Element)parent;
        return null;
    } // getParent(Element):Element

    // get the Document of which this Node is a part
    public static Document getDocument(Node node) {
        return node.getOwnerDocument();
    } // getDocument(Node):Document

    // return this Document's root node
    public static Element getRoot(Document doc) {
        return doc.getDocumentElement();
     } // getRoot(Document(:  Element

   // some methods for handling attributes:

    // return the right attribute node
    public static Attr getAttr(Element elem, String name) {
        return elem.getAttributeNode(name);
    } // getAttr(Element, String):Attr

    // return the right attribute node
    public static Attr getAttrNS(Element elem, String nsUri,
            String localName) {
        return elem.getAttributeNodeNS(nsUri, localName);
    } // getAttrNS(Element, String):Attr

    // get all the attributes for an Element
    public static Attr[] getAttrs(Element elem) {
        NamedNodeMap attrMap = elem.getAttributes();
        Attr [] attrArray = new Attr[attrMap.getLength()];
        for (int i=0; i<attrMap.getLength(); i++)
            attrArray[i] = (Attr)attrMap.item(i);
        return attrArray;
    } // getAttrs(Element):  Attr[]

    // get attribute's value
    public static String getValue(Attr attribute) {
        return attribute.getValue();
    } // getValue(Attr):String

    // It is noteworthy that, because of the way the DOM specs
    // work, the next two methods return the empty string (not
    // null!) when the attribute with the specified name does not
    // exist on an element.  Beware!

    // return the value of the attribute of the given element
    // with the given name
    public static String getAttrValue(Element elem, String name) {
        return elem.getAttribute(name);
    } // getAttr(Element, String):Attr

    // return the value of the attribute of the given element
    // with the given name
    public static String getAttrValueNS(Element elem, String nsUri,
            String localName) {
        return elem.getAttributeNS(nsUri, localName);
    } // getAttrValueNS(Element, String):Attr

    // return the namespace URI
    public static String getNamespaceURI(Node node) {
        return node.getNamespaceURI();
    }
} // class XUtil
