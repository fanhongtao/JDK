/*
 * @(#)SynthButtonUI.java	1.16 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;
 
import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.View;

/**
 * Synth's ButtonUI, derives from BasicButtonUI 1.106.
 *
 * @version 1.16, 01/23/03
 * @author Jeff Dinkins
 */
class SynthButtonUI extends ButtonUI implements SynthUI {
    /**
     * The SkinsButtonListener is shared among all buttons.
     */
    private static SynthButtonListener sharedButtonListener;

    private SynthStyle style;

    /**
     * Returns a ButtonUI implementation.
     */
    public static ComponentUI createUI(JComponent c) {
        return new SynthButtonUI();
    }

    /**
     * Returns the prefix used in looking up property values.
     */
    protected String getPropertyPrefix() {
        return "Button.";
    }


    // ********************************
    //          Install PLAF
    // ********************************
    public void installUI(JComponent c) {
        installDefaults((AbstractButton)c);
        installListeners((AbstractButton)c);
        installKeyboardActions((AbstractButton)c);
	BasicHTML.updateRenderer(c, ((AbstractButton) c).getText());
    }

    protected void installDefaults(AbstractButton b) {
        fetchStyle(b);

        b.setRolloverEnabled(true);
    }

    protected void installListeners(AbstractButton b) {
        SynthButtonListener listener = createButtonListener(b);

        if (listener != null) {
            b.addMouseListener(listener);
            b.addMouseMotionListener(listener);
            b.addFocusListener(listener);
            b.addPropertyChangeListener(listener);
            b.addChangeListener(listener);
        }
    }
    
    protected void installKeyboardActions(AbstractButton b) {
        SynthButtonListener listener = getButtonListener(b);

        if (listener != null) {
            listener.installKeyboardActions(b);
        }
    }

    void fetchStyle(AbstractButton b) {
        SynthContext context = getContext(b, SynthConstants.ENABLED);
        SynthStyle oldStyle = style;
        style = SynthLookAndFeel.updateStyle(context, this);
        if (style != oldStyle) {
            if (b.getMargin() == null ||
                                (b.getMargin() instanceof UIResource)) {
                Insets margin = (Insets)style.get(context,getPropertyPrefix() +
                                                  "margin");

                if (margin == null) {
                    // Some places assume margins are non-null.
                    margin = SynthLookAndFeel.EMPTY_UIRESOURCE_INSETS;
                }
                b.setMargin(margin);
            }

            int iconTextGap = style.getInt(context, getPropertyPrefix() +
                                           "iconTextGap", -1);

            if (iconTextGap != -1) {
                b.setIconTextGap(iconTextGap);
            }

            b.setContentAreaFilled(style.getBoolean(context,
                            getPropertyPrefix() + "contentAreaFilled", true));
        }
        context.dispose();
    }

        
    // ********************************
    //         Uninstall PLAF
    // ********************************
    public void uninstallUI(JComponent c) {
        uninstallKeyboardActions((AbstractButton) c);
        uninstallListeners((AbstractButton) c);
        uninstallDefaults((AbstractButton) c);
	BasicHTML.updateRenderer(c, "");
    }

    protected void uninstallKeyboardActions(AbstractButton b) {
        SynthButtonListener listener = getButtonListener(b);

        if (listener != null) {
            listener.uninstallKeyboardActions(b);
        }
    }

    protected void uninstallListeners(AbstractButton b) {
        SynthButtonListener listener = getButtonListener(b);

        if (listener != null) {
            b.removeMouseListener(listener);
            b.removeMouseListener(listener);
            b.removeMouseMotionListener(listener);
            b.removeFocusListener(listener);
            b.removeChangeListener(listener);
            b.removePropertyChangeListener(listener);
        }
    }

    protected void uninstallDefaults(AbstractButton b) {
        SynthContext context = getContext(b, ENABLED);

        style.uninstallDefaults(context);
        context.dispose();
        style = null;
    }

    // ********************************
    //        Create Listeners 
    // ********************************
    protected SynthButtonListener createButtonListener(AbstractButton b) {
        if (sharedButtonListener == null) {
            sharedButtonListener = new SynthButtonListener();
        }
        return sharedButtonListener;
    }

    public SynthContext getContext(JComponent c) {
        return getContext(c, getComponentState(c));
    }

    SynthContext getContext(JComponent c, int state) {
        Region region = getRegion(c);
        return SynthContext.getContext(SynthContext.class, c, region,
                                       style, state);
    }

    private Region getRegion(JComponent c) {
        return SynthLookAndFeel.getRegion(c);
    }

    /**
     * Returns the current state of the passed in <code>AbstractButton</code>.
     */
    private int getComponentState(JComponent c) {
        int state = ENABLED;

        if (!c.isEnabled()) {
            state = DISABLED;
        }
        ButtonModel model = ((AbstractButton)c).getModel();

        if (model.isPressed()) {
            if (model.isArmed()) {
                state = PRESSED;
            }
            else {
                state = MOUSE_OVER;
            }
        }
        else if (model.isRollover()) {
            state = MOUSE_OVER;
        }
        if (model.isSelected()) {
            state |= SELECTED;
        }
        if (c.isFocusOwner()) {
            state |= FOCUSED;
        }
        if ((c instanceof JButton) && ((JButton)c).isDefaultButton()) {
            state |= DEFAULT;
        }
        return state;
    }

    // ********************************
    //          Paint Methods
    // ********************************

