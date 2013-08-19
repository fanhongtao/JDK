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
 * This software consists of voluntary contributions made by Ovidiu
 * Predescu <ovidiu@cup.hp.com> on behalf of the Apache Software
 * Foundation and was originally developed at Hewlett Packard Company.
 * For more information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.xalan.lib;

import org.apache.xml.dtm.ref.DTMNodeProxy;
import org.apache.xalan.extensions.ExpressionContext;

import javax.xml.transform.SourceLocator;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * <code>NodeInfo</code> defines a set of XSLT extension functions to be
 * used from stylesheets.
 *
 * @author <a href="mailto:ovidiu@cup.hp.com">Ovidiu Predescu</a>
 * @since May 24, 2001
 */
public class NodeInfo
{
  /**
   * <code>systemId</code> returns the system id of the current
   * context node.
   *
   * @param context an <code>ExpressionContext</code> value
   * @return a <code>String</code> value
   */
  public static String systemId(ExpressionContext context)
  {
    Node contextNode = context.getContextNode();
    int nodeHandler = ((DTMNodeProxy)contextNode).getDTMNodeNumber();
    SourceLocator locator = ((DTMNodeProxy)contextNode).getDTM()
      .getSourceLocatorFor(nodeHandler);

    if (locator != null)
      return locator.getSystemId();
    else
      return null;
  }

  /**
   * <code>systemId</code> returns the system id of the node passed as
   * argument. If a node set is passed as argument, the system id of
   * the first node in the set is returned.
   *
   * @param context an <code>ExpressionContext</code> value
   * @param nodeList a <code>NodeList</code> value
   * @return a <code>String</code> value
   */
  public static String systemId(NodeList nodeList)
  {
    if (nodeList == null || nodeList.getLength() == 0)
      return null;
    
    Node node = nodeList.item(0);
    int nodeHandler = ((DTMNodeProxy)node).getDTMNodeNumber();
    SourceLocator locator = ((DTMNodeProxy)node).getDTM()
      .getSourceLocatorFor(nodeHandler);

    if (locator != null)
      return locator.getSystemId();
    else
      return null;
  }

  /**
   * <code>publicId</code> returns the public identifier of the current
   * context node.
   * 
   * Xalan does not currently record this value, and will return null.
   *
   * @param context an <code>ExpressionContext</code> value
   * @return a <code>String</code> value
   */
  public static String publicId(ExpressionContext context)
  {
    Node contextNode = context.getContextNode();
    int nodeHandler = ((DTMNodeProxy)contextNode).getDTMNodeNumber();
    SourceLocator locator = ((DTMNodeProxy)contextNode).getDTM()
      .getSourceLocatorFor(nodeHandler);

    if (locator != null)
      return locator.getPublicId();
    else
      return null;
  }

  /**
   * <code>publicId</code> returns the public identifier of the node passed as
   * argument. If a node set is passed as argument, the public identifier of
   * the first node in the set is returned.
   * 
   * Xalan does not currently record this value, and will return null.
   *
   * @param nodeList a <code>NodeList</code> value
   * @return a <code>String</code> value
   */
  public static String publicId(NodeList nodeList)
  {
    if (nodeList == null || nodeList.getLength() == 0)
      return null;
    
    Node node = nodeList.item(0);
    int nodeHandler = ((DTMNodeProxy)node).getDTMNodeNumber();
    SourceLocator locator = ((DTMNodeProxy)node).getDTM()
      .getSourceLocatorFor(nodeHandler);

    if (locator != null)
      return locator.getPublicId();
    else
      return null;
  }

