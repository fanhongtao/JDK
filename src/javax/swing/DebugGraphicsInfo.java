/*
 * @(#)DebugGraphicsInfo.java	1.7 98/10/19
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */

package javax.swing;

import java.awt.*;
import java.util.*;

/** Class used by DebugGraphics for maintaining information about how
  * to render graphics calls.
  *
  * @version 1.7 10/19/98
  * @author Dave Karlton
  */
class DebugGraphicsInfo {
    Color                flashColor = Color.red;
    int                  flashTime = 100;
    int                  flashCount = 2;
    Hashtable            componentToDebug;
    JFrame               debugFrame = null;
    java.io.PrintStream  stream = System.out;

    void setDebugOptions(JComponent component, int debug) {
        if (componentToDebug == null) {
            componentToDebug = new Hashtable();
        }
	if (debug > 0) {
	    componentToDebug.put(component, new Integer(debug));
	} else {
	    componentToDebug.remove(component);
	}
    }

    int getDebugOptions(JComponent component) {
        if (componentToDebug == null) {
            return 0;
        } else {
            Integer integer = (Integer)componentToDebug.get(component);

            return integer == null ? 0 : integer.intValue();
        }
    }

    void log(String string) {
        stream.println(string);
    }
}


