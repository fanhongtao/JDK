/*
 * @(#)MouseInputListener.java	1.9 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.event;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * A listener implementing all the methods in both the MouseListener and
 * MouseMotionListener interfaces.
 *
 * @version 1.9 01/23/03
 * @author Philip Milne
 */

public interface MouseInputListener extends MouseListener, MouseMotionListener {
}

