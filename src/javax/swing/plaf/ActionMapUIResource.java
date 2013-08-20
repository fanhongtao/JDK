/*
 * @(#)ActionMapUIResource.java	1.6 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf;

import javax.swing.ActionMap;


/**
 * A subclass of javax.swing.ActionMap that implements UIResource. 
 * UI classes which provide an ActionMap should use this class.
 * 
 * @version 1.6 12/19/03
 * @author Scott Violet
 */
public class ActionMapUIResource extends ActionMap implements UIResource {
    public ActionMapUIResource() {
    }
}
