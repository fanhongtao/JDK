/*
 * @(#)LightweightPeer.java	1.2 00/01/12
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
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
