/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing;

import java.awt.Container;
import java.awt.Window;
import java.awt.Font;
import java.awt.Color;
import java.awt.Insets;
import java.awt.Dimension;

import javax.swing.plaf.ComponentUI;
import javax.swing.border.Border;

import javax.swing.event.SwingPropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedInputStream;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;


/**
 * This class keeps track of the current look and feel and its
 * defaults.
 * <p>
 * We manage three levels of defaults: user defaults, look
 * and feel defaults, system defaults.  A call to UIManager.get()
 * checks all three levels in order and returns the first non-null 
 * value for a key, if any.  A call to UIManager.put() just
 * affects the user defaults.  Note that a call to 
 * setLookAndFeel() doesn't affect the user defaults, it just
 * replaces the middle defaults "level".
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with 
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @version 1.85 02/06/02
 * @author Thomas Ball
 * @author Hans Muller
 */
public class UIManager implements Serializable 
{
    /**
     * This class defines the state managed by the UIManager.  For 
     * Swing applications the fields in this class could just as well
     * be static members of UIManager however we give them "AppContext"
     * scope instead so that applets (and potentially multiple lightweight
     * applications running in a single VM) have their own state. For 
     * example an applet can it's look and feel, see setLookAndFeel().
     * Doing so has no affect on other applets (or the browser).
     */
    private static class LAFState 
    {
        Properties swingProps;
        private UIDefaults[] tables = new UIDefaults[2];

        boolean initialized = false;
        MultiUIDefaults multiUIDefaults = new MultiUIDefaults(tables);
        LookAndFeel lookAndFeel;
        LookAndFeel multiLookAndFeel = null;
        Vector auxLookAndFeels = null;
        SwingPropertyChangeSupport changeSupport = 
            new SwingPropertyChangeSupport(UIManager.class);

        UIDefaults getLookAndFeelDefaults() { return tables[0]; }
        void setLookAndFeelDefaults(UIDefaults x) { tables[0] = x; }

        UIDefaults getSystemDefaults() { return tables[1]; }
        void setSystemDefaults(UIDefaults x) { tables[1] = x; }
    }


    /**
     * The AppContext key for our one LAFState instance.
     */
    private static final Object lafStateACKey = new StringBuffer("LookAndFeel State");


    /* Lock object used in place of class object for synchronization. (4187686)
     */
    private static final Object classLock = new Object();


    /* Cache the last referenced LAFState to improve performance 
     * when accessing it.  The cache is based on last thread rather
     * than last AppContext because of the cost of looking up the
     * AppContext each time.  Since most Swing UI work is on the 
     * EventDispatchThread, this hits often enough to justify the
     * overhead.  (4193032)
     */
    private static Thread currentLAFStateThread = null;
    private static LAFState currentLAFState = null;


    /**
     * Return the LAFState object, lazily create one if neccessary.  All access
     * to the LAFState fields is done via this method, for example:
     * <pre>
     *     getLAFState().initialized = true;
     * </pre>
     */
    private static LAFState getLAFState() {
	// First check whether we're running on the same thread as
	// the last request.
	Thread thisThread = Thread.currentThread();
	if (thisThread == currentLAFStateThread) {
	    return currentLAFState;
	}

        LAFState rv = (LAFState)SwingUtilities.appContextGet(lafStateACKey);
        if (rv == null) {
	    synchronized (classLock) {
		rv = (LAFState)SwingUtilities.appContextGet(lafStateACKey);
		if (rv == null) {
		    SwingUtilities.appContextPut(lafStateACKey, 
						 (rv = new LAFState()));
		}
	    }
        }

	currentLAFStateThread = thisThread;
	currentLAFState = rv;

	return rv;
    }


    /* Keys used for the properties file in <java.home>/lib/swing.properties.
     * See loadUserProperties(), initialize().
     */

    private static final String defaultLAFKey = "swing.defaultlaf";
    private static final String auxiliaryLAFsKey = "swing.auxiliarylaf";
    private static final String multiplexingLAFKey = "swing.plaf.multiplexinglaf";
    private static final String installedLAFsKey = "swing.installedlafs";

