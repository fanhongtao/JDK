/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights
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

package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import java.util.Stack;
import java.util.Vector;

import com.sun.org.apache.xerces.internal.impl.validation.ValidationState;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaNamespaceSupport;
import com.sun.org.apache.xerces.internal.impl.xs.SchemaSymbols;
import com.sun.org.apache.xerces.internal.impl.xs.XMLSchemaException;
import com.sun.org.apache.xerces.internal.impl.xs.util.XInt;
import com.sun.org.apache.xerces.internal.util.DOMUtil;
import com.sun.org.apache.xerces.internal.util.SymbolTable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/*
 * Objects of this class hold all information pecular to a
 * particular XML Schema document.  This is needed because
 * namespace bindings and other settings on the <schema/> element
 * affect the contents of that schema document alone.
 *
 * @author Neil Graham, IBM
 * @version $Id: XSDocumentInfo.java,v 1.17 2003/07/03 15:17:16 neilg Exp $
 */
class XSDocumentInfo {

    // Data
    protected SchemaNamespaceSupport fNamespaceSupport;
    protected SchemaNamespaceSupport fNamespaceSupportRoot;
    protected Stack SchemaNamespaceSupportStack = new Stack();

    // schema's attributeFormDefault
    protected boolean fAreLocalAttributesQualified;

    // elementFormDefault
    protected boolean fAreLocalElementsQualified;

    // [block | final]Default
    protected short fBlockDefault;
    protected short fFinalDefault;

    // targetNamespace
    String fTargetNamespace;

    // represents whether this is a chameleon schema (i.e., whether its TNS is natural or comes from without)
    protected boolean fIsChameleonSchema;

    // the root of the schema Document tree itself
    protected Document fSchemaDoc;

    // all namespaces that this document can refer to
    Vector fImportedNS = new Vector();
    
    protected ValidationState fValidationContext = new ValidationState();

    SymbolTable fSymbolTable = null;

    // attribute checker to which we'll return the attributes 
    // once we've been told that we're done with them
    protected XSAttributeChecker fAttrChecker;

    // array of objects on the schema's root element.  This is null
    // once returnSchemaAttrs has been called.
    protected Object [] fSchemaAttrs;

    // note that the caller must ensure to call returnSchemaAttrs()
    // to avoid memory leaks!
    XSDocumentInfo (Document schemaDoc, XSAttributeChecker attrChecker, SymbolTable symbolTable)
                    throws XMLSchemaException {
        fSchemaDoc = schemaDoc;
        fNamespaceSupport = new SchemaNamespaceSupport();
        fNamespaceSupport.reset();
        fIsChameleonSchema = false;

        fSymbolTable = symbolTable;
        fAttrChecker = attrChecker;

        if(schemaDoc != null) {
            Element root = DOMUtil.getRoot(schemaDoc);
            fSchemaAttrs = attrChecker.checkAttributes(root, true, this);
            // schemaAttrs == null means it's not an <xsd:schema> element
            // throw an exception, but we don't know the document systemId,
            // so we leave that to the caller.
            if (fSchemaAttrs == null) {
                throw new XMLSchemaException(null, null);
            }
            fAreLocalAttributesQualified =
                ((XInt)fSchemaAttrs[XSAttributeChecker.ATTIDX_AFORMDEFAULT]).intValue() == SchemaSymbols.FORM_QUALIFIED;
            fAreLocalElementsQualified =
                ((XInt)fSchemaAttrs[XSAttributeChecker.ATTIDX_EFORMDEFAULT]).intValue() == SchemaSymbols.FORM_QUALIFIED;
            fBlockDefault =
                ((XInt)fSchemaAttrs[XSAttributeChecker.ATTIDX_BLOCKDEFAULT]).shortValue();
            fFinalDefault =
                ((XInt)fSchemaAttrs[XSAttributeChecker.ATTIDX_FINALDEFAULT]).shortValue();
            fTargetNamespace =
                (String)fSchemaAttrs[XSAttributeChecker.ATTIDX_TARGETNAMESPACE];
            if (fTargetNamespace != null)
                fTargetNamespace = symbolTable.addSymbol(fTargetNamespace);

            fNamespaceSupportRoot = new SchemaNamespaceSupport(fNamespaceSupport);

            //set namespace support
            fValidationContext.setNamespaceSupport(fNamespaceSupport);
            fValidationContext.setSymbolTable(symbolTable);
            // pass null as the schema document, so that the namespace
            // context is not popped.

            // don't return the attribute array yet!
            //attrChecker.returnAttrArray(schemaAttrs, null);
        }
    }

    // backup the current ns support, and use the one passed-in.
    // if no ns support is passed-in, use the one for <schema> element
    void backupNSSupport(SchemaNamespaceSupport nsSupport) {
        SchemaNamespaceSupportStack.push(fNamespaceSupport);
        if (nsSupport == null)
            nsSupport = fNamespaceSupportRoot;
        fNamespaceSupport = new SchemaNamespaceSupport(nsSupport);

        fValidationContext.setNamespaceSupport(fNamespaceSupport);
    }

    void restoreNSSupport() {
        fNamespaceSupport = (SchemaNamespaceSupport)SchemaNamespaceSupportStack.pop();
        fValidationContext.setNamespaceSupport(fNamespaceSupport);
    }

    // some Object methods
    public String toString() {
        return fTargetNamespace == null?"no targetNamspace":"targetNamespace is " + fTargetNamespace;
    }

    public void addAllowedNS(String namespace) {
        fImportedNS.addElement(namespace == null ? "" : namespace);
    }
    
    public boolean isAllowedNS(String namespace) {
        return fImportedNS.contains(namespace == null ? "" : namespace);
    }
    
    // store whether we have reported an error about that this document
    // can't access components from the given namespace
    private Vector fReportedTNS = null;
    // check whether we need to report an error against the given uri.
    // if we have reported an error, then we don't need to report again;
    // otherwise we reported the error, and remember this fact.
    final boolean needReportTNSError(String uri) {
        if (fReportedTNS == null)
            fReportedTNS = new Vector();
        else if (fReportedTNS.contains(uri))
            return false;
        fReportedTNS.addElement(uri);
        return true;
    }

    // return the attributes on the schema element itself:
    Object [] getSchemaAttrs () {
        return fSchemaAttrs;
    }

    // deallocate the storage set aside for the schema element's
    // attributes
    void returnSchemaAttrs () {
        fAttrChecker.returnAttrArray (fSchemaAttrs, null);
        fSchemaAttrs = null;
    }
    
} // XSDocumentInfo
