/*
 * @(#)GTKEngineParser.java	1.6 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.java.swing.plaf.gtk;

import java.io.IOException;

/**
 * The abstract base class for all theme engine parsers.
 *
 * @author  Shannon Hickey
 * @version 1.6 01/23/03
 */
abstract class GTKEngineParser {

    protected final int uniqueScopeID = GTKScanner.getUniqueScopeID();

    /**
     * Parse the body of an 'engine' section of an rc file and store
     * the results in a <CODE>GTKParser.EngineInfo<CODE> object.
     * <P>
     * This method takes three parameters. The first is a scanner to
     * retrieve tokens from. Configuration options on the scanner may be
     * changed by this method, but it must be sure to restore the previous
     * values when it has completed. A typical implementation will also
     * want to register its own symbols with the scanner. To do so, it should
     * save the current scope of the scanner, and then set the scanner's scope
     * to the value of 'uniqueScopeID'. Then, it should register its own
     * symbols with the scanner if they don't already exist. The int value
     * of every symbol registered must be > GTKScanner.TOKEN_LAST.
     * At the successful completion of this method, the old scope should be
     * restored, but the registered symbols can be left for the next use.
     * <P>
     * When this method is called, the scanner will be ready to return the first
     * token inside the opening '{' of an engine section. Therefore, with the
     * exception of returning early in error, this method must continue parsing
     * until it sees a matching outer '}', even if it no longer has interest in
     * the tokens returned.
     * <P>
     * The second parameter is the parser that called this method. It should
     * not be modified in any way, and has been included only to make available
     * its <CODE>resolvePixmapPath</CODE> method for resolving paths to images.
     * <P>
     * The last parameter will always be a single element array, for returning a
     * <CODE>GTKParser.EngineInfo</CODE> object representing the information
     * that was parsed. Upon invocation of this method, the array may already
     * contain an info object. If so, it is guaranteed that it was created by this
     * <CODE>GTKEngineParser</CODE> on a previous call to <CODE>parse</CODE>.
     * As such, its type can be assumed. Typically, an implementation will
     * want to append to, or merge with the information contained in any passed in
     * info object.
     * <P>
     * Upon successful completion, the information parsed should be stored in
     * a <CODE>GTKParser.EngineInfo</CODE> object as element 0 in the array
     * parameter. This can be null if we wish to signify, for any reason, that
     * that this entire engine section be thrown out and to use the default
     * engine instead.
     * <P>
     * This method should return <CODE>GTKScanner.TOKEN_NONE</CODE>, if successful,
     * otherwise the token that it expected but didn't get.
     *
     * @param   scanner   The scanner to retrieve tokens from.
     * @param   parser    The parser that called us.
     * @param   retVal    A single element array to store an object containing the
     *                    information parsed.
     *
     * @return  <CODE>GTKScanner.TOKEN_NONE</CODE> if the parse was successful,
     *          otherwise the token that was expected but not received.
     */
    abstract int parse(GTKScanner scanner,
                       GTKParser parser,
                       GTKParser.EngineInfo[] retVal) throws IOException;

}
