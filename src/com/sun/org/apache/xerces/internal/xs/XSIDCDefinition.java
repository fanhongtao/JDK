/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2003 The Apache Software Foundation.  All rights
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
 * originally based on software copyright (c) 2003, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package com.sun.org.apache.xerces.internal.xs;

/**
 * This interface represents the Identity-constraint Definition schema 
 * component.
 */
public interface XSIDCDefinition extends XSObject {
    // Identity Constraints
    /**
     * See the definition of <code>key</code> in the identity-constraint 
     * category.
     */
    public static final short IC_KEY                    = 1;
    /**
     * See the definition of <code>keyref</code> in the identity-constraint 
     * category.
     */
    public static final short IC_KEYREF                 = 2;
    /**
     * See the definition of <code>unique</code> in the identity-constraint 
     * category.
     */
    public static final short IC_UNIQUE                 = 3;

    /**
     * [identity-constraint category]: one of key, keyref or unique. 
     */
    public short getCategory();

    /**
     * [selector]: a restricted XPath 1.0 expression. 
     */
    public String getSelectorStr();

    /**
     * [fields]: a non-empty list of restricted  XPath 1.0 expressions. 
     */
    public StringList getFieldStrs();

    /**
     * [referenced key]: required if [identity-constraint category] is keyref, 
     * <code>null</code> otherwise. An identity-constraint definition with [
     * identity-constraint category] equal to key or unique. 
     */
    public XSIDCDefinition getRefKey();

    /**
     * A set of [annotations] if it exists, otherwise an empty 
     * <code>XSObjectList</code>. 
     */
    public XSObjectList getAnnotations();

}
