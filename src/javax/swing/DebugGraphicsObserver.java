/*
 * @(#)DebugGraphicsObserver.java	1.2 00/01/12
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

/** ImageObserver for DebugGraphics, used for images only.
  * 
  * @version 1.6 08/26/98
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
