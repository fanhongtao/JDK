/*
 * @(#)Toolkit.java	1.71 97/01/24
 * 
 * Copyright (c) 1995, 1996 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the confidential and proprietary information of Sun
 * Microsystems, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Sun.
 * 
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
 * PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES
 * SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 * 
 * CopyrightVersion 1.1_beta
 * 
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
 * An AWT toolkit. It is used to bind the abstract AWT classes
 * to a particular native toolkit implementation.
 *
 * @version 	1.71, 01/24/97
 * @author	Sami Shaio
 * @author	Arthur van Hoff
 */
public abstract class  Toolkit {

    /**
     * Uses the specified Peer interface to create a new Button.
     * @param target the Button to be created
     */
    protected abstract ButtonPeer 	createButton(Button target);

    /**
     * Uses the specified Peer interface to create a new TextField.      
     * @param target the TextField to be created
     */
    protected abstract TextFieldPeer 	createTextField(TextField target);

    /**
     * Uses the specified Peer interface to create a new Label.      
     * @param target the Label to be created
     */
    protected abstract LabelPeer 	createLabel(Label target);

    /**
     * Uses the specified Peer interface to create a new List.      
     * @param target the List to be created
     */
    protected abstract ListPeer 	createList(List target);

    /**
     * Uses the specified Peer interface to create a new Checkbox.      
     * @param target the Checkbox to be created
     */
    protected abstract CheckboxPeer 	createCheckbox(Checkbox target);

    /**
     * Uses the specified Peer interface to create a new Scrollbar.      
     * @param target the Scrollbar to be created
     */
    protected abstract ScrollbarPeer 	createScrollbar(Scrollbar target);

    /**
     * Uses the specified Peer interface to create a new scrolling container.
     * @param target the ScrollPane to be created
     */
    protected abstract ScrollPanePeer     createScrollPane(ScrollPane target);

    /**
     * Uses the specified Peer interface to create a new TextArea.      
     * @param target the TextArea to be created
     */
    protected abstract TextAreaPeer  	createTextArea(TextArea target);

    /**
     * Uses the specified Peer interface to create a new Choice.      
     * @param target the Choice to be created
     */
    protected abstract ChoicePeer	createChoice(Choice target);

    /**
     * Uses the specified Peer interface to create a new Frame.
     * @param target the Frame to be created
     */
    protected abstract FramePeer  	createFrame(Frame target);

    /**
     * Uses the specified Peer interface to create a new Canvas.
     * @param target the Canvas to be created
     */
    protected abstract CanvasPeer 	createCanvas(Canvas target);

    /**
     * Uses the specified Peer interface to create a new Panel.
     * @param target the Panel to be created
     */
    protected abstract PanelPeer  	createPanel(Panel target);

    /**
     * Uses the specified Peer interface to create a new Window.
     * @param target the Window to be created
     */
    protected abstract WindowPeer  	createWindow(Window target);

    /**
     * Uses the specified Peer interface to create a new Dialog.
     * @param target the Dialog to be created
     */
    protected abstract DialogPeer  	createDialog(Dialog target);

    /**
     * Uses the specified Peer interface to create a new MenuBar.
     * @param target the MenuBar to be created
     */
    protected abstract MenuBarPeer  	createMenuBar(MenuBar target);

    /**
     * Uses the specified Peer interface to create a new Menu.
     * @param target the Menu to be created
     */
    protected abstract MenuPeer  	createMenu(Menu target);

    /**
     * Uses the specified Peer interface to create a new PopupMenu.
     * @param target the PopupMenu to be created
     */
    protected abstract PopupMenuPeer	createPopupMenu(PopupMenu target);

    /**
     * Uses the specified Peer interface to create a new MenuItem.
     * @param target the MenuItem to be created
     */
    protected abstract MenuItemPeer  	createMenuItem(MenuItem target);

