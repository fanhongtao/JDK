/*
 * @(#)ListDataListener.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.swing.event;

import java.util.EventListener;

/**
 * ListDataListener
 *
 * @version 1.7 09/21/98
 * @author Hans Muller
 */
public interface ListDataListener extends EventListener {

    /** 
     * Sent after the indices in the index0,index1 
     * interval have been inserted in the data model.
     * The new interval includes both index0 and index1.
     *
     * @param e  a ListDataEvent encapuslating the event information
     */
    void intervalAdded(ListDataEvent e);

    
    /**
     * Sent after the indices in the index0,index1 interval
     * have been removed from the data model.  The interval 
     * includes both index0 and index1.
     *
     * @param e  a ListDataEvent encapuslating the event information
     */
    void intervalRemoved(ListDataEvent e);


    /** 
     * Sent when the contents of the list has changed in a way 
     * that's too complex to characterize with the previous 
     * methods.  Index0 and index1 bracket the change.
     *
     * @param e  a ListDataEvent encapuslating the event information
     */
    void contentsChanged(ListDataEvent e);
}

