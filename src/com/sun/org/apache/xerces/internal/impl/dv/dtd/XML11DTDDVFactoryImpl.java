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

package com.sun.org.apache.xerces.internal.impl.dv.dtd;

import java.util.Enumeration;
import java.util.Hashtable;

import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;

/**
 * the factory to create/return built-in XML 1.1 DVs and create user-defined DVs
 *
 * @author Neil Graham, IBM
 *
 * @version $Id: XML11DTDDVFactoryImpl.java,v 1.2 2003/05/08 20:11:55 elena Exp $
 */
public class XML11DTDDVFactoryImpl extends DTDDVFactoryImpl {

    static Hashtable fXML11BuiltInTypes = new Hashtable();

    /**
     * return a dtd type of the given name
     * This will call the super class if and only if it does not
     * recognize the passed-in name.  
     *
     * @param name  the name of the datatype
     * @return      the datatype validator of the given name
     */
    public DatatypeValidator getBuiltInDV(String name) {
        if(fXML11BuiltInTypes.get(name) != null) {
            return (DatatypeValidator)fXML11BuiltInTypes.get(name);
        }
        return (DatatypeValidator)fBuiltInTypes.get(name);
    }

    /**
     * get all built-in DVs, which are stored in a hashtable keyed by the name
     * New XML 1.1 datatypes are inserted.
     *
     * @return      a hashtable which contains all datatypes
     */
    public Hashtable getBuiltInTypes() {
        Hashtable toReturn = (Hashtable)fBuiltInTypes.clone();
        Enumeration xml11Keys = fXML11BuiltInTypes.keys();
        while (xml11Keys.hasMoreElements()) {
            Object key = xml11Keys.nextElement();
            toReturn.put(key, fXML11BuiltInTypes.get(key));
        }
        return toReturn;
    }

    static {
        fXML11BuiltInTypes.put("XML11ID", new XML11IDDatatypeValidator());
        DatatypeValidator dvTemp = new XML11IDREFDatatypeValidator();
        fXML11BuiltInTypes.put("XML11IDREF", dvTemp);
        fXML11BuiltInTypes.put("XML11IDREFS", new ListDatatypeValidator(dvTemp));
        dvTemp = new XML11NMTOKENDatatypeValidator();
        fXML11BuiltInTypes.put("XML11NMTOKEN", dvTemp);
        fXML11BuiltInTypes.put("XML11NMTOKENS", new ListDatatypeValidator(dvTemp));
    } // <clinit>


}//XML11DTDDVFactoryImpl

