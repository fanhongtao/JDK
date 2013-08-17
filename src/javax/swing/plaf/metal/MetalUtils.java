/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package javax.swing.plaf.metal;

import javax.swing.plaf.*;
import javax.swing.*;
import java.awt.*;

/**
 * This is a dumping ground for random stuff we want to use in several places.
 *
 * @version 1.25 02/06/02
 * @author Steve Wilson
 */

class MetalUtils {

    static void drawFlush3DBorder(Graphics g, Rectangle r) {
        drawFlush3DBorder(g, r.x, r.y, r.width, r.height);
    }

    /**
      * This draws the "Flush 3D Border" which is used throughout the Metal L&F
      */
    static void drawFlush3DBorder(Graphics g, int x, int y, int w, int h) {
        g.translate( x, y);
        g.setColor( MetalLookAndFeel.getControlDarkShadow() );
	g.drawRect( 0, 0, w-2, h-2 );
        g.setColor( MetalLookAndFeel.getControlHighlight() );
	g.drawRect( 1, 1, w-2, h-2 );
        g.setColor( MetalLookAndFeel.getControl() );
	g.drawLine( 0, h-1, 1, h-2 );
	g.drawLine( w-1, 0, w-2, 1 );
        g.translate( -x, -y);
    }

    /**
      * This draws a variant "Flush 3D Border"
      * It is used for things like pressed buttons.
      */
    static void drawPressed3DBorder(Graphics g, Rectangle r) {
        drawPressed3DBorder( g, r.x, r.y, r.width, r.height );
    }

    static void drawDisabledBorder(Graphics g, int x, int y, int w, int h) {
        g.translate( x, y);
        g.setColor( MetalLookAndFeel.getControlShadow() );
	g.drawRect( 0, 0, w-1, h-1 );
    }

    /**
      * This draws a variant "Flush 3D Border"
      * It is used for things like pressed buttons.
      */
    static void drawPressed3DBorder(Graphics g, int x, int y, int w, int h) {
        g.translate( x, y);

        drawFlush3DBorder(g, 0, 0, w, h);

        g.setColor( MetalLookAndFeel.getControlShadow() );
	g.drawLine( 1, 1, 1, h-2 );
	g.drawLine( 1, 1, w-2, 1 );
        g.translate( -x, -y);
    }

    /**
      * This draws a variant "Flush 3D Border"
      * It is used for things like active toggle buttons.
      * This is used rarely.
      */
    static void drawDark3DBorder(Graphics g, Rectangle r) {
        drawDark3DBorder(g, r.x, r.y, r.width, r.height);
    }

    /**
      * This draws a variant "Flush 3D Border"
      * It is used for things like active toggle buttons.
      * This is used rarely.
      */
    static void drawDark3DBorder(Graphics g, int x, int y, int w, int h) {
        g.translate( x, y);

        drawFlush3DBorder(g, 0, 0, w, h);

        g.setColor( MetalLookAndFeel.getControl() );
	g.drawLine( 1, 1, 1, h-2 );
	g.drawLine( 1, 1, w-2, 1 );
        g.setColor( MetalLookAndFeel.getControlShadow() );
	g.drawLine( 1, h-2, 1, h-2 );
	g.drawLine( w-2, 1, w-2, 1 );
        g.translate( -x, -y);
    }

    static void drawButtonBorder(Graphics g, int x, int y, int w, int h, boolean active) {
        if (active) {
            drawActiveButtonBorder(g, x, y, w, h);	    
        } else {
            drawFlush3DBorder(g, x, y, w, h);
	}
    }

    static void drawActiveButtonBorder(Graphics g, int x, int y, int w, int h) {
        drawFlush3DBorder(g, x, y, w, h);
        g.setColor( MetalLookAndFeel.getPrimaryControl() );
	g.drawLine( x+1, y+1, x+1, h-3 );
	g.drawLine( x+1, y+1, w-3, x+1 );
        g.setColor( MetalLookAndFeel.getPrimaryControlDarkShadow() );
	g.drawLine( x+2, h-2, w-2, h-2 );
	g.drawLine( w-2, y+2, w-2, h-2 );
    }

    static void drawDefaultButtonBorder(Graphics g, int x, int y, int w, int h, boolean active) {
        drawButtonBorder(g, x+1, y+1, w-1, h-1, active);	    
        g.setColor( MetalLookAndFeel.getControlDarkShadow() );
	g.drawRect( x, y, w-3, h-3 );
	g.drawLine( w-2, 0, w-2, 0);
	g.drawLine( 0, h-2, 0, h-2);
    }

    /*
     * Convenience function for determining ComponentOrientation.  Helps us
     * avoid having Munge directives throughout the code.
     */
    static boolean isLeftToRight( Component c ) {
        return c.getComponentOrientation().isLeftToRight();
    }
    
}

