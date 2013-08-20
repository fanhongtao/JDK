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
 *  This interface defines constants used by this specification.
 * The interface may be updated or replaced. 
 */
public interface XSConstants {
    // XML Schema Components
    /**
     * The object describes an attribute declaration.
     */
    public static final short ATTRIBUTE_DECLARATION     = 1;
    /**
     * The object describes an element declaration.
     */
    public static final short ELEMENT_DECLARATION       = 2;
    /**
     * The object describes a complex type or simple type definition.
     */
    public static final short TYPE_DEFINITION           = 3;
    /**
     * The object describes an attribute use definition.
     */
    public static final short ATTRIBUTE_USE             = 4;
    /**
     * The object describes an attribute group definition.
     */
    public static final short ATTRIBUTE_GROUP           = 5;
    /**
     * The object describes a model group definition.
     */
    public static final short MODEL_GROUP_DEFINITION    = 6;
    /**
     * A model group.
     */
    public static final short MODEL_GROUP               = 7;
    /**
     * The object describes a particle.
     */
    public static final short PARTICLE                  = 8;
    /**
     * The object describes a wildcard.
     */
    public static final short WILDCARD                  = 9;
    /**
     * The object describes an identity constraint definition.
     */
    public static final short IDENTITY_CONSTRAINT       = 10;
    /**
     * The object describes a notation declaration.
     */
    public static final short NOTATION_DECLARATION      = 11;
    /**
     * The object describes an annotation.
     */
    public static final short ANNOTATION                = 12;
    /**
     * The object describes a constraining facet.
     */
    public static final short FACET                     = 13;
    
    /**
     * The object describes enumeration/pattern facets.
     */
    public static final short MULTIVALUE_FACET           = 14;


    // Derivation constants
    /**
     * No constraint is available.
     */
    public static final short DERIVATION_NONE           = 0;
    /**
     * <code>XSTypeDefinition</code> final set or 
     * <code>XSElementDeclaration</code> disallowed substitution group.
     */
    public static final short DERIVATION_EXTENSION      = 1;
    /**
     * <code>XSTypeDefinition</code> final set or 
     * <code>XSElementDeclaration</code> disallowed substitution group.
     */
    public static final short DERIVATION_RESTRICTION    = 2;
    /**
     * <code>XSTypeDefinition</code> final set.
     */
    public static final short DERIVATION_SUBSTITUTION   = 4;
    /**
     * <code>XSTypeDefinition</code> final set.
     */
    public static final short DERIVATION_UNION          = 8;
    /**
     * <code>XSTypeDefinition</code> final set.
     */
    public static final short DERIVATION_LIST           = 16;

    // Scope
    /**
     * The scope of a declaration within named model groups or attribute 
     * groups is <code>absent</code>. The scope of such declaration is 
     * determined when it is used in the construction of complex type 
     * definitions. 
     */
    public static final short SCOPE_ABSENT              = 0;
    /**
     * A scope of <code>global</code> identifies top-level declarations. 
     */
    public static final short SCOPE_GLOBAL              = 1;
    /**
     * <code>Locally scoped</code> declarations are available for use only 
     * within the complex type.
     */
    public static final short SCOPE_LOCAL               = 2;

    // Value Constraint
    /**
     * Indicates that the component does not have any value constraint.
     */
    public static final short VC_NONE                   = 0;
    /**
     * Indicates that there is a default value constraint.
     */
    public static final short VC_DEFAULT                = 1;
    /**
     * Indicates that there is a fixed value constraint for this attribute.
     */
    public static final short VC_FIXED                  = 2;

}
