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
package org.apache.xalan.lib;

import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.traversal.NodeIterator;

import org.apache.xpath.NodeSet;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XBoolean;
import org.apache.xpath.objects.XNumber;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.DOMHelper;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.ref.DTMNodeIterator;
import org.apache.xml.utils.XMLString;

import org.xml.sax.SAXNotSupportedException;

import java.util.Hashtable;
import java.util.StringTokenizer;

import org.apache.xalan.extensions.ExpressionContext;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;
// Note: we should consider loading EnvironmentCheck at runtime
//  to simplify inter-package dependencies Sep-01 -sc
import org.apache.xalan.xslt.EnvironmentCheck;

import javax.xml.parsers.*;

/**
 * <meta name="usage" content="general"/>
 * This class contains many of the Xalan-supplied extensions.
 * It is accessed by specifying a namespace URI as follows:
 * <pre>
 *    xmlns:xalan="http://xml.apache.org/xalan"
 * </pre>
 */
public class Extensions
{

  /**
   * Constructor Extensions
   *
   */
  private Extensions(){}  // Make sure class cannot be instantiated

  /**
   * This method is an extension that implements as a Xalan extension
   * the node-set function also found in xt and saxon.
   * If the argument is a Result Tree Fragment, then <code>nodeset</code>
   * returns a node-set consisting of a single root node as described in
   * section 11.1 of the XSLT 1.0 Recommendation.  If the argument is a
   * node-set, <code>nodeset</code> returns a node-set.  If the argument
   * is a string, number, or boolean, then <code>nodeset</code> returns
   * a node-set consisting of a single root node with a single text node
   * child that is the result of calling the XPath string() function on the
   * passed parameter.  If the argument is anything else, then a node-set
   * is returned consisting of a single root node with a single text node
   * child that is the result of calling the java <code>toString()</code>
   * method on the passed argument.
   * Most of the
   * actual work here is done in <code>MethodResolver</code> and
   * <code>XRTreeFrag</code>.
   * @param myProcessor Context passed by the extension processor
   * @param rtf Argument in the stylesheet to the nodeset extension function
   *
   * NEEDSDOC ($objectName$) @return
   */
  public static NodeSet nodeset(ExpressionContext myProcessor, Object rtf)
  {

    String textNodeValue;

    if (rtf instanceof NodeIterator)
    {
      return new NodeSet((NodeIterator) rtf);
    }
    else
    {
      if (rtf instanceof String)
      {
        textNodeValue = (String) rtf;
      }
      else if (rtf instanceof Boolean)
      {
        textNodeValue = new XBoolean(((Boolean) rtf).booleanValue()).str();
      }
      else if (rtf instanceof Double)
      {
        textNodeValue = new XNumber(((Double) rtf).doubleValue()).str();
      }
      else
      {
        textNodeValue = rtf.toString();
      }

      // This no longer will work right since the DTM.
      // Document myDoc = myProcessor.getContextNode().getOwnerDocument();
      try
      {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document myDoc = db.newDocument();
        
        Text textNode = myDoc.createTextNode(textNodeValue);
        DocumentFragment docFrag = myDoc.createDocumentFragment();
  
        docFrag.appendChild(textNode);
  
        return new NodeSet(docFrag);
      }
      catch(ParserConfigurationException pce)
      {
        throw new org.apache.xml.utils.WrappedRuntimeException(pce);
      }
    }
  }

  /**
   * Returns the intersection of two node-sets.
   * @param n1 NodeIterator for first node-set
   *
   * NEEDSDOC @param ni1
   * @param ni2 NodeIterator for second node-set
   * @return a NodeSet containing the nodes in ni1 that are also
   * in ni2
   *
   * @throws javax.xml.transform.TransformerException
   */
  public static NodeSet intersection(NodeIterator ni1, NodeIterator ni2)
          throws javax.xml.transform.TransformerException
  {

    NodeSet ns1 = new NodeSet(ni1);
    NodeSet ns2 = new NodeSet(ni2);
    NodeSet inter = new NodeSet();

    inter.setShouldCacheNodes(true);

    for (int i = 0; i < ns1.getLength(); i++)
    {
      Node n = ns1.elementAt(i);

      if (ns2.contains(n))
        inter.addElement(n);
    }

    return inter;
  }

