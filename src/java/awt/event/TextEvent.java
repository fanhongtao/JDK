/*
 * @(#)TextEvent.java	1.6 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.event;

import java.awt.AWTEvent;
import java.awt.Event;

/**
 * The text event emitted by TextComponents.
 * @see java.awt.TextComponent
 * @see TextEventListener
 *
 * @version 1.6 12/10/01
 * @author Georges Saab
 */

public class TextEvent extends AWTEvent {

    /**
     * Marks the first integer id for the range of adjustment event ids.
     */
    public static final int TEXT_FIRST 	= 900;

    /**
     * Marks the last integer id for the range of adjustment event ids.
     */
    public static final int TEXT_LAST 	= 900;

    /**
     * The adjustment value changed event.
     */
    public static final int TEXT_VALUE_CHANGED	= TEXT_FIRST;

    /*
     * JDK 1.1 serialVersionUID 
     */
    private static final long serialVersionUID = 6269902291250941179L;

    /**
     * Constructs a TextEvent object with the specified TextComponent source,
     * and type.
     * @param source the TextComponent where the event originated
     * @id the event type
     * @type the textEvent type 
     */
    public TextEvent(Object source, int id) {
        super(source, id);
    }


    public String paramString() {
        String typeStr;
        switch(id) {
          case TEXT_VALUE_CHANGED:
              typeStr = "TEXT_VALUE_CHANGED";
              break;
          default:
              typeStr = "unknown type";
        }
        return typeStr;
    }
}


