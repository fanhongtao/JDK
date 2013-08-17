/*
 * @(#)Beans.java	1.31 98/07/01
 *
 * Copyright 1996-1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package java.beans;

import java.io.*;
import java.awt.*;
import java.applet.*;
import java.net.URL;
import java.lang.reflect.Array;

/**
 * This class provides some general purpose beans control methods.
 */

public class Beans {

    /**
     * Instantiate a bean.
     * <p>
     * The bean is created based on a name relative to a class-loader.
     * This name should be a dot-separated name such as "a.b.c".
     * <p>
     * In Beans 1.0 the given name can indicate either a serialized object
     * or a class.  Other mechanisms may be added in the future.  In
     * beans 1.0 we first try to treat the beanName as a serialized object
     * name then as a class name.
     * <p>
     * When using the beanName as a serialized object name we convert the
     * given beanName to a resource pathname and add a trailing ".ser" suffix.
     * We then try to load a serialized object from that resource.
     * <p>
     * For example, given a beanName of "x.y", Beans.instantiate would first
     * try to read a serialized object from the resource "x/y.ser" and if
     * that failed it would try to load the class "x.y" and create an
     * instance of that class.
     * <p>
     * If the bean is a subtype of java.applet.Applet, then it is given
     * some special initialization.  First, it is supplied with a default
     * AppletStub and AppletContext.  Second, if it was instantiated from 
     * a classname the applet's "init" method is called.  (If the bean was
     * deserialized this step is skipped.) 
     * <p>
     * Note that for beans which are applets, it is the caller's responsiblity
     * to call "start" on the applet.  For correct behaviour, this should be done
     * after the applet has been added into a visible AWT container.
     * <p>
     * Note that applets created via beans.instantiate run in a slightly
     * different environment than applets running inside browsers.  In
     * particular, bean applets have no access to "parameters", so they may 
     * wish to provide property get/set methods to set parameter values.  We
     * advise bean-applet developers to test their bean-applets against both
     * the JDK appletviewer (for a reference browser environment) and the
     * BDK BeanBox (for a reference bean container).
     * 
     * @param     classLoader the class-loader from which we should create
     * 		              the bean.  If this is null, then the system
     *                        class-loader is used.
     * @param     beanName    the name of the bean within the class-loader.
     *   	              For example "sun.beanbox.foobah"
     * @exception java.lang.ClassNotFoundException if the class of a serialized
     *              object could not be found.
     * @exception java.io.IOException if an I/O error occurs.
     */
    public static Object instantiate(ClassLoader cls, String beanName) 
			throws java.io.IOException, ClassNotFoundException {

	java.io.InputStream ins;
	java.io.ObjectInputStream oins = null;
	Object result = null;
	boolean serialized = false;

	// Try to find a serialized object with this name
	String serName = beanName.replace('.','/').concat(".ser");
	if (cls == null) {
	    ins = ClassLoader.getSystemResourceAsStream(serName);
	} else {
	    ins  = cls.getResourceAsStream(serName);
	}
	if (ins != null) {
	    try {
	        if (cls == null) {
		    oins = new ObjectInputStream(ins);
	        } else {
		    oins = new ObjectInputStreamWithLoader(ins, cls);
	        }
	        result = oins.readObject();
		serialized = true;
	        oins.close();
	    } catch (java.io.IOException ex) {
		ins.close();
		// For now, drop through and try opening the class.
		// throw ex;
	    } catch (ClassNotFoundException ex) {
		ins.close();
		throw ex;
	    }
	}

	if (result == null) {
	    // No serialized object, try just instantiating the class
	    Class cl;
	    if (cls == null) {
	        cl = Class.forName(beanName);
	    } else {
	        cl = cls.loadClass(beanName);
	    }
	    try {
	    	result = cl.newInstance();
	    } catch (Exception ex) {
	        throw new ClassNotFoundException();
	    }
	}
	// Ok, if the result is an applet initialize it.
	if (result != null && result instanceof Applet) {
	    Applet applet = (Applet) result;

	    // Figure our the codebase and docbase URLs.  We do this
	    // by locating the URL for a known resource, and then
	    // massaging the URL.

	    // First find the "resource name" corresponding to the bean
	    // itself.  So a serialzied bean "a.b.c" would imply a resource
	    // name of "a/b/c.ser" and a classname of "x.y" would imply
	    // a resource name of "x/y.class".

	    String resourceName;
	    if (serialized) {
		// Serialized bean
		resourceName = beanName.replace('.','/').concat(".ser");
	    } else {
		// Regular class
		resourceName = beanName.replace('.','/').concat(".class");
	    }
	    URL objectUrl = null;
	    URL codeBase = null;
	    URL docBase = null;

	    // Now get the URL correponding to the resource name.
	    if (cls == null) {
		objectUrl = ClassLoader.getSystemResource(resourceName);
	    } else {
		objectUrl = cls.getResource(resourceName);
	    }

	    // If we found a URL, we try to locate the docbase by taking
	    // of the final path name component, and the code base by taking
   	    // of the complete resourceName.
	    // So if we had a resourceName of "a/b/c.class" and we got an
	    // objectURL of "file://bert/classes/a/b/c.class" then we would
	    // want to set the codebase to "file://bert/classes/" and the
	    // docbase to "file://bert/classes/a/b/"

	    if (objectUrl != null) {
		String s = objectUrl.toExternalForm();
		if (s.endsWith(resourceName)) {
  		    int ix = s.length() - resourceName.length();
		    codeBase = new URL(s.substring(0,ix));
		    docBase = codeBase;
		    ix = s.lastIndexOf('/');
		    if (ix >= 0) {
		        docBase = new URL(s.substring(0,ix+1));
		    }
		}
	    }
	    	    
	    // Setup a default context and stub.
	    BeansAppletContext context = new BeansAppletContext(applet);
	    BeansAppletStub stub = new BeansAppletStub(applet, context, codeBase, docBase);
	    applet.setStub(stub);

	    // If it was deserialized then it was already init-ed.  Otherwise
	    // we need to initialize it.
	    if (!serialized) {
		// We need to set a reasonable initial size, as many
		// applets are unhappy if they are started without 
		// having been explicitly sized.
		applet.setSize(100,100);
		applet.init();
	    }
	    stub.active = true;
	}
	return result;
    }


