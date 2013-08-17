/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.text.*;
import java.awt.geom.*;
import java.beans.*;
import java.util.Enumeration;
import java.util.Vector;
import java.io.Serializable;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.accessibility.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.plaf.basic.*;
import java.util.*;

/**
 * Defines common behaviors for buttons and menu items.
 * For further information see 
 * <a
 href="http://java.sun.com/docs/books/tutorial/uiswing/components/button.html">How to Use Buttons, Check Boxes, and Radio Buttons</a>,
 * a section in <em>The Java Tutorial</em>.
 * 
 * <p>
 * 
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.135 02/06/02 
 * @author Jeff Dinkins
 */
public abstract class AbstractButton extends JComponent implements ItemSelectable, SwingConstants {

    // *********************************
    // ******* Button properties *******
    // *********************************

    /** Identifies a change in the button model. */
    public static final String MODEL_CHANGED_PROPERTY = "model";
    /** Identifies a change in the button's text. */
    public static final String TEXT_CHANGED_PROPERTY = "text";
    /** Identifies a change to the button's mnemonic. */
    public static final String MNEMONIC_CHANGED_PROPERTY = "mnemonic";

    // Text positioning and alignment
    /** Identifies a change in the button's margins. */
    public static final String MARGIN_CHANGED_PROPERTY = "margin";
    /** Identifies a change in the button's vertical alignment. */
    public static final String VERTICAL_ALIGNMENT_CHANGED_PROPERTY = "verticalAlignment";
    /** Identifies a change in the button's horizontal alignment. */
    public static final String HORIZONTAL_ALIGNMENT_CHANGED_PROPERTY = "horizontalAlignment";

    /** Identifies a change in the button's vertical text position. */
    public static final String VERTICAL_TEXT_POSITION_CHANGED_PROPERTY = "verticalTextPosition";
    /** Identifies a change in the button's horizontal text position. */
    public static final String HORIZONTAL_TEXT_POSITION_CHANGED_PROPERTY = "horizontalTextPosition";

    // Paint options
    /**
     * Identifies a change to having the border drawn,
     * or having it not drawn.
     */
    public static final String BORDER_PAINTED_CHANGED_PROPERTY = "borderPainted";
    /**
     * Identifies a change to having the border highlighted when focused,
     * or not.
     */
    public static final String FOCUS_PAINTED_CHANGED_PROPERTY = "focusPainted";
    /** Identifies a change in the button's  */
    public static final String ROLLOVER_ENABLED_CHANGED_PROPERTY = "rolloverEnabled";
    /**
     * Identifies a change from rollover enabled to disabled or back
     * to enabled.
     */
    public static final String CONTENT_AREA_FILLED_CHANGED_PROPERTY = "contentAreaFilled";

    // Icons
    /** Identifies a change to the icon that represents the button. */
    public static final String ICON_CHANGED_PROPERTY = "icon";

    /**
     * Identifies a change to the icon used when the button has been
     * pressed.
     */
    public static final String PRESSED_ICON_CHANGED_PROPERTY = "pressedIcon";
    /**
     * Identifies a change to the icon used when the button has
     * been selected.
     */
    public static final String SELECTED_ICON_CHANGED_PROPERTY = "selectedIcon";

    /**
     * Identifies a change to the icon used when the cursor is over
     * the button.
     */
    public static final String ROLLOVER_ICON_CHANGED_PROPERTY = "rolloverIcon";
    /**
     * Identifies a change to the icon used when the cursor is
     * over the button and it has been selected.
     */
    public static final String ROLLOVER_SELECTED_ICON_CHANGED_PROPERTY = "rolloverSelectedIcon";

    /**
     * Identifies a change to the icon used when the button has
     * been disabled.
     */
    public static final String DISABLED_ICON_CHANGED_PROPERTY = "disabledIcon";
    /**
     * Identifies a change to the icon used when the button has been
     * disabled and selected.
     */
    public static final String DISABLED_SELECTED_ICON_CHANGED_PROPERTY = "disabledSelectedIcon";


    /** The data model that determines the button's state. */
    protected ButtonModel model                = null;  

    private String     text                    = ""; // for BeanBox
    private Insets     margin                  = null;
    private Insets     defaultMargin           = null;

    // Button icons
    // PENDING(jeff) - hold icons in an array
    private Icon       defaultIcon             = null;
    private Icon       pressedIcon             = null;
    private Icon       disabledIcon            = null;

    private Icon       selectedIcon            = null;
    private Icon       disabledSelectedIcon    = null;

    private Icon       rolloverIcon            = null;
    private Icon       rolloverSelectedIcon    = null;
    
    // Display properties
    private boolean    paintBorder             = true;  
    private boolean    paintFocus              = true;
    private boolean    rolloverEnabled         = false;   
    private boolean    contentAreaFilled         = true; 

    // Icon/Label Alignment
    private int        verticalAlignment       = CENTER;
    private int        horizontalAlignment     = CENTER;
    
    private int        verticalTextPosition    = CENTER;
    private int        horizontalTextPosition  = TRAILING;

    private AccessibleIcon accessibleIcon      = null;

    /** 
     * The button model's <code>changeListener</code>.
     */
    protected ChangeListener changeListener = null;
    /** 
     * The button model's <code>ActionListener</code>.
     */
    protected ActionListener actionListener = null;
    /** 
     * The button model's <code>ItemListener</code>.
     */
    protected ItemListener itemListener = null;

    /**
     * Only one <code>ChangeEvent</code> is needed per button
     * instance since the
     * event's only state is the source property.  The source of events
     * generated is always "this".
     */
    protected transient ChangeEvent changeEvent;
    
    /**
     * Returns the button's text.
     * @return the buttons text
     * @see #setText
     */
    public String getText() {
        return text;
    }
    
    /**
     * Sets the button's text.
     * @param t the string used to set the text
     * @see #getText
     * @beaninfo
     *        bound: true
     *    preferred: true
     *    attribute: visualUpdate true
     *  description: The button's text.
     */
    public void setText(String text) {
        String oldValue = this.text;
        this.text = text;
        firePropertyChange(TEXT_CHANGED_PROPERTY, oldValue, text);
        if (accessibleContext != null) {
            accessibleContext.firePropertyChange(
                AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY,
                oldValue, text);
        }
        if (text == null || oldValue == null || !text.equals(oldValue)) {
            revalidate();
            repaint();
        }
    }


    /**
     * Returns the state of the button. True if the
     * toggle button is selected, false if it's not.
     * @return true if the toggle button is selected, otherwise false
     */
    public boolean isSelected() {
        return model.isSelected();
    }
 
    /**
     * Sets the state of the button. Note that this method does not
     * trigger an <code>actionEvent</code>.
     * Call <code>doClick</code> to perform a programatic action change.
     *
     * @param b  true if the button is selected, otherwise false
     */
    public void setSelected(boolean b) {
        boolean oldValue = isSelected();
        if (accessibleContext != null && oldValue != b) {
            if (b) {
                accessibleContext.firePropertyChange(
                    AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
                    null, AccessibleState.SELECTED);
            } else {
                accessibleContext.firePropertyChange(
                    AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
                    AccessibleState.SELECTED, null);
            }
        }
        model.setSelected(b);
    }

    /**
     * Programmatically perform a "click". This does the same
     * thing as if the user had pressed and released the button.
     */
    public void doClick() {
        doClick(68);
    }

    /**
     * Programmatically perform a "click". This does the same
     * thing as if the user had pressed and released the button.
     * The button stays visually "pressed" for <code>pressTime</code>
     *  milliseconds.
     *
     * @param pressTime the time to "hold down" the button, in milliseconds
     */
    public void doClick(int pressTime) {
        Dimension size = getSize();
        model.setArmed(true);
        model.setPressed(true);
        paintImmediately(new Rectangle(0,0, size.width, size.height));
        try {
            Thread.currentThread().sleep(pressTime);
        } catch(InterruptedException ie) {
        }
        model.setPressed(false);
        model.setArmed(false);
    }

