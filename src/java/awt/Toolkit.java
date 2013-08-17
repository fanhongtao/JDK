/*
 * @(#)Toolkit.java	1.74 98/12/09
 *
 * Copyright 1995-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.awt;

import java.util.Properties;
import java.awt.peer.*;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.ColorModel;
import java.awt.datatransfer.Clipboard;
import java.net.URL;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * This class is the abstract superclass of all actual
 * implementations of the Abstract Window Toolkit. Subclasses of 
 * <code>Toolkit</code> are used to bind the various components 
 * to particular native toolkit implementations.
 * <p>
 * Most applications should not call any of the methods in this
 * class directly. The methods defined by <code>Toolkit</code> are 
 * the "glue" that joins the platform-independent classes in the 
 * <code>java.awt</code> package with their counterparts in 
 * <code>java.awt.peer</code>. Some methods defined by 
 * <code>Toolkit</code> query the native operating system directly.
 *
 * @version 	1.74, 12/09/98
 * @author	Sami Shaio
 * @author	Arthur van Hoff
 * @since       JDK1.0
 */
public abstract class  Toolkit {

    /**
     * Creates this toolkit's implementation of <code>Button</code> using 
     * the specified peer interface.
     * @param     target the button to be implemented.
     * @return    this toolkit's implementation of <code>Button</code>.
     * @see       java.awt.Button
     * @see       java.awt.peer.ButtonPeer
     * @since     JDK1.0
     */
    protected abstract ButtonPeer 	createButton(Button target);

    /**
     * Creates this toolkit's implementation of <code>TextField</code> using 
     * the specified peer interface.
     * @param     target the text field to be implemented.
     * @return    this toolkit's implementation of <code>TextField</code>.
     * @see       java.awt.TextField
     * @see       java.awt.peer.TextFieldPeer
     * @since     JDK1.0
     */
    protected abstract TextFieldPeer 	createTextField(TextField target);

    /**
     * Creates this toolkit's implementation of <code>Label</code> using 
     * the specified peer interface.
     * @param     target the label to be implemented.
     * @return    this toolkit's implementation of <code>Label</code>.
     * @see       java.awt.Label
     * @see       java.awt.peer.LabelPeer
     * @since     JDK1.0
     */
    protected abstract LabelPeer 	createLabel(Label target);

    /**
     * Creates this toolkit's implementation of <code>List</code> using 
     * the specified peer interface.
     * @param     target the list to be implemented.
     * @return    this toolkit's implementation of <code>List</code>.
     * @see       java.awt.List
     * @see       java.awt.peer.ListPeer
     * @since     JDK1.0
     */
    protected abstract ListPeer 	createList(List target);

    /**
     * Creates this toolkit's implementation of <code>Checkbox</code> using 
     * the specified peer interface.
     * @param     target the check box to be implemented.
     * @return    this toolkit's implementation of <code>Checkbox</code>.
     * @see       java.awt.Checkbox
     * @see       java.awt.peer.CheckboxPeer
     * @since     JDK1.0
     */
    protected abstract CheckboxPeer 	createCheckbox(Checkbox target);

    /**
     * Creates this toolkit's implementation of <code>Scrollbar</code> using 
     * the specified peer interface.
     * @param     target the scroll bar to be implemented.
     * @return    this toolkit's implementation of <code>Scrollbar</code>.
     * @see       java.awt.Scrollbar
     * @see       java.awt.peer.ScrollbarPeer
     * @since     JDK1.0
     */
    protected abstract ScrollbarPeer 	createScrollbar(Scrollbar target);

    /**
     * Creates this toolkit's implementation of <code>ScrollPane</code> using 
     * the specified peer interface.
     * @param     target the scroll pane to be implemented.
     * @return    this toolkit's implementation of <code>ScrollPane</code>.
     * @see       java.awt.ScrollPane
     * @see       java.awt.peer.ScrollPanePeer
     * @since     JDK1.1
     */
    protected abstract ScrollPanePeer     createScrollPane(ScrollPane target);

    /**
     * Creates this toolkit's implementation of <code>TextArea</code> using 
     * the specified peer interface.
     * @param     target the text area to be implemented.
     * @return    this toolkit's implementation of <code>TextArea</code>.
     * @see       java.awt.TextArea
     * @see       java.awt.peer.TextAreaPeer
     * @since     JDK1.0
     */
    protected abstract TextAreaPeer  	createTextArea(TextArea target);

