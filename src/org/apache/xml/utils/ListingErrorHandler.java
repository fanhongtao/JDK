/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000-2001 The Apache Software Foundation.  All rights 
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
 * originally based on software copyright (c) 2000, Lotus
 * Development Corporation., http://www.lotus.com.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

package org.apache.xml.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.SourceLocator;
import javax.xml.transform.TransformerException;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.res.XSLTErrorResources;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/**
 * <meta name="usage" content="general"/>
 * Sample implementation of similar SAX ErrorHandler and JAXP ErrorListener.  
 *
 * <p>This implementation is suitable for various use cases, and 
 * provides some basic configuration API's as well to control 
 * when we re-throw errors, etc.</p>
 *
 * @author shane_curcuru@us.ibm.com
 * @version $Id: ListingErrorHandler.java,v 1.2 2002/06/07 17:13:54 sboag Exp $
 */
public class ListingErrorHandler implements ErrorHandler, ErrorListener
{
    protected PrintWriter m_pw = null;
  

    /**
     * Constructor ListingErrorHandler; user-supplied PrintWriter.  
     */
    public ListingErrorHandler(PrintWriter pw)
    {
        if (null == pw)
            throw new NullPointerException(XSLMessages.createMessage(XSLTErrorResources.ER_ERRORHANDLER_CREATED_WITH_NULL_PRINTWRITER, null));
            // "ListingErrorHandler created with null PrintWriter!");
            
        m_pw = pw;
    }

    /**
     * Constructor ListingErrorHandler; uses System.err.  
     */
    public ListingErrorHandler()
    {
        m_pw = new PrintWriter(System.err, true);
    }


