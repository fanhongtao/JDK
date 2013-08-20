/*
* The Apache Software License, Version 1.1
*
*
* Copyright (c) 1999-2002 The Apache Software Foundation.  All rights 
* reserved.
*
* Redistribution and use in source and binary forms, with or without
* modification, are permitted provided that the following conditions
* are met:
*
* 1. Redistributions of source code must retain the above copyright
*    notice, this list of conditions and the following disclaimer. 
*
* 2. Redistributions in binary form must reproduce the above copyright
*    notice, this list of conditions and the following disclaimer in
*    the documentation and/or other materials provided with the
*    distribution.
*
* 3. The end-user documentation included with the redistribution,
*    if any, must include the following acknowledgment:  
*       "This product includes software developed by the
*        Apache Software Foundation (http://www.apache.org/)."
*    Alternately, this acknowledgment may appear in the software itself,
*    if and wherever such third-party acknowledgments normally appear.
*
* 4. The names "Xerces" and "Apache Software Foundation" must
*    not be used to endorse or promote products derived from this
*    software without prior written permission. For written 
*    permission, please contact apache@apache.org.
*
* 5. Products derived from this software may not be called "Apache",
*    nor may "Apache" appear in their name, without prior written
*    permission of the Apache Software Foundation.
*
* THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
* WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
* OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
* DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
* ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
* SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
* LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
* USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
* ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
* OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
* OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
* SUCH DAMAGE.
* ====================================================================
*
* This software consists of voluntary contributions made by many
* individuals on behalf of the Apache Software Foundation and was
* originally based on software copyright (c) 2002, International
* Business Machines, Inc., http://www.apache.org.  For more
* information on the Apache Software Foundation, please see
* <http://www.apache.org/>.
*/

package com.sun.org.apache.xerces.internal.util;


/**
 * Shadowed symbol table.
 *
 * The table has a reference to the main symbol table and is 
 * not allowed to add new symbols to the main symbol table. 
 * New symbols are added to the shadow symbol table and are local 
 * to the component using this table.
 * 
 * @author Andy Clark IBM
 * @version $Id: ShadowedSymbolTable.java,v 1.2 2002/08/09 15:18:19 neilg Exp $
 */

public final class ShadowedSymbolTable
extends SymbolTable {

    //
    // Data
    //

    /** Main symbol table. */
    protected SymbolTable fSymbolTable;

    //
    // Constructors
    //

    /** Constructs a shadow of the specified symbol table. */
    public ShadowedSymbolTable(SymbolTable symbolTable) {
        fSymbolTable = symbolTable;
    } // <init>(SymbolTable)

    //
    // SymbolTable methods
    //

    /**
     * Adds the specified symbol to the symbol table and returns a
     * reference to the unique symbol. If the symbol already exists, 
     * the previous symbol reference is returned instead, in order
     * guarantee that symbol references remain unique.
     * 
     * @param symbol The new symbol.
     */
    public String addSymbol(String symbol) {

        if (fSymbolTable.containsSymbol(symbol)) {
            return fSymbolTable.addSymbol(symbol);
        }
        return super.addSymbol(symbol);

    } // addSymbol(String)

    /**
     * Adds the specified symbol to the symbol table and returns a
     * reference to the unique symbol. If the symbol already exists, 
     * the previous symbol reference is returned instead, in order
     * guarantee that symbol references remain unique.
     * 
     * @param buffer The buffer containing the new symbol.
     * @param offset The offset into the buffer of the new symbol.
     * @param length The length of the new symbol in the buffer.
     */
    public String addSymbol(char[] buffer, int offset, int length) {

        if (fSymbolTable.containsSymbol(buffer, offset, length)) {
            return fSymbolTable.addSymbol(buffer, offset, length);
        }
        return super.addSymbol(buffer, offset, length);

    } // addSymbol(char[],int,int):String

    /**
     * Returns a hashcode value for the specified symbol. The value
     * returned by this method must be identical to the value returned
     * by the <code>hash(char[],int,int)</code> method when called
     * with the character array that comprises the symbol string.
     * 
     * @param symbol The symbol to hash.
     */
    public int hash(String symbol) {
        return fSymbolTable.hash(symbol);
    } // hash(String):int

    /**
     * Returns a hashcode value for the specified symbol information. 
     * The value returned by this method must be identical to the value
     * returned by the <code>hash(String)</code> method when called
     * with the string object created from the symbol information.
     * 
     * @param buffer The character buffer containing the symbol.
     * @param offset The offset into the character buffer of the start
     *               of the symbol.
     * @param length The length of the symbol.
     */
    public int hash(char[] buffer, int offset, int length) {
        return fSymbolTable.hash(buffer, offset, length);
    } // hash(char[],int,int):int

} // class ShadowedSymbolTable
