/*
 * @(#)GridBagConstraints.java	1.11 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package java.awt;

/**
 * The <code>GridBagConstraints</code> class specifies constraints 
 * for components that are laid out using the 
 * <code>GridBagLayout</code> class.
 *
 * @version 1.11, 07/01/98
 * @author Doug Stein
 * @see java.awt.GridBagLayout
 * @since JDK1.0
 */
public class GridBagConstraints implements Cloneable, java.io.Serializable {

   /**
     * Specify that this component is the next-to-last component in its 
     * column or row (<code>gridwidth</code>, <code>gridheight</code>), 
     * or that this component be placed next to the previously added 
     * component (<code>gridx</code>, <code>gridy</code>). 
     * @see      java.awt.GridBagConstraints#gridwidth
     * @see      java.awt.GridBagConstraints#gridheight
     * @see      java.awt.GridBagConstraints#gridx
     * @see      java.awt.GridBagConstraints#gridy
     * @since    JDK1.0
     */
  public static final int RELATIVE = -1;

   /**
     * Specify that this component is the 
     * last component in its column or row. 
     * @since   JDK1.0
     */
  public static final int REMAINDER = 0;

   /**
     * Do not resize the component. 
     * @since   JDK1.0
     */
  public static final int NONE = 0;

   /**
     * Resize the component both horizontally and vertically. 
     * @since   JDK1.0
     */
  public static final int BOTH = 1;

   /**
     * Resize the component horizontally but not vertically. 
     * @since   JDK1.0
     */
  public static final int HORIZONTAL = 2;

   /**
     * Resize the component vertically but not horizontally. 
     * @since   JDK1.0
     */
  public static final int VERTICAL = 3;

   /**
    * Put the component in the center of its display area.
    * @since    JDK1.0
    */
  public static final int CENTER = 10;

   /**
     * Put the component at the top of its display area,
     * centered horizontally. 
     * @since   JDK1.0
     */
  public static final int NORTH = 11;

    /**
     * Put the component at the top-right corner of its display area. 
     * @since   JDK1.0
     */
  public static final int NORTHEAST = 12;

    /**
     * Put the component on the right side of its display area, 
     * centered vertically.
     * @since    JDK1.0
     */
  public static final int EAST = 13;

    /**
     * Put the component at the bottom-right corner of its display area. 
     * @since   JDK1.0
     */
  public static final int SOUTHEAST = 14;

    /**
     * Put the component at the bottom of its display area, centered 
     * horizontally. 
     * @since   JDK1.0
     */
  public static final int SOUTH = 15;

   /**
     * Put the component at the bottom-left corner of its display area. 
     * @since   JDK1.0
     */
  public static final int SOUTHWEST = 16;

    /**
     * Put the component on the left side of its display area, 
     * centered vertically.
     * @since    JDK1.0
     */
  public static final int WEST = 17;

   /**
     * Put the component at the top-left corner of its display area. 
     * @since   JDK1.0
     */
  public static final int NORTHWEST = 18;

   /**
     * Specifies the cell at the left of the component's display area, 
     * where the leftmost cell has <code>gridx&nbsp;=&nbsp;0</code>. The value 
     * <code>RELATIVE</code> specifies that the component be placed just 
     * to the right of the component that was added to the container just 
     * before this component was added. 
     * <p>
     * The default value is <code>RELATIVE</code>. 
     * @see      java.awt.GridBagConstraints#gridy
     * @since    JDK1.0
     */
  public int gridx;

   /**
     * Specifies the cell at the top of the component's display area, 
     * where the topmost cell has <code>gridy&nbsp;=&nbsp;0</code>. The value 
     * <code>RELATIVE</code> specifies that the component be placed just 
     * below the component that was added to the container just before 
     * this component was added. 
     * <p>
     * The default value is <code>RELATIVE</code>. 
     * @see      java.awt.GridBagConstraints#gridx
     * @since    JDK1.0
     */
  public int gridy;

   /**
     * Specifies the number of cells in a row for the component's 
     * display area. 
     * <p>
     * Use <code>REMAINDER</code> to specify that the component be the 
     * last one in its row. Use <code>RELATIVE</code> to specify that the 
     * component be the next-to-last one in its row. 
     * <p>
     * The default value is 1. 
     * @see      java.awt.GridBagConstraints#gridheight
     * @since    JDK1.0
     */
  public int gridwidth;

   /**
     * Specifies the number of cells in a column for the component's 
     * display area. 
     * <p>
     * Use <code>REMAINDER</code> to specify that the component be the 
     * last one in its column. Use <code>RELATIVE</code> to specify that 
     * the component be the next-to-last one in its column. 
     * <p>
     * The default value is 1.
     * @see      java.awt.GridBagConstraints#gridwidth
     * @since    JDK1.0
     */
  public int gridheight;

