/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing;

/**
 * A data model for JComboBox
 *
 * @version 1.10 02/06/02
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

