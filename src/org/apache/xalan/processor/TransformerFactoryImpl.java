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

import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.XMLReader;

import javax.xml.transform.TransformerException;

import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLFilter;

import org.w3c.dom.Node;

import org.apache.xml.utils.TreeWalker;
import org.apache.xml.utils.SystemIDResolver;
import org.apache.xml.utils.DefaultErrorHandler;
import org.apache.xalan.transformer.TransformerImpl;
import org.apache.xalan.transformer.TransformerIdentityImpl;
import org.apache.xalan.transformer.TrAXFilter;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import javax.xml.transform.Templates;
import javax.xml.transform.sax.TemplatesHandler;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.ErrorListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.StringReader;

import java.util.Properties;
import java.util.Enumeration;

import org.apache.xalan.transformer.XalanProperties;

/**
 * The TransformerFactoryImpl, which implements the TRaX TransformerFactory
 * interface, processes XSLT stylesheets into a Templates object
 * (a StylesheetRoot).
 */
public class TransformerFactoryImpl extends SAXTransformerFactory
{

  /** 
   * The path/filename of the property file: XSLTInfo.properties  
   * Maintenance note: see also org.apache.xpath.functions.FuncSystemProperty.XSLT_PROPERTIES
   */
  public static String XSLT_PROPERTIES =
    "org/apache/xalan/res/XSLTInfo.properties";

     /**
   * Constructor TransformerFactoryImpl
   *
   */
  public TransformerFactoryImpl()
  {
  }

  /** a zero length Class array used in loadPropertyFileToSystem() */
  private static final Class[] NO_CLASSES = new Class[0];

  /** a zero length Object array used in loadPropertyFileToSystem() */
  private static final Object[] NO_OBJS = new Object[0];
  
  /** Static string to be used for incremental feature */
  public static final String FEATURE_INCREMENTAL = "http://xml.apache.org/xalan/features/incremental";

  /** Static string to be used for optimize feature */
  public static final String FEATURE_OPTIMIZE = "http://xml.apache.org/xalan/features/optimize";

  /** Static string to be used for source_location feature */
  public static final String FEATURE_SOURCE_LOCATION = XalanProperties.SOURCE_LOCATION;

