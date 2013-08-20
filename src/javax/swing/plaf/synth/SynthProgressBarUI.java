/*
 * @(#)SynthProgressBarUI.java	1.23 04/04/02
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.synth;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.Serializable;
import sun.swing.plaf.synth.SynthUI;
import com.sun.java.swing.SwingUtilities2;

/**
 * Synth's ProgressBarUI.
 *
 * @version 1.23, 04/02/04
 * @author Joshua Outwater
 */
class SynthProgressBarUI extends BasicProgressBarUI implements SynthUI,
        PropertyChangeListener {
    private SynthStyle style;

    private int progressPadding;

    public static ComponentUI createUI(JComponent x) {
        return new SynthProgressBarUI();
    }

    protected void installListeners() {
        super.installListeners();
        progressBar.addPropertyChangeListener(this);
    }

    protected void uninstallListeners() {
        super.uninstallListeners();
        progressBar.removePropertyChangeListener(this);
    }

    protected void installDefaults() {
        updateStyle(progressBar);
    }

    private void updateStyle(JProgressBar c) {
        SynthContext context = getContext(c, ENABLED);
        SynthStyle oldStyle = style;
        style = SynthLookAndFeel.updateStyle(context, this);
        if (style != oldStyle) {
            setCellLength(style.getInt(context, "ProgressBar.cellLength", 1));
            setCellSpacing(style.getInt(context, "ProgressBar.cellSpacing", 0));
            progressPadding = style.getInt(context,
                    "ProgressBar.progressPadding", 0);
        }
        context.dispose();
    }
    
    protected void uninstallDefaults() {
        SynthContext context = getContext(progressBar, ENABLED);

        style.uninstallDefaults(context);
        context.dispose();
        style = null;
    }
    
    public SynthContext getContext(JComponent c) {
        return getContext(c, getComponentState(c));
    }

    private SynthContext getContext(JComponent c, int state) {
        return SynthContext.getContext(SynthContext.class, c,
                            SynthLookAndFeel.getRegion(c), style, state);
    }

    private Region getRegion(JComponent c) {
        return SynthLookAndFeel.getRegion(c);
    }

    private int getComponentState(JComponent c) {
        return SynthLookAndFeel.getComponentState(c);
    }

    public void update(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        SynthLookAndFeel.update(context, g);
        context.getPainter().paintProgressBarBackground(context,
                          g, 0, 0, c.getWidth(), c.getHeight());
        paint(context, g);
        context.dispose();
    }

    public void paint(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        paint(context, g);
        context.dispose();
    }

    protected void paint(SynthContext context, Graphics g) {
        JProgressBar pBar = (JProgressBar)context.getComponent();
        int x = 0, y = 0, width = 0, height = 0; 
        if (!pBar.isIndeterminate()) {
            Insets pBarInsets = pBar.getInsets();
            double percentComplete = pBar.getPercentComplete();
            if (percentComplete != 0.0) {
                if (pBar.getOrientation() == JProgressBar.HORIZONTAL) {
                    x = pBarInsets.left + progressPadding;
                    y = pBarInsets.top + progressPadding;
                    width = (int)(percentComplete * (pBar.getWidth() 
                            - (pBarInsets.left + progressPadding 
                             + pBarInsets.right + progressPadding)));
                    height = pBar.getHeight() 
                            - (pBarInsets.top + progressPadding 
                             + pBarInsets.bottom + progressPadding);

                    if (!SynthLookAndFeel.isLeftToRight(pBar)) {
                        x = pBar.getWidth() - pBarInsets.right - width 
                                - progressPadding;
                    }
                } else {  // JProgressBar.VERTICAL
                    x = pBarInsets.left + progressPadding;
                    width = pBar.getWidth() 
                            - (pBarInsets.left + progressPadding 
                            + pBarInsets.right + progressPadding);
                    height = (int)(percentComplete * (pBar.getHeight()
                            - (pBarInsets.top + progressPadding
                             + pBarInsets.bottom + progressPadding)));
                    y = pBar.getHeight() - pBarInsets.bottom - height 
                            - progressPadding;

                    // When the progress bar is vertical we always paint
                    // from bottom to top, not matter what the component
                    // orientation is.
                }
            }
        } else {
            boxRect = getBox(boxRect);
            x = boxRect.x + progressPadding;
            y = boxRect.y + progressPadding;
            width = boxRect.width - progressPadding - progressPadding;
            height = boxRect.height - progressPadding - progressPadding;
        }
        context.getPainter().paintProgressBarForeground(context, g,
                x, y, width, height, pBar.getOrientation());

        if (pBar.isStringPainted() && !pBar.isIndeterminate()) {
            paintText(context, g, pBar.getString());
        }
    }

    protected void paintText(SynthContext context, Graphics g,
            String title) {
        Font font = context.getStyle().getFont(context);
        FontMetrics metrics = SwingUtilities2.getFontMetrics(progressBar, g,
                                                             font);

        if (progressBar.isStringPainted()) {
            String pBarString = progressBar.getString();
            Rectangle bounds = progressBar.getBounds();
            int strLength = context.getStyle().getGraphicsUtils(context).
                computeStringWidth(context, font, metrics, pBarString);

            // Calculate the bounds for the text.
            Rectangle textRect = new Rectangle(
                (bounds.width / 2) - (strLength / 2),
                (bounds.height -
                    (metrics.getAscent() + metrics.getDescent())) / 2,
                0, 0);

            // Progress bar isn't tall enough for the font.  Don't paint it.
            if (textRect.y < 0) {
                return;
            }

            // Paint the text.
            SynthStyle style = context.getStyle();
            g.setColor(style.getColor(context, ColorType.TEXT_FOREGROUND));
            g.setFont(style.getFont(context));
            style.getGraphicsUtils(context).paintText(context, g, title,
                                                 textRect.x, textRect.y, -1);
        }
    }

    public void paintBorder(SynthContext context, Graphics g, int x,
                            int y, int w, int h) {
        context.getPainter().paintProgressBarBorder(context, g, x, y, w, h);
    }

    public void propertyChange(PropertyChangeEvent e) {
        if (SynthLookAndFeel.shouldUpdateStyle(e)) {
            updateStyle((JProgressBar)e.getSource());
        }
    }
    
    public Dimension getPreferredSize(JComponent c) {       
        Dimension	size;
        Insets		border = progressBar.getInsets();
        FontMetrics     fontSizer = progressBar.getFontMetrics(
                                                  progressBar.getFont());
        if (progressBar.getOrientation() == JProgressBar.HORIZONTAL) {
            size = new Dimension(getPreferredInnerHorizontal());   
        } else {
            size = new Dimension(getPreferredInnerVertical());
        }
        // Ensure that the progress string will fit.
        if (progressBar.isStringPainted()) {
            String progString = progressBar.getString();
            int stringHeight = fontSizer.getHeight() +
                    fontSizer.getDescent();
            if (stringHeight > size.height) {
                size.height = stringHeight;
            }
            // This is also for completeness.
            int stringWidth = SwingUtilities2.stringWidth(
                                   progressBar, fontSizer, progString);
            if (stringWidth > size.width) {
                size.width = stringWidth;
            }
        }       

        size.width += border.left + border.right;
        size.height += border.top + border.bottom;
        
        return size; 
   }
}
