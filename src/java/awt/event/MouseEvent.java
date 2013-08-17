/*
 * @(#)MouseEvent.java	1.13 98/07/01
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.awt.event;

import java.awt.Component;
import java.awt.Event;
import java.awt.Point;

/**
 * The mouse event.
 *
 * @version 1.13 07/01/98
 * @author Carl Quinn
 */
public class MouseEvent extends InputEvent {

    /**
     * Marks the first integer id for the range of mouse event ids.
     */
    public static final int MOUSE_FIRST 	= 500;

    /**
     * Marks the last integer id for the range of mouse event ids.
     */
    public static final int MOUSE_LAST          = 506;

    /**
     * The mouse clicked event type.
     */
    public static final int MOUSE_CLICKED = MOUSE_FIRST;

    /**
     * The mouse pressed event type.
     */
    public static final int MOUSE_PRESSED = 1 + MOUSE_FIRST; //Event.MOUSE_DOWN

    /**
     * The mouse released event type.
     */
    public static final int MOUSE_RELEASED = 2 + MOUSE_FIRST; //Event.MOUSE_UP

    /**
     * The mouse moved event type.
     */
    public static final int MOUSE_MOVED = 3 + MOUSE_FIRST; //Event.MOUSE_MOVE

    /**
     * The mouse entered event type.
     */
    public static final int MOUSE_ENTERED = 4 + MOUSE_FIRST; //Event.MOUSE_ENTER

    /**
     * The mouse exited event type.
     */
    public static final int MOUSE_EXITED = 5 + MOUSE_FIRST; //Event.MOUSE_EXIT

    /**
     * The mouse dragged event type.
     */
    public static final int MOUSE_DRAGGED = 6 + MOUSE_FIRST; //Event.MOUSE_DRAG

    int x;
    int y;
    int clickCount;
    boolean popupTrigger = false;

    /*
     * JDK 1.1 serialVersionUID 
     */
    private static final long serialVersionUID = -991214153494842848L;

    /**
     * Constructs a MouseEvent object with the specified source component,
     * type, modifiers, coordinates, and click count.
     * @param source the object where the event originated
     * @id the event type
     * @when the time the event occurred
     * @modifiers the modifiers down during event
     * @x the x coordinate location of the mouse
     * @y the y coordinate location of the mouse
     * @clickCount the number of mouse clicks associated with event
     * @popupTrigger whether this event is a popup-menu trigger event
     */
    public MouseEvent(Component source, int id, long when, int modifiers,
                      int x, int y, int clickCount, boolean popupTrigger) {
        super(source, id, when, modifiers);
        this.x = x;
        this.y = y;
        this.clickCount = clickCount;
        this.popupTrigger = popupTrigger;
    }

    /**
     * Returns the x position of the event relative to the source component.
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y position of the event relative to the source component.
     */
    public int getY() {
        return y;
    }

    /**
     * Returns the x,y position of the event relative to the source component.
     */
    public Point getPoint() {
	int x;
	int y;
	synchronized (this) {
	    x = this.x;
	    y = this.y;
	}
        return new Point(x, y);
    }

    /**
     * Translates the coordinate position of the event by x, y.
     * @param x the x value added to the current x coordinate position
     * @param y the y value added to the current y coordinate position
     */
    public synchronized void translatePoint(int x, int y) {
        this.x += x;
        this.y += y;
    }

    /**
     * Return the number of mouse clicks associated with this event.
     */
    public int getClickCount() {
        return clickCount;
    }

    /**
     * Returns whether or not this mouse event is the popup-menu
     * trigger event for the platform.
     */
    public boolean isPopupTrigger() {
        return popupTrigger;
    }

    public String paramString() {
        String typeStr;
        switch(id) {
          case MOUSE_PRESSED:
              typeStr = "MOUSE_PRESSED";
              break;
          case MOUSE_RELEASED:
              typeStr = "MOUSE_RELEASED";
              break;
          case MOUSE_CLICKED:
              typeStr = "MOUSE_CLICKED";
              break;
          case MOUSE_ENTERED:
              typeStr = "MOUSE_ENTERED";
              break;
          case MOUSE_EXITED:
              typeStr = "MOUSE_EXITED";
              break;
          case MOUSE_MOVED:
              typeStr = "MOUSE_MOVED";
              break;
          case MOUSE_DRAGGED:
              typeStr = "MOUSE_DRAGGED";
              break;
          default:
              typeStr = "unknown type";
        }
        return typeStr + ",("+x+","+y+")"+ ",mods="+getModifiers()+ 
               ",clickCount="+clickCount;
    }

}