    /* ======== Implement org.xml.sax.ErrorHandler ======== */
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
     * wrapping another exception; only if setThrowOnWarning is true.
     * @see org.xml.sax.SAXParseException 
     */
    public void warning (SAXParseException exception)
    	throws SAXException
    {
    	logExceptionLocation(m_pw, exception);
        // Note: should we really call .toString() below, since 
        //  sometimes the message is not properly set?
        m_pw.println("warning: " + exception.getMessage());
        m_pw.flush();

        if (getThrowOnWarning())
            throw exception;
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
     * wrapping another exception; only if setThrowOnErroris true.
     * @see org.xml.sax.SAXParseException 
     */
    public void error (SAXParseException exception)
    	throws SAXException
    {
    	logExceptionLocation(m_pw, exception);
        m_pw.println("error: " + exception.getMessage());
        m_pw.flush();

        if (getThrowOnError())
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
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     * wrapping another exception; only if setThrowOnFatalError is true.
     * @see org.xml.sax.SAXParseException
     */
    public void fatalError (SAXParseException exception)
    	throws SAXException
    {
    	logExceptionLocation(m_pw, exception);
        m_pw.println("fatalError: " + exception.getMessage());
        m_pw.flush();

        if (getThrowOnFatalError())
            throw exception;
    }


    /* ======== Implement javax.xml.transform.ErrorListener ======== */

    /**
     * Receive notification of a warning.
     *
     * <p>{@link javax.xml.transform.Transformer} can use this method to report
     * conditions that are not errors or fatal errors.  The default behaviour
     * is to take no action.</p>
     *
     * <p>After invoking this method, the Transformer must continue with
     * the transformation. It should still be possible for the
     * application to process the document through to the end.</p>
     *
     * @param exception The warning information encapsulated in a
     *                  transformer exception.
     *
     * @throws javax.xml.transform.TransformerException  only if 
     * setThrowOnWarning is true.
     *
     * @see javax.xml.transform.TransformerException
     */
    public void warning(TransformerException exception)
        throws TransformerException
    {
    	logExceptionLocation(m_pw, exception);
        m_pw.println("warning: " + exception.getMessage());
        m_pw.flush();

        if (getThrowOnWarning())
            throw exception;
    }

    /**
     * Receive notification of a recoverable error.
     *
     * <p>The transformer must continue to try and provide normal transformation
     * after invoking this method.  It should still be possible for the
     * application to process the document through to the end if no other errors
     * are encountered.</p>
     *
     * @param exception The error information encapsulated in a
     *                  transformer exception.
     *
     * @throws javax.xml.transform.TransformerException  only if 
     * setThrowOnError is true.
     *
     * @see javax.xml.transform.TransformerException
     */
    public void error(TransformerException exception)
        throws TransformerException
    {
    	logExceptionLocation(m_pw, exception);
        m_pw.println("error: " + exception.getMessage());
        m_pw.flush();

        if (getThrowOnError())
            throw exception;
    }

    /**
     * Receive notification of a non-recoverable error.
     *
     * <p>The transformer must continue to try and provide normal transformation
     * after invoking this method.  It should still be possible for the
     * application to process the document through to the end if no other errors
     * are encountered, but there is no guarantee that the output will be
     * useable.</p>
     *
     * @param exception The error information encapsulated in a
     *                  transformer exception.
     *
     * @throws javax.xml.transform.TransformerException  only if 
     * setThrowOnError is true.
     *
     * @see javax.xml.transform.TransformerException
     */
    public void fatalError(TransformerException exception)
        throws TransformerException
    {
    	logExceptionLocation(m_pw, exception);
        m_pw.println("error: " + exception.getMessage());
        m_pw.flush();

        if (getThrowOnError())
            throw exception;
    }



    /* ======== Implement worker methods ======== */


    /**
     * Print out location information about the exception.  
     *
     * Cribbed from DefaultErrorHandler.printLocation() 
     * @param pw PrintWriter to send output to
     * @param exception TransformerException or SAXParseException
     * to log information about
     */
    public static void logExceptionLocation(PrintWriter pw, Throwable exception)
    {
        if (null == pw)
            pw = new PrintWriter(System.err, true);
        
        SourceLocator locator = null;
        Throwable cause = exception;

        // Try to find the locator closest to the cause.
        do
        {
            // Find the current locator, if one present
            if(cause instanceof SAXParseException)
            {
                // A SAXSourceLocator is a Xalan helper class 
                //  that implements both a SourceLocator and a SAX Locator
                //@todo check that the new locator actually has 
                //  as much or more information as the 
                //  current one already does
                locator = new SAXSourceLocator((SAXParseException)cause);
            }
            else if (cause instanceof TransformerException)
            {
                SourceLocator causeLocator = ((TransformerException)cause).getLocator();
                if(null != causeLocator)
                {
                    locator = causeLocator;
                }
            }
            
            // Then walk back down the chain of exceptions
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

        // Formatting note: mimic javac-like errors:
        //  path\filename:123: message-here
        //  systemId:L=1;C=2: message-here
        if(null != locator)
        {
            String id = (locator.getPublicId() != locator.getPublicId())
                      ? locator.getPublicId()
                        : (null != locator.getSystemId())
                          ? locator.getSystemId() : "SystemId-Unknown";

            pw.print(id + ":Line=" + locator.getLineNumber()
                             + ";Column=" + locator.getColumnNumber()+": ");
            pw.println("exception:" + exception.getMessage());
            pw.println("root-cause:" 
                       + ((null != cause) ? cause.getMessage() : "null"));
            logSourceLine(pw, locator); 
        }
        else
        {
            pw.print("SystemId-Unknown:locator-unavailable: ");
            pw.println("exception:" + exception.getMessage());
            pw.println("root-cause:" 
                       + ((null != cause) ? cause.getMessage() : "null"));
        }
    }


    /**
     * Print out the specific source line that caused the exception, 
     * if possible to load it.  
     *
     * @author shane_curcuru@us.ibm.com
     * @param pw PrintWriter to send output to
     * @param locator Xalan wrapper for either a JAXP or a SAX 
     * source location object
     */
    public static void logSourceLine(PrintWriter pw, SourceLocator locator)
    {
        if (null == locator)
            return;
            
        if (null == pw)
            pw = new PrintWriter(System.err, true);

        String url = locator.getSystemId();
        // Bail immediately if we get SystemId-Unknown
        //@todo future improvement: attempt to get resource 
        //  from a publicId if possible
        if (null == url)
        {
            pw.println("line: (No systemId; cannot read file)");
            pw.println();
            return;
        }
        
        //@todo attempt to get DOM backpointer or other ids

        try
        {
            int line = locator.getLineNumber();
            int column = locator.getColumnNumber();
            pw.println("line: " + getSourceLine(url, line));
            StringBuffer buf = new StringBuffer("line: ");
            for (int i = 1; i < column; i++)
            {
                buf.append(' ');
            }
            buf.append('^');
            pw.println(buf.toString());
        }
        catch (Exception e)
        {
            pw.println("line: logSourceLine unavailable due to: " + e.getMessage());
            pw.println();
        }
    }


    /**
     * Return the specific source line that caused the exception, 
     * if possible to load it; allow exceptions to be thrown.  
     *
     * @author shane_curcuru@us.ibm.com
     */
    protected static String getSourceLine(String sourceUrl, int lineNum)
            throws Exception
    {
        URL url = null;
        // Get a URL from the sourceUrl
        try
        {
            // Try to get a URL from it as-is
            url = new URL(sourceUrl);
        }
        catch (java.net.MalformedURLException mue)
        {
            int indexOfColon = sourceUrl.indexOf(':');
            int indexOfSlash = sourceUrl.indexOf('/');
            
            if ((indexOfColon != -1)
                && (indexOfSlash != -1)
                && (indexOfColon < indexOfSlash))
            {
                // The url is already absolute, but we could not get 
                //  the system to form it, so bail
                throw mue;
            }
            else
            {
                // The url is relative, so attempt to get absolute
                url = new URL(SystemIDResolver.getAbsoluteURI(sourceUrl));
                // If this fails, allow the exception to propagate
            }
        }
        
        String line = null;
        InputStream is = null;
        BufferedReader br = null;
        try
        {
            // Open the URL and read to our specified line
            URLConnection uc = url.openConnection();
            is = uc.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));

            // Not the most efficient way, but it works
            // (Feel free to patch to seek to the appropriate line)
            for (int i = 1; i <= lineNum; i++)
            {
                line = br.readLine();
            }
            
        } 
        // Allow exceptions to propagate from here, but ensure 
        //  streams are closed!
        finally
        {
            br.close();
            is.close();
        }
        
        // Return whatever we found
        return line;
    }    


    /* ======== Implement settable properties ======== */

    /**
     * User-settable behavior: when to re-throw exceptions.  
     *
     * <p>This allows per-instance configuration of 
     * ListingErrorHandlers.  You can ask us to either throw 
     * an exception when we're called for various warning / 
     * error / fatalErrors, or simply log them and continue.</p>
     *
     * @param t if we should throw an exception on warnings
     */
    public void setThrowOnWarning(boolean b)
    {
        throwOnWarning = b;
    }

    /**
     * User-settable behavior: when to re-throw exceptions.  
     *
     * @return if we throw an exception on warnings
     */
    public boolean getThrowOnWarning()
    {
        return throwOnWarning;
    }

    /** If we should throw exception on warnings; default:false.  */
    protected boolean throwOnWarning = false;


    /**
     * User-settable behavior: when to re-throw exceptions.  
     *
     * <p>This allows per-instance configuration of 
     * ListingErrorHandlers.  You can ask us to either throw 
     * an exception when we're called for various warning / 
     * error / fatalErrors, or simply log them and continue.</p>
     *
     * <p>Note that the behavior of many parsers/transformers 
     * after an error is not necessarily defined!</p>
     *
     * @param t if we should throw an exception on errors
     */
    public void setThrowOnError(boolean b)
    {
        throwOnError = b;
    }

    /**
     * User-settable behavior: when to re-throw exceptions.  
     *
     * @return if we throw an exception on errors
     */
    public boolean getThrowOnError()
    {
        return throwOnError;
    }

    /** If we should throw exception on errors; default:true.  */
    protected boolean throwOnError = true;


    /**
     * User-settable behavior: when to re-throw exceptions.  
     *
     * <p>This allows per-instance configuration of 
     * ListingErrorHandlers.  You can ask us to either throw 
     * an exception when we're called for various warning / 
     * error / fatalErrors, or simply log them and continue.</p>
     *
     * <p>Note that the behavior of many parsers/transformers 
     * after a fatalError is not necessarily defined, most 
     * products will probably barf if you continue.</p>
     *
     * @param t if we should throw an exception on fatalErrors
     */
    public void setThrowOnFatalError(boolean b)
    {
        throwOnFatalError = b;
    }

    /**
     * User-settable behavior: when to re-throw exceptions.  
     *
     * @return if we throw an exception on fatalErrors
     */
    public boolean getThrowOnFatalError()
    {
        return throwOnFatalError;
    }

    /** If we should throw exception on fatalErrors; default:true.  */
    protected boolean throwOnFatalError = true;

}
