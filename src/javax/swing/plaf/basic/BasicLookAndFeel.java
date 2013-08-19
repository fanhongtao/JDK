/*
 * @(#)BasicLookAndFeel.java	1.208 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.basic;

import java.awt.Font;
import java.awt.Color;
import java.awt.SystemColor;
import java.awt.event.*;
import java.awt.Insets;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.net.URL;
import java.io.*;
import java.awt.Dimension;
import java.awt.KeyboardFocusManager;
import java.util.*;
import java.lang.reflect.*;
import javax.sound.sampled.*;

import javax.swing.LookAndFeel;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.ImageIcon;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.KeyStroke;
import javax.swing.JTextField;
import javax.swing.DefaultListCellRenderer;
import javax.swing.FocusManager;
import javax.swing.LayoutFocusTraversalPolicy;
import javax.swing.SwingUtilities;
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
 * future Swing releases. The current serialization support is
 * appropriate for short term storage or RMI between applications running
 * the same version of Swing.  As of 1.4, support for long term storage
 * of all JavaBeans<sup><font size="-2">TM</font></sup>
 * has been added to the <code>java.beans</code> package.
 * Please see {@link java.beans.XMLEncoder}.
 *
 * @version 1.208 01/23/03
 * @author unattributed
 */
public abstract class BasicLookAndFeel extends LookAndFeel implements Serializable
{
    /**
     * Lock used when manipulating clipPlaying.
     */
    private Object audioLock = new Object();
    /**
     * The Clip that is currently playing (set in AudioAction).
     */
    private Clip clipPlaying;


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
       "FormattedTextFieldUI", basicPackageName + "BasicFormattedTextFieldUI",
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
		  "SpinnerUI", basicPackageName + "BasicSpinnerUI",
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
       "controlHighlight", "#C0C0C0", /* Specular highlight (opposite of the shadow) */
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
    /**
     * Initialize the defaults table with the name of the ResourceBundle
     * used for getting localized defaults.  Also initialize the default
     * locale used when no locale is passed into UIDefaults.get().  The
     * default locale should generally not be relied upon. It is here for
     * compatability with releases prior to 1.4.
     */
    private void initResourceBundle(UIDefaults table) {
        table.setDefaultLocale( Locale.getDefault() );
        table.addResourceBundle( "com.sun.swing.internal.plaf.basic.resources.basic" );
    }

    protected void initComponentDefaults(UIDefaults table)
    {

        initResourceBundle(table);

	// *** Shared Integers
	Integer fiveHundred = new Integer(500);

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
	
	Object popupMenuBorder = new UIDefaults.ProxyLazyValue(
			  "javax.swing.plaf.basic.BasicBorders",
			  "getInternalFrameBorder");

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

	Object caretBlinkRate = fiveHundred;
	Integer four = new Integer(4);

	Object[] allAuditoryCues = new Object[] {
		"CheckBoxMenuItem.commandSound",
		"InternalFrame.closeSound",
		"InternalFrame.maximizeSound",
		"InternalFrame.minimizeSound",
		"InternalFrame.restoreDownSound",
		"InternalFrame.restoreUpSound",
		"MenuItem.commandSound",
		"OptionPane.errorSound",
		"OptionPane.informationSound",
		"OptionPane.questionSound",
		"OptionPane.warningSound",
		"PopupMenu.popupSound",
		"RadioButtonMenuItem.commandSound"};

	Object[] noAuditoryCues = new Object[] {"mute"};

        // *** Component Defaults

	Object[] defaults = {
	    // *** Auditory Feedback
	    "AuditoryCues.cueList", allAuditoryCues,
	    "AuditoryCues.allAuditoryCues", allAuditoryCues,
	    "AuditoryCues.noAuditoryCues", noAuditoryCues,
	    // this key defines which of the various cues to render.
	    // L&Fs that want auditory feedback NEED to override playList.
	    "AuditoryCues.playList", null,

	    // *** Buttons
	    "Button.font", dialogPlain12,
	    "Button.background", table.get("control"),
	    "Button.foreground", table.get("controlText"),
	    "Button.shadow", table.getColor("controlShadow"),
            "Button.darkShadow", table.getColor("controlDkShadow"),
            "Button.light", table.getColor("controlHighlight"),
            "Button.highlight", table.getColor("controlLtHighlight"),
	    "Button.border", buttonBorder,
	    "Button.margin", new InsetsUIResource(2, 14, 2, 14),
	    "Button.textIconGap", four,
	    "Button.textShiftOffset", zero,
	    "Button.focusInputMap", new UIDefaults.LazyInputMap(new Object[] {
                         "SPACE", "pressed",
                "released SPACE", "released",
                         "ENTER", "pressed",
                "released ENTER", "released"
              }),

	    "ToggleButton.font", dialogPlain12,
	    "ToggleButton.background", table.get("control"),
	    "ToggleButton.foreground", table.get("controlText"),
	    "ToggleButton.shadow", table.getColor("controlShadow"),
            "ToggleButton.darkShadow", table.getColor("controlDkShadow"),
            "ToggleButton.light", table.getColor("controlHighlight"),
            "ToggleButton.highlight", table.getColor("controlLtHighlight"),
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
	    "RadioButton.shadow", table.getColor("controlShadow"),
            "RadioButton.darkShadow", table.getColor("controlDkShadow"),
            "RadioButton.light", table.getColor("controlHighlight"),
            "RadioButton.highlight", table.getColor("controlLtHighlight"),
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

            "ColorChooser.rgbRedMnemonic", new Integer(KeyEvent.VK_D),
            "ColorChooser.rgbGreenMnemonic", new Integer(KeyEvent.VK_N),
            "ColorChooser.rgbBlueMnemonic", new Integer(KeyEvent.VK_B),

	    // *** ComboBox
            "ComboBox.font", sansSerifPlain12,
            "ComboBox.background", table.get("window"),
            "ComboBox.foreground", table.get("textText"),
	    "ComboBox.buttonBackground", table.get("control"),
	    "ComboBox.buttonShadow", table.get("controlShadow"),
	    "ComboBox.buttonDarkShadow", table.get("controlDkShadow"),
	    "ComboBox.buttonHighlight", table.get("controlLtHighlight"),
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
		         "END", "endPassThrough",
		       "ENTER", "enterPressed"
		 }),
 
