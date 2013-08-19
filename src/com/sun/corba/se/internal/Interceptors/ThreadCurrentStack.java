/*
 * @(#)ThreadCurrentStack.java	1.8 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.Interceptors;

import com.sun.corba.se.internal.corba.AnyImpl;
import org.omg.PortableInterceptor.Current;
import org.omg.PortableInterceptor.InvalidSlot;

import com.sun.corba.se.internal.util.MinorCodes;

/**
 * ThreadCurrentStack is the container of PICurrent instances for each thread
 */
public class ThreadCurrentStack
{
    // PICurrentPool is the container for reusable PICurrents
    private class PICurrentPool {

        // Contains a list of reusable PICurrents
        private java.util.ArrayList pool;

        // High water mark for the pool
        // If the pool size reaches this limit then putPICurrent will
        // not put PICurrent to the pool.
        private static final int  HIGH_WATER_MARK = 5;

        // currentIndex points to the last PICurrent in the list
        private int currentIndex;

        PICurrentPool( ) {
            pool = new java.util.ArrayList( HIGH_WATER_MARK );
            currentIndex = 0;
        }

        /**
         * Puts PICurrent to the re-usable pool.
         */
        void putPICurrent( PICurrent current ) {
            // If there are enough PICurrents in the pool, then don't add
            // this current to the pool.
            if( currentIndex >= HIGH_WATER_MARK ) {
                return;
            }
            pool.add(currentIndex , current);
            currentIndex++;
        }

        /**
         * Gets PICurrent from the re-usable pool.
         */
        PICurrent getPICurrent( ) {
            // If there are no entries in the pool then return null
            if( currentIndex == 0 ) {
                return null;
            }
            // Works like a stack, Gets the last one added first
            currentIndex--;
            return (PICurrent) pool.get(currentIndex);
        }
    }
   
    // Contains all the active PICurrents for each thread.
    // The ArrayList is made to behave like a stack.
    private java.util.ArrayList currentContainer;

    // Keeps track of number of PICurrents in the stack.
    private int currentIndex;
 
    // For Every Thread there will be a pool of re-usable ThreadCurrent's
    // stored in PICurrentPool
    private PICurrentPool currentPool;

    // The orb associated with this ThreadCurrentStack
    private PIORB piOrb;

    /**
     * Constructs the stack and and PICurrentPool
     */
    ThreadCurrentStack( PIORB piOrb, PICurrent current ) {
       this.piOrb = piOrb;
       currentIndex = 0;
       currentContainer = new java.util.ArrayList( );
       currentPool = new PICurrentPool( );
       currentContainer.add( currentIndex, current );
       currentIndex++;
    }
   

    /**
     * pushPICurrent goes through the following steps
     * 1: Checks to see if there is any PICurrent in PICurrentPool
     *    If present then use that instance to push into the ThreadCurrentStack
     *
     * 2:If there is no PICurrent in the pool, then creates a new one and pushes
     *    that into the ThreadCurrentStack
     */
    void pushPICurrent( ) {
        PICurrent current = currentPool.getPICurrent( );
        if( current == null ) {
            // get an existing PICurrent to get the slotSize
            PICurrent currentTemp = peekPICurrent();
            current = new PICurrent( piOrb, currentTemp.getSlotSize( ));
        }
        currentContainer.add( currentIndex, current );
        currentIndex++;
    }

    /**
     * popPICurrent does the following
     * 1: pops the top PICurrent in the ThreadCurrentStack
     *
     * 2: resets the slots in the PICurrent which resets the slotvalues to
     *    null if there are any previous sets. 
     *
     * 3: pushes the reset PICurrent into the PICurrentPool to reuse 
     */
    void  popPICurrent( ) {
        // Do not pop the PICurrent, If there is only one.
        // This should not happen, But an extra check for safety.
        if( currentIndex <= 1 ) {
            throw new org.omg.CORBA.INTERNAL( 
                      "Cannot pop the only PICurrent in the stack",
		      MinorCodes.CANT_POP_ONLY_CURRENT_2,
		      CompletionStatus.COMPLETED_NO );
        }
        currentIndex--;
        PICurrent current = (PICurrent)currentContainer.get( currentIndex );
        current.resetSlots( );
        currentPool.putPICurrent( current );
    }

    /**
     * peekPICurrent gets the top PICurrent in the ThreadCurrentStack without
     * popping.
     */
    PICurrent peekPICurrent( ) {
       return (PICurrent) currentContainer.get( currentIndex - 1);
    }

}
