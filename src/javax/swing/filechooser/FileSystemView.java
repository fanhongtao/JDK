/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.filechooser;


import javax.swing.event.*;
import javax.swing.*;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Vector;

import java.lang.reflect.*;

/**
 * FileSystemView is JFileChooser's gateway to the
 * file system. Since the JDK1.1 File api doesn't allow
 * access to such information as root partitians, file type
 * information, or hidden file bits, this class is designed
 * to intuit as much OS specific file system information as
 * possible.
 *
 * FileSystemView will eventually delegate its responsibilities
 * to io File classes when JDK1.X provides more direct access to
 * file system information.
 *
 * Java Licenses may want to provide a different implemenation of
 * FileSystemView to better handle a given operation system.
 *
 * PENDING(jeff) - need to provide a specification for
 * how Mac/OS2/BeOS/etc file systems can modify FileSystemView
 * to handle their particular type of file system.
 *
 * @version 1.16 02/06/02
 * @author Jeff Dinkins
 */
public abstract class FileSystemView {

    static FileSystemView windowsFileSystemView = null;
    static FileSystemView unixFileSystemView = null;
    //static FileSystemView macFileSystemView = null;
    static FileSystemView genericFileSystemView = null;

    public static FileSystemView getFileSystemView() {
	if(File.separatorChar == '\\') {
	    if(windowsFileSystemView == null) {
		windowsFileSystemView = new WindowsFileSystemView();
	    }
	    return windowsFileSystemView;
	}

	if(File.separatorChar == '/') {
	    if(unixFileSystemView == null) {
		unixFileSystemView = new UnixFileSystemView();
	    }
	    return unixFileSystemView;
	}

	// if(File.separatorChar == ':') {
	//    if(macFileSystemView == null) {
	//	macFileSystemView = new MacFileSystemView();
	//    }
	//    return macFileSystemView;
	//}

	if(genericFileSystemView == null) {
	    genericFileSystemView = new GenericFileSystemView();
	}
	return genericFileSystemView;
    }

    /**
     * Determines if the given file is a root partition or drive.
     */
    public abstract boolean isRoot(File f);

    /**
     * creates a new folder with a default folder name.
     */
    public abstract File createNewFolder(File containingDir) throws IOException;

    /**
     * Returns whether a file is hidden or not.
     */
    public abstract boolean isHiddenFile(File f);


    /**
     * Returns all root partitians on this system. For example, on Windows,
     * this would be the A: through Z: drives.
     */
    public abstract File[] getRoots();


    // Providing default implemenations for the remaining methods
    // because most OS file systems will likely be able to use this
    // code. If a given OS can't, override these methods in its
    // implementation.

    public File getHomeDirectory() {
	return createFileObject(System.getProperty("user.home"));
    }

    /**
     * Returns a File object constructed in dir from the given filename.
     */
    public File createFileObject(File dir, String filename) {
	if(dir == null) {
	    return new File(filename);
	} else {
	    return new File(dir, filename);
	}
    }

    /**
     * Returns a File object constructed from the given path string.
     */
    public File createFileObject(String path) {
	return new File(path);
    }


    /**
     * gets the list of shown (i.e. not hidden) files
     */
    public File[] getFiles(File dir, boolean useFileHiding) {
	Vector files = new Vector();

	// add all files in dir
	File [] names = dir.listFiles();

	File f;

	int nameCount = names == null ? 0 : names.length;

	for (int i = 0; i < nameCount; i++) {
	    f = names[i];

	    if (f.isFile() || f.isDirectory()) {
	      if (!useFileHiding || !isHiddenFile(f)) {
		files.addElement(f);
	      }
	    }
	}

	return (File[])files.toArray(new File[files.size()]);
    }

    /**
     * Returns the parent directory of dir.
     */
    public File getParentDirectory(File dir) {
	if(dir != null) {
	    File f = createFileObject(dir.getAbsolutePath());
	    String parentFilename = f.getParent();
	    if(parentFilename != null) {
		return new File(parentFilename);
	    }
	}
	return null;
    }
}

/**
 * FileSystemView that handles some specific unix-isms.
 */