  /**
   * Retrieve a propery bundle from XSLT_PROPERTIES and load it
   * int the System properties.
   */
   static 
  {
      try
      {
        InputStream is = null;

        try
        {
          Properties props = new Properties();

          try {
            java.lang.reflect.Method getCCL = Thread.class.getMethod("getContextClassLoader", NO_CLASSES);
            if (getCCL != null) {
              ClassLoader contextClassLoader = (ClassLoader) getCCL.invoke(Thread.currentThread(), NO_OBJS);
              is = contextClassLoader.getResourceAsStream(XSLT_PROPERTIES); // file should be already fully specified
            }
          }
          catch (Exception e) {}

          if (is == null) {
            // NOTE! For the below getResourceAsStream in Sun JDK 1.1.8M
            //  we apparently must add the leading slash character - I 
            //  don't know why, but if it's not there, we throw an NPE from the below loading
            is = TransformerFactoryImpl.class.getResourceAsStream("/" + XSLT_PROPERTIES); // file should be already fully specified
          }

          // get a buffered version
          BufferedInputStream bis = new BufferedInputStream(is);

          props.load(bis);  // and load up the property bag from this
          bis.close();  // close out after reading

          // OK, now we only want to set system properties that 
          // are not already set.
          Properties systemProps = System.getProperties();
          Enumeration propEnum = props.propertyNames();

          while (propEnum.hasMoreElements())
          {
            String prop = (String) propEnum.nextElement();

            if (!systemProps.containsKey(prop))
              systemProps.put(prop, props.getProperty(prop));
          }

          System.setProperties(systemProps);

        }
        catch (Exception ex){}
      }
      catch (SecurityException se)
      {

        // In this case the caller is required to have 
        // the needed attributes already defined.
      }
   }

public javax.xml.transform.Templates processFromNode(Node node)
          throws TransformerConfigurationException
  {

    try
    {
      TemplatesHandler builder = newTemplatesHandler();
      TreeWalker walker = new TreeWalker(builder, new org.apache.xpath.DOM2Helper(), builder.getSystemId());

      walker.traverse(node);

      return builder.getTemplates();
    }
    catch (org.xml.sax.SAXException se)
    {
      if (m_errorListener != null)
      {
        try
        {
          m_errorListener.fatalError(new TransformerException(se));
        }
        catch (TransformerException ex)
        {
          throw new TransformerConfigurationException(ex);
        }

        return null;
      }
      else

        // Should remove this later... but right now diagnostics from 
        // TransformerConfigurationException are not good.
        // se.printStackTrace();
        throw new TransformerConfigurationException(XSLMessages.createMessage(XSLTErrorResources.ER_PROCESSFROMNODE_FAILED, null), se); //"processFromNode failed",
                                                    //se);
    }
    catch (TransformerConfigurationException tce)
    {
      // Assume it's already been reported to the error listener.
      throw tce;
    }
   /* catch (TransformerException tce)
    {
      // Assume it's already been reported to the error listener.
      throw new TransformerConfigurationException(tce.getMessage(), tce);
    }*/
    catch (Exception e)
    {
      if (m_errorListener != null)
      {
        try
        {
          m_errorListener.fatalError(new TransformerException(e));
        }
        catch (TransformerException ex)
        {
          throw new TransformerConfigurationException(ex);
        }

        return null;
      }
      else

        // Should remove this later... but right now diagnostics from 
        // TransformerConfigurationException are not good.
        // se.printStackTrace();
        throw new TransformerConfigurationException(XSLMessages.createMessage(XSLTErrorResources.ER_PROCESSFROMNODE_FAILED, null), e); //"processFromNode failed",
                                                    //e);
    }
  }

  /**
   * The systemID that was specified in
   * processFromNode(Node node, String systemID).
   */
  private String m_DOMsystemID = null;

  /**
   * The systemID that was specified in
   * processFromNode(Node node, String systemID).
   *
   * @return The systemID, or null.
   */
  String getDOMsystemID()
  {
    return m_DOMsystemID;
  }

  /**
   * Process the stylesheet from a DOM tree, if the
   * processor supports the "http://xml.org/trax/features/dom/input"
   * feature.
   *
   * @param node A DOM tree which must contain
   * valid transform instructions that this processor understands.
   * @param systemID The systemID from where xsl:includes and xsl:imports
   * should be resolved from.
   *
   * @return A Templates object capable of being used for transformation purposes.
   *
   * @throws TransformerConfigurationException
   */
  javax.xml.transform.Templates processFromNode(Node node, String systemID)
          throws TransformerConfigurationException
  {

    m_DOMsystemID = systemID;

    return processFromNode(node);
  }

