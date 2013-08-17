/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.basic;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.text.JTextComponent;

import java.awt.Component;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;


/**
 * Factory object that can vend Borders appropriate for the basic L & F.
 * @version 1.21 02/06/02
 * @author Georges Saab
 * @author Amy Fowler
 */

public class BasicBorders {

    public static Border getButtonBorder() {
	UIDefaults table = UIManager.getLookAndFeelDefaults();
	Border buttonBorder = new BorderUIResource.CompoundBorderUIResource(
			   new BasicBorders.ButtonBorder(
					   table.getColor("controlShadow"),
                                           table.getColor("controlDkShadow"),
                                           table.getColor("controlHighlight"),
                                           table.getColor("controlLtHighlight")),
	       			     new MarginBorder());
	return buttonBorder;
    }

    public static Border getRadioButtonBorder() {
	UIDefaults table = UIManager.getLookAndFeelDefaults();
	Border radioButtonBorder = new BorderUIResource.CompoundBorderUIResource(
			   new BasicBorders.RadioButtonBorder(
					   table.getColor("controlShadow"),
                                           table.getColor("controlDkShadow"),
                                           table.getColor("controlHighlight"),
                                           table.getColor("controlLtHighlight")),
	       			     new MarginBorder());
	return radioButtonBorder;
    }

    public static Border getToggleButtonBorder() {
	UIDefaults table = UIManager.getLookAndFeelDefaults();
	Border toggleButtonBorder = new BorderUIResource.CompoundBorderUIResource(
			             new BasicBorders.ToggleButtonBorder(
                                           table.getColor("controlShadow"),
                                           table.getColor("controlDkShadow"),
                                           table.getColor("controlHighlight"),
                                           table.getColor("controlLtHighlight")),
				     new MarginBorder());
	return toggleButtonBorder;
    }

    public static Border getMenuBarBorder() {
	UIDefaults table = UIManager.getLookAndFeelDefaults();
	Border menuBarBorder = new BasicBorders.MenuBarBorder(
				        table.getColor("controlShadow"),
                                        table.getColor("controlLtHighlight")
                                   );
	return menuBarBorder;
    }

    public static Border getSplitPaneBorder() {
	UIDefaults table = UIManager.getLookAndFeelDefaults();
	Border splitPaneBorder = new BasicBorders.SplitPaneBorder(
				     table.getColor("controlLtHighlight"),
				     table.getColor("controlDkShadow"));
	return splitPaneBorder;
    }

    /**
     * Returns a border instance for a JSplitPane divider
     * @since 1.3
     */
    public static Border getSplitPaneDividerBorder() {
	UIDefaults table = UIManager.getLookAndFeelDefaults();
	Border splitPaneBorder = new BasicBorders.SplitPaneDividerBorder(
				     table.getColor("controlLtHighlight"),
				     table.getColor("controlDkShadow"));
	return splitPaneBorder;
    }

    public static Border getTextFieldBorder() {
	UIDefaults table = UIManager.getLookAndFeelDefaults();
	Border textFieldBorder = new BasicBorders.FieldBorder(
                                           table.getColor("controlShadow"),
                                           table.getColor("controlDkShadow"),
                                           table.getColor("controlHighlight"),
                                           table.getColor("controlLtHighlight"));
	return textFieldBorder;
    }

    public static Border getProgressBarBorder() {
	UIDefaults table = UIManager.getLookAndFeelDefaults();
	Border progressBarBorder = new BorderUIResource.LineBorderUIResource(Color.green, 2);
	return progressBarBorder;
    }

    public static Border getInternalFrameBorder() {
	UIDefaults table = UIManager.getLookAndFeelDefaults();
	Border internalFrameBorder = new BorderUIResource.CompoundBorderUIResource(
				new BevelBorder(BevelBorder.RAISED,
					table.getColor("controlHighlight"),
                                        table.getColor("controlLtHighlight"),
                                        table.getColor("controlDkShadow"),
                                        table.getColor("controlShadow")),
				BorderFactory.createLineBorder(
					table.getColor("control"), 1));

	return internalFrameBorder;
    }
 
