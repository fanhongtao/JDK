/*
 * @(#)ItemEvent.java	1.15 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.event;

import java.awt.Component;
import java.awt.AWTEvent;
import java.awt.Event;
import java.awt.ItemSelectable;

/**
 * The item event emitted by ItemSelectable objects.
 * This event is generated when an item is selected or de-selected.
 * @see java.awt.ItemSelectable
 * @see ItemListener
 *
 * @version 1.15 12/10/01
 * @author Carl Quinn
 */
public class ItemEvent extends AWTEvent {

    /**
     * Marks the first integer id for the range of item event ids.
     */
    public static final int ITEM_FIRST		= 701;

    /**
     * Marks the last integer id for the range of item event ids.
     */
    public static final int ITEM_LAST		= 701;

    /** 
     * The item state changed event type.
     */
    public static final int ITEM_STATE_CHANGED	= ITEM_FIRST; //Event.LIST_SELECT 

    /**
     * The item selected state change type.
     */
    public static final int SELECTED = 1;

    /** 
     * The item de-selected state change type.
     */
    public static final int DESELECTED	= 2;

    Object item;
    int stateChange;

    /*
     * JDK 1.1 serialVersionUID 
     */
    private static final long serialVersionUID = -608708132447206933L;

    /**
     * Constructs a ItemSelectEvent object with the specified ItemSelectable source,
     * type, item, and item select state.
     * @param source the ItemSelectable object where the event originated
     * @id the event type
     * @item the item where the event occurred
     * @stateChange the state change type which caused the event
     */
    public ItemEvent(ItemSelectable source, int id, Object item, int stateChange) {
        super(source, id);
	this.item = item;
        this.stateChange = stateChange;
    }

    /**
     * Returns the ItemSelectable object where this event originated.
     */
    public ItemSelectable getItemSelectable() {
        return (ItemSelectable)source;
    }

   /**
    * Returns the item where the event occurred.
    */
    public Object getItem() {
        return item;
    }

   /**
    * Returns the state change type which generated the event.
    * @see #SELECTED
    * @see #DESELECTED
    */
    public int getStateChange() {
        return stateChange;
    }

    public String paramString() {
        String typeStr;
        switch(id) {
          case ITEM_STATE_CHANGED:
              typeStr = "ITEM_STATE_CHANGED";
              break;
          default:
              typeStr = "unknown type";
        }

        String stateStr;
        switch(stateChange) {
          case SELECTED:
              stateStr = "SELECTED";
              break;
          case DESELECTED:
              stateStr = "DESELECTED";
              break;
          default:
              stateStr = "unknown type";
        }
        return typeStr + ",item="+item + ",stateChange="+stateStr;
    }

}
