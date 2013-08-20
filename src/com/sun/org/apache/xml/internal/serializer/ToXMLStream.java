/*
 * Copyright 2001-2004 The Apache Software Foundation.
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
 * $Id: ToXMLStream.java,v 1.13 2004/02/18 22:57:44 minchau Exp $
 */
 package com.sun.org.apache.xml.internal.serializer;

import java.io.IOException;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;

import com.sun.org.apache.xml.internal.res.XMLErrorResources;
import com.sun.org.apache.xml.internal.res.XMLMessages;
import org.xml.sax.SAXException;

/**
 * @author Santiago Pericas-Geertsen
 * @author G. Todd Miller 
 */
public class ToXMLStream extends ToStream
{

    /**
     * remembers if we need to write out "]]>" to close the CDATA
     */
    boolean m_cdataTagOpen = false;


    /**
     * Map that tells which XML characters should have special treatment, and it
     *  provides character to entity name lookup.
     */
    protected static CharInfo m_xmlcharInfo =
//      new CharInfo(CharInfo.XML_ENTITIES_RESOURCE);
        CharInfo.getCharInfo(CharInfo.XML_ENTITIES_RESOURCE, Method.XML);

    /**
     * Default constructor.
     */
    public ToXMLStream()
    {
        m_charInfo = m_xmlcharInfo;

        initCDATA();
        // initialize namespaces
        m_prefixMap = new NamespaceMappings();

    }

    /**
     * Copy properties from another SerializerToXML.
     *
     * @param xmlListener non-null reference to a SerializerToXML object.
     */
    public void CopyFrom(ToXMLStream xmlListener)
    {

        m_writer = xmlListener.m_writer;


        // m_outputStream = xmlListener.m_outputStream;
        String encoding = xmlListener.getEncoding();
        setEncoding(encoding);

        setOmitXMLDeclaration(xmlListener.getOmitXMLDeclaration());

        m_ispreserve = xmlListener.m_ispreserve;
        m_preserves = xmlListener.m_preserves;
        m_isprevtext = xmlListener.m_isprevtext;
        m_doIndent = xmlListener.m_doIndent;
        setIndentAmount(xmlListener.getIndentAmount());
        m_startNewLine = xmlListener.m_startNewLine;
        m_needToOutputDocTypeDecl = xmlListener.m_needToOutputDocTypeDecl;
        setDoctypeSystem(xmlListener.getDoctypeSystem());
        setDoctypePublic(xmlListener.getDoctypePublic());        
        setStandalone(xmlListener.getStandalone());
        setMediaType(xmlListener.getMediaType());
        m_maxCharacter = xmlListener.m_maxCharacter;
        m_spaceBeforeClose = xmlListener.m_spaceBeforeClose;
        m_cdataStartCalled = xmlListener.m_cdataStartCalled;

    }

    /**
     * Receive notification of the beginning of a document.
     *
     * @throws org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     *
     * @throws org.xml.sax.SAXException
     */
    public void startDocumentInternal() throws org.xml.sax.SAXException
    {

        if (m_needToCallStartDocument)
        { 
            super.startDocumentInternal();
            m_needToCallStartDocument = false;

            if (m_inEntityRef)
                return;

            m_needToOutputDocTypeDecl = true;
            m_startNewLine = false;

            if (getOmitXMLDeclaration() == false)
            {
                String encoding = Encodings.getMimeEncoding(getEncoding());
                String version = getVersion();
                if (version == null)
                    version = "1.0";
                String standalone;

                if (m_standaloneWasSpecified)
                {
                    standalone = " standalone=\"" + getStandalone() + "\"";
                }
                else
                {
                    standalone = "";
                }

                try
                {
                    final java.io.Writer writer = m_writer;
                    writer.write("<?xml version=\"");
                    writer.write(version);
                    writer.write("\" encoding=\"");
                    writer.write(encoding);
                    writer.write('\"');
                    writer.write(standalone);
                    writer.write("?>");
                    if (m_doIndent)
                        writer.write(m_lineSep, 0, m_lineSepLen);
                } 
                catch(IOException e)
                {
                    throw new SAXException(e);
                }

            }
        }
    }

    /**
     * Receive notification of the end of a document.
     *
     * @throws org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     *
     * @throws org.xml.sax.SAXException
     */
    public void endDocument() throws org.xml.sax.SAXException
    {
        flushPending();
        if (m_doIndent && !m_isprevtext)
        {
            try
            {
            outputLineSep();
            }
            catch(IOException e)
            {
                throw new SAXException(e);
            }
        }

        flushWriter();
        
        if (m_tracer != null)
            super.fireEndDoc();
    }

