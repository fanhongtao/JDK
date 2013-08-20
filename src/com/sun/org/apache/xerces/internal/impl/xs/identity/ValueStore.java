/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001, 2002 The Apache Software Foundation.  
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

package com.sun.org.apache.xerces.internal.impl.xs.identity;


/**
 * Interface for storing values associated to an identity constraint. 
 * Each value stored corresponds to a field declared for the identity
 * constraint. One instance of an object implementing this interface
 * is created for each identity constraint per element declaration in
 * the instance document to store the information for this identity
 * constraint.
 * <p>
 * <strong>Note:</strong> The component performing identity constraint
 * collection and validation is responsible for providing an 
 * implementation of this interface. The component is also responsible
 * for performing the necessary checks required by each type of identity
 * constraint.
 *
 * @author Andy Clark, IBM
 *
 * @version $Id: ValueStore.java,v 1.5 2003/09/25 22:59:21 elena Exp $
 */
public interface ValueStore {
    
    //
    // ValueStore methods
    //

    /** 
     * Adds the specified value to the value store.
     *
     * @param field The field associated to the value. This reference
     *              is used to ensure that each field only adds a value
     *              once within a selection scope.
     * @param value The value to add.
     */
    public void addValue(Field field, Object actualValue);

    /**
     * Since the valueStore will have access to an error reporter, this
     * allows it to be called appropriately.
     * @param key  the key of the localized error message
     * @param args  the list of arguments for substitution.
     */
    public void reportError(String key, Object[] args);
    
    public void addValue(Field field, Object actualValue,Object type);

} // interface ValueStore