    /**
     * Return a swing.properties file key for the attribute of specified 
     * look and feel.  The attr is either "name" or "class", a typical
     * key would be: "swing.installedlaf.windows.name"
     */
    private static String makeInstalledLAFKey(String laf, String attr) {
        return "swing.installedlaf." + laf + "." + attr;
    }

    /**
     * The filename for swing.properties is a path like this (Unix version):
     * <java.home>/lib/swing.properties.  This method returns a bogus
     * filename if java.home isn't defined.  
     */
    private static String makeSwingPropertiesFilename() {
        final String homeDir[] = new String[]{"<java.home undefined>"};
	SwingUtilities.doPrivileged(new Runnable() {
	    public void run() {
		homeDir[0] = System.getProperty("java.home", "<java.home undefined>");
	    }
	});
        String sep = File.separator;
        return homeDir[0] + sep + "lib" + sep + "swing.properties";
    }


    /** 
     * Provide a little information about an installed LookAndFeel
     * for the sake of configuring a menu or for initial application 
     * set up.
     * 
     * @see UIManager#getInstalledLookAndFeels
     * @see LookAndFeel
     */
    public static class LookAndFeelInfo {
        private String name;
        private String className;

        /**
         * Constructs an UIManager$LookAndFeelInfo object.
         *
         * @param name      a String specifying the name of the look and feel
         * @param className a String specifiying the name of the class that
         *                  implements the look and feel
         */
        public LookAndFeelInfo(String name, String className) {
            this.name = name;
            this.className = className;
        }

        /**
         * Returns the name of the look and feel in a form suitable
         * for a menu or other presentation
         * @return a String containing the name
         * @see LookAndFeel#getName
         */
        public String getName() {
            return name;
        }

        /**
         * Returns the name of the class that implements this look and feel.
         * @return the name of the class that implements this LookAndFeel
         * @see LookAndFeel
         */
        public String getClassName() {
            return className;
        }

        /**
         * Returns a string that displays and identifies this
         * object's properties.
         *
         * @return a String representation of this object
         */
        public String toString() {
            return getClass().getName() + "[" + getName() + " " + getClassName() + "]";
        }
    }


    /**
     * The default value of installedLAFS is used when no swing.properties
     * file is available or if the file doesn't contain a "swing.installedlafs"
     * property.   
     * 
     * @see #initializeInstalledLAFs
     */
    private static LookAndFeelInfo[] installedLAFs = {
        new LookAndFeelInfo("Metal", "javax.swing.plaf.metal.MetalLookAndFeel"),
        new LookAndFeelInfo("CDE/Motif", "com.sun.java.swing.plaf.motif.MotifLookAndFeel"),
        new LookAndFeelInfo("Windows", "com.sun.java.swing.plaf.windows.WindowsLookAndFeel")
    };


    /** 
     * Return an array of objects that provide some information about the
     * LookAndFeel implementations that have been installed with this 
     * software development kit.  The LookAndFeel info objects can be used
     * by an application to construct a menu of look and feel options for 
     * the user or to set the look and feel at start up time.  Note that 
     * we do not return the LookAndFeel classes themselves here to avoid the
     * cost of unnecessarily loading them.
     * <p>
     * Given a LookAndFeelInfo object one can set the current look and feel
     * like this:
     * <pre>
     * UIManager.setLookAndFeel(info.getClassName());
     * </pre>
     * 
     * @see #setLookAndFeel
     */
    public static LookAndFeelInfo[] getInstalledLookAndFeels() {
        maybeInitialize();
        LookAndFeelInfo[] ilafs = installedLAFs;
        LookAndFeelInfo[] rv = new LookAndFeelInfo[ilafs.length];
        System.arraycopy(ilafs, 0, rv, 0, ilafs.length);
        return rv;
    }


    /**
     * Replaces the current array of installed LookAndFeelInfos.
     * 
     * @see #getInstalledLookAndFeels
     */
    public static void setInstalledLookAndFeels(LookAndFeelInfo[] infos)
        throws SecurityException
    {
        LookAndFeelInfo[] newInfos = new LookAndFeelInfo[infos.length];
        System.arraycopy(infos, 0, newInfos, 0, infos.length);
        installedLAFs = newInfos;
    }


