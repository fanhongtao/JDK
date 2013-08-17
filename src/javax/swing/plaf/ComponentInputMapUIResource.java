/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf;

import javax.swing.ComponentInputMap;
import javax.swing.JComponent;


/**
 * A subclass of javax.swing.ComponentInputMap that implements UIResource. 
 * UI classes which provide a ComponentInputMap should use this class.
 * 
 * @version 1.4 02/06/02
 * @author Scott Violet
 */
public class ComponentInputMapUIResource extends ComponentInputMap implements UIResource {
    public ComponentInputMapUIResource(JComponent component) {
	super(component);
    }
}