    /**
     * Sets space for margin between the button's border and
     * the label. Setting to <code>null</code> will cause the button to
     * use the default margin.  The button's default <code>Border</code>
     * object will use this value to create the proper margin.
     * However, if a non-default border is set on the button, 
     * it is that <code>Border</code> object's responsibility to create the
     * appropriate margin space (else this property will
     * effectively be ignored).
     *
     * @param m the space between the border and the label
     *
     * @beaninfo
     *        bound: true
     *    attribute: visualUpdate true
     *  description: The space between the button's border and the label.
     */
    public void setMargin(Insets m) {
        // Cache the old margin if it comes from the UI
        if(m instanceof UIResource) {
            defaultMargin = m;
        } else if(margin instanceof UIResource) {
            defaultMargin = margin;
        }
            
        // If the client passes in a null insets, restore the margin
        // from the UI if possible
        if(m == null && defaultMargin != null) {
            m = defaultMargin;
        }

        Insets old = margin;
        margin = m;
        firePropertyChange(MARGIN_CHANGED_PROPERTY, old, m);
        if (old == null || !m.equals(old)) {
            revalidate();
            repaint();
        }
    }

    /**
     * Returns the margin between the button's border and
     * the label.
     * 
     * @return an <code>Insets</code> object specifying the margin
     *		between the botton's border and the label
     * @see #setMargin
     */
    public Insets getMargin() {
        return (margin == null) ? null : (Insets) margin.clone();
    }

    /**
     * Returns the default icon.
     * @return the default <code>Icon</code>
     * @see #setIcon
     */
    public Icon getIcon() {
        return defaultIcon;
    }
    
    /**
     * Sets the button's default icon. This icon is
     * also used as the "pressed" and "disabled" icon if
     * there is no explicitly set pressed icon.
     *
     * @param defaultIcon the icon used as the default image
     * @see #getIcon
     * @see #setPressedIcon
     * @beaninfo 
     *           bound: true
     *       attribute: visualUpdate true
     *     description: The button's default icon
     */
    public void setIcon(Icon defaultIcon) {
        Icon oldValue = this.defaultIcon;
        this.defaultIcon = defaultIcon;
        firePropertyChange(ICON_CHANGED_PROPERTY, oldValue, defaultIcon);
        if (accessibleContext != null) {
            accessibleContext.firePropertyChange(
                AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY,
                oldValue, pressedIcon);
        }
        if (defaultIcon != oldValue) {
            if (defaultIcon == null || oldValue == null ||
                defaultIcon.getIconWidth() != oldValue.getIconWidth() ||
                defaultIcon.getIconHeight() != oldValue.getIconHeight()) {
                revalidate();
            } 
            repaint();
        }

	// set the accessible icon
	accessibleIcon = null;
	if (defaultIcon instanceof Accessible) {
	    AccessibleContext ac = 
		((Accessible)defaultIcon).getAccessibleContext();
	    if (ac != null && ac instanceof AccessibleIcon) {
		accessibleIcon = (AccessibleIcon)ac;
	    }
	}    
    }
    
    /**
     * Returns the pressed icon for the button.
     * @return the <code>pressedIcon</code> property
     * @see #setPressedIcon
     */
    public Icon getPressedIcon() {
        return pressedIcon;
    }
    
    /**
     * Sets the pressed icon for the button.
     * @param pressedIcon the icon used as the "pressed" image
     * @see #getPressedIcon
     * @beaninfo
     *        bound: true
     *    attribute: visualUpdate true
     *  description: The pressed icon for the button.
     */
    public void setPressedIcon(Icon pressedIcon) {
        Icon oldValue = this.pressedIcon;
        this.pressedIcon = pressedIcon;
        firePropertyChange(PRESSED_ICON_CHANGED_PROPERTY, oldValue, pressedIcon);
        if (accessibleContext != null) {
            accessibleContext.firePropertyChange(
                AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY,
                oldValue, defaultIcon);
        }
        if (pressedIcon != oldValue) {
            if (getModel().isPressed()) {
                repaint();
            }
        }
    }

    /**
     * Returns the selected icon for the button.
     * @return the <code>selectedIcon</code> property
     * @see #setSelectedIcon
     */
    public Icon getSelectedIcon() {
        return selectedIcon;
    }
    
    /**
     * Sets the selected icon for the button.
     * @param selectedIcon the icon used as the "selected" image
     * @see #getSelectedIcon
     * @beaninfo
     *        bound: true
     *    attribute: visualUpdate true
     *  description: The selected icon for the button.
     */
    public void setSelectedIcon(Icon selectedIcon) {
        Icon oldValue = this.selectedIcon;
        this.selectedIcon = selectedIcon;
        firePropertyChange(SELECTED_ICON_CHANGED_PROPERTY, oldValue, selectedIcon);
        if (accessibleContext != null) {
            accessibleContext.firePropertyChange(
                AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY,
                oldValue, selectedIcon);
        }
        if (selectedIcon != oldValue) {
            if (isSelected()) {
                repaint();
            }
        }
    }

    /**
     * Returns the rollover icon for the button.
     * @return the <code>rolloverIcon</code> property
     * @see #setRolloverIcon
     */
    public Icon getRolloverIcon() {
        return rolloverIcon;
    }
    
    /**
     * Sets the rollover icon for the button.
     * @param rolloverIcon the icon used as the "rollover" image
     * @see #getRolloverIcon
     * @beaninfo
     *        bound: true
     *    attribute: visualUpdate true
     *  description: The rollover icon for the button.
     */
    public void setRolloverIcon(Icon rolloverIcon) {
        Icon oldValue = this.rolloverIcon;
        this.rolloverIcon = rolloverIcon;
        firePropertyChange(ROLLOVER_ICON_CHANGED_PROPERTY, oldValue, rolloverIcon);
        if (accessibleContext != null) {
            accessibleContext.firePropertyChange(
                AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY,
                oldValue, rolloverIcon);
        }
        setRolloverEnabled(true);
        if (rolloverIcon != oldValue) {
            // No way to determine whether we are currently in
            // a rollover state, so repaint regardless
            repaint();
        }
      
    }
    
    /**
     * Returns the rollover selection icon for the button.
     * @return the <code>rolloverSelectedIcon</code> property
     * @see #setRolloverSelectedIcon
     */
    public Icon getRolloverSelectedIcon() {
        return rolloverSelectedIcon;
    }
    
    /**
     * Sets the rollover selected icon for the button.
     * @param rolloverSelectedIcon the icon used as the
     *		"selected rollover" image
     * @see #getRolloverSelectedIcon
     * @beaninfo
     *        bound: true
     *    attribute: visualUpdate true
     *  description: The rollover selected icon for the button.
     */
    public void setRolloverSelectedIcon(Icon rolloverSelectedIcon) {
        Icon oldValue = this.rolloverSelectedIcon;
        this.rolloverSelectedIcon = rolloverSelectedIcon;
        firePropertyChange(ROLLOVER_SELECTED_ICON_CHANGED_PROPERTY, oldValue, rolloverSelectedIcon);
        if (accessibleContext != null) {
            accessibleContext.firePropertyChange(
                AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY,
                oldValue, rolloverSelectedIcon);
        }
        if (rolloverSelectedIcon != oldValue) {
            // No way to determine whether we are currently in
            // a rollover state, so repaint regardless
            if (isSelected()) {
                repaint();
            }
        }
    }
    
    /**
     * Returns the icon used by the button when it's disabled.
     * If no disabled icon has been set, the button constructs
     * one from the default icon. 
     * <!-- PENDING(jeff): the disabled icon really should be created 
     * (if necessary) by the L&F.-->
     *
     * @return the <code>disabledIcon</code> property
     * @see #getPressedIcon
     * @see #setDisabledIcon
     */
    public Icon getDisabledIcon() {
        if(disabledIcon == null) {
            if(defaultIcon != null
               && defaultIcon instanceof ImageIcon) {
                disabledIcon = new ImageIcon(
                    GrayFilter.createDisabledImage(
                        ((ImageIcon)defaultIcon).getImage()));
            }
        }
        return disabledIcon;
    }
    
    /**
     * Sets the disabled icon for the button.
     * @param disabledIcon the icon used as the disabled image
     * @see #getDisabledIcon
     * @beaninfo
     *        bound: true
     *    attribute: visualUpdate true
     *  description: The disabled icon for the button.
     */
    public void setDisabledIcon(Icon disabledIcon) {
        Icon oldValue = this.disabledIcon;
        this.disabledIcon = disabledIcon;
        firePropertyChange(DISABLED_ICON_CHANGED_PROPERTY, oldValue, disabledIcon);
        if (accessibleContext != null) {
            accessibleContext.firePropertyChange(
                AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY,
                oldValue, disabledIcon);
        }
        if (disabledIcon != oldValue) {
            if (!isEnabled()) {
                repaint();
            }
        }
    }
    