   /**
     * Specifies how to distribute extra horizontal space. 
     * <p>
     * The grid bag layout manager calculates the weight of a column to 
     * be the maximum <code>weighty</code> of all the components in a 
     * row. If the resulting layout is smaller horizontally than the area 
     * it needs to fill, the extra space is distributed to each column in 
     * proportion to its weight. A column that has a weight zero receives no 
     * extra space. 
     * <p>
     * If all the weights are zero, all the extra space appears between 
     * the grids of the cell and the left and right edges. 
     * <p>
     * The default value of this field is <code>0</code>. 
     * @see      java.awt.GridBagConstraints#weighty
     * @since    JDK1.0
     */
  public double weightx;

   /**
     * Specifies how to distribute extra vertical space. 
     * <p>
     * The grid bag layout manager calculates the weight of a row to be 
     * the maximum <code>weightx</code> of all the components in a row. 
     * If the resulting layout is smaller vertically than the area it 
     * needs to fill, the extra space is distributed to each row in 
     * proportion to its weight. A row that has a weight of zero receives no 
     * extra space. 
     * <p>
     * If all the weights are zero, all the extra space appears between 
     * the grids of the cell and the top and bottom edges. 
     * <p>
     * The default value of this field is <code>0</code>. 
     * @see      java.awt.GridBagConstraints#weightx
     * @since    JDK1.0
     */
  public double weighty;

   /**
     * This field is used when the component is smaller than its display 
     * area. It determines where, within the display area, to place the 
     * component. Possible values are <code>CENTER<code>, 
     * <code>NORTH<code>, <code>NORTHEAST<code>, <code>EAST<code>, 
     * <code>SOUTHEAST<code>, <code>SOUTH<code>, <code>SOUTHWEST<code>, 
     * <code>WEST<code>, and <code>NORTHWEST<code>.
     * The default value is <code>CENTER</code>. 
     * @since    JDK1.0
     */
  public int anchor;

   /**
     * This field is used when the component's display area is larger 
     * than the component's requested size. It determines whether to 
     * resize the component, and if so, how. 
     * <p>
     * The following values are valid for <code>fill</code>: 
     * <p>
     * <ul>
     * <li>
     * <code>NONE</code>: Do not resize the component. 
     * <li>
     * <code>HORIZONTAL</code>: Make the component wide enough to fill 
     *         its display area horizontally, but do not change its height. 
     * <li>
     * <code>VERTICAL</code>: Make the component tall enough to fill its 
     *         display area vertically, but do not change its width. 
     * <li>
     * <code>BOTH</code>: Make the component fill its display area 
     *         entirely. 
     * </ul>
     * <p>
     * The default value is <code>NONE</code>. 
     * @since   JDK1.0
     */
  public int fill;

   /**
     * This field specifies the external padding of the component, the 
     * minimum amount of space between the component and the edges of its 
     * display area. 
     * <p>
     * The default value is <code>new Insets(0, 0, 0, 0)</code>. 
     * @since    JDK1.0
     */
  public Insets insets;

   /**
     * This field specifies the internal padding of the component, how much 
     * space to add to the minimum width of the component. The width of 
     * the component is at least its minimum width plus 
     * <code>(ipadx&nbsp;*&nbsp;2)</code> pixels. 
     * <p>
     * The default value is <code>0</code>. 
     * @see      java.awt.GridBagConstraints#ipady
     * @since    JDK1.0
     */
  public int ipadx;

   /**
     * This field specifies the internal padding, that is, how much 
     * space to add to the minimum height of the component. The height of 
     * the component is at least its minimum height plus 
     * <code>(ipady&nbsp;*&nbsp;2)</code> pixels. 
     * <p>
     * The default value is 0. 
     * @see      java.awt.GridBagConstraints#ipadx
     * @since    JDK1.0
     */
  public int ipady;

  int tempX, tempY;
  int tempWidth, tempHeight;
  int minWidth, minHeight;

   /**
     * Creates a <code>GridBagConstraint</code> object with 
     * all of its fields set to their default value. 
     * @since    JDK1.0
     */
  public GridBagConstraints () {
    gridx = RELATIVE;
    gridy = RELATIVE;
    gridwidth = 1;
    gridheight = 1;

    weightx = 0;
    weighty = 0;
    anchor = CENTER;
    fill = NONE;

    insets = new Insets(0, 0, 0, 0);
    ipadx = 0;
    ipady = 0;
  }

   /**
    * Creates a copy of this grid bag constraint.
    * @return     a copy of this grid bag constraint
    * @since      JDK1.0
    */
  public Object clone () {
      try { 
	  GridBagConstraints c = (GridBagConstraints)super.clone();
	  c.insets = (Insets)insets.clone();
	  return c;
      } catch (CloneNotSupportedException e) { 
	  // this shouldn't happen, since we are Cloneable
	  throw new InternalError();
      }
  }
}
