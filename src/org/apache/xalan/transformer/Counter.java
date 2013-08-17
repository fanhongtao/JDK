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

//import org.w3c.dom.Node;
import org.apache.xml.dtm.DTM;

import org.apache.xml.utils.NodeVector;
import org.apache.xpath.NodeSetDTM;  // for isNodeAfter support
import org.apache.xpath.XPathContext;

import javax.xml.transform.TransformerException;

import org.apache.xpath.NodeSetDTM;
import org.apache.xpath.XPath;
import org.apache.xalan.templates.ElemNumber;

/**
 * <meta name="usage" content="internal"/>
 * A class that does incremental counting for support of xsl:number.
 * This class stores a cache of counted nodes (m_countNodes).
 * It tries to cache the counted nodes in document order...
 * the node count is based on its position in the cache list
 */
public class Counter
{

  /**
   * Set the maximum ammount the m_countNodes list can
   * grow to.
   */
  static final int MAXCOUNTNODES = 500;

  /**
   * The start count from where m_countNodes counts
   * from.  In other words, the count of a given node
   * in the m_countNodes vector is node position +
   * m_countNodesStartCount.
   */
  int m_countNodesStartCount = 0;

  /**
   * A vector of all nodes counted so far.
   */
  NodeSetDTM m_countNodes;

  /**
   * The node from where the counting starts.  This is needed to
   * find a counter if the node being counted is not immediatly
   * found in the m_countNodes vector.
   */
  int m_fromNode = DTM.NULL;

  /**
   * The owning xsl:number element.
   */
  ElemNumber m_numberElem;

  /**
   * Value to store result of last getCount call, for benifit
   * of returning val from CountersTable.getCounterByCounted,
   * who calls getCount.
   */
  int m_countResult;

  /**
   * Construct a counter object.
   *
   * @param numberElem The owning xsl:number element. 
   * @param countNodes A vector of all nodes counted so far.
   *
   * @throws TransformerException
   */
  Counter(ElemNumber numberElem, NodeSetDTM countNodes) throws TransformerException
  {
    m_countNodes = countNodes;
    m_numberElem = numberElem;
  }

  /**
   * Construct a counter object.
   *
   * @param numberElem The owning xsl:number element. 
   *
   * @throws TransformerException
   *
  Counter(ElemNumber numberElem) throws TransformerException
  {
    m_numberElem = numberElem;
  }*/

  /**
   * Try and find a node that was previously counted. If found,
   * return a positive integer that corresponds to the count.
   *
   * @param support The XPath context to use
   * @param node The node to be counted.
   * 
   * @return The count of the node, or -1 if not found.
   */
  int getPreviouslyCounted(XPathContext support, int node)
  {

    int n = m_countNodes.size();

    m_countResult = 0;

    for (int i = n - 1; i >= 0; i--)
    {
      int countedNode = m_countNodes.elementAt(i);

      if (node == countedNode)
      {

        // Since the list is in backwards order, the count is 
        // how many are in the rest of the list.
        m_countResult = i + 1 + m_countNodesStartCount;

        break;
      }
      
      DTM dtm = support.getDTM(countedNode);

      // Try to see if the given node falls after the counted node...
      // if it does, don't keep searching backwards.
      if (dtm.isNodeAfter(countedNode, node))
        break;
    }

    return m_countResult;
  }

  /**
   * Get the last node in the list.
   *
   * @return the last node in the list.
   */
  int getLast()
  {

    int size = m_countNodes.size();

    return (size > 0) ? m_countNodes.elementAt(size - 1) : DTM.NULL;
  }
}
