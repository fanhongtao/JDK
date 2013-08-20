/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000-2002 The Apache Software Foundation.  All rights 
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

package com.sun.org.apache.xerces.internal.xni;

import com.sun.org.apache.xerces.internal.xni.parser.XMLDTDContentModelSource;

/**
 * The DTD content model handler interface defines callback methods 
 * to report information items in DTD content models of an element
 * declaration. Parser components interested in DTD content model
 * information implement this interface and are registered as the DTD
 * content model handler on the DTD content model source.
 *
 * @see XMLDTDHandler
 *
 * @author Andy Clark, IBM
 *
 * @version $Id: XMLDTDContentModelHandler.java,v 1.5 2002/12/07 00:07:51 neilg Exp $
 */
public interface XMLDTDContentModelHandler {

    //
    // Constants
    //

    // separators

    /** 
     * A choice separator for children and mixed content models. This
     * separator is used to specify that the allowed child is one of a
     * collection.
     * <p>
     * For example:
     * <pre>
     * &lt;!ELEMENT elem (foo|bar)&gt;
     * &lt;!ELEMENT elem (foo|bar+)&gt;
     * &lt;!ELEMENT elem (foo|bar|baz)&gt;
     * &lt;!ELEMENT elem (#PCDATA|foo|bar)*&gt;
     * </pre>
     *
     * @see #SEPARATOR_SEQUENCE
     */
    public static final short SEPARATOR_CHOICE = 0;

    /**
     * A sequence separator for children content models. This separator 
     * is used to specify that the allowed children must follow in the
     * specified sequence.
     * <p>
     * <pre>
     * &lt;!ELEMENT elem (foo,bar)&gt;
     * &lt;!ELEMENT elem (foo,bar*)&gt;
     * &lt;!ELEMENT elem (foo,bar,baz)&gt;
     * </pre>
     *
     * @see #SEPARATOR_CHOICE
     */
    public static final short SEPARATOR_SEQUENCE = 1;

    // occurrence counts

    /** 
     * This occurrence count limits the element, choice, or sequence in a
     * children content model to zero or one. In other words, the child
     * is optional.
     * <p>
     * For example:
     * <pre>
     * &lt;!ELEMENT elem (foo?)&gt;
     * </pre>
     *
     * @see #OCCURS_ZERO_OR_MORE
     * @see #OCCURS_ONE_OR_MORE
     */
    public static final short OCCURS_ZERO_OR_ONE = 2;

    /** 
     * This occurrence count limits the element, choice, or sequence in a
     * children content model to zero or more. In other words, the child
     * may appear an arbitrary number of times, or not at all. This
     * occurrence count is also used for mixed content models.
     * <p>
     * For example:
     * <pre>
     * &lt;!ELEMENT elem (foo*)&gt;
     * &lt;!ELEMENT elem (#PCDATA|foo|bar)*&gt;
     * </pre>
     *
     * @see #OCCURS_ZERO_OR_ONE
     * @see #OCCURS_ONE_OR_MORE
     */
    public static final short OCCURS_ZERO_OR_MORE = 3;

    /** 
     * This occurrence count limits the element, choice, or sequence in a
     * children content model to one or more. In other words, the child
     * may appear an arbitrary number of times, but must appear at least
     * once.
     * <p>
     * For example:
     * <pre>
     * &lt;!ELEMENT elem (foo+)&gt;
     * </pre>
     *
     * @see #OCCURS_ZERO_OR_ONE
     * @see #OCCURS_ZERO_OR_MORE
     */
    public static final short OCCURS_ONE_OR_MORE = 4;

    //
    // XMLDTDContentModelHandler methods
    //

    /**
     * The start of a content model. Depending on the type of the content
     * model, specific methods may be called between the call to the
     * startContentModel method and the call to the endContentModel method.
     * 
     * @param elementName The name of the element.
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void startContentModel(String elementName, Augmentations augmentations)
        throws XNIException;

    /** 
     * A content model of ANY. 
     *
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     *
     * @see #empty
     * @see #startGroup
     */
    public void any(Augmentations augmentations) throws XNIException;

    /**
     * A content model of EMPTY.
     *
     * @throws XNIException Thrown by handler to signal an error.
     *
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     *
     * @see #any
     * @see #startGroup
     */
    public void empty(Augmentations augmentations) throws XNIException;

    /**
     * A start of either a mixed or children content model. A mixed
     * content model will immediately be followed by a call to the
     * <code>pcdata()</code> method. A children content model will
     * contain additional groups and/or elements.
     *
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     *
     * @see #any
     * @see #empty
     */
    public void startGroup(Augmentations augmentations) throws XNIException;

    /**
     * The appearance of "#PCDATA" within a group signifying a
     * mixed content model. This method will be the first called
     * following the content model's <code>startGroup()</code>.
     *
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     *     
     * @throws XNIException Thrown by handler to signal an error.
     *
     * @see #startGroup
     */
    public void pcdata(Augmentations augmentations) throws XNIException;

    /**
     * A referenced element in a mixed or children content model.
     * 
     * @param elementName The name of the referenced element.
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void element(String elementName, Augmentations augmentations) 
        throws XNIException;

    /**
     * The separator between choices or sequences of a mixed or children
     * content model.
     * 
     * @param separator The type of children separator.
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     *
     * @see #SEPARATOR_CHOICE
     * @see #SEPARATOR_SEQUENCE
     */
    public void separator(short separator, Augmentations augmentations) 
        throws XNIException;

    /**
     * The occurrence count for a child in a children content model or
     * for the mixed content model group.
     * 
     * @param occurrence The occurrence count for the last element
     *                   or group.
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     *
     * @see #OCCURS_ZERO_OR_ONE
     * @see #OCCURS_ZERO_OR_MORE
     * @see #OCCURS_ONE_OR_MORE
     */
    public void occurrence(short occurrence, Augmentations augmentations) 
        throws XNIException;

    /**
     * The end of a group for mixed or children content models.
     *
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void endGroup(Augmentations augmentations) throws XNIException;

    /**
     * The end of a content model.
     *
     * @param augmentations Additional information that may include infoset
     *                      augmentations.
     *
     * @throws XNIException Thrown by handler to signal an error.
     */
    public void endContentModel(Augmentations augmentations) throws XNIException;

    // set content model source
    public void setDTDContentModelSource(XMLDTDContentModelSource source);

    // get content model source
    public XMLDTDContentModelSource getDTDContentModelSource();

} // interface XMLDTDContentModelHandler
