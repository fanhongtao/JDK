/*
 * @(#)AbstractColorChooserPanel.java	1.2 00/01/12
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.swing.colorchooser;

import java.awt.*;
import java.io.Serializable;
import javax.swing.*;
import javax.swing.event.*;

/**
 * The is the abstract superclass for color choosers.  If you want to add a new color chooser
 * panel into a JColorChooser, sublclass this class.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.10 08/28/98
 * @author Tom Santos
 * @author Steve Wilson
 */
public abstract class AbstractColorChooserPanel extends JPanel {

    private JColorChooser chooser;
    private ChangeListener colorListener;
    private boolean dirty  = true;


    /**
      * override this method to update your ChooserPanel
      * This method will be automatically called when the model's state
      * changes.
      * It is also called by installChooserPanel to allow you to set up
      * the initial state of your chooser
      */
    public abstract void updateChooser();

    protected abstract void buildChooser();

    public abstract String getDisplayName();

    public abstract Icon getSmallDisplayIcon();

    public abstract Icon getLargeDisplayIcon();

    /**
     * This get called when the panel is added to the chooser.
     *
     * if you're going to override this, be sure to call super.
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
     * This get called when the panel is removed from the chooser.
     *
     * if you're going to override this, be sure to call super.
     */
  public void uninstallChooserPanel(JColorChooser enclosingChooser) {
        getColorSelectionModel().removeChangeListener(colorListener);
        chooser = null;
    }

    /**
      * @return The model this panel is editing
      */
    public ColorSelectionModel getColorSelectionModel() {
        return chooser.getSelectionModel();
    }

    protected Color getColorFromModel() {
        return getColorSelectionModel().getSelectedColor();
    }

    public void paint(Graphics g) {
	if (dirty) {
	    updateChooser();
	    dirty = false;
	}
        super.paint(g);
    }

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