    /**
     * Creates this toolkit's implementation of <code>Choice</code> using 
     * the specified peer interface.
     * @param     target the choice to be implemented.
     * @return    this toolkit's implementation of <code>Choice</code>.
     * @see       java.awt.Choice
     * @see       java.awt.peer.ChoicePeer
     * @since     JDK1.0
     */
    protected abstract ChoicePeer	createChoice(Choice target);

    /**
     * Creates this toolkit's implementation of <code>Frame</code> using 
     * the specified peer interface.
     * @param     target the frame to be implemented.
     * @return    this toolkit's implementation of <code>Frame</code>.
     * @see       java.awt.Frame
     * @see       java.awt.peer.FramePeer
     * @since     JDK1.0
     */
    protected abstract FramePeer  	createFrame(Frame target);

    /**
     * Creates this toolkit's implementation of <code>Canvas</code> using 
     * the specified peer interface.
     * @param     target the canvas to be implemented.
     * @return    this toolkit's implementation of <code>Canvas</code>.
     * @see       java.awt.Canvas
     * @see       java.awt.peer.CanvasPeer
     * @since     JDK1.0
     */
    protected abstract CanvasPeer 	createCanvas(Canvas target);

    /**
     * Creates this toolkit's implementation of <code>Panel</code> using 
     * the specified peer interface.
     * @param     target the panel to be implemented.
     * @return    this toolkit's implementation of <code>Panel</code>.
     * @see       java.awt.Panel
     * @see       java.awt.peer.PanelPeer
     * @since     JDK1.0
     */
    protected abstract PanelPeer  	createPanel(Panel target);

    /**
     * Creates this toolkit's implementation of <code>Window</code> using 
     * the specified peer interface.
     * @param     target the window to be implemented.
     * @return    this toolkit's implementation of <code>Window</code>.
     * @see       java.awt.Window
     * @see       java.awt.peer.WindowPeer
     * @since     JDK1.0
     */
    protected abstract WindowPeer  	createWindow(Window target);

    /**
     * Creates this toolkit's implementation of <code>Dialog</code> using 
     * the specified peer interface.
     * @param     target the dialog to be implemented.
     * @return    this toolkit's implementation of <code>Dialog</code>.
     * @see       java.awt.Dialog
     * @see       java.awt.peer.DialogPeer
     * @since     JDK1.0
     */
    protected abstract DialogPeer  	createDialog(Dialog target);

    /**
     * Creates this toolkit's implementation of <code>MenuBar</code> using 
     * the specified peer interface.
     * @param     target the menu bar to be implemented.
     * @return    this toolkit's implementation of <code>MenuBar</code>.
     * @see       java.awt.MenuBar
     * @see       java.awt.peer.MenuBarPeer
     * @since     JDK1.0
     */
    protected abstract MenuBarPeer  	createMenuBar(MenuBar target);

    /**
     * Creates this toolkit's implementation of <code>Menu</code> using 
     * the specified peer interface.
     * @param     target the menu to be implemented.
     * @return    this toolkit's implementation of <code>Menu</code>.
     * @see       java.awt.Menu
     * @see       java.awt.peer.MenuPeer
     * @since     JDK1.0
     */
    protected abstract MenuPeer  	createMenu(Menu target);

    /**
     * Creates this toolkit's implementation of <code>PopupMenu</code> using 
     * the specified peer interface.
     * @param     target the popup menu to be implemented.
     * @return    this toolkit's implementation of <code>PopupMenu</code>.
     * @see       java.awt.PopupMenu
     * @see       java.awt.peer.PopupMenuPeer
     * @since     JDK1.1
     */
    protected abstract PopupMenuPeer	createPopupMenu(PopupMenu target);

    /**
     * Creates this toolkit's implementation of <code>MenuItem</code> using 
     * the specified peer interface.
     * @param     target the menu item to be implemented.
     * @return    this toolkit's implementation of <code>MenuItem</code>.
     * @see       java.awt.MenuItem
     * @see       java.awt.peer.MenuItemPeer
     * @since     JDK1.0
     */
    protected abstract MenuItemPeer  	createMenuItem(MenuItem target);

