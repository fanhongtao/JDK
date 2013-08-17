/*
 * @(#)WindowsBorders.java	1.11 98/08/26
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package com.sun.java.swing.plaf.windows;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;

import java.awt.Component;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;


/**
 * Factory object that can vend Borders appropriate for the Windows 95 L & F.
 * @version 1.11 08/26/98
 * @author Rich Schiavi
 */

public class WindowsBorders {

    public static class ComboBoxBorder implements Border, UIResource {

        protected Color shadow;
        protected Color darkShadow;
        protected Color highlight;

        public ComboBoxBorder(Color shadow, Color darkShadow, Color highlight) {
           this.shadow = shadow;
           this.darkShadow = darkShadow;
           this.highlight = highlight; 
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height){
            Rectangle b = c.getBounds();
            BasicGraphicsUtils.drawEtchedRect(g,0,0,b.width,b.height,
                                              c.getBackground(), shadow,
                                              darkShadow, highlight);
        }

        public Insets getBorderInsets(Component c) {
            return new Insets( 2, 2, 2, 2 );
        }

        public boolean isBorderOpaque() {
            return true;
        }
    }

  public static class ProgressBarBorder extends AbstractBorder implements UIResource {
        protected Color shadow;
        protected Color highlight;

        public ProgressBarBorder(Color shadow, Color highlight) {
            this.highlight = highlight;
            this.shadow = shadow;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, 
                                int width, int height) {
	    g.setColor(shadow);
	    g.drawLine(x,y, width-1,y); // draw top
	    g.drawLine(x,y, x,height-1); // draw left
	    g.setColor(highlight);
	    g.drawLine(x,height-1, width-1,height-1); // draw bottom
	    g.drawLine(width-1,y, width-1,height-1); // draw right
        }

        public Insets getBorderInsets(Component c)       {
            return new Insets(1,1,1,1);
        }
    }


}
