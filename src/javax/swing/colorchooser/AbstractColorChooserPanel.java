/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.colorchooser;

import java.awt.*;
import java.io.Serializable;
import javax.swing.*;
import javax.swing.event.*;

/**
 * This is the abstract superclass for color choosers.  If you want to add
 * a new color chooser panel into a <code>JColorChooser</code>, subclass
 * this class.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.14 02/06/02
 * @author Tom Santos
 * @author Steve Wilson
 */
public abstract class AbstractColorChooserPanel extends JPanel {

    /**
     * 
     */
    private JColorChooser chooser;

    /**
     * 
     */
    private ChangeListener colorListener;

    /**
     * 
     */
    private boolean dirty  = true;


    /**
      * Invoked automatically when the model's state changes.
      * It is also called by <code>installChooserPanel</code> to allow
      * you to set up the initial state of your chooser.
      * Override this method to update your <code>ChooserPanel</code>.
      */
    public abstract void updateChooser();

    /**
     * Builds a new chooser panel.
     */
    protected abstract void buildChooser();

    /**
     * Returns a string containing the display name of the panel.
     * @return the name of the display panel
     */
    public abstract String getDisplayName();

    /**
     * Returns the large display icon for the panel.
     * @return the large display icon
     */
    public abstract Icon getSmallDisplayIcon();

    /**
     * Returns the small display icon for the panel.
     * @return the small display icon
     */
    public abstract Icon getLargeDisplayIcon();

    /**
     * Invoked when the panel is added to the chooser.
     * If you're going to override this, be sure to call super.
     * @param enclosingChooser  the panel to be added
     * @exception RuntimeException  if the chooser panel has already been
     *				installed
     */
    public void installChooserPanel(JColorChooser enclosingChooser) {
        if (chooser != null) {
	    throw new RuntimeException ("This chooser panel is already installed");
        }
        chooser = enclosingChooser;
	buildChooser();
	updateChooser();
	colorListener = new ModelListener();
	getColorSelectionModel().addChangeListener(colorListener);
    }

    /**
     * Invoked when the panel is removed from the chooser.
     * If you're going to override this, be sure to call super.
     */
  public void uninstallChooserPanel(JColorChooser enclosingChooser) {
        getColorSelectionModel().removeChangeListener(colorListener);
        chooser = null;
    }

    /**
      * Returns the model that the chooser panel is editing.
      * @return the <code>ColorSelectionModel</code> model this panel
      *		is editing
      */
    public ColorSelectionModel getColorSelectionModel() {
        return chooser.getSelectionModel();
    }

    /**
     * Returns the color that is currently selected.
     * @return the <code>Color</code> that is selected
     */
    protected Color getColorFromModel() {
        return getColorSelectionModel().getSelectedColor();
    }

    /**
     * Draws the panel. 
     * @param g  the <code>Graphics</code> object
     */
    public void paint(Graphics g) {
	if (dirty) {
	    updateChooser();
	    dirty = false;
	}
        super.paint(g);
    }

    /**
     * 
     */
    class ModelListener implements ChangeListener, Serializable {
        public void stateChanged(ChangeEvent e) {
	  if (isShowing()) {  // isVisible
	        updateChooser();
		dirty = false;
	    } else {
	        dirty = true;
	    }
	}
    }
}
