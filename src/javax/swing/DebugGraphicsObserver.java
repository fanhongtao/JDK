/*
 * @(#)DebugGraphicsObserver.java	1.11 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing;

import java.awt.*;
import java.awt.image.*;

/** ImageObserver for DebugGraphics, used for images only.
  * 
  * @version 1.11 12/19/03
  * @author Dave Karlton
  */
class DebugGraphicsObserver implements ImageObserver {
    int lastInfo;

    synchronized boolean allBitsPresent() {
        return (lastInfo & ImageObserver.ALLBITS) != 0;
    }

    synchronized boolean imageHasProblem() {
        return ((lastInfo & ImageObserver.ERROR) != 0 ||
                (lastInfo & ImageObserver.ABORT) != 0);
    }

    public synchronized boolean imageUpdate(Image img, int infoflags,
                                            int x, int y,
                                            int width, int height) {
        lastInfo = infoflags;
        return true;
    }
}
