/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt;

/** 
 * An abstract class which provides a print graphics context for a page.
 *
 * @version 	1.10 02/06/02
 * @author 	Amy Fowler
 */
public interface PrintGraphics {

    /**
     * Returns the PrintJob object from which this PrintGraphics 
     * object originated.
     */
    public PrintJob getPrintJob();

}