  /**
   * Get InputSource specification(s) that are associated with the
   * given document specified in the source param,
   * via the xml-stylesheet processing instruction
   * (see http://www.w3.org/TR/xml-stylesheet/), and that matches
   * the given criteria.  Note that it is possible to return several stylesheets
   * that match the criteria, in which case they are applied as if they were
   * a list of imports or cascades.
   *
   * <p>Note that DOM2 has it's own mechanism for discovering stylesheets.
   * Therefore, there isn't a DOM version of this method.</p>
   *
   *
   * @param source The XML source that is to be searched.
   * @param media The media attribute to be matched.  May be null, in which
   *              case the prefered templates will be used (i.e. alternate = no).
   * @param title The value of the title attribute to match.  May be null.
   * @param charset The value of the charset attribute to match.  May be null.
   *
   * @return A Source object capable of being used to create a Templates object.
   *
   * @throws TransformerConfigurationException
   */
  public Source getAssociatedStylesheet(
          Source source, String media, String title, String charset)
            throws TransformerConfigurationException
  {

    String baseID;
    InputSource isource = null;
    Node node = null;
    XMLReader reader = null;

    if (source instanceof DOMSource)
    {
      DOMSource dsource = (DOMSource) source;

      node = dsource.getNode();
      baseID = dsource.getSystemId();
    }
    else
    {
      isource = SAXSource.sourceToInputSource(source);
      baseID = isource.getSystemId();
    }

    // What I try to do here is parse until the first startElement
    // is found, then throw a special exception in order to terminate 
    // the parse.
    StylesheetPIHandler handler = new StylesheetPIHandler(baseID, media,
                                    title, charset);
    
    // Use URIResolver. Patch from Dmitri Ilyin 
    if (m_uriResolver != null) 
    {
      handler.setURIResolver(m_uriResolver); 
    }

    try
    {
      if (null != node)
      {
        TreeWalker walker = new TreeWalker(handler, new org.apache.xpath.DOM2Helper(), baseID);

        walker.traverse(node);
      }
      else
      {

        // Use JAXP1.1 ( if possible )
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
        {
          reader = XMLReaderFactory.createXMLReader();
        }

        // Need to set options!
        reader.setContentHandler(handler);
        reader.parse(isource);
      }
    }
    catch (StopParseException spe)
    {

      // OK, good.
    }
    catch (org.xml.sax.SAXException se)
    {
      throw new TransformerConfigurationException(
        "getAssociatedStylesheets failed", se);
    }
    catch (IOException ioe)
    {
      throw new TransformerConfigurationException(
        "getAssociatedStylesheets failed", ioe);
    }

    return handler.getAssociatedStylesheet();
  }

  /**
   * Create a new Transformer object that performs a copy
   * of the source to the result.
   *
   * @param source An object that holds a URI, input stream, etc.
   *
   * @return A Transformer object that may be used to perform a transformation
   * in a single thread, never null.
   *
   * @throws TransformerConfigurationException May throw this during
   *            the parse when it is constructing the
   *            Templates object and fails.
   */
  public TemplatesHandler newTemplatesHandler()
          throws TransformerConfigurationException
  {
    return new StylesheetHandler(this);
  }

  /**
   * Look up the value of a feature.
   *
   * <p>The feature name is any fully-qualified URI.  It is
   * possible for an TransformerFactory to recognize a feature name but
   * to be unable to return its value; this is especially true
   * in the case of an adapter for a SAX1 Parser, which has
   * no way of knowing whether the underlying parser is
   * validating, for example.</p>
   *
   * @param name The feature name, which is a fully-qualified URI.
   * @return The current state of the feature (true or false).
   */
  public boolean getFeature(String name)
  {

    // Try first with identity comparison, which 
    // will be faster.
    if ((DOMResult.FEATURE == name) || (DOMSource.FEATURE == name)
            || (SAXResult.FEATURE == name) || (SAXSource.FEATURE == name)
            || (StreamResult.FEATURE == name)
            || (StreamSource.FEATURE == name)
            || (SAXTransformerFactory.FEATURE == name)
            || (SAXTransformerFactory.FEATURE_XMLFILTER == name))
      return true;
    else if ((DOMResult.FEATURE.equals(name))
             || (DOMSource.FEATURE.equals(name))
             || (SAXResult.FEATURE.equals(name))
             || (SAXSource.FEATURE.equals(name))
             || (StreamResult.FEATURE.equals(name))
             || (StreamSource.FEATURE.equals(name))
             || (SAXTransformerFactory.FEATURE.equals(name))
             || (SAXTransformerFactory.FEATURE_XMLFILTER.equals(name)))
      return true;
    else
      return false;
  }
  
  public static boolean m_optimize = true;
  
