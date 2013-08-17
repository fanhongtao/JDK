/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.metal;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.text.JTextComponent;

import java.awt.Component;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;


/**
 * Factory object that can vend Borders appropriate for the metal L & F.
 * @author Steve Wilson
 * @version 1.19 02/06/02
 */

public class MetalBorders {


    public static class Flush3DBorder extends AbstractBorder implements UIResource{

        private static final Insets insets = new Insets(2, 2, 2, 2);

        public void paintBorder(Component c, Graphics g, int x, int y,
			  int w, int h) {
            if (c.isEnabled()) {
                MetalUtils.drawFlush3DBorder(g, x, y, w, h);
            } else {
                MetalUtils.drawDisabledBorder(g, x, y, w, h);
            }
        }
        public Insets getBorderInsets(Component c)       {
            return insets;
        }
    }

    public static class ButtonBorder extends AbstractBorder implements UIResource {

        protected static Insets borderInsets = new Insets( 3, 3, 3, 3 );

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            JButton button = (JButton)c;
	    ButtonModel model = button.getModel();

	    if ( model.isEnabled() ) {
	        if ( model.isPressed() && model.isArmed() ) {
	            MetalUtils.drawPressed3DBorder( g, x, y, w, h );
	        }
	        else {
	            if (button.isDefaultButton()) {
		      MetalUtils.drawDefaultButtonBorder( g, x, y, w, h, button.hasFocus() && false );
		    } else {
		        MetalUtils.drawButtonBorder( g, x, y, w, h, button.hasFocus() && false);
		    }
	        }
	    } else { // disabled state
	        MetalUtils.drawDisabledBorder( g, x, y, w-1, h-1 );
	    }
        }

