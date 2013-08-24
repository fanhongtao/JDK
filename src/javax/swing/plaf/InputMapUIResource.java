/*
 * @(#)InputMapUIResource.java	1.8 06/04/07
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf;

import javax.swing.InputMap;


/**
 * A subclass of javax.swing.InputMap that implements UIResource. 
 * UI classes which provide a InputMap should use this class.
 * 
 * @version 1.8 04/07/06
 * @author Scott Violet
 * @since 1.3
 */
public class InputMapUIResource extends InputMap implements UIResource {
    public InputMapUIResource() {
    }
}
