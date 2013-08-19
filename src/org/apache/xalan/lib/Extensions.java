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
import org.xml.sax.SAXNotSupportedException;

import java.util.Hashtable;
import java.util.StringTokenizer;

import org.apache.xalan.extensions.ExpressionContext;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;
// Note: we should consider loading EnvironmentCheck at runtime
//  to simplify inter-package dependencies Sep-01 -sc
import org.apache.xalan.xslt.EnvironmentCheck;
import javax.xml.transform.TransformerException;
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

  // Reuse the Document object to reduce memory usage.
  private static Document lDoc = null;
  
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
   * 
   * @param nl1 NodeList for first node-set
   * @param nl2 NodeList for second node-set
   * @return a NodeList containing the nodes in nl1 that are also in nl2
   *
   * Note: The usage of this extension function in the xalan namespace 
   * is deprecated. Please use the same function in the EXSLT sets extension
   * (http://exslt.org/sets).
   */
  public static NodeList intersection(NodeList nl1, NodeList nl2)
  {
    return ExsltSets.intersection(nl1, nl2);
  }

  /**
   * Returns the difference between two node-sets.
   * 
   * @param nl1 NodeList for first node-set
   * @param nl2 NodeList for second node-set
   * @return a NodeList containing the nodes in nl1 that are not in nl2
   * 
   * Note: The usage of this extension function in the xalan namespace 
   * is deprecated. Please use the same function in the EXSLT sets extension
   * (http://exslt.org/sets).
   */
  public static NodeList difference(NodeList nl1, NodeList nl2)
  {
    return ExsltSets.difference(nl1, nl2);
  }

  /**
   * Returns node-set containing distinct string values.
   *
   * @param nl NodeList for node-set
   * @return a NodeList with nodes from nl containing distinct string values.
   * In other words, if more than one node in nl contains the same string value,
   * only include the first such node found.
   *
   * Note: The usage of this extension function in the xalan namespace 
   * is deprecated. Please use the same function in the EXSLT sets extension
   * (http://exslt.org/sets).
   */
  public static NodeList distinct(NodeList nl)
  {
    return ExsltSets.distinct(nl);
  }

  /**
   * Returns true if both node-sets contain the same set of nodes.
   *
   * @param nl1 NodeList for first node-set
   * @param nl2 NodeList for second node-set
   * @return true if nl1 and nl2 contain exactly the same set of nodes.
   */
  public static boolean hasSameNodes(NodeList nl1, NodeList nl2)
  {

    NodeSet ns1 = new NodeSet(nl1);
    NodeSet ns2 = new NodeSet(nl2);

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
   *
   * @param myContext an <code>ExpressionContext</code> passed in by the
   *                  extension mechanism.  This must be an XPathContext.
   * @param xpathExtr The XPath expression to be evaluated.
   * @return the XObject resulting from evaluating the XPath
   *
   * @throws SAXNotSupportedException
   *
   * Note: The usage of this extension function in the xalan namespace 
   * is deprecated. Please use the same function in the EXSLT dynamic extension
   * (http://exslt.org/dynamic).
   */
  public static XObject evaluate(ExpressionContext myContext, String xpathExpr)
         throws SAXNotSupportedException
  {
    return ExsltDynamic.evaluate(myContext, xpathExpr);
  }

  /**
   * Returns a NodeSet containing one text node for each token in the first argument.
   * Delimiters are specified in the second argument.
   * Tokens are determined by a call to <code>StringTokenizer</code>.
   * If the first argument is an empty string or contains only delimiters, the result
   * will be an empty NodeSet.
   *
   * Contributed to XalanJ1 by <a href="mailto:benoit.cerrina@writeme.com">Benoit Cerrina</a>.
   * 
   * @param myContext an <code>ExpressionContext</code> passed in by the
   *                  extension mechanism.  This must be an XPathContext.
   * @param toTokenize The string to be split into text tokens.
   * @param delims The delimiters to use.
   * @return a NodeSet as described above.
   */
  public static NodeList tokenize(String toTokenize, String delims)
  {

    try
    {
      if (lDoc == null)
        lDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
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
   *
   * Contributed to XalanJ1 by <a href="mailto:benoit.cerrina@writeme.com">Benoit Cerrina</a>.
   * 
   * @param myContext an <code>ExpressionContext</code> passed in by the
   *                  extension mechanism.  This must be an XPathContext.
   * @param toTokenize The string to be split into text tokens.
   * @return a NodeSet as described above.
   */
  public static NodeList tokenize(String toTokenize)
  {
    return tokenize(toTokenize, " \t\n\r");
  }

  /**
   * Return a Node of basic debugging information from the 
   * EnvironmentCheck utility about the Java environment.
   *
   * <p>Simply calls the {@link org.apache.xalan.xslt.EnvironmentCheck}
   * utility to grab info about the Java environment and CLASSPATH, 
   * etc., and then returns the resulting Node.  Stylesheets can 
   * then maniuplate this data or simply xsl:copy-of the Node.  Note 
   * that we first attempt to load the more advanced 
   * org.apache.env.Which utility by reflection; only if that fails 
   * to we still use the internal version.  Which is available from 
   * <a href="http://xml.apache.org/commons/">http://xml.apache.org/commons/</a>.</p>
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
      // First use reflection to try to load Which, which is a 
      //  better version of EnvironmentCheck
      resultNode = checkEnvironmentUsingWhich(myContext, factoryDocument);

      if (null != resultNode)
        return resultNode;

      // If reflection failed, fallback to our internal EnvironmentCheck
      EnvironmentCheck envChecker = new EnvironmentCheck();
      Hashtable h = envChecker.getEnvironmentHash();
      resultNode = factoryDocument.createElement("checkEnvironmentExtension");
      envChecker.appendEnvironmentReport(resultNode, factoryDocument, h);
      envChecker = null;
    }
    catch(Exception e)
    {
      throw new org.apache.xml.utils.WrappedRuntimeException(e);
    }

    return resultNode;
  }

  /**
   * Private worker method to attempt to use org.apache.env.Which.
   *
   * @param myContext an <code>ExpressionContext</code> passed in by the
   *                  extension mechanism.  This must be an XPathContext.
   * @param factoryDocument providing createElement services, etc.
   * @return a Node with environment info; null if any error
   */
  private static Node checkEnvironmentUsingWhich(ExpressionContext myContext, 
        Document factoryDocument)
  {
    final String WHICH_CLASSNAME = "org.apache.env.Which";
    final String WHICH_METHODNAME = "which";
    final Class WHICH_METHOD_ARGS[] = { java.util.Hashtable.class,
                                        java.lang.String.class,
                                        java.lang.String.class };
    try
    {
      // Use reflection to try to find xml-commons utility 'Which'
      // Classloader note: if anyone really cares, we could try to 
      //    use the context classloader instead
      Class clazz = Class.forName(WHICH_CLASSNAME);
      if (null == clazz)
        return null;

      // Fully qualify names since this is the only method they're used in
      java.lang.reflect.Method method = clazz.getMethod(WHICH_METHODNAME, WHICH_METHOD_ARGS);
      Hashtable report = new Hashtable();

      // Call the method with our Hashtable, common options, and ignore return value
      Object[] methodArgs = { report, "XmlCommons;Xalan;Xerces;Crimson;Ant", "" };
      Object returnValue = method.invoke(null, methodArgs);

      // Create a parent to hold the report and append hash to it
      Node resultNode = factoryDocument.createElement("checkEnvironmentExtension");
      org.apache.xml.utils.Hashtree2Node.appendHashToNode(report, "whichReport", 
            resultNode, factoryDocument);

      return resultNode;
    }
    catch (Throwable t)
    {
      // Simply return null; no need to report error
      return null;
    }
  }
}
