/*
 * @(#)InvalidDnDOperationException.java	1.3 98/09/21
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.awt.dnd;

/**
 * <p>
 * This exception is thrown by various methods in the java.awt.dnd package.
 * It is usually thrown to indicate that the target in question is unable
 * to undertake the requested operation that the present time, since the
 * undrelying DnD system is not in the appropriate state.
 * </p>
 *
 * @version 1.3
 * @since JDK1.2
 *
 */

public class InvalidDnDOperationException extends IllegalStateException {
    
    static private String dft_msg = "The operation requested cannot be performed by the DnD system since it is not in the appropriate state";

    /**
     * Create a default Exception
     */

    public InvalidDnDOperationException() { super(dft_msg); }

    /**
     * Create an Exception with its own descriptive message
     *
     * @param msg the detail message
     */

    public InvalidDnDOperationException(String msg) { super(msg); }

}
