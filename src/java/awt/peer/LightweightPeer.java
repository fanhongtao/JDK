/*
 * @(#)LightweightPeer.java	1.3 98/07/01
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

package java.awt.peer;

/**
 * The LightweightPeer interface marks a component as depending upon 
 * a native container so window related events can be routed to the 
 * component.  Since this only applies to components and their 
 * extensions, this interface extends ComponentPeer.
 *
 * @version 1.3 07/01/98
 * @author Timothy Prinzing
 */
public interface LightweightPeer extends ComponentPeer {

}
