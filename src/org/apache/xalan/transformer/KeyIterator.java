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
package org.apache.xalan.transformer;

import java.util.Vector;

import javax.xml.transform.TransformerException;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.templates.KeyDeclaration;
import org.apache.xml.dtm.Axis;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.utils.QName;
import org.apache.xpath.XPath;
import org.apache.xpath.XPathContext;
import org.apache.xpath.axes.OneStepIteratorForward;

/**
 * <meta name="usage" content="internal"/>
 * This class implements an optimized iterator for 
 * "key()" patterns, matching each node to the 
 * match attribute in one or more xsl:key declarations.
 */
public class KeyIterator extends OneStepIteratorForward
{

  /** Key name.
   *  @serial           */
  private QName m_name;

  /**
   * Get the key name from a key declaration this iterator will process
   *
   *
   * @return Key name
   */
  public QName getName()
  {
    return m_name;
  }

  /** Vector of Key declarations in the stylesheet.
   *  @serial          */
  private Vector m_keyDeclarations;

  /**
   * Get the key declarations from the stylesheet 
   *
   *
   * @return Vector containing the key declarations from the stylesheet
   */
  public Vector getKeyDeclarations()
  {
    return m_keyDeclarations;
  }

  /**
    * Create a KeyIterator object.
    *
    * @param compiler A reference to the Compiler that contains the op map.
    * @param opPos The position within the op map, which contains the
    * location path expression for this itterator.
    *
    * @throws javax.xml.transform.TransformerException
    */
  KeyIterator(QName name, Vector keyDeclarations)
  {
    super(Axis.ALL);
    m_keyDeclarations = keyDeclarations;
    // m_prefixResolver = nscontext;
    m_name = name;
  }

  /**
   *  Test whether a specified node is visible in the logical view of a
   * TreeWalker or NodeIterator. This function will be called by the
   * implementation of TreeWalker and NodeIterator; it is not intended to
   * be called directly from user code.
   * 
   * @param testnode  The node to check to see if it passes the filter or not.
   *
   * @return  a constant to determine whether the node is accepted,
   *   rejected, or skipped, as defined  above .
   */
  public short acceptNode(int testNode)
  {
    boolean foundKey = false;
    KeyIterator ki = (KeyIterator) m_lpi;
    org.apache.xpath.XPathContext xctxt = ki.getXPathContext();
    Vector keys = ki.getKeyDeclarations();

    QName name = ki.getName();
    try
    {
      // System.out.println("lookupKey: "+lookupKey);
      int nDeclarations = keys.size();

      // Walk through each of the declarations made with xsl:key
      for (int i = 0; i < nDeclarations; i++)
      {
        KeyDeclaration kd = (KeyDeclaration) keys.elementAt(i);

        // Only continue if the name on this key declaration
        // matches the name on the iterator for this walker. 
        if (!kd.getName().equals(name))
          continue;

        foundKey = true;
        // xctxt.setNamespaceContext(ki.getPrefixResolver());

        // See if our node matches the given key declaration according to 
        // the match attribute on xsl:key.
        XPath matchExpr = kd.getMatch();
        double score = matchExpr.getMatchScore(xctxt, testNode);

        if (score == kd.getMatch().MATCH_SCORE_NONE)
          continue;

        return DTMIterator.FILTER_ACCEPT;

      } // end for(int i = 0; i < nDeclarations; i++)
    }
    catch (TransformerException se)
    {

      // TODO: What to do?
    }

    if (!foundKey)
      throw new RuntimeException(
        XSLMessages.createMessage(
          XSLTErrorResources.ER_NO_XSLKEY_DECLARATION,
          new Object[] { name.getLocalName()}));
          
    return DTMIterator.FILTER_REJECT;
  }

}
