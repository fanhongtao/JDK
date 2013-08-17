/*
 * @(#)PopupFactory.java	1.3 00/02/02
 *
 * Copyright 1997-2000 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * This software is the proprietary information of Sun Microsystems, Inc.  
 * Use is subject to license terms.
 * 
 */

package javax.swing;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import javax.accessibility.*;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Vector;


interface PopupFactory {

    public Popup getPopup(Component comp, Component invoker, int x, int y);

    // PENDING(ges): eradicate?
    public void setLightWeightPopupEnabled(boolean aFlag);
    public boolean isLightWeightPopupEnabled();

}

