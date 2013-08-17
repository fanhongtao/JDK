/*
 * @(#)OptionComboBoxModel.java	1.3 98/08/26
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
package javax.swing.text.html;

import javax.swing.*;
import javax.swing.event.*;
import java.io.Serializable;


/**
 * OptionComboBoxModel extends the capabilities of the DefaultComboBoxModel,
 * to store the Option that is initially marked as selected.
 * This is stored, in order to enable an accurate reset of the 
 * ComboBox that represents the SELECT form element when the
 * user requests a clear/reset.  Given that a combobox only allow
 * for one item to be selected, the last OPTION that has the
 * attribute set wins.
 *
  @author Sunita Mani
  @version 1.3 08/26/98
 */

class OptionComboBoxModel extends DefaultComboBoxModel implements Serializable {

    private Option selectedOption = null;

    /**
     * Stores the Option that has been marked its
     * selected attribute set.
     */
    public void setInitialSelection(Option option) {
	selectedOption = option;
    }

    /**
     * Fetches the Option item that represents that was
     * initially set to a selected state.
     */
    public Option getInitialSelection() {
	return selectedOption;
    }
}
