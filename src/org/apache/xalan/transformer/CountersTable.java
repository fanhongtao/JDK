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

import java.util.Hashtable;
import java.util.Vector;

//import org.w3c.dom.Node;
import org.apache.xml.dtm.DTM;

import javax.xml.transform.TransformerException;

import org.apache.xpath.XPathContext;
import org.apache.xpath.XPath;
import org.apache.xpath.NodeSetDTM;
import org.apache.xalan.templates.ElemNumber;

/**
 * <meta name="usage" content="internal"/>
 * This is a table of counters, keyed by ElemNumber objects, each
 * of which has a list of Counter objects.  This really isn't a true
 * table, it is more like a list of lists (there must be a technical
 * term for that...).
 */
public class CountersTable extends Hashtable
{

  /**
   * Construct a CountersTable.
   */
  public CountersTable(){}

  /**
   * Get the list of counters that corresponds to
   * the given ElemNumber object.
   *
   * @param numberElem the given xsl:number element.
   *
   * @return the list of counters that corresponds to
   * the given ElemNumber object.
   */
  Vector getCounters(ElemNumber numberElem)
  {

    Vector counters = (Vector) this.get(numberElem);

    return (null == counters) ? putElemNumber(numberElem) : counters;
  }

  /**
   * Put a counter into the table and create an empty
   * vector as it's value.
   *
   * @param numberElem the given xsl:number element.
   *
   * @return an empty vector to be used to store counts
   * for this number element.
   */
  Vector putElemNumber(ElemNumber numberElem)
  {

    Vector counters = new Vector();

    this.put(numberElem, counters);

    return counters;
  }

  /**
   * Place to collect new counters.
   */
  transient private NodeSetDTM m_newFound;

  /**
   * Add a list of counted nodes that were built in backwards document
   * order, or a list of counted nodes that are in forwards document
   * order.
   *
   * @param flist Vector of nodes built in forwards document order
   * @param blist Vector of nodes built in backwards document order
   */
  void appendBtoFList(NodeSetDTM flist, NodeSetDTM blist)
  {

    int n = blist.size();

    for (int i = (n - 1); i >= 0; i--)
    {
      flist.addElement(blist.item(i));
    }
  }

  // For diagnostics

  /** Number of counters created so far          */
  transient int m_countersMade = 0;

  /**
   * Count forward until the given node is found, or until
   * we have looked to the given amount.
   *
   * @param support The XPath context to use  
   * @param numberElem The given xsl:number element.
   * @param node The node to count.
   * 
   * @return The node count, or 0 if not found.
   *
   * @throws TransformerException
   */
  public int countNode(XPathContext support, ElemNumber numberElem, int node)
          throws TransformerException
  {

    int count = 0;
    Vector counters = getCounters(numberElem);
    int nCounters = counters.size();

    // XPath countMatchPattern = numberElem.getCountMatchPattern(support, node);
    // XPath fromMatchPattern = numberElem.m_fromMatchPattern;
    int target = numberElem.getTargetNode(support, node);

    if (DTM.NULL != target)
    {
      for (int i = 0; i < nCounters; i++)
      {
        Counter counter = (Counter) counters.elementAt(i);

        count = counter.getPreviouslyCounted(support, target);

        if (count > 0)
          return count;
      }

      // In the loop below, we collect the nodes in backwards doc order, so 
      // we don't have to do inserts, but then we store the nodes in forwards 
      // document order, so we don't have to insert nodes into that list, 
      // so that's what the appendBtoFList stuff is all about.  In cases 
      // of forward counting by one, this will mean a single node copy from 
      // the backwards list (m_newFound) to the forwards list (counter.m_countNodes).
      count = 0;
      if (m_newFound == null)
        m_newFound = new NodeSetDTM(support.getDTMManager());

      for (; DTM.NULL != target;
              target = numberElem.getPreviousNode(support, target))
      {

        // First time in, we should not have to check for previous counts, 
        // since the original target node was already checked in the 
        // block above.
        if (0 != count)
        {
          for (int i = 0; i < nCounters; i++)
          {
            Counter counter = (Counter) counters.elementAt(i);
            int cacheLen = counter.m_countNodes.size();

            if ((cacheLen > 0)
                    && (counter.m_countNodes.elementAt(cacheLen
                                                      - 1) == target))
            {
              count += (cacheLen + counter.m_countNodesStartCount);

              if (cacheLen > 0)
                appendBtoFList(counter.m_countNodes, m_newFound);

              m_newFound.removeAllElements();

              return count;
            }
          }
        }

        m_newFound.addElement(target);

        count++;
      }

      // If we got to this point, then we didn't find a counter, so make 
      // one and add it to the list.
      Counter counter = new Counter(numberElem, new NodeSetDTM(support.getDTMManager()));

      m_countersMade++;  // for diagnostics

      appendBtoFList(counter.m_countNodes, m_newFound);
      m_newFound.removeAllElements();
      counters.addElement(counter);
    }

    return count;
  }
}
