/*
 * @(#)GraphicsWrapper.java	1.9 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing;

/**
 * A private interface to access clip bounds in wrapped Graphics objects.
 *
 * @version 1.9 11/17/05
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