    /**
     * Returns the icon used by the button when it's disabled and selected.
     * If not no disabled selection icon has been set, the button constructs
     * one from the selection icon. 
     * <!-- PENDING(jeff): the disabled selection icon really should be 
     * created (if necesary) by the L&F. -->
     *
     * @return the <code>disabledSelectedIcon</code> property
     * @see #getPressedIcon
     * @see #setDisabledIcon
     */
    public Icon getDisabledSelectedIcon() {
        if(disabledSelectedIcon == null) {
            if(selectedIcon != null && selectedIcon instanceof ImageIcon) {
                disabledSelectedIcon = new ImageIcon(
                    GrayFilter.createDisabledImage(((ImageIcon)selectedIcon).getImage()));
            } else {
                return disabledIcon;
            }
        }
        return disabledSelectedIcon;
    }

    /**
     * Sets the disabled selection icon for the button.
     * @param disabledSelectedIcon the icon used as the disabled
     * 		selection image
     * @see #getDisabledSelectedIcon
     * @beaninfo
     *        bound: true
     *    attribute: visualUpdate true
     *  description: The disabled selection icon for the button.
     */
    public void setDisabledSelectedIcon(Icon disabledSelectedIcon) {
        Icon oldValue = this.disabledSelectedIcon;
        this.disabledSelectedIcon = disabledSelectedIcon;
        firePropertyChange(DISABLED_SELECTED_ICON_CHANGED_PROPERTY, oldValue, disabledSelectedIcon);
        if (accessibleContext != null) {
            accessibleContext.firePropertyChange(
                AccessibleContext.ACCESSIBLE_VISIBLE_DATA_PROPERTY,
                oldValue, disabledSelectedIcon);
        }
        if (disabledSelectedIcon != oldValue) {
            if (disabledSelectedIcon == null || oldValue == null ||
                disabledSelectedIcon.getIconWidth() != oldValue.getIconWidth() ||
                disabledSelectedIcon.getIconHeight() != oldValue.getIconHeight()) {
                revalidate();
            } 
            if (!isEnabled() && isSelected()) {
                repaint();
            }
        }
    }

    /**
     * Returns the vertical alignment of the text and icon.
     *
     * @return the <code>verticalAlignment</code> property, one of the
     *		following values: 
     * <ul>
     * <li>SwingConstants.CENTER (the default)
     * <li>SwingConstants.TOP
     * <li>SwingConstants.BOTTOM
     * </ul>
     */
    public int getVerticalAlignment() {
        return verticalAlignment;
    }
    
    /**
     * Sets the vertical alignment of the icon and text.
     * @param alignment  one of the following values:
     * <ul>
     * <li>SwingConstants.CENTER (the default)
     * <li>SwingConstants.TOP
     * <li>SwingConstants.BOTTOM
     * </ul>
     * @beaninfo
     *        bound: true
     *         enum: TOP    SwingConstants.TOP
     *               CENTER SwingConstants.CENTER
     *               BOTTOM  SwingConstants.BOTTOM
     *    attribute: visualUpdate true
     *  description: The vertical alignment of the icon and text.
     */
    public void setVerticalAlignment(int alignment) {
        if (alignment == verticalAlignment) return;
        int oldValue = verticalAlignment;
        verticalAlignment = checkVerticalKey(alignment, "verticalAlignment");
        firePropertyChange(VERTICAL_ALIGNMENT_CHANGED_PROPERTY, oldValue, verticalAlignment);         repaint();
    }
    
    /**
     * Returns the horizontal alignment of the icon and text.
     * @return the <code>horizontalAlignment</code> property,
     *		one of the following values:
     * <ul>
     * <li>SwingConstants.RIGHT (the default)
     * <li>SwingConstants.LEFT
     * <li>SwingConstants.CENTER
     * <li>SwingConstants.LEADING
     * <li>SwingConstants.TRAILING
     * </ul>
     */
    public int getHorizontalAlignment() {
        return horizontalAlignment;
    }
    
    /**
     * Sets the horizontal alignment of the icon and text.
     * @param alignment  one of the following values:
     * <ul>
     * <li>SwingConstants.RIGHT (the default)
     * <li>SwingConstants.LEFT
     * <li>SwingConstants.CENTER
     * <li>SwingConstants.LEADING
     * <li>SwingConstants.TRAILING
     * </ul>
     * @beaninfo
     *        bound: true
     *         enum: LEFT     SwingConstants.LEFT
     *               CENTER   SwingConstants.CENTER
     *               RIGHT    SwingConstants.RIGHT
     *               LEADING  SwingConstants.LEADING
     *               TRAILING SwingConstants.TRAILING
     *    attribute: visualUpdate true
     *  description: The horizontal alignment of the icon and text.
     */
    public void setHorizontalAlignment(int alignment) {
        if (alignment == horizontalAlignment) return;
        int oldValue = horizontalAlignment;
        horizontalAlignment = checkHorizontalKey(alignment,
                                                 "horizontalAlignment");
        firePropertyChange(HORIZONTAL_ALIGNMENT_CHANGED_PROPERTY,
                           oldValue, horizontalAlignment);       
        repaint();
    }

    
    /**
     * Returns the vertical position of the text relative to the icon.
     * @return the <code>verticalTextPosition</code> property, 
     *		one of the following values:
     * <ul>
     * <li>SwingConstants.CENTER  (the default)
     * <li>SwingConstants.TOP
     * <li>SwingConstants.BOTTOM
     * </ul>
     */
    public int getVerticalTextPosition() {
        return verticalTextPosition;
    }
    
    /**
     * Sets the vertical position of the text relative to the icon.
     * @param alignment  one of the following values:
     * <ul>
     * <li>SwingConstants.CENTER (the default)
     * <li>SwingConstants.TOP
     * <li>SwingConstants.BOTTOM
     * </ul>
     * @beaninfo
     *        bound: true
     *         enum: TOP    SwingConstants.TOP
     *               CENTER SwingConstants.CENTER
     *               BOTTOM SwingConstants.BOTTOM
     *    attribute: visualUpdate true
     *  description: The vertical position of the text relative to the icon.
     */
    public void setVerticalTextPosition(int textPosition) {
        if (textPosition == verticalTextPosition) return;
        int oldValue = verticalTextPosition;
        verticalTextPosition = checkVerticalKey(textPosition, "verticalTextPosition");
        firePropertyChange(VERTICAL_TEXT_POSITION_CHANGED_PROPERTY, oldValue, verticalTextPosition);
        repaint();
    }
    
    /**
     * Returns the horizontal position of the text relative to the icon.
     * @return the <code>horizontalTextPosition</code> property, 
     * 		one of the following values:
     * <ul>
     * <li>SwingConstants.RIGHT (the default)
     * <li>SwingConstants.LEFT
     * <li>SwingConstants.CENTER
     * <li>SwingConstants.LEADING
     * <li>SwingConstants.TRAILING
     * </ul>
     */
    public int getHorizontalTextPosition() {
        return horizontalTextPosition;
    }
    
    /**
     * Sets the horizontal position of the text relative to the icon.
     * @param textPosition one of the following values:
     * <ul>
     * <li>SwingConstants.RIGHT (the default)
     * <li>SwingConstants.LEFT
     * <li>SwingConstants.CENTER
     * <li>SwingConstants.LEADING
     * <li>SwingConstants.TRAILING
     * </ul>
     * @exception IllegalArgumentException if <code>textPosition</code.
     *		is not one of the legal values listed above
     * @beaninfo
     *        bound: true
     *         enum: LEFT     SwingConstants.LEFT
     *               CENTER   SwingConstants.CENTER
     *               RIGHT    SwingConstants.RIGHT
     *               LEADING  SwingConstants.LEADING
     *               TRAILING SwingConstants.TRAILING
     *    attribute: visualUpdate true
     *  description: The horizontal position of the text relative to the icon.
     */
    public void setHorizontalTextPosition(int textPosition) {
        if (textPosition == horizontalTextPosition) return;
        int oldValue = horizontalTextPosition;
        horizontalTextPosition = checkHorizontalKey(textPosition,
                                                    "horizontalTextPosition");
        firePropertyChange(HORIZONTAL_TEXT_POSITION_CHANGED_PROPERTY,
                           oldValue,
                           horizontalTextPosition);
        repaint();
    }
    
