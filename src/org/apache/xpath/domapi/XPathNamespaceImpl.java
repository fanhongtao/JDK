/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights 
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
 * 4. The names "Xalan" and "Apache Software Foundation" must
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
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */


package org.apache.xpath.domapi;

import org.w3c.dom.xpath.XPathNamespace;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 *  <meta name="usage" content="experimental"/>
 * 
 * The <code>XPathNamespace</code> interface is returned by 
 * <code>XPathResult</code> interfaces to represent the XPath namespace node 
 * type that DOM lacks. There is no public constructor for this node type. 
 * Attempts to place it into a hierarchy or a NamedNodeMap result in a 
 * <code>DOMException</code> with the code <code>HIERARCHY_REQUEST_ERR</code>
 * . This node is read only, so methods or setting of attributes that would 
 * mutate the node result in a DOMException with the code 
 * <code>NO_MODIFICATION_ALLOWED_ERR</code>.
 * <p>The core specification describes attributes of the <code>Node</code> 
 * interface that are different for different node node types but does not 
 * describe <code>XPATH_NAMESPACE_NODE</code>, so here is a description of 
 * those attributes for this node type. All attributes of <code>Node</code> 
 * not described in this section have a <code>null</code> or 
 * <code>false</code> value.
 * <p><code>ownerDocument</code> matches the <code>ownerDocument</code> of the 
 * <code>ownerElement</code> even if the element is later adopted.
 * <p><code>prefix</code> is the prefix of the namespace represented by the 
 * node.
 * <p><code>nodeName</code> is the same as <code>prefix</code>.
 * <p><code>nodeType</code> is equal to <code>XPATH_NAMESPACE_NODE</code>.
 * <p><code>namespaceURI</code> is the namespace URI of the namespace 
 * represented by the node.
 * <p><code>adoptNode</code>, <code>cloneNode</code>, and 
 * <code>importNode</code> fail on this node type by raising a 
 * <code>DOMException</code> with the code <code>NOT_SUPPORTED_ERR</code>.In 
 * future versions of the XPath specification, the definition of a namespace 
 * node may be changed incomatibly, in which case incompatible changes to 
 * field values may be required to implement versions beyond XPath 1.0.
 * <p>See also the <a href='http://www.w3.org/2002/08/WD-DOM-Level-3-XPath-20020820'>Document Object Model (DOM) Level 3 XPath Specification</a>.
 * 
 * This implementation wraps the DOM attribute node that contained the 
 * namespace declaration.
 */

public class XPathNamespaceImpl implements XPathNamespace {

    // Node that XPathNamespaceImpl wraps
    Node m_attributeNode = null;
    
    /**
     * Constructor for XPathNamespaceImpl.
     */
    public XPathNamespaceImpl(Node node) {
        m_attributeNode = node;
    }

    /**
     * @see org.apache.xalan.dom3.xpath.XPathNamespace#getOwnerElement()
     */
    public Element getOwnerElement() {
        return ((Attr)m_attributeNode).getOwnerElement(); 
    }

    /**
     * @see org.w3c.dom.Node#getNodeName()
     */
    public String getNodeName() {
        return m_attributeNode.getNodeName();
    }

    /**
     * @see org.w3c.dom.Node#getNodeValue()
     */
    public String getNodeValue() throws DOMException {
        return m_attributeNode.getNodeValue();
    }

    /**
     * @see org.w3c.dom.Node#setNodeValue(String)
     */
    public void setNodeValue(String arg0) throws DOMException {
    }

    /**
     * @see org.w3c.dom.Node#getNodeType()
     */
    public short getNodeType() {
        return XPathNamespace.XPATH_NAMESPACE_NODE;
    }

    /**
     * @see org.w3c.dom.Node#getParentNode()
     */
    public Node getParentNode() {
        return m_attributeNode.getParentNode();
    }

    /**
     * @see org.w3c.dom.Node#getChildNodes()
     */
    public NodeList getChildNodes() {
        return m_attributeNode.getChildNodes();
    }

    /**
     * @see org.w3c.dom.Node#getFirstChild()
     */
    public Node getFirstChild() {
        return m_attributeNode.getFirstChild();
    }

    /**
     * @see org.w3c.dom.Node#getLastChild()
     */
    public Node getLastChild() {
        return m_attributeNode.getLastChild();
    }

    /**
     * @see org.w3c.dom.Node#getPreviousSibling()
     */
    public Node getPreviousSibling() {
        return m_attributeNode.getPreviousSibling();
    }

    /**
     * @see org.w3c.dom.Node#getNextSibling()
     */
    public Node getNextSibling() {
        return m_attributeNode.getNextSibling();
    }

    /**
     * @see org.w3c.dom.Node#getAttributes()
     */
    public NamedNodeMap getAttributes() {
        return m_attributeNode.getAttributes();
    }

    /**
     * @see org.w3c.dom.Node#getOwnerDocument()
     */
    public Document getOwnerDocument() {
        return m_attributeNode.getOwnerDocument();
    }

    /**
     * @see org.w3c.dom.Node#insertBefore(Node, Node)
     */
    public Node insertBefore(Node arg0, Node arg1) throws DOMException {
        return null;
    }

    /**
     * @see org.w3c.dom.Node#replaceChild(Node, Node)
     */
    public Node replaceChild(Node arg0, Node arg1) throws DOMException {
        return null;
    }

    /**
     * @see org.w3c.dom.Node#removeChild(Node)
     */
    public Node removeChild(Node arg0) throws DOMException {
        return null;
    }

    /**
     * @see org.w3c.dom.Node#appendChild(Node)
     */
    public Node appendChild(Node arg0) throws DOMException {
        return null;
    }

    /**
     * @see org.w3c.dom.Node#hasChildNodes()
     */
    public boolean hasChildNodes() {
        return false;
    }

    /**
     * @see org.w3c.dom.Node#cloneNode(boolean)
     */
    public Node cloneNode(boolean arg0) {
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR,null);
    }

    /**
     * @see org.w3c.dom.Node#normalize()
     */
    public void normalize() {
        m_attributeNode.normalize();
    }

    /**
     * @see org.w3c.dom.Node#isSupported(String, String)
     */
    public boolean isSupported(String arg0, String arg1) {
        return m_attributeNode.isSupported(arg0, arg1);
    }

    /**
     * @see org.w3c.dom.Node#getNamespaceURI()
     */
    public String getNamespaceURI() {
        return m_attributeNode.getNamespaceURI();
    }

    /**
     * @see org.w3c.dom.Node#getPrefix()
     */
    public String getPrefix() {
        return m_attributeNode.getPrefix();
    }

    /**
     * @see org.w3c.dom.Node#setPrefix(String)
     */
    public void setPrefix(String arg0) throws DOMException {
    }

    /**
     * @see org.w3c.dom.Node#getLocalName()
     */
    public String getLocalName() {
        return m_attributeNode.getLocalName();
    }

    /**
     * @see org.w3c.dom.Node#hasAttributes()
     */
    public boolean hasAttributes() {
        return m_attributeNode.hasAttributes();
    }

}