    /**
     * Adds the specified look and feel to the current array and
     * then calls {@link #setInstalledLookAndFeels}.
     * @param info a LookAndFeelInfo object that names the look and feel
     *        and identifies that class that implements it
     */
    public static void installLookAndFeel(LookAndFeelInfo info) {
        LookAndFeelInfo[] infos = getInstalledLookAndFeels();
        LookAndFeelInfo[] newInfos = new LookAndFeelInfo[infos.length + 1];
        System.arraycopy(infos, 0, newInfos, 0, infos.length);
        newInfos[infos.length] = info;
        setInstalledLookAndFeels(newInfos);
    }


    /**
     * Creates a new look and feel and adds it to the current array.
     * Then calls {@link #setInstalledLookAndFeels}.
     *
     * @param name       a String specifying the name of the look and feel
     * @param className  a String specifying the class name that implements the
     *                   look and feel
     */
    public static void installLookAndFeel(String name, String className) {
        installLookAndFeel(new LookAndFeelInfo(name, className));
    }


    /**
     * Returns The current default look and feel, or null.
     *
     * @return The current default look and feel, or null.
     * @see #setLookAndFeel
     */
    public static LookAndFeel getLookAndFeel() {
        maybeInitialize();
        return getLAFState().lookAndFeel;
    }
    

    /**
     * Set the current default look and feel using a LookAndFeel object.  
     * <p>
     * This is a JavaBeans bound property.
     *
     * @param newLookAndFeel the LookAndFeel object
     * @exception UnsupportedLookAndFeelException If <code>lnf.isSupportedLookAndFeel()</code> is false.
     * @see #getLookAndFeel
     */
    public static void setLookAndFeel(LookAndFeel newLookAndFeel) 
        throws UnsupportedLookAndFeelException 
    {
        if ((newLookAndFeel != null) && !newLookAndFeel.isSupportedLookAndFeel()) {
            String s = newLookAndFeel.toString() + " not supported on this platform";
            throw new UnsupportedLookAndFeelException(s);
        }

        LookAndFeel oldLookAndFeel = getLAFState().lookAndFeel;
        if (oldLookAndFeel != null) {
            oldLookAndFeel.uninitialize();
        }

        getLAFState().lookAndFeel = newLookAndFeel;
        if (newLookAndFeel != null) {
            newLookAndFeel.initialize();
            getLAFState().setLookAndFeelDefaults(newLookAndFeel.getDefaults());
        }
        else {
            getLAFState().setLookAndFeelDefaults(null);
        }

        getLAFState().changeSupport.firePropertyChange("lookAndFeel", oldLookAndFeel, newLookAndFeel);
    }

    
    /**
     * Set the current default look and feel using a class name.
     *
     * @param className  a string specifying the name of the class that implements
     *        the look and feel
     * @exception ClassNotFoundException If the LookAndFeel class could not be found.
     * @exception InstantiationException If a new instance of the class couldn't be creatd.
     * @exception IllegalAccessException If the class or initializer isn't accessible. 
     * @exception UnsupportedLookAndFeelException If <code>lnf.isSupportedLookAndFeel()</code> is false.
     */
    public static void setLookAndFeel(String className) 
        throws ClassNotFoundException, 
               InstantiationException, 
               IllegalAccessException,
               UnsupportedLookAndFeelException 
    {
            Class lnfClass = SwingUtilities.loadSystemClass(className);
            setLookAndFeel((LookAndFeel)(lnfClass.newInstance()));
    }


