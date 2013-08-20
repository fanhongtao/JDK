/*
 * @(#)ScrollbarPeer.java	1.17 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt.peer;

/**
 * The peer interfaces are intended only for use in porting
 * the AWT. They are not intended for use by application
 * developers, and developers should not implement peers
 * nor invoke any of the peer methods directly on the peer
 * instances.
 */
public interface ScrollbarPeer extends ComponentPeer {
    void setValues(int value, int visible, int minimum, int maximum);
    void setLineIncrement(int l);
    void setPageIncrement(int l);
}