	    // *** FileChooser 
	 

            "FileChooser.cancelButtonMnemonic", new Integer(KeyEvent.VK_C),
            "FileChooser.saveButtonMnemonic", new Integer(KeyEvent.VK_S),
            "FileChooser.openButtonMnemonic", new Integer(KeyEvent.VK_O),
            "FileChooser.updateButtonMnemonic", new Integer(KeyEvent.VK_U),
            "FileChooser.helpButtonMnemonic", new Integer(KeyEvent.VK_H),
            "FileChooser.directoryOpenButtonMnemonic", new Integer(KeyEvent.VK_O),

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
	    "InternalFrame.borderColor", table.get("control"),
	    "InternalFrame.borderShadow", table.get("controlShadow"),
	    "InternalFrame.borderDarkShadow", table.getColor("controlDkShadow"),
	    "InternalFrame.borderHighlight", table.getColor("controlLtHighlight"),
	    "InternalFrame.borderLight", table.getColor("controlHighlight"),
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
	    // InternalFrame Auditory Cue Mappings
            "InternalFrame.closeSound", null,
            "InternalFrame.maximizeSound", null,
            "InternalFrame.minimizeSound", null,
            "InternalFrame.restoreDownSound", null,
            "InternalFrame.restoreUpSound", null,

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
             "shift RIGHT", "shrinkRight",
          "shift KP_RIGHT", "shrinkRight",
		    "LEFT", "left",
		 "KP_LEFT", "left",
              "shift LEFT", "shrinkLeft",
           "shift KP_LEFT", "shrinkLeft",
		      "UP", "up",
		   "KP_UP", "up",
                "shift UP", "shrinkUp",
             "shift KP_UP", "shrinkUp",
		    "DOWN", "down",
		 "KP_DOWN", "down",
              "shift DOWN", "shrinkDown",
           "shift KP_DOWN", "shrinkDown",
		  "ESCAPE", "escape",
		 "ctrl F9", "minimize", 
		"ctrl F10", "maximize",
		 "ctrl F6", "selectNextFrame",
		"ctrl TAB", "selectNextFrame",
	     "ctrl alt F6", "selectNextFrame",
       "shift ctrl alt F6", "selectPreviousFrame",
                "ctrl F12", "navigateNext",
          "shift ctrl F12", "navigatePrevious"
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
                           "ctrl C", "copy",
                           "ctrl V", "paste",
                           "ctrl X", "cut",
                             "COPY", "copy",
                            "PASTE", "paste",
                              "CUT", "cut",
		               "UP", "selectPreviousRow",
		            "KP_UP", "selectPreviousRow",
		         "shift UP", "selectPreviousRowExtendSelection",
		      "shift KP_UP", "selectPreviousRowExtendSelection",
		             "DOWN", "selectNextRow",
		          "KP_DOWN", "selectNextRow",
		       "shift DOWN", "selectNextRowExtendSelection",
		    "shift KP_DOWN", "selectNextRowExtendSelection",
		             "LEFT", "selectPreviousColumn",
		          "KP_LEFT", "selectPreviousColumn",
		       "shift LEFT", "selectPreviousColumnExtendSelection",
		    "shift KP_LEFT", "selectPreviousColumnExtendSelection",
		            "RIGHT", "selectNextColumn",
		         "KP_RIGHT", "selectNextColumn",
		      "shift RIGHT", "selectNextColumnExtendSelection",
		   "shift KP_RIGHT", "selectNextColumnExtendSelection",
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
	    "List.focusInputMap.RightToLeft",
	       new UIDefaults.LazyInputMap(new Object[] {
		             "LEFT", "selectNextColumn",
		          "KP_LEFT", "selectNextColumn",
		       "shift LEFT", "selectNextColumnExtendSelection",
		    "shift KP_LEFT", "selectNextColumnExtendSelection",
		            "RIGHT", "selectPreviousColumn",
		         "KP_RIGHT", "selectPreviousColumn",
		      "shift RIGHT", "selectPreviousColumnExtendSelection",
		   "shift KP_RIGHT", "selectPreviousColumnExtendSelection",
		 }),

