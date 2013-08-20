/*
 * @(#)FlavorException.java	1.4 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.print;

import javax.print.DocFlavor;

/**
 * Interface FlavorException is a mixin interface which a subclass of {@link 
 * PrintException PrintException} can implement to report an error condition 
 * involving a doc flavor or flavors (class {@link javax.print.DocFlavor 
 * DocFlavor}). The Print Service API does not define any print exception 
 * classes that implement interface FlavorException, that being left to the 
 * Print Service implementor's discretion. 
 * 
 */
public interface FlavorException {

    /**
     * Returns the unsupported flavors.
     * @return the unsupported doc flavors.
     */
    public DocFlavor[] getUnsupportedFlavors();

}
