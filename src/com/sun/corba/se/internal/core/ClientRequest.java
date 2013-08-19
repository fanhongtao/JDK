/*
 * @(#)ClientRequest.java	1.22 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.internal.core;

/**
 * ClientRequest represents a client-side method invocation request.
 */
public interface ClientRequest extends MarshalOutputStream, Request {
    /**
     * Process this invocation request.
     */
    public abstract ClientResponse invoke();

    /**
     * Resend the invocation to the IOR in the iorHolder, after a location
     * forward.
     * iorHolder must be an array of size 1.
     * 
     * This methods reinvokes until it reaches 
     * a non-location-forward response.
     * 
     * Puts any subsequent forwarded IOR in the iorHolder.
     * Returns the ClientResponse as in invoke() above.
     */
    /* FIX(Ram J) (04/29/2000) not required anymore.
    public abstract ClientResponse reInvoke(IOR[] iorHolder);
    */

    /**
     * Resend the invocation to the IOR in the iorHolder, after a location
     * forward.
     * iorHolder must be an array of size 1.
     *
     * This methods reinvokes only once.
     *
     * Puts any subsequent forwarded IOR in the iorHolder.
     * Returns the ClientResponse as in invoke() above.
     */
    /* FIX(Ram J) (04/29/2000) not required anymore.
    public abstract ClientResponse reInvokeOnce(IOR[] iorHolder);
    */
    
    /**
     * Extracts body.
     */
    /* FIX(Ram J) (04/29/2000) not required anymore.
    public byte[] getBody();
    */

}



	