  /** Flag set by FEATURE_SOURCE_LOCATION.
   * This feature specifies whether the transformation phase should
   * keep track of line and column numbers for the input source
   * document. Note that this works only when that
   * information is available from the source -- in other words, if you
   * pass in a DOM, there's little we can do for you.
   * 
   * The default is false. Setting it true may significantly
   * increase storage cost per node. 
   * 
   * %REVIEW% SAX2DTM is explicitly reaching up to retrieve this global field.
   * We should instead have an architected pathway for passing hints of this
   * sort down from TransformerFactory to Transformer to DTMManager to DTM.
   * */
  public static boolean m_source_location = false;
  
  /**
   * Allows the user to set specific attributes on the underlying
   * implementation.
   *
   * @param name The name of the attribute.
   * @param value The value of the attribute; Boolean or String="true"|"false"
   *
   * @throws IllegalArgumentException thrown if the underlying
   * implementation doesn't recognize the attribute.
   */
  public void setAttribute(String name, Object value)
          throws IllegalArgumentException
  {
    if (name.equals(FEATURE_INCREMENTAL))
    {
      if(value instanceof Boolean)
      {
        // Accept a Boolean object..
        org.apache.xml.dtm.DTMManager.setIncremental(((Boolean)value).booleanValue());
      }
      else if(value instanceof String)
      {
        // .. or a String object
        org.apache.xml.dtm.DTMManager.setIncremental((new Boolean((String)value)).booleanValue());
      }
      else
      {
        // Give a more meaningful error message
        throw new IllegalArgumentException(XSLMessages.createMessage(XSLTErrorResources.ER_BAD_VALUE, new Object[]{name, value})); //name + " bad value " + value);
      }
	}
    else if (name.equals(FEATURE_OPTIMIZE))
    {
      if(value instanceof Boolean)
      {
        // Accept a Boolean object..
        m_optimize = ((Boolean)value).booleanValue();
      }
      else if(value instanceof String)
      {
        // .. or a String object
        m_optimize = (new Boolean((String)value)).booleanValue();
      }
      else
      {
        // Give a more meaningful error message
        throw new IllegalArgumentException(XSLMessages.createMessage(XSLTErrorResources.ER_BAD_VALUE, new Object[]{name, value})); //name + " bad value " + value);
      }
    }
    
    // Custom Xalan feature: annotate DTM with SAX source locator fields.
    // This gets used during SAX2DTM instantiation. 
    //
    // %REVIEW% Should the name of this field really be in XalanProperties?
    // %REVIEW% I hate that it's a global static, but didn't want to change APIs yet.
    else if(name.equals(FEATURE_SOURCE_LOCATION))
    {
      if(value instanceof Boolean)
      {
        // Accept a Boolean object..
        m_source_location = ((Boolean)value).booleanValue();
      }
      else if(value instanceof String)
      {
        // .. or a String object
        m_source_location = (new Boolean((String)value)).booleanValue();
      }
      else
      {
        // Give a more meaningful error message
        throw new IllegalArgumentException(XSLMessages.createMessage(XSLTErrorResources.ER_BAD_VALUE, new Object[]{name, value})); //name + " bad value " + value);
      }
    }
    
    else
    {
      throw new IllegalArgumentException(XSLMessages.createMessage(XSLTErrorResources.ER_NOT_SUPPORTED, new Object[]{name})); //name + "not supported");
    }
  }

  /**
   * Allows the user to retrieve specific attributes on the underlying
   * implementation.
   *
   * @param name The name of the attribute.
   * @return value The value of the attribute.
   *
   * @throws IllegalArgumentException thrown if the underlying
   * implementation doesn't recognize the attribute.
   */
  public Object getAttribute(String name) throws IllegalArgumentException
  {
    if (name.equals(FEATURE_INCREMENTAL))
    {
      return new Boolean(org.apache.xml.dtm.DTMManager.getIncremental());            
    }
    else if (name.equals(FEATURE_OPTIMIZE))
    {
      return new Boolean(m_optimize);
    }
    else if (name.equals(FEATURE_SOURCE_LOCATION))
    {
      return new Boolean(m_source_location);
    }
    else
      throw new IllegalArgumentException(XSLMessages.createMessage(XSLTErrorResources.ER_ATTRIB_VALUE_NOT_RECOGNIZED, new Object[]{name})); //name + " attribute not recognized");
  }

