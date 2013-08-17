/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf;

import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;

/**
 * Pluggable look and feel interface for JPopupMenu.
 *
 * @version 1.12 02/06/02
 * @author Georges Saab
 * @author David Karlton
 */

public abstract class PopupMenuUI extends ComponentUI {
    public boolean isPopupTrigger(MouseEvent e) {
	return e.isPopupTrigger();
    }
}

