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
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.apache.org.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package com.sun.org.apache.xerces.internal.impl.dv;

import com.sun.org.apache.xerces.internal.xs.XSSimpleTypeDefinition;

/**
 * This interface <code>XSSimpleType</code> represents the simple type
 * definition of schema component and defines methods to query the information
 * contained.
 * Any simple type (atomic, list or union) will implement this interface.
 * It inherits from <code>XSTypeDecl</code>.
 *
 * @author Sandy Gao, IBM
 *
 * @version $Id: XSSimpleType.java,v 1.13 2003/11/11 20:14:58 sandygao Exp $
 */
public interface XSSimpleType extends XSSimpleTypeDefinition {

    /**
     * constants defined for the values of 'whitespace' facet.
     * see <a href='http://www.w3.org/TR/xmlschema-2/#dt-whiteSpace'> XML Schema
     * Part 2: Datatypes </a>
     */
    /** preserve the white spaces */
    public static final short WS_PRESERVE = 0;
    /** replace the white spaces */
    public static final short WS_REPLACE  = 1;
    /** collapse the white spaces */
    public static final short WS_COLLAPSE = 2;

    /**
     * Constant defined for the primitive built-in simple tpyes.
     * see <a href='http://www.w3.org/TR/xmlschema-2/#built-in-primitive-datatypes'>
     * XML Schema Part 2: Datatypes </a>
     */
    /** "string" type */
    public static final short PRIMITIVE_STRING        = 1;
    /** "boolean" type */
    public static final short PRIMITIVE_BOOLEAN       = 2;
    /** "decimal" type */
    public static final short PRIMITIVE_DECIMAL       = 3;
    /** "float" type */
    public static final short PRIMITIVE_FLOAT         = 4;
    /** "double" type */
    public static final short PRIMITIVE_DOUBLE        = 5;
    /** "duration" type */
    public static final short PRIMITIVE_DURATION      = 6;
    /** "dataTime" type */
    public static final short PRIMITIVE_DATETIME      = 7;
    /** "time" type */
    public static final short PRIMITIVE_TIME          = 8;
    /** "date" type */
    public static final short PRIMITIVE_DATE          = 9;
    /** "gYearMonth" type */
    public static final short PRIMITIVE_GYEARMONTH    = 10;
    /** "gYear" type */
    public static final short PRIMITIVE_GYEAR         = 11;
    /** "gMonthDay" type */
    public static final short PRIMITIVE_GMONTHDAY     = 12;
    /** "gDay" type */
    public static final short PRIMITIVE_GDAY          = 13;
    /** "gMonth" type */
    public static final short PRIMITIVE_GMONTH        = 14;
    /** "hexBinary" type */
    public static final short PRIMITIVE_HEXBINARY     = 15;
    /** "base64Binary" type */
    public static final short PRIMITIVE_BASE64BINARY  = 16;
    /** "anyURI" type */
    public static final short PRIMITIVE_ANYURI        = 17;
    /** "QName" type */
    public static final short PRIMITIVE_QNAME         = 18;
    /** "NOTATION" type */
    public static final short PRIMITIVE_NOTATION      = 19;

    /**
     * return an ID representing the built-in primitive base type.
     * REVISIT: This method is (currently) for internal use only.
     *          the constants returned from this method are not finalized yet.
     *          the names and values might change in the further.
     *
     * @return   an ID representing the built-in primitive base type
     */
    public short getPrimitiveKind();

    /**
     * validate a given string against this simple type.
     *
     * @param content       the string value that needs to be validated
     * @param context       the validation context
     * @param validatedInfo used to store validation result
     *
     * @return              the actual value (QName, Boolean) of the string value
     */
    public Object validate(String content, ValidationContext context, ValidatedInfo validatedInfo)
        throws InvalidDatatypeValueException;

    /**
     * validate a given string value, represented by content.toString().
     * note that if content is a StringBuffer, for performance reasons,
     * it's possible that the content of the string buffer is modified.
     *
     * @param content       the string value that needs to be validated
     * @param context       the validation context
     * @param validatedInfo used to store validation result
     *
     * @return              the actual value (QName, Boolean) of the string value
     */
    public Object validate(Object content, ValidationContext context, ValidatedInfo validatedInfo)
        throws InvalidDatatypeValueException;

    /**
     * validate an actual value against this simple type.
     *
     * @param value         the actual value that needs to be validated
     * @param context       the validation context
     * @param validatedInfo used to provide the actual value and member types
     * @exception InvalidDatatypeValueException  exception for invalid values.
     */
    public void validate(ValidationContext context, ValidatedInfo validatedInfo)
        throws InvalidDatatypeValueException;

    /**
     * If this type is created from restriction, then some facets can be applied
     * to the simple type. <code>XSFacets</code> is used to pass the value of
     * different facets.
     *
     * @param facets        the value of all the facets
     * @param presentFacets bit combination value of the costraining facet
     *                      constants which are present.
     * @param fixedFacets   bit combination value of the costraining facet
     *                      constants which are fixed.
     * @param ValidationContext the validation context
     * @exception InvalidDatatypeFacetException  exception for invalid facet values.
     */
    public void applyFacets(XSFacets facets, short presentFacet, short fixedFacet, ValidationContext context)
        throws InvalidDatatypeFacetException;

    /**
     * Check whether two actual values are equal.
     *
     * @param value1  the first value
     * @param value2  the second value
     * @return        true if the two value are equal
     */
    public boolean isEqual(Object value1, Object value2);

    /**
     * Check the order of the two actual values. (May not be supported by all
     * simple types.
     * REVISIT: Andy believes that a compare() method is necessary.
     *          I don't see the necessity for schema (the only place where we
     *          need to compare two values is to check min/maxIn/Exclusive
     *          facets, but we only need a private method for this case.)
     *          But Andy thinks XPATH potentially needs this compare() method.
     *
     * @param value1  the first value
     * @prarm value2  the second value
     * @return        > 0 if value1 > value2
     *                = 0 if value1 == value2
     *                < = if value1 < value2
     */
    //public short compare(Object value1, Object value2);

    /**
     * Check whether this type is or is derived from ID.
     * REVISIT: this method makes ID special, which is not a good design.
     *          but since ID is not a primitive, there doesn't seem to be a
     *          clean way of doing it except to define special method like this.
     *
     * @return  whether this simple type is or is derived from ID.
     */
    public boolean isIDType();

    /**
     * Return the whitespace corresponding to this datatype.
     * 
     * @return valid values are WS_PRESERVE, WS_REPLACE, WS_COLLAPSE.
     * @exception DatatypeException
     *                   union datatypes don't have whitespace facet associated with them
     */
    public short getWhitespace() throws DatatypeException;
}
