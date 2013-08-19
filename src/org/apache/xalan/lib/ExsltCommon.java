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
import org.apache.xpath.objects.XRTreeFrag;

import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.DOMHelper;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.ref.DTMNodeIterator;
import org.apache.xml.utils.XMLString;

import org.xml.sax.SAXNotSupportedException;

import java.util.Hashtable;
import java.util.StringTokenizer;

import org.apache.xalan.extensions.ExpressionContext;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.xslt.EnvironmentCheck;

import javax.xml.parsers.*;

/**
 * <meta name="usage" content="general"/>
 * This class contains EXSLT common extension functions.
 * It is accessed by specifying a namespace URI as follows:
 * <pre>
 *    xmlns:exslt="http://exslt.org/common"
 * </pre>
 * 
 * The documentation for each function has been copied from the relevant
 * EXSLT Implementer page.
 * 
 * @see <a href="http://www.exslt.org/">EXSLT</a>
 */
public class ExsltCommon
{
  /**
   * The exsl:object-type function returns a string giving the type of the object passed 
   * as the argument. The possible object types are: 'string', 'number', 'boolean', 
   * 'node-set', 'RTF', or 'external'. 
   * 
   * Most XSLT object types can be coerced to each other without error. However, there are 
   * certain coercions that raise errors, most importantly treating anything other than a 
   * node set as a node set. Authors of utilities such as named templates or user-defined 
   * extension functions may wish to give some flexibility in the parameter and argument values 
   * that are accepted by the utility; the exsl:object-type function enables them to do so.
   * 
   * The Xalan extensions MethodResolver converts 'object-type' to 'objectType'.
   * 
   * @param obj The object to be typed.
   * @return objectType 'string', 'number', 'boolean', 'node-set', 'RTF', or 'external'.
   * 
   * @see <a href="http://www.exslt.org/">EXSLT</a>
   */
  public static String objectType (Object obj)
  {
    if (obj instanceof String)
      return "string";
    else if (obj instanceof Boolean)
      return "boolean";
    else if (obj instanceof Number)
      return "number";
    else if (obj instanceof DTMNodeIterator)
    {
      DTMIterator dtmI = ((DTMNodeIterator)obj).getDTMIterator();
      if (dtmI instanceof org.apache.xpath.axes.RTFIterator)
      	return "RTF";
      else
        return "node-set";
    }
    else
      return "unknown";
  }
    
  /**
   * The exsl:node-set function converts a result tree fragment (which is what you get 
   * when you use the content of xsl:variable rather than its select attribute to give 
   * a variable value) into a node set. This enables you to process the XML that you create 
   * within a variable, and therefore do multi-step processing. 
   * 
   * You can also use this function to turn a string into a text node, which is helpful 
   * if you want to pass a string to a function that only accepts a node set.
   * 
   * The Xalan extensions MethodResolver converts 'node-set' to 'nodeSet'.
   * 
   * @param myProcesser is passed in by the Xalan extension processor
   * @param rtf The result tree fragment to be converted to a node-set.
   * 
   * @returns node-set with the contents of the result tree fragment.
   * 
   * Note: Already implemented in the xalan namespace as nodeset.
   * 
   * @see <a href="http://www.exslt.org/">EXSLT</a>
   */
  public static NodeSet nodeSet(ExpressionContext myProcessor, Object rtf)
  {
    return Extensions.nodeset(myProcessor, rtf);
  }
 
}