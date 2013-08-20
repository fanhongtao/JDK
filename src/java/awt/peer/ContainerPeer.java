/*
 * @(#)ContainerPeer.java	1.20 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt.peer;

import java.awt.*;

/**
 * The peer interfaces are intended only for use in porting
 * the AWT. They are not intended for use by application
 * developers, and developers should not implement peers
 * nor invoke any of the peer methods directly on the peer
 * instances.
 */
public interface ContainerPeer extends ComponentPeer {
    Insets getInsets();
    void beginValidate();
    void endValidate();
    void beginLayout();
    void endLayout();
    boolean isPaintPending();

    /**
     * Cancels a pending paint on the specified region of the
     * Component.
     */
    void cancelPendingPaint(int x, int y, int w, int h);


    /**
     * Restacks native windows - children of this native window - according to Java container order
     * @since 1.5
     */
    void restack();
    
    /**
     * Indicates availabiltity of restacking operation in this container.
     * @return Returns true if restack is supported, false otherwise
     * @since 1.5
     */
    boolean isRestackSupported(); 
    /**



     * DEPRECATED:  Replaced by getInsets().
     */
    Insets insets();
}
