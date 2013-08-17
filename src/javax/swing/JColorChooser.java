/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.util.*;

import javax.swing.colorchooser.*;
import javax.swing.plaf.ColorChooserUI;
import javax.swing.event.*;
import javax.accessibility.*;


/**
 * <code>JColorChooser</code> provides a pane of controls designed to allow
 * a user to manipulate and select a color.
 * For information about using color choosers, see
 * <a
 href="http://java.sun.com/docs/books/tutorial/uiswing/components/colorchooser.html">How to Use Color Choosers</a>,
 * a section in <em>The Java Tutorial</em>.
 *
 * <p>
 *
 * This class provides three levels of API:
 * <ol>
 * <li>A static convenience method which shows a modal color-chooser
 * dialog and returns the color selected by the user.
 * <li>A static convenience method for creating a color-chooser dialog
 * where <code>ActionListeners</code> can be specified to be invoked when
 * the user presses one of the dialog buttons.
 * <li>The ability to create instances of <code>JColorChooser</code> panes
 * directly (within any container). <code>PropertyChange</code> listeners
 * can be added to detect when the current "color" property changes.
 * </ol>
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 *
 * @beaninfo
 *      attribute: isContainer false
 *    description: A component that supports selecting a Color.
 *
 *
 * @version 1.31 02/06/02
 * @author James Gosling
 * @author Amy Fowler
 * @author Steve Wilson
 */
public class JColorChooser extends JComponent implements Accessible {

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "ColorChooserUI";

    private ColorSelectionModel selectionModel;

    private JComponent previewPanel;

    private AbstractColorChooserPanel[] chooserPanels = new AbstractColorChooserPanel[0];



    /**
     * The selection model property name.
     */
    public static final String      SELECTION_MODEL_PROPERTY = "selectionModel";

    /**
     * The preview panel property name.
     */
    public static final String      PREVIEW_PANEL_PROPERTY = "previewPanel";

    /**
     * The chooserPanel array property name.
     */
    public static final String      CHOOSER_PANELS_PROPERTY = "chooserPanels";


    /**
     * Shows a modal color-chooser dialog and blocks until the
     * dialog is hidden.  If the user presses the "OK" button, then
     * this method hides/disposes the dialog and returns the selected color.
     * If the user presses the "Cancel" button or closes the dialog without
     * pressing "OK", then this method hides/disposes the dialog and returns
     * <code>null</code>.
     *
     * @param component    the parent <code>Component</code> for the dialog
     * @param title        the String containing the dialog's title
     * @param initialColor the initial Color set when the color-chooser is shown
     */
    public static Color showDialog(Component component,
                                   String title, Color initialColor) {

        final JColorChooser pane = new JColorChooser(initialColor != null?
                                               initialColor : Color.white);

        ColorTracker ok = new ColorTracker(pane);
        JDialog dialog = createDialog(component, title, true, pane, ok, null);
        dialog.addWindowListener(new ColorChooserDialog.Closer());
        dialog.addComponentListener(new ColorChooserDialog.DisposeOnClose());

        dialog.show(); // blocks until user brings dialog down...

        return ok.getColor();
    }


    /**
     * Creates and returns a new dialog containing the specified
     * <code>ColorChooser</code>` pane along with "OK", "Cancel", and "Reset"
     * buttons. If the "OK" or "Cancel" buttons are pressed, the dialog is
     * automatically hidden (but not disposed).  If the "Reset"
     * button is pressed, the color-chooser's color will be reset to the
     * color which was set the last time <code>show</code> was invoked on the
     * dialog and the dialog will remain showing.
     *
     * @param c              the parent component for the dialog
     * @param title          the title for the dialog
     * @param modal          a boolean. When true, the remainder of the program
     *                       is inactive until the dialog is closed.
     * @param chooserPane    the color-chooser to be placed inside the dialog
     * @param okListener     the ActionListener invoked when "OK" is pressed
     * @param cancelListener the ActionListener invoked when "Cancel" is pressed
     */
    public static JDialog createDialog(Component c, String title, boolean modal,
                                       JColorChooser chooserPane,
                                       ActionListener okListener,
                                       ActionListener cancelListener) {

        return new ColorChooserDialog(c, title, modal, chooserPane,
                                                okListener, cancelListener);
    }


