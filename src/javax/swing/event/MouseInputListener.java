/*
 * @(#)MouseInputListener.java	1.7 00/02/02
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.swing.event;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 * A listener implementing all the methods in both the MouseListener and
 * MouseMotionListener interfaces.
 *
 * @version 1.7 02/02/00
 * @author Philip Milne
 */

public interface MouseInputListener extends MouseListener, MouseMotionListener {
}

