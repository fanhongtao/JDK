/*
 * @(#)InputMapUIResource.java	1.3 00/02/02
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.swing.plaf;

import javax.swing.InputMap;


/**
 * A subclass of javax.swing.InputMap that implements UIResource. 
 * UI classes which provide a InputMap should use this class.
 * 
 * @version 1.3 02/02/00
 * @author Scott Violet
 */
public class InputMapUIResource extends InputMap implements UIResource {
    public InputMapUIResource() {
    }
}
