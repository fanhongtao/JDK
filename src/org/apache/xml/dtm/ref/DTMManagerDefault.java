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
package org.apache.xml.dtm.ref;

import org.apache.xml.dtm.*;

import java.util.Vector;

// JAXP 1.1
import javax.xml.parsers.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;

// Apache XML Utilities
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.SystemIDResolver;
import org.apache.xml.dtm.ref.dom2dtm.DOM2DTM;
import org.apache.xml.dtm.ref.sax2dtm.SAX2DTM;

// W3C DOM
import org.w3c.dom.Document;
import org.w3c.dom.Node;

// SAX2
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

import org.apache.xml.utils.XMLString;
import org.apache.xml.utils.XMLStringFactory;

import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.res.XSLMessages;

/**
 * The default implementation for the DTMManager.
 */
public class DTMManagerDefault extends DTMManager
{

  /**
   * Vector of DTMs that this manager manages.
   */
  protected DTM m_dtms[] = new DTM[IDENT_MAX_DTMS];

  /**
   * Add a DTM to the DTM table.
   *
   * @param dtm Should be a valid reference to a DTM.
   */
  public void addDTM(DTM dtm, int id)
  {
    m_dtms[id] = dtm;
    dtm.documentRegistration();
  }

  /**
   * Get the first free DTM ID available.
   */
  public int getFirstFreeDTMID()
  {
    int n = m_dtms.length;
    for (int i = 1; i < n; i++)
    {
      if(null == m_dtms[i])
      {
        return i;
      }
    }
    throw new DTMException(XSLMessages.createMessage(XSLTErrorResources.ER_NO_DTMIDS_AVAIL, null)); //"No more DTM IDs are available!");
  }

  /**
   * The default table for exandedNameID lookups.
   */
  private ExpandedNameTable m_expandedNameTable =
    new ExpandedNameTable();

  /**
   * Constructor DTMManagerDefault
   *
   */
  public DTMManagerDefault(){}

  /** Set this to true if you want a dump of the DTM after creation. */
  private static final boolean DUMPTREE = false;

  /** Set this to true if you want a basic diagnostics. */
  private static final boolean DEBUG = false;