    /**
     * From a given bean, obtain an object representing a specified
     * type view of that source object. 
     * <p>
     * The result may be the same object or a different object.  If
     * the requested target view isn't available then the given
     * bean is returned.
     * <p>
     * This method is provided in Beans 1.0 as a hook to allow the
     * addition of more flexible bean behaviour in the future.
     *
     * @param obj  Object from which we want to obtain a view.
     * @param targetType  The type of view we'd like to get.
     *
     */
    public static Object getInstanceOf(Object bean, Class targetType) {
    	return bean;
    }
 
    /**
     * Check if a bean can be viewed as a given target type.
     * The result will be true if the Beans.getInstanceof method
     * can be used on the given bean to obtain an object that
     * represents the specified targetType type view.
     *
     * @param bean  Bean from which we want to obtain a view.
     * @param targetType  The type of view we'd like to get.
     * @return "true" if the given bean supports the given targetType.
     */
    public static boolean isInstanceOf(Object bean, Class targetType) {
	return Introspector.isSubclass(bean.getClass(), targetType);
    }


    /**
     * Test if we are in design-mode.
     *
     * @return  True if we are running in an application construction
     *		environment.
     */
    public static boolean isDesignTime() {
	return designTime;
    }

    /**
     * @return  True if we are running in an environment where beans
     *	   can assume that an interactive GUI is available, so they 
     *	   can pop up dialog boxes, etc.  This will normally return
     *	   true in a windowing environment, and will normally return
     *	   false in a server environment or if an application is
     *	   running as part of a batch job.
     */
    public static boolean isGuiAvailable() {
	return guiAvailable;
    }

    /**
     * Used to indicate whether of not we are running in an application
     * builder environment.  Note that this method is security checked
     * and is not available to (for example) untrusted applets.
     *
     * @param isDesignTime  True if we're in an application builder tool.
     */

    public static void setDesignTime(boolean isDesignTime)
			throws SecurityException {
	designTime = isDesignTime;
    }

    /**
     * Used to indicate whether of not we are running in an environment
     * where GUI interaction is available.  Note that this method is 
     * security checked and is not available to (for example) untrusted
     * applets.
     *
     * @param isGuiAvailable  True if GUI interaction is available.
     */

    public static void setGuiAvailable(boolean isGuiAvailable)
			throws SecurityException {
	guiAvailable = isGuiAvailable;
    }


    private static boolean designTime;
    private static boolean guiAvailable = true;
}

/**
 * This subclass of ObjectInputStream delegates loading of classes to
 * an existing ClassLoader.
 */