    /**
     * Returns the name of the LookAndFeel class that implements
     * the native systems look and feel if there is one,
     * otherwise the name of the default cross platform LookAndFeel
     * class.
     * 
     * @see #setLookAndFeel
     * @see #getCrossPlatformLookAndFeelClassName
     */
    public static String getSystemLookAndFeelClassName() {
        final String osName[] = new String[]{""};
	SwingUtilities.doPrivileged(new Runnable() {
	    public void run() {
		osName[0] = System.getProperty("os.name");
	    }
	});

        if (osName[0] != null) {
            if (osName[0].indexOf("Windows") != -1) {
                return "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
            }
            else if ((osName[0].indexOf("Solaris") != -1) || 
		     (osName[0].indexOf("SunOS") != -1)) {
                return "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
            } 
	    else if (osName[0].indexOf("Mac") != -1 ) {
                return "com.sun.java.swing.plaf.mac.MacLookAndFeel";
            }
        }
        return getCrossPlatformLookAndFeelClassName();
    }


    /**
     * Returns the name of the LookAndFeel class that implements
     * the default cross platform look and feel -- the Java
     * Look and Feel (JLF).
     * 
     * @return  a string with the JLF implementation-class
     * @see #setLookAndFeel
     * @see #getSystemLookAndFeelClassName
     */
    public static String getCrossPlatformLookAndFeelClassName() {
        return "javax.swing.plaf.metal.MetalLookAndFeel";
    }


    /**
     * Returns the default values for this look and feel.
     *
     * @return an UIDefaults object containing the default values
     */
    public static UIDefaults getDefaults() {
        maybeInitialize();
        return getLAFState().multiUIDefaults;
    }
    
    /**
     * Returns a drawing font from the defaults table.
     *
     * @param key  an Object specifying the font
     * @return the Font object
     */
    public static Font getFont(Object key) { 
        return getDefaults().getFont(key); 
    }

    /**
     * Returns a drawing color from the defaults table.
     *
     * @param key  an Object specifying the color
     * @return the Color object
     */
    public static Color getColor(Object key) { 
        return getDefaults().getColor(key); 
    }

    /**
     * Returns an Icon from the defaults table.
     *
     * @param key  an Object specifying the icon
     * @return the Icon object
     */
    public static Icon getIcon(Object key) { 
        return getDefaults().getIcon(key); 
    }

    /**
     * Returns a border from the defaults table.
     *
     * @param key  an Object specifying the border
     * @return the Border object
     */
    public static Border getBorder(Object key) { 
        return getDefaults().getBorder(key); 
    }

    /**
     * Returns a string from the defaults table.
     *
     * @param key  an Object specifying the string
     * @return the String
     */
    public static String getString(Object key) { 
        return getDefaults().getString(key); 
    }

    /**
     * Returns an int from the defaults table.
     *
     * @param key  an Object specifying the int
     * @return the int
     */
    public static int getInt(Object key) {
        return getDefaults().getInt(key);
    }

    /**
     * Returns an Insets object from the defaults table.
     *
     * @param key  an Object specifying the Insets object
     * @return the Insets object
     */
    public static Insets getInsets(Object key) {
        return getDefaults().getInsets(key);
    }

    /**
     * Returns a dimension from the defaults table.
     *
     * @param key  an Object specifying the dimension object
     * @return the Dimension object
     */
    public static Dimension getDimension(Object key) {
        return getDefaults().getDimension(key);
    }

    /**
     * Returns an object from the defaults table.
     *
     * @param key  an Object specifying the desired object
     * @return the Object
     */
    public static Object get(Object key) { 
        return getDefaults().get(key); 
    }

    /**
     * Stores an object in the defaults table.
     *
     * @param key    an Object specifying the retrieval key
     * @param value  the Object to store
     * @return the Object returned by {@link UIDefaults#put}
     */
    public static Object put(Object key, Object value) { 
        return getDefaults().put(key, value); 
    }

    /**
     * Returns the L&F object that renders the target component.
     *
     * @param target  the JComponent to render
     * @return the ComponentUI object that renders the target component
     */
    public static ComponentUI getUI(JComponent target) {
        maybeInitialize();
        ComponentUI ui = null;
        LookAndFeel multiLAF = getLAFState().multiLookAndFeel;
        if (multiLAF != null) {
            // This can return null if the multiplexing look and feel
            // doesn't support a particular UI.
            ui = multiLAF.getDefaults().getUI(target);
        }
        if (ui == null) {
            ui = getDefaults().getUI(target);
        }
        return ui;
    }


