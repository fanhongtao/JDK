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

import java.util.Vector;

import javax.xml.transform.TransformerException;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathContext;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.XObject;

/**
 * <meta name="usage" content="advanced"/>
 * This class represents a union pattern, which can have multiple individual 
 * StepPattern patterns.
 */
public class UnionPattern extends Expression
{

  /** Array of the contained step patterns to be tested.
   *  @serial  */
  private StepPattern[] m_patterns;
  
  /**
   * No arguments to process, so this does nothing.
   */
  public void fixupVariables(java.util.Vector vars, int globalsSize)
  {
    for (int i = 0; i < m_patterns.length; i++) 
    {
      m_patterns[i].fixupVariables(vars, globalsSize);
    }
  }

  
  /**
   * Tell if this expression or it's subexpressions can traverse outside 
   * the current subtree.
   * 
   * @return true if traversal outside the context node's subtree can occur.
   */
   public boolean canTraverseOutsideSubtree()
   {
     if(null != m_patterns)
     {
      int n = m_patterns.length;
      for (int i = 0; i < n; i++) 
      {
        if(m_patterns[i].canTraverseOutsideSubtree())
          return true;
      }
     }
     return false;
   }

  /**
   * Set the contained step patterns to be tested. 
   *
   *
   * @param patterns the contained step patterns to be tested. 
   */
  public void setPatterns(StepPattern[] patterns)
  {
    m_patterns = patterns;
    if(null != patterns)
    {
    	for(int i = 0; i < patterns.length; i++)
    	{
    		patterns[i].exprSetParent(this);
    	}
    }
    
  }

  /**
   * Get the contained step patterns to be tested. 
   *
   *
   * @return an array of the contained step patterns to be tested. 
   */
  public StepPattern[] getPatterns()
  {
    return m_patterns;
  }

  /**
   * Test a node to see if it matches any of the patterns in the union.
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
  public XObject execute(XPathContext xctxt) throws javax.xml.transform.TransformerException
  {

    XObject bestScore = null;
    int n = m_patterns.length;

    for (int i = 0; i < n; i++)
    {
      XObject score = m_patterns[i].execute(xctxt);

      if (score != NodeTest.SCORE_NONE)
      {
        if (null == bestScore)
          bestScore = score;
        else if (score.num() > bestScore.num())
          bestScore = score;
      }
    }

    if (null == bestScore)
    {
      bestScore = NodeTest.SCORE_NONE;
    }

    return bestScore;
  }
  
  class UnionPathPartOwner implements ExpressionOwner
  {
  	int m_index;
  	
  	UnionPathPartOwner(int index)
  	{
  		m_index = index;
  	}
  	
    /**
     * @see ExpressionOwner#getExpression()
     */
    public Expression getExpression()
    {
      return m_patterns[m_index];
    }


    /**
     * @see ExpressionOwner#setExpression(Expression)
     */
    public void setExpression(Expression exp)
    {
    	exp.exprSetParent(UnionPattern.this);
    	m_patterns[m_index] = (StepPattern)exp;
    }
  }
  
  /**
   * @see XPathVisitable#callVisitors(ExpressionOwner, XPathVisitor)
   */
  public void callVisitors(ExpressionOwner owner, XPathVisitor visitor)
  {
  	visitor.visitUnionPattern(owner, this);
  	if(null != m_patterns)
  	{
  		int n = m_patterns.length;
  		for(int i = 0; i < n; i++)
  		{
  			m_patterns[i].callVisitors(new UnionPathPartOwner(i), visitor);
  		}
  	}
  }
  
  /**
   * @see Expression#deepEquals(Expression)
   */
  public boolean deepEquals(Expression expr)
  {
  	if(!isSameClass(expr))
  		return false;
  		
  	UnionPattern up = (UnionPattern)expr;
  		
  	if(null != m_patterns)
  	{
  		int n = m_patterns.length;
  		if((null == up.m_patterns) || (up.m_patterns.length != n))
  			return false;
  			
  		for(int i = 0; i < n; i++)
  		{
  			if(!m_patterns[i].deepEquals(up.m_patterns[i]))
  				return false;
  		}
  	}
  	else if(up.m_patterns != null)
  		return false;
  		
  	return true;
  	
  }


}
