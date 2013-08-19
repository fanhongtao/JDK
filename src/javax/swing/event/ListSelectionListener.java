/*
 * @(#)ListSelectionListener.java	1.11 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.event;


import java.util.EventListener;


/** 
 * The listener that's notified when a lists selection value 
 * changes.
 *
 * @see javax.swing.ListSelectionModel
 * 
 * @version 1.11 01/23/03
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


