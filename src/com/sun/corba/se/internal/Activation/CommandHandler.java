/*
 * @(#)CommandHandler.java	1.11 01/12/03
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.corba.se.internal.Activation;

import org.omg.CORBA.ORB;

import java.io.PrintStream;

/**
 * @version     1.11, 02/08/29
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
