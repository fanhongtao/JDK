/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt;

import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.awt.event.*;
import java.awt.peer.*;
import java.awt.im.InputMethodHighlight;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.ColorModel;
import java.awt.datatransfer.Clipboard;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.net.URL;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;

import java.util.EventListener;
import java.util.Map;
import java.util.HashMap;
import java.util.WeakHashMap;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

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
 * @version 	1.158, 03/20/02
 * @author	Sami Shaio
 * @author	Arthur van Hoff
 * @author	Fred Ecks
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
     */
    protected abstract ButtonPeer 	createButton(Button target);

    /**
     * Creates this toolkit's implementation of <code>TextField</code> using
     * the specified peer interface.
     * @param     target the text field to be implemented.
     * @return    this toolkit's implementation of <code>TextField</code>.
     * @see       java.awt.TextField
     * @see       java.awt.peer.TextFieldPeer
     */
    protected abstract TextFieldPeer 	createTextField(TextField target);

    /**
     * Creates this toolkit's implementation of <code>Label</code> using
     * the specified peer interface.
     * @param     target the label to be implemented.
     * @return    this toolkit's implementation of <code>Label</code>.
     * @see       java.awt.Label
     * @see       java.awt.peer.LabelPeer
     */
    protected abstract LabelPeer 	createLabel(Label target);

    /**
     * Creates this toolkit's implementation of <code>List</code> using
     * the specified peer interface.
     * @param     target the list to be implemented.
     * @return    this toolkit's implementation of <code>List</code>.
     * @see       java.awt.List
     * @see       java.awt.peer.ListPeer
     */
    protected abstract ListPeer 	createList(List target);

    /**
     * Creates this toolkit's implementation of <code>Checkbox</code> using
     * the specified peer interface.
     * @param     target the check box to be implemented.
     * @return    this toolkit's implementation of <code>Checkbox</code>.
     * @see       java.awt.Checkbox
     * @see       java.awt.peer.CheckboxPeer
     */
    protected abstract CheckboxPeer 	createCheckbox(Checkbox target);

    /**
     * Creates this toolkit's implementation of <code>Scrollbar</code> using
     * the specified peer interface.
     * @param     target the scroll bar to be implemented.
     * @return    this toolkit's implementation of <code>Scrollbar</code>.
     * @see       java.awt.Scrollbar
     * @see       java.awt.peer.ScrollbarPeer
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
     */
    protected abstract TextAreaPeer  	createTextArea(TextArea target);

    /**
     * Creates this toolkit's implementation of <code>Choice</code> using
     * the specified peer interface.
     * @param     target the choice to be implemented.
     * @return    this toolkit's implementation of <code>Choice</code>.
     * @see       java.awt.Choice
     * @see       java.awt.peer.ChoicePeer
     */
    protected abstract ChoicePeer	createChoice(Choice target);

    /**
     * Creates this toolkit's implementation of <code>Frame</code> using
     * the specified peer interface.
     * @param     target the frame to be implemented.
     * @return    this toolkit's implementation of <code>Frame</code>.
     * @see       java.awt.Frame
     * @see       java.awt.peer.FramePeer
     */
    protected abstract FramePeer  	createFrame(Frame target);

    /**
     * Creates this toolkit's implementation of <code>Canvas</code> using
     * the specified peer interface.
     * @param     target the canvas to be implemented.
     * @return    this toolkit's implementation of <code>Canvas</code>.
     * @see       java.awt.Canvas
     * @see       java.awt.peer.CanvasPeer
     */
    protected abstract CanvasPeer 	createCanvas(Canvas target);

    /**
     * Creates this toolkit's implementation of <code>Panel</code> using
     * the specified peer interface.
     * @param     target the panel to be implemented.
     * @return    this toolkit's implementation of <code>Panel</code>.
     * @see       java.awt.Panel
     * @see       java.awt.peer.PanelPeer
     */
    protected abstract PanelPeer  	createPanel(Panel target);

    /**
     * Creates this toolkit's implementation of <code>Window</code> using
     * the specified peer interface.
     * @param     target the window to be implemented.
     * @return    this toolkit's implementation of <code>Window</code>.
     * @see       java.awt.Window
     * @see       java.awt.peer.WindowPeer
     */
    protected abstract WindowPeer  	createWindow(Window target);

    /**
     * Creates this toolkit's implementation of <code>Dialog</code> using
     * the specified peer interface.
     * @param     target the dialog to be implemented.
     * @return    this toolkit's implementation of <code>Dialog</code>.
     * @see       java.awt.Dialog
     * @see       java.awt.peer.DialogPeer
     */
    protected abstract DialogPeer  	createDialog(Dialog target);

    /**
     * Creates this toolkit's implementation of <code>MenuBar</code> using
     * the specified peer interface.
     * @param     target the menu bar to be implemented.
     * @return    this toolkit's implementation of <code>MenuBar</code>.
     * @see       java.awt.MenuBar
     * @see       java.awt.peer.MenuBarPeer
     */
    protected abstract MenuBarPeer  	createMenuBar(MenuBar target);

    /**
     * Creates this toolkit's implementation of <code>Menu</code> using
     * the specified peer interface.
     * @param     target the menu to be implemented.
     * @return    this toolkit's implementation of <code>Menu</code>.
     * @see       java.awt.Menu
     * @see       java.awt.peer.MenuPeer
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
     */
    protected abstract MenuItemPeer  	createMenuItem(MenuItem target);

    /**
     * Creates this toolkit's implementation of <code>FileDialog</code> using
     * the specified peer interface.
     * @param     target the file dialog to be implemented.
     * @return    this toolkit's implementation of <code>FileDialog</code>.
     * @see       java.awt.FileDialog
     * @see       java.awt.peer.FileDialogPeer
     */
    protected abstract FileDialogPeer	createFileDialog(FileDialog target);

    /**
     * Creates this toolkit's implementation of <code>CheckboxMenuItem</code> using
     * the specified peer interface.
     * @param     target the checkbox menu item to be implemented.
     * @return    this toolkit's implementation of <code>CheckboxMenuItem</code>.
     * @see       java.awt.CheckboxMenuItem
     * @see       java.awt.peer.CheckboxMenuItemPeer
     */
    protected abstract CheckboxMenuItemPeer	createCheckboxMenuItem(CheckboxMenuItem target);

    private static java.awt.LightweightPeer lightweightMarker;

    /**
     * Creates a peer for a component or container.  This peer is windowless
     * and allows the Component and Container classes to be extended directly
     * to create windowless components that are defined entirely in java.
     *
     * @param target The Component to be created.
     */
    protected java.awt.peer.LightweightPeer createComponent(Component target) {
        if (lightweightMarker == null) {
            lightweightMarker = new java.awt.LightweightPeer(target);
        }
	return lightweightMarker;
    }

    /**
     * Creates this toolkit's implementation of <code>Font</code> using
     * the specified peer interface.
     * @param     target the font to be implemented.
     * @return    this toolkit's implementation of <code>Font</code>.
     * @see       java.awt.Font
     * @see       java.awt.peer.FontPeer
     * @see       java.awt.GraphicsEnvironment#getAllFonts
     * @deprecated  see java.awt.GraphicsEnvironment#getAllFonts
     */
    protected abstract FontPeer getFontPeer(String name, int style);

    // The following method is called by the private method
    // <code>updateSystemColors</code> in <code>SystemColor</code>.

    /**
     * Fills in the integer array that is supplied as an argument
     * with the current system color values.
     *
     * @param     an integer array.
     * @since     JDK1.1
     */
    protected void loadSystemColors(int[] systemColors) {
    }

    /**
     * Gets the size of the screen.
     * @return    the size of this toolkit's screen, in pixels.
     */
    public abstract Dimension getScreenSize();

    /**
     * Returns the screen resolution in dots-per-inch.
     * @return    this toolkit's screen resolution, in dots-per-inch.
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
     * The ZapfDingbats fontname is also deprecated in 1.1 but the characters
     * are defined in Unicode starting at 0x2700, and as of 1.1 Java supports
     * those characters.
     * @return    the names of the available fonts in this toolkit.
     * @deprecated see {@link java.awt.GraphicsEnvironment#getAvailableFontFamilyNames()}
     * @see java.awt.GraphicsEnvironment#getAvailableFontFamilyNames()
     */
    public abstract String[] getFontList();

    /**
     * Gets the screen device metrics for rendering of the font.
     * @param     font   a font.
     * @return    the screen metrics of the specified font in this toolkit.
     * @deprecated  This returns integer metrics for the default screen.
     * @see java.awt.font.LineMetrics
     * @see java.awt.Font#getLineMetrics
     * @see java.awt.GraphicsEnvironment#getScreenDevices
     */
    public abstract FontMetrics getFontMetrics(Font font);

    /**
     * Synchronizes this toolkit's graphics state. Some window systems
     * may do buffering of graphics events.
     * <p>
     * This method ensures that the display is up-to-date. It is useful
     * for animation.
     */
    public abstract void sync();

    /**
     * The default toolkit.
     */
    private static Toolkit toolkit;

    /**
     * Loads additional classes into the VM, using the property
     * 'assistive_technologies' specified in the Sun reference
     * implementation by a line in the 'accessibility.properties'
     * file.  The form is "assistive_technologies=..." where
     * the "..." is a comma-separated list of assistive technology
     * classes to load.  Each class is loaded in the order given
     * and a single instance of each is created using
     * Class.forName(class).newInstance().  All errors are handled
     * via an AWTError exception.
     *
     * <p>The assumption is made that assistive technology classes are supplied
     * as part of INSTALLED (as opposed to: BUNDLED) extensions or specified
     * on the class path
     * (and therefore can be loaded using the class loader returned by
     * a call to <code>ClassLoader.getSystemClassLoader</code>, whose
     * delegation parent is the extension class loader for installed
     * extensions).
     */
    private static void loadAssistiveTechnologies() {
        final String sep = File.separator;
        final Properties properties = new Properties();

	java.security.AccessController.doPrivileged(
	    new java.security.PrivilegedAction() {
	    public Object run() {
		try {
		    File propsFile = new File(
		      System.getProperty("java.home") + sep + "lib" +
		      sep + "accessibility.properties");
		    FileInputStream in =
			new FileInputStream(propsFile);

		    // Inputstream has been buffered in Properties class
		    properties.load(in);
		    in.close();
		} catch (Exception e) {
		    // File does not exist; no classes will be auto loaded
		}

		String magPresent = 
		    System.getProperty("javax.accessibility.screen_magnifier_present");
		if (magPresent == null) {
		    magPresent = 
			properties.getProperty("screen_magnifier_present", 
					       null);
		    if (magPresent != null) {
			System.setProperty("javax.accessibility.screen_magnifier_present",
					   magPresent);
		    }
		}
		return null;
	    }
	});
       
        String atNames = properties.getProperty("assistive_technologies",null);
	ClassLoader cl = ClassLoader.getSystemClassLoader();

        if (atNames != null) {
            StringTokenizer parser = new StringTokenizer(atNames," ,");
	    String atName;
            while (parser.hasMoreTokens()) {
		atName = parser.nextToken();
                try {
		    Class clazz;
		    if (cl != null) {
			clazz = cl.loadClass(atName);
		    } else {
			clazz = Class.forName(atName);
		    }
		    clazz.newInstance();
                } catch (ClassNotFoundException e) {
                    throw new AWTError("Assistive Technology not found: "
			    + atName);
                } catch (InstantiationException e) {
                    throw new AWTError("Could not instantiate Assistive"
			    + " Technology: " + atName);
                } catch (IllegalAccessException e) {
                    throw new AWTError("Could not access Assistive"
			    + " Technology: " + atName);
                } catch (Exception e) {
                    throw new AWTError("Error trying to install Assistive"
			    + " Technology: " + atName + " " + e);
                }
            }
        }
    }

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
     * <p>
     * Also loads additional classes into the VM, using the property
     * 'assistive_technologies' specified in the Sun reference
     * implementation by a line in the 'accessibility.properties'
     * file.  The form is "assistive_technologies=..." where
     * the "..." is a comma-separated list of assistive technology
     * classes to load.  Each class is loaded in the order given
     * and a single instance of each is created using
     * Class.forName(class).newInstance().  This is done just after
     * the AWT toolkit is created.  All errors are handled via an
     * AWTError exception.
     * @return    the default toolkit.
     * @exception  AWTError  if a toolkit could not be found, or
     *                 if one could not be accessed or instantiated.
     */
    public static synchronized Toolkit getDefaultToolkit() {
	if (toolkit == null) {
	    try {
		// We disable the JIT during toolkit initialization.  This
		// tends to touch lots of classes that aren't needed again
		// later and therefore JITing is counter-productiive.
		java.lang.Compiler.disable();

	        java.security.AccessController.doPrivileged(
			new java.security.PrivilegedAction() {
		    public Object run() {
		        String nm = null;
			Class cls = null;
		        try {
			    nm = System.getProperty("awt.toolkit",
						"sun.awt.motif.MToolkit");
			    try {
			    	cls = Class.forName(nm);
		            } catch (ClassNotFoundException e) {
			    	ClassLoader cl = ClassLoader.getSystemClassLoader();
                                if (cl != null) {
			    	    try {
                                        cls = cl.loadClass(nm);
		            	    } catch (ClassNotFoundException ee) {
			    		throw new AWTError("Toolkit not found: " + nm);
				    }
                                }
	                    }
                            if (cls != null) {
				toolkit = (Toolkit)cls.newInstance();
			    }
		        } catch (InstantiationException e) {
			    throw new AWTError("Could not instantiate Toolkit: " +
					   nm);
		        } catch (IllegalAccessException e) {
			    throw new AWTError("Could not access Toolkit: " + nm);
		        }
		        return null;
		    }
	        });
	        loadAssistiveTechnologies();

	    } finally {
		// Make sure to always re-enable the JIT.
		java.lang.Compiler.enable();
	    }
	}
	return toolkit;
    }

    /**
     * Returns an image which gets pixel data from the specified file,
     * whose format can be either GIF, JPEG or PNG.
     * The underlying toolkit attempts to resolve multiple requests
     * with the same filename to the same returned Image.
     * Since the mechanism required to facilitate this sharing of
     * Image objects may continue to hold onto images that are no
     * longer of use for an indefinate period of time, developers
     * are encouraged to implement their own caching of images by
     * using the createImage variant wherever available.
     * @param     filename   the name of a file containing pixel data
     *                         in a recognized file format.
     * @return    an image which gets its pixel data from
     *                         the specified file.
     * @see #createImage(java.lang.String)
     */
    public abstract Image getImage(String filename);

    /**
     * Returns an image which gets pixel data from the specified URL.
     * The pixel data referenced by the specified URL must be in one
     * of the following formats: GIF, JPEG or PNG.
     * The underlying toolkit attempts to resolve multiple requests
     * with the same URL to the same returned Image.
     * Since the mechanism required to facilitate this sharing of
     * Image objects may continue to hold onto images that are no
     * longer of use for an indefinate period of time, developers
     * are encouraged to implement their own caching of images by
     * using the createImage variant wherever available.
     * @param     url   the URL to use in fetching the pixel data.
     * @return    an image which gets its pixel data from
     *                         the specified URL.
     * @see #createImage(java.net.URL)
     */
    public abstract Image getImage(URL url);

    /**
     * Returns an image which gets pixel data from the specified file.
     * The returned Image is a new object which will not be shared
     * with any other caller of this method or its getImage variant.
     * @param     filename   the name of a file containing pixel data
     *                         in a recognized file format.
     * @return    an image which gets its pixel data from
     *                         the specified file.
     * @see #getImage(java.lang.String)
     */
    public abstract Image createImage(String filename);

    /**
     * Returns an image which gets pixel data from the specified URL.
     * The returned Image is a new object which will not be shared
     * with any other caller of this method or its getImage variant.
     * @param     url   the URL to use in fetching the pixel data.
     * @return    an image which gets its pixel data from
     *                         the specified URL.
     * @see #getImage(java.net.URL)
     */
    public abstract Image createImage(URL url);

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
     * Gets a <code>PrintJob</code> object which is the result of initiating
     * a print operation on the toolkit's platform.
     * <p>
     * Each actual implementation of this method should first check if there 
     * is a security manager installed. If there is, the method should call
     * the security manager's <code>checkPrintJobAccess</code> method to
     * ensure initiation of a print operation is allowed. If the default
     * implementation of <code>checkPrintJobAccess</code> is used (that is,
     * that method is not overriden), then this results in a call to the
     * security manager's <code>checkPermission</code> method with a <code>
     * RuntimePermission("queuePrintJob")</code> permission.
     *
     * @param	frame the parent of the print dialog. May not be null.
     * @param	jobtitle the title of the PrintJob. A null title is equivalent
     *		to "".
     * @param	props a Properties object containing zero or more properties.
     *		Properties are not standardized and are not consistent across
     *		implementations. Because of this, PrintJobs which require job
     *		and page control should use the version of this function which
     *		takes JobAttributes and PageAttributes objects. This object
     *		may be updated to reflect the user's job choices on exit. May
     *		be null.
     *
     * @return	a <code>PrintJob</code> object, or <code>null</code> if the
     *		user cancelled the print job.
     * @throws	NullPointerException if frame is null
     * @throws	SecurityException if this thread is not allowed to initiate a
     *		print job request
     * @see	java.awt.PrintJob
     * @see	java.lang.RuntimePermission
     * @since	JDK1.1
     */
    public abstract PrintJob getPrintJob(Frame frame, String jobtitle,
					 Properties props);

    /**
     * Gets a <code>PrintJob</code> object which is the result of initiating
     * a print operation on the toolkit's platform.
     * <p>
     * Each actual implementation of this method should first check if there 
     * is a security manager installed. If there is, the method should call
     * the security manager's <code>checkPrintJobAccess</code> method to
     * ensure initiation of a print operation is allowed. If the default
     * implementation of <code>checkPrintJobAccess</code> is used (that is,
     * that method is not overriden), then this results in a call to the
     * security manager's <code>checkPermission</code> method with a <code>
     * RuntimePermission("queuePrintJob")</code> permission.
     *
     * @param	frame the parent of the print dialog. May be null if and only
     *		if jobAttributes is not null and jobAttributes.getDialog()
     *		returns	JobAttributes.DialogType.NONE or
     *		JobAttributes.DialogType.COMMON.
     * @param	jobtitle the title of the PrintJob. A null title is equivalent
     *		to "".
     * @param	jobAttributes a set of job attributes which will control the
     *		PrintJob. The attributes will be updated to reflect the user's
     *		choices as outlined in the JobAttributes documentation. May be
     *		null.
     * @param	pageAttributes a set of page attributes which will control the
     *		PrintJob. The attributes will be applied to every page in the
     *		job. The attributes will be updated to reflect the user's
     *		choices as outlined in the PageAttributes documentation. May be
     *		null.
     *
     * @return	a <code>PrintJob</code> object, or <code>null</code> if the
     *		user cancelled the print job.
     * @throws	NullPointerException if frame is null and either jobAttributes
     *		is null or jobAttributes.getDialog() returns
     *		JobAttributes.DialogType.NATIVE.
     * @throws	IllegalArgumentException if pageAttributes specifies differing
     *		cross feed and feed resolutions
     * @throws	SecurityException if this thread is not allowed to initiate a
     *		print job request, or if jobAttributes specifies print to file,
     *		and this thread is not allowed to access the file system
     * @see	java.awt.PrintJob
     * @see	java.lang.RuntimePermission
     * @see	java.awt.JobAttributes
     * @see	java.awt.PageAttributes
     * @since	1.3
     */
    public PrintJob getPrintJob(Frame frame, String jobtitle,
				JobAttributes jobAttributes,
				PageAttributes pageAttributes) {
        // Override to add printing support with new job/page control classes
	if (this != Toolkit.getDefaultToolkit()) {
	    return Toolkit.getDefaultToolkit().getPrintJob(frame, jobtitle,
							   jobAttributes,
							   pageAttributes);
	} else {
	    return getPrintJob(frame, jobtitle, null);
	}
    }

    /**
     * Emits an audio beep.
     * @since     JDK1.1
     */
    public abstract void beep();

    /**
     * Gets the singleton instance of the system Clipboard which interfaces
     * with clipboard facilities provided by the native platform. This 
     * clipboard enables data transfer between Java programs and native
     * applications which use native clipboard facilities.
     * <p>
     * In addition to any and all formats specified in the flavormap.properties
     * file, or other file specified by the <code>AWT.DnD.flavorMapFileURL
     * </code> Toolkit property, text returned by the system Clipboard's <code>
     * getTransferData()</code> method is available in the following flavors:
     * <ul>
     * <li>DataFlavor.stringFlavor</li>
     * <li>DataFlavor.plainTextFlavor (<b>deprecated</b>)</li>
     * </ul>
     * As with <code>java.awt.datatransfer.StringSelection</code>, if the
     * requested flavor is <code>DataFlavor.plainTextFlavor</code>, or an
     * equivalent flavor, a Reader is returned. <b>Note:</b> The behavior of
     * the system Clipboard's <code>getTransferData()</code> method for <code>
     * DataFlavor.plainTextFlavor</code>, and equivalent DataFlavors, is
     * inconsistent with the definition of <code>DataFlavor.plainTextFlavor
     * </code>. Because of this, support for <code>
     * DataFlavor.plainTextFlavor</code>, and equivalent flavors, is
     * <b>deprecated</b>.
     * <p>
     * Each actual implementation of this method should first check if there
     * is a security manager installed. If there is, the method should call
     * the security manager's <code>checkSystemClipboardAccess</code> method
     * to ensure it's ok to to access the system clipboard. If the default
     * implementation of <code>checkSystemClipboardAccess</code> is used (that
     * is, that method is not overriden), then this results in a call to the
     * security manager's <code>checkPermission</code> method with an <code>
     * AWTPermission("accessClipboard")</code> permission.
     * 
     * @return    the system Clipboard
     * @see       java.awt.datatransfer.Clipboard
     * @see       java.awt.datatransfer.StringSelection
     * @see       java.awt.datatransfer.DataFlavor.stringFlavor
     * @see       java.awt.datatransfer.DataFlavor.plainTextFlavor
     * @see       java.io.Reader
     * @see       java.awt.AWTPermission
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
     * Returns whether the given locking key on the keyboard is currently in
     * its "on" state.
     * Valid key codes are
     * {@link java.awt.event.KeyEvent#VK_CAPS_LOCK VK_CAPS_LOCK},
     * {@link java.awt.event.KeyEvent#VK_NUM_LOCK VK_NUM_LOCK},
     * {@link java.awt.event.KeyEvent#VK_SCROLL_LOCK VK_SCROLL_LOCK}, and
     * {@link java.awt.event.KeyEvent#VK_KANA_LOCK VK_KANA_LOCK}.
     *
     * @exception java.lang.IllegalArgumentException if <code>keyCode</code>
     * is not one of the valid key codes
     * @exception java.lang.UnsupportedOperationException if the host system doesn't
     * allow getting the state of this key programmatically, or if the keyboard
     * doesn't have this key
     * @since 1.3
     */
    public boolean getLockingKeyState(int keyCode) {
        if (! (keyCode == KeyEvent.VK_CAPS_LOCK || keyCode == KeyEvent.VK_NUM_LOCK ||
               keyCode == KeyEvent.VK_SCROLL_LOCK || keyCode == KeyEvent.VK_KANA_LOCK)) {
            throw new IllegalArgumentException("invalid key for Toolkit.getLockingKeyState");
        }
        throw new UnsupportedOperationException("Toolkit.getLockingKeyState");
    }

    /**
     * Sets the state of the given locking key on the keyboard.
     * Valid key codes are
     * {@link java.awt.event.KeyEvent#VK_CAPS_LOCK VK_CAPS_LOCK},
     * {@link java.awt.event.KeyEvent#VK_NUM_LOCK VK_NUM_LOCK},
     * {@link java.awt.event.KeyEvent#VK_SCROLL_LOCK VK_SCROLL_LOCK}, and
     * {@link java.awt.event.KeyEvent#VK_KANA_LOCK VK_KANA_LOCK}.
     * <p>
     * Depending on the platform, setting the state of a locking key may
     * involve event processing and therefore may not be immediately
     * observable through getLockingKeyState.
     *
     * @exception java.lang.IllegalArgumentException if <code>keyCode</code>
     * is not one of the valid key codes
     * @exception java.lang.UnsupportedOperationException if the host system doesn't
     * allow setting the state of this key programmatically, or if the keyboard
     * doesn't have this key
     * @since 1.3
     */
    public void setLockingKeyState(int keyCode, boolean on) {
        if (! (keyCode == KeyEvent.VK_CAPS_LOCK || keyCode == KeyEvent.VK_NUM_LOCK ||
               keyCode == KeyEvent.VK_SCROLL_LOCK || keyCode == KeyEvent.VK_KANA_LOCK)) {
            throw new IllegalArgumentException("invalid key for Toolkit.setLockingKeyState");
        }
        throw new UnsupportedOperationException("Toolkit.setLockingKeyState");
    }

    /**
     * Give native peers the ability to query the native container
     * given a native component (eg the direct parent may be lightweight).
     */
    protected static Container getNativeContainer(Component c) {
	return c.getNativeContainer();
    }

    /**
     * Creates a new custom cursor object.
     * If the image to display is invalid, the cursor will be hidden (made
     * completely transparent), and the hotspot will be set to (0, 0). 
     * @param image the image to display when the cursor is active.
     * @param hotSpot the X and Y of the large cursor's hot spot.  The
     * hotSpot values must be less than the Dimension returned by
     * getBestCursorSize().
     * @param     name a localized description of the cursor, for Java Accessibility use.
     * @exception IndexOutOfBoundsException if the hotSpot values are outside
     * the bounds of the cursor.
     * @since     1.2
     */
    public Cursor createCustomCursor(Image cursor, Point hotSpot, String name)
        throws IndexOutOfBoundsException
    {
        // Override to implement custom cursor support.
        if (this != Toolkit.getDefaultToolkit()) {
	    return Toolkit.getDefaultToolkit().
	        createCustomCursor(cursor, hotSpot, name);
	} else {
	    return new Cursor(Cursor.DEFAULT_CURSOR);
	}
    }

    /**
     * Returns the supported cursor dimension which is closest to the desired
     * sizes.  Systems which only support a single cursor size will return that
     * size regardless of the desired sizes.  Systems which don't support custom
     * cursors will return a dimension of 0, 0. <p>
     * Note:  if an image is used whose dimensions don't match a supported size
     * (as returned by this method), the Toolkit implementation will attempt to
     * resize the image to a supported size.
     * Since converting low-resolution images is difficult,
     * no guarantees are made as to the quality of a cursor image which isn't a
     * supported size.  It is therefore recommended that this method
     * be called and an appropriate image used so no image conversion is made.
     *
     * @param     desiredWidth the preferred cursor width the component would like
     * to use.
     * @param     desiredHeight the preferred cursor height the component would like
     * to use.
     * @return    the closest matching supported cursor size, or a dimension of 0,0 if
     * the Toolkit implementation doesn't support custom cursors.
     * @since     1.2
     */
    public Dimension getBestCursorSize(int preferredWidth, int preferredHeight) {
        // Override to implement custom cursor support.
        if (this != Toolkit.getDefaultToolkit()) {
	    return Toolkit.getDefaultToolkit().
	        getBestCursorSize(preferredWidth, preferredHeight);
	} else {
	    return new Dimension(0, 0);
	}
    }

    /**
     * Returns the maximum number of colors the Toolkit supports in a custom cursor
     * palette.<p>
     * Note: if an image is used which has more colors in its palette than
     * the supported maximum, the Toolkit implementation will attempt to flatten the
     * palette to the maximum.  Since converting low-resolution images is difficult,
     * no guarantees are made as to the quality of a cursor image which has more
     * colors than the system supports.  It is therefore recommended that this method
     * be called and an appropriate image used so no image conversion is made.
     *
     * @return    the maximum number of colors, or zero if custom cursors are not
     * supported by this Toolkit implementation.
     * @since     1.2
     */
    public int getMaximumCursorColors() {
        // Override to implement custom cursor support.
        if (this != Toolkit.getDefaultToolkit()) {
	    return Toolkit.getDefaultToolkit().getMaximumCursorColors();
	} else {
	    return 0;
	}
    }

    /**
     * Support for I18N: any visible strings should be stored in
     * java.awt.resources.awt.properties.  The ResourceBundle is stored
     * here, so that only one copy is maintained.
     */
    private static ResourceBundle resources;

    /**
     * Initialize JNI field and method ids
     */
    private static native void initIDs();

    /**
     * WARNING: This is a temporary workaround for a problem in the
     * way the AWT loads native libraries. A number of classes in the
     * AWT package have a native method, initIDs(), which initializes
     * the JNI field and method ids used in the native portion of
     * their implementation.
     *
     * Since the use and storage of these ids is done by the
     * implementation libraries, the implementation of these method is
     * provided by the particular AWT implementations
     * (i.e. "Toolkit"s/Peer), such as Motif, Win32 or Tiny. The
     * problem is that this means that the native libraries must be
     * loaded by the java.* classes, which do not necessarily know the
     * names of the libraries to load. A better way of doing this
     * would be to provide a separate library which defines java.awt.*
     * initIDs, and exports the relevant symbols out to the
     * implementation libraries.
     *
     * For now, we know it's done by the implementation, and we assume
     * that the name of the library is "awt".  -br.
     *
     * If you change loadLibraries(), please add the change to
     * java.awt.image.ColorModel.loadLibraries(). Unfortunately,
     * classes can be loaded in java.awt.image that depend on
     * libawt and there is no way to call Toolkit.loadLibraries()
     * directly.  -hung
     */
    private static boolean loaded = false;
    static void loadLibraries() {
	if (!loaded) {
	    java.security.AccessController.doPrivileged(
			  new sun.security.action.LoadLibraryAction("awt"));
	    loaded = true;
        }
    }

    static {
	java.security.AccessController.doPrivileged(
				 new java.security.PrivilegedAction() {
	    public Object run() {
		try {
		    resources =
			ResourceBundle.getBundle("java.awt.resources.awt");
		} catch (MissingResourceException e) {
		    // No resource file; defaults will be used.
		}
		return null;
	    }
	});

	// ensure that the proper libraries are loaded
        loadLibraries();
	initIDs();
    }

    /**
     * Gets a property with the specified key and default.
     * This method returns defaultValue if the property is not found.
     */
    public static String getProperty(String key, String defaultValue) {
        if (resources != null) {
	    try {
	        return resources.getString(key);
	    }
	    catch (MissingResourceException e) {}
        }

	return defaultValue;
    }

    /**
     * Get the application's or applet's EventQueue instance.
     * Depending on the Toolkit implementation, different EventQueues
     * may be returned for different applets.  Applets should
     * therefore not assume that the EventQueue instance returned
     * by this method will be shared by other applets or the system.
     * 
     * <p>First, if there is a security manager, its 
     * <code>checkAwtEventQueueAccess</code> 
     * method is called. 
     * If  the default implementation of <code>checkAwtEventQueueAccess</code> 
     * is used (that is, that method is not overriden), then this results in
     * a call to the security manager's <code>checkPermission</code> method
     * with an <code>AWTPermission("accessEventQueue")</code> permission.
     * 
     * @return    the <code>EventQueue</code> object.
     * @throws  SecurityException
     *          if a security manager exists and its <code>{@link
     *          java.lang.SecurityManager#checkAwtEventQueueAccess}</code> method denies
     *          access to the EventQueue.
     * @see       java.awt.AWTPermission
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
        return getDefaultToolkit().getSystemEventQueueImpl();
    }

    /**
     * create the peer for a DragSourceContext
     */
    public abstract DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent dge) throws InvalidDnDOperationException;

    /**
     * create a concrete, platform dependent, subclass of the abstract
     * DragGestureRecognizer class requested, and associate it with the
     * DragSource, Component and DragGestureListener specified
     *
     * subclasses should override this to provide their own implementation
     *
     * @param abstractRecognizerClass The abstract class of the required recognizer
     * @param ds		      The DragSource
     * @param c			      The Component target for the DragGestureRecognizer
     * @param srcActions	      The actions permitted for the gesture
     * @param dgl		      The DragGestureListener
     *
     * @Return the new object or null
     */
    public DragGestureRecognizer createDragGestureRecognizer(Class abstractRecognizerClass, DragSource ds, Component c, int srcActions, DragGestureListener dgl) {
	return null;
    }

    /**
     * obtain a value for the specified desktop property.
     *
     * A desktop property is a uniquely named value for a resource that
     * is Toolkit global in nature. Usually it also is an abstract representation
     * for an underlying platform dependent desktop setting.
     */

    public final synchronized Object getDesktopProperty(String propertyName) {

	if (desktopProperties.isEmpty()) {
	    initializeDesktopProperties();
	}

	Object value = desktopProperties.get(propertyName);

	if (value == null) {
	    value = lazilyLoadDesktopProperty(propertyName);

	    if (value != null) {
		setDesktopProperty(propertyName, value);
	    }
	}

	return value;
    }

    /**
     * set the named desktop property to the specified value and fire a
     * property change event to notify any listeners that the value has changed
     */
    protected final void setDesktopProperty(String name, Object newValue) {
        Object oldValue;

        synchronized (this) {
            oldValue = desktopProperties.get(name);
            desktopProperties.put(name, newValue);
        }

        desktopPropsSupport.firePropertyChange(name, oldValue, newValue);
    }

    /**
     * an opportunity to lazily evaluate desktop property values.
     */

    protected Object lazilyLoadDesktopProperty(String name) {
	return null;
    }

    /**
     * initializeDesktopProperties
     */

    protected void initializeDesktopProperties() {
    }

    /**
     * add the specified property change listener for the named desktop 
     * property
     * If pcl is null, no exception is thrown and no action is performed.
     *
     * @param 	name The name of the property to listen for
     * @param	pcl The property change listener
     */
    public synchronized void addPropertyChangeListener(String name, PropertyChangeListener pcl) {
	if (pcl == null) {
	    return;
	}
	desktopPropsSupport.addPropertyChangeListener(name, pcl);
    }

    /**
     * remove the specified property change listener for the named 
     * desktop property
     * If pcl is null, no exception is thrown and no action is performed.
     *
     */
    public synchronized void removePropertyChangeListener(String name, PropertyChangeListener pcl) {
	if (pcl == null) {
	    return;
	}
	desktopPropsSupport.removePropertyChangeListener(name, pcl);
    }

    protected final Map		          desktopProperties   = new HashMap();
    protected final PropertyChangeSupport desktopPropsSupport = new PropertyChangeSupport(this);

    private AWTEventListener eventListener = null;
    private WeakHashMap listener2SelectiveListener = new WeakHashMap();

    private AWTPermission listenToAllAWTEventsPermission = null;

    /**
     * Adds an AWTEventListener to receive all AWTEvents dispatched
     * system-wide that conform to the given <code>eventMask</code>.
     * <p>
     * First, if there is a security manager, its <code>checkPermission</code> 
     * method is called with an 
     * <code>AWTPermission("listenToAllAWTEvents")</code> permission.
     * This may result in a SecurityException. 
     * <p>
     * <code>eventMask</code> is a bitmask of event types to receive.
     * It is constructed by bitwise OR-ing together the event masks
     * defined in <code>AWTEvent</code>.
     * <p>
     * Note:  event listener use is not recommended for normal
     * application use, but are intended solely to support special
     * purpose facilities including support for accessibility,
     * event record/playback, and diagnostic tracing.
     *
     * If listener is null, no exception is thrown and no action is performed.
     *
     * @param    listener   the event listener.
     * @param    eventMask  the bitmask of event types to receive
     * @throws SecurityException
     *        if a security manager exists and its 
     *        <code>checkPermission</code> method doesn't allow the operation.
     * @see      java.awt.event.AWTEventListener
     * @see      java.awt.Toolkit#addAWTEventListener
     * @see      java.awt.AWTEvent
     * @see      SecurityManager#checkPermission
     * @see      java.awt.AWTPermission
     * @since    1.2
     */
    public void addAWTEventListener(AWTEventListener listener, long eventMask) {
        if (listener == null) {
            return;
        }
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
          if (listenToAllAWTEventsPermission == null) {
            listenToAllAWTEventsPermission =
                        new AWTPermission("listenToAllAWTEvents");
          }
          security.checkPermission(listenToAllAWTEventsPermission);
        }
        synchronized (this) {
            SelectiveAWTEventListener selectiveListener =
                (SelectiveAWTEventListener)listener2SelectiveListener
                .get(listener);
            if (selectiveListener == null) {
                // Create a new selectiveListener.
                selectiveListener =
                    new SelectiveAWTEventListener(listener, eventMask);
                listener2SelectiveListener.put(listener, selectiveListener);
                eventListener = ToolkitEventMulticaster.add(eventListener,
                                                            selectiveListener);
            }
            // OR the eventMask into the selectiveListener's event mask.
            selectiveListener.orEventMasks(eventMask);
        }
    }

    /**
     * Removes an AWTEventListener from receiving dispatched AWTEvents.
     * <p>
     * First, if there is a security manager, its <code>checkPermission</code> 
     * method is called with an 
     * <code>AWTPermission("listenToAllAWTEvents")</code> permission.
     * This may result in a SecurityException. 
     * <p>
     * Note:  event listener use is not recommended for normal
     * application use, but are intended solely to support special
     * purpose facilities including support for accessibility,
     * event record/playback, and diagnostic tracing.
     *
     * If listener is null, no exception is thrown and no action is performed.
     *
     * @param    listener   the event listener.
     * @throws SecurityException
     *        if a security manager exists and its 
     *        <code>checkPermission</code> method doesn't allow the operation.
     * @see      java.awt.event.AWTEventListener
     * @see      java.awt.Toolkit#addAWTEventListener
     * @see      java.awt.AWTEvent
     * @see      SecurityManager#checkPermission
     * @see      java.awt.AWTPermission
     * @since    1.2
     */
    public void removeAWTEventListener(AWTEventListener listener) {
	if (listener == null) {
	    return;
	}
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
	  if (listenToAllAWTEventsPermission == null) {
	    listenToAllAWTEventsPermission =
			new AWTPermission("listenToAllAWTEvents");
	  }
	  security.checkPermission(listenToAllAWTEventsPermission);
        }
	synchronized (this) {
	    SelectiveAWTEventListener selectiveListener =
		(SelectiveAWTEventListener)listener2SelectiveListener
		.get(listener);
	    if (selectiveListener != null)
		listener2SelectiveListener.remove(listener);
	    eventListener = ToolkitEventMulticaster.remove(eventListener,
		(selectiveListener == null) ? listener : selectiveListener);
	}
    }

    /*
     * This method notifies any AWTEventListeners that an event
     * is about to be dispatched.
     *
     * @param theEvent the event which will be dispatched.
     */
    void notifyAWTEventListeners(AWTEvent theEvent) {
//Fix for 4338463
        AWTEventListener eventListener = this.eventListener;
        if (eventListener != null) {
		    eventListener.eventDispatched(theEvent);
        }
    }

    static private class ToolkitEventMulticaster extends AWTEventMulticaster
        implements AWTEventListener {
        // Implementation cloned from AWTEventMulticaster.

        ToolkitEventMulticaster(AWTEventListener a, AWTEventListener b) {
            super(a, b);
        }

        static AWTEventListener add(AWTEventListener a, 
                                    AWTEventListener b) {
	    if (a == null)  return b;
	    if (b == null)  return a;
	    return new ToolkitEventMulticaster(a, b);
        }

        static AWTEventListener remove(AWTEventListener l, 
                                       AWTEventListener oldl) {
            return (AWTEventListener) removeInternal(l, oldl);
        }

	// #4178589: must overload remove(EventListener) to call our add()
	// instead of the static addInternal() so we allocate a
	// ToolkitEventMulticaster instead of an AWTEventMulticaster.
	// Note: this method is called by AWTEventListener.removeInternal(),
	// so its method signature must match AWTEventListener.remove().
	protected EventListener remove(EventListener oldl) {
	    if (oldl == a)  return b;
	    if (oldl == b)  return a;
	    AWTEventListener a2 = (AWTEventListener)removeInternal(a, oldl);
	    AWTEventListener b2 = (AWTEventListener)removeInternal(b, oldl);
	    if (a2 == a && b2 == b) {
		return this;	// it's not here
	    }
	    return add(a2, b2);
	}

        public void eventDispatched(AWTEvent event) {
            ((AWTEventListener)a).eventDispatched(event);
            ((AWTEventListener)b).eventDispatched(event);
        }
    }

    private class SelectiveAWTEventListener implements AWTEventListener {
	AWTEventListener listener;
	private long eventMask;
        static final int LONG_BITS = 64;
        // This array contains the number of times to call the eventlistener
        // for each event type.
        int[] calls = new int[LONG_BITS];

        public void orEventMasks(long mask) {
            eventMask |= mask;
            // For each event bit set in mask, increment its call count.
            for (int i=0; i<LONG_BITS; i++) {
                // If no bits are set, break out of loop.
                if (mask == 0) {
                    break;
                }
                if ((mask & 1L) != 0) {  // Always test bit 0.
                    calls[i]++;
                }
                mask >>>= 1;  // Right shift, fill with zeros on left.
            }
        }

	SelectiveAWTEventListener(AWTEventListener l, long mask) {
	    listener = l;
	    eventMask = mask;
	}

        public void eventDispatched(AWTEvent event) {
            long eventBit = 0; // Used to save the bit of the event type.
	    if (((eventBit = eventMask & AWTEvent.COMPONENT_EVENT_MASK) != 0 &&
		 event.id >= ComponentEvent.COMPONENT_FIRST &&
		 event.id <= ComponentEvent.COMPONENT_LAST)
	     || ((eventBit = eventMask & AWTEvent.CONTAINER_EVENT_MASK) != 0 &&
		 event.id >= ContainerEvent.CONTAINER_FIRST &&
		 event.id <= ContainerEvent.CONTAINER_LAST)
	     || ((eventBit = eventMask & AWTEvent.FOCUS_EVENT_MASK) != 0 &&
		 event.id >= FocusEvent.FOCUS_FIRST &&
		 event.id <= FocusEvent.FOCUS_LAST)
	     || ((eventBit = eventMask & AWTEvent.KEY_EVENT_MASK) != 0 &&
		 event.id >= KeyEvent.KEY_FIRST &&
		 event.id <= KeyEvent.KEY_LAST)
	     || ((eventBit = eventMask & AWTEvent.MOUSE_MOTION_EVENT_MASK) != 0 &&
	         (event.id == MouseEvent.MOUSE_MOVED ||
	          event.id == MouseEvent.MOUSE_DRAGGED))
	     || ((eventBit = eventMask & AWTEvent.MOUSE_EVENT_MASK) != 0 &&
	         event.id != MouseEvent.MOUSE_MOVED &&
	         event.id != MouseEvent.MOUSE_DRAGGED &&
		 event.id >= MouseEvent.MOUSE_FIRST &&
		 event.id <= MouseEvent.MOUSE_LAST)
	     || ((eventBit = eventMask & AWTEvent.WINDOW_EVENT_MASK) != 0 &&
		 event.id >= WindowEvent.WINDOW_FIRST &&
		 event.id <= WindowEvent.WINDOW_LAST)
	     || ((eventBit = eventMask & AWTEvent.ACTION_EVENT_MASK) != 0 &&
		 event.id >= ActionEvent.ACTION_FIRST &&
		 event.id <= ActionEvent.ACTION_LAST)
	     || ((eventBit = eventMask & AWTEvent.ADJUSTMENT_EVENT_MASK) != 0 &&
		 event.id >= AdjustmentEvent.ADJUSTMENT_FIRST &&
		 event.id <= AdjustmentEvent.ADJUSTMENT_LAST)
	     || ((eventBit = eventMask & AWTEvent.ITEM_EVENT_MASK) != 0 &&
		 event.id >= ItemEvent.ITEM_FIRST &&
		 event.id <= ItemEvent.ITEM_LAST)
	     || ((eventBit = eventMask & AWTEvent.TEXT_EVENT_MASK) != 0 &&
		 event.id >= TextEvent.TEXT_FIRST &&
		 event.id <= TextEvent.TEXT_LAST)
	     || ((eventBit = eventMask & AWTEvent.INPUT_METHOD_EVENT_MASK) != 0 &&
		 event.id >= InputMethodEvent.INPUT_METHOD_FIRST &&
		 event.id <= InputMethodEvent.INPUT_METHOD_LAST)
	     || ((eventBit = eventMask & AWTEvent.PAINT_EVENT_MASK) != 0 &&
		 event.id >= PaintEvent.PAINT_FIRST &&
		 event.id <= PaintEvent.PAINT_LAST)
	     || ((eventBit = eventMask & AWTEvent.INVOCATION_EVENT_MASK) != 0 &&
		 event.id >= InvocationEvent.INVOCATION_FIRST &&
		 event.id <= InvocationEvent.INVOCATION_LAST)
	     || ((eventBit = eventMask & AWTEvent.HIERARCHY_EVENT_MASK) != 0 &&
		 event.id == HierarchyEvent.HIERARCHY_CHANGED)
	     || ((eventBit = eventMask & AWTEvent.HIERARCHY_BOUNDS_EVENT_MASK) != 0 &&
		 (event.id == HierarchyEvent.ANCESTOR_MOVED ||
		  event.id == HierarchyEvent.ANCESTOR_RESIZED))) {
                // Get the index of the call count for this event type.
                int ci = (int) (Math.log(eventBit)/Math.log(2));
                // Call the listener as many times as it was added for this
                // event type.
                for (int i=0; i<calls[ci]; i++) {
		    listener.eventDispatched(event);
                }
	    }
        }
    }
    
    /**
     * Returns a map of visual attributes for the abstract level description
     * of the given input method highlight, or null if no mapping is found.
     * The style field of the input method highlight is ignored. The map
     * returned is unmodifiable.
     * @param highlight input method highlight
     * @return style attribute map, or null
     * @since 1.3
     */
    public abstract Map mapInputMethodHighlight(InputMethodHighlight highlight);
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

    public void coalescePaintEvent(PaintEvent e) {
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
    
    public GraphicsConfiguration getGraphicsConfiguration() {
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
