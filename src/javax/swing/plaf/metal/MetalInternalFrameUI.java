/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.metal;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
import java.util.EventListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyVetoException;
import javax.swing.plaf.*;

/**
 * Metal implementation of JInternalFrame.  
 * <p>
 *
 * @version 1.21 02/06/02
 * @author Steve Wilson
 */
public class MetalInternalFrameUI extends BasicInternalFrameUI {

  private MetalInternalFrameTitlePane titlePane;

  private PropertyChangeListener paletteListener;
  private PropertyChangeListener contentPaneListener;

  private static final Border handyEmptyBorder = new EmptyBorder(0,0,0,0);
  
  protected static String IS_PALETTE   = "JInternalFrame.isPalette";

  private static String FRAME_TYPE     = "JInternalFrame.frameType";
  private static String NORMAL_FRAME   = "normal";
  private static String PALETTE_FRAME  = "palette";
  private static String OPTION_DIALOG  = "optionDialog";

  public MetalInternalFrameUI(JInternalFrame b)   {
    super(b);
  }

  public static ComponentUI createUI(JComponent c)    {
      return new MetalInternalFrameUI( (JInternalFrame) c);
  }

  public void installUI(JComponent c) { 
    frame = (JInternalFrame)c;

    paletteListener = new PaletteListener();
    contentPaneListener = new ContentPaneListener();
    c.addPropertyChangeListener(paletteListener);
    c.addPropertyChangeListener(contentPaneListener);

    super.installUI(c);

    Object paletteProp = c.getClientProperty( IS_PALETTE );
    if ( paletteProp != null ) {
	setPalette( ((Boolean)paletteProp).booleanValue() );
    }

    Container content = frame.getContentPane();
    stripContentBorder(content);    
    //c.setOpaque(false);
  }

  
  public void uninstallUI(JComponent c) {                  
      frame = (JInternalFrame)c;

      c.removePropertyChangeListener(paletteListener);
      c.removePropertyChangeListener(contentPaneListener);

      Container cont = ((JInternalFrame)(c)).getContentPane();
      if (cont instanceof JComponent) {
	JComponent content = (JComponent)cont;
	if ( content.getBorder() == handyEmptyBorder) {
	  content.setBorder(null);
	}
      }
      super.uninstallUI(c);
  } 

  protected void installKeyboardActions(){
  }

  protected void uninstallKeyboardActions(){
  }

  private void stripContentBorder(Object c) {
        if ( c instanceof JComponent ) {
            JComponent contentComp = (JComponent)c;
            Border contentBorder = contentComp.getBorder();
   	    if (contentBorder == null || contentBorder instanceof UIResource) {
	        contentComp.setBorder( handyEmptyBorder );
            }
        }
  }

    
  protected JComponent createNorthPane(JInternalFrame w) {
    titlePane = new MetalInternalFrameTitlePane(w);
    return titlePane;
  }


  private void setFrameType( String frameType )
  {
      if ( frameType.equals( OPTION_DIALOG ) )
      {
          LookAndFeel.installBorder(frame, "InternalFrame.optionDialogBorder");
	  titlePane.setPalette( false );
      }
      else if ( frameType.equals( PALETTE_FRAME ) )
      {
          LookAndFeel.installBorder(frame, "InternalFrame.paletteBorder");
	  titlePane.setPalette( true );
      }
      else
      {
          LookAndFeel.installBorder(frame, "InternalFrame.border");
	  titlePane.setPalette( false );
      }
  }

  // this should be deprecated - jcs
  public void setPalette(boolean isPalette) {
    if (isPalette) {
        LookAndFeel.installBorder(frame, "InternalFrame.paletteBorder");
    } else {
        LookAndFeel.installBorder(frame, "InternalFrame.border");
    }
    titlePane.setPalette(isPalette);

  }

  class PaletteListener implements PropertyChangeListener
  {
      public void propertyChange(PropertyChangeEvent e)
      {
	  String name = e.getPropertyName();

	  if ( name.equals( FRAME_TYPE ) )
	  {
	      if ( e.getNewValue() instanceof String )
	      {
		  setFrameType( (String) e.getNewValue() );
	      }
	  }
	  else if ( name.equals( IS_PALETTE ) )
	  {
	      if ( e.getNewValue() != null )
	      {
	          setPalette( ((Boolean)e.getNewValue()).booleanValue() );
	      }
	      else
	      {
		  setPalette( false );
	      }
	  }
      }
  } // end class PaletteListener

  class ContentPaneListener implements PropertyChangeListener {
    public void propertyChange(PropertyChangeEvent e) {
	String name = e.getPropertyName();
	if ( name.equals( JInternalFrame.CONTENT_PANE_PROPERTY ) ) {
	    stripContentBorder(e.getNewValue());
        }
    }
  } // end class ContentPaneListener

}