    /**
     * Verify that key is a legal value for the
     * <code>horizontalAlignment</code> properties.
     *
     * @param key the property value to check, one of the following values:
     * <ul>
     * <li>SwingConstants.RIGHT (the default)
     * <li>SwingConstants.LEFT
     * <li>SwingConstants.CENTER
     * <li>SwingConstants.LEADING
     * <li>SwingConstants.TRAILING
     * </ul>
     * @param exception the <code>IllegalArgumentException</code>
     *		detail message 
     * @exception IllegalArgumentException if key is not one of the legal
     *		values listed above
     * @see #setHorizontalTextPosition
     * @see #setHorizontalAlignment
     */
    protected int checkHorizontalKey(int key, String exception) {
        if ((key == LEFT) ||
            (key == CENTER) ||
            (key == RIGHT) ||
            (key == LEADING) ||
            (key == TRAILING)) {
            return key;
        } else {
            throw new IllegalArgumentException(exception);
        }
    }
    
    /**
     * Ensures that the key is a valid. Throws an
     * <code>IllegalArgumentException</code>
     * exception otherwise.
     *  
     * @param key  the value to check, one of the following values:
     * <ul>
     * <li>SwingConstants.CENTER (the default)
     * <li>SwingConstants.TOP
     * <li>SwingConstants.BOTTOM
     * </ul>
     * @param exception a string to be passed to the
     *		<code>IllegalArgumentException</code> call if key
     *		is not one of the valid values listed above
     * @exception IllegalArgumentException if key is not one of the legal
     *		values listed above
     */
    protected int checkVerticalKey(int key, String exception) {
        if ((key == TOP) || (key == CENTER) || (key == BOTTOM)) {
            return key;
        } else {
            throw new IllegalArgumentException(exception);
        }
    }
    
    /**
     * Sets the action command for this button. 
     * @param actionCommand the action command for this button
     */
    public void setActionCommand(String actionCommand) {
        getModel().setActionCommand(actionCommand);
    }
    
    /**
     * Returns the action command for this button. 
     * @return the action command for this button
     */
    public String getActionCommand() {
        String ac = getModel().getActionCommand();
        if(ac == null) {
            ac = getText();
        }
        return ac;
    }
    
    private Action action;
    private PropertyChangeListener actionPropertyChangeListener;

    /**
     * Sets the <code>Action</code> for the <code>ActionEvent</code> source.
     * The new <code>Action</code> replaces any previously set
     * <code>Action</code> but does not affect <code>ActionListeners</code> 
     * independently added with <code>addActionListener</code>.
     * If the <code>Action</code> is already a registered
     * <code>ActionListener</code> for the button, it is not re-registered.
     * <p>
     * A side-effect of setting the <code>Action</code> is that the
     * <code>ActionEvent</code> source's properties  are immediately
     * set from the values in the <code>Action</code> (performed by the 
     * method <code>configurePropertiesFromAction</code>) and
     * subsequently updated as the <code>Action</code>'s properties change
     * (via a <code>PropertyChangeListener</code> created by the method
     * <code>createActionPropertyChangeListener</code>.
     *
     * @param a the <code>Action</code> for the <code>AbstractButton</code>,
     *		or <code>null</code>
     * @since 1.3
     * @see Action
     * @see #getAction
     * @see #configurePropertiesFromAction
     * @see #createActionPropertyChangeListener
     * @beaninfo
     *        bound: true
     *    attribute: visualUpdate true
     *  description: the Action instance connected with this ActionEvent source
     */
    public void setAction(Action a) {
	Action oldValue = getAction();
	if (action==null || !action.equals(a)) {
	    action = a;
	    if (oldValue!=null) {
		removeActionListener(oldValue);
		oldValue.removePropertyChangeListener(actionPropertyChangeListener);
		actionPropertyChangeListener = null;
	    }
	    configurePropertiesFromAction(action);
	    if (action!=null) {		
		// Don't add if it is already a listener
		if (!isListener(ActionListener.class, action)) {
		    addActionListener(action);
		}
		// Reverse linkage:
		actionPropertyChangeListener = createActionPropertyChangeListener(action);
		action.addPropertyChangeListener(actionPropertyChangeListener);
	    }
	    firePropertyChange("action", oldValue, action);
	    revalidate();
	    repaint();
	}
    }

