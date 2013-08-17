/*
 * @(#)ListModel.java	1.11 98/08/26
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package javax.swing;

import javax.swing.event.ListDataListener;

/**
 * This interface defines the methods components like JList use 
 * to get the value of each cell in a list and the length of the list.
 * Logically the model is a vector, indices vary from 0 to
 * ListDataModel.getSize() - 1.  Any change to the contents or
 * length of the data model must be reported to all of the
 * ListDataListeners.
 *
 * @version 0.0 03/01/97
 * @author Hans Muller
 * @see JList
 */
public interface ListModel
{
  /** 
   * Returns the length of the list.
   */
  int getSize();

  /**
   * Returns the value at the specified index.  
   */
  Object getElementAt(int index);

  /**
   * Add a listener to the list that's notified each time a change
   * to the data model occurs.
   * @param l the ListDataListener
   */  
  void addListDataListener(ListDataListener l);

  /**
   * Remove a listener from the list that's notified each time a 
   * change to the data model occurs.
   * @param l the ListDataListener
   */  
  void removeListDataListener(ListDataListener l);
}