  /**
   * Create an XMLFilter that uses the given source as the
   * transformation instructions.
   *
   * @param src The source of the transformation instructions.
   *
   * @return An XMLFilter object, or null if this feature is not supported.
   *
   * @throws TransformerConfigurationException
   */
  public XMLFilter newXMLFilter(Source src)
          throws TransformerConfigurationException
  {

    Templates templates = newTemplates(src);
    if( templates==null ) return null;
    
    return newXMLFilter(templates);
  }

  /**
   * Create an XMLFilter that uses the given source as the
   * transformation instructions.
   *
   * @param src The source of the transformation instructions.
   *
   * @param templates non-null reference to Templates object.
   *
   * @return An XMLFilter object, or null if this feature is not supported.
   *
   * @throws TransformerConfigurationException
   */
  public XMLFilter newXMLFilter(Templates templates)
          throws TransformerConfigurationException
  {
    try {
      return new TrAXFilter(templates);
    } catch( TransformerConfigurationException ex ) {
      if( m_errorListener != null) {
        try {
          m_errorListener.fatalError( ex );
          return null;
        } catch( TransformerException ex1 ) {
          new TransformerConfigurationException(ex1);
        }
      }
      throw ex;
    }
  }

  /**
   * Get a TransformerHandler object that can process SAX
   * ContentHandler events into a Result, based on the transformation
   * instructions specified by the argument.
   *
   * @param src The source of the transformation instructions.
   *
   * @return TransformerHandler ready to transform SAX events.
   *
   * @throws TransformerConfigurationException
   */
  public TransformerHandler newTransformerHandler(Source src)
          throws TransformerConfigurationException
  {

    Templates templates = newTemplates(src);
    if( templates==null ) return null;
    
    return newTransformerHandler(templates);
  }

  /**
   * Get a TransformerHandler object that can process SAX
   * ContentHandler events into a Result, based on the Templates argument.
   *
   * @param templates The source of the transformation instructions.
   *
   * @return TransformerHandler ready to transform SAX events.
   * @throws TransformerConfigurationException
   */
  public TransformerHandler newTransformerHandler(Templates templates)
          throws TransformerConfigurationException
  {
    try {
      TransformerImpl transformer =
        (TransformerImpl) templates.newTransformer();
      transformer.setURIResolver(m_uriResolver);
      TransformerHandler th =
        (TransformerHandler) transformer.getInputContentHandler(true);

      return th;
    } catch( TransformerConfigurationException ex ) {
      if( m_errorListener != null ) {
        try {
          m_errorListener.fatalError( ex );
          return null;
        } catch (TransformerException ex1 ) {
          ex=new TransformerConfigurationException(ex1);
        }
      }
      throw ex;
    }
    
  }

//  /** The identity transform string, for support of newTransformerHandler()
//   *  and newTransformer().  */
//  private static final String identityTransform =
//    "<xsl:stylesheet " + "xmlns:xsl='http://www.w3.org/1999/XSL/Transform' "
//    + "version='1.0'>" + "<xsl:template match='/|node()'>"
//    + "<xsl:copy-of select='.'/>" + "</xsl:template>" + "</xsl:stylesheet>";
//
//  /** The identity transform Templates, built from identityTransform, 
//   *  for support of newTransformerHandler() and newTransformer().  */
//  private static Templates m_identityTemplate = null;

