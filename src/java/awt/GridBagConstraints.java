/*
 * @(#)GridBagConstraints.java	1.7 96/11/23
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
 */
package java.awt;

/**
 * GridBagConstraints is used to specify constraints for components
 * laid out using the GridBagLayout class.
 *
 * @see java.awt.GridBagLayout
 * @version 1.7, 11/23/96
 * @author Doug Stein
 */
public class GridBagConstraints implements Cloneable, java.io.Serializable {
  public static final int RELATIVE = -1;
  public static final int REMAINDER = 0;

  public static final int NONE = 0;
  public static final int BOTH = 1;
  public static final int HORIZONTAL = 2;
  public static final int VERTICAL = 3;

  public static final int CENTER = 10;
  public static final int NORTH = 11;
  public static final int NORTHEAST = 12;
  public static final int EAST = 13;
  public static final int SOUTHEAST = 14;
  public static final int SOUTH = 15;
  public static final int SOUTHWEST = 16;
  public static final int WEST = 17;
  public static final int NORTHWEST = 18;

  public int gridx, gridy, gridwidth, gridheight;
  public double weightx, weighty;
  public int anchor, fill;
  public Insets insets;
  public int ipadx, ipady;

  int tempX, tempY;
  int tempWidth, tempHeight;
  int minWidth, minHeight;

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
