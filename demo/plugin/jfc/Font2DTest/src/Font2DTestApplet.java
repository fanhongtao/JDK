/*
 * @(#)Font2DTestApplet.java	1.12 05/11/17
 * 
 * Copyright (c) 2006 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * -Redistribution of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 * 
 * -Redistribution in binary form must reproduce the above copyright notice, 
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may 
 * be used to endorse or promote products derived from this software without 
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL 
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MIDROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST 
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, 
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY 
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, 
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or intended
 * for use in the design, construction, operation or maintenance of any
 * nuclear facility.
 */

/*
 * @(#)Font2DTestApplet.java	1.12 05/11/17
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
