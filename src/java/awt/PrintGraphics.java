/*
 * @(#)PrintGraphics.java	1.2 00/01/12
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
 * @version 	1.7 09/21/98
 * @author 	Amy Fowler
 */
public interface PrintGraphics {

    /**
     * Returns the PrintJob object from which this PrintGraphics 
     * object originated.
     */
    public PrintJob getPrintJob();

}
