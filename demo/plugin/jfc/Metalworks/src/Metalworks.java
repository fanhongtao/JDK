/*
 * @(#)Metalworks.java	1.16 04/07/26
 * 
 * Copyright (c) 2004 Sun Microsystems, Inc. All Rights Reserved.
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
 * @(#)Metalworks.java	1.16 04/07/26
 */


import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
  * This application is a demo of the Metal Look & Feel
  *
  * @version 1.16 07/26/04
  * @author Steve Wilson
  */
public class Metalworks {

    public static void main( String[] args ) {
        UIManager.put("swing.boldMetal", Boolean.FALSE);
        JDialog.setDefaultLookAndFeelDecorated(true);
        JFrame.setDefaultLookAndFeelDecorated(true);
        Toolkit.getDefaultToolkit().setDynamicLayout(true);
        System.setProperty("sun.awt.noerasebackground","true");

        try {
            UIManager.setLookAndFeel(new MetalLookAndFeel());
        }  
        catch ( UnsupportedLookAndFeelException e ) {
            System.out.println ("Metal Look & Feel not supported on this platform. \nProgram Terminated");
            System.exit(0);
        }
        JFrame frame = new MetalworksFrame();
        frame.setVisible(true);
    }
}