    private boolean isListener(Class c, ActionListener a) {
	boolean isListener = false;
	Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==c && listeners[i+1]==a) {
		    isListener=true;
	    }
	}
	return isListener;
    }

    /**
     * Returns the currently set <code>Action</code> for this
     * <code>ActionEvent</code> source, or <code>null</code>
     * if no <code>Action</code> is set.
     *
     * @return the <code>Action</code> for this <code>ActionEvent</code>
     *		source, or <code>null</code>
     * @since 1.3
     * @see Action
     * @see #setAction
     */
    public Action getAction() {
	return action;
    }

    /**
     * Factory method which sets the <code>ActionEvent</code>
     * source's properties according to values from the
     * <code>Action</code> instance.  The properties 
     * which are set may differ for subclasses.  By default,
     * the properties which get set are <code>Text</code>, <code>Icon
     * Enabled</code>, <code>ToolTipText</code> and <code>Mnemonic</code>.
     * <p>
     * If the <code>Action</code> passed in is <code>null</code>, 
     * the following things will occur:
     * <ul>
     * <li>the text is set to <code>null</code>,
     * <li>the icon is set to <code>null</code>,
     * <li>enabled is set to true,
     * <li>the tooltip text is set to <code>null</code>
     * </ul>
     *
     * @param a the <code>Action</code> from which to get the properties,
     *		or <code>null</code>
     * @since 1.3
     * @see Action
     * @see #setAction
     */
    protected void configurePropertiesFromAction(Action a) {
	setText((a!=null?(String)a.getValue(Action.NAME):null));
	setIcon((a!=null?(Icon)a.getValue(Action.SMALL_ICON):null));
	setEnabled((a!=null?a.isEnabled():true));
 	setToolTipText((a!=null?(String)a.getValue(Action.SHORT_DESCRIPTION):null));	
        if (a != null)  {
            Integer i = (Integer)a.getValue(Action.MNEMONIC_KEY);
            if (i != null)
                setMnemonic(i.intValue());
        }
    }

    /**
     * Factory method which creates the <code>PropertyChangeListener</code>
     * used to update the <code>ActionEvent</code> source as properties
     * change on its <code>Action</code> instance.  Subclasses may
     * override this in order to provide their own
     * <code>PropertyChangeListener</code> if the set of
     * properties which should be kept up to date differs from the
     * default properties (<code>Text, Icon, Enabled, ToolTipText,
     * Mnemonic</code>).
     * <p>
     * Note that <code>PropertyChangeListeners</code> should avoid holding
     * strong references to the <code>ActionEvent</code> source,
     * as this may hinder garbage collection of the
     * <code>ActionEvent</code> source and all components
     * in its containment hierarchy.  
     *
     * @param a the new action for the button
     * @since 1.3
     * @see Action
     * @see #setAction
     */
    protected PropertyChangeListener createActionPropertyChangeListener(Action a) {
        return new ButtonActionPropertyChangeListener(this, a);
    }

    private static class ButtonActionPropertyChangeListener extends AbstractActionPropertyChangeListener {
	ButtonActionPropertyChangeListener(AbstractButton b, Action a) {
	    super(b, a);
	}
	public void propertyChange(PropertyChangeEvent e) {	    
	    String propertyName = e.getPropertyName();
	    AbstractButton button = (AbstractButton)getTarget();
	    if (button == null) {   //WeakRef GC'ed in 1.2
		Action action = (Action)e.getSource();
		action.removePropertyChangeListener(this);
		} else {
		    if (e.getPropertyName().equals(Action.NAME)) {
			String text = (String) e.getNewValue();
			button.setText(text);
			button.repaint();
		    } else if (e.getPropertyName().equals(Action.SHORT_DESCRIPTION)) {
			String text = (String) e.getNewValue();
			button.setToolTipText(text);
		    } else if (propertyName.equals("enabled")) {
			Boolean enabledState = (Boolean) e.getNewValue();
			button.setEnabled(enabledState.booleanValue());
			button.repaint();
		    } else if (e.getPropertyName().equals(Action.SMALL_ICON)) {
			Icon icon = (Icon) e.getNewValue();
			button.setIcon(icon);
			button.invalidate();
			button.repaint();
		    } else if (e.getPropertyName().equals(Action.MNEMONIC_KEY)) {
			Integer mn = (Integer) e.getNewValue();
			button.setMnemonic(mn.intValue());
			button.invalidate();
			button.repaint();
		    } 
		}
	}
    }

    /**
     * Returns whether the border should be painted.
     * @return true if the border should be painted, false otherwise
     * @see #setBorderPainted
     */
    public boolean isBorderPainted() {
        return paintBorder;
    }
    
    /**
     * Sets whether the border should be painted.
     * @param b if true and border property is not <code>null</code>,
     *		the border is painted.
     * @see #isBorderPainted
     * @beaninfo
     *        bound: true
     *    attribute: visualUpdate true
     *  description: Whether the border should be painted.
     */
    public void setBorderPainted(boolean b) {
        boolean oldValue = paintBorder;
        paintBorder = b;
        firePropertyChange(BORDER_PAINTED_CHANGED_PROPERTY, oldValue, paintBorder);
        if (b != oldValue) {
            revalidate();
            repaint();
        }
    }

    /**
     * Paint the button's border if <code>BorderPainted</code>
     * property is true.
     * @param g the <code>Graphics</code> context in which to paint
     * 
     * @see #paint
     * @see #setBorder
     */
    protected void paintBorder(Graphics g) {    
        if (isBorderPainted()) {
            super.paintBorder(g);
        }
    }
 
    /**
     * Returns whether focus should be painted.
     * @return the <code>paintFocus</code> property
     * @see #setFocusPainted
     */
    public boolean isFocusPainted() {
        return paintFocus;
    }
    
    /**
     * Sets whether focus should be painted.
     * @param b if true, the focus state is painted
     * @see #isFocusPainted
     * @beaninfo
     *        bound: true
     *    attribute: visualUpdate true
     *  description: Whether focus should be painted
     */
    public void setFocusPainted(boolean b) {
        boolean oldValue = paintFocus;
        paintFocus = b;
        firePropertyChange(FOCUS_PAINTED_CHANGED_PROPERTY, oldValue, paintFocus);
        if (b != oldValue && hasFocus()) {
            revalidate();
            repaint();
        }
    }

    /**
     * Checks whether the "content area" of the button should be filled.
     * @return the <code>contentAreaFilled</code> property
     * @see #setFocusPainted
     */
    public boolean isContentAreaFilled() {
        return contentAreaFilled;
    }
    
    /**
     * Sets whether the button should paint the content area
     * or leave it transparent.  If you wish to have a transparent
     * button, for example and icon only button, then you should set
     * this to false.  Do not call <code>setOpaque(false)</code>.
     * Whether the button follows the
     * <code>RepaintManager</code>'s concept of opacity is L&F depandant.
     * <p>
     * This function may cause the component's opaque property to change.
     * <p>
     * The exact behavior of calling this function varies on a
     * component-by-component and L&F-by-L&F basis.
     *
     * @param b if true, rollover effects should be painted
     * @see #isContentAreaFilled
     * @see #setOpaque
     * @beaninfo
     *        bound: true
     *    attribute: visualUpdate true
     *  description: Whether the button should paint the content area
     *               or leave it transparent.
     */
    public void setContentAreaFilled(boolean b) {
        boolean oldValue = contentAreaFilled;
        contentAreaFilled = b;
        firePropertyChange(CONTENT_AREA_FILLED_CHANGED_PROPERTY, oldValue, contentAreaFilled);
        if (b != oldValue) {
            repaint();
        }
    }

    /**
     * Checks whether rollover effects are enabled.
     * @return the <code>rolloverEnabled</code> property
     * @see #setFocusPainted
     */
    public boolean isRolloverEnabled() {
        return rolloverEnabled;
    }
    
    /**
     * Sets whether rollover effects should be enabled.
     * @param b if true, rollover effects should be painted
     * @see #isRolloverEnabled
     * @beaninfo
     *        bound: true
     *    attribute: visualUpdate true
     *  description: Whether rollover effects should be enabled.
     */
    public void setRolloverEnabled(boolean b) {
        boolean oldValue = rolloverEnabled;
        rolloverEnabled = b;
        firePropertyChange(ROLLOVER_ENABLED_CHANGED_PROPERTY, oldValue, rolloverEnabled);
        if (b != oldValue) {
            repaint();
        }
    }

    /**
     * Returns the keyboard mnemonic from the the current model.
     * @return the keyboard mnemonic from the model
     */
    public int getMnemonic() {
        return model.getMnemonic();
    }

    /**
     * Sets the keyboard mnemonic on the current model.
     *
     * @param mnemonic the key code which represents the mnemonic
     * @beaninfo
     *        bound: true
     *    attribute: visualUpdate true
     *  description: the keyboard character mnemonic
     */
    public void setMnemonic(int mnemonic) {
        int oldValue = getMnemonic();
        model.setMnemonic(mnemonic);
        firePropertyChange(MNEMONIC_CHANGED_PROPERTY, oldValue, mnemonic);
        if (mnemonic != oldValue) {
            revalidate();
            repaint();
        }
    }

    /**
     * Specifies the mnemonic value.
     *
     * @param mnemonic  a char specifying the mnemonic value
     * @beaninfo
     *        bound: true
     *    attribute: visualUpdate true
     *  description: the keyboard character mnemonic
     */
    public void setMnemonic(char mnemonic) {
        int vk = (int) mnemonic;
        if(vk >= 'a' && vk <='z')
            vk -= ('a' - 'A');
        setMnemonic(vk);
    }

    /**
     * Identifies whether or not this component can receive the focus.
     *
     * @return true if this component can receive the focus
     */
    public boolean isFocusTraversable() {
        return isEnabled();
    }


    /**
     * Returns the model that this button represents.
     * @return the <code>model</code> property
     * @see #setModel
     */
    public ButtonModel getModel() {
        return model;
    }
    
    /**
     * Sets the model that this button represents.
     * @param m the new <code>ButtonModel</code>
     * @see #getModel
     * @beaninfo
     *        bound: true
     *  description: Model that the Button uses.
     */
    public void setModel(ButtonModel newModel) {
        
        ButtonModel oldModel = getModel();
        
        if (oldModel != null) {
            oldModel.removeChangeListener(changeListener);
            oldModel.removeActionListener(actionListener);
            changeListener = null;
            actionListener = null;
        }
        
        model = newModel;
        
        if (newModel != null) {
            changeListener = createChangeListener();
            actionListener = createActionListener();
            itemListener = createItemListener();
            newModel.addChangeListener(changeListener);
            newModel.addActionListener(actionListener);
            newModel.addItemListener(itemListener);
        }

        firePropertyChange(MODEL_CHANGED_PROPERTY, oldModel, newModel);
        if (newModel != oldModel) {
            revalidate();
            repaint();
        }
    }

    
    /**
     * Returns the L&F object that renders this component.
     * @return the ButtonUI object
     * @see #setUI
     */
    public ButtonUI getUI() {
        return (ButtonUI) ui;
    }

    
    /**
     * Sets the L&F object that renders this component.
     * @param ui the <code>ButtonUI</code> L&F object
     * @see #getUI
     */
    public void setUI(ButtonUI ui) {
        super.setUI(ui);
    }

    
    /**
     * Notification from the <code>UIFactory</code> that the
     * L&F has changed.  Subtypes of <code>AbstractButton</code>
     * should override this to update the UI. For
     * example, <code>JButton</code> might do the following:
     * <pre>
     *      setUI((ButtonUI)UIManager.getUI(
     *          "ButtonUI", "javax.swing.plaf.basic.BasicButtonUI", this));
     * </pre>
     */
    public void updateUI() {
    }
    
    /**
     * Adds a <code>ChangeListener</code> to the button.
     * @param l the listener to be added
     */
    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }
    
    /**
     * Removes a ChangeListener from the button.
     * @param l the listener to be removed
     */
    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }
    
    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * @see EventListenerList
     */
    protected void fireStateChanged() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==ChangeListener.class) {
                // Lazily create the event:
                if (changeEvent == null)
                    changeEvent = new ChangeEvent(this);
                ((ChangeListener)listeners[i+1]).stateChanged(changeEvent);
            }          
        }
    }   

    /**
     * Adds an <code>ActionListener</code> to the button.
     * @param l the <code>ActionListener</code> to be added
     */
    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }
    
    /**
     * Removes an <code>ActionListener</code> from the button.
     * If the listener is the currently set <code>Action</code>
     * for the button, then the <code>Action</code>
     * is set to <code>null</code>.
     *
     * @param l the listener to be removed
     */
    public void removeActionListener(ActionListener l) {
	if ((l != null) && (getAction() == l)) {
	    setAction(null);
	} else {
	    listenerList.remove(ActionListener.class, l);
	}
    }
    
    
    /**
     * Subclasses that want to handle <code>ChangeEvents</code> differently
     * can override this to return another <code>ChangeListener</code>
     * implementation.
     *
     * @return the new <code>ButtonChangeListener</code>
     */
    protected ChangeListener createChangeListener() {
        return (ChangeListener) new ButtonChangeListener();
    }

    /**
     * Extends <code>ChangeListener</code> to be serializable.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    protected class ButtonChangeListener implements ChangeListener, Serializable {
        ButtonChangeListener() {
        }

        public void stateChanged(ChangeEvent e) {
            fireStateChanged();
            repaint();
        }
    }


    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     *
     * @param e  the <code>ActionEvent</code> object
     * @see EventListenerList
     */
    protected void fireActionPerformed(ActionEvent event) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        ActionEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==ActionListener.class) {
                // Lazily create the event:
                if (e == null) {
                      String actionCommand = event.getActionCommand();
                      if(actionCommand == null) {
                         actionCommand = getActionCommand();
                      }
                      e = new ActionEvent(AbstractButton.this,
                                          ActionEvent.ACTION_PERFORMED,
                                          actionCommand,
                                          event.getModifiers());
                }
                ((ActionListener)listeners[i+1]).actionPerformed(e);
            }          
        }
    }
    
    /**
     * Notifies all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
     * 
     * @param event  the <code>ItemEvent</code> object
     * @see EventListenerList
     */
    protected void fireItemStateChanged(ItemEvent event) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        ItemEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==ItemListener.class) {
                // Lazily create the event:
                if (e == null) {
                    e = new ItemEvent(AbstractButton.this,
                                      ItemEvent.ITEM_STATE_CHANGED,
                                      AbstractButton.this,
                                      event.getStateChange());
                }
                ((ItemListener)listeners[i+1]).itemStateChanged(e);
            }          
        }
	if (accessibleContext != null) {
	    if (event.getStateChange() == ItemEvent.SELECTED) {
		accessibleContext.firePropertyChange(
	            AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
                    null, AccessibleState.SELECTED);
		accessibleContext.firePropertyChange(
	            AccessibleContext.ACCESSIBLE_VALUE_PROPERTY,
                    new Integer(0), new Integer(1));
	    } else {
		accessibleContext.firePropertyChange(
	            AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
                    AccessibleState.SELECTED, null);
		accessibleContext.firePropertyChange(
	            AccessibleContext.ACCESSIBLE_VALUE_PROPERTY,
                    new Integer(1), new Integer(0));
	    }
	}   
    }
    

    private class ForwardActionEvents implements ActionListener, Serializable {
        public void actionPerformed(ActionEvent event) {
            fireActionPerformed(event);
        }
    }

    protected ActionListener createActionListener() {
        return new ForwardActionEvents();
    }


    private class ForwardItemEvents implements ItemListener, Serializable {
        public void itemStateChanged(ItemEvent event) {
            fireItemStateChanged(event);
        }
    }

    protected ItemListener createItemListener() {
        return new ForwardItemEvents();
    }

    
    /**
     * Enables (or disables) the button.
     * @param b  true to enable the button, otherwise false
     */
    public void setEnabled(boolean b) {
	if (!b && model.isRollover()) {
	    model.setRollover(false);
	} 
        super.setEnabled(b);
        model.setEnabled(b);
    }

    // *** Deprecated java.awt.Button APIs below *** //
    
    /**
     * Returns the label text.
     *
     * @return a <code>String</code> containing the label
     * @deprecated - Replaced by <code>getText</code>
     */
    public String getLabel() {
        return getText();
    }
    
    /**
     * Sets the label text.
     *
     * @param label  a <code>String</code> containing the text
     * @deprecated - Replaced by <code>setText(text)</code>
     * @beaninfo
     *        bound: true
     *  description: Replace by setText(text)
     */
    public void setLabel(String label) {
        setText(label);
    }

    /**
     * Adds an <code>ItemListener</code> to the <code>checkbox</code>.
     * @param l  the <code>ItemListener</code> to be added
     */
    public void addItemListener(ItemListener l) {
        listenerList.add(ItemListener.class, l);
    }
    
    /**
     * Removes an <code>ItemListener</code> from the button.
     * @param l the <code>ItemListener</code> to be removed
     */
    public void removeItemListener(ItemListener l) {
        listenerList.remove(ItemListener.class, l);
    }

   /**
     * Returns an array (length 1) containing the label or
     * <code>null</code> if the button is not selected.
     *
     * @return an array containing 1 Object: the text of the button,
     *         if the item is selected; otherwise <code>null</code>
     */
    public Object[] getSelectedObjects() {
        if (isSelected() == false) {
            return null;
        }
        Object[] selectedObjects = new Object[1];
        selectedObjects[0] = getText();
        return selectedObjects;
    }

    protected void init(String text, Icon icon) {
        setLayout(new OverlayLayout(this));

        if(text != null) {
            setText(text);
        }
        
        if(icon != null) {
            setIcon(icon);
        }
        
        // Set the UI
        updateUI();
        
        // Listen for Focus events
        addFocusListener(
            new FocusListener() {
            public void focusGained(FocusEvent event) {
            }
            public void focusLost(FocusEvent event) {
		model.setArmed(false);
                // repaint focus is lost
                if(isFocusPainted()) {
                    repaint();
                }
            }
        }
        );

        setAlignmentX(LEFT_ALIGNMENT);
        setAlignmentY(CENTER_ALIGNMENT);
    }


    /**
     * This is overridden to return false if the current <code>Icon</code>'s
     * <code>Image</code> is not equal to the
     * passed in <code>Image</code> <code>img</code>.
     *
     * @param img  the <code>Image</code> to be compared
     * @param infoflags flags used to repaint the button when the image
     *		is updated and which determine how much is to be painted
     * @param x  the x coordinate
     * @param y  the y coordinate
     * @param w  the width
     * @param h  the height
     * @see     java.awt.image.ImageObserver
     * @see     java.awt.Component#imageUpdate(java.awt.Image, int, int, int, int, int)
     */
    public boolean imageUpdate(Image img, int infoflags,
			       int x, int y, int w, int h) {
	if (!SwingUtilities.doesIconReferenceImage(getIcon(), img) &&
	    !SwingUtilities.doesIconReferenceImage(getPressedIcon(), img) &&
	    !SwingUtilities.doesIconReferenceImage(disabledIcon, img) &&
	    !SwingUtilities.doesIconReferenceImage(getSelectedIcon(), img) &&
	    !SwingUtilities.doesIconReferenceImage(disabledSelectedIcon,
						   img) &&
	    !SwingUtilities.doesIconReferenceImage(getRolloverIcon(), img) &&
	    !SwingUtilities.doesIconReferenceImage(getRolloverSelectedIcon(),
						   img)) {
	    // We don't know about this image, disable the notification so
	    // we don't keep repainting.
	    return false;
	}
	return super.imageUpdate(img, infoflags, x, y, w, h);
    }

    /**
     * Returns a string representation of this <code>AbstractButton</code>.
     * This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * <P>
     * Overriding <code>paramString</code> to provide information about the
     * specific new aspects of the JFC components.
     * 
     * @return  a string representation of this <code>AbstractButton</code>
     */
    protected String paramString() {
	String defaultIconString = ((defaultIcon != null)
				    && (defaultIcon != this) ?
				    defaultIcon.toString() : "");
	String pressedIconString = ((pressedIcon != null)
				    && (pressedIcon != this) ?
				    pressedIcon.toString() : "");
	String disabledIconString = ((disabledIcon != null)
				     && (disabledIcon != this) ?
				     disabledIcon.toString() : "");
	String selectedIconString = ((selectedIcon != null)
				     && (selectedIcon != this) ?
				     selectedIcon.toString() : "");
	String disabledSelectedIconString = ((disabledSelectedIcon != null) &&
					     (disabledSelectedIcon != this) ?
					     disabledSelectedIcon.toString()
					     : "");
	String rolloverIconString = ((rolloverIcon != null)
				     && (rolloverIcon != this) ?
				     rolloverIcon.toString() : "");
	String rolloverSelectedIconString = ((rolloverSelectedIcon != null) &&
					     (rolloverSelectedIcon != this) ?
					     rolloverSelectedIcon.toString()
					     : "");
	String paintBorderString = (paintBorder ? "true" : "false");
	String paintFocusString = (paintFocus ? "true" : "false");
	String rolloverEnabledString = (rolloverEnabled ? "true" : "false");

	return super.paramString() +
	",defaultIcon=" + defaultIconString +
	",disabledIcon=" + disabledIconString +
	",disabledSelectedIcon=" + disabledSelectedIconString +
	",margin=" + margin +
	",paintBorder=" + paintBorderString +
	",paintFocus=" + paintFocusString +
	",pressedIcon=" + pressedIconString +
	",rolloverEnabled=" + rolloverEnabledString +
	",rolloverIcon=" + rolloverIconString +
	",rolloverSelectedIcon=" + rolloverSelectedIconString +
	",selectedIcon=" + selectedIconString +
	",text=" + text;
    }