    /**
     * Notifies this UI delegate that it's time to paint the specified
     * component.  This method is invoked by <code>JComponent</code> 
     * when the specified component is being painted. 
     * <p>If <code>c</code> is opaque, this will paint the background using
     * the <code>GraphicsEngine</code> method <code>paintBackground</code>.
     */
    public void update(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        SynthLookAndFeel.update(context, g);
        paint(context, g);
        context.dispose();
    }

    public void paint(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        paint(context, g);
        context.dispose();
    }

    protected void paint(SynthContext context, Graphics g) {
        AbstractButton b = (AbstractButton)context.getComponent();

        g.setColor(context.getStyle().getColor(context,
                                               ColorType.TEXT_FOREGROUND));
        g.setFont(style.getFont(context));
        context.getStyle().getSynthGraphics(context).paintText(
            context, g, b.getText(), getIcon(b),
            b.getHorizontalAlignment(), b.getVerticalAlignment(),
            b.getHorizontalTextPosition(), b.getVerticalTextPosition(),
            b.getIconTextGap(), b.getDisplayedMnemonicIndex(),
            getTextShiftOffset(context));
    }

    /**
     * Returns the Icon to use in painting the button.
     */
    protected Icon getIcon(AbstractButton b) {
        Icon icon = b.getIcon();

        if (icon == null) {
            return null;
        }

        ButtonModel model = b.getModel();
        Icon tmpIcon = null;

        if (!model.isEnabled()) {
            if (model.isSelected()) {
                tmpIcon = b.getDisabledSelectedIcon();
            } else {
                tmpIcon = b.getDisabledIcon();
            }
        } else if (model.isPressed() && model.isArmed()) {
            tmpIcon = b.getPressedIcon();
            if (tmpIcon == null) {
                tmpIcon = b.getSelectedIcon();
            }
        } else if (b.isRolloverEnabled() && model.isRollover()) {
            if (model.isSelected()) {
                tmpIcon = b.getRolloverSelectedIcon();
                if (tmpIcon == null) {
                    tmpIcon = b.getSelectedIcon();
                }
            } else {
                tmpIcon = b.getRolloverIcon();
            }
        } else if (model.isSelected()) {
            tmpIcon = b.getSelectedIcon();
        }

        if (tmpIcon != null) {
            icon = tmpIcon;
        }

        return icon;
    }

    /**
     * Returns the amount to shift the text/icon when painting.
     */
    protected int getTextShiftOffset(SynthContext state) {
        AbstractButton button = (AbstractButton)state.getComponent();
        ButtonModel model = button.getModel();

        if (model.isArmed() && model.isPressed() &&
                               button.getPressedIcon() != null) {
            int tso = state.getStyle().getInt(state, getPropertyPrefix() +
                                              "textShiftOffset", -1);

            if (tso != -1) {
                return tso;
            }
        }
        return 0;
    }

    // ********************************
    //          Layout Methods
    // ********************************
    public Dimension getMinimumSize(JComponent c) {
        if (c.getComponentCount() > 0 && c.getLayout() != null) {
            return null;
        }
        AbstractButton b = (AbstractButton)c;
        SynthContext ss = getContext(c);
        Dimension size = ss.getStyle().getSynthGraphics(ss).getMinimumSize(
               ss, ss.getStyle().getFont(ss), b.getText(), getSizingIcon(b),
               b.getHorizontalAlignment(), b.getVerticalAlignment(),
               b.getHorizontalTextPosition(),
               b.getVerticalTextPosition(), b.getIconTextGap(),
               b.getDisplayedMnemonicIndex());

        ss.dispose();
        return size;
    }

    public Dimension getPreferredSize(JComponent c) {
        if (c.getComponentCount() > 0 && c.getLayout() != null) {
            return null;
        }
        AbstractButton b = (AbstractButton)c;
        SynthContext ss = getContext(c);
        Dimension size = ss.getStyle().getSynthGraphics(ss).getPreferredSize(
               ss, ss.getStyle().getFont(ss), b.getText(), getSizingIcon(b),
               b.getHorizontalAlignment(), b.getVerticalAlignment(),
               b.getHorizontalTextPosition(),
               b.getVerticalTextPosition(), b.getIconTextGap(),
               b.getDisplayedMnemonicIndex());

        ss.dispose();
        return size;
    }

    public Dimension getMaximumSize(JComponent c) {
        if (c.getComponentCount() > 0 && c.getLayout() != null) {
            return null;
        }

        AbstractButton b = (AbstractButton)c;
        SynthContext ss = getContext(c);
        Dimension size = ss.getStyle().getSynthGraphics(ss).getMaximumSize(
               ss, ss.getStyle().getFont(ss), b.getText(), getSizingIcon(b),
               b.getHorizontalAlignment(), b.getVerticalAlignment(),
               b.getHorizontalTextPosition(),
               b.getVerticalTextPosition(), b.getIconTextGap(),
               b.getDisplayedMnemonicIndex());

        ss.dispose();
        return size;
    }

    /**
     * Returns the Icon used in calculating the pref/min/max size.
     */
    protected Icon getSizingIcon(AbstractButton b) {
        // NOTE: this is slightly different than BasicButtonUI, where it
        // would just use getIcon, but this should be ok.
        return (b.isEnabled()) ? b.getIcon() : b.getDisabledIcon();
    }

    /**
     * Returns the SkinsButtonListener install on the passed in button.
     */
    private SynthButtonListener getButtonListener(AbstractButton b) {
        return (SynthButtonListener)SynthLookAndFeel.getSynthEventListener(b);
    }
}
