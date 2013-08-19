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
package org.apache.xml.utils;

import org.xml.sax.*;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;
import javax.xml.transform.SourceLocator;

import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;
 
import java.io.PrintWriter;
import java.io.PrintStream;


/**
 * <meta name="usage" content="general"/>
 * Implement SAX error handler for default reporting.
 */
public class DefaultErrorHandler implements ErrorHandler, ErrorListener
{
  PrintWriter m_pw;

  /**
   * Constructor DefaultErrorHandler
   */
  public DefaultErrorHandler(PrintWriter pw)
  {
    m_pw = pw;
  }
  
  /**
   * Constructor DefaultErrorHandler
   */
  public DefaultErrorHandler(PrintStream pw)
  {
    m_pw = new PrintWriter(pw, true);
  }
  
  /**
   * Constructor DefaultErrorHandler
   */
  public DefaultErrorHandler()
  {
    m_pw = new PrintWriter(System.err, true);
  }


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
   * @param exception The warning information encapsulated in a
   *                  SAX parse exception.
   * @throws SAXException Any SAX exception, possibly
   *            wrapping another exception.
   */
  public void warning(SAXParseException exception) throws SAXException
  {
    printLocation(m_pw, exception);
    m_pw.println("Parser warning: " + exception.getMessage());
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
   * @param exception The error information encapsulated in a
   *                  SAX parse exception.
   * @throws SAXException Any SAX exception, possibly
   *            wrapping another exception.
   */
  public void error(SAXParseException exception) throws SAXException
  {
    //printLocation(exception);
    // m_pw.println(exception.getMessage());

    throw exception;
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
   * @throws SAXException Any SAX exception, possibly
   *            wrapping another exception.
   */
  public void fatalError(SAXParseException exception) throws SAXException
  {
    // printLocation(exception);
    // m_pw.println(exception.getMessage());

    throw exception;
  }
  
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
   * @param exception The warning information encapsulated in a
   *                  SAX parse exception.
   * @throws javax.xml.transform.TransformerException Any SAX exception, possibly
   *            wrapping another exception.
   * @see javax.xml.transform.TransformerException
   */
  public void warning(TransformerException exception) throws TransformerException
  {
    printLocation(m_pw, exception);

    m_pw.println(exception.getMessage());
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
   * @param exception The error information encapsulated in a
   *                  SAX parse exception.
   * @throws javax.xml.transform.TransformerException Any SAX exception, possibly
   *            wrapping another exception.
   * @see javax.xml.transform.TransformerException
   */
  public void error(TransformerException exception) throws TransformerException
  {
    // printLocation(exception);
    // ensureLocationSet(exception);

    throw exception;
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
   * @throws javax.xml.transform.TransformerException Any SAX exception, possibly
   *            wrapping another exception.
   * @see javax.xml.transform.TransformerException
   */
  public void fatalError(TransformerException exception) throws TransformerException
  {
    // printLocation(exception);
    // ensureLocationSet(exception);

    throw exception;
  }
  
  public static void ensureLocationSet(TransformerException exception)
  {
    // SourceLocator locator = exception.getLocator();
    SourceLocator locator = null;
    Throwable cause = exception;
    
    // Try to find the locator closest to the cause.
    do
    {
      if(cause instanceof SAXParseException)
      {
        locator = new SAXSourceLocator((SAXParseException)cause);
      }
      else if (cause instanceof TransformerException)
      {
        SourceLocator causeLocator = ((TransformerException)cause).getLocator();
        if(null != causeLocator)
          locator = causeLocator;
      }
      
      if(cause instanceof TransformerException)
        cause = ((TransformerException)cause).getCause();
      else if(cause instanceof SAXException)
        cause = ((SAXException)cause).getException();
      else
        cause = null;
    }
    while(null != cause);
    
    exception.setLocator(locator);
  }
  
  public static void printLocation(PrintStream pw, TransformerException exception)
  {
    printLocation(new PrintWriter(pw), exception);
  }
  
  public static void printLocation(java.io.PrintStream pw, org.xml.sax.SAXParseException exception)
  {
    printLocation(new PrintWriter(pw), exception);
  }
  
  public static void printLocation(PrintWriter pw, Throwable exception)
  {
    SourceLocator locator = null;
    Throwable cause = exception;
    
    // Try to find the locator closest to the cause.
    do
    {
      if(cause instanceof SAXParseException)
      {
        locator = new SAXSourceLocator((SAXParseException)cause);
      }
      else if (cause instanceof TransformerException)
      {
        SourceLocator causeLocator = ((TransformerException)cause).getLocator();
        if(null != causeLocator)
          locator = causeLocator;
      }
      if(cause instanceof TransformerException)
        cause = ((TransformerException)cause).getCause();
      else if(cause instanceof WrappedRuntimeException)
        cause = ((WrappedRuntimeException)cause).getException();
      else if(cause instanceof SAXException)
        cause = ((SAXException)cause).getException();
      else
        cause = null;
    }
    while(null != cause);
        
    if(null != locator)
    {
      // m_pw.println("Parser fatal error: "+exception.getMessage());
      String id = (null != locator.getPublicId() )
                  ? locator.getPublicId()
                    : (null != locator.getSystemId())
                      ? locator.getSystemId() : XSLMessages.createMessage(XSLTErrorResources.ER_SYSTEMID_UNKNOWN, null); //"SystemId Unknown";

      pw.print(id + "; " +XSLMessages.createMessage("line", null) + locator.getLineNumber()
                         + "; " +XSLMessages.createMessage("column", null) + locator.getColumnNumber()+"; ");
    }
    else
      pw.print("("+XSLMessages.createMessage(XSLTErrorResources.ER_LOCATION_UNKNOWN, null)+")");
  }
}
