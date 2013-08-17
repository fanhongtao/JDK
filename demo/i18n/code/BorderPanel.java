/*
 * @(#)BorderPanel.java	1.1 96/11/23
 *
 * (C) Copyright Taligent, Inc. 1996 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - All Rights Reserved
 *
 * Portions copyright (c) 1996 Sun Microsystems, Inc. All Rights Reserved.
 *
 *   The original version of this source code and documentation is copyrighted
 * and owned by Taligent, Inc., a wholly-owned subsidiary of IBM. These
 * materials are provided under terms of a License Agreement between Taligent
 * and Sun. This technology is protected by multiple US and International
 * patents. This notice and attribution to Taligent may not be removed.
 *   Taligent is a registered trademark of Taligent, Inc.
 *
 * Permission to use, copy, modify, and distribute this software
 * and its documentation for NON-COMMERCIAL purposes and without
 * fee is hereby granted provided that this copyright notice
 * appears in all copies. Please refer to the file "copyright.html"
 * for further important copyright and licensing information.
 *
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 * THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 * TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */


import java.awt.*;

public class BorderPanel extends Panel
{
   /**
    * Panel shadow border width
    */
   protected int shadow = 4;

   /**
    * Panel raised vs depressed look
    */
   protected boolean raised = true;
    public BorderPanel() {
        this.raised=true;
    }

    public BorderPanel(boolean raised) {
        this.raised=raised;
    }


   /**
    * Re-layout parent. Called when a panel changes
    * size etc.
    */
   protected void layoutParent() {
      Container parent = getParent();
      if (parent != null) {
	 parent.doLayout();
      }
   }

   public void paint(Graphics g) {
        super.paint(g);
        Dimension size = getSize();
        paintBorder(g, size);
    }

   protected void paintBorder(Graphics g, Dimension size) {
      Color c = getBackground();
      g.setColor(c);
      g.fillRect(0, 0, size.width, size.height);
      draw3DRect(g, 0, 0, size.width, size.height, raised);
   }

   /**
    * Draw a 3D Rectangle.
    * @param g the specified Graphics window
    * @param x, y, width, height
    * @param raised - true if border should be painted as raised.
    * @see #paint
    */
   public void draw3DRect(Graphics g, int x, int y, int width, int height,
			  boolean raised) {
      Color c = g.getColor();
      Color brighter = avgColor(c,Color.white);
      Color darker = avgColor(c,Color.black);


      // upper left corner
      g.setColor(raised ? brighter : darker);
      for (int i=0; i<shadow; i++) {
	  g.drawLine(x+i, y+i, x+width-1-i, y+i);
	  g.drawLine(x+i, y+i, x+i, y+height-1-i);
      }
      // lower right corner
      g.setColor(raised ? darker : brighter);
      for (int i=0; i<shadow; i++) {
	  g.drawLine(x+i, y+height-1-i, x+width-1-i, y+height-1-i);
	  g.drawLine(x+width-1-i, y+height-1-i, x+width-1-i, y+i);
      }
      g.setColor(c);
      // added by rip.
      g.setColor(Color.black);
      g.drawRect(x,y,width+2,height+2);

   }

   public static Color avgColor(Color c1, Color c2) {
    return new Color(
        (c1.getRed()+c2.getRed())/2,
        (c1.getGreen()+c2.getGreen())/2,
        (c1.getBlue()+c2.getBlue())/2
        );
   }

}

