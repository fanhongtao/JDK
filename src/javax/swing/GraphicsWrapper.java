/*
 * @(#)GraphicsWrapper.java	1.7 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing;

/**
 * A private interface to access clip bounds in wrapped Graphics objects.
 *
 * @version 1.7 01/23/03
 * @author Thomas Ball
 */

import java.awt.*;

interface GraphicsWrapper {
    Graphics subGraphics();

    boolean isClipIntersecting(Rectangle r);

    int getClipX();

    int getClipY();

    int getClipWidth();

    int getClipHeight();
}