  /**
   * <code>lineNumber</code> returns the line number of the current
   * context node.
   * 
   * NOTE: Xalan does not normally record location information for each node. 
   * To obtain it, you must set the custom TrAX attribute 
   * "http://xml.apache.org/xalan/features/source_location"
   * true in the TransformerFactory before generating the Transformer and executing
   * the stylesheet. Storage cost per node will be noticably increased in this mode.
   *
   * @param context an <code>ExpressionContext</code> value
   * @return an <code>int</code> value. This may be -1 to indicate that the
   * line number is not known.
   */
  public static int lineNumber(ExpressionContext context)
  {
    Node contextNode = context.getContextNode();
    int nodeHandler = ((DTMNodeProxy)contextNode).getDTMNodeNumber();
    SourceLocator locator = ((DTMNodeProxy)contextNode).getDTM()
      .getSourceLocatorFor(nodeHandler);

    if (locator != null)
      return locator.getLineNumber();
    else
      return -1;
  }

  /**
   * <code>lineNumber</code> returns the line number of the node
   * passed as argument. If a node set is passed as argument, the line
   * number of the first node in the set is returned.
   *
   * NOTE: Xalan does not normally record location information for each node. 
   * To obtain it, you must set the custom TrAX attribute 
   * "http://xml.apache.org/xalan/features/source_location"
   * true in the TransformerFactory before generating the Transformer and executing
   * the stylesheet. Storage cost per node will be noticably increased in this mode.
   *
   * @param nodeList a <code>NodeList</code> value
   * @return an <code>int</code> value. This may be -1 to indicate that the
   * line number is not known.
   */
  public static int lineNumber(NodeList nodeList)
  {
    if (nodeList == null || nodeList.getLength() == 0)
      return -1;
    
    Node node = nodeList.item(0);
    int nodeHandler = ((DTMNodeProxy)node).getDTMNodeNumber();
    SourceLocator locator = ((DTMNodeProxy)node).getDTM()
      .getSourceLocatorFor(nodeHandler);

    if (locator != null)
      return locator.getLineNumber();
    else
      return -1;
  }

  /**
   * <code>columnNumber</code> returns the column number of the
   * current context node.
   *
   * NOTE: Xalan does not normally record location information for each node. 
   * To obtain it, you must set the custom TrAX attribute 
   * "http://xml.apache.org/xalan/features/source_location"
   * true in the TransformerFactory before generating the Transformer and executing
   * the stylesheet. Storage cost per node will be noticably increased in this mode.
   *
   * @param context an <code>ExpressionContext</code> value
   * @return an <code>int</code> value. This may be -1 to indicate that the
   * column number is not known.
   */
  public static int columnNumber(ExpressionContext context)
  {
    Node contextNode = context.getContextNode();
    int nodeHandler = ((DTMNodeProxy)contextNode).getDTMNodeNumber();
    SourceLocator locator = ((DTMNodeProxy)contextNode).getDTM()
      .getSourceLocatorFor(nodeHandler);

    if (locator != null)
      return locator.getColumnNumber();
    else
      return -1;
  }

  /**
   * <code>columnNumber</code> returns the column number of the node
   * passed as argument. If a node set is passed as argument, the line
   * number of the first node in the set is returned.
   *
   * NOTE: Xalan does not normally record location information for each node. 
   * To obtain it, you must set the custom TrAX attribute 
   * "http://xml.apache.org/xalan/features/source_location"
   * true in the TransformerFactory before generating the Transformer and executing
   * the stylesheet. Storage cost per node will be noticably increased in this mode.
   *
   * @param nodeList a <code>NodeList</code> value
   * @return an <code>int</code> value. This may be -1 to indicate that the
   * column number is not known.
   */
  public static int columnNumber(NodeList nodeList)
  {
    if (nodeList == null || nodeList.getLength() == 0)
      return -1;
    
    Node node = nodeList.item(0);
    int nodeHandler = ((DTMNodeProxy)node).getDTMNodeNumber();
    SourceLocator locator = ((DTMNodeProxy)node).getDTM()
      .getSourceLocatorFor(nodeHandler);

    if (locator != null)
      return locator.getColumnNumber();
    else
      return -1;
  }
}
