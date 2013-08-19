/*
 * @(#)CommandHandler.java	1.12 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.corba.se.internal.Activation;

import org.omg.CORBA.ORB;

import java.io.PrintStream;

/**
 * @version     1.12, 03/06/20
 * @author      Rohit Garg
 * @since       JDK1.2
 */

public interface CommandHandler
{
    String getCommandName();

    public final static boolean shortHelp = true;
    public final static boolean longHelp  = false;

    void printCommandHelp(PrintStream out, boolean helpType);

    public final static boolean parseError = true;
    public final static boolean commandDone = false;

    boolean processCommand(String[] cmd, ORB orb, PrintStream out);
}
