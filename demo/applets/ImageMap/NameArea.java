/*
 * @(#)NameArea.java	1.9 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import java.awt.Graphics;

/**
 * A message feedback ImageArea class.
 * This class extends the basic ImageArea Class to show the a given
 * message in the status message area when the user enters this area.
 *
 * @author 	Jim Graham
 * @version 	1.9, 11/29/01
 */
class NameArea extends ImageMapArea {
    /** The string to be shown in the status message area. */
    String name;

    /**
     * The argument is the string to be displayed in the status message
     * area.
     */
    public void handleArg(String arg) {
	name = arg;
    }

    /**
     * The enter method displays the message in the status bar.
     */
    public void enter() {
	showStatus(name);
    }

    /**
     * The exit method clears the status bar.
     */
    public void exit() {
	showStatus(null);
    }
}

