/*
 * @(#)PropertyEditorManager.java	1.27 98/07/01
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

/**
 * The PropertyEditorManager can be used to locate a property editor for
 * any given type name.  This property editor must support the
 * java.beans.PropertyEditor interface for editing a given object.
 * <P>
 * The PropertyEditorManager uses three techniques for locating an editor
 * for a given type.  First, it provides a registerEditor method to allow
 * an editor to be specifically registered for a given type.  Second it
 * tries to locate a suitable class by adding "Editor" to the full 
 * qualified classname of the given type (e.g. "foo.bah.FozEditor").
 * Finally it takes the simple classname (without the package name) adds
 * "Editor" to it and looks in a search-path of packages for a matching
 * class.
 * <P>
 * So for an input class foo.bah.Fred, the PropertyEditorManager would
 * first look in its tables to see if an editor had been registered for
 * foo.bah.Fred and if so use that.  Then it will look for a
 * foo.bah.FredEditor class.  Then it will look for (say) 
 * standardEditorsPackage.FredEditor class.
 * <p>
 * Default PropertyEditors will be provided for the Java builtin types
 * "boolean", "byte", "short", "int", "long", "float", and "double"; and
 * for the classes java.lang.String. java.awt.Color, and java.awt.Font.
 */

public class PropertyEditorManager {

    /**
     * Register an editor class to be used to editor values of
     * a given target class.
     * @param targetType the Class object of the type to be edited
     * @param editorClass the Class object of the editor class.  If
     *	   this is null, then any existing definition will be removed.
     */

    public static void registerEditor(Class targetType, Class editorClass) {
	initialize();
	if (editorClass == null) {
	    registry.remove(targetType);
	} else {
	    registry.put(targetType, editorClass);
	}
    }

    /**
     * Locate a value editor for a given target type.
     * @param targetType  The Class object for the type to be edited
     * @return An editor object for the given target class. 
     * The result is null if no suitable editor can be found.
     */

    public static PropertyEditor findEditor(Class targetType) {
	initialize();
	Class editorClass = (Class)registry.get(targetType);
	if (editorClass != null) {
	    try {
		Object o = editorClass.newInstance();
        	return (PropertyEditor)o;
	    } catch (Exception ex) {
	 	System.err.println("Couldn't instantiate type editor \"" +
			editorClass.getName() + "\" : " + ex);
	    }
	}

	// Now try adding "Editor" to the class name.

	String editorName = targetType.getName() + "Editor";
	try {
	    return instantiate(targetType, editorName);
	} catch (Exception ex) {
	   // Silently ignore any errors.
	}

	// Now try looking for <searchPath>.fooEditor
	editorName = targetType.getName();
   	while (editorName.indexOf('.') > 0) {
	    editorName = editorName.substring(editorName.indexOf('.')+1);
	}
	for (int i = 0; i < searchPath.length; i++) {
	    String name = searchPath[i] + "." + editorName + "Editor";
	    try {
	        return instantiate(targetType, name);
	    } catch (Exception ex) {
	       // Silently ignore any errors.
	    }
	}

	// We couldn't find a suitable Editor.
	return (null);
    }

    private static PropertyEditor instantiate(Class sibling, String className)
		 throws InstantiationException, IllegalAccessException,
						ClassNotFoundException {
	// First check with siblings classloader (if any). 
	ClassLoader cl = sibling.getClassLoader();
	if (cl != null) {
	    try {
	        Class cls = cl.loadClass(className);
	    	Object o = cls.newInstance();
	        PropertyEditor ed = (PropertyEditor)o;
		return ed;
	    } catch (Exception ex) {
	        // Just drop through
	    }
        }
	// Now try the system classloader.
	Class cls = Class.forName(className);
	Object o = cls.newInstance();
        PropertyEditor ed = (PropertyEditor)o;
	return ed;
    }

    /**
     * @return  The array of package names that will be searched in
     *		order to find property editors.
     * <p>     This is initially set to {"sun.beans.editors"}.
     */

    public static String[] getEditorSearchPath() {
	return searchPath;
    }

    /**
     * Change the list of package names that will be used for
     *		finding property editors.
     * @param path  Array of package names.
     */

    public static void setEditorSearchPath(String path[]) {
	if (path == null) {
	    path = new String[0];
	}
	searchPath = path;
    }

    private static void load(Class targetType, String name) {
	String editorName = name;
	for (int i = 0; i < searchPath.length; i++) {
	    try {
	        editorName = searchPath[i] + "." + name;
	        Class cls = Class.forName(editorName);
	        registry.put(targetType, cls);
		return;
	    } catch (Exception ex) {
		// Drop through and try next package.
	    }
	}
	// This shouldn't happen.
	System.err.println("load of " + editorName + " failed");
    }


    private static synchronized void initialize() {
	if (registry != null) {
	    return;
	}
	registry = new java.util.Hashtable();
	load(Byte.TYPE, "ByteEditor");
	load(Short.TYPE, "ShortEditor");
	load(Integer.TYPE, "IntEditor");
	load(Long.TYPE ,"LongEditor");
	load(Boolean.TYPE, "BoolEditor");
	load(Float.TYPE, "FloatEditor");
	load(Double.TYPE, "DoubleEditor");
    }

    private static String[] searchPath = { "sun.beans.editors" };
    private static java.util.Hashtable registry;
}
