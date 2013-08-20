/*
 * @(#)OptionPaneUI.java	1.12 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf;

import javax.swing.JOptionPane;

/**
 * Pluggable look and feel interface for JOptionPane.
 *
 * @version 1.12 12/19/03
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