  /**
   * Returns the difference between two node-sets.
   * @param n1 NodeIterator for first node-set
   *
   * NEEDSDOC @param ni1
   * @param ni2 NodeIterator for second node-set
   * @return a NodeSet containing the nodes in ni1 that are not
   * in ni2
   *
   * @throws javax.xml.transform.TransformerException
   */
  public static NodeSet difference(NodeIterator ni1, NodeIterator ni2)
          throws javax.xml.transform.TransformerException
  {

    NodeSet ns1 = new NodeSet(ni1);
    NodeSet ns2 = new NodeSet(ni2);

    // NodeSet inter= new NodeSet();
    NodeSet diff = new NodeSet();

    diff.setShouldCacheNodes(true);

    for (int i = 0; i < ns1.getLength(); i++)
    {
      Node n = ns1.elementAt(i);

      if (!ns2.contains(n))
        diff.addElement(n);
    }

    return diff;
  }

  /**
   * Returns node-set containing distinct string values.
   * @param ni NodeIterator for node-set
   * @return a NodeSet with nodes from ni containing distinct string values.
   * In other words, if more than one node in ni contains the same string value,
   * only include the first such node found.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public static NodeSet distinct(ExpressionContext myContext, NodeIterator ni)
          throws javax.xml.transform.TransformerException
  {

    // Set up our resulting NodeSet and the hashtable we use to keep track of duplicate
    // strings.

    NodeSet dist = new NodeSet();
    dist.setShouldCacheNodes(true);

    Hashtable stringTable = new Hashtable();

    Node currNode = ni.nextNode();

    while (currNode != null)
    {
      String key = myContext.toString(currNode);

      if (!stringTable.containsKey(key))
      {
        stringTable.put(key, currNode);
        dist.addElement(currNode);
      }
      currNode = ni.nextNode();
    }

    return dist;
  }

  /**
   * Returns true of both node-sets contain the same set of nodes.
   * @param n1 NodeIterator for first node-set
   *
   * NEEDSDOC @param ni1
   * @param ni2 NodeIterator for second node-set
   * @return true if ni1 and ni2 contain exactly the same set of nodes.
   */
  public static boolean hasSameNodes(NodeIterator ni1, NodeIterator ni2)
  {

    NodeSet ns1 = new NodeSet(ni1);
    NodeSet ns2 = new NodeSet(ni2);

    if (ns1.getLength() != ns2.getLength())
      return false;

    for (int i = 0; i < ns1.getLength(); i++)
    {
      Node n = ns1.elementAt(i);

      if (!ns2.contains(n))
        return false;
    }

    return true;
  }

  /**
   * Returns the result of evaluating the argument as a string containing
   * an XPath expression.  Used where the XPath expression is not known until
   * run-time.  The expression is evaluated as if the run-time value of the
   * argument appeared in place of the evaluate function call at compile time.
   * @param myContext an <code>ExpressionContext</code> passed in by the
   *                  extension mechanism.  This must be an XPathContext.
   * @param xpathExtr The XPath expression to be evaluated.
   * NEEDSDOC @param xpathExpr
   * @return the XObject resulting from evaluating the XPath
   *
   * @throws Exception
   * @throws SAXNotSupportedException
   */
  public static XObject evaluate(
          ExpressionContext myContext, String xpathExpr)
            throws SAXNotSupportedException, Exception
  {

    if (myContext instanceof XPathContext.XPathExpressionContext)
    {
      try
      {
        XPathContext xctxt =
                    ((XPathContext.XPathExpressionContext) myContext).getXPathContext();
        XPath dynamicXPath = new XPath(xpathExpr, xctxt.getSAXLocator(),
                                       xctxt.getNamespaceContext(),
                                       XPath.SELECT);

        return dynamicXPath.execute(xctxt, myContext.getContextNode(),
                                    xctxt.getNamespaceContext());
      }
      catch (Exception e)
      {
        throw e;
      }
    }
    else
      throw new SAXNotSupportedException(XSLMessages.createMessage(XSLTErrorResources.ER_INVALID_CONTEXT_PASSED, new Object[]{myContext })); //"Invalid context passed to evaluate "
                                         //+ myContext);
  }

