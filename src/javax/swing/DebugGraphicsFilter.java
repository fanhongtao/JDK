/*
 * @(#)DebugGraphicsFilter.java	1.8 00/02/02
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.swing;

import java.awt.*;
import java.awt.image.*;

/** Color filter for DebugGraphics, used for images only.
  * 
  * @version 1.8 02/02/00
  * @author Dave Karlton
  */
class DebugGraphicsFilter extends RGBImageFilter {
    Color color;

    DebugGraphicsFilter(Color c) {
        canFilterIndexColorModel = true;
        color = c;
    }

    public int filterRGB(int x, int y, int rgb) {
        return color.getRGB() | (rgb & 0xFF000000);
    }
}
