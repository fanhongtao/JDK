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
package org.apache.xpath.axes;

import org.apache.xpath.XPathContext;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xpath.compiler.Compiler;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.Axis;

/**
 * <meta name="usage" content="internal"/>
 * NEEDSDOC Class WalkingIteratorSorted <needs-comment/>
 */
public class WalkingIteratorSorted extends WalkingIterator
{

  /** NEEDSDOC Field m_inNaturalOrder          */
  protected boolean m_inNaturalOrder = false;

  /**
   * Create a WalkingIteratorSorted object.
   *
   * @param nscontext The namespace context for this iterator,
   * should be OK if null.
   */
  public WalkingIteratorSorted(PrefixResolver nscontext)
  {
    super(nscontext);
  }

  /**
   * Create a WalkingIteratorSorted iteratorWalkingIteratorSortedWalkingIteratorSorted.
   *
   * @param compiler The Compiler which is creating
   * this expression.
   * @param opPos The position of this iterator in the
   * opcode list from the compiler.
   * NEEDSDOC @param analysis
   * @param shouldLoadWalkers True if walkers should be
   * loaded, or false if this is a derived iterator and
   * it doesn't wish to load child walkers.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public WalkingIteratorSorted(
          Compiler compiler, int opPos, int analysis, boolean shouldLoadWalkers)
            throws javax.xml.transform.TransformerException
  {

    super(compiler, opPos, analysis, shouldLoadWalkers);

    //this.setShouldCacheNodes(true);
  }

  /**
   * NEEDSDOC Method canBeWalkedInNaturalDocOrder 
   *
   *
   * NEEDSDOC (canBeWalkedInNaturalDocOrder) @return
   */
  boolean canBeWalkedInNaturalDocOrder()
  {

    if (null != m_firstWalker)
    {
      AxesWalker walker = m_firstWalker;
      int prevAxis = -1;
      boolean prevIsSimpleDownAxis = true;

      for(int i = 0; null != walker; i++)
      {
        int axis = walker.getAxis();
        boolean isSimpleDownAxis = ((axis == Axis.CHILD)
           || (axis == Axis.SELF)
           || (axis == Axis.ROOT));
        if(walker.isDocOrdered())
        {
          if(isSimpleDownAxis)
            walker = walker.getNextWalker();
          else
          {
            boolean isLastWalker = (null == walker.getNextWalker());
            if(isLastWalker)
            {
              if(walker.isDocOrdered() && (axis == Axis.DESCENDANT || 
                 axis == Axis.DESCENDANTORSELF || axis == Axis.DESCENDANTSFROMROOT
                 || axis == Axis.DESCENDANTSORSELFFROMROOT) || (axis == Axis.ATTRIBUTE))
                return true;
            }
            return false;
          }
        }
        else
          return false;
      }
      return true;
    }
    return false;
  }

  /**
   * Initialize the context values for this expression
   * after it is cloned.
   *
   * @param execContext The XPath runtime context for this
   * transformation.
   *
   * NEEDSDOC @param context
   * NEEDSDOC @param environment
   */
  public void setRoot(int context, Object environment)
  {

    super.setRoot(context, environment);

    m_inNaturalOrder = canBeWalkedInNaturalDocOrder();

    if (!m_inNaturalOrder)
    {
      this.setShouldCacheNodes(true);

      // This should really be done in the super's setRoot, but if I do that 
      // it becomes unhappy in the minitest... possibly something to do with 
      // the keyref iterator.  -sb
      m_cachedNodes.setLast(0);
      m_cachedNodes.reset();
      m_cachedNodes.RemoveAllNoClear();
      setNextPosition(0);
      m_firstWalker.setRoot(context);

      m_lastUsedWalker = m_firstWalker;

      int nextNode = DTM.NULL;
      AxesWalker walker = getLastUsedWalker();
      XPathContext execContext = (XPathContext) environment;

      execContext.pushCurrentNodeAndExpression(context, context);

      try
      {
        do
        {
          while (true)
          {
            if (null == walker)
              break;

            nextNode = walker.getNextNode();

            if (DTM.NULL == nextNode)
            {
              walker = walker.m_prevWalker;
            }
            else
            {
              if (walker.acceptNode(nextNode) != DTMIterator.FILTER_ACCEPT)
              {
                continue;
              }

              if (null == walker.m_nextWalker)
              {
                setLastUsedWalker(walker);

                // return walker.returnNextNode(nextNode);
                break;
              }
              else
              {
                AxesWalker prev = walker;

                walker = walker.m_nextWalker;

                walker.setRoot(nextNode);

                walker.m_prevWalker = prev;

                continue;
              }
            }  // if(null != nextNode)
          }  // while(null != walker)

          if (DTM.NULL != nextNode)
          {
            incrementNextPosition();

            // m_currentContextNode = nextNode;
            m_cachedNodes.addNodeInDocOrder(nextNode, execContext);

            walker = getLastUsedWalker();
          }
        }
        while (DTM.NULL != nextNode);
      }
      finally
      {
        execContext.popCurrentNodeAndExpression();
      }

      // m_prevReturned = nextNode;
      setNextPosition(0);

      m_last = m_cachedNodes.size();
      m_lastFetched = DTM.NULL;
      m_currentContextNode = DTM.NULL;
      m_foundLast = true;
    }
  }

  //  public int nextNode()
  //  {
  //    return super.nextNode();
  //  }

  /**
   * Reset the iterator.
   */
  public void reset()
  {

    if (m_inNaturalOrder)
      super.reset();
    else
    {

      // super.reset();
      // m_foundLast = false;
      m_lastFetched = DTM.NULL;
      m_next = 0;

      // m_last = 0;
      if (null != m_firstWalker)
      {
        m_lastUsedWalker = m_firstWalker;

        m_firstWalker.setRoot(m_context);
      }
    }
  }
}
