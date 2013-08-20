/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000-2002 The Apache Software Foundation.  All rights 
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
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package com.sun.org.apache.xerces.internal.xni.grammars;

/**
 * <p> This interface specifies how the parser and the application
 * interact with respect to Grammar objects that the application
 * possesses--either by having precompiled them or by having stored them
 * from a previous validation of an instance document.  It makes no
 * assumptions about the kind of Grammar involved, or about how the
 * application's storage mechanism works.</p>
 *
 * <p>The interaction works as follows:
 * <ul>
 * <li>When a validator considers a document, it is expected to request
 * grammars of the type it can handle from this object using the
 * <code>retrieveInitialGrammarSet</code> method. </li>
 * <li>If it requires a grammar
 * not in this set, it will request it from this Object using the
 * <code>retrieveGrammar</code> method.  </li>
 * <li> After successfully validating an
 * instance, the validator should make any new grammars it has compiled
 * available to this object using the <code>cacheGrammars</code>
 * method; for ease of implementation it may make other Grammars it holds references to as well (i.e., 
 * it may return some grammars that were retrieved from the GrammarPool in earlier operations). </li> </ul> </p>
 *
 * @author Neil Graham, IBM
 * @version $Id: XMLGrammarPool.java,v 1.3 2003/11/14 16:54:05 mrglavas Exp $
 */

public interface XMLGrammarPool {

    // <p>we are trying to make this XMLGrammarPool work for all kinds of
    // grammars, so we have a parameter "grammarType" for each of the
    // methods. </p>

    /**
     * <p> retrieve the initial known set of grammars. this method is
     * called by a validator before the validation starts. the application 
     * can provide an initial set of grammars available to the current 
     * validation attempt. </p>
     * @param grammarType the type of the grammar, from the
     *  <code>com.sun.org.apache.xerces.internal.xni.grammars.Grammar</code> interface.
     * @return the set of grammars the validator may put in its "bucket"
     */
    public Grammar[] retrieveInitialGrammarSet(String grammarType);

    /** 
     * <p>return the final set of grammars that the validator ended up
     * with.  
     * This method is called after the
     * validation finishes. The application may then choose to cache some
     * of the returned grammars. </p>
     * @param grammarType the type of the grammars being returned;
     * @param grammars an array containing the set of grammars being
     *  returned; order is not significant.
     */
    public void cacheGrammars(String grammarType, Grammar[] grammars);

    /** 
     * <p> This method requests that the application retrieve a grammar
     * corresponding to the given GrammarIdentifier from its cache.
     * If it cannot do so it must return null; the parser will then
     * call the EntityResolver.  <strong>An application must not call its
     * EntityResolver itself from this method; this may result in infinite
     * recursions.</strong>
     * @param desc The description of the Grammar being requested.
     * @return the Grammar corresponding to this description or null if
     *  no such Grammar is known.
     */
    public Grammar retrieveGrammar(XMLGrammarDescription desc);

    /**
     * Causes the XMLGrammarPool not to store any grammars when
     * the cacheGrammars(String, Grammar[[]) method is called.
     */
    public void lockPool();

    /**
     * Allows the XMLGrammarPool to store grammars when its cacheGrammars(String, Grammar[])
     * method is called.  This is the default state of the object.
     */
    public void unlockPool();

    /**
     * Removes all grammars from the pool.
     */
    public void clear();
} // XMLGrammarPool

