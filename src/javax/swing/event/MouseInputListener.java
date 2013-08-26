/*
 * @(#)MouseInputListener.java	1.13 10/03/23
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.event;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * A listener implementing all the methods in both the {@code MouseListener} and
 * {@code MouseMotionListener} interfaces.
 *
 * @see MouseInputAdapter
 * @version 1.13 03/23/10
 * @author Philip Milne
 */

public interface MouseInputListener extends MouseListener, MouseMotionListener {
}

