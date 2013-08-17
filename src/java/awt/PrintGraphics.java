/*
 * @(#)PrintGraphics.java	1.9 00/02/02
 *
 * Copyright 1996-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package java.awt;

/** 
 * An abstract class which provides a print graphics context for a page.
 *
 * @version 	1.9 02/02/00
 * @author 	Amy Fowler
 */
public interface PrintGraphics {

    /**
     * Returns the PrintJob object from which this PrintGraphics 
     * object originated.
     */
    public PrintJob getPrintJob();

}
