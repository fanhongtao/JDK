/*
 * @(#)LightweightPeer.java	1.6 98/06/29
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

package java.awt.peer;

/**
 * The LightweightPeer interface marks a component as depending upon
 * a native container so window related events can be routed to the
 * component.  Since this only applies to components and their
 * extensions, this interface extends ComponentPeer.
 * <p>
 * The peer interfaces are intended only for use in porting
 * the AWT. They are not intended for use by application
 * developers, and developers should not implement peers
 * nor invoke any of the peer methods directly on the peer
 * instances.
 *
 * @version 1.6 06/29/98
 * @author Timothy Prinzing
 */
public interface LightweightPeer extends ComponentPeer {

}