    /**
     * Creates a color chooser pane with an initial color of white.
     */
    public JColorChooser() {
        this(Color.white);
    }

    /**
     * Creates a color chooser pane with the specified initial color.
     *
     * @param initialColor the initial color set in the chooser
     */
    public JColorChooser(Color initialColor) {
	this( new DefaultColorSelectionModel(initialColor) );

    }

    /**
     * Creates a color chooser pane with the specified
     * <code>ColorSelectionModel</code>.
     *
     * @param initialColor the initial color set in the chooser
     */
    public JColorChooser(ColorSelectionModel model) {
	selectionModel = model;
        updateUI();

    }

    /**
     * Returns the L&F object that renders this component.
     *
     * @return the ColorChooserUI object that renders this component
     */
    public ColorChooserUI getUI() {
        return (ColorChooserUI)ui;
    }

    /**
     * Sets the L&F object that renders this component.
     *
     * @param ui  the ColorChooserUI L&F object
     * @see UIDefaults#getUI
     *
     * @beaninfo
     *       bound: true
     *      hidden: true
     * description: The UI object that implements the color chooser's LookAndFeel.
     */
    public void setUI(ColorChooserUI ui) {
        super.setUI(ui);
    }

    /**
     * Notification from the <code>UIManager</code> that the L&F has changed.
     * Replaces the current UI object with the latest version from the
     * <code>UIManager</code>.
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        setUI((ColorChooserUI)UIManager.getUI(this));
    }

    /**
     * Returns the name of the L&F class that renders this component.
     *
     * @return the string "ColorChooserUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }

    /**
     * Gets the current color value from the color chooser.
     * By default, this delegates to the model.
     *
     * @return the current color value of the color chooser
     */
    public Color getColor() {
        return selectionModel.getSelectedColor();
    }

    /**
     * Sets the current color of the color chooser to the
     * specified color.
     * This will fire a <code>PropertyChangeEvent</code> for the property
     * named "color".
     *
     * @param color the color to be set in the color chooser
     * @see JComponent#addPropertyChangeListener
     *
     * @beaninfo
     *       bound: false
     *      hidden: false
     * description: The current color the chooser is to display.
     */
    public void setColor(Color color) {
        selectionModel.setSelectedColor(color);

    }

    /**
     * Sets the current color of the color chooser to the
     * specified RGB color.  Note that the values of red, green,
     * and blue should be between the numbers 0 and 255, inclusive.
     *
     * @param r   an int specifying the amount of Red
     * @param g   an int specifying the amount of Green
     * @param b   an int specifying the amount of Blue
     * @exception IllegalArgumentException if r,g,b values are out of range
     * @see java.awt.Color
     */
    public void setColor(int r, int g, int b) {
        setColor(new Color(r,g,b));
    }

    /**
     * Sets the current color of the color chooser to the
     * specified color.
     *
     * @param c an int value that sets the current color in the chooser
     *          where the low-order 8 bits specify the Blue value,
     *          the next 8 bits specify the Green value, and the 8 bits
     *          above that specify the Red value.
     */
    public void setColor(int c) {
        setColor((c >> 16) & 0xFF, (c >> 8) & 0xFF, c & 0xFF);
    }

    /**
     * Sets the current preview panel.
     * This will fire a <code>PropertyChangeEvent</code> for the property
     * named "previewPanel".
     *
     * @param color the color to be set in the color chooser
     * @see JComponent#addPropertyChangeListener
     *
     * @beaninfo
     *       bound: true
     *      hidden: true
     * description: The UI component which displays the current color.
     */
    public void setPreviewPanel(JComponent preview) {

        if (previewPanel != preview) {
	    JComponent oldPreview = previewPanel;
	    previewPanel = preview;
            firePropertyChange(JColorChooser.PREVIEW_PANEL_PROPERTY, oldPreview, preview);
        }
    }

    /**
     * Returns the preview panel that shows a chosen color.
     *
     * @return a JComponent object -- the preview panel
     */
    public JComponent getPreviewPanel() {
        return previewPanel;
    }

