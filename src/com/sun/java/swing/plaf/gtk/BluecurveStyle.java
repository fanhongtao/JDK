/*
 * @(#)BluecurveStyle.java	1.2 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.java.swing.plaf.gtk;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.plaf.*;

/**
 * @version 1.2, 01/23/03
 * @author Scott Violet
 */
class BluecurveStyle extends GTKStyle implements GTKConstants {
    /**
     * There should only ever be one pixmap engine.
     */
    private static final GTKEngine BLUECURVE_ENGINE = new BluecurveEngine();

    private static final Color DEFAULT_COLOR = new ColorUIResource(0, 0, 0);

    /**
     * Colors specific to blue curve. These don't appear to be specific
     * to a state, hence they are stored here.
     */
    private Color[] blueColors;

    /**
     * Creates a duplicate of the passed in style.
     */
    public BluecurveStyle(DefaultSynthStyle style) {
        super(style);
    }

    /**
     * Creates a PixmapStyle from the passed in arguments.
     */
    public BluecurveStyle(StateInfo[] states,
                          CircularIdentityList classSpecificValues,
                          Font font,
                          int xThickness, int yThickness,
                          GTKStockIconInfo[] icons) {
        super(states, classSpecificValues, font, xThickness, yThickness,icons);
    }

    /**
     * Adds the state of this PixmapStyle to that of <code>s</code>
     * returning a combined SynthStyle.
     */
    public DefaultSynthStyle addTo(DefaultSynthStyle s) {
        if (!(s instanceof BluecurveStyle)) {
            s = new BluecurveStyle(s);
        }
        BluecurveStyle style = (BluecurveStyle)super.addTo(s);
        return style;
    }

    /**
     * Creates a copy of the reciever and returns it.
     */
    public Object clone() {
        return super.clone();
    }

    /**
     * Returns a GTKEngine to use for rendering.
     */
    public GTKEngine getEngine(SynthContext context) {
        return BLUECURVE_ENGINE;
    }

    Color getDefaultColor(JComponent c, Region id, int state,
                          ColorType type) {
        int colorID = type.getID();
        if (colorID >= BluecurveColorType.MIN_ID &&
                      colorID <= BluecurveColorType.MAX_ID) {
            if (blueColors == null) {
                int min = BluecurveColorType.MIN_ID;
                Color base = getGTKColor(null, id, SynthConstants.SELECTED,
                                         GTKColorType.TEXT_BACKGROUND);
                Color bg = getGTKColor(null, id, SynthConstants.ENABLED,
                                       GTKColorType.BACKGROUND);

                blueColors = new Color[BluecurveColorType.MAX_ID - min + 1];
                blueColors[BluecurveColorType.OUTER.getID() - min] =
                    GTKColorType.adjustColor(base, 1.0f, .72f, .7f);
                blueColors[BluecurveColorType.INNER_LEFT.getID() - min] =
                    GTKColorType.adjustColor(base, 1.0f, 1.63f, 1.53f);
                blueColors[BluecurveColorType.TOP_GRADIENT.getID() - min] =
                    GTKColorType.adjustColor(base, 1.0f, .93f, .88f);
                blueColors[BluecurveColorType.BOTTOM_GRADIENT.getID() - min] =
                    GTKColorType.adjustColor(base, 1f, 1.16f,1.13f);
                blueColors[BluecurveColorType.INNER_RIGHT.getID() - min] =
                    GTKColorType.adjustColor(base, 1.0f, 1.06f, 1.08f);

                blueColors[BluecurveColorType.OUTER2.getID() - min] =
                    GTKColorType.adjustColor(bg, 1.0f, .67f, .67f);
                blueColors[BluecurveColorType.INNER_RIGHT2.getID() - min] =
                    GTKColorType.adjustColor(bg, 1.0f, .92f, .92f);

                blueColors[BluecurveColorType.OUTER3.getID() - min] =
                    GTKColorType.adjustColor(bg, 1.0f, .4f, .4f);

                blueColors[BluecurveColorType.OUTER4.getID() - min] =
                    GTKColorType.adjustColor(bg, 1.0f, .84f, .84f);

                blueColors[BluecurveColorType.OUTER5.getID() - min] =
                    GTKColorType.adjustColor(bg, 1.0f, .245f, .192f);
            }
            return blueColors[colorID - BluecurveColorType.MIN_ID];
        }
        return super.getDefaultColor(c, id, state, type);
    }

    public String toString() {
        StringBuffer buf = new StringBuffer(super.toString());

        if (blueColors != null) {
            buf.append("\t" + BluecurveColorType.OUTER + "=" +
                       blueColors[BluecurveColorType.OUTER.getID() -
                                  BluecurveColorType.MIN_ID] + "\n");
            buf.append("\t" + BluecurveColorType.INNER_LEFT + "=" +
                       blueColors[BluecurveColorType.INNER_LEFT.getID() -
                                  BluecurveColorType.MIN_ID] + "\n");
            buf.append("\t" + BluecurveColorType.INNER_RIGHT + "=" +
                       blueColors[BluecurveColorType.INNER_RIGHT.getID() -
                                  BluecurveColorType.MIN_ID] + "\n");
            buf.append("\t" + BluecurveColorType.TOP_GRADIENT + "=" +
                       blueColors[BluecurveColorType.TOP_GRADIENT.getID() -
                                  BluecurveColorType.MIN_ID] + "\n");
            buf.append("\t" + BluecurveColorType.BOTTOM_GRADIENT + "=" +
                       blueColors[BluecurveColorType.BOTTOM_GRADIENT.getID() -
                                  BluecurveColorType.MIN_ID] + "\n");
            buf.append("\t" + BluecurveColorType.OUTER2 + "=" +
                       blueColors[BluecurveColorType.OUTER2.getID() -
                                  BluecurveColorType.MIN_ID] + "\n");
            buf.append("\t" + BluecurveColorType.INNER_RIGHT2 + "=" +
                       blueColors[BluecurveColorType.INNER_RIGHT2.getID() -
                                  BluecurveColorType.MIN_ID] + "\n");
            buf.append("\t" + BluecurveColorType.OUTER3 + "=" +
                       blueColors[BluecurveColorType.OUTER3.getID() -
                                  BluecurveColorType.MIN_ID] + "\n");
            buf.append("\t" + BluecurveColorType.OUTER4 + "=" +
                       blueColors[BluecurveColorType.OUTER4.getID() -
                                  BluecurveColorType.MIN_ID] + "\n");
            buf.append("\t" + BluecurveColorType.OUTER5 + "=" +
                       blueColors[BluecurveColorType.OUTER5.getID() -
                                  BluecurveColorType.MIN_ID] + "\n");
        }
        return buf.toString();
    }
}