    /**
     * Returns the default values for this look and feel.
     *
     * @return an UIDefaults object containing the default values
     */
    public static UIDefaults getLookAndFeelDefaults() {
        maybeInitialize();
        return getLAFState().getLookAndFeelDefaults();
    }

    /**
     * Find the Multiplexing LookAndFeel.
     */
    private static LookAndFeel getMultiLookAndFeel() {
	LookAndFeel multiLookAndFeel = getLAFState().multiLookAndFeel;
	if (multiLookAndFeel == null) {
            String defaultName = "javax.swing.plaf.multi.MultiLookAndFeel";
            String className = getLAFState().swingProps.getProperty(multiplexingLAFKey, defaultName);
            try {
                Class lnfClass = SwingUtilities.loadSystemClass(className);
                multiLookAndFeel = (LookAndFeel)lnfClass.newInstance();
            } catch (Exception exc) {
                System.err.println("UIManager: failed loading " + className);
            }
	}
	return multiLookAndFeel;
    }

    /**
     * Add a LookAndFeel to the list of auxiliary look and feels.  The
     * auxiliary look and feels tell the multiplexing look and feel what
     * other LookAndFeel classes for a component instance are to be used 
     * in addition to the default LookAndFeel class when creating a 
     * multiplexing UI.  The change will only take effect when a new
     * UI class is created or when the default look and feel is changed
     * on a component instance.
     * <p>Note these are not the same as the installed look and feels.
     *
     * @param laf the LookAndFeel object
     * @see #removeAuxiliaryLookAndFeel
     * @see #setLookAndFeel
     * @see #getAuxiliaryLookAndFeels
     * @see #getInstalledLookAndFeels
     */
    static public void addAuxiliaryLookAndFeel(LookAndFeel laf) {
        maybeInitialize();

        Vector v = getLAFState().auxLookAndFeels;
        if (v == null) {
            v = new Vector();
        } 

	if (!v.contains(laf)) {
	    v.addElement(laf);
	    laf.initialize();
            getLAFState().auxLookAndFeels = v;

	    if (getLAFState().multiLookAndFeel == null) {
	        getLAFState().multiLookAndFeel = getMultiLookAndFeel();
            }
	}
    }

    /**
     * Remove a LookAndFeel from the list of auxiliary look and feels.  The
     * auxiliary look and feels tell the multiplexing look and feel what
     * other LookAndFeel classes for a component instance are to be used 
     * in addition to the default LookAndFeel class when creating a 
     * multiplexing UI.  The change will only take effect when a new
     * UI class is created or when the default look and feel is changed
     * on a component instance.
     * <p>Note these are not the same as the installed look and feels.
     * @return true if the LookAndFeel was removed from the list
     * @see #removeAuxiliaryLookAndFeel
     * @see #getAuxiliaryLookAndFeels
     * @see #setLookAndFeel
     * @see #getInstalledLookAndFeels
     */
    static public boolean removeAuxiliaryLookAndFeel(LookAndFeel laf) {
        maybeInitialize();

	boolean result;

        Vector v = getLAFState().auxLookAndFeels;
        if ((v == null) || (v.size() == 0)) {
            return false;
        } 
	
	result = v.removeElement(laf);
	if (result) {
	    if (v.size() == 0) {
	        getLAFState().auxLookAndFeels = null;
	        getLAFState().multiLookAndFeel = null;
	    } else {
	        getLAFState().auxLookAndFeels = v;
            }
        }
	laf.uninitialize();

	return result;
    }

