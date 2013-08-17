/*
 * @(#)Autoscroller.java	1.7 98/08/26
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package javax.swing;

import java.awt.*;
import java.awt.event.*;

import java.io.Serializable;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;


/**
 * @version 1.7 08/26/98
 * @author Dave Moore
 */

class Autoscroller extends MouseAdapter implements Serializable
{
    transient MouseEvent event;
    transient Timer timer;
    JComponent component;


    Autoscroller(JComponent c) {
	if (c == null) {
	    throw new IllegalArgumentException("component must be non null");
	}
	component = c;
	timer = new Timer(100, new AutoScrollTimerAction());
	component.addMouseListener(this);
    }

    class AutoScrollTimerAction implements ActionListener {
	public void actionPerformed(ActionEvent x) {
	    if(!component.isShowing() || (event == null)) {
		stop();
		return;
	    }
	    Point screenLocation = component.getLocationOnScreen();
	    MouseEvent e = new MouseEvent(component, event.getID(),
					  event.getWhen(), event.getModifiers(),
					  event.getX() - screenLocation.x,
					  event.getY() - screenLocation.y,
					  event.getClickCount(), event.isPopupTrigger());
	    component.superProcessMouseMotionEvent(e);
	}
    }

    void stop() {
	timer.stop();
	event = null;
    }

    public void mouseReleased(MouseEvent e) {
	stop();
    }

    public void mouseDragged(MouseEvent e) {
	Rectangle visibleRect = component.getVisibleRect();
	boolean contains = visibleRect.contains(e.getX(), e.getY());

	if (contains) {
	    if (timer.isRunning()) {
		stop();
	    }
	} else {
	    Point screenLocation = component.getLocationOnScreen();

	    event = new MouseEvent(component, e.getID(), e.getWhen(), e.getModifiers(),
				   e.getX() + screenLocation.x,
				   e.getY() + screenLocation.y,
				   e.getClickCount(), e.isPopupTrigger());
	    if (!timer.isRunning()) {
		timer.start();
	    }
	}
    }

    private void writeObject(ObjectOutputStream s) throws IOException {
	s.defaultWriteObject();
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException 
    {
	s.defaultReadObject();
	timer = new Timer(100, new AutoScrollTimerAction());
    }
}
