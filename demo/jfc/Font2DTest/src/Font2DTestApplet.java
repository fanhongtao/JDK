/*
 * @(#)Font2DTestApplet.java	1.7 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import java.awt.AWTPermission;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

/**
 * Font2DTestApplet.java
 *
 * @version @(#)Font2DTestApplet.java	1.1 00/08/22
 * @author Shinsuke Fukuda
 * @author Ankit Patel [Conversion to Swing - 01/07/30]  
 */

/// Applet version of Font2DTest that wraps the actual demo

public final class Font2DTestApplet extends JApplet {
    public void init() {
        /// Check if necessary permission is given...
        SecurityManager security = System.getSecurityManager();
        if ( security != null ) {
            try {
                security.checkPermission( new AWTPermission( "showWindowWithoutWarningBanner" ));
            }
            catch ( SecurityException e ) {
                System.out.println( "NOTE: showWindowWithoutWarningBanner AWTPermission not given.\n" +
                                    "Zoom window will contain warning banner at bottom when shown\n" );
            }
            try {
                security.checkPrintJobAccess();
            }
            catch ( SecurityException e ) {
                System.out.println( "NOTE: queuePrintJob RuntimePermission not given.\n" +
                                    "Printing feature will not be available\n" );
            }
        }
        
        final JFrame f = new JFrame( "Font2DTest" );
        final Font2DTest f2dt = new Font2DTest( f, true );
        f.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) { f.dispose(); }
        });

        f.getContentPane().add( f2dt );
        f.pack();
        f.show();
    }
}
