/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
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
 * 4. The names "Xalan" and "Apache Software Foundation" must
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
 * originally based on software copyright (c) 1999, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */
package org.apache.xalan.processor;

import javax.xml.transform.OutputKeys;

import java.util.Hashtable;

import org.apache.xalan.templates.OutputProperties;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.templates.ElemTemplateElement;

import org.apache.xml.utils.QName;
import org.apache.xml.utils.SystemIDResolver;

import javax.xml.transform.TransformerException;

import org.xml.sax.Attributes;

/**
 * TransformerFactory for xsl:output markup.
 * @see <a href="http://www.w3.org/TR/xslt#dtd">XSLT DTD</a>
 * @see <a href="http://www.w3.org/TR/xslt#output">output in XSLT Specification</a>
 */
class ProcessorOutputElem extends XSLTElementProcessor
{

  /** The output properties, set temporarily while the properties are 
   *  being set from the attributes, and then nulled after that operation 
   *  is completed.  */
  private OutputProperties m_outputProperties;

  /**
   * Set the cdata-section-elements property from the attribute value.
   * @see javax.xml.transform.OutputKeys#CDATA_SECTION_ELEMENTS
   * @param newValue non-null reference to processed attribute value.
   */
  public void setCdataSectionElements(java.util.Vector newValue)
  {
    m_outputProperties.setQNameProperties(OutputKeys.CDATA_SECTION_ELEMENTS, newValue);
  }

  /**
   * Set the doctype-public property from the attribute value.
   * @see javax.xml.transform.OutputKeys#DOCTYPE_PUBLIC
   * @param newValue non-null reference to processed attribute value.
   */
  public void setDoctypePublic(String newValue)
  {
    m_outputProperties.setProperty(OutputKeys.DOCTYPE_PUBLIC, newValue);
  }

  /**
   * Set the doctype-system property from the attribute value.
   * @see javax.xml.transform.OutputKeys#DOCTYPE_SYSTEM
   * @param newValue non-null reference to processed attribute value.
   */
  public void setDoctypeSystem(String newValue)
  {
    m_outputProperties.setProperty(OutputKeys.DOCTYPE_SYSTEM, newValue);
  }

  /**
   * Set the encoding property from the attribute value.
   * @see javax.xml.transform.OutputKeys#ENCODING
   * @param newValue non-null reference to processed attribute value.
   */
  public void setEncoding(String newValue)
  {
    m_outputProperties.setProperty(OutputKeys.ENCODING, newValue);
  }

  /**
   * Set the indent property from the attribute value.
   * @see javax.xml.transform.OutputKeys#INDENT
   * @param newValue non-null reference to processed attribute value.
   */
  public void setIndent(boolean newValue)
  {
    m_outputProperties.setBooleanProperty(OutputKeys.INDENT, newValue);
  }

  /**
   * Set the media type property from the attribute value.
   * @see javax.xml.transform.OutputKeys#MEDIA_TYPE
   * @param newValue non-null reference to processed attribute value.
   */
  public void setMediaType(String newValue)
  {
    m_outputProperties.setProperty(OutputKeys.MEDIA_TYPE, newValue);
  }

  /**
   * Set the method property from the attribute value.
   * @see javax.xml.transform.OutputKeys#METHOD
   * @param newValue non-null reference to processed attribute value.
   */
  public void setMethod(org.apache.xml.utils.QName newValue)
  {
    m_outputProperties.setQNameProperty(OutputKeys.METHOD, newValue);
  }

  /**
   * Set the omit-xml-declaration property from the attribute value.
   * @see javax.xml.transform.OutputKeys#OMIT_XML_DECLARATION
   * @param newValue processed attribute value.
   */
  public void setOmitXmlDeclaration(boolean newValue)
  {
    m_outputProperties.setBooleanProperty(OutputKeys.OMIT_XML_DECLARATION, newValue);
  }

  /**
   * Set the standalone property from the attribute value.
   * @see javax.xml.transform.OutputKeys#STANDALONE
   * @param newValue processed attribute value.
   */
  public void setStandalone(boolean newValue)
  {
    m_outputProperties.setBooleanProperty(OutputKeys.STANDALONE, newValue);
  }

  /**
   * Set the version property from the attribute value.
   * @see javax.xml.transform.OutputKeys#VERSION
   * @param newValue non-null reference to processed attribute value.
   */
  public void setVersion(String newValue)
  {
    m_outputProperties.setProperty(OutputKeys.VERSION, newValue);
  }
  
  /**
   * Set a foreign property from the attribute value.
   * @param newValue non-null reference to attribute value.
   */
  public void setForeignAttr(String attrUri, String attrLocalName, String attrRawName, String attrValue)
  {
    QName key = new QName(attrUri, attrLocalName);
    m_outputProperties.setProperty(key, attrValue);
  }
  
  /**
   * Set a foreign property from the attribute value.
   * @param newValue non-null reference to attribute value.
   */
  public void addLiteralResultAttribute(String attrUri, String attrLocalName, String attrRawName, String attrValue)
  {
    QName key = new QName(attrUri, attrLocalName);
    m_outputProperties.setProperty(key, attrValue);
  }

  /**
   * Receive notification of the start of an xsl:output element.
   *
   * @param handler The calling StylesheetHandler/TemplatesBuilder.
   * @param uri The Namespace URI, or the empty string if the
   *        element has no Namespace URI or if Namespace
   *        processing is not being performed.
   * @param localName The local name (without prefix), or the
   *        empty string if Namespace processing is not being
   *        performed.
   * @param rawName The raw XML 1.0 name (with prefix), or the
   *        empty string if raw names are not available.
   * @param attributes The attributes attached to the element.  If
   *        there are no attributes, it shall be an empty
   *        Attributes object.
   *
   * @throws org.xml.sax.SAXException
   */
  public void startElement(
          StylesheetHandler handler, String uri, String localName, String rawName, Attributes attributes)
            throws org.xml.sax.SAXException
  {
    // Hmmm... for the moment I don't think I'll have default properties set for this. -sb
    m_outputProperties = new OutputProperties();

    m_outputProperties.setDOMBackPointer(handler.getOriginatingNode());
    m_outputProperties.setLocaterInfo(handler.getLocator());
    m_outputProperties.setUid(handler.nextUid());
    setPropertiesFromAttributes(handler, rawName, attributes, this);
    
    // Access this only from the Hashtable level... we don't want to 
    // get default properties.
    String entitiesFileName =
      (String) m_outputProperties.getProperties().get(OutputProperties.S_KEY_ENTITIES);

    if (null != entitiesFileName)
    {
      try
      {
        String absURL = SystemIDResolver.getAbsoluteURI(entitiesFileName,
                    handler.getBaseIdentifier());
        m_outputProperties.getProperties().put(OutputProperties.S_KEY_ENTITIES, absURL);
      }
      catch(TransformerException te)
      {
        handler.error(te.getMessage(), te);
      }
    }
    
    handler.getStylesheet().setOutput(m_outputProperties);
    
    ElemTemplateElement parent = handler.getElemTemplateElement();
    parent.appendChild(m_outputProperties);
    
    m_outputProperties = null;
  }
}