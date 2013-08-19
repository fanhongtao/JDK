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
package org.apache.xalan.transformer;

import java.util.Enumeration;

import org.apache.xalan.processor.StylesheetHandler;
import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.templates.Stylesheet;
import org.apache.xalan.templates.StylesheetRoot;
import org.apache.xalan.templates.ElemTemplate;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xalan.trace.TraceManager;
import org.apache.xalan.trace.GenerateEvent;
import org.apache.xml.utils.MutableAttrListImpl;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.TreeWalker;
import org.apache.xml.utils.ObjectPool;
import org.apache.xml.utils.XMLCharacterRecognizer;
import org.apache.xpath.objects.XObject;
import org.apache.xpath.XPathContext;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMFilter;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXException;
import org.xml.sax.ErrorHandler;

import org.xml.sax.helpers.NamespaceSupport;
import org.apache.xml.utils.NamespaceSupport2;
import org.xml.sax.Locator;

import javax.xml.transform.TransformerException;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;

/**
 * This class is a layer between the direct calls to the result
 * tree content handler, and the transformer.  For one thing,
 * we have to delay the call to
 * getContentHandler().startElement(name, atts) because of the
 * xsl:attribute and xsl:copy calls.  In other words,
 * the attributes have to be fully collected before you
 * can call startElement.
 */