  /**
   * Get a TransformerHandler object that can process SAX
   * ContentHandler events into a Result.
   *
   * @param src The source of the transformation instructions.
   *
   * @return TransformerHandler ready to transform SAX events.
   *
   * @throws TransformerConfigurationException
   */
  public TransformerHandler newTransformerHandler()
          throws TransformerConfigurationException
  {

//    if (null == m_identityTemplate)
//    {
//      synchronized (identityTransform)
//      {
//        if (null == m_identityTemplate)
//        {
//          StringReader reader = new StringReader(identityTransform);
//
//          m_identityTemplate = newTemplates(new StreamSource(reader));
//        }
//      }
//    }
//
//    return newTransformerHandler(m_identityTemplate);
    return new TransformerIdentityImpl();
  }

  /**
   * Process the source into a Transformer object.  Care must
   * be given to know that this object can not be used concurrently
   * in multiple threads.
   *
   * @param source An object that holds a URL, input stream, etc.
   *
   * @return A Transformer object capable of
   * being used for transformation purposes in a single thread.
   *
   * @throws TransformerConfigurationException May throw this during the parse when it
   *            is constructing the Templates object and fails.
   */
  public Transformer newTransformer(Source source)
          throws TransformerConfigurationException
  {
    try {
      Templates tmpl=newTemplates( source );
      /* this can happen if an ErrorListener is present and it doesn't
         throw any exception in fatalError. 
         The spec says: "a Transformer must use this interface
         instead of throwing an exception" - the newTemplates() does
         that, and returns null.
      */
      if( tmpl==null ) return null;
      Transformer transformer = tmpl.newTransformer();
      transformer.setURIResolver(m_uriResolver);
      return transformer;
    } catch( TransformerConfigurationException ex ) {
      if( m_errorListener != null ) {
        try {
          m_errorListener.fatalError( ex );
          return null;
        } catch( TransformerException ex1 ) {
          ex=new TransformerConfigurationException( ex1 );
        }
      }
      throw ex;
    }
  }

  /**
   * Create a new Transformer object that performs a copy
   * of the source to the result.
   *
   * @param source An object that holds a URL, input stream, etc.
   *
   * @return A Transformer object capable of
   * being used for transformation purposes in a single thread.
   *
   * @throws TransformerConfigurationException May throw this during
   *            the parse when it is constructing the
   *            Templates object and it fails.
   */
  public Transformer newTransformer() throws TransformerConfigurationException
  {

//    if (null == m_identityTemplate)
//    {
//      synchronized (identityTransform)
//      {
//        if (null == m_identityTemplate)
//        {
//          StringReader reader = new StringReader(identityTransform);
//
//          m_identityTemplate = newTemplates(new StreamSource(reader));
//        }
//      }
//    }
//
//    return m_identityTemplate.newTransformer();
      return new TransformerIdentityImpl();
  }

