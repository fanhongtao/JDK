/*
 * @(#)CheckboxPeer.java	1.9 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt.peer;

import java.awt.*;

public interface CheckboxPeer extends ComponentPeer {
    void setState(boolean state);
    void setCheckboxGroup(CheckboxGroup g);
    void setLabel(String label);
}
