/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001-2004 The Apache Software Foundation.  
 * All rights reserved.
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

package com.sun.org.apache.xerces.internal.parsers;


import com.sun.org.apache.xerces.internal.impl.Constants;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarPool;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import com.sun.org.apache.xerces.internal.util.SecurityManager;

/**
 * This configuration allows Xerces to behave in a security-conscious manner; that is,
 * it permits applications to instruct Xerces to limit certain
 * operations that could be exploited by malicious document authors to cause a denail-of-service
 * attack when the document is parsed.
 *
 * In addition to the features and properties recognized by the base
 * parser configuration, this class recognizes these additional 
 * features and properties:
 * <ul>
 * <li>Properties
 *  <ul>
 *   <li>http://apache.org/xml/properties/security-manager</li>
 *  </ul>
 * </ul>
 *
 * @author Neil Graham, IBM
 * @author Gopal Sharma, Sun Microsystems Inc.
 *
 * @version $Id: SecurityConfiguration.java,v 1.4 2004/02/16 19:28:30 mrglavas Exp $
 */
public class SecurityConfiguration extends XML11Configuration
{

    //
    // Constants
    //

    protected static final String SECURITY_MANAGER_PROPERTY =
        Constants.XERCES_PROPERTY_PREFIX + Constants.SECURITY_MANAGER_PROPERTY;

    //
    // Constructors
    //

    /** Default constructor. */
    public SecurityConfiguration () {
        this(null, null, null);
    } // <init>()

    /** 
     * Constructs a parser configuration using the specified symbol table. 
     *
     * @param symbolTable The symbol table to use.
     */
    public SecurityConfiguration (SymbolTable symbolTable) {
        this(symbolTable, null, null);
    } // <init>(SymbolTable)

    /**
     * Constructs a parser configuration using the specified symbol table and
     * grammar pool.
     * <p>
     * <strong>REVISIT:</strong> 
     * Grammar pool will be updated when the new validation engine is
     * implemented.
     *
     * @param symbolTable The symbol table to use.
     * @param grammarPool The grammar pool to use.
     */
    public SecurityConfiguration (SymbolTable symbolTable,
                                         XMLGrammarPool grammarPool) {
        this(symbolTable, grammarPool, null);
    } // <init>(SymbolTable,XMLGrammarPool)

    /**
     * Constructs a parser configuration using the specified symbol table,
     * grammar pool, and parent settings.
     * <p>
     * <strong>REVISIT:</strong> 
     * Grammar pool will be updated when the new validation engine is
     * implemented.
     *
     * @param symbolTable    The symbol table to use.
     * @param grammarPool    The grammar pool to use.
     * @param parentSettings The parent settings.
     */
    public SecurityConfiguration (SymbolTable symbolTable,
                                         XMLGrammarPool grammarPool,
                                         XMLComponentManager parentSettings) {
        super(symbolTable, grammarPool, parentSettings);
        
        // create the SecurityManager property:
        setProperty(SECURITY_MANAGER_PROPERTY, new SecurityManager());
    } // <init>(SymbolTable,XMLGrammarPool)

} // class SecurityConfiguration

