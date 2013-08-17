/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing;

/**
 * A private interface to access clip bounds in wrapped Graphics objects.
 *
 * @version 1.6 02/06/02
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
