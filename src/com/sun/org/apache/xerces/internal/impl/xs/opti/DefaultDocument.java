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

package com.sun.org.apache.xerces.internal.impl.xs.opti;

import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.w3c.dom.Element;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.DocumentType;
import org.w3c.dom.CDATASection;
import org.w3c.dom.EntityReference;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.ProcessingInstruction;

import org.w3c.dom.DOMException;


/**
 * @author Rahul Srivastava, Sun Microsystems Inc.
 *
 * @version $Id: DefaultDocument.java,v 1.5 2003/11/13 22:47:16 elena Exp $
 */
public class DefaultDocument extends NodeImpl
                             implements Document {

    // default constructor
    public DefaultDocument() {
    }
    
    //
    // org.w3c.dom.Document methods
    //
    
    public DocumentType getDoctype() {
	return null;
    }


    public DOMImplementation getImplementation() {
	return null;
    }


    public Element getDocumentElement() {
	return null;
    }


    public NodeList getElementsByTagName(String tagname) {
	return null;
    }


    public NodeList getElementsByTagNameNS(String namespaceURI, String localName) {
	return null;
    }


    public Element getElementById(String elementId) {
	return null;
    }


    public Node importNode(Node importedNode, boolean deep) throws DOMException {
	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }


    public Element createElement(String tagName) throws DOMException {
	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }


    public DocumentFragment createDocumentFragment() {
	return null;
    }


    public Text createTextNode(String data) {
	return null;
    }

    public Comment createComment(String data) {
	return null;
    }
    

    public CDATASection createCDATASection(String data) throws DOMException {
	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }


    public ProcessingInstruction createProcessingInstruction(String target, String data) throws DOMException {
	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }


    public Attr createAttribute(String name) throws DOMException {
	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }


    public EntityReference createEntityReference(String name) throws DOMException {
	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }


    public Element createElementNS(String namespaceURI, String qualifiedName) throws DOMException {
	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }


    public Attr createAttributeNS(String namespaceURI, String qualifiedName) throws DOMException {
	throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }

    // DOM Level 3 methods.
    
    public String getInputEncoding(){
        return null;
    }
    
    /**
    public void setInputEncoding(String actualEncoding){
       throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }
	*/
	
    public String getXmlEncoding(){
        return null;
    }
    
    
    /**
     * An attribute specifying, as part of the XML declaration, the encoding 
     * of this document. This is <code>null</code> when unspecified.
     * @since DOM Level 3
    public void setXmlEncoding(String encoding){
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }
     */
     
    /**
     * An attribute specifying, as part of the XML declaration, whether this 
     * document is standalone.
     * <br> This attribute represents the property [standalone] defined in . 
     * @since DOM Level 3
     */
    public boolean getXmlStandalone(){
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }
    /**
     * An attribute specifying, as part of the XML declaration, whether this 
     * document is standalone.
     * <br> This attribute represents the property [standalone] defined in . 
     * @since DOM Level 3
     */
    public void setXmlStandalone(boolean standalone){
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }

    /**
     * An attribute specifying, as part of the XML declaration, the version 
     * number of this document. This is <code>null</code> when unspecified.
     * <br> This attribute represents the property [version] defined in . 
     * @exception DOMException
     *   NOT_SUPPORTED_ERR: Raised if the version is set to a value that is 
     *   not supported by this <code>Document</code>.
     * @since DOM Level 3
     */
    public String getXmlVersion(){
        return null;
    }
    /**
     * An attribute specifying, as part of the XML declaration, the version 
     * number of this document. This is <code>null</code> when unspecified.
     * <br> This attribute represents the property [version] defined in . 
     * @exception DOMException
     *   NOT_SUPPORTED_ERR: Raised if the version is set to a value that is 
     *   not supported by this <code>Document</code>.
     * @since DOM Level 3
     */
    public void setXmlVersion(String version) throws DOMException{
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }

    /**
     * An attribute specifying whether errors checking is enforced or not. 
     * When set to <code>false</code>, the implementation is free to not 
     * test every possible error case normally defined on DOM operations, 
     * and not raise any <code>DOMException</code>. In case of error, the 
     * behavior is undefined. This attribute is <code>true</code> by 
     * defaults.
     * @since DOM Level 3
     */
    public boolean getStrictErrorChecking(){
        return false;
    }
    /**
     * An attribute specifying whether errors checking is enforced or not. 
     * When set to <code>false</code>, the implementation is free to not 
     * test every possible error case normally defined on DOM operations, 
     * and not raise any <code>DOMException</code>. In case of error, the 
     * behavior is undefined. This attribute is <code>true</code> by 
     * defaults.
     * @since DOM Level 3
     */
    public void setStrictErrorChecking(boolean strictErrorChecking){
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }

    /**
     * The location of the document or <code>null</code> if undefined.
     * <br>Beware that when the <code>Document</code> supports the feature 
     * "HTML" , the href attribute of the HTML BASE element takes precedence 
     * over this attribute.
     * @since DOM Level 3
     */
    public String getDocumentURI(){
        return null;
    }
    /**
     * The location of the document or <code>null</code> if undefined.
     * <br>Beware that when the <code>Document</code> supports the feature 
     * "HTML" , the href attribute of the HTML BASE element takes precedence 
     * over this attribute.
     * @since DOM Level 3
     */
    public void setDocumentURI(String documentURI){
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }

    /** DOM Level 3*/
    public Node adoptNode(Node source) throws DOMException{
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }
    
    /** DOM Level 3*/
    public void normalizeDocument(){
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }

    /**
     *  The configuration used when <code>Document.normalizeDocument</code> is 
     * invoked. 
     * @since DOM Level 3
     */
    public DOMConfiguration getDomConfig(){
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }
    
    /** DOM Level 3*/
    public Node renameNode(Node n,String namespaceURI, String name) throws DOMException{
        throw new DOMException(DOMException.NOT_SUPPORTED_ERR, "Method not supported");
    }
    




    


}
