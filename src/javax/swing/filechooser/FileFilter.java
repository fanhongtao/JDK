/*
 * @(#)FileFilter.java	1.8 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.filechooser;

import java.io.File;

/**
 * FileFilter is an abstract class that has no default implemention.
 * A FileFilter, once implemented, can be set on a JFileChooser to
 * keep unwanted files from appearing in the directory listing.
 *
 * A default implementation (ExtensionFileFilter) is currently in the
 * FileChooserDemo directory, and will become a first class swing
 * implementation in Swing 1.0.3.
 *
 * For an example implementation of a simple file filter, see
 * <code><i>yourJDK</i>/demo/jfc/FileChooserDemo/ExampleFileFilter.java</code>.
 * For more information, see the Swing Connection article on the 
 * <a href="http://java.sun.com/products/jfc/swingdoc-current/file_chooser.html">File Chooser</a>
 *
 * @see javax.swing.JFileChooser#setFileFilter
 * @see javax.swing.JFileChooser#addChoosableFileFilter
 *
 * @version 1.8 11/29/01
 * @author Jeff Dinkins
 */
public abstract class FileFilter {
    /**
     * Whether the given file is accepted by this filter.
     */
    public abstract boolean accept(File f);

    /**
     * The description of this filter. For example: "JPG and GIF Images"
     * @see FileView#getName
     */
    public abstract String getDescription();
}
