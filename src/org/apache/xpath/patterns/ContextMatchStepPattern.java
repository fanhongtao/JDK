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
package org.apache.xpath.patterns;

import org.apache.xpath.XPathContext;
import org.apache.xpath.objects.XObject;

import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMFilter;
import org.apache.xml.dtm.DTMAxisTraverser;
import org.apache.xml.dtm.Axis;
import org.apache.xpath.axes.WalkerFactory; // evil import.

/**
 * Special context node pattern matcher.
 */
public class ContextMatchStepPattern extends StepPattern
{

  /**
   * Construct a ContextMatchStepPattern.
   *
   * @param whatToShow Bit set defined mainly by {@link org.w3c.dom.traversal.NodeFilter}.
   */
  public ContextMatchStepPattern(int axis, int paxis)
  {
    super(DTMFilter.SHOW_ALL, axis, paxis);
  }

  /**
   * Execute this pattern step, including predicates.
   *
   *
   * @param xctxt XPath runtime context.
   *
   * @return {@link org.apache.xpath.patterns.NodeTest#SCORE_NODETEST},
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_NONE},
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_NSWILD},
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_QNAME}, or
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_OTHER}.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject execute(XPathContext xctxt)
          throws javax.xml.transform.TransformerException
  {

    if (xctxt.getIteratorRoot() == xctxt.getCurrentNode())
      return getStaticScore();
    else
      return this.SCORE_NONE;
  }
  
  /**
   * Execute the match pattern step relative to another step.
   *
   *
   * @param xctxt The XPath runtime context.
   * NEEDSDOC @param prevStep
   *
   * @return {@link org.apache.xpath.patterns.NodeTest#SCORE_NODETEST},
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_NONE},
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_NSWILD},
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_QNAME}, or
   *         {@link org.apache.xpath.patterns.NodeTest#SCORE_OTHER}.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public XObject executeRelativePathPattern(
          XPathContext xctxt, StepPattern prevStep)
            throws javax.xml.transform.TransformerException
  {

    XObject score = NodeTest.SCORE_NONE;
    int context = xctxt.getCurrentNode();
    DTM dtm = xctxt.getDTM(context);

    if (null != dtm)
    {
      int predContext = xctxt.getCurrentNode();
      DTMAxisTraverser traverser;
      
      int axis = m_axis;
      
      boolean needToTraverseAttrs = WalkerFactory.isDownwardAxisOfMany(axis);
      boolean iterRootIsAttr = (dtm.getNodeType(xctxt.getIteratorRoot()) 
                                 == DTM.ATTRIBUTE_NODE);

      if((Axis.PRECEDING == axis) && iterRootIsAttr)
      {
        axis = Axis.PRECEDINGANDANCESTOR;
      }
      
      traverser = dtm.getAxisTraverser(axis);

      for (int relative = traverser.first(context); DTM.NULL != relative;
              relative = traverser.next(context, relative))
      {
        try
        {
          xctxt.pushCurrentNode(relative);

          score = execute(xctxt);

          if (score != NodeTest.SCORE_NONE)
          {
	      //score = executePredicates( xctxt, prevStep, SCORE_OTHER, 
	      //       predContext, relative);
	      if (executePredicates(xctxt, dtm, context))
		  return score;
	      
	      score = NodeTest.SCORE_NONE;
          }
          
          if(needToTraverseAttrs && iterRootIsAttr
             && (DTM.ELEMENT_NODE == dtm.getNodeType(relative)))
          {
            int xaxis = Axis.ATTRIBUTE;
            for (int i = 0; i < 2; i++) 
            {            
              DTMAxisTraverser atraverser = dtm.getAxisTraverser(xaxis);
        
              for (int arelative = atraverser.first(relative); 
                      DTM.NULL != arelative;
                      arelative = atraverser.next(relative, arelative))
              {
                try
                {
                  xctxt.pushCurrentNode(arelative);
        
                  score = execute(xctxt);
        
                  if (score != NodeTest.SCORE_NONE)
                  {
		      //score = executePredicates( xctxt, prevStep, SCORE_OTHER, 
		      //       predContext, arelative);
        
                    if (score != NodeTest.SCORE_NONE)
                      return score;
                  }
                }
                finally
                {
                  xctxt.popCurrentNode();
                }
              }
              xaxis = Axis.NAMESPACE;
            }
          }

        }
        finally
        {
          xctxt.popCurrentNode();
        }
      }

    }

    return score;
  }

}