    /**
     * Starts a whitespace preserving section. All characters printed
     * within a preserving section are printed without indentation and
     * without consolidating multiple spaces. This is equivalent to
     * the <tt>xml:space=&quot;preserve&quot;</tt> attribute. Only XML
     * and HTML serializers need to support this method.
     * <p>
     * The contents of the whitespace preserving section will be delivered
     * through the regular <tt>characters</tt> event.
     *
     * @throws org.xml.sax.SAXException
     */
    public void startPreserving() throws org.xml.sax.SAXException
    {

        // Not sure this is really what we want.  -sb
        m_preserves.push(true);

        m_ispreserve = true;
    }

    /**
     * Ends a whitespace preserving section.
     *
     * @see #startPreserving
     *
     * @throws org.xml.sax.SAXException
     */
    public void endPreserving() throws org.xml.sax.SAXException
    {

        // Not sure this is really what we want.  -sb
        m_ispreserve = m_preserves.isEmpty() ? false : m_preserves.pop();
    }

    /**
     * Receive notification of a processing instruction.
     *
     * @param target The processing instruction target.
     * @param data The processing instruction data, or null if
     *        none was supplied.
     * @throws org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     *
     * @throws org.xml.sax.SAXException
     */
    public void processingInstruction(String target, String data)
        throws org.xml.sax.SAXException
    {
        if (m_inEntityRef)
            return;
        
        flushPending();   

        if (target.equals(Result.PI_DISABLE_OUTPUT_ESCAPING))
        {
            startNonEscaping();
        }
        else if (target.equals(Result.PI_ENABLE_OUTPUT_ESCAPING))
        {
            endNonEscaping();
        }
        else
        {
            try
            {
                if (m_elemContext.m_startTagOpen)
                {
                    closeStartTag();
                    m_elemContext.m_startTagOpen = false;
                }

                if (shouldIndent())
                    indent();

                final java.io.Writer writer = m_writer;
                writer.write("<?");
                writer.write(target);

                if (data.length() > 0
                    && !Character.isSpaceChar(data.charAt(0)))
                    writer.write(' ');

                int indexOfQLT = data.indexOf("?>");

                if (indexOfQLT >= 0)
                {

                    // See XSLT spec on error recovery of "?>" in PIs.
                    if (indexOfQLT > 0)
                    {
                        writer.write(data.substring(0, indexOfQLT));
                    }

                    writer.write("? >"); // add space between.

                    if ((indexOfQLT + 2) < data.length())
                    {
                        writer.write(data.substring(indexOfQLT + 2));
                    }
                }
                else
                {
                    writer.write(data);
                }

                writer.write('?');
                writer.write('>');

                // Always output a newline char if not inside of an
                // element. The whitespace is not significant in that
                // case.
                if (m_elemContext.m_currentElemDepth <= 0)
                    writer.write(m_lineSep, 0, m_lineSepLen);

                m_startNewLine = true;
            }
            catch(IOException e)
            {
                throw new SAXException(e);
            }
        }
        
        if (m_tracer != null)
            super.fireEscapingEvent(target, data);  
    }

    /**
     * Receive notivication of a entityReference.
     *
     * @param name The name of the entity.
     *
     * @throws org.xml.sax.SAXException
     */
    public void entityReference(String name) throws org.xml.sax.SAXException
    {
        if (m_elemContext.m_startTagOpen)
        {
            closeStartTag();
            m_elemContext.m_startTagOpen = false;
        }

        try
        {
            if (shouldIndent())
                indent();

            final java.io.Writer writer = m_writer;
            writer.write('&');
            writer.write(name);
            writer.write(';');
        }
        catch(IOException e)
        {
            throw new SAXException(e);
        }
        
        if (m_tracer != null)
            super.fireEntityReference(name);            
    }

