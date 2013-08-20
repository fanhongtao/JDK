/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000-2002 The Apache Software Foundation.  
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
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package com.sun.org.apache.xerces.internal.xni.grammars;

/**
 * A generic grammar for use in validating XML documents. The Grammar
 * object stores the validation information in a compiled form. Specific
 * subclasses extend this class and "populate" the grammar by compiling 
 * the specific syntax (DTD, Schema, etc) into the data structures used
 * by this object.
 * <p>
 * <strong>Note:</strong> The Grammar object is not useful as a generic 
 * grammar access or query object. In other words, you cannot round-trip 
 * specific grammar syntaxes with the compiled grammar information in 
 * the Grammar object. You <em>can</em> create equivalent validation
 * rules in your choice of grammar syntax but there is no guarantee that
 * the input and output will be the same.
 * 
 * <p> Right now, this class is largely a shell; eventually, 
 * it will be enriched by having more expressive methods added. </p>
 * will be moved from dtd.Grammar here.
 *
 * @author Jeffrey Rodriguez, IBM
 * @author Eric Ye, IBM
 * @author Andy Clark, IBM
 * @author Neil Graham, IBM
 *
 * @version $Id: Grammar.java,v 1.5 2003/11/14 16:54:05 mrglavas Exp $
 */

public interface Grammar {

    /**
     * get the <code>XMLGrammarDescription</code> associated with this
     * object
     */
    public XMLGrammarDescription getGrammarDescription ();
} // interface Grammar

