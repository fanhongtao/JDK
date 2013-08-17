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
package org.apache.xpath.functions;

import java.util.Hashtable;
import java.util.StringTokenizer;

import org.apache.xpath.res.XPATHErrorResources;

//import org.w3c.dom.Node;
//import org.w3c.dom.traversal.NodeIterator;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;

import java.util.Vector;

import org.apache.xpath.XPathContext;
import org.apache.xpath.DOMHelper;
import org.apache.xpath.XPath;
import org.apache.xpath.NodeSetDTM;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.objects.XNodeSet;
import org.apache.xml.utils.StringVector;

/**
 * <meta name="usage" content="advanced"/>
 * Execute the Id() function.
 */
public class FuncId extends FunctionOneArg
{

  /**
   * Fill in a list with nodes that match a space delimited list if ID 
   * ID references.
   *
   * @param xctxt The runtime XPath context.
   * @param docContext The document where the nodes are being looked for.
   * @param refval A space delimited list of ID references.
   * @param usedrefs List of references for which nodes were found.
   * @param nodeSet Node set where the nodes will be added to.
   * @param mayBeMore true if there is another set of nodes to be looked for.
   *
   * @return The usedrefs value.
   */
  private StringVector getNodesByID(XPathContext xctxt, int docContext,
                                    String refval, StringVector usedrefs,
                                    NodeSetDTM nodeSet, boolean mayBeMore)
  {

    if (null != refval)
    {
      String ref = null;
//      DOMHelper dh = xctxt.getDOMHelper();
      StringTokenizer tokenizer = new StringTokenizer(refval);
      boolean hasMore = tokenizer.hasMoreTokens();
      DTM dtm = xctxt.getDTM(docContext);

      while (hasMore)
      {
        ref = tokenizer.nextToken();
        hasMore = tokenizer.hasMoreTokens();

        if ((null != usedrefs) && usedrefs.contains(ref))
        {
          ref = null;

          continue;
        }

        int node = dtm.getElementById(ref);

        if (DTM.NULL != node)
          nodeSet.addNodeInDocOrder(node, xctxt);

        if ((null != ref) && (hasMore || mayBeMore))
        {
          if (null == usedrefs)
            usedrefs = new StringVector();

          usedrefs.addElement(ref);
        }
      }
    }

    return usedrefs;
  }

  /**
   * Execute the function.  The function must return
   * a valid object.
   * @param xctxt The current execution context.
   * @return A valid XObject.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {

    int context = xctxt.getCurrentNode();
    DTM dtm = xctxt.getDTM(context);
    int docContext = dtm.getDocument();

    if (DTM.NULL == docContext)
      error(xctxt, XPATHErrorResources.ER_CONTEXT_HAS_NO_OWNERDOC, null);

    XObject arg = m_arg0.execute(xctxt);
    int argType = arg.getType();
    XNodeSet nodes = new XNodeSet(xctxt.getDTMManager());
    NodeSetDTM nodeSet = nodes.mutableNodeset();

    if (XObject.CLASS_NODESET == argType)
    {
      DTMIterator ni = arg.iter();
      StringVector usedrefs = null;
      int pos = ni.nextNode();

      while (DTM.NULL != pos)
      {
        DTM ndtm = ni.getDTM(pos);
        String refval = ndtm.getStringValue(pos).toString();

        pos = ni.nextNode();
        usedrefs = getNodesByID(xctxt, docContext, refval, usedrefs, nodeSet,
                                DTM.NULL != pos);
      }
      // ni.detach();
    }
    else if (XObject.CLASS_NULL == argType)
    {
      return nodes;
    }
    else
    {
      String refval = arg.str();

      getNodesByID(xctxt, docContext, refval, null, nodeSet, false);
    }

    return nodes;
  }
}
