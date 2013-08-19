/*
 * @(#)SynthPainter.java	1.7 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import java.awt.*;
import javax.swing.*;

/**
 * A painter that is used to paint various portions of a region.
 *
 * @version 1.7, 01/23/03
 * @author Scott Violet
 */
abstract class SynthPainter {
    /**
     * Paints the specified region.
     *
     * @param context SynthContext indentifying the hosting component
     * @param paintKey Identifies the portion of the component being asked
     *                 to paint, for example 'border', may be null.
     * @param g Graphics object to paint to
     * @param x x location to paint to
     * @param y y location to paint to
     * @param width Width of the region to paint to
     * @param height Height of the region to paint to
     */
    public abstract void paint(SynthContext context, Object paintKey,
                               Graphics g, int x, int y,
                               int width, int height);
}
