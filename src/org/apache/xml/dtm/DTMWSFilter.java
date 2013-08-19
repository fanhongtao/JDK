package org.apache.xml.dtm;

/**
 * This interface is meant to be implemented by a client of the DTM, and allows
 * stripping of whitespace nodes.
 */
public interface DTMWSFilter
{
  /**
   * Do not strip whitespace child nodes of this element.
   */
  public static final short NOTSTRIP = 1;

  /**
   * Strip whitespace child nodes of this element.
   */
  public static final short STRIP = 2;

  /**
   * Inherit whitespace stripping behavior of the parent node.
   */
  public static final short INHERIT = 3;

  /**
   * Test whether whitespace-only text nodes are visible in the logical 
   * view of <code>DTM</code>. Normally, this function
   * will be called by the implementation of <code>DTM</code>; 
   * it is not normally called directly from
   * user code.
   * 
   * @param elementHandle int Handle of the element.
   * @return one of NOTSTRIP, STRIP, or INHERIT.
   */
  public short getShouldStripSpace(int elementHandle, DTM dtm);
  
}