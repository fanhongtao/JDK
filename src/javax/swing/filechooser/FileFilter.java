/*
 * @(#)FileFilter.java	1.7 98/08/26
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
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
 * @version 1.7 08/26/98
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
