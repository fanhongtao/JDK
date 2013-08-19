/*
 * @(#)Terminator.java	1.9 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

import sun.misc.Signal;
import sun.misc.SignalHandler;


/**
 * Package-private utility class for setting up and tearing down
 * platform-specific support for termination-triggered shutdowns.
 *
 * @author   Mark Reinhold
 * @version  1.9, 03/01/23
 * @since    1.3
 */

class Terminator {

    private static SignalHandler handler = null;

    /* Invocations of setup and teardown are already synchronized
     * on the shutdown lock, so no further synchronization is needed here
     */

    static void setup() {
	if (handler != null) return;
	SignalHandler sh = new SignalHandler() {
	    public void handle(Signal sig) {
		Shutdown.exit(sig.getNumber() + 0200);
	    }
	};
	handler = sh;
	try {
            Signal.handle(new Signal("HUP"), sh);
            Signal.handle(new Signal("INT"), sh);
            Signal.handle(new Signal("TERM"), sh);
        } catch (IllegalArgumentException e) {
            // When -Xrs is specified the user is responsible for
            // ensuring that shutdown hooks are run by calling
            // System.exit()
        }
    }

    static void teardown() {
	/* The current sun.misc.Signal class does not support
	 * the cancellation of handlers
	 */
    }

}
