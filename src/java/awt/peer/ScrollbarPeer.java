/*
 * @(#)ScrollbarPeer.java	1.10 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt.peer;

public interface ScrollbarPeer extends ComponentPeer {
    void setValues(int value, int visible, int minimum, int maximum);
    void setLineIncrement(int l);
    void setPageIncrement(int l);
}
