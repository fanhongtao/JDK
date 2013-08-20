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
 * This interface provides access to the post schema validation infoset for an 
 * API that provides a streaming document infoset, such as SAX, XNI, and 
 * others. 
 * <p>For implementations that would like to provide access to the PSVI in a 
 * streaming model, a parser object should also implement the 
 * <code>PSVIProvider</code> interface. Within the scope of the methods 
 * handling the start and end of an element, applications may use the 
 * <code>PSVIProvider</code> to retrieve the PSVI related to the element and 
 * its attributes.
 */
public interface PSVIProvider {
    /**
     *  Provides the post schema validation item for the current element 
     * information item. The method must be called by an application while 
     * in the scope of the methods which report the start and end of an 
     * element. For example, for SAX the method must be called within the 
     * scope of the document handler's <code>startElement</code> or 
     * <code>endElement</code> call. If the method is called outside of the 
     * specified scope, the return value is undefined. 
     * @return The post schema validation infoset for the current element. If 
     *   an element information item is valid, then in the 
     *   post-schema-validation infoset the following properties must be 
     *   available for the element information item: The following 
     *   properties are available in the scope of the method that reports 
     *   the start of an element: {element declaration}, {validation 
     *   context}, {notation}. The {schema information} property is 
     *   available for the validation root. The {error codes} property is 
     *   available if any errors occured during validation.  The following 
     *   properties are available in the scope of the method that reports 
     *   the end of an element: {nil}, {schema specified}, {normalized 
     *   value},{ member type definition}, {validity}, {validation attempted}
     *   . If the declaration has a value constraint, the property {schema 
     *   default} is available. The {error codes} property is available if 
     *   any errors occured during validation. Note: some processors may 
     *   choose to provide all the PSVI properties in the scope of the 
     *   method that reports the end of an element. 
     */
    public ElementPSVI getElementPSVI();

    /**
     * Provides <code>AttributePSVI</code> given the index of an attribute 
     * information item in the current element's attribute list. The method 
     * must be called by an application while in the scope of the methods 
     * which report the start and end of an element at a point where the 
     * attribute list is available. For example, for SAX the method must be 
     * called while in the scope of the document handler's 
     * <code>startElement</code> call. If the method is called outside of 
     * the specified scope, the return value is undefined.
     * @param index The attribute index. 
     * @return The post schema validation properties of the attribute.
     */
    public AttributePSVI getAttributePSVI(int index);

    /**
     * Provides <code>AttributePSVI</code> given the namespace name and the 
     * local name of an attribute information item in the current element's 
     * attribute list. The method must be called by an application while in 
     * the scope of the methods which report the start and end of an element 
     * at a point where the attribute list is available. For example, for 
     * SAX the method must be called while in the scope of the document 
     * handler's <code>startElement</code> call. If the method is called 
     * outside of the specified scope, the return value is undefined.
     * @param uri The namespace name of an attribute. 
     * @param localname The local name of an attribute. 
     * @return The post schema validation properties of the attribute.
     */
    public AttributePSVI getAttributePSVIByName(String uri, 
                                                String localname);

}