        public Insets getBorderInsets( Component c ) {
            return borderInsets;
        }
    }

    public static class InternalFrameBorder extends AbstractBorder implements UIResource {

        private static final Insets insets = new Insets(5, 5, 5, 5);

        private static final int corner = 14;

        public void paintBorder(Component c, Graphics g, int x, int y,
			  int w, int h) {

            Color background;
            Color highlight;
            Color shadow;

            if (c instanceof JInternalFrame && ((JInternalFrame)c).isSelected()) {
                background = MetalLookAndFeel.getPrimaryControlDarkShadow();
	        highlight = MetalLookAndFeel.getPrimaryControlShadow();
	        shadow = MetalLookAndFeel.getPrimaryControlInfo();
            } else {
                background = MetalLookAndFeel.getControlDarkShadow();
	        highlight = MetalLookAndFeel.getControlShadow();
	        shadow = MetalLookAndFeel.getControlInfo();
            }

              g.setColor(background);
              // Draw outermost lines
              g.drawLine( 1, 0, w-2, 0);
              g.drawLine( 0, 1, 0, h-2);
              g.drawLine( w-1, 1, w-1, h-2);
              g.drawLine( 1, h-1, w-2, h-1);

              // Draw the bulk of the border
              for (int i = 1; i < 5; i++) {
	          g.drawRect(x+i,y+i,w-(i*2)-1, h-(i*2)-1);
              }

              g.setColor(highlight);
              // Draw the Long highlight lines
              g.drawLine( corner+1, 3, w-corner, 3);
              g.drawLine( 3, corner+1, 3, h-corner);
	      g.drawLine( w-2, corner+1, w-2, h-corner);
	      g.drawLine( corner+1, h-2, w-corner, h-2);

              g.setColor(shadow);
              // Draw the Long shadow lines
              g.drawLine( corner, 2, w-corner-1, 2);
              g.drawLine( 2, corner, 2, h-corner-1);
	      g.drawLine( w-3, corner, w-3, h-corner-1);
	      g.drawLine( corner, h-3, w-corner-1, h-3);

          }

          public Insets getBorderInsets(Component c)       {
              return insets;
          }
    }

    /**
     * Border for a Palatte.
     * @since 1.3
     */
    public static class PaletteBorder extends AbstractBorder implements UIResource {
        private static final Insets insets = new Insets(1, 1, 1, 1);
        int titleHeight = 0;

        public void paintBorder( Component c, Graphics g, int x, int y, int w, int h ) {  

	    g.translate(x,y);  
	    g.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
	    g.drawLine(0, 1, 0, h-2);
	    g.drawLine(1, h-1, w-2, h-1);
	    g.drawLine(w-1,  1, w-1, h-2);
	    g.drawLine( 1, 0, w-2, 0);
	    g.drawRect(1,1, w-3, h-3);
	    

/*
	    titleHeight = UIManager.getInt("JInternalFrame.paletteTitleHeight");
	    g.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());
	    g.drawLine(0, titleHeight, 0, h-2);
	    g.drawLine(1, h-1, w-2, h-1);
	    g.drawLine(w-1,  titleHeight, w-1, h-2);

	    g.setColor(MetalLookAndFeel.getPrimaryControl());
	    g.drawLine(1,0, w-2, 0);
	    g.drawLine(0, 1, 0, titleHeight-1);
	    g.drawLine(w-1, 1, w-1, titleHeight-1);
*/

	    g.translate(-x,-y);
      
	}

        public Insets getBorderInsets(Component c)       {
            return insets;
        }
    }

    public static class OptionDialogBorder extends AbstractBorder implements UIResource {
        private static final Insets insets = new Insets(3, 3, 3, 3);
        int titleHeight = 0;

        public void paintBorder( Component c, Graphics g, int x, int y, int w, int h ) {  

	    g.translate(x,y);  

	    g.setColor(MetalLookAndFeel.getPrimaryControlDarkShadow());

              // Draw outermost lines
              g.drawLine( 1, 0, w-2, 0);
              g.drawLine( 0, 1, 0, h-2);
              g.drawLine( w-1, 1, w-1, h-2);
              g.drawLine( 1, h-1, w-2, h-1);

              // Draw the bulk of the border
              for (int i = 1; i < 3; i++) {
	          g.drawRect(i, i, w-(i*2)-1, h-(i*2)-1);
              }

	    g.translate(-x,-y);
      
	}

        public Insets getBorderInsets(Component c)       {
            return insets;
        }
    }


    public static class MenuBarBorder extends AbstractBorder implements UIResource {
        protected static Insets borderInsets = new Insets( 1, 0, 1, 0 );

        public void paintBorder( Component c, Graphics g, int x, int y, int w, int h ) {
	    g.translate( x, y );

            g.setColor( MetalLookAndFeel.getControlShadow() );
	    g.drawLine( 0, h-1, w, h-1 );

	    g.translate( -x, -y );

        }

        public Insets getBorderInsets( Component c ) {
            return borderInsets;
        }
    }

    public static class MenuItemBorder extends AbstractBorder implements UIResource {
        protected static Insets borderInsets = new Insets( 2, 2, 2, 2 );

        public void paintBorder( Component c, Graphics g, int x, int y, int w, int h ) {
            JMenuItem b = (JMenuItem) c;
	    ButtonModel model = b.getModel();

	    g.translate( x, y );

	    if ( c.getParent() instanceof JMenuBar ) {
	        if ( model.isArmed() || model.isSelected() ) {
	            g.setColor( MetalLookAndFeel.getControlDarkShadow() );
	            g.drawLine( 0, 0, w - 2, 0 );
	            g.drawLine( 0, 0, 0, h - 1 );
	            g.drawLine( w - 2, 2, w - 2, h - 1 );

	            g.setColor( MetalLookAndFeel.getPrimaryControlHighlight() );
	            g.drawLine( w - 1, 1, w - 1, h - 1 );

	            g.setColor( MetalLookAndFeel.getMenuBackground() );
	            g.drawLine( w - 1, 0, w - 1, 0 );
	        }
	    } else {
	        if (  model.isArmed() || ( c instanceof JMenu && model.isSelected() ) ) {
	            g.setColor( MetalLookAndFeel.getPrimaryControlDarkShadow() );
	            g.drawLine( 0, 0, w - 1, 0 );

	            g.setColor( MetalLookAndFeel.getPrimaryControlHighlight() );
	            g.drawLine( 0, h - 1, w - 1, h - 1 );
	        } else {
	            g.setColor( MetalLookAndFeel.getPrimaryControlHighlight() );
	            g.drawLine( 0, 0, 0, h - 1 );
	        }
	    }

	    g.translate( -x, -y );
        }

        public Insets getBorderInsets( Component c ) {
            return borderInsets;
        }
    }

    public static class PopupMenuBorder extends AbstractBorder implements UIResource {
        protected static Insets borderInsets = new Insets( 3, 1, 2, 1 );

        public void paintBorder( Component c, Graphics g, int x, int y, int w, int h ) {
	    g.translate( x, y );

            g.setColor( MetalLookAndFeel.getPrimaryControlDarkShadow() );
	    g.drawRect( 0, 0, w - 1, h - 1 );

            g.setColor( MetalLookAndFeel.getPrimaryControlHighlight() );
	    g.drawLine( 1, 1, w - 2, 1 );
	    g.drawLine( 1, 2, 1, 2 );
	    g.drawLine( 1, h - 2, 1, h - 2 );

	    g.translate( -x, -y );

        }

        public Insets getBorderInsets( Component c ) {
             return borderInsets;
        }
    }


    public static class RolloverButtonBorder extends ButtonBorder {

        public void paintBorder( Component c, Graphics g, int x, int y, int w, int h ) {
            AbstractButton b = (AbstractButton) c;
            ButtonModel model = b.getModel();

            if ( model.isRollover() && !( model.isPressed() && !model.isArmed() ) ) {
	        super.paintBorder( c, g, x, y, w, h );
            }
        }

    }


    public static class ToolBarBorder extends AbstractBorder implements UIResource, SwingConstants
    {
        protected MetalBumps bumps = new MetalBumps( 10, 10,
				      MetalLookAndFeel.getControlHighlight(),
				      MetalLookAndFeel.getControlDarkShadow(),
				      MetalLookAndFeel.getMenuBackground() );

        public void paintBorder( Component c, Graphics g, int x, int y, int w, int h )
	{
	    g.translate( x, y );

	    if ( ((JToolBar) c).isFloatable() )
	    {
	        if ( ((JToolBar) c).getOrientation() == HORIZONTAL )
		{
		    bumps.setBumpArea( 10, c.getSize().height - 4 );
                    if( MetalUtils.isLeftToRight(c) ) {
                        bumps.paintIcon( c, g, 2, 2 );
                    } else {
                        bumps.paintIcon( c, g, c.getBounds().width-12, 2 );
                    }
	        }
		else // vertical
		{
		    bumps.setBumpArea( c.getSize().width - 4, 10 );
		    bumps.paintIcon( c, g, 2, 2 );
	        }

	    }


	    g.translate( -x, -y );
        }

        public Insets getBorderInsets( Component c )
	{
            Insets borderInsets = new Insets( 2, 2, 2, 2 );

	    if ( ((JToolBar) c).isFloatable() )
	    {
	        if ( ((JToolBar) c).getOrientation() == HORIZONTAL )
		{

	            borderInsets.left = 16;
	        }
		else // vertical
		{
	            borderInsets.top = 16;
	        }
	    }

	    Insets margin = ((JToolBar) c).getMargin();

	    if ( margin != null )
	    {
	        borderInsets.left   += margin.left;
	        borderInsets.top    += margin.top;
	        borderInsets.right  += margin.right;
	        borderInsets.bottom += margin.bottom;
	    }

            return borderInsets;
        }
    }

    private static Border buttonBorder;

    /**
     * Returns a border instance for a JButton
     * @since 1.3
     */
    public static Border getButtonBorder() {
	if (buttonBorder == null) {
	    buttonBorder = new BorderUIResource.CompoundBorderUIResource(
						   new MetalBorders.ButtonBorder(),
						   new BasicBorders.MarginBorder());
	}
	return buttonBorder;
    }

    private static Border textBorder;

    /**
     * Returns a border instance for a text component
     * @since 1.3
     */
    public static Border getTextBorder() {
	if (textBorder == null) {
	    textBorder = new BorderUIResource.CompoundBorderUIResource(
						   new MetalBorders.Flush3DBorder(),
						   new BasicBorders.MarginBorder());
	}
	return textBorder;
    }

    private static Border textFieldBorder;

    /**
     * Returns a border instance for a JTextField
     * @since 1.3
     */
    public static Border getTextFieldBorder() {
	if (textFieldBorder == null) {
	    textFieldBorder = new BorderUIResource.CompoundBorderUIResource(
						   new MetalBorders.TextFieldBorder(),
						   new BasicBorders.MarginBorder());
	}
	return textFieldBorder;
    }

    public static class TextFieldBorder extends Flush3DBorder {

        public void paintBorder(Component c, Graphics g, int x, int y,
				int w, int h) {

	  if (!(c instanceof JTextComponent)) {
	        // special case for non-text components (bug ID 4144840)
	        if (c.isEnabled()) {
		    MetalUtils.drawFlush3DBorder(g, x, y, w, h);
		} else {
		    MetalUtils.drawDisabledBorder(g, x, y, w, h);
		}
		return;
	    }

	    if (c.isEnabled() && ((JTextComponent)c).isEditable()) {
	        MetalUtils.drawFlush3DBorder(g, x, y, w, h);
	    } else {
	        MetalUtils.drawDisabledBorder(g, x, y, w, h);
	    }

	}
    }

    public static class ScrollPaneBorder extends AbstractBorder implements UIResource {

       private static final Insets insets = new Insets(1, 1, 2, 2);

        public void paintBorder(Component c, Graphics g, int x, int y,
			  int w, int h) {

            JScrollPane scroll = (JScrollPane)c;
            JComponent colHeader = scroll.getColumnHeader();
            int colHeaderHeight = 0;
            if (colHeader != null)
               colHeaderHeight = colHeader.getHeight();

            JComponent rowHeader = scroll.getRowHeader();
            int rowHeaderWidth = 0;
            if (rowHeader != null)
               rowHeaderWidth = rowHeader.getWidth();


            g.translate( x, y);

            g.setColor( MetalLookAndFeel.getControlDarkShadow() );
            g.drawRect( 0, 0, w-2, h-2 );
            g.setColor( MetalLookAndFeel.getControlHighlight() );

            g.drawLine( w-1, 1, w-1, h-1);
            g.drawLine( 1, h-1, w-1, h-1);

            g.setColor( MetalLookAndFeel.getControl() );
            g.drawLine( w-2, 2+colHeaderHeight, w-2, 2+colHeaderHeight );
            g.drawLine( 1+rowHeaderWidth, h-2, 1+rowHeaderWidth, h-2 );

            g.translate( -x, -y);

        }

        public Insets getBorderInsets(Component c)       {
            return insets;
        }
    }

    private static Border toggleButtonBorder;

    /**
     * Returns a border instance for a JToggleButton
     * @since 1.3
     */
    public static Border getToggleButtonBorder() {
	if (toggleButtonBorder == null) {
	    toggleButtonBorder = new BorderUIResource.CompoundBorderUIResource(
						   new MetalBorders.ToggleButtonBorder(),
						   new BasicBorders.MarginBorder());
	}
	return toggleButtonBorder;
    }
    public static class ToggleButtonBorder extends ButtonBorder {
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
	    JToggleButton button = (JToggleButton)c;
	    ButtonModel model = button.getModel();

	    if (! c.isEnabled() ) {
	        MetalUtils.drawDisabledBorder( g, x, y, w-1, h-1 );
	    } else {
	        if ( model.isPressed() && model.isArmed() ) {
		    MetalUtils.drawPressed3DBorder( g, x, y, w, h );
		} else if ( model.isSelected() ) {
		    MetalUtils.drawDark3DBorder( g, x, y, w, h );
		} else {
		    MetalUtils.drawFlush3DBorder( g, x, y, w, h );
		}
	    }
	}
    }

    /**
     * Border for a Table Header
     * @since 1.3
     */
    public static class TableHeaderBorder extends javax.swing.border.AbstractBorder {
        protected Insets editorBorderInsets = new Insets( 2, 2, 2, 0 );

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
	    g.translate( x, y );
	    
	    g.setColor( MetalLookAndFeel.getControlDarkShadow() );
	    g.drawLine( w-1, 0, w-1, h-1 );
	    g.drawLine( 1, h-1, w-1, h-1 );
	    g.setColor( MetalLookAndFeel.getControlHighlight() );
	    g.drawLine( 0, 0, w-2, 0 );
	    g.drawLine( 0, 0, 0, h-2 );

	    g.translate( -x, -y );
	}

        public Insets getBorderInsets( Component c ) {
	    return editorBorderInsets;
	}
    }

    /**
     * Returns a border instance for a Desktop Icon
     * @since 1.3
     */
    public static Border getDesktopIconBorder() {
	return new BorderUIResource.CompoundBorderUIResource(
                                          new LineBorder(MetalLookAndFeel.getControlDarkShadow(), 1),
                                          new MatteBorder (2,2,1,2, MetalLookAndFeel.getControl()));
    }
     
}

