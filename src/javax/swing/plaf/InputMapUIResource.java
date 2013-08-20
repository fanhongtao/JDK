/*
 * @(#)InputMapUIResource.java	1.6 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf;

import javax.swing.InputMap;


/**
 * A subclass of javax.swing.InputMap that implements UIResource. 
 * UI classes which provide a InputMap should use this class.
 * 
 * @version 1.6 12/19/03
 * @author Scott Violet
 */
public class InputMapUIResource extends InputMap implements UIResource {
    public InputMapUIResource() {
    }
}