  /**
   * Process the source into a Templates object, which is likely
   * a compiled representation of the source. This Templates object
   * may then be used concurrently across multiple threads.  Creating
   * a Templates object allows the TransformerFactory to do detailed
   * performance optimization of transformation instructions, without
   * penalizing runtime transformation.
   *
   * @param source An object that holds a URL, input stream, etc.
   * @return A Templates object capable of being used for transformation purposes.
   *
   * @throws TransformerConfigurationException May throw this during the parse when it
   *            is constructing the Templates object and fails.
   */
  public Templates newTemplates(Source source)
          throws TransformerConfigurationException
  {

    TemplatesHandler builder = newTemplatesHandler();
    String baseID = source.getSystemId();

    if (null == baseID)
    {
      try
      {
        String currentDir = System.getProperty("user.dir");
        
        if (currentDir.startsWith(java.io.File.separator))
          baseID = "file://" + currentDir + java.io.File.separatorChar
                   + source.getClass().getName();
        else
          baseID = "file:///" + currentDir + java.io.File.separatorChar
                   + source.getClass().getName();
      }
      catch (SecurityException se)
      {

        // For untrusted applet case, user.dir is outside the sandbox 
        //  and not accessible: just leave baseID as null (-sb & -sc)
      }
    }
    else
    {
      try
      {
        baseID = SystemIDResolver.getAbsoluteURI(baseID);
      }
      catch (TransformerException te)
      {
        throw new TransformerConfigurationException(te);
      }
    }

    builder.setSystemId(baseID);

    if (source instanceof DOMSource)
    {
      DOMSource dsource = (DOMSource) source;
      Node node = dsource.getNode();

      if (null != node)
        return processFromNode(node, baseID);
      else
      {
        String messageStr = XSLMessages.createMessage(
          XSLTErrorResources.ER_ILLEGAL_DOMSOURCE_INPUT, null);

        throw new IllegalArgumentException(messageStr);
      }
    }

    try
    {
      InputSource isource = SAXSource.sourceToInputSource(source);
      XMLReader reader = null;

      if (source instanceof SAXSource)
        reader = ((SAXSource) source).getXMLReader();
        
      boolean isUserReader = (reader != null);

      if (null == reader)
      {

        // Use JAXP1.1 ( if possible )
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
      }

      if (null == reader)
        reader = XMLReaderFactory.createXMLReader();

      // If you set the namespaces to true, we'll end up getting double 
      // xmlns attributes.  Needs to be fixed.  -sb
      // reader.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
      try
      {
        if(!isUserReader)
          reader.setFeature("http://apache.org/xml/features/validation/dynamic",
                            true);
      }
      catch (org.xml.sax.SAXException ex)
      {

        // feature not recognized
      }

      reader.setContentHandler(builder);
      reader.parse(isource);
    }
    catch (org.xml.sax.SAXException se)
    {
      if (m_errorListener != null)
      {
        try
        {
          m_errorListener.fatalError(new TransformerException(se));
        }
        catch (TransformerException ex1)
        {
          throw new TransformerConfigurationException(ex1);
        }
      }
      else
        throw new TransformerConfigurationException(se.getMessage(), se);
    }
    catch (Exception e)
    {
      if (m_errorListener != null)
      {
        try
        {
          m_errorListener.fatalError(new TransformerException(e));

          return null;
        }
        catch (TransformerException ex1)
        {
          throw new TransformerConfigurationException(ex1);
        }
      }
      else
        throw new TransformerConfigurationException(e.getMessage(), e);
    }

    return builder.getTemplates();
  }

  /**
   * The object that implements the URIResolver interface,
   * or null.
   */
  URIResolver m_uriResolver;

  /**
   * Set an object that will be used to resolve URIs used in
   * xsl:import, etc.  This will be used as the default for the
   * transformation.
   * @param resolver An object that implements the URIResolver interface,
   * or null.
   */
  public void setURIResolver(URIResolver resolver)
  {
    m_uriResolver = resolver;
  }

  /**
   * Get the object that will be used to resolve URIs used in
   * xsl:import, etc.  This will be used as the default for the
   * transformation.
   *
   * @return The URIResolver that was set with setURIResolver.
   */
  public URIResolver getURIResolver()
  {
    return m_uriResolver;
  }

  /** The error listener.   */
  private ErrorListener m_errorListener = new DefaultErrorHandler();

  /**
   * Get the error listener in effect for the TransformerFactory.
   *
   * @return A non-null reference to an error listener.
   */
  public ErrorListener getErrorListener()
  {
    return m_errorListener;
  }

  /**
   * Set an error listener for the TransformerFactory.
   *
   * @param listener Must be a non-null reference to an ErrorListener.
   *
   * @throws IllegalArgumentException if the listener argument is null.
   */
  public void setErrorListener(ErrorListener listener)
          throws IllegalArgumentException
  {

    if (null == listener)
      throw new IllegalArgumentException(XSLMessages.createMessage(XSLTErrorResources.ER_ERRORLISTENER, null));
      // "ErrorListener");

    m_errorListener = listener;
  }
}
