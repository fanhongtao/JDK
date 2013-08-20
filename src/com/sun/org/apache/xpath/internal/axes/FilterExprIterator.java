/*
 * Copyright 1999-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id: FilterExprIterator.java,v 1.5 2004/02/17 04:32:08 minchau Exp $
 */
package com.sun.org.apache.xpath.internal.axes;

import com.sun.org.apache.xml.internal.dtm.DTM;
import com.sun.org.apache.xpath.internal.Expression;
import com.sun.org.apache.xpath.internal.ExpressionOwner;
import com.sun.org.apache.xpath.internal.XPathVisitor;
import com.sun.org.apache.xpath.internal.objects.XNodeSet;

public class FilterExprIterator extends BasicTestIterator
{
  /** The contained expression. Should be non-null.
   *  @serial   */
  private Expression m_expr;

  /** The result of executing m_expr.  Needs to be deep cloned on clone op.  */
  transient private XNodeSet m_exprObj;

  private boolean m_mustHardReset = false;
  private boolean m_canDetachNodeset = true;

  /**
   * Create a ChildTestIterator object.
   *
   * @param traverser Traverser that tells how the KeyIterator is to be handled.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public FilterExprIterator()
  {
    super(null);
  }
  
  /**
   * Create a ChildTestIterator object.
   *
   * @param traverser Traverser that tells how the KeyIterator is to be handled.
   *
   * @throws javax.xml.transform.TransformerException
   */
  public FilterExprIterator(Expression expr)
  {
    super(null);
    m_expr = expr;
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
 	
  	m_exprObj = FilterExprIteratorSimple.executeFilterExpr(context, 
  	                  m_execContext, getPrefixResolver(), 
  	                  getIsTopLevel(), m_stackFrame, m_expr);
   }


  /**
   * Get the next node via getNextXXX.  Bottlenecked for derived class override.
   * @return The next node on the axis, or DTM.NULL.
   */
  protected int getNextNode()
  {
    if (null != m_exprObj)
    {
      m_lastFetched = m_exprObj.nextNode();
    }
    else
      m_lastFetched = DTM.NULL;

    return m_lastFetched;
  }
  
  /**
   * Detaches the walker from the set which it iterated over, releasing
   * any computational resources and placing the iterator in the INVALID
   * state.
   */
  public void detach()
  {  
  	super.detach();
  	m_exprObj.detach();
  	m_exprObj = null;
  }

  /**
   * This function is used to fixup variables from QNames to stack frame 
   * indexes at stylesheet build time.
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
    m_expr.fixupVariables(vars, globalsSize);
  }

  /**
   * Get the inner contained expression of this filter.
   */
  public Expression getInnerExpression()
  {
    return m_expr;
  }

  /**
   * Set the inner contained expression of this filter.
   */
  public void setInnerExpression(Expression expr)
  {
    expr.exprSetParent(this);
    m_expr = expr;
  }

  /** 
   * Get the analysis bits for this walker, as defined in the WalkerFactory.
   * @return One of WalkerFactory#BIT_DESCENDANT, etc.
   */
  public int getAnalysisBits()
  {
    if (null != m_expr && m_expr instanceof PathComponent)
    {
      return ((PathComponent) m_expr).getAnalysisBits();
    }
    return WalkerFactory.BIT_FILTER;
  }

  /**
   * Returns true if all the nodes in the iteration well be returned in document 
   * order.
   * Warning: This can only be called after setRoot has been called!
   * 
   * @return true as a default.
   */
  public boolean isDocOrdered()
  {
    return m_exprObj.isDocOrdered();
  }

  class filterExprOwner implements ExpressionOwner
  {
    /**
    * @see ExpressionOwner#getExpression()
    */
    public Expression getExpression()
    {
      return m_expr;
    }

    /**
     * @see ExpressionOwner#setExpression(Expression)
     */
    public void setExpression(Expression exp)
    {
      exp.exprSetParent(FilterExprIterator.this);
      m_expr = exp;
    }

  }

  /**
   * This will traverse the heararchy, calling the visitor for 
   * each member.  If the called visitor method returns 
   * false, the subtree should not be called.
   * 
   * @param owner The owner of the visitor, where that path may be 
   *              rewritten if needed.
   * @param visitor The visitor whose appropriate method will be called.
   */
  public void callPredicateVisitors(XPathVisitor visitor)
  {
    m_expr.callVisitors(new filterExprOwner(), visitor);

    super.callPredicateVisitors(visitor);
  }

  /**
   * @see Expression#deepEquals(Expression)
   */
  public boolean deepEquals(Expression expr)
  {
    if (!super.deepEquals(expr))
      return false;

    FilterExprIterator fet = (FilterExprIterator) expr;
    if (!m_expr.deepEquals(fet.m_expr))
      return false;

    return true;
  }

}
