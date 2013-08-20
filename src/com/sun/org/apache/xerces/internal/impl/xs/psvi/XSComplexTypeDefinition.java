/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001, 2002 The Apache Software Foundation.  All rights
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
 * originally based on software copyright (c) 2001, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package com.sun.org.apache.xerces.internal.impl.xs.psvi;

/**
 * This interface represents the Complex Type Definition schema component.
 * The interface may be updated or replaced. 
 */
public interface XSComplexTypeDefinition extends XSTypeDefinition {
    // Content Model Types
    /**
     * Represents an empty content type. A content type with the distinguished 
     * value empty validates elements with no character or element 
     * information item children. 
     */
    public static final short CONTENTTYPE_EMPTY         = 0;
    /**
     * Represents a simple content type. A content type which is a simple 
     * validates elements with character-only children. 
     */
    public static final short CONTENTTYPE_SIMPLE        = 1;
    /**
     * Represents an element-only content type. An element-only content type 
     * validates elements with children that conform to the supplied content 
     * model. 
     */
    public static final short CONTENTTYPE_ELEMENT       = 2;
    /**
     * Represents a mixed content type.
     */
    public static final short CONTENTTYPE_MIXED         = 3;

    /**
     * [derivation method]: either <code>DERIVATION_EXTENSION</code>, 
     * <code>DERIVATION_RESTRICTION</code>, or <code>DERIVATION_NONE</code> 
     * (see <code>XSConstants</code>). 
     */
    public short getDerivationMethod();

    /**
     * [abstract]: a boolean. Complex types for which <code>abstract</code> is 
     * true must not be used as the type definition for the validation of 
     * element information items. 
     */
    public boolean getAbstract();

    /**
     *  A set of attribute uses. 
     */
    public XSObjectList getAttributeUses();

    /**
     * Optional.An attribute wildcard. 
     */
    public XSWildcard getAttributeWildcard();

    /**
     * [content type]: one of empty (<code>CONTENTTYPE_EMPTY</code>), a simple 
     * type definition (<code>CONTENTTYPE_SIMPLE</code>), mixed (
     * <code>CONTENTTYPE_EMPTY</code>), or element-only (
     * <code>CONTENTTYPE_EMPTY</code>). 
     */
    public short getContentType();

    /**
     * A simple type definition corresponding to simple content model, 
     * otherwise <code>null</code> 
     */
    public XSSimpleTypeDefinition getSimpleType();

    /**
     * A particle for mixed or element-only content model, otherwise 
     * <code>null</code> 
     */
    public XSParticle getParticle();

    /**
     * [prohibited substitutions]: a subset of {extension, restriction}
     * @param restriction  Extention or restriction constants (see 
     *   <code>XSConstants</code>). 
     * @return True if restriction is a prohibited substitution, otherwise 
     *   false.
     */
    public boolean isProhibitedSubstitution(short restriction);

    /**
     *  [prohibited substitutions]: A subset of {extension, restriction} or 
     * <code>DERIVATION_NONE</code> represented as a bit flag (see 
     * <code>XSConstants</code>). 
     */
    public short getProhibitedSubstitutions();

    /**
     * A set of [annotations]. 
     */
    public XSObjectList getAnnotations();

}
