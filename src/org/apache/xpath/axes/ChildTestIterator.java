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

import javax.xml.transform.TransformerException;

import org.apache.xpath.compiler.Compiler;
import org.apache.xpath.patterns.NodeTest;
import org.apache.xpath.objects.XObject;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMFilter;
import org.apache.xml.dtm.DTMAxisTraverser;
import org.apache.xml.dtm.Axis;

/**
 * <meta name="usage" content="advanced"/>
 * This class implements an optimized iterator for
 * children patterns that have a node test, and possibly a predicate.
 * @see org.apache.xpath.axes.WalkerFactory#newLocPathIterator
 */
public class ChildTestIterator extends LocPathIterator
{
  /** The traverser to use to navigate over the descendants. */
  transient protected DTMAxisTraverser m_traverser;
  
  /** The extended type ID, not set until setRoot. */
//  protected int m_extendedTypeID;


  /**
   * Create a ChildTestIterator object.
   *
   * @param compiler A reference to the Compiler that contains the op map.
   * @param opPos The position within the op map, which contains the
   * location path expression for this itterator.
   *
   * @throws javax.xml.transform.TransformerException
   */
  ChildTestIterator(Compiler compiler, int opPos, int analysis)
          throws javax.xml.transform.TransformerException
  {

    super(compiler, opPos, analysis, false);

    int firstStepPos = compiler.getFirstChildPos(opPos);
    int whatToShow = compiler.getWhatToShow(firstStepPos);

    if ((0 == (whatToShow
               & (DTMFilter.SHOW_ATTRIBUTE 
               | DTMFilter.SHOW_NAMESPACE 
               | DTMFilter.SHOW_ELEMENT
               | DTMFilter.SHOW_PROCESSING_INSTRUCTION))) 
               || (whatToShow == DTMFilter.SHOW_ALL))
      initNodeTest(whatToShow);
    else
    {
      initNodeTest(whatToShow, compiler.getStepNS(firstStepPos),
                              compiler.getStepLocalName(firstStepPos));
    }
    initPredicateInfo(compiler, firstStepPos);
  }
  
  /**
   * Create a ChildTestIterator object.
   *
   * @param compiler A reference to the Compiler that contains the op map.
   * @param opPos The position within the op map, which contains the
   * location path expression for this itterator.
   *
   * @throws javax.xml.transform.TransformerException
   */
  ChildTestIterator(DTMAxisTraverser traverser)
  {

    super(null);

    m_traverser = traverser;
  }

  
  /**
   *  Get a cloned Iterator that is reset to the beginning
   *  of the query.
   * 
   *  @return A cloned NodeIterator set of the start of the query.
   * 
   *  @throws CloneNotSupportedException
   */
  public DTMIterator cloneWithReset() throws CloneNotSupportedException
  {

    ChildTestIterator clone = (ChildTestIterator) super.cloneWithReset();

    clone.resetProximityPositions();

    return clone;
  }
  
  /**
   * Get the next node via getNextXXX.  Bottlenecked for derived class override.
   * @return The next node on the axis, or DTM.NULL.
   */
  protected int getNextNode()
  {                     
    if(true /* 0 == m_extendedTypeID */)
    {
      m_lastFetched = (DTM.NULL == m_lastFetched)
                   ? m_traverser.first(m_context)
                   : m_traverser.next(m_context, m_lastFetched);
    }
//    else
//    {
//      m_lastFetched = (DTM.NULL == m_lastFetched)
//                   ? m_traverser.first(m_context, m_extendedTypeID)
//                   : m_traverser.next(m_context, m_lastFetched, 
//                                      m_extendedTypeID);
//    }

    return m_lastFetched;
  }

  /**
   *  Returns the next node in the set and advances the position of the
   * iterator in the set. After a NodeIterator is created, the first call
   * to nextNode() returns the first node in the set.
   *
   * @return  The next <code>Node</code> in the set being iterated over, or
   *   <code>null</code> if there are no more members in that set.
   */
  public int nextNode()
  {

    // If the cache is on, and the node has already been found, then 
    // just return from the list.
    // If the cache is on, and the node has already been found, then 
    // just return from the list.
    if ((null != m_cachedNodes)
            && (m_next < m_cachedNodes.size()))
    {
      int next = m_cachedNodes.elementAt(m_next);
    
      incrementNextPosition();
      m_currentContextNode = next;

      return next;
    }

    if (m_foundLast)
    {
      m_lastFetched = DTM.NULL;
      return DTM.NULL;
    }
      
    if(DTM.NULL == m_lastFetched)
    {
      resetProximityPositions();
    }

    int next;
    
    org.apache.xpath.VariableStack vars;
    int savedStart;
    if (-1 != m_stackFrame)
    {
      vars = m_execContext.getVarStack();

      // These three statements need to be combined into one operation.
      savedStart = vars.getStackFrame();

      vars.setStackFrame(m_stackFrame);
    }
    else
    {
      // Yuck.  Just to shut up the compiler!
      vars = null;
      savedStart = 0;
    }
    
    try
    {
      do
      {
        next = getNextNode();
  
        if (DTM.NULL != next)
        {
          if(DTMIterator.FILTER_ACCEPT == acceptNode(next))
            break;
          else
            continue;
        }
        else
          break;
      }
      while (next != DTM.NULL);
  
      if (DTM.NULL != next)
      {
        if (null != m_cachedNodes)
          m_cachedNodes.addElement(m_lastFetched);
  
        m_next++;
  
        return next;
      }
      else
      {
        m_foundLast = true;
  
        return DTM.NULL;
      }
    }
    finally
    {
      if (-1 != m_stackFrame)
      {
        // These two statements need to be combined into one operation.
        vars.setStackFrame(savedStart);
      }
    }
  }
  
  /**
   * Initialize the context values for this expression
   * after it is cloned.
   *
   * @param execContext The XPath runtime context for this
   * transformation.
   */
  public void setRoot(int context, Object environment)
  {
    super.setRoot(context, environment);
    m_traverser = m_cdtm.getAxisTraverser(Axis.CHILD);
    
//    String localName = getLocalName();
//    String namespace = getNamespace();
//    int what = m_whatToShow;
//    // System.out.println("what: ");
//    // NodeTest.debugWhatToShow(what);
//    if(DTMFilter.SHOW_ALL == what ||
//       ((DTMFilter.SHOW_ELEMENT & what) == 0)
//       || localName == NodeTest.WILD
//       || namespace == NodeTest.WILD)
//    {
//      m_extendedTypeID = 0;
//    }
//    else
//    {
//      int type = getNodeTypeTest(what);
//      m_extendedTypeID = m_cdtm.getExpandedTypeID(namespace, localName, type);
//    }
    
  }
  
  /**
   * Returns the axis being iterated, if it is known.
   * 
   * @return Axis.CHILD, etc., or -1 if the axis is not known or is of multiple 
   * types.
   */
  public int getAxis()
  {
    return org.apache.xml.dtm.Axis.CHILD;
  }


}
