/*
 * @(#)Metalworks.java	1.5 98/08/26
 *
 * Copyright 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */


import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;


/**
  * This application is a demo of the Metal Look & Feel
  *
  * @version 1.5 08/26/98
  * @author Steve Wilson
  */
public class Metalworks {

    public static void main( String[] args ) {
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