///////////////////
// Accessibility support
///////////////////
    /**
     * This class implements accessibility support for the 
     * <code>AbstractButton</code> class.  It provides an implementation of the 
     * Java Accessibility API appropriate to button and menu item 
     * user-interface elements.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    protected abstract class AccessibleAbstractButton
        extends AccessibleJComponent implements AccessibleAction, 
        AccessibleValue, AccessibleText {

        /**
         * Returns the accessible name of this object.  
         *
         * @return the localized name of the object -- can be 
         *		<code>null</code> if this 
         * 		object does not have a name
         */
        public String getAccessibleName() {
            if (accessibleName != null) {
                return accessibleName;
            } else {
                if (AbstractButton.this.getText() == null) {
                    return super.getAccessibleName();
                } else {
                    return AbstractButton.this.getText();
                }
            }
        }

	/**
	 * Get the AccessibleIcons associated with this object if one
	 * or more exist.  Otherwise return null.
	 */
	public AccessibleIcon [] getAccessibleIcon() {
	    if (AbstractButton.this.accessibleIcon == null) {
		return null; 
	    } else {
		AccessibleIcon [] ac = new AccessibleIcon[1];
		ac[0] = AbstractButton.this.accessibleIcon;
		return ac;
	    }
	}

        /**
         * Get the state set of this object.
         *
         * @return an instance of AccessibleState containing the current state 
         * of the object
         * @see AccessibleState
         */
        public AccessibleStateSet getAccessibleStateSet() {
        AccessibleStateSet states = super.getAccessibleStateSet();
            if (getModel().isArmed()) {
                states.add(AccessibleState.ARMED);
            }
            if (hasFocus()) {
                states.add(AccessibleState.FOCUSED);
            }
            if (getModel().isPressed()) {
                states.add(AccessibleState.PRESSED);
            }
            if (isSelected()) {
                states.add(AccessibleState.CHECKED);
            }
            return states;
        }

        /**
         * Get the AccessibleRelationSet associated with this object if one
         * exists.  Otherwise return null.
         * @see AccessibleRelation
         */
        public AccessibleRelationSet getAccessibleRelationSet() {

	    // Check where the AccessibleContext's relation
	    // set already contains a MEMBER_OF relation.
	    AccessibleRelationSet relationSet 
		= super.getAccessibleRelationSet();

	    if (!relationSet.contains(AccessibleRelation.MEMBER_OF)) {
		// get the members of the button group if one exists
		ButtonModel model = getModel();
		if (model != null && model instanceof DefaultButtonModel) {
		    ButtonGroup group = ((DefaultButtonModel)model).getGroup();
		    if (group != null) {
			// set the target of the MEMBER_OF relation to be
        		// the members of the button group.
			int len = group.getButtonCount();
			Object [] target = new Object[len];
			Enumeration elem = group.getElements();
			for (int i = 0; i < len; i++) {
			    if (elem.hasMoreElements()) {
				target[i] = elem.nextElement();
			    }
			}
			AccessibleRelation relation = 
			    new AccessibleRelation(AccessibleRelation.MEMBER_OF);
			relation.setTarget(target);
			relationSet.add(relation);
		    }
		}
	    }
	    return relationSet;
        }

        /**
         * Get the AccessibleAction associated with this object.  In the
         * implementation of the Java Accessibility API for this class, 
	 * return this object, which is responsible for implementing the
         * AccessibleAction interface on behalf of itself.
	 * 
	 * @return this object
         */
        public AccessibleAction getAccessibleAction() {
            return this;
        }

        /**
         * Get the AccessibleValue associated with this object.  In the
         * implementation of the Java Accessibility API for this class, 
	 * return this object, which is responsible for implementing the
         * AccessibleValue interface on behalf of itself.
	 * 
	 * @return this object
         */
        public AccessibleValue getAccessibleValue() {
            return this;
        }

        /**
         * Returns the number of Actions available in this object.  The 
         * default behavior of a button is to have one action - toggle 
         * the button.
         *
         * @return 1, the number of Actions in this object
         */
        public int getAccessibleActionCount() {
            return 1;
        }
    
        /**
         * Return a description of the specified action of the object.
         *
         * @param i zero-based index of the actions
         */
        public String getAccessibleActionDescription(int i) {
            if (i == 0) {
                return UIManager.getString("AbstractButton.clickText");
            } else {
                return null;
            }
        }
    
        /**
         * Perform the specified Action on the object
         *
         * @param i zero-based index of actions
         * @return true if the the action was performed; else false.
         */
        public boolean doAccessibleAction(int i) {
            if (i == 0) {
                doClick();
                return true;
            } else {
                return false;
            }
        }

        /**
         * Get the value of this object as a Number.
         *
         * @return An Integer of 0 if this isn't selected or an Integer of 1 if
         * this is selected.
         * @see AbstractButton#isSelected
         */
        public Number getCurrentAccessibleValue() {
            if (isSelected()) {
                return new Integer(1);
            } else {
                return new Integer(0);
            }
        }

        /**
         * Set the value of this object as a Number.
         *
         * @return True if the value was set.
         */
        public boolean setCurrentAccessibleValue(Number n) {
            if (n instanceof Integer) {
                int i = n.intValue();
                if (i == 0) {
                    setSelected(false);
                } else {
                    setSelected(true);
                }
                return true;
            } else {
                return false;
            }
        }

        /**
         * Get the minimum value of this object as a Number.
         *
         * @return an Integer of 0.
         */
        public Number getMinimumAccessibleValue() {
            return new Integer(0);
        }

        /**
         * Get the maximum value of this object as a Number.
         *
         * @return An Integer of 1.
         */
        public Number getMaximumAccessibleValue() {
            return new Integer(1);
        }


	/* AccessibleText ---------- */
        
	public AccessibleText getAccessibleText() {
	    View view = (View)AbstractButton.this.getClientProperty("html");
	    if (view != null) {
		return this;
	    } else {
		return null;
	    }
	}

	/**
	 * Given a point in local coordinates, return the zero-based index
	 * of the character under that Point.  If the point is invalid,
	 * this method returns -1.
	 *
         * Note: the AbstractButton must have a valid size (e.g. have
         * been added to a parent container whose ancestor container
         * is a valid top-level window) for this method to be able
         * to return a meaningful value.
	 *
	 * @param p the Point in local coordinates
	 * @return the zero-based index of the character under Point p; if 
	 * Point is invalid returns -1.
	 */
	public int getIndexAtPoint(Point p) {
	    View view = (View) AbstractButton.this.getClientProperty("html");
	    if (view != null) {
		Rectangle r = getTextRectangle();
		if (r == null) {
		    return -1;
		}
		Rectangle2D.Float shape = 
		    new Rectangle2D.Float(r.x, r.y, r.width, r.height);
		Position.Bias bias[] = new Position.Bias[1];
		return view.viewToModel(p.x, p.y, shape, bias);
	    } else {
		return -1;
	    }
	}
	
	/**
	 * Determine the bounding box of the character at the given 
	 * index into the string.  The bounds are returned in local
	 * coordinates.  If the index is invalid an empty rectangle is 
	 * returned.
	 *
         * Note: the AbstractButton must have a valid size (e.g. have
         * been added to a parent container whose ancestor container
         * is a valid top-level window) for this method to be able
         * to return a meaningful value.
	 *
	 * @param i the index into the String
	 * @return the screen coordinates of the character's the bounding box,
	 * if index is invalid returns an empty rectangle.
	 */
	public Rectangle getCharacterBounds(int i) {
	    View view = (View) AbstractButton.this.getClientProperty("html");
	    if (view != null) {
		Rectangle r = getTextRectangle();
		if (r == null) {
		    return null;
		}
		Rectangle2D.Float shape = 
		    new Rectangle2D.Float(r.x, r.y, r.width, r.height);
		try {
		    Shape charShape = 
			view.modelToView(i, shape, Position.Bias.Forward);
		    return charShape.getBounds();
		} catch (BadLocationException e) {
		    return null;
		}
	    } else {
		return null;
	    }
	}
	
	/**
	 * Return the number of characters (valid indicies) 
	 *
	 * @return the number of characters
	 */
	public int getCharCount() {
	    View view = (View) AbstractButton.this.getClientProperty("html");
	    if (view != null) {
		Document d = view.getDocument();
		if (d instanceof StyledDocument) {
		    StyledDocument doc = (StyledDocument)d;
		    return doc.getLength();
		}
	    }
	    return accessibleContext.getAccessibleName().length();
	}
	
	/**
	 * Return the zero-based offset of the caret.
	 *
	 * Note: That to the right of the caret will have the same index
	 * value as the offset (the caret is between two characters).
	 * @return the zero-based offset of the caret.
	 */
	public int getCaretPosition() {
	    // There is no caret.
	    return -1;
	}
	
        /**
         * Returns the String at a given index. 
         *
         * @param part the AccessibleText.CHARACTER, AccessibleText.WORD,
         * or AccessibleText.SENTENCE to retrieve
         * @param index an index within the text >= 0
         * @return the letter, word, or sentence,
         *   null for an invalid index or part
         */
        public String getAtIndex(int part, int index) {
            if (index < 0 || index >= getCharCount()) {
                return null;
            }
            switch (part) {
            case AccessibleText.CHARACTER:
                try {
                    return getText(index, 1);
                } catch (BadLocationException e) {
                    return null;
                }
            case AccessibleText.WORD:
                try {
                    String s = getText(0, getCharCount());
                    BreakIterator words = BreakIterator.getWordInstance();
                    words.setText(s);
                    int end = words.following(index);
                    return s.substring(words.previous(), end);
                } catch (BadLocationException e) {
                    return null;
                }
            case AccessibleText.SENTENCE:
                try {
                    String s = getText(0, getCharCount());
                    BreakIterator sentence = 
			BreakIterator.getSentenceInstance();
                    sentence.setText(s);
                    int end = sentence.following(index);
                    return s.substring(sentence.previous(), end);
                } catch (BadLocationException e) {
                    return null;
                }
            default:
                return null;
            }
        }

        /**
         * Returns the String after a given index.
         *
         * @param part the AccessibleText.CHARACTER, AccessibleText.WORD,
         * or AccessibleText.SENTENCE to retrieve
         * @param index an index within the text >= 0
         * @return the letter, word, or sentence, null for an invalid
         *  index or part
         */
        public String getAfterIndex(int part, int index) {
            if (index < 0 || index >= getCharCount()) {
                return null;
            }
            switch (part) {
            case AccessibleText.CHARACTER:
		if (index+1 >= getCharCount()) {
		   return null;
		}
                try {
                    return getText(index+1, 1);
                } catch (BadLocationException e) {
                    return null;
                }
            case AccessibleText.WORD:
                try {
                    String s = getText(0, getCharCount());
                    BreakIterator words = BreakIterator.getWordInstance();
                    words.setText(s);
                    int start = words.following(index);
		    if (start == BreakIterator.DONE || start >= s.length()) {
			return null;
		    }
		    int end = words.following(start);
		    if (end == BreakIterator.DONE || end >= s.length()) {
			return null;
		    }
                    return s.substring(start, end);
                } catch (BadLocationException e) {
                    return null;
                }
            case AccessibleText.SENTENCE:
                try {
                    String s = getText(0, getCharCount());
                    BreakIterator sentence = 
			BreakIterator.getSentenceInstance();
                    sentence.setText(s);
                    int start = sentence.following(index);
		    if (start == BreakIterator.DONE || start >= s.length()) {
			return null;
		    }
		    int end = sentence.following(start);
		    if (end == BreakIterator.DONE || end >= s.length()) {
			return null;
		    }
                    return s.substring(start, end);
                } catch (BadLocationException e) {
                    return null;
                }
            default:
                return null;
            }
        }

        /**
         * Returns the String before a given index.
         *
         * @param part the AccessibleText.CHARACTER, AccessibleText.WORD,
         *   or AccessibleText.SENTENCE to retrieve
         * @param index an index within the text >= 0
         * @return the letter, word, or sentence, null for an invalid index
         *  or part
         */
        public String getBeforeIndex(int part, int index) {
            if (index < 0 || index > getCharCount()-1) {
                return null;
            }
            switch (part) {
            case AccessibleText.CHARACTER:
		if (index == 0) {
		    return null;
		}
                try {
                    return getText(index-1, 1);
                } catch (BadLocationException e) {
                    return null;
                }
            case AccessibleText.WORD:
                try {
                    String s = getText(0, getCharCount());
                    BreakIterator words = BreakIterator.getWordInstance();
                    words.setText(s);
                    int end = words.following(index);
                    end = words.previous();
		    int start = words.previous();
		    if (start == BreakIterator.DONE) {
			return null;
		    }
                    return s.substring(start, end);
                } catch (BadLocationException e) {
                    return null;
                }
            case AccessibleText.SENTENCE:
                try {
                    String s = getText(0, getCharCount());
                    BreakIterator sentence = 
			BreakIterator.getSentenceInstance();
                    sentence.setText(s);
                    int end = sentence.following(index);
                    end = sentence.previous();
		    int start = sentence.previous();
		    if (start == BreakIterator.DONE) {
			return null;
		    }
                    return s.substring(start, end);
                } catch (BadLocationException e) {
                    return null;
                }
            default:
                return null;
            }
        }

	/**
	 * Return the AttributeSet for a given character at a given index
	 *
	 * @param i the zero-based index into the text 
	 * @return the AttributeSet of the character
	 */
	public AttributeSet getCharacterAttribute(int i) {
	    View view = (View) AbstractButton.this.getClientProperty("html");
	    if (view != null) {
		Document d = view.getDocument();
		if (d instanceof StyledDocument) {
		    StyledDocument doc = (StyledDocument)d;
		    Element elem = doc.getCharacterElement(i);
		    if (elem != null) {
			return elem.getAttributes();
		    }
		}
	    }
	    return null;
	}

	/**
	 * Returns the start offset within the selected text.
	 * If there is no selection, but there is
	 * a caret, the start and end offsets will be the same.
	 *
	 * @return the index into the text of the start of the selection
	 */
	public int getSelectionStart() {
	    // Text cannot be selected.
	    return -1;
	}

	/**
	 * Returns the end offset within the selected text.
	 * If there is no selection, but there is
	 * a caret, the start and end offsets will be the same.
	 *
	 * @return the index into teh text of the end of the selection
	 */
	public int getSelectionEnd() {
	    // Text cannot be selected.
	    return -1;
	}

	/**
	 * Returns the portion of the text that is selected. 
	 *
	 * @return the String portion of the text that is selected
	 */
	public String getSelectedText() {
	    // Text cannot be selected.
	    return null;
	}

	/*
	 * Returns the text substring starting at the specified
	 * offset with the specified length.
	 */
	private String getText(int offset, int length) 
	    throws BadLocationException {

	    View view = (View) AbstractButton.this.getClientProperty("html");
	    if (view != null) {
		Document d = view.getDocument();
		if (d instanceof StyledDocument) {
		    StyledDocument doc = (StyledDocument)d;
		    return doc.getText(offset, length);
		}
	    }
	    return null;
	}

	/*
	 * Returns the bounding rectangle for the component text.
	 */
	private Rectangle getTextRectangle() {
	    
	    String text = AbstractButton.this.getText();
	    Icon icon = (AbstractButton.this.isEnabled()) ? AbstractButton.this.getIcon() : AbstractButton.this.getDisabledIcon();
	    
	    if ((icon == null) && (text == null)) {
		return null;
	    }

	    Rectangle paintIconR = new Rectangle();
	    Rectangle paintTextR = new Rectangle();
	    Rectangle paintViewR = new Rectangle();
	    Insets paintViewInsets = new Insets(0, 0, 0, 0);

	    paintViewInsets = AbstractButton.this.getInsets(paintViewInsets);
	    paintViewR.x = paintViewInsets.left;
	    paintViewR.y = paintViewInsets.top;
	    paintViewR.width = AbstractButton.this.getWidth() - (paintViewInsets.left + paintViewInsets.right);
	    paintViewR.height = AbstractButton.this.getHeight() - (paintViewInsets.top + paintViewInsets.bottom);
	    
	    Graphics g = AbstractButton.this.getGraphics();
	    if (g == null) {
		return null;
	    }
	    String clippedText = SwingUtilities.layoutCompoundLabel(
	        (JComponent)AbstractButton.this,
		g.getFontMetrics(),
		text,
		icon,
		AbstractButton.this.getVerticalAlignment(),
		AbstractButton.this.getHorizontalAlignment(),
		AbstractButton.this.getVerticalTextPosition(),
		AbstractButton.this.getHorizontalTextPosition(),
		paintViewR,
		paintIconR,
		paintTextR,
		0);

	    return paintTextR;
	}
    }
}
