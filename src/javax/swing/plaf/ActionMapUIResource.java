/*
 * @(#)ActionMapUIResource.java	1.8 06/04/07
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf;

import javax.swing.ActionMap;


/**
 * A subclass of javax.swing.ActionMap that implements UIResource. 
 * UI classes which provide an ActionMap should use this class.
 * 
 * @version 1.8 04/07/06
 * @author Scott Violet
 * @since 1.3
 */
public class ActionMapUIResource extends ActionMap implements UIResource {
    public ActionMapUIResource() {
    }
}
