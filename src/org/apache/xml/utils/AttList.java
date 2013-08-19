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

import org.xml.sax.*;

import org.apache.xpath.DOMHelper;
import org.apache.xpath.DOM2Helper;

/**
 * <meta name="usage" content="internal"/>
 * Wraps a DOM attribute list in a SAX Attributes.
 */
public class AttList implements Attributes
{

  /** List of attribute nodes          */
  NamedNodeMap m_attrs;

  /** Index of last attribute node          */
  int m_lastIndex;

  // ARGHH!!  JAXP Uses Xerces without setting the namespace processing to ON!
  // DOM2Helper m_dh = new DOM2Helper();

  /** Local reference to DOMHelper          */
  DOMHelper m_dh;

//  /**
//   * Constructor AttList
//   *
//   *
//   * @param attrs List of attributes this will contain
//   */
//  public AttList(NamedNodeMap attrs)
//  {
//
//    m_attrs = attrs;
//    m_lastIndex = m_attrs.getLength() - 1;
//    m_dh = new DOM2Helper();
//  }

  /**
   * Constructor AttList
   *
   *
   * @param attrs List of attributes this will contain
   * @param dh DOMHelper 
   */
  public AttList(NamedNodeMap attrs, DOMHelper dh)
  {

    m_attrs = attrs;
    m_lastIndex = m_attrs.getLength() - 1;
    m_dh = dh;
  }

  /**
   * Get the number of attribute nodes in the list 
   *
   *
   * @return number of attribute nodes
   */
  public int getLength()
  {
    return m_attrs.getLength();
  }

  /**
   * Look up an attribute's Namespace URI by index.
   *
   * @param index The attribute index (zero-based).
   * @return The Namespace URI, or the empty string if none
   *         is available, or null if the index is out of
   *         range.
   */
  public String getURI(int index)
  {
    String ns = m_dh.getNamespaceOfNode(((Attr) m_attrs.item(index)));
    if(null == ns)
      ns = "";
    return ns;
  }

  /**
   * Look up an attribute's local name by index.
   *
   * @param index The attribute index (zero-based).
   * @return The local name, or the empty string if Namespace
   *         processing is not being performed, or null
   *         if the index is out of range.
   */
  public String getLocalName(int index)
  {
    return m_dh.getLocalNameOfNode(((Attr) m_attrs.item(index)));
  }

  /**
   * Look up an attribute's qualified name by index.
   *
   *
   * @param index The attribute index (zero-based).
   *
   * @return The attribute's qualified name
   */
  public String getQName(int i)
  {
    return ((Attr) m_attrs.item(i)).getName();
  }

  /**
   * Get the attribute's node type by index
   *
   *
   * @param index The attribute index (zero-based)
   *
   * @return the attribute's node type
   */
  public String getType(int i)
  {
    return "CDATA";  // for the moment
  }

  /**
   * Get the attribute's node value by index
   *
   *
   * @param index The attribute index (zero-based)
   *
   * @return the attribute's node value
   */
  public String getValue(int i)
  {
    return ((Attr) m_attrs.item(i)).getValue();
  }

  /**
   * Get the attribute's node type by name
   *
   *
   * @param name Attribute name
   *
   * @return the attribute's node type
   */
  public String getType(String name)
  {
    return "CDATA";  // for the moment
  }

  /**
   * Look up an attribute's type by Namespace name.
   *
   * @param uri The Namespace URI, or the empty String if the
   *        name has no Namespace URI.
   * @param localName The local name of the attribute.
   * @return The attribute type as a string, or null if the
   *         attribute is not in the list or if Namespace
   *         processing is not being performed.
   */
  public String getType(String uri, String localName)
  {
    return "CDATA";  // for the moment
  }

  /**
   * Look up an attribute's value by name.
   *
   *
   * @param name The attribute node's name
   *
   * @return The attribute node's value
   */
  public String getValue(String name)
  {
    Attr attr = ((Attr) m_attrs.getNamedItem(name));
    return (null != attr) 
          ? attr.getValue() : null;
  }

  /**
   * Look up an attribute's value by Namespace name.
   *
   * @param uri The Namespace URI, or the empty String if the
   *        name has no Namespace URI.
   * @param localName The local name of the attribute.
   * @return The attribute value as a string, or null if the
   *         attribute is not in the list.
   */
  public String getValue(String uri, String localName)
  {
		Node a=m_attrs.getNamedItemNS(uri,localName);
		return (a==null) ? null : a.getNodeValue();
  }

  /**
   * Look up the index of an attribute by Namespace name.
   *
   * @param uri The Namespace URI, or the empty string if
   *        the name has no Namespace URI.
   * @param localPart The attribute's local name.
   * @return The index of the attribute, or -1 if it does not
   *         appear in the list.
   */
  public int getIndex(String uri, String localPart)
  {
    for(int i=m_attrs.getLength()-1;i>=0;--i)
    {
      Node a=m_attrs.item(i);
      String u=a.getNamespaceURI();
      if( (u==null ? uri==null : u.equals(uri))
	  &&
	  a.getLocalName().equals(localPart) )
	return i;
    }
    return -1;
  }

  /**
   * Look up the index of an attribute by raw XML 1.0 name.
   *
   * @param qName The qualified (prefixed) name.
   * @return The index of the attribute, or -1 if it does not
   *         appear in the list.
   */
  public int getIndex(String qName)
  {
    for(int i=m_attrs.getLength()-1;i>=0;--i)
    {
      Node a=m_attrs.item(i);
      if(a.getNodeName().equals(qName) )
	return i;
    }
    return -1;
  }
}