    /**
     * Creates this toolkit's implementation of <code>FileDialog</code> using 
     * the specified peer interface.
     * @param     target the file dialog to be implemented.
     * @return    this toolkit's implementation of <code>FileDialog</code>.
     * @see       java.awt.FileDialog
     * @see       java.awt.peer.FileDialogPeer
     * @since     JDK1.0
     */
    protected abstract FileDialogPeer	createFileDialog(FileDialog target);

    /**
     * Creates this toolkit's implementation of <code>CheckboxMenuItem</code> using 
     * the specified peer interface.
     * @param     target the checkbox menu item to be implemented.
     * @return    this toolkit's implementation of <code>CheckboxMenuItem</code>.
     * @see       java.awt.CheckboxMenuItem
     * @see       java.awt.peer.CheckboxMenuItemPeer
     * @since     JDK1.0
     */
    protected abstract CheckboxMenuItemPeer	createCheckboxMenuItem(CheckboxMenuItem target);

    /**
     * Creates a peer for a component or container.  This peer is windowless
     * and allows the Component and Container classes to be extended directly
     * to create windowless components that are defined entirely in java.
     *
     * @param target The Component to be created.
     */
    protected java.awt.peer.LightweightPeer createComponent(Component target) {
	return new java.awt.LightweightPeer(target);
    }

    /**
     * Creates this toolkit's implementation of <code>Font</code> using 
     * the specified peer interface.
     * @param     target the font to be implemented.
     * @return    this toolkit's implementation of <code>Font</code>.
     * @see       java.awt.Font
     * @see       java.awt.peer.FontPeer
     * @since     JDK1.0
     */
    protected abstract FontPeer getFontPeer(String name, int style);

    /**
     * Fills in the integer array that is supplied as an argument 
     * with the current system color values.
     * <p>
     * This method is called by the method <code>updateSystemColors</code>
     * in the <code>SystemColor</code> class.
     * @param     an integer array.
     * @see       java.awt.SystemColor#updateSystemColors
     * @since     JDK1.1
     */
    protected void loadSystemColors(int[] systemColors) {
    }

    /**
     * Gets the size of the screen.
     * @return    the size of this toolkit's screen, in pixels.
     * @since     JDK1.0
     */
    public abstract Dimension getScreenSize();

    /**
     * Returns the screen resolution in dots-per-inch.
     * @return    this toolkit's screen resolution, in dots-per-inch.
     * @since     JDK1.0
     */
    public abstract int getScreenResolution();

    /**
     * Determines the color model of this toolkit's screen. 
     * <p>
     * <code>ColorModel</code> is an abstract class that 
     * encapsulates the ability to translate between the 
     * pixel values of an image and its red, green, blue, 
     * and alpha components. 
     * <p>
     * This toolkit method is called by the 
     * <code>getColorModel</code> method 
     * of the <code>Component</code> class. 
     * @return    the color model of this toolkit's screen.
     * @see       java.awt.image.ColorModel
     * @see       java.awt.Component#getColorModel
     * @since     JDK1.0
     */
    public abstract ColorModel getColorModel();

    /**
     * Returns the names of the available fonts in this toolkit.<p>
     * For 1.1, the following font names are deprecated (the replacement
     * name follows):
     * <ul>
     * <li>TimesRoman (use Serif)
     * <li>Helvetica (use SansSerif)
     * <li>Courier (use Monospaced)
     * </ul><p>
     * The ZapfDingbats font is also deprecated in 1.1, but only as a
     * separate fontname.  Unicode defines the ZapfDingbat characters
     * starting at \u2700, and as of 1.1 Java supports those characters.
     * @return    the names of the available fonts in this toolkit.
     * @since     JDK1.0
     */
    public abstract String[] getFontList();

    /**
     * Gets the screen metrics of the font.
     * @param     font   a font.
     * @return    the screen metrics of the specified font in this toolkit.
     * @since     JDK1.0
     */
    public abstract FontMetrics getFontMetrics(Font font);

    /**
     * Synchronizes this toolkit's graphics state. Some window systems 
     * may do buffering of graphics events. 
     * <p>
     * This method ensures that the display is up-to-date. It is useful
     * for animation.
     * @since     JDK1.0
     */
    public abstract void sync();

    /**
     * The default toolkit.
     */
    private static Toolkit toolkit;

