/*
 * @(#)MetalInternalFrameUI.java	1.31 09/08/10
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.metal;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import javax.swing.plaf.*;

/**
 * Metal implementation of JInternalFrame.  
 * <p>
 *
 * @version 1.31 08/10/09
 * @author Steve Wilson
 */
public class MetalInternalFrameUI extends BasicInternalFrameUI {

  private MetalInternalFrameTitlePane titlePane;

  private static final PropertyChangeListener metalPropertyChangeListener =
        new MetalPropertyChangeHandler();

  private static final Border handyEmptyBorder = new EmptyBorder(0,0,0,0);
  
  protected static String IS_PALETTE   = "JInternalFrame.isPalette";
  private static String IS_PALETTE_KEY = "JInternalFrame.isPalette";
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
    super.installUI(c);

    Object paletteProp = c.getClientProperty(IS_PALETTE_KEY);
    if ( paletteProp != null ) {
	setPalette( ((Boolean)paletteProp).booleanValue() );
    }

    Container content = frame.getContentPane();
    stripContentBorder(content);    
    //c.setOpaque(false);
  }
  
  public void uninstallUI(JComponent c) {                  
      frame = (JInternalFrame)c;

      Container cont = ((JInternalFrame)(c)).getContentPane();
      if (cont instanceof JComponent) {
	JComponent content = (JComponent)cont;
	if ( content.getBorder() == handyEmptyBorder) {
	  content.setBorder(null);
	}
      }
      super.uninstallUI(c);
  } 

    protected void installListeners() {
        super.installListeners();
        frame.addPropertyChangeListener(metalPropertyChangeListener);
    }

    protected void uninstallListeners() {
        frame.removePropertyChangeListener(metalPropertyChangeListener);
        super.uninstallListeners();
    }

  protected void installKeyboardActions(){
      super.installKeyboardActions();
      ActionMap map = SwingUtilities.getUIActionMap(frame);
      if (map != null) {
	  // BasicInternalFrameUI creates an action with the same name, we override
	  // it as Metal frames do not have system menus.
	  map.remove("showSystemMenu");
      }
  }

  protected void uninstallKeyboardActions(){
      super.uninstallKeyboardActions();
  }

    protected void uninstallComponents() {
        titlePane = null;
        super.uninstallComponents();
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

  private static class MetalPropertyChangeHandler implements
        PropertyChangeListener
  {
      public void propertyChange(PropertyChangeEvent e)
      {
	  String name = e.getPropertyName();
          JInternalFrame jif = (JInternalFrame)e.getSource();

          if (!(jif.getUI() instanceof MetalInternalFrameUI)) {
              return;
          }

          MetalInternalFrameUI ui = (MetalInternalFrameUI)jif.getUI();

	  if ( name.equals( FRAME_TYPE ) )
	  {
	      if ( e.getNewValue() instanceof String )
	      {
		  ui.setFrameType( (String) e.getNewValue() );
	      }
	  }
          else if ( name.equals(IS_PALETTE_KEY) )
	  {
	      if ( e.getNewValue() != null )
	      {
	          ui.setPalette( ((Boolean)e.getNewValue()).booleanValue() );
	      }
	      else
	      {
		  ui.setPalette( false );
	      }
	  } else if ( name.equals( JInternalFrame.CONTENT_PANE_PROPERTY ) ) {
              ui.stripContentBorder(e.getNewValue());
          }
      }
  } // end class MetalPropertyChangeHandler
}

