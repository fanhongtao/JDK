/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.basic;

import java.awt.Font;
import java.awt.Color;
import java.awt.SystemColor;
import java.awt.event.KeyEvent;
import java.awt.Insets;
import java.net.URL;
import java.io.Serializable;
import java.awt.Dimension;
import java.util.*;
import java.lang.reflect.*;

import javax.swing.LookAndFeel;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.ImageIcon;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.KeyStroke;
import javax.swing.JTextField;
import javax.swing.DefaultListCellRenderer;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.text.JTextComponent;
import javax.swing.text.DefaultEditorKit;

/**
 * Implements the a standard base LookAndFeel class from which
 * standard desktop LookAndFeel classes (JLF, Mac, Windows, etc.)
 * can be derived.  This class cannot be instantiated directly,
 * however the UI classes "Basic" defines can be.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.161 02/06/02
 * @author unattributed
 */
public abstract class BasicLookAndFeel extends LookAndFeel implements Serializable
{

    public UIDefaults getDefaults() {
	UIDefaults table = new UIDefaults();

	initClassDefaults(table);
	initSystemColorDefaults(table);
	initComponentDefaults(table);

	return table;
    }

    /**
     * Initialize the uiClassID to BasicComponentUI mapping.
     * The JComponent classes define their own uiClassID constants
     * (see AbstractComponent.getUIClassID).  This table must
     * map those constants to a BasicComponentUI class of the
     * appropriate type.
     *
     * @see #getDefaults
     */
    protected void initClassDefaults(UIDefaults table)
    {
	String basicPackageName = "javax.swing.plaf.basic.";
	Object[] uiDefaults = {
		   "ButtonUI", basicPackageName + "BasicButtonUI",
		 "CheckBoxUI", basicPackageName + "BasicCheckBoxUI",
             "ColorChooserUI", basicPackageName + "BasicColorChooserUI",
		  "MenuBarUI", basicPackageName + "BasicMenuBarUI",
		     "MenuUI", basicPackageName + "BasicMenuUI",
		 "MenuItemUI", basicPackageName + "BasicMenuItemUI",
	 "CheckBoxMenuItemUI", basicPackageName + "BasicCheckBoxMenuItemUI",
      "RadioButtonMenuItemUI", basicPackageName + "BasicRadioButtonMenuItemUI",
	      "RadioButtonUI", basicPackageName + "BasicRadioButtonUI",
	     "ToggleButtonUI", basicPackageName + "BasicToggleButtonUI",
		"PopupMenuUI", basicPackageName + "BasicPopupMenuUI",
	      "ProgressBarUI", basicPackageName + "BasicProgressBarUI",
		"ScrollBarUI", basicPackageName + "BasicScrollBarUI",
	       "ScrollPaneUI", basicPackageName + "BasicScrollPaneUI",
		"SplitPaneUI", basicPackageName + "BasicSplitPaneUI",
		   "SliderUI", basicPackageName + "BasicSliderUI",
		"SeparatorUI", basicPackageName + "BasicSeparatorUI",
	 "ToolBarSeparatorUI", basicPackageName + "BasicToolBarSeparatorUI",
       "PopupMenuSeparatorUI", basicPackageName + "BasicPopupMenuSeparatorUI",
	       "TabbedPaneUI", basicPackageName + "BasicTabbedPaneUI",
		 "TextAreaUI", basicPackageName + "BasicTextAreaUI",
		"TextFieldUI", basicPackageName + "BasicTextFieldUI",
	    "PasswordFieldUI", basicPackageName + "BasicPasswordFieldUI",
		 "TextPaneUI", basicPackageName + "BasicTextPaneUI",
               "EditorPaneUI", basicPackageName + "BasicEditorPaneUI",
		     "TreeUI", basicPackageName + "BasicTreeUI",
		    "LabelUI", basicPackageName + "BasicLabelUI",
		     "ListUI", basicPackageName + "BasicListUI",
		  "ToolBarUI", basicPackageName + "BasicToolBarUI",
		  "ToolTipUI", basicPackageName + "BasicToolTipUI",
		 "ComboBoxUI", basicPackageName + "BasicComboBoxUI",
		    "TableUI", basicPackageName + "BasicTableUI",
	      "TableHeaderUI", basicPackageName + "BasicTableHeaderUI",
	    "InternalFrameUI", basicPackageName + "BasicInternalFrameUI",
	   "StandardDialogUI", basicPackageName + "BasicStandardDialogUI",
	      "DesktopPaneUI", basicPackageName + "BasicDesktopPaneUI",
	      "DesktopIconUI", basicPackageName + "BasicDesktopIconUI",
	       "OptionPaneUI", basicPackageName + "BasicOptionPaneUI",
	            "PanelUI", basicPackageName + "BasicPanelUI",
		 "ViewportUI", basicPackageName + "BasicViewportUI",
		 "RootPaneUI", basicPackageName + "BasicRootPaneUI",
	};

	table.putDefaults(uiDefaults);
    }

    /**
     * Load the SystemColors into the defaults table.  The keys
     * for SystemColor defaults are the same as the names of
     * the public fields in SystemColor.  If the table is being
     * created on a native Windows platform we use the SystemColor
     * values, otherwise we create color objects whose values match
     * the defaults Windows95 colors.
     */
    protected void initSystemColorDefaults(UIDefaults table)
    {
	String[] defaultSystemColors = {
  	        "desktop", "#005C5C", /* Color of the desktop background */
	  "activeCaption", "#000080", /* Color for captions (title bars) when they are active. */
      "activeCaptionText", "#FFFFFF", /* Text color for text in captions (title bars). */
    "activeCaptionBorder", "#C0C0C0", /* Border color for caption (title bar) window borders. */
        "inactiveCaption", "#808080", /* Color for captions (title bars) when not active. */
    "inactiveCaptionText", "#C0C0C0", /* Text color for text in inactive captions (title bars). */
  "inactiveCaptionBorder", "#C0C0C0", /* Border color for inactive caption (title bar) window borders. */
	         "window", "#FFFFFF", /* Default color for the interior of windows */
	   "windowBorder", "#000000", /* ??? */
	     "windowText", "#000000", /* ??? */
		   "menu", "#C0C0C0", /* Background color for menus */
	       "menuText", "#000000", /* Text color for menus  */
		   "text", "#C0C0C0", /* Text background color */
	       "textText", "#000000", /* Text foreground color */
	  "textHighlight", "#000080", /* Text background color when selected */
      "textHighlightText", "#FFFFFF", /* Text color when selected */
       "textInactiveText", "#808080", /* Text color when disabled */
	        "control", "#C0C0C0", /* Default color for controls (buttons, sliders, etc) */
	    "controlText", "#000000", /* Default color for text in controls */
       "controlHighlight", "#C0C0C0",
/*  "controlHighlight", "#E0E0E0",*/ /* Specular highlight (opposite of the shadow) */
     "controlLtHighlight", "#FFFFFF", /* Highlight color for controls */
	  "controlShadow", "#808080", /* Shadow color for controls */
        "controlDkShadow", "#000000", /* Dark shadow color for controls */
	      "scrollbar", "#E0E0E0", /* Scrollbar background (usually the "track") */
		   "info", "#FFFFE1", /* ??? */
	       "infoText", "#000000"  /* ??? */
	};

	loadSystemColors(table, defaultSystemColors, isNativeLookAndFeel());
    }