   public static class ButtonBorder extends AbstractBorder implements UIResource {
        protected Color shadow;
        protected Color darkShadow;
        protected Color highlight;
        protected Color lightHighlight;

        public ButtonBorder(Color shadow, Color darkShadow, 
                            Color highlight, Color lightHighlight) {
            this.shadow = shadow;
            this.darkShadow = darkShadow;
            this.highlight = highlight; 
            this.lightHighlight = lightHighlight;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, 
                            int width, int height) {
            boolean isPressed = false;
            boolean isDefault = false;
      
            if (c instanceof AbstractButton) {
	        AbstractButton b = (AbstractButton)c;
	        ButtonModel model = b.getModel();
	
   	        isPressed = model.isPressed() && model.isArmed();

                if (c instanceof JButton) {
                    isDefault = ((JButton)c).isDefaultButton();
                }
            }	
            BasicGraphicsUtils.drawBezel(g, x, y, width, height, 
				   isPressed, isDefault, shadow,
                                   darkShadow, highlight, lightHighlight);
        }

        public Insets getBorderInsets(Component c)       {
            // leave room for default visual
            return new Insets(3,3,3,3);
        }
    }

    public static class ToggleButtonBorder extends ButtonBorder {

        public ToggleButtonBorder(Color shadow, Color darkShadow, 
                                  Color highlight, Color lightHighlight) {
            super(shadow, darkShadow, highlight, lightHighlight);
        }

        public void paintBorder(Component c, Graphics g, int x, int y, 
                                int width, int height) {
                BasicGraphicsUtils.drawBezel(g, x, y, width, height, 
                                             false, false,
                                             shadow, darkShadow, 
                                             highlight, lightHighlight);
	}

        public Insets getBorderInsets(Component c)       {
            return new Insets(2, 2, 2, 2);
        }
    }

    public static class RadioButtonBorder extends ButtonBorder {

        public RadioButtonBorder(Color shadow, Color darkShadow, 
                                 Color highlight, Color lightHighlight) {
            super(shadow, darkShadow, highlight, lightHighlight);
        }

      
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
	  
	    if (c instanceof AbstractButton) {
	        AbstractButton b = (AbstractButton)c;
	        ButtonModel model = b.getModel();
	      
	        if (model.isArmed() && model.isPressed() || model.isSelected()) {
		    BasicGraphicsUtils.drawLoweredBezel(g, x, y, width, height,
                                                        shadow, darkShadow, 
                                                        highlight, lightHighlight);
	        } else {
		    BasicGraphicsUtils.drawBezel(g, x, y, width, height, 
					       false, b.isFocusPainted() && b.hasFocus(),
                                                 shadow, darkShadow, 
                                                 highlight, lightHighlight);
	        }
	    } else {	
	        BasicGraphicsUtils.drawBezel(g, x, y, width, height, false, false,
                                             shadow, darkShadow, highlight, lightHighlight);
	    }
        }
      