    /**
     * This method is used to add an attribute to the currently open element. 
     * The caller has guaranted that this attribute is unique, which means that it
     * not been seen before and will not be seen again.
     * 
     * @param name the qualified name of the attribute
     * @param value the value of the attribute which can contain only
     * ASCII printable characters characters in the range 32 to 127 inclusive.
     * @param flags the bit values of this integer give optimization information.
     */
    public void addUniqueAttribute(String name, String value, int flags)
        throws SAXException
    {
        if (m_elemContext.m_startTagOpen)
        {
           
            try
            {
                final String patchedName = patchName(name);
                final java.io.Writer writer = m_writer;
                if ((flags & NO_BAD_CHARS) > 0 && m_xmlcharInfo.onlyQuotAmpLtGt)
                {
                    // "flags" has indicated that the characters
                    // '>'  '<'   '&'  and '"' are not in the value and
                    // m_htmlcharInfo has recorded that there are no other
                    // entities in the range 32 to 127 so we write out the
                    // value directly
                    
                    writer.write(' ');
                    writer.write(patchedName);
                    writer.write("=\"");
                    writer.write(value);
                    writer.write('"');
                }
                else
                {
                    writer.write(' ');
                    writer.write(patchedName);
                    writer.write("=\"");
                    writeAttrString(writer, value, this.getEncoding());
                    writer.write('"');
                }
            } catch (IOException e) {
                throw new SAXException(e);
            }
        }
    }

    public void addAttribute(
        String uri,
        String localName,
        String rawName,
        String type,
        String value)
        throws SAXException
    {
        if (m_elemContext.m_startTagOpen)
        {
            if (!rawName.startsWith("xmlns"))
            {
                String prefixUsed =
                    ensureAttributesNamespaceIsDeclared(
                        uri,
                        localName,
                        rawName);
                if (prefixUsed != null
                    && rawName != null
                    && !rawName.startsWith(prefixUsed))
                {
                    // use a different raw name, with the prefix used in the
                    // generated namespace declaration
                    rawName = prefixUsed + ":" + localName;

                }
            }
            addAttributeAlways(uri, localName, rawName, type, value);
        }
        else
        {
            /*
             * The startTag is closed, yet we are adding an attribute?
             *
             * Section: 7.1.3 Creating Attributes Adding an attribute to an
             * element after a PI (for example) has been added to it is an
             * error. The attributes can be ignored. The spec doesn't explicitly
             * say this is disallowed, as it does for child elements, but it
             * makes sense to have the same treatment.
             *
             * We choose to ignore the attribute which is added too late.
             */
            // Generate a warning of the ignored attributes

            // Create the warning message
            String msg = XMLMessages.createXMLMessage(
                    XMLErrorResources.ER_ILLEGAL_ATTRIBUTE_POSITION,new Object[]{ localName });

            try {
                // Prepare to issue the warning message
                Transformer tran = super.getTransformer();
                ErrorListener errHandler = tran.getErrorListener();


                // Issue the warning message
                if (null != errHandler && m_sourceLocator != null)
                  errHandler.warning(new TransformerException(msg, m_sourceLocator));
                else
                  System.out.println(msg);
                }
            catch (Exception e){}             
        }
    }

    /**
     * @see com.sun.org.apache.xml.internal.serializer.ExtendedContentHandler#endElement(String)
     */
    public void endElement(String elemName) throws SAXException
    {
        endElement(null, null, elemName);
    }

    /**
     * From XSLTC
     * Related to startPrefixMapping ???
     */
    public void namespaceAfterStartElement(
        final String prefix,
        final String uri)
        throws SAXException
    {

        // hack for XSLTC with finding URI for default namespace
        if (m_elemContext.m_elementURI == null)
        {
            String prefix1 = getPrefixPart(m_elemContext.m_elementName);
            if (prefix1 == null && EMPTYSTRING.equals(prefix))
            {
                // the elements URI is not known yet, and it
                // doesn't have a prefix, and we are currently
                // setting the uri for prefix "", so we have
                // the uri for the element... lets remember it
                m_elemContext.m_elementURI = uri;
            }
        }            
        startPrefixMapping(prefix,uri,false);
        return;

    }

    /**
     * From XSLTC
     * Declare a prefix to point to a namespace URI. Inform SAX handler
     * if this is a new prefix mapping.
     */
    protected boolean pushNamespace(String prefix, String uri)
    {
        try
        {
            if (m_prefixMap.pushNamespace(
                prefix, uri, m_elemContext.m_currentElemDepth))
            {
                startPrefixMapping(prefix, uri);
                return true;
            }
        }
        catch (SAXException e)
        {
            // falls through
        }
        return false;
    }
    /**
     * Try's to reset the super class and reset this class for 
     * re-use, so that you don't need to create a new serializer 
     * (mostly for performance reasons).
     * 
     * @return true if the class was successfuly reset.
     */
    public boolean reset()
    {
        boolean wasReset = false;
        if (super.reset())
        {
            resetToXMLStream();
            wasReset = true;
        }
        return wasReset;
    }
    
    /**
     * Reset all of the fields owned by ToStream class
     *
     */
    private void resetToXMLStream()
    {
        this.m_cdataTagOpen = false;

    }  

}