class UnixFileSystemView extends FileSystemView {

    private static final Object[] noArgs = {};
    private static final Class[] noArgTypes = {};
    private static final String newFolderString =
            UIManager.getString("FileChooser.other.newFolder");
    private static final String newFolderNextString  =
            UIManager.getString("FileChooser.other.newFolder.subsequent");

    private static Method listRootsMethod = null;
    private static boolean listRootsMethodChecked = false;

    public boolean isRoot(File f) {
	String path = f.getAbsolutePath();
	if(path.length() == 1 && path.charAt(0) == '/') {
	    return true;
	}
	return false;
    }

    /**
     * creates a new folder with a default folder name.
     */
    public File createNewFolder(File containingDir) throws IOException {
	if(containingDir == null) {
	    throw new IOException("Containing directory is null:");
	}
	File newFolder = null;
	// Unix - using OpenWindow's default folder name. Can't find one for Motif/CDE.
	newFolder = createFileObject(containingDir, newFolderString);
	int i = 1;
	while (newFolder.exists() && (i < 100)) {
	    newFolder = createFileObject(containingDir, MessageFormat.format(
                newFolderNextString, new Object[] { new Integer(i) }));
	    i++;
	}

	if(newFolder.exists()) {
	    throw new IOException("Directory already exists:" + newFolder.getAbsolutePath());
	} else {
	    newFolder.mkdirs();
	}

	return newFolder;
    }

    /**
     * Returns whether a file is hidden or not. On Unix,
     * all files that begin with "." are hidden.
     */
    public boolean isHiddenFile(File f) {
	if(f != null) {
	    String filename = f.getName();
	    if(filename.charAt(0) == '.') {
		return true;
	    } else {
		return false;
	    }
	}
	return false;
    }

    /**
     * Returns the root partitians on this system.
     */
    public File[] getRoots() {
        if (!listRootsMethodChecked) {
            try {
                listRootsMethod = File.class.getMethod("listRoots", noArgTypes);
            }
            catch (NoSuchMethodException e) {
            }
            finally {
                listRootsMethodChecked = true;
            }
        }
	
        if (listRootsMethod != null) {
            try {
                File[] roots = (File[])(listRootsMethod.invoke(null, noArgs));
		for(int i = 0; i < roots.length; i++) {
		    roots[i] = new FileSystemRoot(roots[i]);
		}
                return roots;
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        } else {
	    // System.out.println("File.listRoots doesn't exist");
	    File[] roots = new File[1];
	    roots[0] = new File("/");
	    if(roots[0].exists() && roots[0].isDirectory()) {
		return roots;
	    }
	}
	return null;
    }
}


/**
 * FileSystemView that handles some specific windows concepts.
 */
class WindowsFileSystemView extends FileSystemView {
    private static final Object[] noArgs = {};
    private static final Class[] noArgTypes = {};
    private static final String newFolderString =
            UIManager.getString("FileChooser.win32.newFolder");
    private static final String newFolderNextString =
            UIManager.getString("FileChooser.win32.newFolder.subsequent");

    private static Method listRootsMethod = null;
    private static boolean listRootsMethodChecked = false;

    /**
     * Returns true if the given file is a root.
     */
    public boolean isRoot(File f) {
	if(!f.isAbsolute()) {
	    return false;
	}

        String parentPath = f.getParent();
        if(parentPath == null) {
            return true;
        } else {
            File parent = new File(parentPath);
            return parent.equals(f);
        }
    }

    /**
     * creates a new folder with a default folder name.
     */
    public File createNewFolder(File containingDir) throws IOException {
	if(containingDir == null) {
	    throw new IOException("Containing directory is null:");
	}
	File newFolder = null;
	// Using NT's default folder name
	newFolder = createFileObject(containingDir, newFolderString);
	int i = 2;
	while (newFolder.exists() && (i < 100)) {
	    newFolder = createFileObject(containingDir, MessageFormat.format(
                    newFolderNextString, new Object[] { new Integer(i) }));
	    i++;
	}

	if(newFolder.exists()) {
	    throw new IOException("Directory already exists:" + newFolder.getAbsolutePath());
	} else {
	    newFolder.mkdirs();
	}

	return newFolder;
    }