  /**
   * Get an instance of a DTM, loaded with the content from the
   * specified source.  If the unique flag is true, a new instance will
   * always be returned.  Otherwise it is up to the DTMManager to return a
   * new instance or an instance that it already created and may be being used
   * by someone else.
   * (I think more parameters will need to be added for error handling, and entity
   * resolution).
   *
   * @param source the specification of the source object.
   * @param unique true if the returned DTM must be unique, probably because it
   * is going to be mutated.
   * @param whiteSpaceFilter Enables filtering of whitespace nodes, and may
   *                         be null.
   * @param incremental true if the DTM should be built incrementally, if
   *                    possible.
   * @param doIndexing true if the caller considers it worth it to use
   *                   indexing schemes.
   *
   * @return a non-null DTM reference.
   */
  public DTM getDTM(Source source, boolean unique,
                    DTMWSFilter whiteSpaceFilter, boolean incremental,
                    boolean doIndexing)
  {

    if(DEBUG && null != source)
      System.out.println("Starting source: "+source.getSystemId());
    XMLStringFactory xstringFactory = m_xsf;
    int dtmPos = getFirstFreeDTMID();
    int documentID = dtmPos << IDENT_DTM_NODE_BITS;

    if ((null != source) && source instanceof DOMSource)
    {
      DOM2DTM dtm = new DOM2DTM(this, (DOMSource) source, documentID,
                                whiteSpaceFilter, xstringFactory, doIndexing);

      addDTM(dtm, dtmPos);

//      if (DUMPTREE)
//      {
//        dtm.dumpDTM();
//      }

      return dtm;
    }
    else
    {
      boolean isSAXSource = (null != source)
                            ? (source instanceof SAXSource) : true;
      boolean isStreamSource = (null != source)
                               ? (source instanceof StreamSource) : false;

      if (isSAXSource || isStreamSource)
      {
        XMLReader reader;
        InputSource xmlSource;

        if (null == source)
        {
          xmlSource = null;
          reader = null;
        }
        else
        {
          reader = getXMLReader(source);
          xmlSource = SAXSource.sourceToInputSource(source);

          String urlOfSource = xmlSource.getSystemId();

          if (null != urlOfSource)
          {
            try
            {
              urlOfSource = SystemIDResolver.getAbsoluteURI(urlOfSource);
            }
            catch (Exception e)
            {

              // %REVIEW% Is there a better way to send a warning?
              System.err.println("Can not absolutize URL: " + urlOfSource);
            }

            xmlSource.setSystemId(urlOfSource);
          }
        }

        // Create the basic SAX2DTM.
        SAX2DTM dtm = new SAX2DTM(this, source, documentID, whiteSpaceFilter,
                                  xstringFactory, doIndexing);

        // Go ahead and add the DTM to the lookup table.  This needs to be
        // done before any parsing occurs.
        addDTM(dtm, dtmPos);

        boolean haveXercesParser =
          (null != reader)
          && (reader.getClass().getName().equals("org.apache.xerces.parsers.SAXParser") );

        if (haveXercesParser)
          incremental = true;  // No matter what.  %REVIEW%

        // If the reader is null, but they still requested an incremental build,
        // then we still want to set up the IncrementalSAXSource stuff.
        if (this.m_incremental && incremental /* || ((null == reader) && incremental) */)
        {
          IncrementalSAXSource coParser=null;

          if (haveXercesParser)
          {
            // IncrementalSAXSource_Xerces to avoid threading.
            // System.out.println("Using IncrementalSAXSource_Xerces to avoid threading");
            try {
              // should be ok, it's in the same package - no need for thread class loader
              Class c=Class.forName( "org.apache.xml.dtm.ref.IncrementalSAXSource_Xerces" );
              coParser=(IncrementalSAXSource)c.newInstance();
            }  catch( Exception ex ) {
              ex.printStackTrace();
              coParser=null;
            }
          }

          if( coParser==null ) {
            // Create a IncrementalSAXSource that will run on the secondary thread.
            if (null == reader)
              coParser = new IncrementalSAXSource_Filter();
            else
	    {
	      IncrementalSAXSource_Filter filter=new IncrementalSAXSource_Filter();
	      filter.setXMLReader(reader);
	      coParser=filter;
	    }

          }

          // Have the DTM set itself up as the IncrementalSAXSource's listener.
          dtm.setIncrementalSAXSource(coParser);

          if (null == xmlSource)
          {

            // Then the user will construct it themselves.
            return dtm;
          }

          reader.setDTDHandler(dtm);
          reader.setErrorHandler(dtm);

          try
          {

	    // Launch parsing coroutine.  Launches a second thread,
	    // if we're using IncrementalSAXSource.filter().
            coParser.startParse(xmlSource);
          }
          catch (RuntimeException re)
          {

            dtm.clearCoRoutine();

            throw re;
          }
          catch (Exception e)
          {

            dtm.clearCoRoutine();

            throw new org.apache.xml.utils.WrappedRuntimeException(e);
          }
        }
        else
        {
          if (null == reader)
          {

            // Then the user will construct it themselves.
            return dtm;
          }

          // not incremental
          reader.setContentHandler(dtm);
          reader.setDTDHandler(dtm);
          reader.setErrorHandler(dtm);

          try
          {
            reader.setProperty(
              "http://xml.org/sax/properties/lexical-handler", dtm);
          }
          catch (SAXNotRecognizedException e){}
          catch (SAXNotSupportedException e){}

          try
          {
            reader.parse(xmlSource);
          }
          catch (RuntimeException re)
          {

            dtm.clearCoRoutine();

            throw re;
          }
          catch (Exception e)
          {

            dtm.clearCoRoutine();

            throw new org.apache.xml.utils.WrappedRuntimeException(e);
          }
        }

        if (DUMPTREE)
        {
          System.out.println("Dumping SAX2DOM");
          dtm.dumpDTM();
        }

        return dtm;
      }
      else
      {

        // It should have been handled by a derived class or the caller
        // made a mistake.
        throw new DTMException(XSLMessages.createMessage(XSLTErrorResources.ER_NOT_SUPPORTED, new Object[]{source})); //"Not supported: " + source);
      }
    }
  }