    /**
     * Uses the specified Peer interface to create a new FileDialog.
     * @param target the FileDialog to be created
     */
    protected abstract FileDialogPeer	createFileDialog(FileDialog target);

    /**
     * Uses the specified Peer interface to create a new CheckboxMenuItem.
     * @param target the CheckboxMenuItem to be created
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
     * Uses the specified Peer interface to create a new Font.
     * @param name the font name
     * @param style style the constant style used
     */
    protected abstract FontPeer getFontPeer(String name, int style);

    /**
     * Fills in the provided int array with the current system color values
     */
    protected void loadSystemColors(int[] systemColors) {
    }

    /**
     * Gets the size of the screen.
     */
    public abstract Dimension getScreenSize();

    /**
     * Returns the screen resolution in dots-per-inch.
     */
    public abstract int getScreenResolution();

    /**
     * Returns the ColorModel of the screen.
     */
    public abstract ColorModel getColorModel();

    /**
     * Returns the names of the available fonts.<p>
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
     */
    public abstract String[] getFontList();

    /**
     * Returns the screen metrics of the font.
     */
    public abstract FontMetrics getFontMetrics(Font font);

    /**
     * Syncs the graphics state; useful when doing animation.
     */
    public abstract void sync();

    /**
     * The default toolkit.
     */
    private static Toolkit toolkit;

    /**
     * Returns the default toolkit. This is controlled by the
     * "awt.toolkit" property.
     * @exception AWTError Toolkit not found or could not be instantiated.
     */
    public static synchronized Toolkit getDefaultToolkit() {
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

    /**
     * Returns an image which gets pixel data from the specified file.
     * @param filename the file containing the pixel data in one of
     * the recognized file formats
     */
    public abstract Image getImage(String filename);

    /**
     * Returns an image which gets pixel data from the specified URL.
     * @param url the URL to use in fetching the pixel data
     */
    public abstract Image getImage(URL url);

    /**
     * Prepares an image for rendering on the default screen at the
     * specified width and height.
     */
    public abstract boolean prepareImage(Image image, int width, int height,
					 ImageObserver observer);

    /**
     * Returns the status of the construction of the indicated method
     * at the indicated width and height for the default screen.
     */
    public abstract int checkImage(Image image, int width, int height,
				   ImageObserver observer);

    /**
     * Creates an image with the specified image producer.
     * @param producer the image producer to be used
     */
    public abstract Image createImage(ImageProducer producer);

    /**
     * Creates an image which decodes the image stored in the specified
     * byte array.
     * The data must be in some image format supported by the toolkit
     * (such as GIF or JPEG).
     * @param imagedata the array of image data in a supported image format
     */
    public Image createImage(byte[] imagedata) {
	return createImage(imagedata, 0, imagedata.length);
    }

    /**
     * Creates an image which decodes the image stored in the specified
     * byte array at the specified offset and length.
     * The data must be in some image format supported by the toolkit
     * (such as GIF or JPEG).
     * @param imagedata the array of image data in a supported image format
     * @param imageoffset the offset of the start of the data in the array
     * @param imagelength the length of the data in the array
     */
    public abstract Image createImage(byte[] imagedata,
				      int imageoffset,
				      int imagelength);

    /**
     * Returns a PrintJob object which is the result of initiating
     * a print operation on the toolkit's platform.  Returns null if 
     * the user cancelled print job.
     */
    public abstract PrintJob getPrintJob(Frame frame, String jobtitle, Properties props);

    /**
     * Emits an audio beep.
     */
    public abstract void beep();

    /**
     * Returns an instance of the "system" clipboard which interfaces with
     * the clipboard facilities on the native platform.  This clipboard enables
     * data transfer between java programs and native platform applications
     * which use these native clipboard facilities.
     */
    public abstract Clipboard getSystemClipboard();

    /**
     * Return the Event modifier mask appropriate for shortcuts.
     * Toolkit implementation should override this method if the
     * CONTROL key isn't the correct key for accelerators.
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