    // fix for 4187686 Several class objects are used for synchronization
    private static Object classLock = new Object();

    /**
     * Gets the default toolkit. 
     * <p>
     * If there is a system property named <code>"awt.toolkit"</code>, 
     * that property is treated as the name of a class that is a subclass 
     * of <code>Toolkit</code>. 
     * <p>
     * If the system property does not exist, then the default toolkit 
     * used is the class named <code>"sun.awt.motif.MToolkit"</code>, 
     * which is a motif implementation of the Abstract Window Toolkit. 
     * @return    the default toolkit.
     * @exception  AWTError  if a toolkit could not be found, or 
     *                 if one could not be accessed or instantiated.
     * @since     JDK1.0
     */
    public static Toolkit getDefaultToolkit() {
	// fix for 4187686 Several class objects are used for synchronization
	synchronized (classLock) {
	    if (toolkit == null) {
		String nm = System.getProperty("awt.toolkit", "sun.awt.motif.MToolkit");
		try {
		    toolkit = (Toolkit)Class.forName(nm).newInstance();
		} catch (ClassNotFoundException e) {
		    throw new AWTError("Toolkit not found: " + nm);
		} catch (InstantiationException e) {
		    throw new AWTError("Could not instantiate Toolkit: " + nm);
		} catch (IllegalAccessException e) {
		    throw new AWTError("Could not access Toolkit: " + nm);
		}
	    }
	    return toolkit; 
	}
    }

    /**
     * Returns an image which gets pixel data from the specified file.
     * @param     filename   the name of a file containing pixel data 
     *                         in a recognized file format.
     * @return    an image which gets its pixel data from 
     *                         the specified file.
     * @since     JDK1.0
     */
    public abstract Image getImage(String filename);

    /**
     * Returns an image which gets pixel data from the specified URL.
     * @param     url   the URL to use in fetching the pixel data.
     * @return    an image which gets its pixel data from 
     *                         the specified URL.
     * @since     JDK1.0
     */
    public abstract Image getImage(URL url);

    /**
     * Prepares an image for rendering. 
     * <p>
     * If the values of the width and height arguments are both 
     * <code>-1</code>, this method prepares the image for rendering 
     * on the default screen; otherwise, this method prepares an image 
     * for rendering on the default screen at the specified width and height. 
     * <p>
     * The image data is downloaded asynchronously in another thread, 
     * and an appropriately scaled screen representation of the image is 
     * generated. 
     * <p>
     * This method is called by components <code>prepareImage</code> 
     * methods. 
     * <p>
     * Information on the flags returned by this method can be found 
     * with the definition of the <code>ImageObserver</code> interface. 

     * @param     image      the image for which to prepare a  
     *                           screen representation.
     * @param     width      the width of the desired screen 
     *                           representation, or <code>-1</code>.
     * @param     height     the height of the desired screen 
     *                           representation, or <code>-1</code>.
     * @param     observer   the <code>ImageObserver</code> 
     *                           object to be notified as the 
     *                           image is being prepared.
     * @return    <code>true</code> if the image has already been 
     *                 fully prepared; <code>false</code> otherwise.
     * @see       java.awt.Component#prepareImage(java.awt.Image, 
     *                 java.awt.image.ImageObserver)
     * @see       java.awt.Component#prepareImage(java.awt.Image, 
     *                 int, int, java.awt.image.ImageObserver)
     * @see       java.awt.image.ImageObserver
     * @since     JDK1.0
     */
    public abstract boolean prepareImage(Image image, int width, int height,
					 ImageObserver observer);