  /**
   * Given a W3C DOM node, try and return a DTM handle.
   * Note: calling this may be non-optimal, and there is no guarantee that
   * the node will be found in any particular DTM.
   *
   * @param node Non-null reference to a DOM node.
   *
   * @return a valid DTM handle.
   */
  public int getDTMHandleFromNode(org.w3c.dom.Node node)
  {
    if(null == node)
      throw new IllegalArgumentException(XSLMessages.createMessage(XSLTErrorResources.ER_NODE_NON_NULL, null)); //"node must be non-null for getDTMHandleFromNode!");

    if (node instanceof org.apache.xml.dtm.ref.DTMNodeProxy)
      return ((org.apache.xml.dtm.ref.DTMNodeProxy) node).getDTMNodeNumber();
    else
    {
      // Find the DOM2DTMs wrapped around this Document (if any)
      // and check whether they contain the Node in question.
      //
      // NOTE that since a DOM2DTM may represent a subtree rather
      // than a full document, we have to be prepared to check more
      // than one -- and there is no guarantee that we will find
      // one that contains ancestors or siblings of the node we're
      // seeking.
      //
      // %REVIEW% We could search for the one which contains this
      // node at the deepest level, and thus covers the widest
      // subtree, but that's going to entail additional work
      // checking more DTMs... and getHandleFromNode is not a
      // cheap operation in most implementations.
      // [I had to change this to look forward... sorry.  -sb]
      int max = m_dtms.length;
      for(int i = 0; i < max; i++)
        {
          DTM thisDTM=m_dtms[i];
          if((null != thisDTM) && thisDTM instanceof DOM2DTM)
          {
            int handle=((DOM2DTM)thisDTM).getHandleOfNode(node);
            if(handle!=DTM.NULL) return handle;
          }
         }

      // Since the real root of our tree may be a DocumentFragment, we need to
      // use getParent to find the root, instead of getOwnerDocument.  Otherwise
      // DOM2DTM#getHandleOfNode will be very unhappy.
      Node root = node;
      for (Node p = root.getParentNode(); p != null; p = p.getParentNode())
      {
        root = p;
      }

      DTM dtm = getDTM(new javax.xml.transform.dom.DOMSource(root), false,
                       null, true, true);

      int handle = ((DOM2DTM)dtm).getHandleOfNode(node);

      if(DTM.NULL == handle)
        throw new RuntimeException(XSLMessages.createMessage(XSLTErrorResources.ER_COULD_NOT_RESOLVE_NODE, null)); //"Could not resolve the node to a handle!");

      return handle;
    }
  }

  /**
   * This method returns the SAX2 parser to use with the InputSource
   * obtained from this URI.
   * It may return null if any SAX2-conformant XML parser can be used,
   * or if getInputSource() will also return null. The parser must
   * be free for use (i.e.
   * not currently in use for another parse().
   *
   * @param inputSource The value returned from the URIResolver.
   * @returns a SAX2 XMLReader to use to resolve the inputSource argument.
   *
   * @return non-null XMLReader reference ready to parse.
   */
  public XMLReader getXMLReader(Source inputSource)
  {

    try
    {
      XMLReader reader = (inputSource instanceof SAXSource)
                         ? ((SAXSource) inputSource).getXMLReader() : null;

      boolean isUserReader = (reader != null);

      if (null == reader)
      {
        try
        {
          javax.xml.parsers.SAXParserFactory factory =
            javax.xml.parsers.SAXParserFactory.newInstance();

          factory.setNamespaceAware(true);

          javax.xml.parsers.SAXParser jaxpParser = factory.newSAXParser();

          reader = jaxpParser.getXMLReader();
        }
        catch (javax.xml.parsers.ParserConfigurationException ex)
        {
          throw new org.xml.sax.SAXException(ex);
        }
        catch (javax.xml.parsers.FactoryConfigurationError ex1)
        {
          throw new org.xml.sax.SAXException(ex1.toString());
        }
        catch (NoSuchMethodError ex2){}
        catch (AbstractMethodError ame){}

        if (null == reader)
          reader = XMLReaderFactory.createXMLReader();
      }

      try
      {
        reader.setFeature("http://xml.org/sax/features/namespace-prefixes",
                          true);
      }
      catch (org.xml.sax.SAXException se)
      {

        // What can we do?
        // TODO: User diagnostics.
      }

        // Commented out as per discussion with Thomas2.Maesing@bgs-ag.de
        // about bug 2124.
//      if(!isUserReader)
//      {
//        try
//        {
//          reader.setFeature("http://apache.org/xml/features/validation/dynamic",
//                            true);
//        }
//        catch (org.xml.sax.SAXException se)
//        {
//
//          // What can we do?
//          // TODO: User diagnostics.
//        }
//      }

      return reader;
    }
    catch (org.xml.sax.SAXException se)
    {
      throw new DTMException(se.getMessage(), se);
    }
  }

