/*
 * @(#)SlotTable.java	1.7 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.Interceptors;

import com.sun.corba.se.internal.corba.AnyImpl;
import org.omg.PortableInterceptor.Current;
import org.omg.PortableInterceptor.InvalidSlot;
import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;

import com.sun.corba.se.internal.util.MinorCodes;

/**
 * SlotTable is used internally by PICurrent to store the slot information.
 */
public class SlotTable {
    // The vector where all the slot data for the current thread is stored
    private Any[] theSlotData;

    // Required for instantiating Any object.
    private ORB orb;

    // The flag to check whether there are any updates in the current SlotTable.
    // The slots will be reset to null, only if this flag is set.
    private boolean dirtyFlag;

    /**
     * The constructor instantiates an Array of Any[] of size given by slotSize
     * parameter.
     */
    SlotTable( PIORB piOrb, int slotSize ) {
        dirtyFlag = false;
        orb = piOrb;
        theSlotData = new Any[slotSize];
    }

    /**
     * This method sets the slot data at the given slot id (index).
     */
    public void set_slot( int id, Any data ) throws InvalidSlot
    {
        // First check whether the slot is allocated
        // If not, raise the invalid slot exception
        if( id >= theSlotData.length ) {
            throw new InvalidSlot();
        }
        dirtyFlag = true;
        theSlotData[id] = data;
    }

    /**
     * This method get the slot data for the given slot id (index).
     */
    public Any get_slot( int id ) throws InvalidSlot
    {
        // First check whether the slot is allocated
        // If not, raise the invalid slot exception
        if( id >= theSlotData.length ) {
            throw new InvalidSlot();
        }
        if( theSlotData[id] == null ) {
            theSlotData [id] = new AnyImpl(orb);
        }
        return theSlotData[ id ];
    }


    /**
     * This method resets all the slot data to null if dirtyFlag is set.
     */
    void resetSlots( ) {
        if( dirtyFlag == true ) {
            for( int i = 0; i < theSlotData.length; i++ ) {
                theSlotData[i] = null;
            }
        }
    }

    /**
     * This method returns the size of the allocated slots.
     */
    int getSize( ) {
        return theSlotData.length;
    }

}
    
