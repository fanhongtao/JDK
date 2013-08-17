/*
 * @(#)ComboBoxModel.java	1.7 98/08/26
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

/**
 * A data model for JComboBox
 *
 * @version 1.7 08/26/98
 * @author Arnaud Weber
 */

/** ComboBoxDataModel is a ListDataModel with a selected item
  * This selected item is in the model since it is not
  * always in the item list.
  */
public interface ComboBoxModel extends ListModel {
  /** Set the selected item **/
  void setSelectedItem(Object anItem);

  /** Return the selected item **/
  Object getSelectedItem();
}