    /**
     * Indicates the construction status of a specified image that is
     * being prepared for display.
     * <p>
     * If the values of the width and height arguments are both 
     * <code>-1</code>, this method returns the construction status of 
     * a screen representation of the specified image in this toolkit. 
     * Otherwise, this method returns the construction status of a
     * scaled representation of the image at the specified width 
     * and height.
     * <p>
     * This method does not cause the image to begin loading. 
     * An application must call <code>prepareImage</code> to force 
     * the loading of an image.
     * <p>
     * This method is called by the component's <code>checkImage</code>
     * methods.
     * <p>
     * Information on the flags returned by this method can be found
     * with the definition of the <code>ImageObserver</code> interface.
     * @param     image   the image whose status is being checked.
     * @param     width   the width of the scaled version whose status is
     *                 being checked, or <code>-1</code>.
     * @param     height  the height of the scaled version whose status
     *                 is being checked, or <code>-1</code>.
     * @param     observer   the <code>ImageObserver</code> object to be
     *                 notified as the image is being prepared.
     * @return    the bitwise inclusive <strong>OR</strong> of the
     *                 <code>ImageObserver</code> flags for the 
     *                 image data that is currently available.
     * @see       java.awt.Toolkit#prepareImage(java.awt.Image, 
     *                 int, int, java.awt.image.ImageObserver)
     * @see       java.awt.Component#checkImage(java.awt.Image, 
     *                 java.awt.image.ImageObserver)
     * @see       java.awt.Component#checkImage(java.awt.Image, 
     *                 int, int, java.awt.image.ImageObserver)
     * @see       java.awt.image.ImageObserver
     * @since     JDK1.0
     */
    public abstract int checkImage(Image image, int width, int height,
				   ImageObserver observer);

    /**
     * Creates an image with the specified image producer.
     * @param     producer the image producer to be used.
     * @return    an image with the specified image producer.
     * @see       java.awt.Image
     * @see       java.awt.image.ImageProducer
     * @see       java.awt.Component#createImage(java.awt.image.ImageProducer)
     * @since     JDK1.0
     */
    public abstract Image createImage(ImageProducer producer);

    /**
     * Creates an image which decodes the image stored in the specified
     * byte array.
     * <p>
     * The data must be in some image format, such as GIF or JPEG, 
     * that is supported by this toolkit.
     * @param     imagedata   an array of bytes, representing 
     *                         image data in a supported image format.
     * @return    an image.
     * @since     JDK1.1
     */
    public Image createImage(byte[] imagedata) {
	return createImage(imagedata, 0, imagedata.length);
    }

    /**
     * Creates an image which decodes the image stored in the specified
     * byte array, and at the specified offset and length.
     * The data must be in some image format, such as GIF or JPEG, 
     * that is supported by this toolkit. 
     * @param     imagedata   an array of bytes, representing 
     *                         image data in a supported image format.
     * @param     imageoffset  the offset of the beginning 
     *                         of the data in the array.
     * @param     imagelength  the length of the data in the array.
     * @return    an image.
     * @since     JDK1.1
     */
    public abstract Image createImage(byte[] imagedata,
				      int imageoffset,
				      int imagelength);

    /**
     * Gets a <code>PrintJob</code> object which is the result 
     * of initiating a print operation on the toolkit's platform. 
     * @return    a <code>PrintJob</code> object, or 
     *                  <code>null</code> if the user 
     *                  cancelled the print job.
     * @see       java.awt.PrintJob
     * @since     JDK1.1
     */
    public abstract PrintJob getPrintJob(Frame frame, String jobtitle, Properties props);

    /**
     * Emits an audio beep.
     * @since     JDK1.1
     */
    public abstract void beep();

    /**
     * Gets an instance of the system clipboard which interfaces 
     * with clipboard facilities provided by the native platform. 
     * <p>
     * This clipboard enables data transfer between Java programs 
     * and native applications which use native clipboard facilities.
     * @return    an instance of the system clipboard.
     * @see       java.awt.datatransfer.Clipboard
     * @since     JDK1.1
     */
    public abstract Clipboard getSystemClipboard();

    /**
     * Determines which modifier key is the appropriate accelerator
     * key for menu shortcuts.
     * <p>
     * Menu shortcuts, which are embodied in the 
     * <code>MenuShortcut</code> class, are handled by the 
     * <code>MenuBar</code> class.
     * <p>
     * By default, this method returns <code>Event.CTRL_MASK</code>.
     * Toolkit implementations should override this method if the
     * <b>Control</b> key isn't the correct key for accelerators.
     * @return    the modifier mask on the <code>Event</code> class 
     *                 that is used for menu shortcuts on this toolkit. 
     * @see       java.awt.MenuBar
     * @see       java.awt.MenuShortcut
     * @since     JDK1.1
     */
    public int getMenuShortcutKeyMask() {
        return Event.CTRL_MASK;
    }

    /**
     * Give native peers the ability to query the native container 
     * given a native component (eg the direct parent may be lightweight).
     */
    protected static Container getNativeContainer(Component c) {
	return c.getNativeContainer();
    }