  /**
   * Returns a NodeSet containing one text node for each token in the first argument.
   * Delimiters are specified in the second argument.
   * Tokens are determined by a call to <code>StringTokenizer</code>.
   * If the first argument is an empty string or contains only delimiters, the result
   * will be an empty NodeSet.
   * Contributed to XalanJ1 by <a href="mailto:benoit.cerrina@writeme.com">Benoit Cerrina</a>.
   * @param myContext an <code>ExpressionContext</code> passed in by the
   *                  extension mechanism.  This must be an XPathContext.
   * @param toTokenize The string to be split into text tokens.
   * @param delims The delimiters to use.
   * @return a NodeSet as described above.
   *
   */
  public static NodeSet tokenize(ExpressionContext myContext,
                                 String toTokenize, String delims)
  {

    Document lDoc;

    // Document lDoc = myContext.getContextNode().getOwnerDocument();
    try
    {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      lDoc = db.newDocument();
    }
    catch(ParserConfigurationException pce)
    {
      throw new org.apache.xml.utils.WrappedRuntimeException(pce);
    }

    StringTokenizer lTokenizer = new StringTokenizer(toTokenize, delims);
    NodeSet resultSet = new NodeSet();

    while (lTokenizer.hasMoreTokens())
    {
      resultSet.addNode(lDoc.createTextNode(lTokenizer.nextToken()));
    }

    return resultSet;
  }

  /**
   * Returns a NodeSet containing one text node for each token in the first argument.
   * Delimiters are whitespace.  That is, the delimiters that are used are tab (&#x09),
   * linefeed (&#x0A), return (&#x0D), and space (&#x20).
   * Tokens are determined by a call to <code>StringTokenizer</code>.
   * If the first argument is an empty string or contains only delimiters, the result
   * will be an empty NodeSet.
   * Contributed to XalanJ1 by <a href="mailto:benoit.cerrina@writeme.com">Benoit Cerrina</a>.
   * @param myContext an <code>ExpressionContext</code> passed in by the
   *                  extension mechanism.  This must be an XPathContext.
   * @param toTokenize The string to be split into text tokens.
   * @return a NodeSet as described above.
   *
   */
  public static NodeSet tokenize(ExpressionContext myContext,
                                 String toTokenize)
  {
    return tokenize(myContext, toTokenize, " \t\n\r");
  }

  /**
   * Return a Node of basic debugging information from the 
   * EnvironmentCheck utility about the Java environment.
   *
   * <p>Simply calls the {@link org.apache.xalan.xslt.EnvironmentCheck}
   * utility to grab info about the Java environment and CLASSPATH, 
   * etc., and then returns the resulting Node.  Stylesheets can 
   * then maniuplate this data or simply xsl:copy-of the Node.</p>
   *
   * <p>We throw a WrappedRuntimeException in the unlikely case 
   * that reading information from the environment throws us an 
   * exception. (Is this really the best thing to do?)</p>
   *
   * @param myContext an <code>ExpressionContext</code> passed in by the
   *                  extension mechanism.  This must be an XPathContext.
   * @return a Node as described above.
   */
  public static Node checkEnvironment(ExpressionContext myContext)
  {

    Document factoryDocument;
    try
    {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      factoryDocument = db.newDocument();
    }
    catch(ParserConfigurationException pce)
    {
      throw new org.apache.xml.utils.WrappedRuntimeException(pce);
    }

    Node resultNode = null;
    try
    {
      resultNode = factoryDocument.createElement("checkEnvironmentExtension");
      EnvironmentCheck envChecker = new EnvironmentCheck();
      Hashtable h = envChecker.getEnvironmentHash();
      envChecker.appendEnvironmentReport(resultNode, factoryDocument, h);
      envChecker = null;
    }
    catch(Exception e)
    {
      throw new org.apache.xml.utils.WrappedRuntimeException(e);
    }

    return resultNode;
  }

}
