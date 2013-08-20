/*
 * @(#)ComponentInputMapUIResource.java	1.6 03/12/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf;

import javax.swing.ComponentInputMap;
import javax.swing.JComponent;


/**
 * A subclass of javax.swing.ComponentInputMap that implements UIResource. 
 * UI classes which provide a ComponentInputMap should use this class.
 * 
 * @version 1.6 12/19/03
 * @author Scott Violet
 */
public class ComponentInputMapUIResource extends ComponentInputMap implements UIResource {
    public ComponentInputMapUIResource(JComponent component) {
	super(component);
    }
}
