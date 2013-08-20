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
 *  This interface defines constants used by this specification.
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
     * The object describes a constraining facet. Note: this object does not 
     * describe pattern and enumeration facets.
     */
    public static final short FACET                     = 13;
    /**
     * The object describes enumeration and pattern facets. 
     */
    public static final short MULTIVALUE_FACET          = 14;

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
     * groups is <code>absent</code>. The scope of such a declaration is 
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

    // Build-in types: primitive and derived
    /**
     * anySimpleType
     */
    public static final short ANYSIMPLETYPE_DT          = 1;
    /**
     * string
     */
    public static final short STRING_DT                 = 2;
    /**
     * boolean
     */
    public static final short BOOLEAN_DT                = 3;
    /**
     * decimal
     */
    public static final short DECIMAL_DT                = 4;
    /**
     * float
     */
    public static final short FLOAT_DT                  = 5;
    /**
     * double
     */
    public static final short DOUBLE_DT                 = 6;
    /**
     * duration
     */
    public static final short DURATION_DT               = 7;
    /**
     * dateTime
     */
    public static final short DATETIME_DT               = 8;
    /**
     * time
     */
    public static final short TIME_DT                   = 9;
    /**
     * date
     */
    public static final short DATE_DT                   = 10;
    /**
     * gYearMonth
     */
    public static final short GYEARMONTH_DT             = 11;
    /**
     * gYear
     */
    public static final short GYEAR_DT                  = 12;
    /**
     * gMonthDay
     */
    public static final short GMONTHDAY_DT              = 13;
    /**
     * gDay
     */
    public static final short GDAY_DT                   = 14;
    /**
     * gMonth
     */
    public static final short GMONTH_DT                 = 15;
    /**
     * hexBinary
     */
    public static final short HEXBINARY_DT              = 16;
    /**
     * base64Binary
     */
    public static final short BASE64BINARY_DT           = 17;
    /**
     * anyURI
     */
    public static final short ANYURI_DT                 = 18;
    /**
     * QName
     */
    public static final short QNAME_DT                  = 19;
    /**
     * NOTATION
     */
    public static final short NOTATION_DT               = 20;
    /**
     * normalizedString
     */
    public static final short NORMALIZEDSTRING_DT       = 21;
    /**
     * token
     */
    public static final short TOKEN_DT                  = 22;
    /**
     * language
     */
    public static final short LANGUAGE_DT               = 23;
    /**
     * NMTOKEN
     */
    public static final short NMTOKEN_DT                = 24;
    /**
     * Name
     */
    public static final short NAME_DT                   = 25;
    /**
     * NCName
     */
    public static final short NCNAME_DT                 = 26;
    /**
     * ID
     */
    public static final short ID_DT                     = 27;
    /**
     * IDREF
     */
    public static final short IDREF_DT                  = 28;
    /**
     * ENTITY
     */
    public static final short ENTITY_DT                 = 29;
    /**
     * integer
     */
    public static final short INTEGER_DT                = 30;
    /**
     * nonPositiveInteger
     */
    public static final short NONPOSITIVEINTEGER_DT     = 31;
    /**
     * negativeInteger
     */
    public static final short NEGATIVEINTEGER_DT        = 32;
    /**
     * long
     */
    public static final short LONG_DT                   = 33;
    /**
     * int
     */
    public static final short INT_DT                    = 34;
    /**
     * short
     */
    public static final short SHORT_DT                  = 35;
    /**
     * byte
     */
    public static final short BYTE_DT                   = 36;
    /**
     * nonNegativeInteger
     */
    public static final short NONNEGATIVEINTEGER_DT     = 37;
    /**
     * unsignedLong
     */
    public static final short UNSIGNEDLONG_DT           = 38;
    /**
     * unsignedInt
     */
    public static final short UNSIGNEDINT_DT            = 39;
    /**
     * unsignedShort
     */
    public static final short UNSIGNEDSHORT_DT          = 40;
    /**
     * unsignedByte
     */
    public static final short UNSIGNEDBYTE_DT           = 41;
    /**
     * positiveInteger
     */
    public static final short POSITIVEINTEGER_DT        = 42;
    /**
     * The type represents a list type definition whose item type (itemType) 
     * is a union type definition
     */
    public static final short LISTOFUNION_DT            = 43;
    /**
     * The type represents a list type definition.
     */
    public static final short LIST_DT                   = 44;
    /**
     * The built-in type category is not available.
     */
    public static final short UNAVAILABLE_DT            = 45;

}
