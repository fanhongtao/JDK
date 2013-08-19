/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights 
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
package org.apache.xml.utils;

import org.w3c.dom.*;

import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.res.XSLMessages;

/**
 * <meta name="usage" content="internal"/>
 * To be subclassed by classes that wish to fake being nodes.
 */
public class UnImplNode implements Node, Element, NodeList, Document
{

  /**
   * Constructor UnImplNode
   *
   */
  public UnImplNode(){}

  /**
   * Throw an error.
   *
   * @param msg Message Key for the error
   */
  public void error(String msg)
  {

    System.out.println("DOM ERROR! class: " + this.getClass().getName());

    throw new RuntimeException(XSLMessages.createMessage(msg, null));
  }

  /**
   * Throw an error.
   *
   * @param msg Message Key for the error
   * @param args Array of arguments to be used in the error message
   */
  public void error(String msg, Object[] args)
  {

    System.out.println("DOM ERROR! class: " + this.getClass().getName());

    throw new RuntimeException(XSLMessages.createMessage(msg, args));  //"UnImplNode error: "+msg);
  }

  /**
   * Unimplemented. See org.w3c.dom.Node
   *
   * @param newChild New node to append to the list of this node's children
   *
   * @return null
   *
   * @throws DOMException
   */
  public Node appendChild(Node newChild) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"appendChild not supported!");

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Node
   *
   * @return false
   */
  public boolean hasChildNodes()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"hasChildNodes not supported!");

    return false;
  }

  /**
   * Unimplemented. See org.w3c.dom.Node
   *
   * @return 0
   */
  public short getNodeType()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getNodeType not supported!");

    return 0;
  }

  /**
   * Unimplemented. See org.w3c.dom.Node
   *
   * @return null
   */
  public Node getParentNode()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getParentNode not supported!");

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Node
   *
   * @return null
   */
  public NodeList getChildNodes()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getChildNodes not supported!");

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Node
   *
   * @return null
   */
  public Node getFirstChild()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getFirstChild not supported!");

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Node
   *
   * @return null
   */
  public Node getLastChild()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getLastChild not supported!");

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Node
   *
   * @return null
   */
  public Node getNextSibling()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getNextSibling not supported!");

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.NodeList
   *
   * @return 0
   */
  public int getLength()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getLength not supported!");

    return 0;
  }  // getLength():int

  /**
   * Unimplemented. See org.w3c.dom.NodeList
   *
   * @param index index of a child of this node in its list of children
   *
   * @return null
   */
  public Node item(int index)
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"item not supported!");

    return null;
  }  // item(int):Node

  /**
   * Unimplemented. See org.w3c.dom.Node
   *
   * @return null
   */
  public Document getOwnerDocument()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getOwnerDocument not supported!");

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Node
   *
   * @return null
   */
  public String getTagName()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getTagName not supported!");

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Node
   *
   * @return null
   */
  public String getNodeName()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getNodeName not supported!");

    return null;
  }

  /** Unimplemented. See org.w3c.dom.Node */
  public void normalize()
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"normalize not supported!");
  }

  /**
   * Unimplemented. See org.w3c.dom.Element
   *
   * @param name Name of the element
   *
   * @return null
   */
  public NodeList getElementsByTagName(String name)
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getElementsByTagName not supported!");

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Element
   *
   * @param oldAttr Attribute to be removed from this node's list of attributes
   *
   * @return null
   *
   * @throws DOMException
   */
  public Attr removeAttributeNode(Attr oldAttr) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"removeAttributeNode not supported!");

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Element
   *
   * @param newAttr Attribute node to be added to this node's list of attributes
   *
   * @return null
   *
   * @throws DOMException
   */
  public Attr setAttributeNode(Attr newAttr) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"setAttributeNode not supported!");

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Element
   *
   *
   * @param name Name of an attribute
   *
   * @return false
   */
  public boolean hasAttribute(String name)
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"hasAttribute not supported!");

    return false;
  }

  /**
   * Unimplemented. See org.w3c.dom.Element
   *
   *
   * @param name
   * @param x
   *
   * @return false
   */
  public boolean hasAttributeNS(String name, String x)
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"hasAttributeNS not supported!");

    return false;
  }

  /**
   * Unimplemented. See org.w3c.dom.Element
   *
   *
   * @param name Attribute node name
   *
   * @return null
   */
  public Attr getAttributeNode(String name)
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getAttributeNode not supported!");

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Element
   *
   * @param name Attribute node name to remove from list of attributes
   *
   * @throws DOMException
   */
  public void removeAttribute(String name) throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"removeAttribute not supported!");
  }

  /**
   * Unimplemented. See org.w3c.dom.Element
   *
   * @param name Name of attribute to set
   * @param value Value of attribute
   *
   * @throws DOMException
   */
  public void setAttribute(String name, String value) throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"setAttribute not supported!");
  }

  /**
   * Unimplemented. See org.w3c.dom.Element
   *
   * @param name Name of attribute to get
   *
   * @return null
   */
  public String getAttribute(String name)
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getAttribute not supported!");

    return null;
  }

  /**
   * Unimplemented. Introduced in DOM Level 2.
   *
   * @return false
   */
  public boolean hasAttributes()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"hasAttributes not supported!");

    return false;
  }

  /**
   * Unimplemented. See org.w3c.dom.Element
   *
   * @param namespaceURI Namespace URI of the element
   * @param localName Local part of qualified name of the element
   *
   * @return null
   */
  public NodeList getElementsByTagNameNS(String namespaceURI,
                                         String localName)
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getElementsByTagNameNS not supported!");

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Element
   *
   * @param newAttr Attribute to set
   *
   * @return null
   *
   * @throws DOMException
   */
  public Attr setAttributeNodeNS(Attr newAttr) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"setAttributeNodeNS not supported!");

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Element
   *
   * @param namespaceURI Namespace URI of attribute node to get
   * @param localName Local part of qualified name of attribute node to get
   *
   * @return null
   */
  public Attr getAttributeNodeNS(String namespaceURI, String localName)
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getAttributeNodeNS not supported!");

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Element
   *
   * @param namespaceURI Namespace URI of attribute node to remove
   * @param localName Local part of qualified name of attribute node to remove
   *
   * @throws DOMException
   */
  public void removeAttributeNS(String namespaceURI, String localName)
          throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"removeAttributeNS not supported!");
  }

  /**
   * Unimplemented. See org.w3c.dom.Element
   *
   * @param namespaceURI Namespace URI of attribute node to set
   * @param localName Local part of qualified name of attribute node to set
   * NEEDSDOC @param qualifiedName
   * @param value value of attribute
   *
   * @throws DOMException
   */
  public void setAttributeNS(
          String namespaceURI, String qualifiedName, String value)
            throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"setAttributeNS not supported!");
  }

  /**
   * Unimplemented. See org.w3c.dom.Element
   *
   * @param namespaceURI Namespace URI of attribute node to get
   * @param localName Local part of qualified name of attribute node to get
   *
   * @return null
   */
  public String getAttributeNS(String namespaceURI, String localName)
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getAttributeNS not supported!");

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Node
   *
   * @return null
   */
  public Node getPreviousSibling()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getPreviousSibling not supported!");

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Node
   *
   * @param deep Flag indicating whether to clone deep (clone member variables)
   *
   * @return null
   */
  public Node cloneNode(boolean deep)
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"cloneNode not supported!");

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Node
   *
   * @return null
   *
   * @throws DOMException
   */
  public String getNodeValue() throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getNodeValue not supported!");

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Node
   *
   * @param nodeValue Value to set this node to
   *
   * @throws DOMException
   */
  public void setNodeValue(String nodeValue) throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"setNodeValue not supported!");
  }

  /**
   * Unimplemented. See org.w3c.dom.Node
   *
   *
   * NEEDSDOC @param value
   * @return value Node value
   *
   * @throws DOMException
   */

  // public String getValue ()
  // {      
  //  error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED); //"getValue not supported!");
  //  return null;
  // } 

  /**
   * Unimplemented. See org.w3c.dom.Node
   *
   * @param value Value to set this node to
   *
   * @throws DOMException
   */
  public void setValue(String value) throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"setValue not supported!");
  }

  /**
   *  Returns the name of this attribute.
   *
   * @return the name of this attribute.
   */

  // public String getName()
  // {
  //  return this.getNodeName();
  // }

  /**
   * Unimplemented. See org.w3c.dom.Node
   *
   * @return null
   */
  public Element getOwnerElement()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getOwnerElement not supported!");

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Node
   *
   * @return False
   */
  public boolean getSpecified()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"setValue not supported!");

    return false;
  }

  /**
   * Unimplemented. See org.w3c.dom.Node
   *
   * @return null
   */
  public NamedNodeMap getAttributes()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getAttributes not supported!");

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Node
   *
   * @param newChild New child node to insert
   * @param refChild Insert in front of this child
   *
   * @return null
   *
   * @throws DOMException
   */
  public Node insertBefore(Node newChild, Node refChild) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"insertBefore not supported!");

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Node
   *
   * @param newChild Replace existing child with this one
   * @param oldChild Existing child to be replaced
   *
   * @return null
   *
   * @throws DOMException
   */
  public Node replaceChild(Node newChild, Node oldChild) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"replaceChild not supported!");

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Node
   *
   * @param oldChild Child to be removed
   *
   * @return null
   *
   * @throws DOMException
   */
  public Node removeChild(Node oldChild) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"replaceChild not supported!");

    return null;
  }

  /**
   * Tests whether the DOM implementation implements a specific feature and
   * that feature is supported by this node.
   * @param featureThe name of the feature to test. This is the same name
   *   which can be passed to the method <code>hasFeature</code> on
   *   <code>DOMImplementation</code>.
   * @param versionThis is the version number of the feature to test. In
   *   Level 2, version 1, this is the string "2.0". If the version is not
   *   specified, supporting any version of the feature will cause the
   *   method to return <code>true</code>.
   *
   * NEEDSDOC @param feature
   * NEEDSDOC @param version
   * @return Returns <code>false</code>
   * @since DOM Level 2
   */
  public boolean isSupported(String feature, String version)
  {
    return false;
  }

  /**
   * Unimplemented. See org.w3c.dom.Node
   *
   * @return null
   */
  public String getNamespaceURI()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getNamespaceURI not supported!");

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Node
   *
   * @return null
   */
  public String getPrefix()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getPrefix not supported!");

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Node
   *
   * @param prefix Prefix to set for this node
   *
   * @throws DOMException
   */
  public void setPrefix(String prefix) throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"setPrefix not supported!");
  }

  /**
   * Unimplemented. See org.w3c.dom.Node
   *
   * @return null
   */
  public String getLocalName()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);  //"getLocalName not supported!");

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Document
   *
   * @return null
   */
  public DocumentType getDoctype()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Document
   *
   * @return null
   */
  public DOMImplementation getImplementation()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Document
   *
   * @return null
   */
  public Element getDocumentElement()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Document
   *
   * @param tagName Element tag name
   *
   * @return null
   *
   * @throws DOMException
   */
  public Element createElement(String tagName) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Document
   *
   * @return null
   */
  public DocumentFragment createDocumentFragment()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Document
   *
   * @param data Data for text node
   *
   * @return null
   */
  public Text createTextNode(String data)
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Document
   *
   * @param data Data for comment
   *
   * @return null
   */
  public Comment createComment(String data)
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Document
   *
   * @param data Data for CDATA section
   *
   * @return null
   *
   * @throws DOMException
   */
  public CDATASection createCDATASection(String data) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Document
   *
   * @param target Target for Processing instruction
   * @param data Data for Processing instruction
   *
   * @return null
   *
   * @throws DOMException
   */
  public ProcessingInstruction createProcessingInstruction(
          String target, String data) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Document
   *
   * @param name Attribute name
   *
   * @return null
   *
   * @throws DOMException
   */
  public Attr createAttribute(String name) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Document
   *
   * @param name Entity Reference name
   *
   * @return null
   *
   * @throws DOMException
   */
  public EntityReference createEntityReference(String name)
          throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Document
   *
   * @param importedNodeThe node to import.
   * @param deepIf <code>true</code>, recursively import the subtree under
   *   the specified node; if <code>false</code>, import only the node
   *   itself, as explained above. This has no effect on <code>Attr</code>
   *   , <code>EntityReference</code>, and <code>Notation</code> nodes.
   *
   * NEEDSDOC @param importedNode
   * NEEDSDOC @param deep
   *
   * @return null
   *
   * @throws DOMException
   */
  public Node importNode(Node importedNode, boolean deep) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Document
   *
   * @param namespaceURI Namespace URI for the element
   * @param qualifiedName Qualified name of the element
   *
   * @return null
   *
   * @throws DOMException
   */
  public Element createElementNS(String namespaceURI, String qualifiedName)
          throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Document
   *
   * @param namespaceURI Namespace URI of the attribute
   * @param qualifiedName Qualified name of the attribute
   *
   * @return null
   *
   * @throws DOMException
   */
  public Attr createAttributeNS(String namespaceURI, String qualifiedName)
          throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Unimplemented. See org.w3c.dom.Document
   *
   * @param elementId ID of the element to get
   *
   * @return null
   */
  public Element getElementById(String elementId)
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Set Node data
   *
   *
   * @param data data to set for this node
   *
   * @throws DOMException
   */
  public void setData(String data) throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);
  }

  /**
   * Unimplemented.
   *
   * @param offset Start offset of substring to extract.
   * @param count The length of the substring to extract.
   *
   * @return null
   *
   * @throws DOMException
   */
  public String substringData(int offset, int count) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * Unimplemented.
   *
   * @param arg String data to append
   *
   * @throws DOMException
   */
  public void appendData(String arg) throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);
  }

  /**
   * Unimplemented.
   *
   * @param offset Start offset of substring to insert.
   * @param count The length of the substring to insert.
   * NEEDSDOC @param arg
   *
   * @throws DOMException
   */
  public void insertData(int offset, String arg) throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);
  }

  /**
   * Unimplemented.
   *
   * @param offset Start offset of substring to delete.
   * @param count The length of the substring to delete.
   *
   * @throws DOMException
   */
  public void deleteData(int offset, int count) throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);
  }

  /**
   * Unimplemented.
   *
   * @param offset Start offset of substring to replace.
   * @param count The length of the substring to replace.
   * @param arg substring to replace with
   *
   * @throws DOMException
   */
  public void replaceData(int offset, int count, String arg)
          throws DOMException
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);
  }

  /**
   * Unimplemented.
   *
   * @param offset Offset into text to split
   *
   * @return null, unimplemented
   *
   * @throws DOMException
   */
  public Text splitText(int offset) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * NEEDSDOC Method adoptNode 
   *
   *
   * NEEDSDOC @param source
   *
   * NEEDSDOC (adoptNode) @return
   *
   * @throws DOMException
   */
  public Node adoptNode(Node source) throws DOMException
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * <p>EXPERIMENTAL! Based on the <a
   * href='http://www.w3.org/TR/2001/WD-DOM-Level-3-Core-20010605'>Document
   * Object Model (DOM) Level 3 Core Working Draft of 5 June 2001.</a>.
   * <p>
   * An attribute specifying, as part of the XML declaration, the encoding
   * of this document. This is <code>null</code> when unspecified.
   * @since DOM Level 3
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getEncoding()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * <p>EXPERIMENTAL! Based on the <a
   * href='http://www.w3.org/TR/2001/WD-DOM-Level-3-Core-20010605'>Document
   * Object Model (DOM) Level 3 Core Working Draft of 5 June 2001.</a>.
   * <p>
   * An attribute specifying, as part of the XML declaration, the encoding
   * of this document. This is <code>null</code> when unspecified.
   * @since DOM Level 3
   *
   * NEEDSDOC @param encoding
   */
  public void setEncoding(String encoding)
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);
  }

  /**
   * <p>EXPERIMENTAL! Based on the <a
   * href='http://www.w3.org/TR/2001/WD-DOM-Level-3-Core-20010605'>Document
   * Object Model (DOM) Level 3 Core Working Draft of 5 June 2001.</a>.
   * <p>
   * An attribute specifying, as part of the XML declaration, whether this
   * document is standalone.
   * @since DOM Level 3
   *
   * NEEDSDOC ($objectName$) @return
   */
  public boolean getStandalone()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return false;
  }

  /**
   * <p>EXPERIMENTAL! Based on the <a
   * href='http://www.w3.org/TR/2001/WD-DOM-Level-3-Core-20010605'>Document
   * Object Model (DOM) Level 3 Core Working Draft of 5 June 2001.</a>.
   * <p>
   * An attribute specifying, as part of the XML declaration, whether this
   * document is standalone.
   * @since DOM Level 3
   *
   * NEEDSDOC @param standalone
   */
  public void setStandalone(boolean standalone)
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);
  }

  /**
   * <p>EXPERIMENTAL! Based on the <a
   * href='http://www.w3.org/TR/2001/WD-DOM-Level-3-Core-20010605'>Document
   * Object Model (DOM) Level 3 Core Working Draft of 5 June 2001.</a>.
   * <p>
   * An attribute specifying whether errors checking is enforced or not.
   * When set to <code>false</code>, the implementation is free to not
   * test every possible error case normally defined on DOM operations,
   * and not raise any <code>DOMException</code>. In case of error, the
   * behavior is undefined. This attribute is <code>true</code> by
   * defaults.
   * @since DOM Level 3
   *
   * NEEDSDOC ($objectName$) @return
   */
  public boolean getStrictErrorChecking()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return false;
  }

  /**
   * <p>EXPERIMENTAL! Based on the <a
   * href='http://www.w3.org/TR/2001/WD-DOM-Level-3-Core-20010605'>Document
   * Object Model (DOM) Level 3 Core Working Draft of 5 June 2001.</a>.
   * <p>
   * An attribute specifying whether errors checking is enforced or not.
   * When set to <code>false</code>, the implementation is free to not
   * test every possible error case normally defined on DOM operations,
   * and not raise any <code>DOMException</code>. In case of error, the
   * behavior is undefined. This attribute is <code>true</code> by
   * defaults.
   * @since DOM Level 3
   *
   * NEEDSDOC @param strictErrorChecking
   */
  public void setStrictErrorChecking(boolean strictErrorChecking)
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);
  }

  /**
   * <p>EXPERIMENTAL! Based on the <a
   * href='http://www.w3.org/TR/2001/WD-DOM-Level-3-Core-20010605'>Document
   * Object Model (DOM) Level 3 Core Working Draft of 5 June 2001.</a>.
   * <p>
   * An attribute specifying, as part of the XML declaration, the version
   * number of this document. This is <code>null</code> when unspecified.
   * @since DOM Level 3
   *
   * NEEDSDOC ($objectName$) @return
   */
  public String getVersion()
  {

    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);

    return null;
  }

  /**
   * <p>EXPERIMENTAL! Based on the <a
   * href='http://www.w3.org/TR/2001/WD-DOM-Level-3-Core-20010605'>Document
   * Object Model (DOM) Level 3 Core Working Draft of 5 June 2001.</a>.
   * <p>
   * An attribute specifying, as part of the XML declaration, the version
   * number of this document. This is <code>null</code> when unspecified.
   * @since DOM Level 3
   *
   * NEEDSDOC @param version
   */
  public void setVersion(String version)
  {
    error(XSLTErrorResources.ER_FUNCTION_NOT_SUPPORTED);
  }
}
