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
 * This interface represents an XML Schema. 
 * The interface may be updated or replaced. 
 */
public interface XSModel {
    /**
     * Convenience method. Returns a list of all namespaces that belong to 
     * this schema. The value <code>null</code> is not a valid namespace 
     * name, but if there are components that don't have a target namespace, 
     * <code>null</code> is included in this list. 
     */
    public StringList getNamespaces();

    /**
     * A set of namespace schema information information items ( of type 
     * <code>XSNamespaceItem</code>), one for each namespace name which 
     * appears as the target namespace of any schema component in the schema 
     * used for that assessment, and one for absent if any schema component 
     * in the schema had no target namespace. For more information see 
     * schema information. 
     */
    public XSNamespaceItemList getNamespaceItems();

    /**
     * Returns a list of top-level components, i.e. element declarations, 
     * attribute declarations, etc. 
     * @param objectType The type of the declaration, i.e. 
     *   <code>ELEMENT_DECLARATION</code>. Note that 
     *   <code>XSTypeDefinition.SIMPLE_TYPE</code> and 
     *   <code>XSTypeDefinition.COMPLEX_TYPE</code> can also be used as the 
     *   <code>objectType</code> to retrieve only complex types or simple 
     *   types, instead of all types.
     * @return A list of top-level definitions of the specified type in 
     *   <code>objectType</code> or <code>null</code>. 
     */
    public XSNamedMap getComponents(short objectType);

    /**
     * Convenience method. Returns a list of top-level component declarations 
     * that are defined within the specified namespace, i.e. element 
     * declarations, attribute declarations, etc. 
     * @param objectType The type of the declaration, i.e. 
     *   <code>ELEMENT_DECLARATION</code>.
     * @param namespace The namespace to which declaration belong or 
     *   <code>null</code> (for components with no target namespace).
     * @return A list of top-level definitions of the specified type in 
     *   <code>objectType</code> and defined in the specified 
     *   <code>namespace</code> or <code>null</code>. 
     */
    public XSNamedMap getComponentsByNamespace(short objectType, 
                                               String namespace);

    /**
     *  [annotations]: a set of annotations. 
     */
    public XSObjectList getAnnotations();

    /**
     * Convenience method. Returns a top-level element declaration. 
     * @param name The name of the declaration.
     * @param namespace The namespace of the declaration, otherwise null.
     * @return A top-level element declaration or null if such declaration 
     *   does not exist. 
     */
    public XSElementDeclaration getElementDeclaration(String name, 
                                                      String namespace);

    /**
     * Convenience method. Returns a top-level attribute declaration. 
     * @param name The name of the declaration.
     * @param namespace The namespace of the declaration, otherwise null.
     * @return A top-level attribute declaration or null if such declaration 
     *   does not exist. 
     */
    public XSAttributeDeclaration getAttributeDeclaration(String name, 
                                                          String namespace);

    /**
     * Convenience method. Returns a top-level simple or complex type 
     * definition. 
     * @param name The name of the definition.
     * @param namespace The namespace of the declaration, otherwise null.
     * @return An <code>XSTypeDefinition</code> or null if such definition 
     *   does not exist. 
     */
    public XSTypeDefinition getTypeDefinition(String name, 
                                              String namespace);

    /**
     * Convenience method. Returns a top-level attribute group definition. 
     * @param name The name of the definition.
     * @param namespace The namespace of the definition, otherwise null.
     * @return A top-level attribute group definition or null if such 
     *   definition does not exist. 
     */
    public XSAttributeGroupDefinition getAttributeGroup(String name, 
                                                        String namespace);

    /**
     * Convenience method. Returns a top-level model group definition. 
     * @param name The name of the definition.
     * @param namespace The namespace of the definition, otherwise null.
     * @return A top-level model group definition definition or null if such 
     *   definition does not exist. 
     */
    public XSModelGroupDefinition getModelGroupDefinition(String name, 
                                                          String namespace);

    /**
     * Convenience method. Returns a top-level notation declaration. 
     * @param name The name of the declaration.
     * @param namespace The namespace of the declaration, otherwise null.
     * @return A top-level notation declaration or null if such declaration 
     *   does not exist. 
     */
    public XSNotationDeclaration getNotationDeclaration(String name, 
                                                        String namespace);

}
