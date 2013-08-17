/*
 * @(#)ListSelectionListener.java	1.9 00/02/02
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
 * @version 1.9 02/02/00
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


