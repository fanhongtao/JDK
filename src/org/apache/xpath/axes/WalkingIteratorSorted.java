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
 * This class iterates over set of nodes that needs to be sorted.
 */
public class WalkingIteratorSorted extends WalkingIterator
{

//  /** True if the nodes will be found in document order */
//  protected boolean m_inNaturalOrder = false;
  
  /** True if the nodes will be found in document order, and this can 
   * be determined statically. */
  protected boolean m_inNaturalOrderStatic = false;

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
   * Create a WalkingIterator iterator, including creation
   * of step walkers from the opcode list, and call back
   * into the Compiler to create predicate expressions.
   *
   * @param compiler The Compiler which is creating
   * this expression.
   * @param opPos The position of this iterator in the
   * opcode list from the compiler.
   * @param shouldLoadWalkers True if walkers should be
   * loaded, or false if this is a derived iterator and
   * it doesn't wish to load child walkers.
   *
   * @throws javax.xml.transform.TransformerException
   */
  WalkingIteratorSorted(
          Compiler compiler, int opPos, int analysis, boolean shouldLoadWalkers)
            throws javax.xml.transform.TransformerException
  {
    super(compiler, opPos, analysis, shouldLoadWalkers);
  }
  
  /**
   * Returns true if all the nodes in the iteration well be returned in document 
   * order.
   * 
   * @return true as a default.
   */
  public boolean isDocOrdered()
  {
    return m_inNaturalOrderStatic;
  }

    
  /**
   * Tell if the nodeset can be walked in doc order, via static analysis. 
   *
   *
   * @return true if the nodeset can be walked in doc order, without sorting.
   */
  boolean canBeWalkedInNaturalDocOrderStatic()
  {

    if (null != m_firstWalker)
    {
      AxesWalker walker = m_firstWalker;
      int prevAxis = -1;
      boolean prevIsSimpleDownAxis = true;

      for(int i = 0; null != walker; i++)
      {
        int axis = walker.getAxis();
        
        if(walker.isDocOrdered())
        {
          boolean isSimpleDownAxis = ((axis == Axis.CHILD)
                                   || (axis == Axis.SELF)
                                   || (axis == Axis.ROOT));
          // Catching the filtered list here is only OK because
          // FilterExprWalker#isDocOrdered() did the right thing.
          if(isSimpleDownAxis || (axis == -1))
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


//  /**
//   * NEEDSDOC Method canBeWalkedInNaturalDocOrder 
//   *
//   *
//   * NEEDSDOC (canBeWalkedInNaturalDocOrder) @return
//   */
//  boolean canBeWalkedInNaturalDocOrder()
//  {
//
//    if (null != m_firstWalker)
//    {
//      AxesWalker walker = m_firstWalker;
//      int prevAxis = -1;
//      boolean prevIsSimpleDownAxis = true;
//
//      for(int i = 0; null != walker; i++)
//      {
//        int axis = walker.getAxis();
//        
//        if(walker.isDocOrdered())
//        {
//          boolean isSimpleDownAxis = ((axis == Axis.CHILD)
//                                   || (axis == Axis.SELF)
//                                   || (axis == Axis.ROOT));
//          // Catching the filtered list here is only OK because
//          // FilterExprWalker#isDocOrdered() did the right thing.
//          if(isSimpleDownAxis || (axis == -1))
//            walker = walker.getNextWalker();
//          else
//          {
//            boolean isLastWalker = (null == walker.getNextWalker());
//            if(isLastWalker)
//            {
//              if(walker.isDocOrdered() && (axis == Axis.DESCENDANT || 
//                 axis == Axis.DESCENDANTORSELF || axis == Axis.DESCENDANTSFROMROOT
//                 || axis == Axis.DESCENDANTSORSELFFROMROOT) || (axis == Axis.ATTRIBUTE))
//                return true;
//            }
//            return false;
//          }
//        }
//        else
//          return false;
//      }
//      return true;
//    }
//    return false;
//  }
  
  /**
   * This function is used to perform some extra analysis of the iterator.
   * 
   * @param vars List of QNames that correspond to variables.  This list 
   * should be searched backwards for the first qualified name that 
   * corresponds to the variable reference qname.  The position of the 
   * QName in the vector from the start of the vector will be its position 
   * in the stack frame (but variables above the globalsTop value will need 
   * to be offset to the current stack frame).
   */
  public void fixupVariables(java.util.Vector vars, int globalsSize)
  {
    super.fixupVariables(vars, globalsSize);

    int analysis = getAnalysisBits();
    if(WalkerFactory.isNaturalDocOrder(analysis))
    {
    	m_inNaturalOrderStatic = true;
    }
    else
    {
    	m_inNaturalOrderStatic = false;
    	// System.out.println("Setting natural doc order to false: "+
    	//    WalkerFactory.getAnalysisString(analysis));
    }
    
  }

}
