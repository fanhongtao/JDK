/*
 * @(#)AbstractButton.java	1.2 00/01/12
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */
package javax.swing;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.Serializable;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.accessibility.*;

/**
 * Defines the common behaviors for the JButton, JToggleButton, JCheckbox,
 * and the JRadioButton classes.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.101 04/22/99
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
    /** Identifies a change to having the border drawn, or having it not drawn. */
    public static final String BORDER_PAINTED_CHANGED_PROPERTY = "borderPainted";
    /** Identifies a change to having the border highlighted when focused, or not. */
    public static final String FOCUS_PAINTED_CHANGED_PROPERTY = "focusPainted";
    /** Identifies a change in the button's  */
    public static final String ROLLOVER_ENABLED_CHANGED_PROPERTY = "rolloverEnabled";
    /** Identifies a change from rollover enabled to disabled or back to enabled. */
    public static final String CONTENT_AREA_FILLED_CHANGED_PROPERTY = "contentAreaFilled";

    // Icons
    /** Identifies a change to the icon that represents the button. */
    public static final String ICON_CHANGED_PROPERTY = "icon";

    /** Identifies a change to the icon used when the button has been pressed. */
    public static final String PRESSED_ICON_CHANGED_PROPERTY = "pressedIcon";
    /** Identifies a change to the icon used when the button has been selected. */
    public static final String SELECTED_ICON_CHANGED_PROPERTY = "selectedIcon";

    /** Identifies a change to the icon used when the cursor is over the button. */
    public static final String ROLLOVER_ICON_CHANGED_PROPERTY = "rolloverIcon";
    /** Identifies a change to the icon used when the cursror is over the button and it has been selected. */
    public static final String ROLLOVER_SELECTED_ICON_CHANGED_PROPERTY = "rolloverSelectedIcon";

    /** Identifies a change to the icon used when the button has been disabled. */
    public static final String DISABLED_ICON_CHANGED_PROPERTY = "disabledIcon";
    /** Identifies a change to the icon used when the button has been disabled and selected. */
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

    /** 
     * The button's model listeners.
     */
    protected ChangeListener changeListener = null;
    protected ActionListener actionListener = null;
    protected ItemListener itemListener = null;

    /**
     * Only one ChangeEvent is needed per button instance since the
     * event's only state is the source property.  The source of events
     * generated is always "this".
     */
    protected transient ChangeEvent changeEvent;
    
    /**
     * Returns the button's text.
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
     */
    public boolean isSelected() {
        return model.isSelected();
    }
 
    /**
     * Sets the state of the button. Note that this method does not
     * trigger an actionEvent. Call doClick() to perform a programatic
     * action change.
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
     * Programatically perform a "click". This does the same
     * thing as if the user had pressed and released the button.
     */
    public void doClick() {
        doClick(68);
    }

    /**
     * Programatically perform a "click". This does the same
     * thing as if the user had pressed and released the button.
     * The button stays visually "pressed" for pressTime milliseconds.
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
     * the label. Setting to null will cause the button to
     * use the default margin.  The button's default Border
     * object will use this value to create the proper margin.
     * However, if a non-default border is set on the button, 
     * it is that Border object's responsibility to create the
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
     * @see #setMargin
     */
    public Insets getMargin() {
        return margin;
    }

    /**
     * Returns the default icon.
     * @see #setIcon
     */
    public Icon getIcon() {
        return defaultIcon;
    }
    
    /**
     * Sets the button's default icon. This icon is
     * also used as the "pressed" and "disabled" icon if
     * there is no explicitly set pressed icon.
     * @param g the icon used as the default image
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
                oldValue, defaultIcon);
        }
        if (defaultIcon != oldValue) {
            if (defaultIcon == null || oldValue == null ||
                defaultIcon.getIconWidth() != oldValue.getIconWidth() ||
                defaultIcon.getIconHeight() != oldValue.getIconHeight()) {
                revalidate();
            } 
            repaint();
        }
    }
    
    /**
     * Returns the pressed icon for the button.
     * @see #setPressedIcon
     */
    public Icon getPressedIcon() {
        return pressedIcon;
    }
    
    /**
     * Sets the pressed icon for the button.
     * @param g the icon used as the "pressed" image
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
     * @see #setSelectedIcon
     */
    public Icon getSelectedIcon() {
        return selectedIcon;
    }
    
    /**
     * Sets the selected icon for the button.
     * @param g the icon used as the "selected" image
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
     * @see #setRolloverIcon
     */
    public Icon getRolloverIcon() {
        return rolloverIcon;
    }
    
    /**
     * Sets the rollover icon for the button.
     * @param g the icon used as the "rollover" image
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
     * Returns the rollover seletion icon for the button.
     * @see #setRolloverSelectedIcon
     */
    public Icon getRolloverSelectedIcon() {
        return rolloverSelectedIcon;
    }
    
    /**
     * Sets the rollover selected icon for the button.
     * @param g the icon used as the "selected rollover" image
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
     * If not no disabled icon has been set, the button constructs
     * one from the default icon. 
     * PENDING(jeff): the disabled icon really should be created 
     * (if necesary) by the L&F.
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
     * @param g the icon used as the disabled image
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
     * PENDING(jeff): the disabled selection icon really should be created 
     * (if necesary) by the L&F.
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
     * @param g the icon used as the disabled selection image
     * @see #getDisabledSelectedIcon
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
     * Valid keys: CENTER (the default), TOP, BOTTOM
     */
    public int getVerticalAlignment() {
        return verticalAlignment;
    }
    
    /**
     * Sets the vertical alignment of the icon and text.
     * Valid keys: CENTER (the default), TOP, BOTTOM
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
     * Valid keys: CENTER (the default), LEFT, RIGHT
     */
    public int getHorizontalAlignment() {
        return horizontalAlignment;
    }
    
    /**
     * Sets the horizontal alignment of the icon and text.
     * Valid keys: CENTER (the default), LEFT, RIGHT, LEADING or TRAILING
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
     * Returns the vertical position of the text relative to the icon
     * Valid keys: CENTER (the default), TOP, BOTTOM
     */
    public int getVerticalTextPosition() {
        return verticalTextPosition;
    }
    
    /**
     * Sets the vertical position of the text relative to the icon.
     * Valid keys: CENTER (the default), TOP, BOTTOM
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
     * Sets the horizontal position of the text relative to the icon.
     * Valid keys: RIGHT (the default), LEFT, CENTER
     */
    public int getHorizontalTextPosition() {
        return horizontalTextPosition;
    }
    
    /**
     * Sets the horizontal position of the text relative to the icon.
     * Valid keys: RIGHT (the default), LEFT, CENTER, LEADING, TRAILING
     * @exception IllegalArgumentException
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
     * Verify that key is a legal value for the horizontalAlignment properties.
     *
     * @param key the property value to check
     * @param exception the IllegalArgumentException detail message 
     * @exception IllegalArgumentException if key isn't LEFT, CENTER, RIGHT,
     * LEADING or TRAILING.
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
     * Ensures that the key is a valid. Throws an IllegalArgument exception
     * exception otherwise.
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
     */
    public void setActionCommand(String actionCommand) {
        getModel().setActionCommand(actionCommand);
    }
    
    /**
     * Returns the action command for this button. 
     */
    public String getActionCommand() {
        String ac = getModel().getActionCommand();
        if(ac == null) {
            ac = getText();
        }
        return ac;
    }
    
    /**
     * Returns whether the border should be painted.
     * @see #setBorderPainted
     */
    public boolean isBorderPainted() {
        return paintBorder;
    }
    
    /**
     * Sets whether the border should be painted.
     * @param b if true and border property is not null, the border is painted.
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
     * Paint the button's border if BorderPainted property is true.
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
     * @see #setFocusPainted
     */
    public boolean isFocusPainted() {
        return paintFocus;
    }
    
    /**
     * Sets whether focus should be painted.
     * @param b if true, the focus state is painted.
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
     * @see #setFocusPainted
     */
    public boolean isContentAreaFilled() {
        return contentAreaFilled;
    }
    
    /**
     * Sets whether the button should paint the content area
     * or leave it transparent.  If you wish to have a transparent
     * button, for example and icon only button, then you should set
     * this to false.  Do not call setOpaque(false).  Whether the button
     * follows the RepaintManager's concept of opacity is L&F depandant.
     *
     * This function may cause the component's opaque property to change.
     *
     * The exact behavior of calling this function varies on a
     * component-by-component and L&F-by-L&F basis.
     *
     * @param b if true, rollover effects should be painted.
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
     * @see #setFocusPainted
     */
    public boolean isRolloverEnabled() {
        return rolloverEnabled;
    }
    
    /**
     * Sets whether rollover effects should be enabled.
     * @param b if true, rollover effects should be painted.
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
     * Get the keyboard mnemonic from the the current model 
     */
    public int getMnemonic() {
        return model.getMnemonic();
    }

    /**
     * Set the keyboard mnemonic on the current model.
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
     */
    public void setMnemonic(char mnemonic) {
        int vk = (int) mnemonic;
        if(vk >= 'a' && vk <='z')
            vk -= ('a' - 'A');
        setMnemonic(vk);
    }

    /**
     * Get the model that this button represents.
     * @see #setModel
     */
    public ButtonModel getModel() {
        return model;
    }
    
    /**
     * Set the model that this button represents.
     * @param m the Model
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
     * Returns the button's current UI.
     * @see #setUI
     */
    public ButtonUI getUI() {
        return (ButtonUI) ui;
    }

    
    /**
     * Sets the button's UI.
     * @param ui the new ButtonUI
     * @see #getUI
     */
    public void setUI(ButtonUI ui) {
        super.setUI(ui);
    }

    
    /**
     * Gets a new UI object from the default UIFactory. Subtypes of
     * AbstractButton should override this to update the UI. For
     * example, JButton might do the following:
     *      setUI((ButtonUI)UIManager.getUI(
     *          "ButtonUI", "javax.swing.plaf.basic.BasicButtonUI", this));
     */
    public void updateUI() {
    }
    
    /**
     * Adds a ChangeListener to the button.
     */
    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }
    
    /**
     * Removes a ChangeListener from the button.
     */
    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }
    
    /*
     * Notify all listeners that have registered interest for
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
     * adds an ActionListener to the button
     */
    public void addActionListener(ActionListener l) {
        listenerList.add(ActionListener.class, l);
    }
    
    /**
     * removes an ActionListener from the button
     */
    public void removeActionListener(ActionListener l) {
        listenerList.remove(ActionListener.class, l);
    }
    
    
    /**
     * Subclasses that want to handle ChangeEvents differently
     * can override this to return another ChangeListener implementation.
     */
    protected ChangeListener createChangeListener() {
        return (ChangeListener) new ButtonChangeListener();
    }

    /**
     * Extend ChangeListener to be serializable
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


    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
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
    
    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance 
     * is lazily created using the parameters passed into 
     * the fire method.
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
     */
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        model.setEnabled(b);
    }

    // *** Deprecated java.awt.Button APIs below *** //
    
    /**
     * Returns the label text.
     *
     * @return a String containing the label
     * @deprecated - Replaced by getText()
     */
    public String getLabel() {
        return getText();
    }
    
    /**
     * Sets the label text.
     *
     * @param label  a String containing the text
     * @deprecated - Replaced by setText(text)
     * @beaninfo
     *        bound: true
     *  description: Replace by setText(text)
     */
    public void setLabel(String label) {
        setText(label);
    }

    /**
     * adds an ItemListener to the checkbox
     */
    public void addItemListener(ItemListener l) {
        listenerList.add(ItemListener.class, l);
    }
    
    /**
     * removes an ItemListener from the button
     */
    public void removeItemListener(ItemListener l) {
        listenerList.remove(ItemListener.class, l);
    }

   /**
     * Returns an array (length 1) containing the label or null if the 
     * button is not selected.
     *
     * @return an array containing 1 Object -- the text of the button
     *         -- if the item is selected, otherwise null
     */
    public synchronized Object[] getSelectedObjects() {
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
                if (accessibleContext != null) {
                    accessibleContext.firePropertyChange(
                        AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
                        null, AccessibleState.FOCUSED);
                }
            }
            public void focusLost(FocusEvent event) {
                if (accessibleContext != null) {
                    accessibleContext.firePropertyChange(
                        AccessibleContext.ACCESSIBLE_STATE_PROPERTY,
                        null, AccessibleState.FOCUSED);
                }
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
     * Returns a string representation of this AbstractButton. This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * <P>
     * Overriding paramString() to provide information about the
     * specific new aspects of the JFC components.
     * 
     * @return  a string representation of this AbstractButton.
     */
    protected String paramString() {
	String defaultIconString = (defaultIcon != null ?
				    defaultIcon.toString() : "");
	String pressedIconString = (pressedIcon != null ?
				    pressedIcon.toString() : "");
	String disabledIconString = (disabledIcon != null ?
				     disabledIcon.toString() : "");
	String selectedIconString = (selectedIcon != null ?
				     selectedIcon.toString() : "");
	String disabledSelectedIconString = (disabledSelectedIcon != null ?
					     disabledSelectedIcon.toString()
					     : "");
	String rolloverIconString = (rolloverIcon != null ?
				     rolloverIcon.toString() : "");
	String rolloverSelectedIconString = (rolloverSelectedIcon != null ?
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
     * Accessiblity support.
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
        AccessibleValue {

        /**
         * Get the accessible name of this object.  
         *
         * @return the localized name of the object -- can be null if this 
         * object does not have a name
         */
        public String getAccessibleName() {
            if (accessibleName != null) {
                return accessibleName;
            } else {
                if (getText() == null) {
                    return super.getAccessibleName();
                } else {
                    return getText();
                }
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
         * Get the AccessibleAction associated with this object if one
         * exists.  Otherwise return null.
         */
        public AccessibleAction getAccessibleAction() {
            return this;
        }

        /**
         * Get the AccessibleValue associated with this object if one
         * exists.  Otherwise return null.
         */
        public AccessibleValue getAccessibleValue() {
            return this;
        }

        /**
         * Returns the number of Actions available in this object.
         * If there is more than one, the first one is the "default"
         * action.
         *
         * @return the number of Actions in this object
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
                // [[[PENDING:  WDW -- need to provide a localized string]]]
                return new String("click");
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
         * @return An Integer of 0.
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
    }
}