    /**
     * Returns whether a file is hidden or not. On Windows
     * there is currently no way to get this information from
     * io.File, therefore always return false.
     */
    public boolean isHiddenFile(File f) {
	return false;
    }

    /**
     * Returns all root partitians on this system. On Windows, this
     * will be the A: through Z: drives.
     */
    public File[] getRoots() {
        if (!listRootsMethodChecked) {
            try {
                listRootsMethod = File.class.getMethod("listRoots", noArgTypes);
            }
            catch (NoSuchMethodException e) {
            }
            finally {
                listRootsMethodChecked = true;
            }
        }

        if (listRootsMethod != null) {
            try {
                File[] roots = (File[])(listRootsMethod.invoke(null, noArgs));
		for(int i = 0; i < roots.length; i++) {
		    roots[i] = new FileSystemRoot(roots[i]);
		}
                return roots;
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        } else {
	    Vector rootsVector = new Vector();

	    // Create the A: drive whether it is mounted or not
	    FileSystemRoot floppy = new FileSystemRoot("A" + ":" + "\\");
	    rootsVector.addElement(floppy);

	    // Run through all possible mount points and check
	    // for their existance.
	    for (char c = 'C'; c <= 'Z'; c++) {
	        char device[] = {c, ':', '\\'};
	        String deviceName = new String(device);
	        File deviceFile = new FileSystemRoot(deviceName);
	        if (deviceFile != null && deviceFile.exists()) {
		    rootsVector.addElement(deviceFile);
	        }
	    }
	    File[] roots = new File[rootsVector.size()];
	    rootsVector.copyInto(roots);
	    return roots;
        }
	return null;
    }
}

/**
 * Fallthrough FileSystemView in case we can't determine the OS.
 */
class GenericFileSystemView extends FileSystemView {
    private static final Object[] noArgs = {};
    private static final Class[] noArgTypes = {};
    private static final String newFolderString =
            UIManager.getString("FileChooser.other.newFolder");

    private static Method listRootsMethod = null;
    private static boolean listRootsMethodChecked = false;

    /**
     * Returns true if the given file is a root.
     */
    public boolean isRoot(File f) {
	if(!f.isAbsolute()) {
	    return false;
	}

        String parentPath = f.getParent();
        if(parentPath == null) {
            return true;
        } else {
            File parent = new File(parentPath);
            return parent.equals(f);
        }
    }

    /**
     * creates a new folder with a default folder name.
     */
    public File createNewFolder(File containingDir) throws IOException {
	if(containingDir == null) {
	    throw new IOException("Containing directory is null:");
	}
	File newFolder = null;
	// Using NT's default folder name
	newFolder = createFileObject(containingDir, newFolderString);

	if(newFolder.exists()) {
	    throw new IOException("Directory already exists:" + newFolder.getAbsolutePath());
	} else {
	    newFolder.mkdirs();
	}

	return newFolder;
    }

    /**
     * Returns whether a file is hidden or not. Since we don't
     * know the OS type, always return false
     */
    public boolean isHiddenFile(File f) {
	return false;
    }

    /**
     * Returns all root partitians on this system. 
     */
    public File[] getRoots() {
        if (!listRootsMethodChecked) {
            try {
                listRootsMethod = File.class.getMethod("listRoots", noArgTypes);
            }
            catch (NoSuchMethodException e) {
            }
            finally {
                listRootsMethodChecked = true;
            }
        }

        if (listRootsMethod != null) {
            try {
                File[] roots = (File[])(listRootsMethod.invoke(null, noArgs));
		for(int i = 0; i < roots.length; i++) {
		    roots[i] = new FileSystemRoot(roots[i]);
		}
                return roots;
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        } else {
	    File[] roots = new File[0];
	    return roots;
        }
	return null;
    }

}

class FileSystemRoot extends File {
    public FileSystemRoot(File f) {
        super(f,"");
    }
  
    public FileSystemRoot(String s) {
        super(s);
    }
  
    public boolean isDirectory() {
        return true;
    }
}
