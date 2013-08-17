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
package org.apache.xalan.trace;

import org.w3c.dom.*;

import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xml.utils.QName;

/**
 * <meta name="usage" content="advanced"/>
 * Parent class of events generated for tracing the
 * progress of the XSL processor.
 */
public class TracerEvent implements java.util.EventListener
{

  /**
   * The node in the style tree where the event occurs.
   */
  public final ElemTemplateElement m_styleNode;

  /**
   * The XSLT processor instance.
   */
  public final TransformerImpl m_processor;

  /**
   * The current context node.
   */
  public final Node m_sourceNode;

  /**
   * The current mode.
   */
  public final QName m_mode;

  /**
   * Create an event originating at the given node of the style tree.
   * @param processor The XSLT TransformerFactory.
   * @param sourceNode The current context node.
   * @param mode The current mode.
   * @param m_styleNode node in the style tree reference for the event.
   * Should not be null.  That is not enforced.
   * @param styleNode The stylesheet element that is executing.
   */
  public TracerEvent(TransformerImpl processor, Node sourceNode, QName mode,
                     ElemTemplateElement styleNode)
  {

    this.m_processor = processor;
    this.m_sourceNode = sourceNode;
    this.m_mode = mode;
    this.m_styleNode = styleNode;
  }

  /**
   * Returns a string representation of the node.
   * The string returned for elements will contain the element name
   * and any attributes enclosed in angle brackets.
   * The string returned for attributes will be of form, "name=value."
   *
   * @param n any DOM node. Must not be null.
   *
   * @return a string representation of the given node.
   */
  public static String printNode(Node n)
  {

    String r = n.hashCode() + " ";

    if (n instanceof Element)
    {
      r += "<" + n.getNodeName();

      Node c = n.getFirstChild();

      while (null != c)
      {
        if (c instanceof Attr)
        {
          r += printNode(c) + " ";
        }

        c = c.getNextSibling();
      }

      r += ">";
    }
    else
    {
      if (n instanceof Attr)
      {
        r += n.getNodeName() + "=" + n.getNodeValue();
      }
      else
      {
        r += n.getNodeName();
      }
    }

    return r;
  }

  /**
   * Returns a string representation of the node list.
   * The string will contain the list of nodes inside square braces.
   * Elements will contain the element name
   * and any attributes enclosed in angle brackets.
   * Attributes will be of form, "name=value."
   *
   * @param l any DOM node list. Must not be null.
   *
   * @return a string representation of the given node list.
   */
  public static String printNodeList(NodeList l)
  {

    String r = l.hashCode() + "[";
    int len = l.getLength() - 1;
    int i = 0;

    while (i < len)
    {
      Node n = l.item(i);

      if (null != n)
      {
        r += printNode(n) + ", ";
      }

      ++i;
    }

    if (i == len)
    {
      Node n = l.item(len);

      if (null != n)
      {
        r += printNode(n);
      }
    }

    return r + "]";
  }
}
