/*
 * @(#)InputMapUIResource.java	1.9 10/03/23
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf;

import javax.swing.InputMap;


/**
 * A subclass of javax.swing.InputMap that implements UIResource. 
 * UI classes which provide a InputMap should use this class.
 * 
 * @version 1.9 03/23/10
 * @author Scott Violet
 * @since 1.3
 */
public class InputMapUIResource extends InputMap implements UIResource {
    public InputMapUIResource() {
    }
}
