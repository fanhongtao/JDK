/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999,2000 The Apache Software Foundation.  All rights 
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

package org.apache.xml.dtm.ref;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.XMLReader;

import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.res.XSLMessages;


/** <p>IncrementalSAXSource_Xerces takes advantage of the fact that Xerces
 * incremental mode is already a coroutine of sorts, and just wraps our
 * IncrementalSAXSource API around it.</p>
 *
 * <p>Usage example: See main().</p>
 *
 * <p>Status: Passes simple main() unit-test. NEEDS JAVADOC.</p>
 * */
public class IncrementalSAXSource_Xerces
  implements IncrementalSAXSource
{
  SAXParser fIncrementalParser;

  //
  // Data
  //

  private boolean fParseInProgress=false;

  //
  // Constructors
  //

  /** Create a IncrementalSAXSource_Xerces, and create a SAXParsre
   * to go with it
   * */
  public IncrementalSAXSource_Xerces() {
    fIncrementalParser=new SAXParser();
  }

  /** Create a IncrementalSAXSource_Xerces wrapped around
   * an existing SAXParser
   * */
  public IncrementalSAXSource_Xerces(SAXParser parser) {
    fIncrementalParser=parser;
  }

  //
  // Factories
  //
  static public IncrementalSAXSource createIncrementalSAXSource() {
    return new IncrementalSAXSource_Xerces();
  }
  static public IncrementalSAXSource
  createIncrementalSAXSource(SAXParser parser) {
    return new IncrementalSAXSource_Xerces(parser);
  }

  //
  // Public methods
  //

  // Register handler directly with the incremental parser
  public void setContentHandler(org.xml.sax.ContentHandler handler)
  {
    fIncrementalParser.setContentHandler(handler);
  }

  // Note name, needed to dodge the inherited Xerces setLexicalHandler
  // which isn't public.
  public void setLexicalHandler(org.xml.sax.ext.LexicalHandler handler)
  {
    // Not supported by all SAX2 parsers but should work in Xerces:
    try 
    {
      fIncrementalParser.setProperty("http://xml.org/sax/properties/lexical-handler",
                                     handler);
    }
    catch(org.xml.sax.SAXNotRecognizedException e)
    {
      // Nothing we can do about it
    }
    catch(org.xml.sax.SAXNotSupportedException e)
    {
      // Nothing we can do about it
    }
  }
  
  //================================================================
  /** startParse() is a simple API which tells the IncrementalSAXSource
   * to begin reading a document.
   *
   * @throws SAXException is parse thread is already in progress
   * or parsing can not be started.
   * */
  public void startParse(InputSource source) throws SAXException
  {
    if (fIncrementalParser==null)
      throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_STARTPARSE_NEEDS_SAXPARSER, null)); //"startParse needs a non-null SAXParser.");
    if (fParseInProgress)
      throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_STARTPARSE_WHILE_PARSING, null)); //"startParse may not be called while parsing.");

    boolean ok=false;

    try
    {
      ok = fIncrementalParser.parseSomeSetup(source);
    }
    catch(Exception ex)
    {
      throw new SAXException(ex);
    }
    
    if(!ok)
      throw new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_COULD_NOT_INIT_PARSER, null)); //"could not initialize parser with");
  }

  
  /** deliverMoreNodes() is a simple API which tells the coroutine
   * parser that we need more nodes.  This is intended to be called
   * from one of our partner routines, and serves to encapsulate the
   * details of how incremental parsing has been achieved.
   *
   * @param parsemore If true, tells the incremental parser to generate
   * another chunk of output. If false, tells the parser that we're
   * satisfied and it can terminate parsing of this document.
   * @return Boolean.TRUE if the CoroutineParser believes more data may be available
   * for further parsing. Boolean.FALSE if parsing ran to completion.
   * Exception if the parser objected for some reason.
   * */
  public Object deliverMoreNodes (boolean parsemore)
  {
    if(!parsemore)
    {
      fParseInProgress=false;
      return Boolean.FALSE;
    }

    Object arg;
    try {
      boolean keepgoing = fIncrementalParser.parseSome();
      arg = keepgoing ? Boolean.TRUE : Boolean.FALSE;
    } catch (SAXException ex) {
      arg = ex;
    } catch (IOException ex) {
      arg = ex;
    } catch (Exception ex) {
      arg = new SAXException(ex);
    }
    return arg;
  }

  //================================================================
  /** Simple unit test. Attempt coroutine parsing of document indicated
   * by first argument (as a URI), report progress.
   */
  public static void main(String args[])
  {
    System.out.println("Starting...");

    CoroutineManager co = new CoroutineManager();
    int appCoroutineID = co.co_joinCoroutineSet(-1);
    if (appCoroutineID == -1)
    {
      System.out.println("ERROR: Couldn't allocate coroutine number.\n");
      return;
    }
    IncrementalSAXSource_Xerces parser=
      new IncrementalSAXSource_Xerces();

    // Use a serializer as our sample output
    org.apache.xml.serialize.XMLSerializer trace;
    trace=new org.apache.xml.serialize.XMLSerializer(System.out,null);
    parser.setContentHandler(trace);
    parser.setLexicalHandler(trace);

    // Tell coroutine to begin parsing, run while parsing is in progress

    for(int arg=0;arg<args.length;++arg)
    {
      try
      {
        InputSource source = new InputSource(args[arg]);
        Object result=null;
        boolean more=true;
        parser.startParse(source);
        for(result = parser.deliverMoreNodes(more);
            result==Boolean.TRUE;
            result = parser.deliverMoreNodes(more))
        {
          System.out.println("\nSome parsing successful, trying more.\n");
            
          // Special test: Terminate parsing early.
          if(arg+1<args.length && "!".equals(args[arg+1]))
          {
            ++arg;
            more=false;
          }
            
        }
        
        if (result instanceof Boolean && ((Boolean)result)==Boolean.FALSE)
        {
          System.out.println("\nParser ended (EOF or on request).\n");
        }
        else if (result == null) {
          System.out.println("\nUNEXPECTED: Parser says shut down prematurely.\n");
        }
        else if (result instanceof Exception) {
          throw new org.apache.xml.utils.WrappedRuntimeException((Exception)result);
          //          System.out.println("\nParser threw exception:");
          //          ((Exception)result).printStackTrace();
        }
        
      }

      catch(SAXException e)
      {
        e.printStackTrace();
      }
    }
    
  }

  
} // class IncrementalSAXSource_Xerces
