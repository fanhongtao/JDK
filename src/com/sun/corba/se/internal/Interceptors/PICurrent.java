/*
 * @(#)PICurrent.java	1.12 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.Interceptors;

import com.sun.corba.se.internal.corba.AnyImpl;
import org.omg.PortableInterceptor.Current;
import org.omg.PortableInterceptor.InvalidSlot;
import org.omg.CORBA.Any;
import org.omg.CORBA.BAD_INV_ORDER;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.ORB;

/**
 * PICurrent is the implementation of Current as specified in the Portable
 * Interceptors Spec orbos/99-12-02.
 * IMPORTANT: PICurrent is implemented with the assumption that get_slot()
 * or set_slot() will not be called in ORBInitializer.pre_init() and 
 * post_init().
 */
public class PICurrent extends org.omg.CORBA.LocalObject
    implements Current
{
    // slotCounter is used to keep track of ORBInitInfo.allocate_slot_id()
    private int slotCounter;

    // The PIORB associated with this PICurrent object.
    private PIORB piOrb;

    // True if the orb is still initialzing and get_slot and set_slot are not
    // to be called.
    private boolean orbInitializing;

    // ThreadLocal contains a stack of SlotTable which are used
    // for resolve_initial_references( "PICurrent" );
    private ThreadLocal threadLocalSlotTable
        = new ThreadLocal( ) {
            protected Object initialValue( ) {
                SlotTable table = new SlotTable( piOrb, slotCounter );
                return new SlotTableStack( piOrb, table );
            }
        };

    /**
     * PICurrent constructor which will be called for every PIORB 
     * initialization.
     */
    PICurrent( PIORB piOrb ) {
	this.piOrb = piOrb;
	this.orbInitializing = true;
        slotCounter = 0;
    }


    /**
     * This method will be called from ORBInitInfo.allocate_slot_id( ).
     * simply returns a slot id by incrementing slotCounter.
     */
    int allocateSlotId( ) {
        int slotId = slotCounter;
        slotCounter = slotCounter + 1;
        return slotId;
    }


    /**
     * This method gets the SlotTable which is on the top of the
     * ThreadLocalStack.
     */
    SlotTable getSlotTable( ) {
        SlotTable table = (SlotTable)
                ((SlotTableStack)threadLocalSlotTable.get()).peekSlotTable();
        return table;
    }

    /**
     * This method pushes a SlotTable on the SlotTableStack. When there is
     * a resolve_initial_references("PICurrent") after this call. The new
     * PICurrent will be returned.
     */
    void pushSlotTable( ) {
        SlotTableStack st = (SlotTableStack)threadLocalSlotTable.get();
        st.pushSlotTable( );
    }


    /**
     * This method pops a SlotTable on the SlotTableStack.
     */
    void popSlotTable( ) {
        SlotTableStack st = (SlotTableStack)threadLocalSlotTable.get();
        st.popSlotTable( );
    }

    /**
     * This method sets the slot data at the given slot id (index) in the
     * Slot Table which is on the top of the SlotTableStack.
     */
    public void set_slot( int id, Any data ) throws InvalidSlot 
    {
	if( orbInitializing ) {
	    // As per ptc/00-08-06 if the ORB is still initializing, disallow
	    // calls to get_slot and set_slot.  If an attempt is made to call,
	    // throw a BAD_INV_ORDER.
	    throw new BAD_INV_ORDER( 
		"Cannot call set_slot from within ORBInitializer.",
		MinorCodes.INVALID_PI_CALL, CompletionStatus.COMPLETED_NO );
	}

        getSlotTable().set_slot( id, data );
    }

    /**
     * This method gets the slot data at the given slot id (index) from the
     * Slot Table which is on the top of the SlotTableStack.
     */
    public Any get_slot( int id ) throws InvalidSlot 
    {
	if( orbInitializing ) {
	    // As per ptc/00-08-06 if the ORB is still initializing, disallow
	    // calls to get_slot and set_slot.  If an attempt is made to call,
	    // throw a BAD_INV_ORDER.
	    throw new BAD_INV_ORDER( 
		"Cannot call get_slot from within ORBInitializer.",
		MinorCodes.INVALID_PI_CALL, CompletionStatus.COMPLETED_NO );
	}

        return getSlotTable().get_slot( id );
    }

    /**
     * This method resets all the slot data to null in the  
     * Slot Table which is on the top of SlotTableStack.
     */
    void resetSlotTable( ) {
        getSlotTable().resetSlots();
    }

    /**
     * Called from PIORB when the ORBInitializers are about to start 
     * initializing.
     */
    void setORBInitializing( boolean init ) {
	this.orbInitializing = init;
    }
}


    
