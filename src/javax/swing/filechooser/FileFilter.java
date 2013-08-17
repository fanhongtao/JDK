/*
 * @(#)FileFilter.java	1.14 00/02/02
 *
 * Copyright 1998-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.swing.filechooser;

import java.io.File;

/**
 * FileFilter is an abstract class that has no default implemention.
 * A FileFilter, once implemented, can be set on a JFileChooser to
 * keep unwanted files from appearing in the directory listing.
 * For an example implementation of a simple file filter, see
 * <code><i>yourSDK</i>/demo/jfc/FileChooserDemo/ExampleFileFilter.java</code>.
 * For more information and examples see 
 * <a href="http://java.sun.com/docs/books/tutorial/uiswing/components/filechooser.html">How to Use File Choosers</a>,
 * a section in <em>The Java Tutorial</em>.
 *
 * @see javax.swing.JFileChooser#setFileFilter
 * @see javax.swing.JFileChooser#addChoosableFileFilter
 *
 * @version 1.14 02/02/00
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