    /**
     * Return the list of auxiliary look and feels (can be null).  The
     * auxiliary look and feels tell the multiplexing look and feel what
     * other LookAndFeel classes for a component instance are to be used 
     * in addition to the default LookAndFeel class when creating a 
     * multiplexing UI.  
     * <p>Note these are not the same as the installed look and feels.
     * @see #addAuxiliaryLookAndFeel
     * @see #removeAuxiliaryLookAndFeel
     * @see #setLookAndFeel
     * @see #getInstalledLookAndFeels
     */
    static public LookAndFeel[] getAuxiliaryLookAndFeels() {
        maybeInitialize();

        Vector v = getLAFState().auxLookAndFeels;
        if ((v == null) || (v.size() == 0)) {
            return null;
        } 
        else {
            LookAndFeel[] rv = new LookAndFeel[v.size()];
            for (int i = 0; i < rv.length; i++) {
                rv[i] = (LookAndFeel)v.elementAt(i);
            }
            return rv;
        }
    }


    /**
     * Add a PropertyChangeListener to the listener list.
     * The listener is registered for all properties.
     *
     * @param listener  The PropertyChangeListener to be added
     * @see java.beans.PropertyChangeSupport
     */
    public static void addPropertyChangeListener(PropertyChangeListener listener) 
    {
	synchronized (classLock) {
	    getLAFState().changeSupport.addPropertyChangeListener(listener);
	}
    }


    /**
     * Remove a PropertyChangeListener from the listener list.
     * This removes a PropertyChangeListener that was registered
     * for all properties.
     *
     * @param listener  The PropertyChangeListener to be removed
     * @see java.beans.PropertyChangeSupport
     */
    public static void removePropertyChangeListener(PropertyChangeListener listener) 
    {
        synchronized (classLock) {
	    getLAFState().changeSupport.removePropertyChangeListener(listener);
	}
    }


    private static Properties loadSwingProperties()
    {
	/* Don't bother checking for Swing properties if untrusted, as
	 * there's no way to look them up without triggering SecurityExceptions.
	 */
        if (UIManager.class.getClassLoader() != null) {
	    return new Properties();
	}
	else {
	    final Properties props = new Properties();

	    SwingUtilities.doPrivileged(new Runnable() {
		public void run() {
		    try {
			File file = new File(makeSwingPropertiesFilename());
			// InputStream has been buffered in Properties class
			FileInputStream ins = new FileInputStream(file);
			props.load(ins);
			ins.close();
		    } 
		    catch (Exception e) {
			// No such file, or file is otherwise non-readable.
		    }

		    // Check whether any properties were overridden at the
		    // command line.
		    checkProperty(props, defaultLAFKey);
		    checkProperty(props, auxiliaryLAFsKey);
		    checkProperty(props, multiplexingLAFKey);
		    checkProperty(props, installedLAFsKey);
		}
	    });
	    return props;
	}
    }

    private static void checkProperty(Properties props, String key) {
	try {
	    String value = System.getProperty(key);
	    if (value != null) {
		props.put(key, value);
	    }
	} catch (SecurityException e) {
	    // If system won't give us a property, we don't want it!
	}
    }


    /**
     * If a swing.properties file exist and it has a swing.installedlafs property
     * then initialize the installedLAFs field.
     * 
     * @see #getInstalledLookAndFeels
     */
    private static void initializeInstalledLAFs(Properties swingProps) 
    {
        String ilafsString = swingProps.getProperty(installedLAFsKey);
        if (ilafsString == null) {
            return;
        }

        /* Create a vector that contains the value of the swing.installedlafs
         * property.  For example given "swing.installedlafs=motif,windows"
         * lafs = {"motif", "windows"}.
         */
        Vector lafs = new Vector();
        StringTokenizer st = new StringTokenizer(ilafsString, ",", false);
        while (st.hasMoreTokens()) {
            lafs.addElement(st.nextToken());
        }

        /* Look up the name and class for each name in the "swing.installedlafs"
         * list.  If they both exist then add a LookAndFeelInfo to 
         * the installedLafs array.
         */
        Vector ilafs = new Vector(lafs.size());
        for(int i = 0; i < lafs.size(); i++) {
            String laf = (String)lafs.elementAt(i);
            String name = swingProps.getProperty(makeInstalledLAFKey(laf, "name"), laf);
            String cls = swingProps.getProperty(makeInstalledLAFKey(laf, "class"));
            if (cls != null) {
                ilafs.addElement(new LookAndFeelInfo(name, cls));
            }
        }

        installedLAFs = new LookAndFeelInfo[ilafs.size()];
        for(int i = 0; i < ilafs.size(); i++) {
            installedLAFs[i] = (LookAndFeelInfo)(ilafs.elementAt(i));
        }
    }