    /* Support for I18N: any visible strings should be stored in 
     * lib/awt.properties.  The Properties list is stored here, so
     * that only one copy is maintained.
     */
    private static Properties properties;
    static {
        String sep = File.separator;
        File propsFile = new File(
            System.getProperty("java.home") + sep + "lib" +
            sep + "awt.properties");
        properties = new Properties();
	try {
	    FileInputStream in =
		new FileInputStream(propsFile);
	    properties.load(new BufferedInputStream(in));
	    in.close();
	} catch (Exception e) {
            // No properties, defaults will be used.
	}
    }

    /**
     * Gets a property with the specified key and default. 
     * This method returns defaultValue if the property is not found.
     */
    public static String getProperty(String key, String defaultValue) {
	String val = properties.getProperty(key);
	return (val == null) ? defaultValue : val;
    }

    /**
     * Get the application's or applet's EventQueue instance.  
     * Depending on the Toolkit implementation, different EventQueues 
     * may be returned for different applets.  Applets should 
     * therefore not assume that the EventQueue instance returned
     * by this method will be shared by other applets or the system.
     */
    public final EventQueue getSystemEventQueue() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
	  security.checkAwtEventQueueAccess();
        }
        return getSystemEventQueueImpl();
    }

    /*
     * Get the application's or applet's EventQueue instance, without
     * checking access.  For security reasons, this can only be called 
     * from a Toolkit subclass.  Implementations wishing to modify
     * the default EventQueue support should subclass this method.
     */
    protected abstract EventQueue getSystemEventQueueImpl();

    /* Accessor method for use by AWT package routines. */
    static EventQueue getEventQueue() {
        return toolkit.getSystemEventQueueImpl();
    }
}



/**
 * Implements the LightweightPeer interface for use in lightweight components
 * that have no native window associated with them.  This gets created by
 * default in Component so that Component and Container can be directly
 * extended to create useful components written entirely in java.  These 
 * components must be hosted somewhere higher up in the component tree by a 
 * native container (such as a Frame).
 *
 * This implementation provides no useful semantics and serves only as a
 * marker.  One could provide alternative implementations in java that do
 * something useful for some of the other peer interfaces to minimize the
 * native code.
 *
 * @author Timothy Prinzing
 */
class LightweightPeer implements java.awt.peer.LightweightPeer {

    public LightweightPeer(Component target) {
    }

    public boolean isFocusTraversable() {
	return false;
    }

    public void setVisible(boolean b) {
    }

    public void show() {
    }

    public void hide() {
    }

    public void setEnabled(boolean b) {
    }

    public void enable() {
    }

    public void disable() {
    }

    public void paint(Graphics g) {
    }

    public void repaint(long tm, int x, int y, int width, int height) {
    }

    public void print(Graphics g) {
    }

    public void setBounds(int x, int y, int width, int height) {
    }

    public void reshape(int x, int y, int width, int height) {
    }

    public boolean handleEvent(Event e) {
	return false;
    }

    public void handleEvent(java.awt.AWTEvent arg0) {
    }

    public Dimension getPreferredSize() {
	return new Dimension(1,1);
    }

    public Dimension getMinimumSize() {
	return new Dimension(1,1);
    }

    public java.awt.Toolkit getToolkit() {
	return null;
    }

    public ColorModel getColorModel() {
	return null;
    }

    public Graphics getGraphics() {
	return null;
    }

    public FontMetrics	getFontMetrics(Font font) {
	return null;
    }

    public void dispose() {
	// no native code
    }

    public void setForeground(Color c) {
    }

    public void setBackground(Color c) {
    }

    public void setFont(Font f) {
    }

    public void setCursor(Cursor cursor) {
    }

    public void requestFocus() {
    }

    public Image createImage(ImageProducer producer) {
	return null;
    }

    public Image createImage(int width, int height) {
	return null;
    }

    public boolean prepareImage(Image img, int w, int h, ImageObserver o) {
	return false;
    }

    public int	checkImage(Image img, int w, int h, ImageObserver o) {
	return 0;
    }

    public Dimension preferredSize() {
	return getPreferredSize();
    }

    public Dimension minimumSize() {
	return getMinimumSize();
    }

    public Point getLocationOnScreen() {
	return null;
    }
}
