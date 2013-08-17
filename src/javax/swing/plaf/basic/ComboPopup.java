/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.basic;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.KeyListener;
import javax.swing.JList;


/**
 * The interface which defines the kind of popup menu that BasicComboBoxUI requires.
 * Classes that implement this interface don't have to extend JPopupMenu.  This interface
 * demands very little so alternatives to JPopupMenu can be used.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.10 02/06/02
 * @author Tom Santos
 */
public interface ComboPopup {
    /**
     * Shows the popup
     */
    public void show();

    /**
     * Hides the popup
     */
    public void hide();

    /**
     * Returns whether or not the popup is visible
     */
    public boolean isVisible();

    /**
     * Returns the list that is being used to draw the items in the JComboBox.
     */
    public JList getList();

    /**
     * Returns a mouse listener that shows and hides the popup.
     */
    public MouseListener getMouseListener();

    /**
     * Returns a mouse motion listener that makes the popup act like a menu.
     */
    public MouseMotionListener getMouseMotionListener();

    /**
     * Returns a key listener that shows and hides the popup.
     */
    public KeyListener getKeyListener();

    /**
     * Called to inform the ComboPopup that the UI is uninstalling.
     * If the ComboPopup added any listeners in the component, it should remove them here.
     */
    public void uninstallingUI();
}