        public Insets getBorderInsets(Component c)       {
	    return new Insets(2, 2, 2, 2);
        }
    }

    public static class MenuBarBorder extends AbstractBorder implements UIResource {
        private Color shadow;
        private Color highlight;

        public MenuBarBorder(Color shadow, Color highlight) {
            this.shadow = shadow;
            this.highlight = highlight;
        }

	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
	    BasicGraphicsUtils.drawGroove(g, 0, height-2, 
					  width, height,
                                          shadow, highlight);
	}
	
	public Insets getBorderInsets(Component c)       {
	    return new Insets(0, 0, 2, 0);
	}
    }

    public static class MarginBorder extends AbstractBorder implements UIResource {

        public Insets getBorderInsets(Component c)       {
            Insets margin = null;
            //
            // Ideally we'd have an interface defined for classes which
            // support margins (to avoid this hackery), but we've
            // decided against it for simplicity
            //
           if (c instanceof AbstractButton) {
               AbstractButton b = (AbstractButton)c;
               margin = b.getMargin();
           } else if (c instanceof JToolBar) {
               JToolBar t = (JToolBar)c;
               margin = t.getMargin();
           } else if (c instanceof JTextComponent) {
               JTextComponent t = (JTextComponent)c;
               margin = t.getMargin();
           }
           
           return (margin != null? margin : new Insets(0, 0, 0, 0));
        }
    }

    public static class FieldBorder extends AbstractBorder implements UIResource {
        protected Color shadow;
        protected Color darkShadow;
        protected Color highlight;
        protected Color lightHighlight;

        public FieldBorder(Color shadow, Color darkShadow, 
                           Color highlight, Color lightHighlight) {
            this.shadow = shadow;
            this.highlight = highlight;
            this.darkShadow = darkShadow;
            this.lightHighlight = lightHighlight;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, 
                            int width, int height) {
            BasicGraphicsUtils.drawEtchedRect(g, x, y, width, height,
                                              shadow, darkShadow, 
                                              highlight, lightHighlight);
        }

        public Insets getBorderInsets(Component c)       {
            Insets margin = null;
            if (c instanceof JTextComponent) {
                margin = ((JTextComponent)c).getMargin();
            }
            if (margin != null) {
                return new Insets(2+margin.top, 2+margin.left, 
                                  2+margin.bottom, 2+margin.right);
                
            }
            return new Insets(2, 2, 2, 2);
        }
    }


    /**
     * Draws the border around the divider in a splitpane
     * (when BasicSplitPaneUI is used). To get the appropriate effect, this
     * needs to be used with a SplitPaneBorder.
     */
    static class SplitPaneDividerBorder implements Border, UIResource {
        Color highlight;
        Color shadow;

        SplitPaneDividerBorder(Color highlight, Color shadow) {
	    this.highlight = highlight;
	    this.shadow = shadow;
	}

	public void paintBorder(Component c, Graphics g, int x, int y,
				int width, int height) {
	    Component          child;
	    Rectangle          cBounds;
	    JSplitPane         splitPane = ((BasicSplitPaneDivider)c).
		                         getBasicSplitPaneUI().getSplitPane();
	    Dimension          size = c.getSize();
	    
	    child = splitPane.getLeftComponent();
	    // This is needed for the space between the divider and end of
	    // splitpane.
	    g.setColor(c.getBackground());
	    g.drawRect(x, y, width - 1, height - 1);
	    if(splitPane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
		if(child != null) {
		    g.setColor(highlight);
		    g.drawLine(0, 0, 0, size.height);
		}
		child = splitPane.getRightComponent();
		if(child != null) {
		    g.setColor(shadow);
		    g.drawLine(size.width - 1, 0, size.width - 1, size.height);
		}
	    } else {
		if(child != null) {
		    g.setColor(highlight);
		    g.drawLine(0, 0, size.width, 0);
		}
		child = splitPane.getRightComponent();
		if(child != null) {
		    g.setColor(shadow);
		    g.drawLine(0, size.height - 1, size.width,
			       size.height - 1);
		}
	    }
	}
	public Insets getBorderInsets(Component c) {
	    if (c instanceof BasicSplitPaneDivider) {
		BasicSplitPaneUI bspui = ((BasicSplitPaneDivider)c).
		                         getBasicSplitPaneUI();

		if (bspui != null) {
		    JSplitPane splitPane = bspui.getSplitPane();

		    if (splitPane != null) {
			if (splitPane.getOrientation() ==
			    JSplitPane.HORIZONTAL_SPLIT) {
			    return new Insets(0, 1, 0, 1);
			}
			// VERTICAL_SPLIT
			return new Insets(1, 0, 1, 0);
		    }
		}
	    }
	    return new Insets(1, 1, 1, 1);
	}
	public boolean isBorderOpaque() { return true; }
    }


    /**
     * Draws the border around the splitpane. To work correctly you shoudl
     * also install a border on the divider (property SplitPaneDivider.border).
     */
    public static class SplitPaneBorder implements Border, UIResource {
        protected Color highlight;
        protected Color shadow;

        public SplitPaneBorder(Color highlight, Color shadow) {
	    this.highlight = highlight;
	    this.shadow = shadow;
	}

	public void paintBorder(Component c, Graphics g, int x, int y,
				int width, int height) {
	    // The only tricky part with this border is that the divider is
	    // not positioned at the top (for horizontal) or left (for vert),
	    // so this border draws to where the divider is:
	    // -----------------
	    // |xxxxxxx xxxxxxx|
	    // |x     ---     x|
	    // |x     |	|     x|
	    // |x     |D|     x|
	    // |x     | |     x|
	    // |x     ---     x|
	    // |xxxxxxx xxxxxxx|
	    // -----------------
	    // The above shows (rather excessively) what this looks like for
	    // a horizontal orientation. This border then draws the x's, with
	    // the SplitPaneDividerBorder drawing its own border.

	    Component          child;
	    Rectangle          cBounds;

	    JSplitPane splitPane = (JSplitPane)c;
	    
	    child = splitPane.getLeftComponent();
	    // This is needed for the space between the divider and end of
	    // splitpane.
	    g.setColor(c.getBackground());
	    g.drawRect(x, y, width - 1, height - 1);
	    if(splitPane.getOrientation() == JSplitPane.HORIZONTAL_SPLIT) {
		if(child != null) {
		    cBounds = child.getBounds();
		    g.setColor(shadow);
		    g.drawLine(0, 0, cBounds.width + 1, 0);
		    g.drawLine(0, 1, 0, cBounds.height + 2);

		    g.setColor(highlight);
		    g.drawLine(1, cBounds.height + 1, cBounds.width + 1,
			       cBounds.height + 1);
		}
		child = splitPane.getRightComponent();
		if(child != null) {
		    cBounds = child.getBounds();

		    int             maxX = cBounds.x + cBounds.width;
		    int             maxY = cBounds.y + cBounds.height;
		    
		    g.setColor(shadow);
		    g.drawLine(cBounds.x - 1, 0, maxX, 0);
		    g.drawLine(cBounds.x - 1, maxY, cBounds.x, maxY);
		    g.setColor(highlight);
		    g.drawLine(cBounds.x, maxY, maxX, maxY);
		    g.drawLine(maxX, 0, maxX, maxY + 1);
		}
	    } else {
		if(child != null) {
		    cBounds = child.getBounds();
		    g.setColor(shadow);
		    g.drawLine(0, 0, cBounds.width + 1, 0);
		    g.drawLine(0, 1, 0, cBounds.height);
		    g.setColor(highlight);
		    g.drawLine(1 + cBounds.width, 0, 1 + cBounds.width,
			       cBounds.height + 1);
		    g.drawLine(0, cBounds.height + 1, 0, cBounds.height + 1);
		}
		child = splitPane.getRightComponent();
		if(child != null) {
		    cBounds = child.getBounds();

		    int             maxX = cBounds.x + cBounds.width;
		    int             maxY = cBounds.y + cBounds.height;
		    
		    g.setColor(shadow);
		    g.drawLine(0, cBounds.y - 1, 0, maxY);
		    g.drawLine(maxX, cBounds.y - 1, maxX, cBounds.y - 1);
		    g.drawLine(0, cBounds.y - 1, cBounds.width, cBounds.y - 1);
		    g.setColor(highlight);
		    g.drawLine(0, maxY, cBounds.width + 1, maxY);
		    g.drawLine(maxX, cBounds.y, maxX, maxY);
		}
	    }
	}
	public Insets getBorderInsets(Component c) {
	    return new Insets(1, 1, 1, 1);
	}
	public boolean isBorderOpaque() { return true; }
    }
    
}
