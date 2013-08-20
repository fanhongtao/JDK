/*
 * @(#)SynthButtonUI.java	1.20 04/04/16
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.synth;

import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;
import sun.swing.plaf.synth.SynthUI;
import sun.swing.plaf.synth.DefaultSynthStyle;

/**
 * Synth's ButtonUI implementation.
 *
 * @version 1.20, 04/16/04
 * @author Scott Violet
 */
class SynthButtonUI extends BasicButtonUI implements
                                 PropertyChangeListener, SynthUI {
    private SynthStyle style;

    public static ComponentUI createUI(JComponent c) {
        return new SynthButtonUI();
    }

    protected void installDefaults(AbstractButton b) {
        updateStyle(b);

        LookAndFeel.installProperty(b, "rolloverEnabled", Boolean.TRUE);
    }

    protected void installListeners(AbstractButton b) {
        super.installListeners(b);
        b.addPropertyChangeListener(this);
    }

    void updateStyle(AbstractButton b) {
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

            Object value = style.get(context, getPropertyPrefix() + "iconTextGap");
            if (value != null) {
		        LookAndFeel.installProperty(b, "iconTextGap", value);
            }

            value = style.get(context, getPropertyPrefix() + "contentAreaFilled");
            LookAndFeel.installProperty(b, "contentAreaFilled",
                                        value != null? value : Boolean.TRUE);

            if (oldStyle != null) {
                uninstallKeyboardActions(b);
                installKeyboardActions(b);
            }

        }
        context.dispose();
    }

    protected void uninstallListeners(AbstractButton b) {
        super.uninstallListeners(b);
        b.removePropertyChangeListener(this);
    }

    protected void uninstallDefaults(AbstractButton b) {
        SynthContext context = getContext(b, ENABLED);

        style.uninstallDefaults(context);
        context.dispose();
        style = null;
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
        if (SynthLookAndFeel.selectedUI == this) {
            return SynthLookAndFeel.selectedUIState | SynthConstants.ENABLED;
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

    public void update(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        SynthLookAndFeel.update(context, g);
        paintBackground(context, g, c);
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
        context.getStyle().getGraphicsUtils(context).paintText(
            context, g, b.getText(), getIcon(b),
            b.getHorizontalAlignment(), b.getVerticalAlignment(),
            b.getHorizontalTextPosition(), b.getVerticalTextPosition(),
            b.getIconTextGap(), b.getDisplayedMnemonicIndex(),
            getTextShiftOffset(context));
    }

    void paintBackground(SynthContext context, Graphics g, JComponent c) {
        context.getPainter().paintButtonBackground(context, g, 0, 0,
                                                c.getWidth(), c.getHeight());
    }

    public void paintBorder(SynthContext context, Graphics g, int x,
                            int y, int w, int h) {
        context.getPainter().paintButtonBorder(context, g, x, y, w, h);
    }

    /**
     * Returns the default icon. This should NOT callback
     * to the JComponent.
     *
     * @param b AbstractButton the iocn is associated with
     * @return default icon 
     */
   
    protected Icon getDefaultIcon(AbstractButton b) {
        SynthContext context = getContext(b);
        Icon icon = context.getStyle().getIcon(context, getPropertyPrefix() + "icon");
        context.dispose();
        return icon;
    }
    
    /**
     * Returns the Icon to use in painting the button.
     */
    protected Icon getIcon(AbstractButton b) {
        Icon icon = getEnabledIcon(b);

        ButtonModel model = b.getModel();
        Icon tmpIcon = null; 

        if (!model.isEnabled()) {
            tmpIcon = getSynthDisabledIcon(b);
        } else if (model.isPressed() && model.isArmed()) {
            tmpIcon = getPressedIcon(b);
        } else if (b.isRolloverEnabled() && model.isRollover()) {
            tmpIcon = getRolloverIcon(b);
        } else if (model.isSelected()) {
            tmpIcon = getSelectedIcon(b);
        }
        if (tmpIcon != null) {
            icon = tmpIcon;
        }
        if(icon == null) {
            return getDefaultIcon(b);
        }
        return icon;
    }

    private Icon getSynthIcon(AbstractButton b, int synthConstant) {
        return style.getIcon(getContext(b, synthConstant), getPropertyPrefix() + "icon");
    }
    
    private Icon getEnabledIcon(AbstractButton b) {
        Icon tmpIcon = b.getIcon();
        if(tmpIcon == null) {
            tmpIcon = getSynthIcon(b, SynthConstants.ENABLED); 
        }
        return tmpIcon;
    }
    
    private Icon getSelectedIcon(AbstractButton b) {
        Icon tmpIcon = b.getSelectedIcon();
        if(tmpIcon == null) {
            tmpIcon = getSynthIcon(b, SynthConstants.SELECTED);
        }
        return tmpIcon;
    }

    private Icon getRolloverIcon(AbstractButton b) {
        ButtonModel model = b.getModel();
        Icon tmpIcon;
        if (model.isSelected()) {
            tmpIcon = b.getRolloverSelectedIcon();
            if (tmpIcon == null) {
                tmpIcon = getSynthIcon(b, SynthConstants.SELECTED);
                if (tmpIcon == null) {
                  tmpIcon = getSelectedIcon(b); 
                }
            }
        } else {
            tmpIcon = b.getRolloverIcon();
            if (tmpIcon == null) {
              tmpIcon = getSynthIcon(b, SynthConstants.MOUSE_OVER); 
            }
        }
        return tmpIcon;
    }

    private Icon getPressedIcon(AbstractButton b) {
        Icon tmpIcon;
        tmpIcon = b.getPressedIcon();
        if (tmpIcon == null) {
            tmpIcon = getSynthIcon(b, SynthConstants.PRESSED);
            if (tmpIcon == null) {
              tmpIcon = getSelectedIcon(b);
            }
        }
        return tmpIcon;
    }

    private Icon getSynthDisabledIcon(AbstractButton b) {
        ButtonModel model = b.getModel();
        Icon tmpIcon;
        if (model.isSelected()) {
            tmpIcon = b.getDisabledSelectedIcon();
            if(tmpIcon == null) {
              tmpIcon = getSynthIcon(b, SynthConstants.DISABLED|SynthConstants.SELECTED);   
            }
        } else {
            tmpIcon = b.getDisabledIcon();
            if(tmpIcon == null) {
                tmpIcon = getSynthIcon(b, SynthConstants.DISABLED);
            }
        }
        return tmpIcon;
    }

    /**
     * Returns the amount to shift the text/icon when painting.
     */
    protected int getTextShiftOffset(SynthContext state) {
        AbstractButton button = (AbstractButton)state.getComponent();
        ButtonModel model = button.getModel();

        if (model.isArmed() && model.isPressed() &&
                               button.getPressedIcon() == null) {
            return state.getStyle().getInt(state, getPropertyPrefix() +
                                           "textShiftOffset", 0);
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
        Dimension size = ss.getStyle().getGraphicsUtils(ss).getMinimumSize(
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
        Dimension size = ss.getStyle().getGraphicsUtils(ss).getPreferredSize(
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
        Dimension size = ss.getStyle().getGraphicsUtils(ss).getMaximumSize(
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

    public void propertyChange(PropertyChangeEvent e) {
        if (SynthLookAndFeel.shouldUpdateStyle(e)) {
            updateStyle((AbstractButton)e.getSource());
        }
    }
}
