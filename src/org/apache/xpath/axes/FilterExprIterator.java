package org.apache.xpath.axes;

import java.util.Vector;

import org.apache.xml.dtm.DTM;
import org.apache.xpath.Expression;
import org.apache.xpath.ExpressionOwner;
import org.apache.xpath.XPathVisitor;
import org.apache.xpath.objects.XNodeSet;

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