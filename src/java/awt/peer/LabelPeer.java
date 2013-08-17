/*
 * @(#)LabelPeer.java	1.9 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt.peer;

import java.awt.Color;
import java.awt.Font;

public interface LabelPeer extends ComponentPeer {
    void setText(String label);
    void setAlignment(int alignment);
}
