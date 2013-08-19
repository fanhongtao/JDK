/*
 * @(#)FileView.java	1.16 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.filechooser;

import java.io.File;
import javax.swing.*;

/**
 * FileView defines an abstract class that can be implemented to
 * provide the filechooser with ui information for a File.
 * Each L&F JFileChooserUI object implements this class to pass
 * back the correct icons and type descriptions specific to
 * that L&F. For example, the Windows L&F returns the generic Windows
 * icons for directories and generic files.
 * Additionally, you may want to provide your own FileView to
 * JFileChooser to return different icons or additional information
 * using {@link javax.swing.JFileChooser#setFileView}.
 *
 * <p>
 *
 * JFileChooser first looks to see if there is a user defined FileView,
 * if there is, it gets type information from there first. If FileView
 * returns null for any method, JFileChooser then uses the L&F specific
 * view to get the information.
 * So, for example, if you provide a FileView class that returns an
 * <code>Icon</code> for JPG files, and returns <code>null</code>
 * icons for all other files, the UI's FileView will provide default
 * icons for all other files.
 *
 * <p>
 *
 * For an example implementation of a simple file filter, see
 * <code><i>yourSDK</i>/demo/jfc/FileChooserDemo/ExampleFileView.java</code>.
 * For more information and examples see 
 * <a
 href="http://java.sun.com/docs/books/tutorial/uiswing/components/filechooser.html">How to Use File Choosers</a>,
 * a section in <em>The Java Tutorial</em>.
 *
 * @see javax.swing.JFileChooser
 *
 * @version 1.16 01/23/03
 * @author Jeff Dinkins
 *
 */
public abstract class FileView {
    /**
     * The name of the file. Normally this would be simply f.getName()
     */
    public String getName(File f) {
	return null;
    };

    /**
     * A human readable description of the file. For example,
     * a file named jag.jpg might have a description that read:
     * "A JPEG image file of James Gosling's face"
     */
    public String getDescription(File f) {
	return null;
    }

    /**
     * A human readable description of the type of the file. For
     * example, a jpg file might have a type description of:
     * "A JPEG Compressed Image File"
     */
    public String getTypeDescription(File f) {
	return null;
    }

    /**
     * The icon that represents this file in the JFileChooser.
     */
    public Icon getIcon(File f) {
	return null;
    }

    /**
     * Whether the directory is traversable or not. This might be
     * useful, for example, if you want a directory to represent
     * a compound document and don't want the user to descend into it.
     */
    public Boolean isTraversable(File f) {
	return null;
    }

}
