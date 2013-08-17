/*
 * @(#)WindowEvent.java	1.12 98/07/01
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
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

import java.awt.Event;
import java.awt.Window;

/**
 * The window-level event.
 *
 * @version 1.12 07/01/98
 * @author Carl Quinn
 * @author Amy Fowler
 */
public class WindowEvent extends ComponentEvent {

    /**
     * Marks the first integer id for the range of window event ids.
     */
    public static final int WINDOW_FIRST        = 200;

    /**
     * Marks the last integer id for the range of window event ids.
     */
    public static final int WINDOW_LAST         = 206;

    /**
     * The window opened event type.  This event is delivered only
     * the first time a window is made visible.
     */
    public static final int WINDOW_OPENED	= WINDOW_FIRST; // 200

    /**
     * The window closing event type. This event is delivered when
     * the user selects "Quit" from the window's system menu.  If
     * the program does not explicitly hide or destroy the window as
     * a result of this event, the window close operation will be
     * cancelled.
     */
    public static final int WINDOW_CLOSING	= 1 + WINDOW_FIRST; //Event.WINDOW_DESTROY

    /**
     * The window closed event type. This event is delivered after
     * the window has been closed as the result of a call to hide or
     * destroy.
     */
    public static final int WINDOW_CLOSED	= 2 + WINDOW_FIRST;

    /**
     * The window iconified event type.
     */
    public static final int WINDOW_ICONIFIED	= 3 + WINDOW_FIRST; //Event.WINDOW_ICONIFY

    /**
     * The window deiconified event type.
     */
    public static final int WINDOW_DEICONIFIED	= 4 + WINDOW_FIRST; //Event.WINDOW_DEICONIFY

    /**
     * The window activated event type.
     */
    public static final int WINDOW_ACTIVATED	= 5 + WINDOW_FIRST;

    /**
     * The window deactivated event type.
     */
    public static final int WINDOW_DEACTIVATED	= 6 + WINDOW_FIRST;

    /*
     * JDK 1.1 serialVersionUID 
     */
     private static final long serialVersionUID = -1567959133147912127L;

    /**
     * Constructs a WindowEvent object with the specified source window 
     * and type.
     * @param source the component where the event originated
     * @id the event type
     */
    public WindowEvent(Window source, int id) {
        super(source, id);
    }

    /**
     * Returns the window where this event originated.
     */
    public Window getWindow() {
        return (source instanceof Window) ? (Window)source : null;
    } 

    public String paramString() {
        String typeStr;
        switch(id) {
          case WINDOW_OPENED:
              typeStr = "WINDOW_OPENED";
              break;
          case WINDOW_CLOSING:
              typeStr = "WINDOW_CLOSING";
              break;
          case WINDOW_CLOSED:
              typeStr = "WINDOW_CLOSED";
              break;
          case WINDOW_ICONIFIED:
              typeStr = "WINDOW_ICONIFIED";
              break;
          case WINDOW_DEICONIFIED:
              typeStr = "WINDOW_DEICONIFIED";
              break;
          case WINDOW_ACTIVATED:
              typeStr = "WINDOW_ACTIVATED";
              break;
          case WINDOW_DEACTIVATED:
              typeStr = "WINDOW_DEACTIVATED";
              break;
          default:
              typeStr = "unknown type";
        }
        return typeStr;
    }

}