    /**
     * If the user has specified a default look and feel, use that.  
     * Otherwise use the look and feel that's native to this platform.
     * If this code is called after the application has expclicitly
     * set it's look and feel, do nothing.
     *
     * @see #maybeInitialize
     */
    private static void initializeDefaultLAF(Properties swingProps)
    {
        if (getLAFState().lookAndFeel != null) {
            return;
        }

        String metalLnf = getCrossPlatformLookAndFeelClassName();
        String lnfDefault = metalLnf;

        String lnfName = "<undefined>" ;
        try {
            lnfName = swingProps.getProperty(defaultLAFKey, lnfDefault);
            setLookAndFeel(lnfName);
        } catch (Exception e) {
            try {
                lnfName = swingProps.getProperty(defaultLAFKey, metalLnf);
                setLookAndFeel(lnfName);
            } catch (Exception e2) {
                throw new Error("can't load " + lnfName);
            }
        }
    }


    private static void initializeAuxiliaryLAFs(Properties swingProps)
    {
        String auxLookAndFeelNames = swingProps.getProperty(auxiliaryLAFsKey);
        if (auxLookAndFeelNames == null) {
            return;
        }

        Vector auxLookAndFeels = new Vector();

        StringTokenizer p = new StringTokenizer(auxLookAndFeelNames,",");
        String factoryName;

        /* Try to load each LookAndFeel subclass in the list.
         */

        while (p.hasMoreTokens()) {
            String className = p.nextToken();
            try {
                Class lnfClass = SwingUtilities.loadSystemClass(className);
		LookAndFeel newLAF = (LookAndFeel)lnfClass.newInstance();
		newLAF.initialize();
                auxLookAndFeels.addElement(newLAF);
            } 
            catch (Exception e) {
                System.err.println("UIManager: failed loading auxiliary look and feel " + className);
            }
        }

        /* If there were problems and no auxiliary look and feels were 
         * loaded, make sure we reset auxLookAndFeels to null.
         * Otherwise, we are going to use the MultiLookAndFeel to get
         * all component UI's, so we need to load it now.
         */
        if (auxLookAndFeels.size() == 0) {
            auxLookAndFeels = null;
        } 
        else {
	    getLAFState().multiLookAndFeel = getMultiLookAndFeel();
	    if (getLAFState().multiLookAndFeel == null) {
                auxLookAndFeels = null;
	    }
        }

        getLAFState().auxLookAndFeels = auxLookAndFeels;
    }


    private static void initializeSystemDefaults(Properties swingProps) {
        Object defaults[] = {
            "FocusManagerClassName", "javax.swing.DefaultFocusManager"
        };
        getLAFState().setSystemDefaults(new UIDefaults(defaults));
	getLAFState().swingProps = swingProps;
    }


    /* 
     * This method is called before any code that depends on the 
     * AppContext specific LAFState object runs.  When the AppContext
     * corresponds to a set of applets it's possible for this method 
     * to be re-entered, which is why we grab a lock before calling
     * initialize().
     */
    private static void maybeInitialize() {
	synchronized (classLock) {
	    if (!getLAFState().initialized) {
		getLAFState().initialized = true;
		initialize();
	    }
        }
    }


    /*
     * Only called by maybeInitialize().
     */
    private static void initialize() {
        Properties swingProps = loadSwingProperties();
	try {
	    // We discourage the JIT during UI initialization.
	    // JITing here tends to be counter-productive.
	    java.lang.Compiler.disable();

            initializeSystemDefaults(swingProps);
            initializeDefaultLAF(swingProps);
            initializeAuxiliaryLAFs(swingProps);
            initializeInstalledLAFs(swingProps);

	} 
	finally {
	    // Make sure to always re-enable the JIT.
	    java.lang.Compiler.enable();
	}
    }
}

