/*
 * @(#)SynthInternalFrameTitlePane.java	1.22 04/09/10
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.synth;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import javax.swing.border.*;
import javax.swing.event.InternalFrameEvent;
import java.util.EventListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.VetoableChangeListener;
import java.beans.PropertyVetoException;
import sun.swing.plaf.synth.SynthUI;

/**
 * The class that manages a synth title bar
 *
 * @version 1.22 09/10/04
 * @author David Kloba
 * @author Joshua Outwater
 * @author Steve Wilson
 */
class SynthInternalFrameTitlePane extends BasicInternalFrameTitlePane
        implements SynthUI, PropertyChangeListener {

    protected JPopupMenu systemPopupMenu;
    protected JButton menuButton;

    private SynthStyle style;

    public SynthInternalFrameTitlePane(JInternalFrame f) {
        super(f);
    }

    public String getUIClassID() {
        return "InternalFrameTitlePaneUI";
    }

    public SynthContext getContext(JComponent c) {
        return getContext(c, getComponentState(c));
    }

    public SynthContext getContext(JComponent c, int state) {
        return SynthContext.getContext(SynthContext.class, c,
                    SynthLookAndFeel.getRegion(c), style, state);
    }

    private Region getRegion(JComponent c) {
        return SynthLookAndFeel.getRegion(c);
    }

    private int getComponentState(JComponent c) {
        if (frame != null) {
            if (frame.isSelected()) {
                return SELECTED;
            }
        }
        return SynthLookAndFeel.getComponentState(c);
    }

    protected void addSubComponents() {
	menuButton.setName("InternalFrameTitlePane.menuButton");
        iconButton.setName("InternalFrameTitlePane.iconifyButton");
        maxButton.setName("InternalFrameTitlePane.maximizeButton");
        closeButton.setName("InternalFrameTitlePane.closeButton");

	add(menuButton);
	add(iconButton);
	add(maxButton);
	add(closeButton);
    }

    protected void installListeners() {
        super.installListeners();
        frame.addPropertyChangeListener(this);
    }

    protected void uninstallListeners() {
        frame.removePropertyChangeListener(this);
        super.uninstallListeners();
    }

    private void updateStyle(JComponent c) {
        SynthContext context = getContext(this, ENABLED);
        SynthStyle oldStyle = style;
        style = SynthLookAndFeel.updateStyle(context, this);
        if (style != oldStyle) {
            maxIcon =
                style.getIcon(context,"InternalFrameTitlePane.maximizeIcon");
            minIcon =
                style.getIcon(context,"InternalFrameTitlePane.minimizeIcon");
            iconIcon =
                style.getIcon(context,"InternalFrameTitlePane.iconifyIcon");
            closeIcon =
                style.getIcon(context,"InternalFrameTitlePane.closeIcon");
        }
        context.dispose();
    }

    protected void installDefaults() {
        super.installDefaults();
        updateStyle(this);
    }

    protected void uninstallDefaults() {
        SynthContext context = getContext(this, ENABLED);
        style.uninstallDefaults(context);
        context.dispose();
        style = null;
        JInternalFrame.JDesktopIcon di = frame.getDesktopIcon();
        if(di != null && di.getComponentPopupMenu() == systemPopupMenu) {
            // Release link to systemMenu from the JInternalFrame
            di.setComponentPopupMenu(null);
        }
        super.uninstallDefaults();
    }

    private static class JPopupMenuUIResource extends JPopupMenu implements
        UIResource { }

    protected void assembleSystemMenu() {
        systemPopupMenu = new JPopupMenuUIResource();
        addSystemMenuItems(systemPopupMenu);
	enableActions();
        menuButton = createNoFocusButton();
	menuButton.setIcon(frame.getFrameIcon());
        menuButton.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                showSystemMenu();
            }
        });
	JPopupMenu p = frame.getComponentPopupMenu();
	if (p == null || p instanceof UIResource) {
	    frame.setComponentPopupMenu(systemPopupMenu);
	}
	if (frame.getDesktopIcon() != null) {
	    p = frame.getDesktopIcon().getComponentPopupMenu();
	    if (p == null || p instanceof UIResource) {
		frame.getDesktopIcon().setComponentPopupMenu(systemPopupMenu);
	    }
	}
	setInheritsPopupMenu(true);
    }

    protected void addSystemMenuItems(JPopupMenu menu) {
        // PENDING: this should all be localizable!
        JMenuItem mi = (JMenuItem)menu.add(restoreAction);
	mi.setMnemonic('R');
	mi = (JMenuItem)menu.add(moveAction);
	mi.setMnemonic('M');
	mi = (JMenuItem)menu.add(sizeAction);
	mi.setMnemonic('S');
	mi = (JMenuItem)menu.add(iconifyAction);
	mi.setMnemonic('n');
	mi = (JMenuItem)menu.add(maximizeAction);
	mi.setMnemonic('x');
	menu.add(new JSeparator());
	mi = (JMenuItem)menu.add(closeAction);
	mi.setMnemonic('C');
    }

    protected void showSystemMenu() {
        Dimension dim = new Dimension();
        Insets insets = frame.getInsets();
        dim.width += insets.left + insets.right;
        dim.height += insets.bottom + insets.top;
        if (!frame.isIcon()) {
            systemPopupMenu.show(menuButton,
                getX() - dim.width,
                getY() + getHeight() - dim.height);
        } else {
            systemPopupMenu.show(menuButton,
                getX() - dim.width,
                getY() - systemPopupMenu.getPreferredSize().height -
                    dim.height);
        }
    }

    // SynthInternalFrameTitlePane has no UI, we'll invoke paint on it.
    public void paintComponent(Graphics g) {
        SynthContext context = getContext(this);
        SynthLookAndFeel.update(context, g);
        context.getPainter().paintInternalFrameTitlePaneBackground(context,
                          g, 0, 0, getWidth(), getHeight());
        paint(context, g);
        context.dispose();
    }

    public void paint(Graphics g, JComponent c) {
        SynthContext context = getContext(c);

        paint(context, g);
        context.dispose();
    }

    protected void paint(SynthContext context, Graphics g) {
    }

    public void paintBorder(SynthContext context, Graphics g, int x,
                            int y, int w, int h) {
        context.getPainter().paintInternalFrameTitlePaneBorder(context,
                                                            g, x, y, w, h);
    }

    protected LayoutManager createLayout() {
        SynthContext context = getContext(this);
	LayoutManager lm =
	    (LayoutManager)style.get(context, "InternalFrameTitlePane.titlePaneLayout");
	context.dispose();
        return (lm != null) ? lm : new SynthTitlePaneLayout();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (SynthLookAndFeel.shouldUpdateStyle(evt)) {
            updateStyle(SynthInternalFrameTitlePane.this);
        }
    }

    class SynthTitlePaneLayout implements LayoutManager {
        public void addLayoutComponent(String name, Component c) {}
        public void removeLayoutComponent(Component c) {}    
        public Dimension preferredLayoutSize(Container c)  {
	    return minimumLayoutSize(c);
	}
    
        public Dimension minimumLayoutSize(Container c) {
            // Calculate width.
            int width = 22;
 
            if (frame.isClosable()) {
                width += 19;
            }
            if (frame.isMaximizable()) {
                width += 19;
            }
            if (frame.isIconifiable()) {
                width += 19;
            }

            FontMetrics fm = frame.getFontMetrics(getFont());
            SynthContext context = getContext(frame);
            SynthGraphicsUtils graphicsUtils = context.getStyle().
                                       getGraphicsUtils(context);
            String frameTitle = frame.getTitle();
            int title_w = frameTitle != null ? graphicsUtils.
                               computeStringWidth(context, fm.getFont(),
                               fm, frameTitle) : 0;
            int title_length = frameTitle != null ? frameTitle.length() : 0;

            // Leave room for three characters in the title.
            if (title_length > 3) {
                int subtitle_w = graphicsUtils.computeStringWidth(context,
                    fm.getFont(), fm, frameTitle.substring(0, 3) + "...");
                width += (title_w < subtitle_w) ? title_w : subtitle_w;
            } else {
                width += title_w;
            }

            // Calculate height.
            Icon icon = frame.getFrameIcon();
            int fontHeight = fm.getHeight();
            fontHeight += 2;
            int iconHeight = 0;
            if (icon != null) {
                // SystemMenuBar forces the icon to be 16x16 or less.
                iconHeight = Math.min(icon.getIconHeight(), 16);
            }
            iconHeight += 2;
      
            int height = Math.max( fontHeight, iconHeight );

            Dimension dim = new Dimension(width, height);

            // Take into account the border insets if any.
            if (getBorder() != null) {
                Insets insets = getBorder().getBorderInsets(c);
                dim.height += insets.top + insets.bottom;
                dim.width += insets.left + insets.right;
            }
            context.dispose();
            return dim;
	}
    
        public void layoutContainer(Container c) {
            boolean leftToRight = SynthLookAndFeel.isLeftToRight(frame);

            int w = getWidth();
            int h = getHeight();
            int x;

            Icon closeIcon = closeButton.getIcon();
            int buttonHeight = (closeIcon != null) ? closeIcon.getIconHeight(): 12;
	    if (buttonHeight == 0) {
		buttonHeight = 12;
	    }
            //int buttonWidth = closeButton.getIcon().getIconWidth();

            Icon icon = frame.getFrameIcon();
            int iconHeight = (icon != null) ? icon.getIconHeight() : buttonHeight;

	    Insets insets = frame.getInsets();

            x = (leftToRight) ? insets.left : w - 16 - insets.right;
            menuButton.setBounds(x, (h - iconHeight) / 2, 16, 14);

            x = (leftToRight) ? w - 16 - insets.right : insets.left;

            if (frame.isClosable()) {
                closeButton.setBounds(x, (h - buttonHeight) / 2, 16, 14);
                x += (leftToRight) ? -(16 + 2) : 16 + 2;
            } 

            if (frame.isMaximizable()) {
                maxButton.setBounds(x, (h - buttonHeight) / 2, 16, 14);
                x += (leftToRight) ? -(16 + 2) : 16 + 2;
            }

            if (frame.isIconifiable()) {
                iconButton.setBounds(x, (h - buttonHeight) / 2, 16, 14);
            } 
        }
    }

    private JButton createNoFocusButton() {
        JButton button = new JButton();
        button.setFocusable(false);
        button.setMargin(new Insets(0,0,0,0));
        return button;
    }
}
