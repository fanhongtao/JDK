/*
 * @(#)ListSelectionListener.java	1.2 00/01/12
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
 * The listener that's notified when a lists selection value 
 * changes.
 *
 * @see javax.swing.ListSelectionModel
 * 
 * @version 1.7 09/21/98
 * @author Hans Muller
 */

public interface ListSelectionListener extends EventListener
{
  /** 
   * Called whenever the value of the selection changes.
   * @param e the event that characterizes the change.
   */
  void valueChanged(ListSelectionEvent e);
}


