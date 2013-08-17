/*
 * @(#)FocusEvent.java	1.15 98/07/01
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

/**
 * The component-level focus event.
 * There are two levels of focus change events: permanent and temporary.
 * Permanent focus change events occur when focus is directly moved
 * from one component to another, such as through calls to requestFocus()
 * or as the user uses the Tab key to traverse components.
 * Temporary focus change events occur when focus is temporarily
 * gained or lost for a component as the indirect result of another
 * operation, such as window deactivation or a scrollbar drag.  In this
 * case, the original focus state will automatically be restored once
 * that operation is finished, or, for the case of window deactivation,
 * when the window is reactivated.  Both permanent and temporary focus
 * events are delivered using the FOCUS_GAINED and FOCUS_LOST event ids;
 * the levels may be distinguished in the event using the isTemporary()
 * method.
 *  
 * @version 1.15 07/01/98
 * @author Carl Quinn
 * @author Amy Fowler
 */
public class FocusEvent extends ComponentEvent {

    /**
     * Marks the first integer id for the range of focus event ids.
     */    
    public static final int FOCUS_FIRST		= 1004;

    /**
     * Marks the last integer id for the range of focus event ids.
     */
    public static final int FOCUS_LAST		= 1005;

    /**
     * The focus gained event type.  
     */
    public static final int FOCUS_GAINED = FOCUS_FIRST; //Event.GOT_FOCUS

    /**
     * The focus lost event type.  
     */
    public static final int FOCUS_LOST = 1 + FOCUS_FIRST; //Event.LOST_FOCUS

    boolean temporary = false;

    /*
     * JDK 1.1 serialVersionUID 
     */
     private static final long serialVersionUID = 523753786457416396L;

    /**
     * Constructs a FocusEvent object with the specified source component,
     * type, and whether or not the focus event is a temporary level event.
     * @param source the object where the event originated
     * @id the event type
     * @temporary whether or not this focus change is temporary
     */
    public FocusEvent(Component source, int id, boolean temporary) {
        super(source, id);
        this.temporary = temporary;
    }

    /**
     * Constructs a permanent-level FocusEvent object with the 
     * specified source component and type.
     * @param source the object where the event originated
     * @id the event type
     */
    public FocusEvent(Component source, int id) {
        this(source, id, false);
    }

    /**
     * Returns whether or not this focus change event is a temporary
     * change.
     */
    public boolean isTemporary() {
        return temporary;
    }

    public String paramString() {
        String typeStr;
        switch(id) {
          case FOCUS_GAINED:
              typeStr = "FOCUS_GAINED";
              break;
          case FOCUS_LOST:
              typeStr = "FOCUS_LOST";
              break;
          default:
              typeStr = "unknown type";
        }
        return typeStr + (temporary? ",temporary" : ",permanent");
    }

}