	    // *** Menus
	    "MenuBar.font", dialogPlain12,
	    "MenuBar.background", table.get("menu"),
	    "MenuBar.foreground", table.get("menuText"),
	    "MenuBar.shadow", table.getColor("controlShadow"),
            "MenuBar.highlight", table.getColor("controlLtHighlight"),
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
	    "MenuItem.commandSound", null,

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
	    "RadioButtonMenuItem.commandSound", null,

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
	    "CheckBoxMenuItem.commandSound", null,

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
	    "Menu.menuPopupOffsetX", new Integer(0),
	    "Menu.menuPopupOffsetY", new Integer(0),
	    "Menu.submenuPopupOffsetX", new Integer(0),
	    "Menu.submenuPopupOffsetY", new Integer(0),
 	    "Menu.shortcutKeys", new int[] {KeyEvent.ALT_MASK},
            "Menu.crossMenuMnemonic", Boolean.TRUE,

	    // PopupMenu
	    "PopupMenu.font", dialogPlain12,
	    "PopupMenu.background", table.get("menu"),
	    "PopupMenu.foreground", table.get("menuText"),
	    "PopupMenu.border", popupMenuBorder,
	         // Internal Frame Auditory Cue Mappings
            "PopupMenu.popupSound", null,
	    // These window InputMap bindings are used when the Menu is
	    // selected.
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
	    "PopupMenu.selectedWindowInputMapBindings.RightToLeft", new Object[] {
		    "LEFT", "selectChild",
		 "KP_LEFT", "selectChild",
		   "RIGHT", "selectParent",
		"KP_RIGHT", "selectParent",
	    },