    /**
     * Adds a color chooser panel to the color chooser.
     *
     */
    public void addChooserPanel( AbstractColorChooserPanel panel ) {
        AbstractColorChooserPanel[] oldPanels = getChooserPanels();
        AbstractColorChooserPanel[] newPanels = new AbstractColorChooserPanel[oldPanels.length+1];
	System.arraycopy(oldPanels, 0, newPanels, 0, oldPanels.length);
	newPanels[newPanels.length-1] = panel;
	setChooserPanels(newPanels);
    }

    /**
     * Removes the Color Panel specified.
     *
     * @exception IllegalArgumentException if panel is not in list of
     * 			known chooser panels
     * @param name   a string that specifies the panel to be removed
     * @return the color panel
     */
    public AbstractColorChooserPanel removeChooserPanel( AbstractColorChooserPanel panel ) {


	int containedAt = -1;

        for (int i = 0; i < chooserPanels.length; i++) {
  	    if (chooserPanels[i] == panel) {
	        containedAt = i;
		break;
	    }
	}
	if (containedAt == -1) {
	    throw new IllegalArgumentException("chooser panel not in this chooser");
	}

        AbstractColorChooserPanel[] newArray = new AbstractColorChooserPanel[chooserPanels.length-1];

	if (containedAt == chooserPanels.length-1) {  // at end
	    System.arraycopy(chooserPanels, 0, newArray, 0, newArray.length);
	}
	else if (containedAt == 0) {  // at start
	    System.arraycopy(chooserPanels, 1, newArray, 0, newArray.length);
	}
	else {  // in middle
	    System.arraycopy(chooserPanels, 0, newArray, 0, containedAt);
	    System.arraycopy(chooserPanels, containedAt+1,
			     newArray, containedAt, (chooserPanels.length - containedAt - 1));
	}

	setChooserPanels(newArray);

	return panel;
    }


    /**
     * Specifies the Color Panels used to choose a color value.
     *
     * @param panels  an array of AbstractColorChooserPanel object
     *
     * @beaninfo
     *       bound: true
     *      hidden: true
     * description: An array of different chooser types.
     */
    public void setChooserPanels( AbstractColorChooserPanel[] panels) {
        AbstractColorChooserPanel[] oldValue = chooserPanels;
	chooserPanels = panels;
	firePropertyChange(CHOOSER_PANELS_PROPERTY, oldValue, panels);
    }

    /**
     * Returns the specified color panels.
     *
     * @return an array of AbstractColorChooserPanel objects
     */
    public AbstractColorChooserPanel[] getChooserPanels() {
        return chooserPanels;
    }

    /**
     * Returns the data model that handles color selections.
     *
     * @return a ColorSelectionModel object 
     */
    public ColorSelectionModel getSelectionModel() {
        return selectionModel;
    }


    /**
     * Set the model containing the selected color.
     *
     * @param newModel   the new ColorSelectionModel object
     *
     * @beaninfo
     *       bound: true
     *      hidden: true
     * description: The model which contains the currently selected color.
     */
    public void setSelectionModel(ColorSelectionModel newModel ) {
        ColorSelectionModel oldModel = selectionModel;
	selectionModel = newModel;
	firePropertyChange(JColorChooser.SELECTION_MODEL_PROPERTY, oldModel, newModel);
    }


    /**
     * See <code>readObject</code> and <code>writeObject</code> in
     * <code>JComponent</code> for more
     * information about serialization in Swing.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
	if ((ui != null) && (getUIClassID().equals(uiClassID))) {
	    ui.installUI(this);
	}
    }


    /**
     * Returns a string representation of this <code>JColorChooser</code>.
     * This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this <code>JColorChooser</code>
     */
    protected String paramString() {
	StringBuffer chooserPanelsString = new StringBuffer("");
	for (int i=0; i<chooserPanels.length; i++) {
	    chooserPanelsString.append("[" + chooserPanels[i].toString()
				       + "]");
	}
        String previewPanelString = (previewPanel != null ?
				     previewPanel.toString() : "");

        return super.paramString() +
        ",chooserPanels=" + chooserPanelsString.toString() +
        ",previewPanel=" + previewPanelString;
    }

/////////////////
// Accessibility support
////////////////

