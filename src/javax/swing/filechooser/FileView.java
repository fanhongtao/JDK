/*
 * @(#)FileView.java	1.2 00/01/12
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.swing.filechooser;

import java.io.File;
import javax.swing.*;

/**
 * FileView defines an abstract class that can be implemented to
 * provide the filechooser with ui information for a File.
 *
 * Each L&F JFileChooserUI object implements this class to pass
 * back the correct icons and type descriptions specific to
 * that L&F. For example, the Windows L&F returns the generic Windows
 * icons for directories and generic files.
 *
 * Additionally, you may want to provide your own FileView to
 * JFileChooser to return different icons or additional information
 * using {@link javax.swing.JFileChooser#setFileView}.
 *
 * JFileChooser first looks to see if there is a user defined FileView,
 * if there is, it gets type information from there first. If FileView
 * returns null for any method, JFileChooser then uses the L&F specific
 * view to get the information.
 *
 * So, for example, if you provide a FileView class that returns an
 * <code>Icon</code> for JPG files, and returns <code>null</code>
 * icons for all other files, the UI's FileView will provide default
 * icons for all other files.
 *
 * For an example implementation of a simple file filter, see
 * <code><i>yourJDK</i>/demo/jfc/FileChooserDemo/ExampleFileView.java</code>.
 * For more information, see the Swing Connection article on the 
 * <a href="http://java.sun.com/products/jfc/swingdoc-current/file_chooser.html">File Chooser</a>
 *
 * @see javax.swing.JFileChooser
 *
 * @version 1.7 08/26/98
 * @author Jeff Dinkins
 *
 */
public abstract class FileView {
    /**
     * The name of the file. Normally this would be simply f.getName()
     */
    public abstract String getName(File f);

    /**
     * A human readable description of the file. For example,
     * a file named jag.jpg might have a description that read:
     * "A JPEG image file of James Gosling's face"
     */
    public abstract String getDescription(File f);

    /**
     * A human readable description of the type of the file. For
     * example, a jpg file might have a type description of:
     * "A JPEG Compressed Image File"
     */
    public abstract String getTypeDescription(File f);

    /**
     * The icon that represents this file in the JFileChooser.
     */
    public abstract Icon getIcon(File f);

    /**
     * Whether the directory is traversable or not. This might be
     * useful, for example, if you want a directory to represent
     * a compound document and don't want the user to descend into it.
     */
    public abstract Boolean isTraversable(File f);

}
