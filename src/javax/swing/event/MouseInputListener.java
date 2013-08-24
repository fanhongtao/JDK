/*
 * @(#)MouseInputListener.java	1.12 06/06/12
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.event;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * A listener implementing all the methods in both the {@code MouseListener} and
 * {@code MouseMotionListener} interfaces.
 *
 * @see MouseInputAdapter
 * @version 1.12 06/12/06
 * @author Philip Milne
 */

public interface MouseInputListener extends MouseListener, MouseMotionListener {
}