    protected AccessibleContext accessibleContext = null;

    /**
     * Gets the AccessibleContext associated with this JColorChooser. 
     * For color choosers, the AccessibleContext takes the form of an 
     * AccessibleJColorChooser. 
     * A new AccessibleJColorChooser instance is created if necessary.
     *
     * @return an AccessibleJColorChooser that serves as the 
     *         AccessibleContext of this JColorChooser
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJColorChooser();
        }
        return accessibleContext;
    }

    /**
     * This class implements accessibility support for the 
     * <code>JColorChooser</code> class.  It provides an implementation of the 
     * Java Accessibility API appropriate to color chooser user-interface 
     * elements.
     */
    protected class AccessibleJColorChooser extends AccessibleJComponent {

        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the
         * object
         * @see AccessibleRole
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.COLOR_CHOOSER;
        }

    } // inner class AccessibleJColorChooser
}


/*
 * Class which builds a color chooser dialog consisting of
 * a JColorChooser with "Ok", "Cancel", and "Reset" buttons.
 *
 * Note: This needs to be fixed to deal with localization!
 */
class ColorChooserDialog extends JDialog {
    private Color initialColor;
    private JColorChooser chooserPane;

    public ColorChooserDialog(Component c, String title, boolean modal,
                              JColorChooser chooserPane,
                              ActionListener okListener, ActionListener cancelListener) {
        super(JOptionPane.getFrameForComponent(c), title, modal);
        //setResizable(false);

        this.chooserPane = chooserPane;

	String okString = UIManager.getString("ColorChooser.okText");
	String cancelString = UIManager.getString("ColorChooser.cancelText");
	String resetString = UIManager.getString("ColorChooser.resetText");

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(chooserPane, BorderLayout.CENTER);

        /*
         * Create Lower button panel
         */
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
        JButton okButton = new JButton(okString);
	getRootPane().setDefaultButton(okButton);
        okButton.setActionCommand("OK");
        if (okListener != null) {
            okButton.addActionListener(okListener);
        }
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hide();
            }
        });
        buttonPane.add(okButton);

        JButton cancelButton = new JButton(cancelString);

	// The following few lines are used to register esc to close the dialog
	Action cancelKeyAction = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                ((AbstractButton)e.getSource()).fireActionPerformed(e);
            }
        }; 
	KeyStroke cancelKeyStroke = KeyStroke.getKeyStroke((char)KeyEvent.VK_ESCAPE, false);
	InputMap inputMap = cancelButton.getInputMap(JComponent.
						     WHEN_IN_FOCUSED_WINDOW);
	ActionMap actionMap = cancelButton.getActionMap();
	if (inputMap != null && actionMap != null) {
	    inputMap.put(cancelKeyStroke, "cancel");
	    actionMap.put("cancel", cancelKeyAction);
	}
	// end esc handling

        cancelButton.setActionCommand("cancel");
        if (cancelListener != null) {
            cancelButton.addActionListener(cancelListener);
        }
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                hide();
            }
        });
        buttonPane.add(cancelButton);

        JButton resetButton = new JButton(resetString);
        resetButton.addActionListener(new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               reset();
           }
        });
        buttonPane.add(resetButton);
        contentPane.add(buttonPane, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(c);
    }

    public void show() {
        initialColor = chooserPane.getColor();
        super.show();
    }

    public void reset() {
        chooserPane.setColor(initialColor);
    }

    static class Closer extends WindowAdapter implements Serializable{
        public void windowClosing(WindowEvent e) {
            Window w = e.getWindow();
            w.hide();
        }
    }

    static class DisposeOnClose extends ComponentAdapter implements Serializable{
        public void componentHidden(ComponentEvent e) {
            Window w = (Window)e.getComponent();
            w.dispose();
        }
    }

}

class ColorTracker implements ActionListener, Serializable {
    JColorChooser chooser;
    Color color;

    public ColorTracker(JColorChooser c) {
        chooser = c;
    }

    public void actionPerformed(ActionEvent e) {
        color = chooser.getColor();
    }

    public Color getColor() {
        return color;
    }
}

