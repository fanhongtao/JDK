/*
 * @(#)ActionEvent.java	1.12 97/01/27
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
 */

package java.awt.event;

import java.awt.AWTEvent;
import java.awt.Event;

/**
 * The action semantic event.
 * @see ActionListener
 *
 * @version 1.12 01/27/97
 * @author Carl Quinn
 */
public class ActionEvent extends AWTEvent {

    /**
     * The shift modifier constant.
     */
    public static final int SHIFT_MASK		= Event.SHIFT_MASK;

    /**
     * The control modifier constant.
     */
    public static final int CTRL_MASK		= Event.CTRL_MASK;

    /** 
     * The meta modifier constant.
     */
    public static final int META_MASK		= Event.META_MASK;

    /** 
     * The alt modifier constant.
     */
    public static final int ALT_MASK		= Event.ALT_MASK;


    /**
     * Marks the first integer id for the range of action event ids.
     */
    public static final int ACTION_FIRST		= 1001;

    /**
     * Marks the last integer id for the range of action event ids.
     */
    public static final int ACTION_LAST		        = 1001;

    /**
     * An action performed event type.
     */
    public static final int ACTION_PERFORMED	= ACTION_FIRST; //Event.ACTION_EVENT

    String actionCommand;
    int modifiers;

    /*
     * JDK 1.1 serialVersionUID 
     */
    private static final long serialVersionUID = -7671078796273832149L;

    /**
     * Constructs an ActionEvent object with the specified source object.
     * @param source the object where the event originated
     * @param id the type of event
     * @param command the command string for this action event
     */
    public ActionEvent(Object source, int id, String command) {
        this(source, id, command, 0);
    }

    /**
     * Constructs an ActionEvent object with the specified source object.
     * @param source the object where the event originated
     * @param id the type of event
     * @param command the command string for this action event
     * @param modifiers the modifiers held down during this action
     */
    public ActionEvent(Object source, int id, String command, int modifiers) {
        super(source, id);
        this.actionCommand = command;
        this.modifiers = modifiers;
    }

    /**
     * Returns the command name associated with this action.
     */
    public String getActionCommand() {
        return actionCommand;
    }

    /**
     * Returns the modifiers held down during this action event.
     */
    public int getModifiers() {
        return modifiers;
    }

    public String paramString() {
        String typeStr;
        switch(id) {
          case ACTION_PERFORMED:
              typeStr = "ACTION_PERFORMED";
              break;
          default:
              typeStr = "unknown type";
        }
        return typeStr + ",cmd="+actionCommand;
    }

}
