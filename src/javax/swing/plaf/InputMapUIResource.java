/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf;

import javax.swing.InputMap;


/**
 * A subclass of javax.swing.InputMap that implements UIResource. 
 * UI classes which provide a InputMap should use this class.
 * 
 * @version 1.4 02/06/02
 * @author Scott Violet
 */
public class InputMapUIResource extends InputMap implements UIResource {
    public InputMapUIResource() {
    }
}