class ObjectInputStreamWithLoader extends ObjectInputStream
{
    private ClassLoader loader;

    /**
     * Loader must be non-null;
     */

    public ObjectInputStreamWithLoader(InputStream in, ClassLoader loader)
	    throws IOException, StreamCorruptedException {

	super(in);
	if (loader == null) {
            throw new IllegalArgumentException("Illegal null argument to ObjectInputStreamWithLoader");
	}
	this.loader = loader;
    }

    /**
     * Make a primitive array class
     */

    private Class primitiveType(char type) {
	switch (type) {
	case 'B': return byte.class;
        case 'C': return char.class;
	case 'D': return double.class;
	case 'F': return float.class;
	case 'I': return int.class;
	case 'J': return long.class;
	case 'S': return short.class;
	case 'Z': return boolean.class;
	default: return null;
	}
    }

    /**
     * Use the given ClassLoader rather than using the system class
     */
    protected Class resolveClass(ObjectStreamClass classDesc)
	throws IOException, ClassNotFoundException {

	String cname = classDesc.getName();
	if (cname.startsWith("[")) {
	    // An array
	    Class component;		// component class
	    int dcount;			// dimension
	    for (dcount=1; cname.charAt(dcount)=='['; dcount++) ;
	    if (cname.charAt(dcount) == 'L') {
		component = loader.loadClass(cname.substring(dcount+1,
							     cname.length()-1));
	    } else {
		if (cname.length() != dcount+1) {
		    throw new ClassNotFoundException(cname);// malformed
		}
		component = primitiveType(cname.charAt(dcount));
	    }
	    int dim[] = new int[dcount];
	    for (int i=0; i<dcount; i++) {
		dim[i]=0;
	    }
	    return Array.newInstance(component, dim).getClass();
	} else {
	    return loader.loadClass(cname);
	}
    }
}

/**
 * Package private support class.  This provides a default AppletContext
 * for beans which are applets.
 */

class BeansAppletContext implements AppletContext {
    Applet target;
    java.util.Hashtable imageCache = new java.util.Hashtable();

    BeansAppletContext(Applet target) {
        this.target = target;
    }

    public AudioClip getAudioClip(URL url) {
	// We don't currently support audio clips in the Beans.instantiate
	// applet context, unless by some luck there exists a URL content
	// class that can generate an AudioClip from the audio URL.
	try {
	    return (AudioClip) url.getContent();
  	} catch (Exception ex) {
	    return null;
	}
    }

    public synchronized Image getImage(URL url) {
	Object o = imageCache.get(url);
	if (o != null) {
	    return (Image)o;
	}
	try {
	    o = url.getContent();
	    if (o == null) {
		return null;
	    }
	    if (o instanceof Image) {
		imageCache.put(url, o);
		return (Image) o;
	    }
	    // Otherwise it must be an ImageProducer.
	    Image img = target.createImage((java.awt.image.ImageProducer)o);
	    imageCache.put(url, img);
	    return img;

  	} catch (Exception ex) {
	    return null;
	}
    }

    public Applet getApplet(String name) {
	return null;
    }

    public java.util.Enumeration getApplets() {
	java.util.Vector applets = new java.util.Vector();
	applets.addElement(target);
	return applets.elements();	
    }

    public void showDocument(URL url) {
	// We do nothing.
    }

    public void showDocument(URL url, String target) {
	// We do nothing.
    }

    public void showStatus(String status) {
	// We do nothing.
    }
}

/**
 * Package private support class.  This provides an AppletStub
 * for beans which are applets.
 */
class BeansAppletStub implements AppletStub {
    transient boolean active;
    transient Applet target;
    transient AppletContext context;
    transient URL codeBase;
    transient URL docBase;

    BeansAppletStub(Applet target,
		AppletContext context, URL codeBase, 
				URL docBase) {
        this.target = target;
	this.context = context;
	this.codeBase = codeBase;
	this.docBase = docBase;
    }

    public boolean isActive() {
	return active;
    }
    
    public URL getDocumentBase() {
	// use the root directory of the applet's class-loader
	return docBase;
    }

    public URL getCodeBase() {
	// use the directory where we found the class or serialized object.
	return codeBase;
    }

    public String getParameter(String name) {
	return null;
    }

    public AppletContext getAppletContext() {
	return context;
    }

    public void appletResize(int width, int height) {
	// we do nothing.
    }
}
