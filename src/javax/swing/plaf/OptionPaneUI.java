/*
 * @(#)OptionPaneUI.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.swing.plaf;

import javax.swing.JOptionPane;

/**
 * Pluggable look and feel interface for JOptionPane.
 *
 * @version 1.7 08/26/98
 * @author Scott Violet
 */

public abstract class OptionPaneUI extends ComponentUI
{
    /**
     * Requests the component representing the default value to have
     * focus.
     */
    public abstract void selectInitialValue(JOptionPane op);

    /**
     * Returns true if the user has supplied instances of Component for
     * either the options or message.
     */
    public abstract boolean containsCustomComponents(JOptionPane op);
}
