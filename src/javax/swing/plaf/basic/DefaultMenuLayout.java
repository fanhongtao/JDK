/*
 * @(#)DefaultMenuLayout.java	1.4 00/02/02
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.swing.plaf.basic;

import javax.swing.*;
import javax.swing.plaf.UIResource;

import java.awt.Container;

/**
 * The default layout manager for Popup menus and menubars.  This
 * class is an extension of BoxLayout which adds the UIResource tag
 * so that plauggable L&Fs can distinguish it from user-installed
 * layout managers on menus.
 *
 * @version 1.4 02/02/00
 * @author Georges Saab
 */

public class DefaultMenuLayout extends BoxLayout implements UIResource {
    public DefaultMenuLayout(Container target, int axis) {
	super(target, axis);
    }
}
