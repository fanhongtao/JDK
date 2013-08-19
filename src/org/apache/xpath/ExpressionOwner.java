package org.apache.xpath;

/**
 * Classes that implement this interface own an expression, which 
 * can be rewritten.
 */
public interface ExpressionOwner
{
  /**
   * Get the raw Expression object that this class wraps.
   *
   * @return the raw Expression object, which should not normally be null.
   */
  public Expression getExpression();

  /**
   * Set the raw expression object for this object.
   *
   * @param exp the raw Expression object, which should not normally be null.
   */
  public void setExpression(Expression exp);


}

