/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
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