	    // *** OptionPane
            // You can additionaly define OptionPane.messageFont which will
            // dictate the fonts used for the message, and
            // OptionPane.buttonFont, which defines the font for the buttons.
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
	         // OptionPane Auditory Cue Mappings
            "OptionPane.errorSound", null,
            "OptionPane.informationSound", null, // Info and Plain
            "OptionPane.questionSound", null,
            "OptionPane.warningSound", null,
	    "OptionPane.buttonClickThreshhold", fiveHundred,

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
            "ProgressBar.repaintInterval", new Integer(50),
            "ProgressBar.cycleTime", new Integer(3000),

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
	    "ScrollBar.thumbShadow", table.get("controlShadow"),
	    "ScrollBar.border", null,
	    "ScrollBar.minimumThumbSize", minimumThumbSize,
	    "ScrollBar.maximumThumbSize", maximumThumbSize,
	    "ScrollBar.focusInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
		       "RIGHT", "positiveUnitIncrement",
		    "KP_RIGHT", "positiveUnitIncrement",
		        "DOWN", "positiveUnitIncrement",
		     "KP_DOWN", "positiveUnitIncrement",
		   "PAGE_DOWN", "positiveBlockIncrement",
		        "LEFT", "negativeUnitIncrement",
		     "KP_LEFT", "negativeUnitIncrement",
		          "UP", "negativeUnitIncrement",
		       "KP_UP", "negativeUnitIncrement",
		     "PAGE_UP", "negativeBlockIncrement",
		        "HOME", "minScroll",
		         "END", "maxScroll"
		 }),
	    "ScrollBar.focusInputMap.RightToLeft",
	       new UIDefaults.LazyInputMap(new Object[] {
		       "RIGHT", "negativeUnitIncrement",
		    "KP_RIGHT", "negativeUnitIncrement",
		        "LEFT", "positiveUnitIncrement",
		     "KP_LEFT", "positiveUnitIncrement",
		 }),
            "ScrollBar.width", new Integer(16),

	    "ScrollPane.font", dialogPlain12,
	    "ScrollPane.background", table.get("control"),
	    "ScrollPane.foreground", table.get("controlText"),
	    "ScrollPane.border", textFieldBorder,
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
	    "ScrollPane.ancestorInputMap.RightToLeft",
	       new UIDefaults.LazyInputMap(new Object[] {
		    "ctrl PAGE_UP", "scrollRight",
		  "ctrl PAGE_DOWN", "scrollLeft",
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
	    "Slider.focusInputMap.RightToLeft",
	       new UIDefaults.LazyInputMap(new Object[] {
		       "RIGHT", "negativeUnitIncrement",
		    "KP_RIGHT", "negativeUnitIncrement",
		        "LEFT", "positiveUnitIncrement",
		     "KP_LEFT", "positiveUnitIncrement",
		 }),

	    // *** Spinner
	    "Spinner.font", monospacedPlain12,
	    "Spinner.background", table.get("control"),
	    "Spinner.foreground", table.get("control"),
	    "Spinner.border", textFieldBorder,
	    "Spinner.arrowButtonBorder", null,
	    "Spinner.arrowButtonInsets", null,
	    "Spinner.arrowButtonSize", new Dimension(16, 5),
            "Spinner.ancestorInputMap",
	       new UIDefaults.LazyInputMap(new Object[] {
                               "UP", "increment",
                            "KP_UP", "increment",
                             "DOWN", "decrement",
                          "KP_DOWN", "decrement",
               }),
	    "Spinner.editorBorderPainted", Boolean.FALSE,

	    // *** SplitPane
	    "SplitPane.background", table.get("control"),
	    "SplitPane.highlight", table.get("controlLtHighlight"),
	    "SplitPane.shadow", table.get("controlShadow"),
	    "SplitPane.darkShadow", table.get("controlDkShadow"),
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
		        "F6", "toggleFocus",
		  "ctrl TAB", "focusOutForward",
 	    "ctrl shift TAB", "focusOutBackward"
		 }),

	    // *** TabbedPane
            "TabbedPane.font", dialogPlain12,
            "TabbedPane.background", table.get("control"),
            "TabbedPane.foreground", table.get("controlText"),
            "TabbedPane.highlight", table.get("controlLtHighlight"),
            "TabbedPane.light", table.get("controlHighlight"),
            "TabbedPane.shadow", table.get("controlShadow"),
            "TabbedPane.darkShadow", table.get("controlDkShadow"),
	    "TabbedPane.selected", null,
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
		         "ctrl TAB", "navigateNext",
		   "ctrl shift TAB", "navigatePrevious",
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
                               "ctrl C", "copy",
                               "ctrl V", "paste",
                               "ctrl X", "cut",
                                 "COPY", "copy",
                                "PASTE", "paste",
                                  "CUT", "cut",
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
	    "Table.ancestorInputMap.RightToLeft",
	       new UIDefaults.LazyInputMap(new Object[] {
		                "RIGHT", "selectPreviousColumn",
		             "KP_RIGHT", "selectPreviousColumn",
		                 "LEFT", "selectNextColumn",
		              "KP_LEFT", "selectNextColumn",
		          "shift RIGHT", "selectPreviousColumnExtendSelection",
		       "shift KP_RIGHT", "selectPreviousColumnExtendSelection",
		           "shift LEFT", "selectNextColumnExtendSelection",
		        "shift KP_LEFT", "selectNextColumnExtendSelection",
		         "ctrl PAGE_UP", "scrollRightChangeSelection",
		       "ctrl PAGE_DOWN", "scrollLeftChangeSelection",
		   "ctrl shift PAGE_UP", "scrollRightExtendSelection",
		 "ctrl shift PAGE_DOWN", "scrollLeftExtendSelection",
		 }),

	    "TableHeader.font", dialogPlain12,
	    "TableHeader.foreground", table.get("controlText"), // header text color
	    "TableHeader.background", table.get("control"), // header background
	    "TableHeader.cellBorder", tableHeaderBorder,

	    // *** Text
	    "TextField.font", sansSerifPlain12,
	    "TextField.background", table.get("window"),
	    "TextField.foreground", table.get("textText"),
            "TextField.shadow", table.getColor("controlShadow"),
            "TextField.darkShadow", table.getColor("controlDkShadow"),
            "TextField.light", table.getColor("controlHighlight"),
            "TextField.highlight", table.getColor("controlLtHighlight"),
	    "TextField.inactiveForeground", table.get("textInactiveText"),
	    "TextField.inactiveBackground", table.get("control"),
	    "TextField.selectionBackground", table.get("textHighlight"),
	    "TextField.selectionForeground", table.get("textHighlightText"),
	    "TextField.caretForeground", table.get("textText"),
	    "TextField.caretBlinkRate", caretBlinkRate,
	    "TextField.border", textFieldBorder,
            "TextField.margin", zeroInsets,

	    "FormattedTextField.font", sansSerifPlain12,
	    "FormattedTextField.background", table.get("window"),
	    "FormattedTextField.foreground", table.get("textText"),
	    "FormattedTextField.inactiveForeground", table.get("textInactiveText"),
	    "FormattedTextField.inactiveBackground", table.get("control"),
	    "FormattedTextField.selectionBackground", table.get("textHighlight"),
	    "FormattedTextField.selectionForeground", table.get("textHighlightText"),
	    "FormattedTextField.caretForeground", table.get("textText"),
	    "FormattedTextField.caretBlinkRate", caretBlinkRate,
	    "FormattedTextField.border", textFieldBorder,
            "FormattedTextField.margin", zeroInsets,
	    "FormattedTextField.focusInputMap",
              new UIDefaults.LazyInputMap(new Object[] {
                           "ctrl C", DefaultEditorKit.copyAction,
                           "ctrl V", DefaultEditorKit.pasteAction,
                           "ctrl X", DefaultEditorKit.cutAction,
                             "COPY", DefaultEditorKit.copyAction,
                            "PASTE", DefaultEditorKit.pasteAction,
                              "CUT", DefaultEditorKit.cutAction,
                       "shift LEFT", DefaultEditorKit.selectionBackwardAction,
                    "shift KP_LEFT", DefaultEditorKit.selectionBackwardAction,
                      "shift RIGHT", DefaultEditorKit.selectionForwardAction,
                   "shift KP_RIGHT", DefaultEditorKit.selectionForwardAction,
                        "ctrl LEFT", DefaultEditorKit.previousWordAction,
                     "ctrl KP_LEFT", DefaultEditorKit.previousWordAction,
                       "ctrl RIGHT", DefaultEditorKit.nextWordAction,
                    "ctrl KP_RIGHT", DefaultEditorKit.nextWordAction,
                  "ctrl shift LEFT", DefaultEditorKit.selectionPreviousWordAction,
               "ctrl shift KP_LEFT", DefaultEditorKit.selectionPreviousWordAction,
                 "ctrl shift RIGHT", DefaultEditorKit.selectionNextWordAction,
              "ctrl shift KP_RIGHT", DefaultEditorKit.selectionNextWordAction,
                           "ctrl A", DefaultEditorKit.selectAllAction,
                             "HOME", DefaultEditorKit.beginLineAction,
                              "END", DefaultEditorKit.endLineAction,
                       "shift HOME", DefaultEditorKit.selectionBeginLineAction,
                        "shift END", DefaultEditorKit.selectionEndLineAction,
                       "typed \010", DefaultEditorKit.deletePrevCharAction,
                           "DELETE", DefaultEditorKit.deleteNextCharAction,
                            "RIGHT", DefaultEditorKit.forwardAction,
                             "LEFT", DefaultEditorKit.backwardAction,
                         "KP_RIGHT", DefaultEditorKit.forwardAction,
                          "KP_LEFT", DefaultEditorKit.backwardAction,
                            "ENTER", JTextField.notifyAction,
                  "ctrl BACK_SLASH", "unselect",
                  "control shift O", "toggle-componentOrientation",
                           "ESCAPE", "reset-field-edit",
                               "UP", "increment",
                            "KP_UP", "increment",
                             "DOWN", "decrement",
                          "KP_DOWN", "decrement",
              }),

	    "PasswordField.font", monospacedPlain12,
	    "PasswordField.background", table.get("window"),
	    "PasswordField.foreground", table.get("textText"),
	    "PasswordField.inactiveForeground", table.get("textInactiveText"),
	    "PasswordField.inactiveBackground", table.get("control"),
	    "PasswordField.selectionBackground", table.get("textHighlight"),
	    "PasswordField.selectionForeground", table.get("textHighlightText"),
	    "PasswordField.caretForeground", table.get("textText"),
	    "PasswordField.caretBlinkRate", caretBlinkRate,
	    "PasswordField.border", textFieldBorder,
            "PasswordField.margin", zeroInsets,

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

	    "TextPane.font", serifPlain12,
	    "TextPane.background", white,
	    "TextPane.foreground", table.get("textText"),
	    "TextPane.selectionBackground", table.get("textHighlight"),
	    "TextPane.selectionForeground", table.get("textHighlightText"),
	    "TextPane.caretForeground", table.get("textText"),
	    "TextPane.caretBlinkRate", caretBlinkRate,
	    "TextPane.inactiveForeground", table.get("textInactiveText"),
	    "TextPane.border", marginBorder,
            "TextPane.margin", editorMargin,

	    "EditorPane.font", serifPlain12,
	    "EditorPane.background", white,
	    "EditorPane.foreground", table.get("textText"),
	    "EditorPane.selectionBackground", table.get("textHighlight"),
	    "EditorPane.selectionForeground", table.get("textHighlightText"),
	    "EditorPane.caretForeground", table.get("textText"),
	    "EditorPane.caretBlinkRate", caretBlinkRate,
	    "EditorPane.inactiveForeground", table.get("textInactiveText"),
	    "EditorPane.border", marginBorder,
            "EditorPane.margin", editorMargin,

	    // *** TitledBorder
            "TitledBorder.font", dialogPlain12,
            "TitledBorder.titleColor", table.get("controlText"),
            "TitledBorder.border", etchedBorder,

	    // *** ToolBar
	    "ToolBar.font", dialogPlain12,
	    "ToolBar.background", table.get("control"),
	    "ToolBar.foreground", table.get("controlText"),
	    "ToolBar.shadow", table.getColor("controlShadow"),
            "ToolBar.darkShadow", table.getColor("controlDkShadow"),
            "ToolBar.light", table.getColor("controlHighlight"),
            "ToolBar.highlight", table.getColor("controlLtHighlight"),
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
            // ToolTips also support backgroundInactive, borderInactive,
            // and foregroundInactive

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
                                 "ctrl C", "copy",
                                 "ctrl V", "paste",
                                 "ctrl X", "cut",
                                   "COPY", "copy",
                                  "PASTE", "paste",
                                    "CUT", "cut",
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
	    "Tree.focusInputMap.RightToLeft",
	       new UIDefaults.LazyInputMap(new Object[] {
		                  "RIGHT", "selectParent",
		               "KP_RIGHT", "selectParent",
		                   "LEFT", "selectChild",
		                "KP_LEFT", "selectChild",
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

    // ********* Auditory Cue support methods and objects *********
    // also see the "AuditoryCues" section of the defaults table

    /**
     * Returns an <code>ActionMap</code>.
     * <P>
     * This <code>ActionMap</code> contains <code>Actions</code> that 
     * embody the ability to render an auditory cue. These auditory 
     * cues map onto user and system activities that may be useful 
     * for an end user to know about (such as a dialog box appearing).
     * <P>
     * At the appropriate time in a <code>JComponent</code> UI's lifecycle, 
     * the ComponentUI is responsible for getting the appropriate 
     * <code>Action</code> out of the <code>ActionMap</code> and passing 
     * it on to <code>playSound</code>.
     * <P>
     * The <code>Actions</code> in this <code>ActionMap</code> are 
     * created by the <code>createAudioAction</code> method.
     *
     * @return      an ActionMap containing Actions
     *              responsible for rendering auditory cues
     * @see #createAudioAction
     * @see #playSound(Action)
     * @since 1.4
     */
    protected ActionMap getAudioActionMap() {
	ActionMap audioActionMap = (ActionMap)UIManager.get(
					      "AuditoryCues.actionMap");
	if (audioActionMap == null) {
	    Object[] acList = (Object[])UIManager.get("AuditoryCues.cueList");
	    if (acList != null) {
		audioActionMap = new ActionMapUIResource();
		for(int counter = acList.length-1; counter >= 0; counter--) {
		    audioActionMap.put(acList[counter],
				       createAudioAction(acList[counter]));
		}
	    }
	    UIManager.getLookAndFeelDefaults().put("AuditoryCues.actionMap",
						   audioActionMap);
	}
	return audioActionMap;
    }

    /**
     * Returns an <code>Action</code>.
     * <P>
     * This Action contains the information and logic to render an
     * auditory cue. The <code>Object</code> that is passed to this
     * method contains the information needed to render the auditory 
     * cue. Normally, this <code>Object</code> is a <code>String</code> 
     * that points to an audio file relative to the current package.
     * This <code>Action</code>'s <code>actionPerformed</code> method
     * is fired by the <code>playSound</code> method.
     *
     * @return      an Action which knows how to render the auditory
     *              cue for one particular system or user activity
     * @see #playSound(Action)
     * @since 1.4
     */
    protected Action createAudioAction(Object key) {
	if (key != null) {
	    String audioKey = (String)key;
	    String audioValue = (String)UIManager.get(key);
	    return new AudioAction(audioKey, audioValue);
	} else {
	    return null;
	}
    }

    /**
     * Pass the name String to the super constructor. This is used 
     * later to identify the Action and decide whether to play it or 
     * not. Store the resource String. I is used to get the audio 
     * resource. In this case, the resource is an audio file.
     *
     * @since 1.4
     */
    private class AudioAction extends AbstractAction implements LineListener {
        // We strive to only play one sound at a time (other platforms
        // appear to do this). This is done by maintaining the field
        // clipPlaying. Every time a sound is to be played,
        // cancelCurrentSound is invoked to cancel any sound that may be
        // playing.
	private String audioResource;
	private byte[] audioBuffer;

	/**
	 * The String is the name of the Action and
	 * points to the audio resource.
	 * The byte[] is a buffer of the audio bits.
	 */
	public AudioAction(String name, String resource) {
	    super(name);
	    audioResource = resource;
	}

	public void actionPerformed(ActionEvent e) {
	    if (audioBuffer == null) {
		audioBuffer = loadAudioData(audioResource);
	    }
	    if (audioBuffer != null) {
                cancelCurrentSound(null);
		try {
		    AudioInputStream soundStream =
			AudioSystem.getAudioInputStream(
			    new ByteArrayInputStream(audioBuffer));
		    DataLine.Info info =
			new DataLine.Info(Clip.class, soundStream.getFormat());
		    Clip clip = (Clip) AudioSystem.getLine(info);
		    clip.open(soundStream);
                    clip.addLineListener(this);

                    synchronized(audioLock) {
                        clipPlaying = clip;
                    }

		    clip.start();
		} catch (Exception ex) {}
	    }
	}

        public void update(LineEvent event) {
            if (event.getType() == LineEvent.Type.STOP) {
                cancelCurrentSound((Clip)event.getLine());
            }
        }

        /**
         * If the parameter is null, or equal to the currently
         * playing sound, then cancel the currently playing sound.
         */
        private void cancelCurrentSound(Clip clip) {
            Clip lastClip = null;

            synchronized(audioLock) {
                if (clip == null || clip == clipPlaying) {
                    lastClip = clipPlaying;
                    clipPlaying = null;
                }
            }

            if (lastClip != null) {
                lastClip.removeLineListener(this);
                lastClip.close();
            }
        }
    }

    /**
     * Utility method that helps get permission for loading audio files.
     * This is an exact copy of
     * <code>javax.swing.SwingUtilities#doPrivileged(final Runnable)</code>.
     * The SwingUtilities version is unusable here as it is package private.
     *
     * @param doRun     the object that needs special access, in this case,
     *                  to the file system
     * @see             javax.swing.SwingUtilities#doPrivileged(final Runnable)
     * @since 1.4
     */
    private final static void doPrivileged(final Runnable doRun) {
        java.security.AccessController.doPrivileged(
            new java.security.PrivilegedAction() {
                public Object run() {
                  doRun.run();
                  return null;
                }
            }
        );
    }

    /**
     * Utility method that loads audio bits for the specified 
     * <code>soundFile</code> filename. If this method is unable to
     * build a viable path name from the <code>baseClass</code> and 
     * <code>soundFile</code> passed into this method, it will 
     * return <code>null</code>.
     *
     * @param baseClass    used as the root class/location to get the
     *                     soundFile from
     * @param soundFile    the name of the audio file to be retrieved 
     *                     from disk
     * @return             A byte[] with audio data or null
     * @since 1.4
     */
    private byte[] loadAudioData(final String soundFile){
	if (soundFile == null) {
	    return null;
	}
	/* Copy resource into a byte array.  This is
	 * necessary because several browsers consider
	 * Class.getResource a security risk since it
	 * can be used to load additional classes.
	 * Class.getResourceAsStream just returns raw
	 * bytes, which we can convert to a sound.
	 */
	final byte[][] buffer = new byte[1][];
	doPrivileged(new Runnable() {
		public void run() {
		    try {
			InputStream resource = BasicLookAndFeel.this.
			    getClass().getResourceAsStream(soundFile);
			if (resource == null) {
			    return; 
			}
			BufferedInputStream in = 
			    new BufferedInputStream(resource);
			ByteArrayOutputStream out = 
			    new ByteArrayOutputStream(1024);
			buffer[0] = new byte[1024];
			int n;
			while ((n = in.read(buffer[0])) > 0) {
			    out.write(buffer[0], 0, n);
			}
			in.close();
			out.flush();
			buffer[0] = out.toByteArray();
		    } catch (IOException ioe) {
			System.err.println(ioe.toString());
			return;
		    }
		}
	    });
	if (buffer[0] == null) {
	    System.err.println(getClass().getName() + "/" + 
			       soundFile + " not found.");
	    return null;
	}
	if (buffer[0].length == 0) {
	    System.err.println("warning: " + soundFile + 
			       " is zero-length");
	    return null;
	}
	return buffer[0];
    }

    /**
     * Decides whether to fire the <code>Action</code> that is passed into
     * it and, if needed, fires the <code>Action</code>'s 
     * <code>actionPerformed</code> method. This has the effect
     * of rendering the audio appropriate for the situation.
     * <P>
     * The set of possible cues to be played are stored in the default 
     * table value "AuditoryCues.cueList". The cues that will be played
     * are stored in "AuditoryCues.playList".
     *
     * @param audioAction an Action that knows how to render the audio
     *                    associated with the system or user activity
     *                    that is occurring
     * @since 1.4
     */
    protected void playSound(Action audioAction) {
	if (audioAction != null) {
	    Object[] audioStrings = (Object[])
		                    UIManager.get("AuditoryCues.playList");
	    if (audioStrings != null) {
		// create a HashSet to help us decide to play or not
		HashSet audioCues = new HashSet();
		for (int i = 0; i < audioStrings.length; i++) {
		    audioCues.add(audioStrings[i]);
		}
		// get the name of the Action
		String actionName = (String)audioAction.getValue(Action.NAME);
		// if the actionName is in the audioCues HashSet, play it.
		if (audioCues.contains(actionName)) {
		    audioAction.actionPerformed(new 
			ActionEvent(this, ActionEvent.ACTION_PERFORMED, 
				    actionName));
		}
	    }
	}
    }

    // At this point we need this method here. But we assume that there
    // will be a common method for this purpose in the future releases.
    static void compositeRequestFocus(Component component) {
 	if (component instanceof Container) {
 	    Container container = (Container)component;
 	    if (container.isFocusCycleRoot()) {
 		FocusTraversalPolicy policy = container.getFocusTraversalPolicy();
 		Component comp = policy.getDefaultComponent(container);
 		if (comp!=null) {
 		    comp.requestFocus();
 		    return;
 		}
 	    }
 	    Container rootAncestor = container.getFocusCycleRootAncestor();
 	    if (rootAncestor!=null) {
 		FocusTraversalPolicy policy = rootAncestor.getFocusTraversalPolicy();
 		Component comp = policy.getComponentAfter(rootAncestor, container);
 		
 		if (comp!=null && SwingUtilities.isDescendingFrom(comp, container)) {
 		    comp.requestFocus();
 		    return;
 		}
 	    }
 	}
 	component.requestFocus();
    }
    
}
