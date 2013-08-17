/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.event;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * A listener implementing all the methods in both the MouseListener and
 * MouseMotionListener interfaces.
 *
 * @version 1.8 02/06/02
 * @author Philip Milne
 */

public interface MouseInputListener extends MouseListener, MouseMotionListener {
}