public class ResultTreeHandler extends QueuedEvents
        implements ContentHandler, LexicalHandler, TransformState,
        org.apache.xml.dtm.ref.dom2dtm.DOM2DTM.CharacterNodeHandler,
        ErrorHandler
{

  /** Indicate whether running in Debug mode */
  private static final boolean DEBUG = false;

  /**
   * Null constructor for object pooling.
   */
  public ResultTreeHandler(){}

  /**
   * Create a new result tree handler.  The real content
   * handler will be the ContentHandler passed as an argument.
   *
   * @param transformer non-null transformer instance
   * @param realHandler Content Handler instance
   */
  public ResultTreeHandler(TransformerImpl transformer,
                           ContentHandler realHandler)
  {
    init(transformer, realHandler);
  }

  /**
   * Initializer method.
   *
   * @param transformer non-null transformer instance
   * @param realHandler Content Handler instance
   */
  public void init(TransformerImpl transformer, ContentHandler realHandler)
  {

    m_transformer = transformer;

    // m_startDoc.setTransformer(m_transformer);
    TraceManager tracer = transformer.getTraceManager();

    if ((null != tracer) && tracer.hasTraceListeners())
      m_tracer = tracer;
    else
      m_tracer = null;

    // m_startDoc.setTraceManager(m_tracer);
    m_contentHandler = realHandler;

    // m_startDoc.setContentHandler(m_contentHandler);
    if (m_contentHandler instanceof LexicalHandler)
      m_lexicalHandler = (LexicalHandler) m_contentHandler;
    else
      m_lexicalHandler = null;

    m_isTransformClient = (m_contentHandler instanceof TransformerClient);

    m_cloner = new ClonerToResultTree(transformer, this);

    // The stylesheet is set at a rather late stage, so I do 
    // this here, though it would probably be better done elsewhere.
    if (null != m_transformer)
      m_stylesheetRoot = m_transformer.getStylesheet();

    pushDocumentEvent();  // not pending yet.
  }

  /**
   * Bottleneck the startDocument event.
   *
   * @throws org.xml.sax.SAXException
   */
  public void startDocument() throws org.xml.sax.SAXException{}

  /**
   * Bottleneck the endDocument event.  This may be called
   * more than once in order to make sure the pending start
   * document is called.
   *
   * @throws org.xml.sax.SAXException
   */
  public void endDocument() throws org.xml.sax.SAXException
  {

    flushPending(true);

    if (!m_docEnded)
    {
      m_contentHandler.endDocument();

      if (null != m_tracer)
      {
        GenerateEvent ge =
          new GenerateEvent(m_transformer,
                            GenerateEvent.EVENTTYPE_ENDDOCUMENT, null);

        m_tracer.fireGenerateEvent(ge);
      }

      m_docEnded = true;
      m_docPending = false;
    }
  }

  /**
   * Bottleneck the startElement event.  This is used to "pend" an
   * element, so that attributes can still be added to it before
   * the real "startElement" is called on the result tree listener.
   *
   * @param ns Namespace URI of element
   * @param localName Local part of qname of element
   * @param name Name of element
   * @param atts List of attributes for the element
   *
   * @throws org.xml.sax.SAXException
   */
  public void startElement(
          String ns, String localName, String name, Attributes atts)
            throws org.xml.sax.SAXException
  {

    if (DEBUG)
    {
      if (m_elemIsPending)
        System.out.println("(ResultTreeHandler#startElement - pended: "
                           + m_url + "#" + m_localName);

      System.out.println("ResultTreeHandler#startElement: " + ns + "#"
                         + localName);

      //      if(null == ns)
      //      {
      //        (new RuntimeException(localName+" has a null namespace!")).printStackTrace();
      //      }
    }

    if(m_docPending)
      checkForSerializerSwitch(ns, localName);
      
    flushPending(true);

    if (!m_nsContextPushed)
    {
      if (DEBUG)
        System.out.println(
          "ResultTreeHandler#startElement - push(startElement)");

      m_nsSupport.pushContext();

      m_nsContextPushed = true;
    }
    
    if (ns != null)
      ensurePrefixIsDeclared(ns, name);

    m_name = name;
    m_url = ns;
    m_localName = localName;

    if (null != atts)
      m_attributes.addAttributes(atts);

    m_elemIsPending = true;
    m_elemIsEnded = false;
    
    if(m_isTransformClient && (null != m_transformer))
    {
      m_snapshot.m_currentElement = m_transformer.getCurrentElement();
      m_snapshot.m_currentTemplate = m_transformer.getCurrentTemplate();
      m_snapshot.m_matchedTemplate = m_transformer.getMatchedTemplate();
      int currentNodeHandle = m_transformer.getCurrentNode();
      DTM dtm = m_transformer.getXPathContext().getDTM(currentNodeHandle);
      m_snapshot.m_currentNode = dtm.getNode(currentNodeHandle);
      m_snapshot.m_matchedNode = m_transformer.getMatchedNode();
      m_snapshot.m_contextNodeList = m_transformer.getContextNodeList(); // TODO: Need to clone
    }
    // initQSE(m_startElement);

    m_eventCount++;
  }

  /**
   * Bottleneck the endElement event.
   *
   * @param ns Namespace URI of element
   * @param localName Local part of qname of element
   * @param name Name of element
   *
   * @throws org.xml.sax.SAXException
   */
  public void endElement(String ns, String localName, String name)
          throws org.xml.sax.SAXException
  {

    if (DEBUG)
    {
      if (m_elemIsPending)
        System.out.println("(ResultTreeHandler#endElement - pended: "
                           + m_url + "#" + m_localName);

      System.out.println("ResultTreeHandler#endElement: " + ns + "#"
                         + localName);
    }

    flushPending(true);
    m_contentHandler.endElement(ns, localName, name);

    if (null != m_tracer)
    {
      GenerateEvent ge = new GenerateEvent(m_transformer,
                                           GenerateEvent.EVENTTYPE_ENDELEMENT,
                                           name, (Attributes)null);

      m_tracer.fireGenerateEvent(ge);
    }

    sendEndPrefixMappings();
    popEvent();

    if (DEBUG)
      System.out.println("ResultTreeHandler#startElement pop: " + localName);

    m_nsSupport.popContext();
  }

  /** Indicate whether a namespace context was pushed */
  boolean m_nsContextPushed = false;

  /**
   * Begin the scope of a prefix-URI Namespace mapping.
   *
   * <p>The information from this event is not necessary for
   * normal Namespace processing: the SAX XML reader will
   * automatically replace prefixes for element and attribute
   * names when the http://xml.org/sax/features/namespaces
   * feature is true (the default).</p>
   *
   * <p>There are cases, however, when applications need to
   * use prefixes in character data or in attribute values,
   * where they cannot safely be expanded automatically; the
   * start/endPrefixMapping event supplies the information
   * to the application to expand prefixes in those contexts
   * itself, if necessary.</p>
   *
   * <p>Note that start/endPrefixMapping events are not
   * guaranteed to be properly nested relative to each-other:
   * all startPrefixMapping events will occur before the
   * corresponding startElement event, and all endPrefixMapping
   * events will occur after the corresponding endElement event,
   * but their order is not guaranteed.</p>
   *
   * @param prefix The Namespace prefix being declared.
   * @param uri The Namespace URI the prefix is mapped to.
   * @throws org.xml.sax.SAXException The client may throw
   *            an exception during processing.
   * @see #endPrefixMapping
   * @see #startElement
   */
  public void startPrefixMapping(String prefix, String uri)
          throws org.xml.sax.SAXException
  {
    startPrefixMapping(prefix, uri, true);
  }

  /**
   * Begin the scope of a prefix-URI Namespace mapping.
   *
   *
   * @param prefix The Namespace prefix being declared.
   * @param uri The Namespace URI the prefix is mapped to.
   * @param shouldFlush Indicate whether pending events needs
   * to be flushed first
   *
   * @throws org.xml.sax.SAXException The client may throw
   *            an exception during processing.
   */
  public void startPrefixMapping(
          String prefix, String uri, boolean shouldFlush)
            throws org.xml.sax.SAXException
  {

    if (shouldFlush)
      flushPending(false);

    if (!m_nsContextPushed)
    {
      if (DEBUG)
        System.out.println(
          "ResultTreeHandler#startPrefixMapping push(startPrefixMapping: "
          + prefix + ")");

      m_nsSupport.pushContext();

      m_nsContextPushed = true;
    }

    if (null == prefix)
      prefix = "";  // bit-o-hack, that that's OK

    String existingURI = m_nsSupport.getURI(prefix);

    if (null == existingURI)
      existingURI = "";

    if (null == uri)
      uri = "";

    if (!existingURI.equals(uri))
    {
      if (DEBUG)
      {
        System.out.println("ResultTreeHandler#startPrefixMapping Prefix: "
                           + prefix);
        System.out.println("ResultTreeHandler#startPrefixMapping uri: "
                           + uri);
      }

      m_nsSupport.declarePrefix(prefix, uri);
    }
  }

  /**
   * End the scope of a prefix-URI mapping.
   *
   * <p>See startPrefixMapping for details.  This event will
   * always occur after the corresponding endElement event,
   * but the order of endPrefixMapping events is not otherwise
   * guaranteed.</p>
   *
   * @param prefix The prefix that was being mapping.
   * @throws org.xml.sax.SAXException The client may throw
   *            an exception during processing.
   * @see #startPrefixMapping
   * @see #endElement
   */
  public void endPrefixMapping(String prefix)
          throws org.xml.sax.SAXException{}

  /**
   * Bottleneck the characters event.
   *
   * @param ch Array of characters to process
   * @param start start of characters in the array
   * @param length Number of characters in the array
   *
   * @throws org.xml.sax.SAXException
   */
  public void characters(char ch[], int start, int length)
          throws org.xml.sax.SAXException
  {

    // It would be nice to suppress all whitespace before the
    // first element, but this is going to cause potential problems with 
    // text serialization and with text entities (right term?).
    // So this really needs to be done at the serializer level.

    /*if (m_startDoc.isPending
    && XMLCharacterRecognizer.isWhiteSpace(ch, start, length))
    return;*/
    if (DEBUG)
    {
      System.out.print("ResultTreeHandler#characters: ");

      int n = start + length;

      for (int i = start; i < n; i++)
      {
        if (Character.isWhitespace(ch[i]))
          System.out.print("\\" + ((int) ch[i]));
        else
          System.out.print(ch[i]);
      }

      System.out.println("");
    }

    flushPending(true);
    m_contentHandler.characters(ch, start, length);

    if (null != m_tracer)
    {
      GenerateEvent ge = new GenerateEvent(m_transformer,
                                           GenerateEvent.EVENTTYPE_CHARACTERS,
                                           ch, start, length);

      m_tracer.fireGenerateEvent(ge);
    }
  }
  
  public void characters(org.w3c.dom.Node node)
          throws org.xml.sax.SAXException
  {

    flushPending(true);
    
    if(m_isTransformClient)
      m_snapshot.m_currentNode = node;

    String data = node.getNodeValue();
    char [] ch = null;
    int length = 0;
    if (data != null)
    {
    ch = data.toCharArray();
    length = data.length();
    m_contentHandler.characters(ch, 0, length);
    }
    
    if (null != m_tracer)
    {
      GenerateEvent ge = new GenerateEvent(m_transformer,
                                           GenerateEvent.EVENTTYPE_CHARACTERS,
                                           ch, 0, length);

      m_tracer.fireGenerateEvent(ge);
    }  
    if(m_isTransformClient)
      m_snapshot.m_currentNode = null;
  }

  /**
   * Bottleneck the ignorableWhitespace event.
   *
   * @param ch Array of characters to process
   * @param start start of characters in the array
   * @param length Number of characters in the array
   *
   * @throws org.xml.sax.SAXException
   */
  public void ignorableWhitespace(char ch[], int start, int length)
          throws org.xml.sax.SAXException
  {

    if (m_docPending
            && XMLCharacterRecognizer.isWhiteSpace(ch, start, length))
      return;

    flushPending(true);
    m_contentHandler.ignorableWhitespace(ch, start, length);

    if (null != m_tracer)
    {
      GenerateEvent ge =
        new GenerateEvent(m_transformer,
                          GenerateEvent.EVENTTYPE_IGNORABLEWHITESPACE, ch,
                          start, length);

      m_tracer.fireGenerateEvent(ge);
    }
  }

  /**
   * Bottleneck the processingInstruction event.
   *
   * @param target Processing instruction target name
   * @param data Processing instruction data
   *
   * @throws org.xml.sax.SAXException
   */
  public void processingInstruction(String target, String data)
          throws org.xml.sax.SAXException
  {

    flushPending(true);
    m_contentHandler.processingInstruction(target, data);

    if (null != m_tracer)
    {
      GenerateEvent ge = new GenerateEvent(m_transformer,
                                           GenerateEvent.EVENTTYPE_PI,
                                           target, data);

      m_tracer.fireGenerateEvent(ge);
    }
  }

  /**
   * Bottleneck the comment event.
   *
   * @param data Comment data
   *
   * @throws org.xml.sax.SAXException
   */
  public void comment(String data) throws org.xml.sax.SAXException
  {

    flushPending(true);

    if (null != m_lexicalHandler)
    {
      m_lexicalHandler.comment(data.toCharArray(), 0, data.length());
    }

    if (null != m_tracer)
    {
      GenerateEvent ge = new GenerateEvent(m_transformer,
                                           GenerateEvent.EVENTTYPE_COMMENT,
                                           data);

      m_tracer.fireGenerateEvent(ge);
    }
  }

  /**
   * Bottleneck the comment event.
   *
   * @param ch Character array with comment data
   * @param start start of characters in the array
   * @param length number of characters in the array
   *
   * @throws org.xml.sax.SAXException
   */
  public void comment(char ch[], int start, int length)
          throws org.xml.sax.SAXException
  {

    flushPending(true);

    if (null != m_lexicalHandler)
    {
      m_lexicalHandler.comment(ch, start, length);
    }

    if (null != m_tracer)
    {
      GenerateEvent ge = new GenerateEvent(m_transformer,
                                           GenerateEvent.EVENTTYPE_COMMENT,
                                           new String(ch, start, length));

      m_tracer.fireGenerateEvent(ge);
    }
  }

  /**
   * Entity reference event.
   *
   * @param name Name of entity
   *
   * @throws org.xml.sax.SAXException
   */
  public void entityReference(String name) throws org.xml.sax.SAXException
  {

    flushPending(true);

    if (null != m_lexicalHandler)
    {
      m_lexicalHandler.startEntity(name);
      m_lexicalHandler.endEntity(name);
    }

    if (null != m_tracer)
    {
      GenerateEvent ge = new GenerateEvent(m_transformer,
                                           GenerateEvent.EVENTTYPE_ENTITYREF,
                                           name);

      m_tracer.fireGenerateEvent(ge);
    }
  }

  /**
   * Start an entity.
   *
   * @param name Name of the entity
   *
   * @throws org.xml.sax.SAXException
   */
  public void startEntity(String name) throws org.xml.sax.SAXException
  {

    flushPending(true);

    if (null != m_lexicalHandler)
    {
      m_lexicalHandler.startEntity(name);
    }
  }

  /**
   * End an entity.
   *
   * @param name Name of the entity
   *
   * @throws org.xml.sax.SAXException
   */
  public void endEntity(String name) throws org.xml.sax.SAXException
  {

    flushPending(true);

    if (null != m_lexicalHandler)
    {
      m_lexicalHandler.endEntity(name);
    }

    if (null != m_tracer)
    {
      GenerateEvent ge = new GenerateEvent(m_transformer,
                                           GenerateEvent.EVENTTYPE_ENTITYREF,
                                           name);

      m_tracer.fireGenerateEvent(ge);
    }
  }

  /**
   * Start the DTD.
   *
   * @param s1 The document type name.
   * @param s2 The declared public identifier for the
   *        external DTD subset, or null if none was declared.
   * @param s3 The declared system identifier for the
   *        external DTD subset, or null if none was declared.
   *
   * @throws org.xml.sax.SAXException
   */
  public void startDTD(String s1, String s2, String s3)
          throws org.xml.sax.SAXException
  {

    flushPending(true);

    if (null != m_lexicalHandler)
    {
      m_lexicalHandler.startDTD(s1, s2, s3);
    }
  }

  /**
   * End the DTD.
   *
   * @throws org.xml.sax.SAXException
   */
  public void endDTD() throws org.xml.sax.SAXException
  {

    flushPending(true);

    if (null != m_lexicalHandler)
    {
      m_lexicalHandler.endDTD();
    }
  }

  /**
   * Start the CDATACharacters.
   *
   * @throws org.xml.sax.SAXException
   */
  public void startCDATA() throws org.xml.sax.SAXException
  {

    flushPending(true);

    if (null != m_lexicalHandler)
    {
      m_lexicalHandler.startCDATA();
    }
  }

  /**
   * End the CDATA characters.
   *
   * @throws org.xml.sax.SAXException
   */
  public void endCDATA() throws org.xml.sax.SAXException
  {

    flushPending(true);

    if (null != m_lexicalHandler)
    {
      m_lexicalHandler.endCDATA();
    }
  }

  /**
   * Receive notification of a skipped entity.
   *
   * <p>The Parser will invoke this method once for each entity
   * skipped.  Non-validating processors may skip entities if they
   * have not seen the declarations (because, for example, the
   * entity was declared in an external DTD subset).  All processors
   * may skip external entities, depending on the values of the
   * http://xml.org/sax/features/external-general-entities and the
   * http://xml.org/sax/features/external-parameter-entities
   * properties.</p>
   *
   * @param name The name of the skipped entity.  If it is a
   *        parameter entity, the name will begin with '%'.
   * @throws org.xml.sax.SAXException Any SAX exception, possibly
   *            wrapping another exception.
   */
  public void skippedEntity(String name) throws org.xml.sax.SAXException{}

  /**
   * Set whether Namespace declarations have been added to
   * this element
   *
   *
   * @param b Flag indicating whether Namespace declarations
   * have been added to this element
   */
  public void setNSDeclsHaveBeenAdded(boolean b)
  {

    m_nsDeclsHaveBeenAdded = b;
  }

  /**
   * Flush the event.
   *
   * @throws TransformerException
   *
   * @throws org.xml.sax.SAXException
   */
  void flushDocEvent() throws org.xml.sax.SAXException
  {

    if (m_docPending)
    {
      m_contentHandler.startDocument();

      if (null != m_tracer)
      {
        GenerateEvent ge =
          new GenerateEvent(m_transformer,
                            GenerateEvent.EVENTTYPE_STARTDOCUMENT);

        m_tracer.fireGenerateEvent(ge);
      }

      if (m_contentHandler instanceof TransformerClient)
      {
        ((TransformerClient) m_contentHandler).setTransformState(this);
      }

      m_docPending = false;
    }
  }
  
  /**
   * Flush the event.
   *
   * @throws SAXException
   */
  void flushElem() throws org.xml.sax.SAXException
  {

    if (m_elemIsPending)
    {
      if (null != m_name)
      {
        try
        {
          m_contentHandler.startElement(m_url, m_localName, m_name,
                                      m_attributes);
        }
        catch(Exception re)
        {
          // If we don't do this, and the exception is a RuntimeException, 
          // good line numbers of where the exception occured in the stylesheet
          // won't get reported.  I tried just catching RuntimeException, but 
          // for whatever reason it didn't seem to catch.
          // Fix for Christina's DOMException error problem.
          throw new SAXParseException(re.getMessage(), 
          m_transformer.getCurrentElement().getPublicId(), 
          m_transformer.getCurrentElement().getSystemId(), 
          m_transformer.getCurrentElement().getLineNumber(), 
          m_transformer.getCurrentElement().getColumnNumber(), 
          re);
        }
        
        if(null != m_tracer)
        {
          GenerateEvent ge =
            new GenerateEvent(m_transformer,
                              GenerateEvent.EVENTTYPE_STARTELEMENT, m_name,
                                      m_attributes);
  
          m_tracer.fireGenerateEvent(ge);
        }
        if(m_isTransformClient)
          m_snapshot.m_currentNode = null;
      }

      m_elemIsPending = false;
      m_attributes.clear();
  
      m_nsDeclsHaveBeenAdded = false;
      m_name = null;
      m_url = null;
      m_localName = null;
      m_namespaces = null;

      // super.flush();
    }
  }


  /**
   * Flush the pending element.
   *
   * @throws org.xml.sax.SAXException
   */
  public final void flushPending() throws org.xml.sax.SAXException
  {
    flushPending(true);
  }

  /**
   * Flush the pending element, and any attributes associated with it.
   *
   * NOTE: If there are attributes but _no_ pending element (which can
   * happen if the user's stylesheet is doing something inappropriate),
   * we still want to make sure they are flushed.
   *
   * @param type Event type
   *
   * NEEDSDOC @param flushPrefixes
   *
   * @throws org.xml.sax.SAXException
   */
  public final void flushPending(boolean flushPrefixes)
          throws org.xml.sax.SAXException
  {

    if (flushPrefixes && m_docPending)
    {
      flushDocEvent();
    }

    if (m_elemIsPending)
    {
      // Combined loop shoud be much more efficient.
      // %REVIEW% %OPT% Will the "else" case ever arise?
      if (!m_nsDeclsHaveBeenAdded)
//        addNSDeclsToAttrs();
          startAndAddPrefixMappings();  // new
      else                              // new
          sendStartPrefixMappings();


      if (DEBUG)
      {
        System.out.println("ResultTreeHandler#flushPending - start flush: "
                           + m_name);
      }

      flushElem();

      if (DEBUG)
      {
        System.out.println(
          "ResultTreeHandler#flushPending - after flush, isPending: "
          + m_elemIsPending);
      }

      m_nsContextPushed = false;
    }
  }

  /**
   * Given a result tree fragment, walk the tree and
   * output it to the result stream.
   *
   * @param obj Result tree fragment object
   * @param support XPath context for the result tree fragment
   *
   * @throws org.xml.sax.SAXException
   */
  public void outputResultTreeFragment(XObject obj, XPathContext support)
          throws org.xml.sax.SAXException
  {

    int doc = obj.rtf();
    DTM dtm = support.getDTM(doc);

    if(null != dtm)
    {
	    for (int n = dtm.getFirstChild(doc); DTM.NULL != n;
	            n = dtm.getNextSibling(n))
	    {
	      flushPending(true);  // I think.
          startPrefixMapping("","");
	      dtm.dispatchToEvents(n, this);
	    }
    }
  }

  /**
   * To fullfill the FormatterListener interface... no action
   * for the moment.
   *
   * @param locator Document locator
   */
  public void setDocumentLocator(Locator locator){}

  /**
   * This function checks to make sure a given prefix is really
   * declared.  It might not be, because it may be an excluded prefix.
   * If it's not, it still needs to be declared at this point.
   * TODO: This needs to be done at an earlier stage in the game... -sb
   *
   * @param ns Namespace URI of the element
   * @param rawName Raw name of element (with prefix)
   *
   * @throws org.xml.sax.SAXException
   */
  public void ensurePrefixIsDeclared(String ns, String rawName)
          throws org.xml.sax.SAXException
  {

    if (ns != null && ns.length() > 0)
    {
      int index;
      String prefix = (index = rawName.indexOf(":")) < 0
                      ? "" : rawName.substring(0, index);

      if (null != prefix)
      {
        String foundURI = m_nsSupport.getURI(prefix);

        if ((null == foundURI) ||!foundURI.equals(ns))
        {
          startPrefixMapping(prefix, ns, false);
                                        
          // Bugzilla1133: Generate attribute as well as namespace event.
          // SAX does expect both.

          m_attributes.addAttribute("http://www.w3.org/2000/xmlns/", 
                                    prefix, 
                                    "xmlns"+(prefix.length()==0 ? "" : ":")+prefix, 
                                    "CDATA", ns);
        }
      }
    }
  }

  /**
   * This function checks to make sure a given prefix is really
   * declared.  It might not be, because it may be an excluded prefix.
   * If it's not, it still needs to be declared at this point.
   * TODO: This needs to be done at an earlier stage in the game... -sb
   *
   * @param ns Namespace URI of the element
   * @param rawName Raw name of element (with prefix)
   *
   * NEEDSDOC @param dtm
   * NEEDSDOC @param namespace
   *
   * @throws org.xml.sax.SAXException
   */
  public void ensureNamespaceDeclDeclared(DTM dtm, int namespace)
          throws org.xml.sax.SAXException
  {

    String uri = dtm.getNodeValue(namespace);
    String prefix = dtm.getNodeNameX(namespace);

    if ((uri != null && uri.length() > 0) && (null != prefix))
    {
      String foundURI = m_nsSupport.getURI(prefix);

      if ((null == foundURI) ||!foundURI.equals(uri))
      {
        startPrefixMapping(prefix, uri, false);
      }
    }
  }

  /**
   * Add the attributes that have been declared to the attribute list.
   * (Seems like I shouldn't have to do this...)
   * Internally deprecated in favor of combined startAndAddPrefixMappings();
   * 
   *
   * @throws org.xml.sax.SAXException
   */
  protected void sendStartPrefixMappings() throws org.xml.sax.SAXException
  {
    Enumeration prefixes = m_nsSupport.getDeclaredPrefixes();
    ContentHandler handler = m_contentHandler;
    while (prefixes.hasMoreElements())
    {
      String prefix = (String) prefixes.nextElement();
      handler.startPrefixMapping(prefix, m_nsSupport.getURI(prefix));
    }
  }

  /**
   * Combination of sendStartPrefixMappings and
   * addNSDeclsToAttrs() (which it mostly replaces).  Merging the two
   * loops is significantly more efficient.
   *
   * @throws org.xml.sax.SAXException */
  protected void startAndAddPrefixMappings() throws org.xml.sax.SAXException
  {

    Enumeration prefixes = m_nsSupport.getDeclaredPrefixes();
    ContentHandler handler = m_contentHandler;

    while (prefixes.hasMoreElements())
    {
      String prefix = (String) prefixes.nextElement();
      String uri=m_nsSupport.getURI(prefix);
      
      // Send event
      handler.startPrefixMapping(prefix, uri);

      // Set attribute
      boolean isDefault = (prefix.length() == 0);
      String name;

      if (isDefault)
      {
        //prefix = "xml";
        name = "xmlns";
      }
      else
        name = "xmlns:" + prefix;

      if (null == uri)
        uri = "";

      m_attributes.addAttribute("http://www.w3.org/2000/xmlns/", 
                                prefix, name, "CDATA", uri);
    }
    m_nsDeclsHaveBeenAdded=true;
  }

  /**
   * Add the attributes that have been declared to the attribute list.
   * (Seems like I shouldn't have to do this...)
   *
   * @throws org.xml.sax.SAXException
   */
  protected void sendEndPrefixMappings() throws org.xml.sax.SAXException
  {

    Enumeration prefixes = m_nsSupport.getDeclaredPrefixes();
    ContentHandler handler = m_contentHandler;

    while (prefixes.hasMoreElements())
    {
      String prefix = (String) prefixes.nextElement();

      handler.endPrefixMapping(prefix);
    }
  }

  /**
   * Check to see if we should switch serializers based on the
   * first output element being an HTML element.
   *
   * @param ns Namespace URI of the element
   * @param localName Local part of name of the element
   *
   * @throws org.xml.sax.SAXException
   */
  private void checkForSerializerSwitch(String ns, String localName)
          throws org.xml.sax.SAXException
  {

    try
    {
      if (m_docPending)
      {
        SerializerSwitcher.switchSerializerIfHTML(m_transformer, ns,
                                                  localName);
      }
    }
    catch (TransformerException te)
    {
      throw new org.xml.sax.SAXException(te);
    }
  }

  /**
   * Add the attributes that have been declared to the attribute list.
   *
   * %REVIEW% This should have been done automatically during
   * flushPending(boolean); is it ever explicitly reinvoked?
   */
  public void addNSDeclsToAttrs()
  {

    Enumeration prefixes = m_nsSupport.getDeclaredPrefixes();

    while (prefixes.hasMoreElements())
    {
      String prefix = (String) prefixes.nextElement();
      boolean isDefault = (prefix.length() == 0);
      String name;

      if (isDefault)
      {

        //prefix = "xml";
        name = "xmlns";
      }
      else
        name = "xmlns:" + prefix;

      String uri = m_nsSupport.getURI(prefix);

      if (null == uri)
        uri = "";

      m_attributes.addAttribute("http://www.w3.org/2000/xmlns/", 
                                prefix, name, "CDATA", uri);
      
      m_nsDeclsHaveBeenAdded = true;        
    }

  }

  /**
   * Copy <KBD>xmlns:</KBD> attributes in if not already in scope.
   * 
   * As a quick hack to support ClonerToResultTree, this can also be used
   * to copy an individual namespace node.
   *
   * @param src Source Node
   * NEEDSDOC @param type
   * NEEDSDOC @param dtm
   *
   * @throws TransformerException
   */
  public void processNSDecls(int src, int type, DTM dtm)
          throws TransformerException
  {

    try
    {
      if (type == DTM.ELEMENT_NODE)
      {
        for (int namespace = dtm.getFirstNamespaceNode(src, true);
                DTM.NULL != namespace;
                namespace = dtm.getNextNamespaceNode(src, namespace, true))
        {

          // String prefix = dtm.getPrefix(namespace);
          String prefix = dtm.getNodeNameX(namespace);
          String desturi = getURI(prefix);
          String srcURI = dtm.getNodeValue(namespace);

          if (!srcURI.equalsIgnoreCase(desturi))
          {
            this.startPrefixMapping(prefix, srcURI, false);
          }
        }
      }
      else if (type == DTM.NAMESPACE_NODE)
			{
          String prefix = dtm.getNodeNameX(src);
          String desturi = getURI(prefix);
          String srcURI = dtm.getNodeValue(src);

          if (!srcURI.equalsIgnoreCase(desturi))
          {
            this.startPrefixMapping(prefix, srcURI, false);
          }
			}
    }
    catch (org.xml.sax.SAXException se)
    {
      throw new TransformerException(se);
    }
  }

  /**
   * Given a prefix, return the namespace,
   *
   * @param prefix Given prefix name
   *
   * @return Namespace associated with the given prefix, or null
   */
  public String getURI(String prefix)
  {
    return m_nsSupport.getURI(prefix);
  }

  /**
   * Given a namespace, try and find a prefix.
   *
   * @param namespace Given namespace URI
   *
   * @return Prefix name associated with namespace URI
   */
  public String getPrefix(String namespace)
  {

    // This Enumeration business may be too slow for our purposes...
    Enumeration enum = m_nsSupport.getPrefixes();

    while (enum.hasMoreElements())
    {
      String prefix = (String) enum.nextElement();

      if (m_nsSupport.getURI(prefix).equals(namespace))
        return prefix;
    }

    return null;
  }

  /**
   * Get the NamespaceSupport object.
   *
   * @return NamespaceSupport object.
   */
  public NamespaceSupport getNamespaceSupport()
  {
    return m_nsSupport;
  }

  //  /**
  //   * Override QueuedEvents#initQSE.
  //   *
  //   * @param qse Give queued Sax event
  //   */
  //  protected void initQSE(QueuedSAXEvent qse)
  //  {
  //
  //    // qse.setContentHandler(m_contentHandler);
  //    // qse.setTransformer(m_transformer);
  //    // qse.setTraceManager(m_tracer);
  //  }

  /**
   * Return the current content handler.
   *
   * @return The current content handler, or null if none
   *         has been registered.
   * @see #setContentHandler
   */
  public final ContentHandler getContentHandler()
  {
    return m_contentHandler;
  }

  /**
   * Set the current content handler.
   *
   *
   * @param ch Content Handler to be set
   * @return The current content handler, or null if none
   *         has been registered.
   * @see #getContentHandler
   */
  public void setContentHandler(ContentHandler ch)
  {

    m_contentHandler = ch;

    m_isTransformClient = (m_contentHandler instanceof TransformerClient);

    if (m_contentHandler instanceof LexicalHandler)
      m_lexicalHandler = (LexicalHandler) m_contentHandler;
    else
      m_lexicalHandler = null;

    reInitEvents();
  }

  /**
   * Get a unique namespace value.
   *
   * @return a unique namespace value to be used with a
   * fabricated prefix
   */
  public int getUniqueNSValue()
  {
    return m_uniqueNSValue++;
  }

  /**
   * Get new unique namespace prefix.
   *
   * @return Unique fabricated prefix.
   */
  public String getNewUniqueNSPrefix()
  {
    return S_NAMESPACEPREFIX + String.valueOf(getUniqueNSValue());
  }

  /**
   * Get the pending attributes.  We have to delay the call to
   * m_flistener.startElement(name, atts) because of the
   * xsl:attribute and xsl:copy calls.  In other words,
   * the attributes have to be fully collected before you
   * can call startElement.
   *
   * @return the pending attributes.
   */
  public MutableAttrListImpl getPendingAttributes()
  {
    return m_attributes;
  }

  /**
   * Add an attribute to the end of the list.
   *
   * <p>Do not pass in xmlns decls to this function!
   *
   * <p>For the sake of speed, this method does no checking
   * to see if the attribute is already in the list: that is
   * the responsibility of the application.</p>
   *
   * @param uri The Namespace URI, or the empty string if
   *        none is available or Namespace processing is not
   *        being performed.
   * @param localName The local name, or the empty string if
   *        Namespace processing is not being performed.
   * @param rawName The raw XML 1.0 name, or the empty string
   *        if raw names are not available.
   * @param type The attribute type as a string.
   * @param value The attribute value.
   *
   * @throws TransformerException
   */
  public void addAttribute(
          String uri, String localName, String rawName, String type, String value)
            throws TransformerException
  {
    // %REVIEW% See Bugzilla 4344. Do we need an "else" that announces
    // an error? Technically, this can't happen unless the stylesheet
    // is unreasonable... but it's unclear whether silent or noisy
    // failure is called for.
    // Will add an "else" and emit a warning message.  This should
    // cover testcases such as copyerr04-07, attribset19,34,35, 
    // attribseterr08...(is)
    if (m_elemIsPending)
    {
        // %REVIEW% %OPT% Is this ever needed?????
        // The check is not needed. See Bugzilla 10306. 
        // if (!m_nsDeclsHaveBeenAdded)
        addNSDeclsToAttrs();

        if (null == uri) { // defensive, should not really need this.
            uri = "";
        }

        try {
            if (!rawName.equals("xmlns")) { // don't handle xmlns default namespace.
                ensurePrefixIsDeclared(uri, rawName);
            }    
        } catch (org.xml.sax.SAXException se) {
            throw new TransformerException(se);
        }
      
        if (DEBUG) {
            System.out.println("ResultTreeHandler#addAttribute Adding attr: "
			   + localName + ", " + uri);
        }
        
        if (!isDefinedNSDecl(rawName, value)) {
            m_attributes.addAttribute(uri, localName, rawName, type, value);
        }
    } else {
        m_transformer.getMsgMgr().warn(m_stylesheetRoot,
                                   XSLTErrorResources.WG_ILLEGAL_ATTRIBUTE_POSITION,
                                   new Object[]{ localName });
    }
}

  /**
   * Return whether or not a namespace declaration is defined
   *
   *
   * @param rawName Raw name of namespace element
   * @param value URI of given namespace
   *
   * @return True if the namespace is already defined in list of
   * namespaces
   */
  public boolean isDefinedNSDecl(String rawName, String value)
  {

    if (rawName.equals("xmlns") || rawName.startsWith("xmlns:"))
    {
      int index;
      String prefix = (index = rawName.indexOf(":")) < 0
                      ? "" : rawName.substring(0, index);
      String definedURI = m_nsSupport.getURI(prefix);

      if (null != definedURI)
      {
        if (definedURI.equals(value))
        {
          return true;
        }
        else
          return false;
      }
      else
        return false;
    }
    else
      return false;
  }

  /**
   * Returns whether a namespace is defined
   *
   *
   * @param attr Namespace attribute node
   *
   * @return True if the namespace is already defined in
   * list of namespaces
   */
  public boolean isDefinedNSDecl(int attr)
  {

    DTM dtm = m_transformer.getXPathContext().getDTM(attr);

    if (DTM.NAMESPACE_NODE == dtm.getNodeType(attr))
    {

      // String prefix = dtm.getPrefix(attr);
      String prefix = dtm.getNodeNameX(attr);
      String uri = getURI(prefix);

      if ((null != uri) && uri.equals(dtm.getStringValue(attr)))
        return true;
    }

    return false;
  }

  /**
   * Returns whether a namespace is defined
   *
   *
   * @param attr Namespace attribute node
   * @param dtm The DTM that owns attr.
   *
   * @return True if the namespace is already defined in
   * list of namespaces
   */
  public boolean isDefinedNSDecl(int attr, DTM dtm)
  {

    if (DTM.NAMESPACE_NODE == dtm.getNodeType(attr))
    {

      // String prefix = dtm.getPrefix(attr);
      String prefix = dtm.getNodeNameX(attr);
      String uri = getURI(prefix);

      if ((null != uri) && uri.equals(dtm.getStringValue(attr)))
        return true;
    }

    return false;
  }

  /**
   * Copy an DOM attribute to the created output element, executing
   * attribute templates as need be, and processing the xsl:use
   * attribute.
   *
   * @param attr Attribute node to add to result tree
   *
   * @throws TransformerException
   */
  public void addAttribute(int attr) throws TransformerException
  {

    DTM dtm = m_transformer.getXPathContext().getDTM(attr);

    if (isDefinedNSDecl(attr, dtm))
      return;

    String ns = dtm.getNamespaceURI(attr);

    if (ns == null)
      ns = "";

    // %OPT% ...can I just store the node handle?    
    addAttribute(ns, dtm.getLocalName(attr), dtm.getNodeName(attr), "CDATA",
                 dtm.getNodeValue(attr));
  }  // end copyAttributeToTarget method

  /**
   * Copy DOM attributes to the result element.
   *
   * @param src Source node with the attributes
   *
   * @throws TransformerException
   */
  public void addAttributes(int src) throws TransformerException
  {

    DTM dtm = m_transformer.getXPathContext().getDTM(src);

    for (int node = dtm.getFirstAttribute(src); DTM.NULL != node;
            node = dtm.getNextAttribute(node))
    {
      addAttribute(node);
    }
  }

  /**
   * Tell if an element is pending, to be output to the result tree.
   *
   * @return True if an element is pending
   */
  public final boolean isElementPending()
  {
    
    return m_elemIsPending;
  }

  /**
   * Retrieves the stylesheet element that produced
   * the SAX event.
   *
   * <p>Please note that the ElemTemplateElement returned may
   * be in a default template, and thus may not be
   * defined in the stylesheet.</p>
   *
   * @return the stylesheet element that produced the SAX event.
   */
  public ElemTemplateElement getCurrentElement()
  {

    if (m_elemIsPending)
      return m_snapshot.m_currentElement;
    else
      return m_transformer.getCurrentElement();
  }

  /**
   * This method retrieves the current context node
   * in the source tree.
   *
   * @return the current context node in the source tree.
   */
  public org.w3c.dom.Node getCurrentNode()
  {
    
    if (m_snapshot.m_currentNode != null)
    {
      return m_snapshot.m_currentNode;
    }
    else
    {
      DTM dtm = m_transformer.getXPathContext().getDTM(m_transformer.getCurrentNode());
      return dtm.getNode(m_transformer.getCurrentNode());
    }
  }

  /**
   * This method retrieves the xsl:template
   * that is in effect, which may be a matched template
   * or a named template.
   *
   * <p>Please note that the ElemTemplate returned may
   * be a default template, and thus may not have a template
   * defined in the stylesheet.</p>
   *
   * @return the xsl:template that is in effect
   */
  public ElemTemplate getCurrentTemplate()
  {

    if (m_elemIsPending)
      return m_snapshot.m_currentTemplate;
    else
      return m_transformer.getCurrentTemplate();
  }

  /**
   * This method retrieves the xsl:template
   * that was matched.  Note that this may not be
   * the same thing as the current template (which
   * may be from getCurrentElement()), since a named
   * template may be in effect.
   *
   * <p>Please note that the ElemTemplate returned may
   * be a default template, and thus may not have a template
   * defined in the stylesheet.</p>
   *
   * @return the xsl:template that was matched.
   */
  public ElemTemplate getMatchedTemplate()
  {

    if (m_elemIsPending)
      return m_snapshot.m_matchedTemplate;
    else
      return m_transformer.getMatchedTemplate();
  }

  /**
   * Retrieves the node in the source tree that matched
   * the template obtained via getMatchedTemplate().
   *
   * @return the node in the source tree that matched
   * the template obtained via getMatchedTemplate().
   */
  public org.w3c.dom.Node getMatchedNode()
  {

    if (m_elemIsPending)
    {
      DTM dtm = m_transformer.getXPathContext().getDTM(m_snapshot.m_matchedNode);
      return dtm.getNode(m_snapshot.m_matchedNode);
    }
    else
    {
      DTM dtm = m_transformer.getXPathContext().getDTM(m_transformer.getMatchedNode());
      return dtm.getNode(m_transformer.getMatchedNode());
    }
  }

  /**
   * Get the current context node list.
   *
   * @return the current context node list.
   */
  public org.w3c.dom.traversal.NodeIterator getContextNodeList()
  {

    if (m_elemIsPending)
    {
      return new org.apache.xml.dtm.ref.DTMNodeIterator(m_snapshot.m_contextNodeList);
    }
    else
      return new org.apache.xml.dtm.ref.DTMNodeIterator(m_transformer.getContextNodeList());
  }

  /**
   * Get the TrAX Transformer object in effect.
   *
   * @return the TrAX Transformer object in effect.
   */
  public Transformer getTransformer()
  {
    return m_transformer;
  }
  
  
  // Implement ErrorHandler
  
  /**
    * Receive notification of a warning.
    *
    * <p>SAX parsers will use this method to report conditions that
    * are not errors or fatal errors as defined by the XML 1.0
    * recommendation.  The default behaviour is to take no action.</p>
    *
    * <p>The SAX parser must continue to provide normal parsing events
    * after invoking this method: it should still be possible for the
    * application to process the document through to the end.</p>
    *
    * <p>Filters may use this method to report other, non-XML warnings
    * as well.</p>
    *
    * @param exception The warning information encapsulated in a
    *                  SAX parse exception.
    * @exception org.xml.sax.SAXException Any SAX exception, possibly
    *            wrapping another exception.
    * @see org.xml.sax.SAXParseException 
    */
  public void warning (SAXParseException exception)
    throws SAXException
  {
    if (m_contentHandler instanceof ErrorHandler)
      ((ErrorHandler)m_contentHandler).warning(exception);
  }
    
    
   /**
    * Receive notification of a recoverable error.
    *
    * <p>This corresponds to the definition of "error" in section 1.2
    * of the W3C XML 1.0 Recommendation.  For example, a validating
    * parser would use this callback to report the violation of a
    * validity constraint.  The default behaviour is to take no
    * action.</p>
    *
    * <p>The SAX parser must continue to provide normal parsing events
    * after invoking this method: it should still be possible for the
    * application to process the document through to the end.  If the
    * application cannot do so, then the parser should report a fatal
    * error even if the XML 1.0 recommendation does not require it to
    * do so.</p>
    *
    * <p>Filters may use this method to report other, non-XML errors
    * as well.</p>
    *
    * @param exception The error information encapsulated in a
    *                  SAX parse exception.
    * @exception org.xml.sax.SAXException Any SAX exception, possibly
    *            wrapping another exception.
    * @see org.xml.sax.SAXParseException 
    */
  public void error (SAXParseException exception)
    throws SAXException
  {
    if (m_contentHandler instanceof ErrorHandler)
      ((ErrorHandler)m_contentHandler).error(exception);
  }
    
    
   /**
    * Receive notification of a non-recoverable error.
    *
    * <p>This corresponds to the definition of "fatal error" in
    * section 1.2 of the W3C XML 1.0 Recommendation.  For example, a
    * parser would use this callback to report the violation of a
    * well-formedness constraint.</p>
    *
    * <p>The application must assume that the document is unusable
    * after the parser has invoked this method, and should continue
    * (if at all) only for the sake of collecting addition error
    * messages: in fact, SAX parsers are free to stop reporting any
    * other events once this method has been invoked.</p>
    *
    * @param exception The error information encapsulated in a
    *                  SAX parse exception.  
    * @exception org.xml.sax.SAXException Any SAX exception, possibly
    *            wrapping another exception.
    * @see org.xml.sax.SAXParseException
    */
  public void fatalError (SAXParseException exception)
    throws SAXException
  {      
    m_elemIsPending = false;
    m_docEnded = true;
    m_docPending = false;
    
    if (m_contentHandler instanceof ErrorHandler)
      ((ErrorHandler)m_contentHandler).fatalError(exception);
  }
  
  boolean m_isTransformClient = false;

  /**
   * Use the SAX2 helper class to track result namespaces.
   */
  NamespaceSupport m_nsSupport = new NamespaceSupport2();

  /**
   * The transformer object.
   */
  private TransformerImpl m_transformer;

  /**
   * The content handler.  May be null, in which
   * case, we'll defer to the content handler in the
   * transformer.
   */
  private ContentHandler m_contentHandler;

  /** The LexicalHandler */
  private LexicalHandler m_lexicalHandler;

  /**
   * The root of a linked set of stylesheets.
   */
  private StylesheetRoot m_stylesheetRoot = null;

  /**
   * This is used whenever a unique namespace is needed.
   */
  private int m_uniqueNSValue = 0;

  /** Prefix used to create unique prefix names */
  private static final String S_NAMESPACEPREFIX = "ns";

  /**
   * This class clones nodes to the result tree.
   */
  public ClonerToResultTree m_cloner;

  /**
   * Trace manager for debug support.
   */
  private TraceManager m_tracer;
  
  private QueuedStateSnapshot m_snapshot = new QueuedStateSnapshot();

  // These are passed to flushPending, to help it decide if it 
  // should really flush.
  
  class QueuedStateSnapshot
  {
    /**
     * The stylesheet element that produced the SAX event.
     */
    ElemTemplateElement m_currentElement;
    
    /**
     * The current context node in the source tree.
     */
    org.w3c.dom.Node m_currentNode;
    
    /**
     * The xsl:template that is in effect, which may be a matched template
     * or a named template.
     */
    ElemTemplate m_currentTemplate;
    
    /**
     * The xsl:template that was matched.
     */
    ElemTemplate m_matchedTemplate;
    
    /**
     * The node in the source tree that matched
     * the template obtained via getMatchedTemplate().
     */
    int m_matchedNode;
    
    /**
     * The current context node list.
     */
    DTMIterator m_contextNodeList;
  }
}
