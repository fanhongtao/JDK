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
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package com.sun.org.apache.xerces.internal.dom;


/** Internal class LCount is used to track the number of
    listeners registered for a given event name, as an entry
    in a global hashtable. This should allow us to avoid generating,
    or discard, events for which no listeners are registered. 
    
    ***** There should undoubtedly be methods here to manipulate
    this table. At the moment that code's residing in NodeImpl.
    Move it when we have a chance to do so. Sorry; we were
    rushed.
    
    ***** Also, I'm currently asking "are there any listeners"
    by testing captures+bubbles+defaults =? 0. It would probably
    make sense to have a separate "total" field, calculated at
    add/remove, to save a few cycles during dispatch. Fix.
    
    ???? CONCERN: Hashtables are known to be "overserialized" in
    current versions of Java. That may impact performance.
    
    ???? CONCERN: The hashtable should probably be a per-document object.
    Finer granularity would be even better, but would cost more cycles to
    resolve and might not save enough event traffic to be worth the investment.
*/
/**
 * @version $Id: LCount.java,v 1.6 2003/05/08 19:52:40 elena Exp $
 */

class LCount 
{ 
    static java.util.Hashtable lCounts=new java.util.Hashtable();
    public int captures=0,bubbles=0,defaults=0;

    static LCount lookup(String evtName)
    {
        LCount lc=(LCount)lCounts.get(evtName);
        if(lc==null)
            lCounts.put(evtName,(lc=new LCount()));
        return lc;	        
    }
} // class LCount
