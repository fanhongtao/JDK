/*
 * @(#)LightweightPeer.java	1.4 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.peer;

/**
 * The LightweightPeer interface marks a component as depending upon 
 * a native container so window related events can be routed to the 
 * component.  Since this only applies to components and their 
 * extensions, this interface extends ComponentPeer.
 *
 * @version 1.4 12/10/01
 * @author Timothy Prinzing
 */
public interface LightweightPeer extends ComponentPeer {

}
