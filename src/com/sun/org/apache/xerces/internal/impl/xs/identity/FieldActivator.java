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
 * Interface for a field activator. The field activator is responsible
 * for activating fields within a specific scope; the caller merely
 * requests the fields to be activated.
 *
 * @author Andy Clark, IBM
 *
 * @version $Id: FieldActivator.java,v 1.6 2004/03/10 23:05:24 mrglavas Exp $
 */
public interface FieldActivator {
    
    //
    // FieldActivator methods
    //

    /**
     * Start the value scope for the specified identity constraint. This 
     * method is called when the selector matches in order to initialize 
     * the value store.
     *
     * @param identityConstraint The identity constraint.
     * @param initialDepth  the depth at which the selector began matching
     */
    public void startValueScopeFor(IdentityConstraint identityConstraint,
            int initialDepth);

    /** 
     * Request to activate the specified field. This method returns the
     * matcher for the field.
     * It's also important for the implementor to ensure that it marks whether a Field
     * is permitted to match a value--that is, to call the setMayMatch(Field, Boolean) method.
     *
     * @param field The field to activate.
     * @param initialDepth the 0-indexed depth in the instance document at which the Selector began to match.
     */
    public XPathMatcher activateField(Field field, int initialDepth);
    
    /**
     * Sets whether the given field is permitted to match a value.
     * This should be used to catch instance documents that try 
     * and match a field several times in the same scope.
     * 
     * @param field The field that may be permitted to be matched.
     * @param state Boolean indiciating whether the field may be matched.
     */
    public void setMayMatch(Field field, Boolean state);
    
    /**
     * Returns whether the given field is permitted to match a value.
     * 
     * @param field The field that may be permitted to be matched.
     * @return Boolean indicating whether the field may be matched.
     */
    public Boolean mayMatch(Field field);

    /**
     * Ends the value scope for the specified identity constraint.
     *
     * @param identityConstraint The identity constraint.
     * @param initialDepth  the 0-indexed depth where the Selector began to match.
     */
    public void endValueScopeFor(IdentityConstraint identityConstraint, int initialDepth);

} // interface FieldActivator
