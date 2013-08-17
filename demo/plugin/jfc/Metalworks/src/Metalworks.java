/*
 * @(#)Metalworks.java	1.10 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;


/**
  * This application is a demo of the Metal Look & Feel
  *
  * @version 1.10 12/03/01
  * @author Steve Wilson
  */
public class Metalworks {

    public static void main( String[] args ) {
        JDialog.setDefaultLookAndFeelDecorated(true);
        JFrame.setDefaultLookAndFeelDecorated(true);
        Toolkit.getDefaultToolkit().setDynamicLayout(true);
        System.setProperty("sun.awt.noerasebackground","true");

        try {
            javax.swing.plaf.metal.MetalLookAndFeel.setCurrentTheme( new javax.swing.plaf.metal.DefaultMetalTheme());
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        }  
        catch ( UnsupportedLookAndFeelException e ) {
            System.out.println ("Metal Look & Feel not supported on this platform. \nProgram Terminated");
            System.exit(0);
        }
        catch ( IllegalAccessException e ) {
            System.out.println ("Metal Look & Feel could not be accessed. \nProgram Terminated");
            System.exit(0);
        }
        catch ( ClassNotFoundException e ) {
            System.out.println ("Metal Look & Feel could not found. \nProgram Terminated");
            System.exit(0);
        }   
        catch ( InstantiationException e ) {
            System.out.println ("Metal Look & Feel could not be instantiated. \nProgram Terminated");
            System.exit(0);
        }
        catch ( Exception e ) {
            System.out.println ("Unexpected error. \nProgram Terminated");
            e.printStackTrace();
            System.exit(0);
        }
        JFrame frame = new MetalworksFrame();
        frame.setVisible(true);
    }
}