    /**
     * If this is the native look and feel the initial values for the
     * system color properties are the same as the SystemColor constants.
     * If not we use the integer color values in the <code>systemColors</code>
     * argument.
     */
    protected void loadSystemColors(UIDefaults table, String[] systemColors, boolean useNative)
    {
	/* PENDING(hmuller) We don't load the system colors below because
	 * they're not reliable.  Hopefully we'll be able to do better in
	 * a future version of AWT.
	 */
	if (useNative) {
	    for(int i = 0; i < systemColors.length; i += 2) {
		Color color = Color.black;
		try {
		    String name = systemColors[i];
		    color = (Color)(SystemColor.class.getField(name).get(null));
		} catch (Exception e) {
		}
		table.put(systemColors[i], new ColorUIResource(color));
	    }
	} else {
	    for(int i = 0; i < systemColors.length; i += 2) {
		Color color = Color.black;
		try {
		    color = Color.decode(systemColors[i + 1]);
		}
		catch(NumberFormatException e) {
		    e.printStackTrace();
		}
		table.put(systemColors[i], new ColorUIResource(color));
	    }
	}
    }

    private void loadResourceBundle(UIDefaults table) {
        ResourceBundle bundle = ResourceBundle.getBundle("javax.swing.plaf.basic.resources.basic");
	Enumeration iter = bundle.getKeys();
	while(iter.hasMoreElements()) {
	    String key = (String)iter.nextElement();
	    //System.out.println("key :" +key+ " value: " + bundle.getObject(key));
	    table.put( key, bundle.getObject(key) );
	}
    }

