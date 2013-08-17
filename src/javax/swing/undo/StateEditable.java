/*
 * @(#)StateEditable.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.swing.undo;

import java.util.Hashtable;


/**
 * StateEditable defines the interface for objects that can have
 * their state undone/redone by a StateEdit.
 *
 * @see StateEdit
 */

public interface StateEditable {

    /** Resource ID for this class. */
    public static final String RCSID = "$Id: StateEditable.java,v 1.1.1.1 1998/10/28 06:43:54 sbb Exp $";

    /**
     * Upon receiving this message the receiver should place any relevant
     * state into <EM>state</EM>.
     */
    public void storeState(Hashtable state);

    /**
     * Upon receiving this message the receiver should extract any relevant
     * state out of <EM>state</EM>.
     */
    public void restoreState(Hashtable state);
} // End of interface StateEditable
