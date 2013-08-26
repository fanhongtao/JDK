/*
 * @(#)ActionMapUIResource.java	1.9 10/03/23
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf;

import javax.swing.ActionMap;


/**
 * A subclass of javax.swing.ActionMap that implements UIResource. 
 * UI classes which provide an ActionMap should use this class.
 * 
 * @version 1.9 03/23/10
 * @author Scott Violet
 * @since 1.3
 */
public class ActionMapUIResource extends ActionMap implements UIResource {
    public ActionMapUIResource() {
    }
}
