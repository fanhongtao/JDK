/*
 * @(#)LayeredHighlighter.java	1.2 98/08/26
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package javax.swing.text;

import java.awt.Graphics;
import java.awt.Shape;

/**
 * 
 * @author  Scott Violet
 * @author  Timothy Prinzing
 * @version 1.2 08/26/98
 * @see     Highlighter
 */
public abstract class LayeredHighlighter implements Highlighter {
    /**
     * When leaf Views (such as LabelView) are rendering they should
     * call into this method. If a highlight is in the given region it will
     * be drawn immediately.
     *
     * @param g Graphics used to draw
     * @param p0 starting offset of view
     * @param p1 ending offset of view
     * @param viewBounds Bounds of View
     * @param editor JTextComponent
     * @param view View instance being rendered
     */
    public abstract void paintLayeredHighlights(Graphics g, int p0, int p1,
						Shape viewBounds,
						JTextComponent editor,
						View view);


    /**
     * Layered highlight renderer.
     */
    static public abstract class LayerPainter implements Highlighter.HighlightPainter {
	public abstract Shape paintLayer(Graphics g, int p0, int p1,
					Shape viewBounds,JTextComponent editor,
					View view);
    }
}
