/*
 * @(#)CommandHandler.java	1.16 10/03/23
 *
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.sun.corba.se.impl.activation;

import org.omg.CORBA.ORB;

import java.io.PrintStream;

/**
 * @version     1.16, 10/07/17
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
