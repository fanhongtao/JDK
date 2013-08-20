/*
 * @(#)ButtonModel.java	1.26 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing;


import java.awt.event.*;
import java.awt.*;
import javax.swing.event.*;

/**
 * State Model for buttons.
 * This model is used for check boxes and radio buttons, which are
 * special kinds of buttons, as well as for normal buttons.
 * For check boxes and radio buttons, pressing the mouse selects
 * the button. For normal buttons, pressing the mouse "arms" the
 * button. Releasing the mouse over the button then initiates a
 * <i>button</i> press, firing its action event. Releasing the 
 * mouse elsewhere disarms the button.
 * <p>
 * In use, a UI will invoke {@link #setSelected} when a mouse
 * click occurs over a check box or radio button. It will invoke
 * {@link #setArmed} when the mouse is pressed over a regular
 * button and invoke {@link #setPressed} when the mouse is released.
 * If the mouse travels outside the button in the meantime, 
 * <code>setArmed(false)</code> will tell the button not to fire
 * when it sees <code>setPressed</code>. (If the mouse travels 
 * back in, the button will be rearmed.)
 * <blockquote>
 * <b>Note: </b><br>
 * A button is triggered when it is both "armed" and "pressed".
 * </blockquote>
 *
 * @version 1.26 12/19/03
 * @author Jeff Dinkins
 */
public interface ButtonModel extends ItemSelectable {
    
    /**
     * Indicates partial commitment towards pressing the
     * button. 
     *
     * @return true if the button is armed, and ready to be pressed
     * @see #setArmed
     */
    boolean isArmed();     
        
    /**
     * Indicates if the button has been selected. Only needed for
     * certain types of buttons - such as radio buttons and check boxes.
     *
     * @return true if the button is selected
     */
    boolean isSelected();
        
    /**
     * Indicates if the button can be selected or pressed by
     * an input device (such as a mouse pointer). (Check boxes
     * are selected, regular buttons are "pressed".)
     *
     * @return true if the button is enabled, and therefore
     *         selectable (or pressable)
     */
    boolean isEnabled();
        
    /**
     * Indicates if button has been pressed.
     *
     * @return true if the button has been pressed
     */
    boolean isPressed();

    /**
     * Indicates that the mouse is over the button.
     *
     * @return true if the mouse is over the button
     */
    boolean isRollover();

    /**
     * Marks the button as "armed". If the mouse button is
     * released while it is over this item, the button's action event
     * fires. If the mouse button is released elsewhere, the
     * event does not fire and the button is disarmed.
     * 
     * @param b true to arm the button so it can be selected
     */
    public void setArmed(boolean b);

    /**
     * Selects or deselects the button.
     *
     * @param b true selects the button,
     *          false deselects the button.
     */
    public void setSelected(boolean b);

    /**
     * Enables or disables the button.
     * 
     * @param b true to enable the button
     * @see #isEnabled
     */
    public void setEnabled(boolean b);

    /**
     * Sets the button to pressed or unpressed.
     * 
     * @param b true to set the button to "pressed"
     * @see #isPressed
     */
    public void setPressed(boolean b);

    /**
     * Sets or clears the button's rollover state
     * 
     * @param b true to turn on rollover
     * @see #isRollover
     */
    public void setRollover(boolean b);

    /**
     * Sets the keyboard mnemonic (shortcut key or
     * accelerator key) for this button.
     *
     * @param key an int specifying the accelerator key
     */
    public void setMnemonic(int key);

    /**
     * Gets the keyboard mnemonic for this model
     *
     * @return an int specifying the accelerator key
     * @see #setMnemonic
     */
    public int  getMnemonic();

    /**
     * Sets the actionCommand string that gets sent as part of the
     * event when the button is pressed.
     *
     * @param s the String that identifies the generated event
     */
    public void setActionCommand(String s);

    /**
     * Returns the action command for this button.
     *
     * @return the String that identifies the generated event
     * @see #setActionCommand
     */
    public String getActionCommand();

    /**
     * Identifies the group this button belongs to --
     * needed for radio buttons, which are mutually
     * exclusive within their group.
     *
     * @param group the ButtonGroup this button belongs to
     */
    public void setGroup(ButtonGroup group);
    
    /**
     * Adds an ActionListener to the button.
     *
     * @param l the listener to add
     */
    void addActionListener(ActionListener l);

    /**
     * Removes an ActionListener from the button.
     *
     * @param l the listener to remove
     */
    void removeActionListener(ActionListener l);

    /**
     * Adds an ItemListener to the button.
     *
     * @param l the listener to add
     */
    void addItemListener(ItemListener l);

    /**
     * Removes an ItemListener from the button.
     *
     * @param l the listener to remove
     */
    void removeItemListener(ItemListener l);

    /**
     * Adds a ChangeListener to the button.
     *
     * @param l the listener to add
     */
    void addChangeListener(ChangeListener l);

    /**
     * Removes a ChangeListener from the button.
     *
     * @param l the listener to remove
     */
    void removeChangeListener(ChangeListener l);

}
