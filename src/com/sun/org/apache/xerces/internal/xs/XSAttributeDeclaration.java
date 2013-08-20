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
 * The interface represents the Attribute Declaration schema component.
 */
public interface XSAttributeDeclaration extends XSObject {
    /**
     * [type definition]: A simple type definition. 
     */
    public XSSimpleTypeDefinition getTypeDefinition();

    /**
     * [scope]. One of <code>SCOPE_GLOBAL</code>, <code>SCOPE_LOCAL</code>, or 
     * <code>SCOPE_ABSENT</code>. If the scope is local, then the 
     * <code>enclosingCTDefinition</code> is present. 
     */
    public short getScope();

    /**
     * The complex type definition for locally scoped declarations (see 
     * <code>scope</code>), otherwise <code>null</code> if no such 
     * definition exists. 
     */
    public XSComplexTypeDefinition getEnclosingCTDefinition();

    /**
     * Value constraint: one of <code>VC_NONE, VC_DEFAULT, VC_FIXED</code>. 
     */
    public short getConstraintType();

    /**
     * Value constraint: The constraint value with respect to the [type 
     * definition], otherwise <code>null</code>. 
     */
    public String getConstraintValue();

    /**
     * Value Constraint: Binding specific actual constraint value or 
     * <code>null</code> if the value is in error or there is no value 
     * constraint. 
     * @exception XSException
     *   NOT_SUPPORTED_ERR: Raised if the implementation does not support this 
     *   method.
     */
    public Object getActualVC()
                                                        throws XSException;

    /**
     * The actual constraint value built-in datatype, e.g. 
     * <code>STRING_DT, SHORT_DT</code>. If the type definition of this 
     * value is a list type definition, this method returns 
     * <code>LIST_DT</code>. If the type definition of this value is a list 
     * type definition whose item type is a union type definition, this 
     * method returns <code>LISTOFUNION_DT</code>. To query the actual 
     * constraint value of the list or list of union type definitions use 
     * <code>itemValueTypes</code>. If the <code>actualValue</code> is 
     * <code>null</code>, this method returns <code>UNAVAILABLE_DT</code>. 
     * @exception XSException
     *   NOT_SUPPORTED_ERR: Raised if the implementation does not support this 
     *   method.
     */
    public short getActualVCType()
                                                        throws XSException;

    /**
     * In the case the actual constraint value represents a list, i.e. the 
     * <code>actualValueType</code> is <code>LIST_DT</code>, the returned 
     * array consists of one type kind which represents the itemType. If the 
     * actual constraint value represents a list type definition whose item 
     * type is a union type definition, i.e. <code>LISTOFUNION_DT</code>, 
     * for each actual constraint value in the list the array contains the 
     * corresponding memberType kind. For examples, see 
     * <code>ItemPSVI.itemValueTypes</code>. 
     * @exception XSException
     *   NOT_SUPPORTED_ERR: Raised if the implementation does not support this 
     *   method.
     */
    public ShortList getItemValueTypes()
                                                        throws XSException;

    /**
     * An annotation if it exists, otherwise <code>null</code>. 
     */
    public XSAnnotation getAnnotation();

}