    protected void initComponentDefaults(UIDefaults table)
    {

        loadResourceBundle(table);
	// *** Shared Fonts
	Integer twelve = new Integer(12);
	Integer fontPlain = new Integer(Font.PLAIN);
	Integer fontBold = new Integer(Font.BOLD);
	Object dialogPlain12 = new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.FontUIResource",
			  null,
			  new Object[] {"Dialog", fontPlain, twelve});
	Object serifPlain12 = new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.FontUIResource",
			  null,
			  new Object[] {"Serif", fontPlain, twelve});
	Object sansSerifPlain12 =  new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.FontUIResource",
			  null,
			  new Object[] {"SansSerif", fontPlain, twelve});
	Object monospacedPlain12 = new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.FontUIResource",
			  null,
			  new Object[] {"MonoSpaced", fontPlain, twelve});
	Object dialogBold12 = new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.FontUIResource",
			  null,
			  new Object[] {"Dialog", fontBold, twelve});


	// *** Shared Colors
	ColorUIResource red = new ColorUIResource(Color.red);
	ColorUIResource black = new ColorUIResource(Color.black);
        ColorUIResource white = new ColorUIResource(Color.white);
	ColorUIResource yellow = new ColorUIResource(Color.yellow);
        ColorUIResource gray = new ColorUIResource(Color.gray);
	ColorUIResource lightGray = new ColorUIResource(Color.lightGray);
	ColorUIResource darkGray = new ColorUIResource(Color.darkGray);
	ColorUIResource scrollBarTrack = new ColorUIResource(224, 224, 224);

        // *** Shared Insets
        InsetsUIResource zeroInsets = new InsetsUIResource(0,0,0,0);

        // *** Shared Borders
	Object marginBorder = new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.basic.BasicBorders$MarginBorder");
	Object etchedBorder = new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.BorderUIResource",
			  "getEtchedBorderUIResource");
        Object loweredBevelBorder = new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.BorderUIResource",
			  "getLoweredBevelBorderUIResource");

        Object raisedBevelBorder = new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.BorderUIResource",
			  "getRaisedBevelBorderUIResource");

        Object blackLineBorder = new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.BorderUIResource",
			  "getBlackLineBorderUIResource");
	Object focusCellHighlightBorder = new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.BorderUIResource$LineBorderUIResource",
			  null,
			  new Object[] {yellow});


	Object tableHeaderBorder = new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.BorderUIResource$BevelBorderUIResource",
			  null,
			  new Object[] { new Integer(BevelBorder.RAISED),
					 table.getColor("controlLtHighlight"),
					 table.getColor("control"),
					 table.getColor("controlDkShadow"),
					 table.getColor("controlShadow") });


	// *** Button value objects

	Object buttonBorder = 
	    new UIDefaults.ProxyLazyValue(
			    "javax.swing.plaf.basic.BasicBorders",
			    "getButtonBorder");

	Object buttonToggleBorder = 
	    new UIDefaults.ProxyLazyValue(
			    "javax.swing.plaf.basic.BasicBorders",
			    "getToggleButtonBorder");

	Object radioButtonBorder = 
	    new UIDefaults.ProxyLazyValue(
			    "javax.swing.plaf.basic.BasicBorders",
			    "getRadioButtonBorder");

	// *** FileChooser / FileView value objects

	Object newFolderIcon = LookAndFeel.makeIcon(getClass(), "icons/NewFolder.gif");
	Object upFolderIcon = LookAndFeel.makeIcon(getClass(), "icons/UpFolder.gif");
	Object homeFolderIcon = LookAndFeel.makeIcon(getClass(), "icons/HomeFolder.gif");
	Object detailsViewIcon = LookAndFeel.makeIcon(getClass(), "icons/DetailsView.gif");
	Object listViewIcon = LookAndFeel.makeIcon(getClass(), "icons/ListView.gif");
	Object directoryIcon = LookAndFeel.makeIcon(getClass(), "icons/Directory.gif");
	Object fileIcon = LookAndFeel.makeIcon(getClass(), "icons/File.gif");
	Object computerIcon = LookAndFeel.makeIcon(getClass(), "icons/Computer.gif");
	Object hardDriveIcon = LookAndFeel.makeIcon(getClass(), "icons/HardDrive.gif");
	Object floppyDriveIcon = LookAndFeel.makeIcon(getClass(), "icons/FloppyDrive.gif");


	// *** InternalFrame value objects

	Object internalFrameBorder = new UIDefaults.ProxyLazyValue(
                "javax.swing.plaf.basic.BasicBorders", 
		"getInternalFrameBorder");

	// *** List value objects

	Object listCellRendererActiveValue = new UIDefaults.ActiveValue() {
	    public Object createValue(UIDefaults table) {
		return new DefaultListCellRenderer.UIResource();
	    }
	};


	// *** Menus value objects

	Object menuBarBorder = 
	    new UIDefaults.ProxyLazyValue(
                "javax.swing.plaf.basic.BasicBorders", 
		"getMenuBarBorder");

	Object menuItemCheckIcon = 
	    new UIDefaults.ProxyLazyValue(
		"javax.swing.plaf.basic.BasicIconFactory", 
		"getMenuItemCheckIcon");

	Object menuItemArrowIcon = 
	    new UIDefaults.ProxyLazyValue(
		"javax.swing.plaf.basic.BasicIconFactory", 
		"getMenuItemArrowIcon");


	Object menuArrowIcon = 
	    new UIDefaults.ProxyLazyValue(
		"javax.swing.plaf.basic.BasicIconFactory", 
		"getMenuArrowIcon");

	Object checkBoxIcon = 
	    new UIDefaults.ProxyLazyValue(
		"javax.swing.plaf.basic.BasicIconFactory", 
		"getCheckBoxIcon");

	Object radioButtonIcon = 
	    new UIDefaults.ProxyLazyValue(
		"javax.swing.plaf.basic.BasicIconFactory", 
		"getRadioButtonIcon");


	Object checkBoxMenuItemIcon = 
	    new UIDefaults.ProxyLazyValue(
		"javax.swing.plaf.basic.BasicIconFactory", 
		"getCheckBoxMenuItemIcon");

	Object radioButtonMenuItemIcon = 
	    new UIDefaults.ProxyLazyValue(
		"javax.swing.plaf.basic.BasicIconFactory", 
		"getRadioButtonMenuItemIcon");

	Object menuItemAcceleratorDelimiter = new String("+");

	// *** OptionPane value objects

        Object optionPaneMinimumSize = new DimensionUIResource(262, 90);

	Integer zero =  new Integer(0);
        Object zeroBorder = new UIDefaults.ProxyLazyValue(
			   "javax.swing.plaf.BorderUIResource$EmptyBorderUIResource",
			   new Object[] {zero, zero, zero, zero});

	Integer ten = new Integer(10);
        Object optionPaneBorder = new UIDefaults.ProxyLazyValue(
			   "javax.swing.plaf.BorderUIResource$EmptyBorderUIResource",
			   new Object[] {ten, ten, twelve, ten});
	
        Object optionPaneButtonAreaBorder = new UIDefaults.ProxyLazyValue(
			   "javax.swing.plaf.BorderUIResource$EmptyBorderUIResource",
			   new Object[] {new Integer(6), zero, zero, zero});


	// *** ProgessBar value objects

	Object progressBarBorder = 
	    new UIDefaults.ProxyLazyValue(
			    "javax.swing.plaf.basic.BasicBorders",
			    "getProgressBarBorder");

	// ** ScrollBar value objects

	Object minimumThumbSize = new DimensionUIResource(8,8);
	Object maximumThumbSize = new DimensionUIResource(4096,4096);

	// ** Slider value objects

	Object sliderFocusInsets = new InsetsUIResource( 2, 2, 2, 2 );

	Object toolBarSeparatorSize = new DimensionUIResource( 10, 10 );


	// *** SplitPane value objects

	Object splitPaneBorder = 
	    new UIDefaults.ProxyLazyValue(
			    "javax.swing.plaf.basic.BasicBorders",
			    "getSplitPaneBorder");
	Object splitPaneDividerBorder = 
	    new UIDefaults.ProxyLazyValue(
			    "javax.swing.plaf.basic.BasicBorders",
			    "getSplitPaneDividerBorder");

	// ** TabbedBane value objects

        Object tabbedPaneTabInsets = new InsetsUIResource(0, 4, 1, 4);

        Object tabbedPaneTabPadInsets = new InsetsUIResource(2, 2, 2, 1);

        Object tabbedPaneTabAreaInsets = new InsetsUIResource(3, 2, 0, 2);

        Object tabbedPaneContentBorderInsets = new InsetsUIResource(2, 2, 3, 3);


	// *** Text value objects

	Object textFieldBorder = 
	    new UIDefaults.ProxyLazyValue(
			    "javax.swing.plaf.basic.BasicBorders",
			    "getTextFieldBorder");

        Object editorMargin = new InsetsUIResource(3,3,3,3);


	JTextComponent.KeyBinding[] fieldBindings = makeKeyBindings( new Object[]{
	    "ENTER", JTextField.notifyAction
	});

	JTextComponent.KeyBinding[] multilineBindings = makeKeyBindings( new Object[]{
		  "UP", DefaultEditorKit.upAction,
		"DOWN", DefaultEditorKit.downAction,
	     "PAGE_UP", DefaultEditorKit.pageUpAction,
	   "PAGE_DOWN", DefaultEditorKit.pageDownAction,
	       "ENTER", DefaultEditorKit.insertBreakAction,
		 "TAB", DefaultEditorKit.insertTabAction
        });

	Object caretBlinkRate = new Integer(500);
	Integer four = new Integer(4);

        // *** Component Defaults

	Object[] defaults = {

	    // *** Buttons
	    "Button.font", dialogPlain12,
	    "Button.background", table.get("control"),
	    "Button.foreground", table.get("controlText"),
	    "Button.border", buttonBorder,
	    "Button.margin", new InsetsUIResource(2, 14, 2, 14),
	    "Button.textIconGap", four,
	    "Button.textShiftOffset", zero,
	    "Button.focusInputMap", new UIDefaults.LazyInputMap(new Object[] {
                         "SPACE", "pressed",
                "released SPACE", "released"
              }),

	    "ToggleButton.font", dialogPlain12,
	    "ToggleButton.background", table.get("control"),
	    "ToggleButton.foreground", table.get("controlText"),
	    "ToggleButton.border", buttonToggleBorder,
	    "ToggleButton.margin", new InsetsUIResource(2, 14, 2, 14),
	    "ToggleButton.textIconGap", four,
	    "ToggleButton.textShiftOffset", zero,
	    "ToggleButton.focusInputMap",
	      new UIDefaults.LazyInputMap(new Object[] {
		            "SPACE", "pressed",
                   "released SPACE", "released"
	        }),

	    "RadioButton.font", dialogPlain12,
	    "RadioButton.background", table.get("control"),
	    "RadioButton.foreground", table.get("controlText"),
	    "RadioButton.border", radioButtonBorder,
	    "RadioButton.margin", new InsetsUIResource(2, 2, 2, 2),
	    "RadioButton.textIconGap", four,
	    "RadioButton.textShiftOffset", zero,
	    "RadioButton.icon", radioButtonIcon,
	    "RadioButton.focusInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
                          "SPACE", "pressed",
                 "released SPACE", "released",
			 "RETURN", "pressed"
	      }),

	    "CheckBox.font", dialogPlain12,
	    "CheckBox.background", table.get("control"),
	    "CheckBox.foreground", table.get("controlText"),
	    "CheckBox.border", radioButtonBorder,
	    "CheckBox.margin", new InsetsUIResource(2, 2, 2, 2),
	    "CheckBox.textIconGap", four,
	    "CheckBox.textShiftOffset", zero,
	    "CheckBox.icon", checkBoxIcon,
	    "CheckBox.focusInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		            "SPACE", "pressed",
                   "released SPACE", "released"
		 }),

	    // *** ColorChooser
            "ColorChooser.font", dialogPlain12,
            "ColorChooser.background", table.get("control"),
            "ColorChooser.foreground", table.get("controlText"),

            "ColorChooser.swatchesSwatchSize", new Dimension(10, 10),
            "ColorChooser.swatchesRecentSwatchSize", new Dimension(10, 10),
            "ColorChooser.swatchesDefaultRecentColor", table.get("control"),

            "ColorChooser.rgbRedMnemonic", new Integer(KeyEvent.VK_R),
            "ColorChooser.rgbGreenMnemonic", new Integer(KeyEvent.VK_G),
            "ColorChooser.rgbBlueMnemonic", new Integer(KeyEvent.VK_B),

	    // *** ComboBox
            "ComboBox.font", sansSerifPlain12,
            "ComboBox.background", white/*table.get("text")*/,
            "ComboBox.foreground", black/*table.get("TextText")*/,
            "ComboBox.selectionBackground", table.get("textHighlight"),
            "ComboBox.selectionForeground", table.get("textHighlightText"),
            "ComboBox.disabledBackground", table.get("control"),
            "ComboBox.disabledForeground", table.get("textInactiveText"),
	    "ComboBox.ancestorInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		      "ESCAPE", "hidePopup",
		     "PAGE_UP", "pageUpPassThrough",
		   "PAGE_DOWN", "pageDownPassThrough",
		        "HOME", "homePassThrough",
		         "END", "endPassThrough"
		 }),
 
	    // *** FileChooser 
	 

            "FileChooser.cancelButtonMnemonic", new Integer(KeyEvent.VK_C),
            "FileChooser.saveButtonMnemonic", new Integer(KeyEvent.VK_S),
            "FileChooser.openButtonMnemonic", new Integer(KeyEvent.VK_O),
            "FileChooser.updateButtonMnemonic", new Integer(KeyEvent.VK_U),
            "FileChooser.helpButtonMnemonic", new Integer(KeyEvent.VK_H),

	    "FileChooser.newFolderIcon", newFolderIcon,
            "FileChooser.upFolderIcon", upFolderIcon,
            "FileChooser.homeFolderIcon", homeFolderIcon,
            "FileChooser.detailsViewIcon", detailsViewIcon,
            "FileChooser.listViewIcon", listViewIcon,
	    "FileChooser.ancestorInputMap", 
	       new UIDefaults.LazyInputMap(new Object[] {
		     "ESCAPE", "cancelSelection"
		 }),

            "FileView.directoryIcon", directoryIcon,
            "FileView.fileIcon", fileIcon,
            "FileView.computerIcon", computerIcon,
            "FileView.hardDriveIcon", hardDriveIcon,
            "FileView.floppyDriveIcon", floppyDriveIcon,

	    // *** InternalFrame
            "InternalFrame.titleFont", dialogBold12,
	    "InternalFrame.border", internalFrameBorder,
            "InternalFrame.icon", LookAndFeel.makeIcon(getClass(), "icons/JavaCup.gif"),

            /* Default frame icons are undefined for Basic. */
            "InternalFrame.maximizeIcon", 
	    new UIDefaults.ProxyLazyValue(
			   "javax.swing.plaf.basic.BasicIconFactory",
			   "createEmptyFrameIcon"),
            "InternalFrame.minimizeIcon", 
	    new UIDefaults.ProxyLazyValue(
			   "javax.swing.plaf.basic.BasicIconFactory",
			   "createEmptyFrameIcon"),
            "InternalFrame.iconifyIcon", 
	    new UIDefaults.ProxyLazyValue(
			   "javax.swing.plaf.basic.BasicIconFactory",
			   "createEmptyFrameIcon"),
            "InternalFrame.closeIcon", 
	    new UIDefaults.ProxyLazyValue(
			   "javax.swing.plaf.basic.BasicIconFactory",
			   "createEmptyFrameIcon"),

	    "InternalFrame.activeTitleBackground", table.get("activeCaption"),
	    "InternalFrame.activeTitleForeground", table.get("activeCaptionText"),
	    "InternalFrame.inactiveTitleBackground", table.get("inactiveCaption"),
	    "InternalFrame.inactiveTitleForeground", table.get("inactiveCaptionText"),
	    "InternalFrame.windowBindings", new Object[] {
	      "shift ESCAPE", "showSystemMenu",
		"ctrl SPACE", "showSystemMenu",
	            "ESCAPE", "hideSystemMenu"},

	    "DesktopIcon.border", internalFrameBorder,

	    "Desktop.background", table.get("desktop"),
	    "Desktop.ancestorInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		 "ctrl F5", "restore", 
		 "ctrl F4", "close",
		 "ctrl F7", "move", 
		 "ctrl F8", "resize",
		   "RIGHT", "right",
		"KP_RIGHT", "right",
		    "LEFT", "left",
		 "KP_LEFT", "left",
		      "UP", "up",
		   "KP_UP", "up",
		    "DOWN", "down",
		 "KP_DOWN", "down",
		  "ESCAPE", "escape",
		 "ctrl F9", "minimize", 
		"ctrl F10", "maximize",
		 "ctrl F6", "selectNextFrame",
		"ctrl TAB", "selectNextFrame",
	     "ctrl alt F6", "selectNextFrame",
       "shift ctrl alt F6", "selectPreviousFrame"
	      }),

	    // *** Label
	    "Label.font", dialogPlain12,
	    "Label.background", table.get("control"),
	    "Label.foreground", table.get("controlText"),
	    "Label.disabledForeground", white,
	    "Label.disabledShadow", table.get("controlShadow"),
            "Label.border", null,

	    // *** List
	    "List.font", dialogPlain12,
 	    "List.background", table.get("window"),
	    "List.foreground", table.get("textText"),
	    "List.selectionBackground", table.get("textHighlight"),
	    "List.selectionForeground", table.get("textHighlightText"),
	    "List.focusCellHighlightBorder", focusCellHighlightBorder,
	    "List.border", null,
	    "List.cellRenderer", listCellRendererActiveValue,
	    "List.focusInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		               "UP", "selectPreviousRow",
		            "KP_UP", "selectPreviousRow",
		         "shift UP", "selectPreviousRowExtendSelection",
		      "shift KP_UP", "selectPreviousRowExtendSelection",
		             "DOWN", "selectNextRow",
		          "KP_DOWN", "selectNextRow",
		       "shift DOWN", "selectNextRowExtendSelection",
		    "shift KP_DOWN", "selectNextRowExtendSelection",
		       "ctrl SPACE", "selectNextRowExtendSelection",
		             "HOME", "selectFirstRow",
		       "shift HOME", "selectFirstRowExtendSelection",
		              "END", "selectLastRow",
		        "shift END", "selectLastRowExtendSelection",
		          "PAGE_UP", "scrollUp",
		    "shift PAGE_UP", "scrollUpExtendSelection",
		        "PAGE_DOWN", "scrollDown",
		  "shift PAGE_DOWN", "scrollDownExtendSelection",
		           "ctrl A", "selectAll",
		       "ctrl SLASH", "selectAll",
		  "ctrl BACK_SLASH", "clearSelection"
		 }),

	    // *** Menus
	    "MenuBar.font", dialogPlain12,
	    "MenuBar.background", table.get("menu"),
	    "MenuBar.foreground", table.get("menuText"),
	    "MenuBar.border", menuBarBorder,
	    "MenuBar.windowBindings", new Object[] {
		"F10", "takeFocus" },

	    "MenuItem.font", dialogPlain12,
	    "MenuItem.acceleratorFont", dialogPlain12,
	    "MenuItem.background", table.get("menu"),
	    "MenuItem.foreground", table.get("menuText"),
	    "MenuItem.selectionForeground", table.get("textHighlightText"),
	    "MenuItem.selectionBackground", table.get("textHighlight"),
	    "MenuItem.disabledForeground", null,
	    "MenuItem.acceleratorForeground", table.get("menuText"),
	    "MenuItem.acceleratorSelectionForeground", table.get("textHighlightText"),
	    "MenuItem.acceleratorDelimiter", menuItemAcceleratorDelimiter,
	    "MenuItem.border", marginBorder,
	    "MenuItem.borderPainted", Boolean.FALSE,
	    "MenuItem.margin", new InsetsUIResource(2, 2, 2, 2),
	    "MenuItem.checkIcon", menuItemCheckIcon,
	    "MenuItem.arrowIcon", menuItemArrowIcon,

	    "RadioButtonMenuItem.font", dialogPlain12,
	    "RadioButtonMenuItem.acceleratorFont", dialogPlain12,
	    "RadioButtonMenuItem.background", table.get("menu"),
	    "RadioButtonMenuItem.foreground", table.get("menuText"),
	    "RadioButtonMenuItem.selectionForeground", table.get("textHighlightText"),
	    "RadioButtonMenuItem.selectionBackground", table.get("textHighlight"),
	    "RadioButtonMenuItem.disabledForeground", null,
	    "RadioButtonMenuItem.acceleratorForeground", table.get("menuText"),
	    "RadioButtonMenuItem.acceleratorSelectionForeground", table.get("textHighlightText"),
	    "RadioButtonMenuItem.border", marginBorder,
	    "RadioButtonMenuItem.borderPainted", Boolean.FALSE,
	    "RadioButtonMenuItem.margin", new InsetsUIResource(2, 2, 2, 2),
	    "RadioButtonMenuItem.checkIcon", radioButtonMenuItemIcon,
	    "RadioButtonMenuItem.arrowIcon", menuItemArrowIcon,

	    "CheckBoxMenuItem.font", dialogPlain12,
	    "CheckBoxMenuItem.acceleratorFont", dialogPlain12,
	    "CheckBoxMenuItem.background", table.get("menu"),
	    "CheckBoxMenuItem.foreground", table.get("menuText"),
	    "CheckBoxMenuItem.selectionForeground", table.get("textHighlightText"),
	    "CheckBoxMenuItem.selectionBackground", table.get("textHighlight"),
	    "CheckBoxMenuItem.disabledForeground", null,
	    "CheckBoxMenuItem.acceleratorForeground", table.get("menuText"),
	    "CheckBoxMenuItem.acceleratorSelectionForeground", table.get("textHighlightText"),
	    "CheckBoxMenuItem.border", marginBorder,
	    "CheckBoxMenuItem.borderPainted", Boolean.FALSE,
	    "CheckBoxMenuItem.margin", new InsetsUIResource(2, 2, 2, 2),
	    "CheckBoxMenuItem.checkIcon", checkBoxMenuItemIcon,
	    "CheckBoxMenuItem.arrowIcon", menuItemArrowIcon,

	    "Menu.font", dialogPlain12,
	    "Menu.acceleratorFont", dialogPlain12,
	    "Menu.background", table.get("menu"),
	    "Menu.foreground", table.get("menuText"),
	    "Menu.selectionForeground", table.get("textHighlightText"),
	    "Menu.selectionBackground", table.get("textHighlight"),
	    "Menu.disabledForeground", null,
	    "Menu.acceleratorForeground", table.get("menuText"),
	    "Menu.acceleratorSelectionForeground", table.get("textHighlightText"),
	    "Menu.border", marginBorder,
	    "Menu.borderPainted", Boolean.FALSE,
	    "Menu.margin", new InsetsUIResource(2, 2, 2, 2),
	    "Menu.checkIcon", menuItemCheckIcon,
	    "Menu.arrowIcon", menuArrowIcon,
	    "Menu.consumesTabs", Boolean.TRUE,
	    // These window InputMap bindings are used when the Menu is
	    // selected.
	    "Menu.selectedWindowInputMapBindings", new Object[] {
		  "ESCAPE", "cancel",
                    "DOWN", "selectNext",
		 "KP_DOWN", "selectNext",
		      "UP", "selectPrevious",
		   "KP_UP", "selectPrevious",
		    "LEFT", "selectParent",
		 "KP_LEFT", "selectParent",
		   "RIGHT", "selectChild",
		"KP_RIGHT", "selectChild",
		   "ENTER", "return",
		   "SPACE", "return"
	    },

	    "PopupMenu.font", dialogPlain12,
	    "PopupMenu.background", table.get("menu"),
	    "PopupMenu.foreground", table.get("menuText"),
	    "PopupMenu.border", raisedBevelBorder,
	    "PopupMenu.selectedWindowInputMapBindings", new Object[] {
                  "ESCAPE", "cancel",
                    "DOWN", "selectNext",
                 "KP_DOWN", "selectNext",
                      "UP", "selectPrevious",
                   "KP_UP", "selectPrevious",
                    "LEFT", "selectParent",
                 "KP_LEFT", "selectParent",
                   "RIGHT", "selectChild",
                "KP_RIGHT", "selectChild",
                   "ENTER", "return",
                   "SPACE", "return"
            },

	    // *** OptionPane
	    "OptionPane.font", dialogPlain12,
	    "OptionPane.background", table.get("control"),
	    "OptionPane.foreground", table.get("controlText"),
            "OptionPane.messageForeground", table.get("controlText"),
	    "OptionPane.border", optionPaneBorder,
            "OptionPane.messageAreaBorder", zeroBorder,
            "OptionPane.buttonAreaBorder", optionPaneButtonAreaBorder,
            "OptionPane.minimumSize", optionPaneMinimumSize,
	    "OptionPane.errorIcon", LookAndFeel.makeIcon(getClass(), "icons/Error.gif"),
	    "OptionPane.informationIcon", LookAndFeel.makeIcon(getClass(), "icons/Inform.gif"),
	    "OptionPane.warningIcon", LookAndFeel.makeIcon(getClass(), "icons/Warn.gif"),
	    "OptionPane.questionIcon", LookAndFeel.makeIcon(getClass(), "icons/Question.gif"),
	    "OptionPane.windowBindings", new Object[] {
		"ESCAPE", "close" },


	    // *** Panel
	    "Panel.font", dialogPlain12,
	    "Panel.background", table.get("control"),
	    "Panel.foreground", table.get("textText"),

	    // *** ProgressBar
	    "ProgressBar.font", dialogPlain12,
	    "ProgressBar.foreground",  table.get("textHighlight"),
	    "ProgressBar.background", table.get("control"),
	    "ProgressBar.selectionForeground", table.get("control"),
	    "ProgressBar.selectionBackground", table.get("textHighlight"),
	    "ProgressBar.border", progressBarBorder,
            "ProgressBar.cellLength", new Integer(1),
            "ProgressBar.cellSpacing", zero,

           // *** Separator
            "Separator.shadow", table.get("controlShadow"),          // DEPRECATED - DO NOT USE!
            "Separator.highlight", table.get("controlLtHighlight"),  // DEPRECATED - DO NOT USE!

            "Separator.background", table.get("controlLtHighlight"),
            "Separator.foreground", table.get("controlShadow"),

	    // *** ScrollBar/ScrollPane/Viewport
	    "ScrollBar.background", scrollBarTrack,
	    "ScrollBar.foreground", table.get("control"),
	    "ScrollBar.track", table.get("scrollbar"),
	    "ScrollBar.trackHighlight", table.get("controlDkShadow"),
	    "ScrollBar.thumb", table.get("control"),
	    "ScrollBar.thumbHighlight", table.get("controlLtHighlight"),
	    "ScrollBar.thumbDarkShadow", table.get("controlDkShadow"),
	    "ScrollBar.thumbLightShadow", table.get("controlShadow"),
	    "ScrollBar.border", null,
	    "ScrollBar.minimumThumbSize", minimumThumbSize,
	    "ScrollBar.maximumThumbSize", maximumThumbSize,
	    "ScrollBar.focusInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		       "RIGHT", "negativeUnitIncrement",
		    "KP_RIGHT", "negativeUnitIncrement",
		        "DOWN", "positiveUnitIncrement",
		     "KP_DOWN", "positiveUnitIncrement",
		   "PAGE_DOWN", "positiveBlockIncrement",
		        "LEFT", "positiveUnitIncrement",
		     "KP_LEFT", "positiveUnitIncrement",
		          "UP", "negativeUnitIncrement",
		       "KP_UP", "negativeUnitIncrement",
		     "PAGE_UP", "negativeBlockIncrement",
		        "HOME", "minScroll",
		         "END", "maxScroll"
		 }),

	    "ScrollPane.font", dialogPlain12,
	    "ScrollPane.background", table.get("control"),
	    "ScrollPane.foreground", table.get("controlText"),
	    "ScrollPane.border", etchedBorder,
	    "ScrollPane.viewportBorder", null,
	    "ScrollPane.ancestorInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		           "RIGHT", "unitScrollRight",
		        "KP_RIGHT", "unitScrollRight",
		            "DOWN", "unitScrollDown",
		         "KP_DOWN", "unitScrollDown",
		            "LEFT", "unitScrollLeft",
		         "KP_LEFT", "unitScrollLeft",
		              "UP", "unitScrollUp",
		           "KP_UP", "unitScrollUp",
		         "PAGE_UP", "scrollUp",
		       "PAGE_DOWN", "scrollDown",
		    "ctrl PAGE_UP", "scrollLeft",
		  "ctrl PAGE_DOWN", "scrollRight",
		       "ctrl HOME", "scrollHome",
		        "ctrl END", "scrollEnd"
		 }),

	    "Viewport.font", dialogPlain12,
	    "Viewport.background", table.get("control"),
	    "Viewport.foreground", table.get("textText"),

	    // *** Slider
	    "Slider.foreground", table.get("control"),
	    "Slider.background", table.get("control"),
	    "Slider.highlight", table.get("controlLtHighlight"),
	    "Slider.shadow", table.get("controlShadow"),
	    "Slider.focus", table.get("controlDkShadow"),
	    "Slider.border", null,
	    "Slider.focusInsets", sliderFocusInsets,
	    "Slider.focusInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		       "RIGHT", "positiveUnitIncrement",
		    "KP_RIGHT", "positiveUnitIncrement",
		        "DOWN", "negativeUnitIncrement",
		     "KP_DOWN", "negativeUnitIncrement",
		   "PAGE_DOWN", "negativeBlockIncrement",
		        "LEFT", "negativeUnitIncrement",
		     "KP_LEFT", "negativeUnitIncrement",
		          "UP", "positiveUnitIncrement",
		       "KP_UP", "positiveUnitIncrement",
		     "PAGE_UP", "positiveBlockIncrement",
		        "HOME", "minScroll",
		         "END", "maxScroll"
		 }),

	    // *** SplitPane
	    "SplitPane.background", table.get("control"),
	    "SplitPane.highlight", table.get("controlLtHighlight"),
	    "SplitPane.shadow", table.get("controlShadow"),
	    "SplitPane.border", splitPaneBorder,
	    "SplitPane.dividerSize", new Integer(7),
	    "SplitPaneDivider.border", splitPaneDividerBorder,
	    "SplitPane.ancestorInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		        "UP", "negativeIncrement",
		      "DOWN", "positiveIncrement",
		      "LEFT", "negativeIncrement",
		     "RIGHT", "positiveIncrement",
		     "KP_UP", "negativeIncrement",
		   "KP_DOWN", "positiveIncrement",
		   "KP_LEFT", "negativeIncrement",
		  "KP_RIGHT", "positiveIncrement",
		      "HOME", "selectMin",
		       "END", "selectMax",
		        "F8", "startResize",
		        "F6", "toggleFocus"
		 }),
		

	    // *** TabbedPane
            "TabbedPane.font", dialogPlain12,
            "TabbedPane.background", table.get("control"),
            "TabbedPane.foreground", table.get("controlText"),
            "TabbedPane.lightHighlight", table.get("controlLtHighlight"),
            "TabbedPane.highlight", table.get("controlHighlight"),
            "TabbedPane.shadow", table.get("controlShadow"),
            "TabbedPane.darkShadow", table.get("controlDkShadow"),
            "TabbedPane.focus", table.get("controlText"),
            "TabbedPane.textIconGap", four,
            "TabbedPane.tabInsets", tabbedPaneTabInsets,
            "TabbedPane.selectedTabPadInsets", tabbedPaneTabPadInsets,
            "TabbedPane.tabAreaInsets", tabbedPaneTabAreaInsets,
            "TabbedPane.contentBorderInsets", tabbedPaneContentBorderInsets,
            "TabbedPane.tabRunOverlay", new Integer(2),
	    "TabbedPane.focusInputMap",
	      new UIDefaults.LazyInputMap(new Object[] {
		         "RIGHT", "navigateRight",
	              "KP_RIGHT", "navigateRight",
	                  "LEFT", "navigateLeft",
	               "KP_LEFT", "navigateLeft",
	                    "UP", "navigateUp",
	                 "KP_UP", "navigateUp",
	                  "DOWN", "navigateDown",
	               "KP_DOWN", "navigateDown",
	             "ctrl DOWN", "requestFocusForVisibleComponent",
	          "ctrl KP_DOWN", "requestFocusForVisibleComponent",
		}),
	    "TabbedPane.ancestorInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		   "ctrl PAGE_DOWN", "navigatePageDown",
	             "ctrl PAGE_UP", "navigatePageUp",
	                  "ctrl UP", "requestFocus",
	               "ctrl KP_UP", "requestFocus",
		 }),


	    // *** Table
	    "Table.font", dialogPlain12,
	    "Table.foreground", table.get("controlText"),  // cell text color
	    "Table.background", table.get("window"),  // cell background color
	    "Table.selectionForeground", table.get("textHighlightText"),
	    "Table.selectionBackground", table.get("textHighlight"),
      	    "Table.gridColor", gray,  // grid line color
	    "Table.focusCellBackground", table.get("window"),
	    "Table.focusCellForeground", table.get("controlText"),
	    "Table.focusCellHighlightBorder", focusCellHighlightBorder,
	    "Table.scrollPaneBorder", loweredBevelBorder,
	    "Table.ancestorInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		                "RIGHT", "selectNextColumn",
		             "KP_RIGHT", "selectNextColumn",
		                 "LEFT", "selectPreviousColumn",
		              "KP_LEFT", "selectPreviousColumn",
		                 "DOWN", "selectNextRow",
		              "KP_DOWN", "selectNextRow",
		                   "UP", "selectPreviousRow",
		                "KP_UP", "selectPreviousRow",
		          "shift RIGHT", "selectNextColumnExtendSelection",
		       "shift KP_RIGHT", "selectNextColumnExtendSelection",
		           "shift LEFT", "selectPreviousColumnExtendSelection",
		        "shift KP_LEFT", "selectPreviousColumnExtendSelection",
		           "shift DOWN", "selectNextRowExtendSelection",
		        "shift KP_DOWN", "selectNextRowExtendSelection",
		             "shift UP", "selectPreviousRowExtendSelection",
		          "shift KP_UP", "selectPreviousRowExtendSelection",
		              "PAGE_UP", "scrollUpChangeSelection",
		            "PAGE_DOWN", "scrollDownChangeSelection",
		                 "HOME", "selectFirstColumn",
		                  "END", "selectLastColumn",
		        "shift PAGE_UP", "scrollUpExtendSelection",
		      "shift PAGE_DOWN", "scrollDownExtendSelection",
		           "shift HOME", "selectFirstColumnExtendSelection",
		            "shift END", "selectLastColumnExtendSelection",
		         "ctrl PAGE_UP", "scrollLeftChangeSelection",
		       "ctrl PAGE_DOWN", "scrollRightChangeSelection",
		            "ctrl HOME", "selectFirstRow",
		             "ctrl END", "selectLastRow",
		   "ctrl shift PAGE_UP", "scrollRightExtendSelection",
		 "ctrl shift PAGE_DOWN", "scrollLeftExtendSelection",
		      "ctrl shift HOME", "selectFirstRowExtendSelection",
		       "ctrl shift END", "selectLastRowExtendSelection",
		                  "TAB", "selectNextColumnCell",
		            "shift TAB", "selectPreviousColumnCell",
		                "ENTER", "selectNextRowCell",
		          "shift ENTER", "selectPreviousRowCell",
		               "ctrl A", "selectAll",
		               "ESCAPE", "cancel",
		                   "F2", "startEditing"
		 }),

	    "TableHeader.font", dialogPlain12,
	    "TableHeader.foreground", table.get("controlText"), // header text color
	    "TableHeader.background", table.get("control"), // header background
	    "TableHeader.cellBorder", tableHeaderBorder,

	    // *** Text
	    "TextField.font", sansSerifPlain12,
	    "TextField.background", table.get("window"),
	    "TextField.foreground", table.get("textText"),
	    "TextField.inactiveForeground", table.get("textInactiveText"),
	    "TextField.selectionBackground", table.get("textHighlight"),
	    "TextField.selectionForeground", table.get("textHighlightText"),
	    "TextField.caretForeground", table.get("textText"),
	    "TextField.caretBlinkRate", caretBlinkRate,
	    "TextField.border", textFieldBorder,
            "TextField.margin", zeroInsets,
	    "TextField.keyBindings", fieldBindings,

	    "PasswordField.font", monospacedPlain12,
	    "PasswordField.background", table.get("window"),
	    "PasswordField.foreground", table.get("textText"),
	    "PasswordField.inactiveForeground", table.get("textInactiveText"),
	    "PasswordField.selectionBackground", table.get("textHighlight"),
	    "PasswordField.selectionForeground", table.get("textHighlightText"),
	    "PasswordField.caretForeground", table.get("textText"),
	    "PasswordField.caretBlinkRate", caretBlinkRate,
	    "PasswordField.border", textFieldBorder,
            "PasswordField.margin", zeroInsets,
	    "PasswordField.keyBindings", fieldBindings,

	    "TextArea.font", monospacedPlain12,
	    "TextArea.background", table.get("window"),
	    "TextArea.foreground", table.get("textText"),
	    "TextArea.inactiveForeground", table.get("textInactiveText"),
	    "TextArea.selectionBackground", table.get("textHighlight"),
	    "TextArea.selectionForeground", table.get("textHighlightText"),
	    "TextArea.caretForeground", table.get("textText"),
	    "TextArea.caretBlinkRate", caretBlinkRate,
	    "TextArea.border", marginBorder,
            "TextArea.margin", zeroInsets,
	    "TextArea.keyBindings", multilineBindings,

	    "TextPane.font", serifPlain12,
	    "TextPane.background", white,
	    "TextPane.foreground", table.get("textText"),
	    "TextPane.selectionBackground", lightGray,
	    "TextPane.selectionForeground", table.get("textHighlightText"),
	    "TextPane.caretForeground", table.get("textText"),
	    "TextPane.caretBlinkRate", caretBlinkRate,
	    "TextPane.inactiveForeground", table.get("textInactiveText"),
	    "TextPane.border", marginBorder,
            "TextPane.margin", editorMargin,
	    "TextPane.keyBindings", multilineBindings,

	    "EditorPane.font", serifPlain12,
	    "EditorPane.background", white,
	    "EditorPane.foreground", table.get("textText"),
	    "EditorPane.selectionBackground", lightGray,
	    "EditorPane.selectionForeground", table.get("textHighlightText"),
	    "EditorPane.caretForeground", red,
	    "EditorPane.caretBlinkRate", caretBlinkRate,
	    "EditorPane.inactiveForeground", table.get("textInactiveText"),
	    "EditorPane.border", marginBorder,
            "EditorPane.margin", editorMargin,
	    "EditorPane.keyBindings", multilineBindings,

	    // *** TitledBorder
            "TitledBorder.font", dialogPlain12,
            "TitledBorder.titleColor", table.get("controlText"),
            "TitledBorder.border", etchedBorder,

	    // *** ToolBar
	    "ToolBar.font", dialogPlain12,
	    "ToolBar.background", table.get("control"),
	    "ToolBar.foreground", table.get("controlText"),
	    "ToolBar.dockingBackground", table.get("control"),
	    "ToolBar.dockingForeground", red,
	    "ToolBar.floatingBackground", table.get("control"),
	    "ToolBar.floatingForeground", darkGray,
	    "ToolBar.border", etchedBorder,
	    "ToolBar.separatorSize", toolBarSeparatorSize,
	    "ToolBar.ancestorInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		        "UP", "navigateUp",
		     "KP_UP", "navigateUp",
		      "DOWN", "navigateDown",
		   "KP_DOWN", "navigateDown",
		      "LEFT", "navigateLeft",
		   "KP_LEFT", "navigateLeft",
		     "RIGHT", "navigateRight",
		  "KP_RIGHT", "navigateRight"
		 }),

	    // *** ToolTips
            "ToolTip.font", sansSerifPlain12,
            "ToolTip.background", table.get("info"),
            "ToolTip.foreground", table.get("infoText"),
            "ToolTip.border", blackLineBorder,

	    // *** Tree
	    "Tree.font", dialogPlain12,
	    "Tree.background", table.get("window"),
            "Tree.foreground", table.get("textText"),
	    "Tree.hash", gray,
	    "Tree.textForeground", table.get("textText"),
	    "Tree.textBackground", table.get("text"),
	    "Tree.selectionForeground", table.get("textHighlightText"),
	    "Tree.selectionBackground", table.get("textHighlight"),
	    "Tree.selectionBorderColor", black,
	    "Tree.editorBorder", blackLineBorder,
	    "Tree.leftChildIndent", new Integer(7),
	    "Tree.rightChildIndent", new Integer(13),
	    "Tree.rowHeight", new Integer(16),
	    "Tree.scrollsOnExpand", Boolean.TRUE,
	    "Tree.openIcon", LookAndFeel.makeIcon(getClass(), "icons/TreeOpen.gif"),
	    "Tree.closedIcon", LookAndFeel.makeIcon(getClass(), "icons/TreeClosed.gif"),
	    "Tree.leafIcon", LookAndFeel.makeIcon(getClass(), "icons/TreeLeaf.gif"),
	    "Tree.expandedIcon", null,
	    "Tree.collapsedIcon", null,
	    "Tree.changeSelectionWithFocus", Boolean.TRUE,
	    "Tree.drawsFocusBorderAroundIcon", Boolean.FALSE,
	    "Tree.focusInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		                     "UP", "selectPrevious",
		                  "KP_UP", "selectPrevious",
		               "shift UP", "selectPreviousExtendSelection",
		            "shift KP_UP", "selectPreviousExtendSelection",
		                   "DOWN", "selectNext",
		                "KP_DOWN", "selectNext",
		             "shift DOWN", "selectNextExtendSelection",
		          "shift KP_DOWN", "selectNextExtendSelection",
		                  "RIGHT", "selectChild",
		               "KP_RIGHT", "selectChild",
		                   "LEFT", "selectParent",
		                "KP_LEFT", "selectParent",
		                "PAGE_UP", "scrollUpChangeSelection",
		          "shift PAGE_UP", "scrollUpExtendSelection",
		              "PAGE_DOWN", "scrollDownChangeSelection",
		        "shift PAGE_DOWN", "scrollDownExtendSelection",
		                   "HOME", "selectFirst",
		             "shift HOME", "selectFirstExtendSelection",
		                    "END", "selectLast",
		              "shift END", "selectLastExtendSelection",
		                  "ENTER", "toggle",
		                     "F2", "startEditing",
		                 "ctrl A", "selectAll",
		             "ctrl SLASH", "selectAll",
		        "ctrl BACK_SLASH", "clearSelection",
		             "ctrl SPACE", "toggleSelectionPreserveAnchor",
		            "shift SPACE", "extendSelection",
		              "ctrl HOME", "selectFirstChangeLead",
		               "ctrl END", "selectLastChangeLead",
		                "ctrl UP", "selectPreviousChangeLead",
		             "ctrl KP_UP", "selectPreviousChangeLead",
		              "ctrl DOWN", "selectNextChangeLead",
		           "ctrl KP_DOWN", "selectNextChangeLead",
		         "ctrl PAGE_DOWN", "scrollDownChangeLead",
		   "ctrl shift PAGE_DOWN", "scrollDownExtendSelection",
		           "ctrl PAGE_UP", "scrollUpChangeLead",
		     "ctrl shift PAGE_UP", "scrollUpExtendSelection",
		              "ctrl LEFT", "scrollLeft",
		           "ctrl KP_LEFT", "scrollLeft",
		             "ctrl RIGHT", "scrollRight",
		          "ctrl KP_RIGHT", "scrollRight",
		                  "SPACE", "toggleSelectionPreserveAnchor",
		 }),
	    "Tree.ancestorInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		     "ESCAPE", "cancel"
		 }),

	    // These bindings are only enabled when there is a default
	    // button set on the rootpane.
	    "RootPane.defaultButtonWindowKeyBindings", new Object[] {
		             "ENTER", "press",
		    "released ENTER", "release",
		        "ctrl ENTER", "press",
	       "ctrl released ENTER", "release"
	      },
	};

	table.putDefaults(defaults);
    }

}
