/*
 * @(#)MultiLookAndFeel.java	1.26 98/08/28
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package javax.swing.plaf.multi;

import java.util.Vector;
import java.lang.reflect.Method;
import javax.swing.*;
import javax.swing.plaf.*;

/**
 * <p>A Multiplexing UI Look and Feel that allows more than one UI 
 * to be associated with a component at the same time. 
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.26 08/28/98
 * @author Willie Walker
 */
public class MultiLookAndFeel extends LookAndFeel {

//////////////////////////////
// LookAndFeel methods
//////////////////////////////

    public String getName() {
        return "Multiplexing Look and Feel";
    }
    
    public String getID() {
	return "Multiplex";
    }

    public String getDescription() {
        return "Allows multiple UI instances per component instance";
    }

    public boolean isNativeLookAndFeel() {
	return false;
    }

    public boolean isSupportedLookAndFeel() {
	return true;
    }

    public UIDefaults getDefaults() {
        UIDefaults table = new MultiUIDefaults();
	String packageName = "javax.swing.plaf.multi.Multi";
	Object[] uiDefaults = {
		   "ButtonUI", packageName + "ButtonUI",
	 "CheckBoxMenuItemUI", packageName + "MenuItemUI",
		 "CheckBoxUI", packageName + "ButtonUI",
             "ColorChooserUI", packageName + "ColorChooserUI",
		 "ComboBoxUI", packageName + "ComboBoxUI",
	      "DesktopIconUI", packageName + "DesktopIconUI",
	      "DesktopPaneUI", packageName + "DesktopPaneUI",
               "EditorPaneUI", packageName + "TextUI",
              "FileChooserUI", packageName + "FileChooserUI",
	    "InternalFrameUI", packageName + "InternalFrameUI",
		    "LabelUI", packageName + "LabelUI",
		     "ListUI", packageName + "ListUI",
		  "MenuBarUI", packageName + "MenuBarUI",
		 "MenuItemUI", packageName + "MenuItemUI",
		     "MenuUI", packageName + "MenuItemUI",
	       "OptionPaneUI", packageName + "OptionPaneUI",
	            "PanelUI", packageName + "PanelUI",
	    "PasswordFieldUI", packageName + "TextUI",
       "PopupMenuSeparatorUI", packageName + "SeparatorUI",
		"PopupMenuUI", packageName + "PopupMenuUI",
	      "ProgressBarUI", packageName + "ProgressBarUI",
      "RadioButtonMenuItemUI", packageName + "MenuItemUI",
	      "RadioButtonUI", packageName + "ButtonUI",
		"ScrollBarUI", packageName + "ScrollBarUI",
	       "ScrollPaneUI", packageName + "ScrollPaneUI",
		"SeparatorUI", packageName + "SeparatorUI",
		   "SliderUI", packageName + "SliderUI",
		"SplitPaneUI", packageName + "SplitPaneUI",
	       "TabbedPaneUI", packageName + "TabbedPaneUI",
	      "TableHeaderUI", packageName + "TableHeaderUI",
		    "TableUI", packageName + "TableUI",
		 "TextAreaUI", packageName + "TextUI",
		"TextFieldUI", packageName + "TextUI",
		 "TextPaneUI", packageName + "TextUI",
	     "ToggleButtonUI", packageName + "ButtonUI",
         "ToolBarSeparatorUI", packageName + "SeparatorUI",
		  "ToolBarUI", packageName + "ToolBarUI",
		  "ToolTipUI", packageName + "ToolTipUI",
		     "TreeUI", packageName + "TreeUI",
		 "ViewportUI", packageName + "ViewportUI",
	};

	table.putDefaults(uiDefaults);
	return table;
    }

///////////////////////////////
// Utility methods for the UI's
///////////////////////////////

    /**
     * Create the real UI's from the default and auxiliary look and feels,
     * placing the results in the uis vector passed in.  
     * @return the ComponentUI for the component.
     */
    public static ComponentUI createUIs(ComponentUI mui,
				        Vector      uis,
			                JComponent  target) {
        ComponentUI ui;

        // Make sure we can at least get the default UI
        //
        ui = UIManager.getDefaults().getUI(target);
        if (ui != null) {
            uis.addElement(ui);
            LookAndFeel[] auxiliaryLookAndFeels;
	    auxiliaryLookAndFeels = UIManager.getAuxiliaryLookAndFeels();
            if (auxiliaryLookAndFeels != null) {
                for (int i = 0; i < auxiliaryLookAndFeels.length; i++) {
                    ui = auxiliaryLookAndFeels[i].getDefaults().getUI(target);
                    if (ui != null) {
                        uis.addElement(ui);
                    }
                }
	    }
        } else {
	    return null;
	}

        // Don't bother returning the multiplexing UI if all we did was
        // get a UI from just the default look and feel.
        //
	if (uis.size() == 1) {
	    return (ComponentUI) uis.elementAt(0);
	} else {
	    return mui;
	}
    }

    /**
     * Turn the Vector of UI's into an array.
     */
    protected static ComponentUI[] uisToArray(Vector uis) {
        if (uis == null) {
            return new ComponentUI[0];
        } else {
            int count = uis.size();
            if (count > 0) {
                ComponentUI[] u = new ComponentUI[count];
                for (int i = 0; i < count; i++) {
                    u[i] = (ComponentUI)uis.elementAt(i);
                }
                return u;
            } else {
                return null;
            }
        }
    }
}

/**
 * We want the Multiplexing LookAndFeel to be quiet and fallback
 * gracefully if it cannot find a UI.  This class overrides the
 * getUIError method of UIDefaults, which is the method that 
 * emits error messages when it cannot find a UI class in the
 * LAF.
 */
class MultiUIDefaults extends UIDefaults {
    protected void getUIError(String msg) {
	System.err.println("Multiplexing LAF:  " + msg);
    }
}
