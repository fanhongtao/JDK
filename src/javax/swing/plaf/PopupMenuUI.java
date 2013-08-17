/*
 * @(#)PopupMenuUI.java	1.11 00/02/02
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.swing.plaf;

import java.awt.event.MouseEvent;
import javax.swing.JPopupMenu;

/**
 * Pluggable look and feel interface for JPopupMenu.
 *
 * @version 1.11 02/02/00
 * @author Georges Saab
 * @author David Karlton
 */

public abstract class PopupMenuUI extends ComponentUI {
    public boolean isPopupTrigger(MouseEvent e) {
	return e.isPopupTrigger();
    }
}

