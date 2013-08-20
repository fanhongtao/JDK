/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2002, 2003 The Apache Software Foundation.  
 * All rights reserved.
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

package com.sun.org.apache.xerces.internal.impl.xs;

import com.sun.org.apache.xerces.internal.util.XMLResourceIdentifierImpl;
import com.sun.org.apache.xerces.internal.xni.QName;
import com.sun.org.apache.xerces.internal.xni.XMLAttributes;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLGrammarDescription;
import com.sun.org.apache.xerces.internal.xni.grammars.XMLSchemaDescription;

/*
 * All information specific to XML Schema grammars.
 *
 * @author Neil Graham, IBM
 * @author Neeraj Bajaj, SUN Microsystems.
 *
 * @version $Id: XSDDescription.java,v 1.9 2003/03/24 21:10:58 sandygao Exp $
 */
public class XSDDescription extends XMLResourceIdentifierImpl 
                implements XMLSchemaDescription {
    // used to indicate what triggered the call
    /**
     * Indicate that this description was just initialized. 
     */
    public final static short CONTEXT_INITIALIZE = -1;
    /**
     * Indicate that the current schema document is <include>d by another
     * schema document.
     */
    public final static short CONTEXT_INCLUDE   = 0;
    /**
     * Indicate that the current schema document is <redefine>d by another
     * schema document.
     */
    public final static short CONTEXT_REDEFINE  = 1;
    /**
     * Indicate that the current schema document is <import>ed by another
     * schema document.
     */
    public final static short CONTEXT_IMPORT    = 2;
    /**
     * Indicate that the current schema document is being preparsed.
     */
    public final static short CONTEXT_PREPARSE  = 3;
    /**
     * Indicate that the parse of the current schema document is triggered
     * by xsi:schemaLocation/noNamespaceSchemaLocation attribute(s) in the
     * instance document. This value is only used if we don't defer the loading
     * of schema documents.
     */
    public final static short CONTEXT_INSTANCE  = 4;
    /**
     * Indicate that the parse of the current schema document is triggered by
     * the occurrence of an element whose namespace is the target namespace
     * of this schema document. This value is only used if we do defer the
     * loading of schema documents until a component from that namespace is
     * referenced from the instance.
     */
    public final static short CONTEXT_ELEMENT   = 5;
    /**
     * Indicate that the parse of the current schema document is triggered by
     * the occurrence of an attribute whose namespace is the target namespace
     * of this schema document. This value is only used if we do defer the
     * loading of schema documents until a component from that namespace is
     * referenced from the instance.
     */
    public final static short CONTEXT_ATTRIBUTE = 6;
    /**
     * Indicate that the parse of the current schema document is triggered by
     * the occurrence of an "xsi:type" attribute, whose value (a QName) has
     * the target namespace of this schema document as its namespace.
     * This value is only used if we do defer the loading of schema documents
     * until a component from that namespace is referenced from the instance.
     */
    public final static short CONTEXT_XSITYPE   = 7;

    // REVISIT: write description of these fields	
    protected short fContextType;
    protected String [] fLocationHints ;
    protected QName fTriggeringComponent;
    protected QName fEnclosedElementName;
    protected XMLAttributes  fAttributes;
        
    /**
     * the type of the grammar (e.g., DTD or XSD);
     *  
     * @see com.sun.org.apache.xerces.internal.xni.grammars.Grammar
     */
    public String getGrammarType() {
        return XMLGrammarDescription.XML_SCHEMA;
    }

    /**
     * Get the context. The returned value is one of the pre-defined
     * CONTEXT_xxx constants.
     * 
     * @return  the value indicating the context
     */
    public short getContextType() {
        return fContextType ;
    }

    /**
     * If the context is "include" or "redefine", then return the target
     * namespace of the enclosing schema document; otherwise, the expected
     * target namespace of this document.
     * 
     * @return  the expected/enclosing target namespace
     */
    public String getTargetNamespace() {
        return fNamespace;
    }

    /**
     * For import and references from the instance document, it's possible to
     * have multiple hints for one namespace. So this method returns an array,
     * which contains all location hints.
     * 
     * @return  an array of all location hints associated to the expected
     *          target namespace
     */          
    public String[] getLocationHints() {
        return fLocationHints ;
    }

    /**
     * If a call is triggered by an element/attribute/xsi:type in the instance,
     * this call returns the name of such triggering component: the name of
     * the element/attribute, or the value of the xsi:type.
     * 
     * @return  the name of the triggering component
     */
    public QName getTriggeringComponent() {
        return fTriggeringComponent ;
    }

    /**
     * If a call is triggered by an attribute or xsi:type, then this mehtod
     * returns the enclosing element of such element.
     * 
     * @return  the name of the enclosing element
     */
    public QName getEnclosingElementName() {
        return fEnclosedElementName ;
    }
    
    /**
     * If a call is triggered by an element/attribute/xsi:type in the instance,
     * this call returns all attribute of such element (or enclosing element).
     * 
     * @return  all attributes of the tiggering/enclosing element
     */
    public XMLAttributes getAttributes() {
        return fAttributes;
    }
    
    public boolean fromInstance() {
        return fContextType == CONTEXT_ATTRIBUTE ||
               fContextType == CONTEXT_ELEMENT ||
               fContextType == CONTEXT_INSTANCE ||
               fContextType == CONTEXT_XSITYPE;
    }
    
    /**
     * Compares this grammar with the given grammar. Currently, we compare 
     * the target namespaces.
     * 
     * @param desc The description of the grammar to be compared with
     * @return     True if they are equal, else false
     */
    public boolean equals(Object descObj) {
        if(!(descObj instanceof XMLSchemaDescription)) return false;
        XMLSchemaDescription desc = (XMLSchemaDescription)descObj;
        if (fNamespace != null)
            return fNamespace.equals(desc.getTargetNamespace());
        else // fNamespace == null
            return desc.getTargetNamespace() == null;
    }
    
    /**
     * Returns the hash code of this grammar
     * 
     * @return The hash code
     */
    public int hashCode() {
         return (fNamespace == null) ? 0 : fNamespace.hashCode();
    }
    
    public void setContextType(short contextType){
        fContextType = contextType ;
    }

    public void setTargetNamespace(String targetNamespace){
        fNamespace = targetNamespace ;
    }

    public void setLocationHints(String [] locationHints){
        int length = locationHints.length ;
        fLocationHints  = new String[length];
        System.arraycopy(locationHints, 0, fLocationHints, 0, length);
        //fLocationHints = locationHints ;
    }

    public void setTriggeringComponent(QName triggeringComponent){
        fTriggeringComponent = triggeringComponent ;
    }

    public void setEnclosingElementName(QName enclosedElementName){
        fEnclosedElementName = enclosedElementName ;
    }

    public void setAttributes(XMLAttributes attributes){
        fAttributes = attributes ;    
    }
    
    /**
     *  resets all the fields
     */
    public void reset(){
        super.clear();
        fContextType = CONTEXT_INITIALIZE;
        fLocationHints  = null ;
        fTriggeringComponent = null ;
        fEnclosedElementName = null ;
        fAttributes = null ;    
    }
    
    public XSDDescription makeClone() {
        XSDDescription desc = new XSDDescription();
        desc.fAttributes = this.fAttributes;
        desc.fBaseSystemId = this.fBaseSystemId;
        desc.fContextType = this.fContextType;
        desc.fEnclosedElementName = this.fEnclosedElementName;
        desc.fExpandedSystemId = this.fExpandedSystemId;
        desc.fLiteralSystemId = this.fLiteralSystemId;
        desc.fLocationHints = this.fLocationHints;
        desc.fPublicId = this.fPublicId;
        desc.fNamespace = this.fNamespace;
        desc.fTriggeringComponent = this.fTriggeringComponent;
        return desc;
    }
    
} // XSDDescription
