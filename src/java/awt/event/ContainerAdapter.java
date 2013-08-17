/*
 * @(#)ContainerAdapter.java	1.3 98/07/01
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

/**
 * The adapter which receives container events.
 * The methods in this class are empty;  this class is provided as a
 * convenience for easily creating listeners by extending this class
 * and overriding only the methods of interest.
 *
 * @version 1.3 07/01/98
 * @author Amy Fowler
 */
public abstract class ContainerAdapter implements ContainerListener {
    public void componentAdded(ContainerEvent e) {}
    public void componentRemoved(ContainerEvent e) {}
}
