/*
 * Copyright 2003-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id: EmptySerializer.java,v 1.5 2004/02/17 04:18:19 minchau Exp $
 */
package com.sun.org.apache.xml.internal.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import javax.xml.transform.SourceLocator;
import javax.xml.transform.Transformer;

import org.w3c.dom.Node;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * This class is an adapter class. Its only purpose is to be extended and
 * for that extended class to over-ride all methods that are to be used. 
 */
public class EmptySerializer implements SerializationHandler
{
    protected static final String ERR = "EmptySerializer method not over-ridden";
    /**
     * @see com.sun.org.apache.xml.internal.serializer.SerializationHandler#asContentHandler()
     */

    private static void throwUnimplementedException()
    {
        /* TODO: throw this exception for real.
         * Some users of this class do not over-ride all methods that 
         * they use, which is a violation of the intended use of this
         * class. Those tests used to end in error, but fail when this
         * exception is enabled.  Perhaps that is an indication of what
         * the true problem is.  Such tests include copy56,58,59,60 for 
         * both Xalan-J interpretive and for XSLTC. - bjm
         */
        // throw new RuntimeException(err);
        return;
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.SerializationHandler#asContentHandler()
     */
    public ContentHandler asContentHandler() throws IOException
    {
        throwUnimplementedException();
        return null;
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.SerializationHandler#setContentHandler(org.xml.sax.ContentHandler)
     */
    public void setContentHandler(ContentHandler ch)
    {
        throwUnimplementedException();
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.SerializationHandler#close()
     */
    public void close()
    {
        throwUnimplementedException();
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.SerializationHandler#getOutputFormat()
     */
    public Properties getOutputFormat()
    {
        throwUnimplementedException();
        return null;
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.SerializationHandler#getOutputStream()
     */
    public OutputStream getOutputStream()
    {
        throwUnimplementedException();
        return null;
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.SerializationHandler#getWriter()
     */
    public Writer getWriter()
    {
        throwUnimplementedException();
        return null;
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.SerializationHandler#reset()
     */
    public boolean reset()
    {
        throwUnimplementedException();
        return false;
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.SerializationHandler#serialize(org.w3c.dom.Node)
     */
    public void serialize(Node node) throws IOException
    {
        throwUnimplementedException();
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.SerializationHandler#setCdataSectionElements(java.util.Vector)
     */
    public void setCdataSectionElements(Vector URI_and_localNames)
    {
        throwUnimplementedException();
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.SerializationHandler#setEscaping(boolean)
     */
    public boolean setEscaping(boolean escape) throws SAXException
    {
        throwUnimplementedException();
        return false;
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.SerializationHandler#setIndent(boolean)
     */
    public void setIndent(boolean indent)
    {
        throwUnimplementedException();
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.SerializationHandler#setIndentAmount(int)
     */
    public void setIndentAmount(int spaces)
    {
        throwUnimplementedException();
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.SerializationHandler#setOutputFormat(java.util.Properties)
     */
    public void setOutputFormat(Properties format)
    {
        throwUnimplementedException();
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.SerializationHandler#setOutputStream(java.io.OutputStream)
     */
    public void setOutputStream(OutputStream output)
    {
        throwUnimplementedException();
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.SerializationHandler#setVersion(java.lang.String)
     */
    public void setVersion(String version)
    {
        throwUnimplementedException();
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.SerializationHandler#setWriter(java.io.Writer)
     */
    public void setWriter(Writer writer)
    {
        throwUnimplementedException();
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.SerializationHandler#setTransformer(javax.xml.transform.Transformer)
     */
    public void setTransformer(Transformer transformer)
    {
        throwUnimplementedException();
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.SerializationHandler#getTransformer()
     */
    public Transformer getTransformer()
    {
        throwUnimplementedException();
        return null;
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.SerializationHandler#flushPending()
     */
    public void flushPending() throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.ExtendedContentHandler#addAttribute(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void addAttribute(
        String uri,
        String localName,
        String rawName,
        String type,
        String value)
        throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.ExtendedContentHandler#addAttributes(org.xml.sax.Attributes)
     */
    public void addAttributes(Attributes atts) throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.ExtendedContentHandler#addAttribute(java.lang.String, java.lang.String)
     */
    public void addAttribute(String name, String value)
    {
        throwUnimplementedException();
    }

    /**
     * @see com.sun.org.apache.xml.internal.serializer.ExtendedContentHandler#characters(java.lang.String)
     */
    public void characters(String chars) throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.ExtendedContentHandler#endElement(java.lang.String)
     */
    public void endElement(String elemName) throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.ExtendedContentHandler#startDocument()
     */
    public void startDocument() throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.ExtendedContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void startElement(String uri, String localName, String qName)
        throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.ExtendedContentHandler#startElement(java.lang.String)
     */
    public void startElement(String qName) throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.ExtendedContentHandler#namespaceAfterStartElement(java.lang.String, java.lang.String)
     */
    public void namespaceAfterStartElement(String uri, String prefix)
        throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.ExtendedContentHandler#startPrefixMapping(java.lang.String, java.lang.String, boolean)
     */
    public boolean startPrefixMapping(
        String prefix,
        String uri,
        boolean shouldFlush)
        throws SAXException
    {
        throwUnimplementedException();
        return false;
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.ExtendedContentHandler#entityReference(java.lang.String)
     */
    public void entityReference(String entityName) throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.ExtendedContentHandler#getNamespaceMappings()
     */
    public NamespaceMappings getNamespaceMappings()
    {
        throwUnimplementedException();
        return null;
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.ExtendedContentHandler#getPrefix(java.lang.String)
     */
    public String getPrefix(String uri)
    {
        throwUnimplementedException();
        return null;
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.ExtendedContentHandler#getNamespaceURI(java.lang.String, boolean)
     */
    public String getNamespaceURI(String name, boolean isElement)
    {
        throwUnimplementedException();
        return null;
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.ExtendedContentHandler#getNamespaceURIFromPrefix(java.lang.String)
     */
    public String getNamespaceURIFromPrefix(String prefix)
    {
        throwUnimplementedException();
        return null;
    }
    /**
     * @see org.xml.sax.ContentHandler#setDocumentLocator(org.xml.sax.Locator)
     */
    public void setDocumentLocator(Locator arg0)
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ContentHandler#endDocument()
     */
    public void endDocument() throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ContentHandler#startPrefixMapping(java.lang.String, java.lang.String)
     */
    public void startPrefixMapping(String arg0, String arg1)
        throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ContentHandler#endPrefixMapping(java.lang.String)
     */
    public void endPrefixMapping(String arg0) throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    public void startElement(
        String arg0,
        String arg1,
        String arg2,
        Attributes arg3)
        throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
     */
    public void endElement(String arg0, String arg1, String arg2)
        throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    public void characters(char[] arg0, int arg1, int arg2) throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ContentHandler#ignorableWhitespace(char[], int, int)
     */
    public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
        throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ContentHandler#processingInstruction(java.lang.String, java.lang.String)
     */
    public void processingInstruction(String arg0, String arg1)
        throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ContentHandler#skippedEntity(java.lang.String)
     */
    public void skippedEntity(String arg0) throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.ExtendedLexicalHandler#comment(java.lang.String)
     */
    public void comment(String comment) throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ext.LexicalHandler#startDTD(java.lang.String, java.lang.String, java.lang.String)
     */
    public void startDTD(String arg0, String arg1, String arg2)
        throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ext.LexicalHandler#endDTD()
     */
    public void endDTD() throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ext.LexicalHandler#startEntity(java.lang.String)
     */
    public void startEntity(String arg0) throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ext.LexicalHandler#endEntity(java.lang.String)
     */
    public void endEntity(String arg0) throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ext.LexicalHandler#startCDATA()
     */
    public void startCDATA() throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ext.LexicalHandler#endCDATA()
     */
    public void endCDATA() throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ext.LexicalHandler#comment(char[], int, int)
     */
    public void comment(char[] arg0, int arg1, int arg2) throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.XSLOutputAttributes#getDoctypePublic()
     */
    public String getDoctypePublic()
    {
        throwUnimplementedException();
        return null;
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.XSLOutputAttributes#getDoctypeSystem()
     */
    public String getDoctypeSystem()
    {
        throwUnimplementedException();
        return null;
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.XSLOutputAttributes#getEncoding()
     */
    public String getEncoding()
    {
        throwUnimplementedException();
        return null;
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.XSLOutputAttributes#getIndent()
     */
    public boolean getIndent()
    {
        throwUnimplementedException();
        return false;
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.XSLOutputAttributes#getIndentAmount()
     */
    public int getIndentAmount()
    {
        throwUnimplementedException();
        return 0;
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.XSLOutputAttributes#getMediaType()
     */
    public String getMediaType()
    {
        throwUnimplementedException();
        return null;
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.XSLOutputAttributes#getOmitXMLDeclaration()
     */
    public boolean getOmitXMLDeclaration()
    {
        throwUnimplementedException();
        return false;
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.XSLOutputAttributes#getStandalone()
     */
    public String getStandalone()
    {
        throwUnimplementedException();
        return null;
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.XSLOutputAttributes#getVersion()
     */
    public String getVersion()
    {
        throwUnimplementedException();
        return null;
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.XSLOutputAttributes#setCdataSectionElements
     */
    public void setCdataSectionElements(Hashtable h) throws Exception
    {
        throwUnimplementedException();
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.XSLOutputAttributes#setDoctype(java.lang.String, java.lang.String)
     */
    public void setDoctype(String system, String pub)
    {
        throwUnimplementedException();
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.XSLOutputAttributes#setDoctypePublic(java.lang.String)
     */
    public void setDoctypePublic(String doctype)
    {
        throwUnimplementedException();
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.XSLOutputAttributes#setDoctypeSystem(java.lang.String)
     */
    public void setDoctypeSystem(String doctype)
    {
        throwUnimplementedException();
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.XSLOutputAttributes#setEncoding(java.lang.String)
     */
    public void setEncoding(String encoding)
    {
        throwUnimplementedException();
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.XSLOutputAttributes#setMediaType(java.lang.String)
     */
    public void setMediaType(String mediatype)
    {
        throwUnimplementedException();
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.XSLOutputAttributes#setOmitXMLDeclaration(boolean)
     */
    public void setOmitXMLDeclaration(boolean b)
    {
        throwUnimplementedException();
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.XSLOutputAttributes#setStandalone(java.lang.String)
     */
    public void setStandalone(String standalone)
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ext.DeclHandler#elementDecl(java.lang.String, java.lang.String)
     */
    public void elementDecl(String arg0, String arg1) throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ext.DeclHandler#attributeDecl(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void attributeDecl(
        String arg0,
        String arg1,
        String arg2,
        String arg3,
        String arg4)
        throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ext.DeclHandler#internalEntityDecl(java.lang.String, java.lang.String)
     */
    public void internalEntityDecl(String arg0, String arg1)
        throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ext.DeclHandler#externalEntityDecl(java.lang.String, java.lang.String, java.lang.String)
     */
    public void externalEntityDecl(String arg0, String arg1, String arg2)
        throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ErrorHandler#warning(org.xml.sax.SAXParseException)
     */
    public void warning(SAXParseException arg0) throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
     */
    public void error(SAXParseException arg0) throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see org.xml.sax.ErrorHandler#fatalError(org.xml.sax.SAXParseException)
     */
    public void fatalError(SAXParseException arg0) throws SAXException
    {
        throwUnimplementedException();
    }
    /**
     * @see com.sun.org.apache.xml.internal.serializer.Serializer#asDOMSerializer()
     */
    public DOMSerializer asDOMSerializer() throws IOException
    {
        throwUnimplementedException();
        return null;
    }

    /**
     * @see com.sun.org.apache.xml.internal.serializer.SerializationHandler#setNamespaceMappings(NamespaceMappings)
     */
    public void setNamespaceMappings(NamespaceMappings mappings) {
        throwUnimplementedException();
    }
    
    /**
     * @see com.sun.org.apache.xml.internal.serializer.ExtendedContentHandler#setSourceLocator(javax.xml.transform.SourceLocator)
     */
    public void setSourceLocator(SourceLocator locator)
    {
        throwUnimplementedException();
    }

    /**
     * @see com.sun.org.apache.xml.internal.serializer.ExtendedContentHandler#addUniqueAttribute(java.lang.String, java.lang.String, int)
     */
    public void addUniqueAttribute(String name, String value, int flags)
        throws SAXException
    {
        throwUnimplementedException();
    }
}