  /**
   * NEEDSDOC Method getDTM
   *
   *
   * NEEDSDOC @param nodeHandle
   *
   * NEEDSDOC (getDTM) @return
   */
  public DTM getDTM(int nodeHandle)
  {

    // Performance critical function.
    return m_dtms[nodeHandle >>> IDENT_DTM_NODE_BITS];
  }

  /**
   * Given a DTM, find it's ID number in the DTM list.
   *
   *
   * @param dtm The DTM reference in question.
   *
   * @return The ID, or -1 if not found in the list.
   */
  public int getDTMIdentity(DTM dtm)
  {

    // A backwards search should normally be the fastest.
    // [But we can't do it... sorry.  -sb]
    int n = m_dtms.length;

    for (int i = 0; i < n; i++)
    {
      DTM tdtm = m_dtms[i];

      if (tdtm == dtm)
        return i;
    }

    return -1;
  }

  /**
   * NEEDSDOC Method release
   *
   *
   * NEEDSDOC @param dtm
   * NEEDSDOC @param shouldHardDelete
   *
   * NEEDSDOC (release) @return
   */
  public boolean release(DTM dtm, boolean shouldHardDelete)
  {

    if (dtm instanceof SAX2DTM)
    {
      ((SAX2DTM) dtm).clearCoRoutine();
    }

    int i = getDTMIdentity(dtm);

    if (i >= 0)
    {
      m_dtms[i] = null;
    }

    dtm.documentRelease();
    return true;
  }

  /**
   * Method createDocumentFragment
   *
   *
   * NEEDSDOC (createDocumentFragment) @return
   */
  public DTM createDocumentFragment()
  {

    try
    {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

      dbf.setNamespaceAware(true);

      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.newDocument();
      Node df = doc.createDocumentFragment();

      return getDTM(new DOMSource(df), true, null, false, false);
    }
    catch (Exception e)
    {
      throw new DTMException(e);
    }
  }

  /**
   * NEEDSDOC Method createDTMIterator
   *
   *
   * NEEDSDOC @param whatToShow
   * NEEDSDOC @param filter
   * NEEDSDOC @param entityReferenceExpansion
   *
   * NEEDSDOC (createDTMIterator) @return
   */
  public DTMIterator createDTMIterator(int whatToShow, DTMFilter filter,
                                       boolean entityReferenceExpansion)
  {

    /** @todo: implement this org.apache.xml.dtm.DTMManager abstract method */
    return null;
  }

  /**
   * NEEDSDOC Method createDTMIterator
   *
   *
   * NEEDSDOC @param xpathString
   * NEEDSDOC @param presolver
   *
   * NEEDSDOC (createDTMIterator) @return
   */
  public DTMIterator createDTMIterator(String xpathString,
                                       PrefixResolver presolver)
  {

    /** @todo: implement this org.apache.xml.dtm.DTMManager abstract method */
    return null;
  }

  /**
   * NEEDSDOC Method createDTMIterator
   *
   *
   * NEEDSDOC @param node
   *
   * NEEDSDOC (createDTMIterator) @return
   */
  public DTMIterator createDTMIterator(int node)
  {

    /** @todo: implement this org.apache.xml.dtm.DTMManager abstract method */
    return null;
  }

  /**
   * NEEDSDOC Method createDTMIterator
   *
   *
   * NEEDSDOC @param xpathCompiler
   * NEEDSDOC @param pos
   *
   * NEEDSDOC (createDTMIterator) @return
   */
  public DTMIterator createDTMIterator(Object xpathCompiler, int pos)
  {

    /** @todo: implement this org.apache.xml.dtm.DTMManager abstract method */
    return null;
  }

  /**
   * return the expanded name table.
   *
   * NEEDSDOC @param dtm
   *
   * NEEDSDOC ($objectName$) @return
   */
  public ExpandedNameTable getExpandedNameTable(DTM dtm)
  {
    return m_expandedNameTable;
  }

}
