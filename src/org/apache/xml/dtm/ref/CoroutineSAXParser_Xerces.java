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

//package org.apache.xerces.parsers;
package org.apache.xml.dtm.ref;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.io.IOException;
//import org.apache.xml.dtm.CoroutineManager;
import org.apache.xerces.parsers.SAXParser;
import org.xml.sax.XMLReader;

import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.res.XSLMessages;


/** <p>CoroutineSAXParser_Xerces takes advantage of the fact that Xerces
 * incremental mode is already a coroutine of sorts, and just wraps our
 * CoroutineParser API around it.</p>
 *
 * NOTE that this requires access to a few features which are not yet
 * part of the public Xerces API. Glenn solved that by making this class
 * a subclass of xerces.parsers.SAXParser. A better solution may be to
 * make a separate subclass thereof which exposes the relevant methods as
 * public -- both because it provides more explicit control over what
 * is and isn't exposed, and because it holds out the hope of dropping that
 * subclass and plugging in a normal instance of Xerces if/when these methods
 * become official API calls.
 *
 * <p>Usage example: See main().</p>
 *
 * @deprecated Since the ability to start a parse via the
 * coroutine protocol was not being used and was complicating design.
 * See {@link IncrementalSAXSource_Xerces}.
 * */
public class CoroutineSAXParser_Xerces
// extends org.apache.xerces.parsers.SAXParser
implements CoroutineParser
{
  // IncrementalXercesSaxParser incrementalParser;
  SAXParser incrementalParser;

    //
    // Data
    //

    private boolean fParseInProgress=false;

    //
    // Constructors
    //
    public CoroutineSAXParser_Xerces() {
    }
    
    public CoroutineSAXParser_Xerces(CoroutineManager co, int appCoroutineID) {
      this.init( co,appCoroutineID, null);
    }

    // XXX the parameter order is confusing, different from CoroutineSAXParser
    public CoroutineSAXParser_Xerces(SAXParser ixsp, CoroutineManager co, int appCoroutineID) {
      this.init( co, appCoroutineID, ixsp); 
    }

    public void init( CoroutineManager co, int appCoroutineID, XMLReader ixsp ) {
      if(ixsp!=null)
        incrementalParser=(SAXParser)ixsp;
      else
        incrementalParser=new SAXParser();
      
      // incrementalParser.initHandlers(true, incrementalParser, incrementalParser);
    }
    
    //
    // Factories
    //
    static public CoroutineParser createCoroutineParser(CoroutineManager co, int appCoroutineID) {
      return new CoroutineSAXParser_Xerces(co, appCoroutineID);
    }

    //
    // Public methods
    //

    // coroutine support

    public int getParserCoroutineID() {
      // return fParserCoroutineID;
      return -1;
    }

  /** @return the CoroutineManager this CoroutineParser object is bound to.
   * If you're using the do...() methods, applications should only
   * need to talk to the CoroutineManager once, to obtain the
   * application's Coroutine ID.
   * */
  public CoroutineManager getCoroutineManager()
  {
    // return fCoroutineManager;
    return null;
  }

  // Register handler directly with the incremental parser
  public void setContentHandler(org.xml.sax.ContentHandler handler)
  {
    incrementalParser.setContentHandler(handler);
  }

  // Note name, needed to dodge the inherited Xerces setLexicalHandler
  // which isn't public.
  public void setLexHandler(org.xml.sax.ext.LexicalHandler handler)
  {
    // Not supported by all SAX2 parsers but should work in Xerces:
    try 
      {
        incrementalParser.setProperty("http://xml.org/sax/properties/lexical-handler",
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
  /** doParse() is a simple API which tells the coroutine parser
   * to begin reading from a file.  This is intended to be called from one
   * of our partner coroutines, and serves both to encapsulate the
   * communication protocol and to avoid having to explicitly use the
   * CoroutineParser's coroutine ID number.
   *
   * %REVIEW% Can/should this unify with doMore? (if URI hasn't changed,
   * parse more from same file, else end and restart parsing...?
   *
   * @param source The InputSource to parse from.
   * @param appCoroutineID The coroutine ID number of the coroutine invoking
   * this method, so it can be resumed after the parser has responded to the
   * request.
   * @return Boolean.TRUE if the CoroutineParser believes more data may be available
   * for further parsing. Boolean.FALSE if parsing ran to completion.
   * Exception if the parser objected for some reason.
   * */
  public Object doParse(InputSource source, int appCoroutineID)
  {
    if (fParseInProgress) {
      // %review% -- We never set this flag in the previous version of
      // this class, we still don't set it here... Discard, or fix?
      return new SAXException(XSLMessages.createMessage(XSLTErrorResources.ER_NO_PARSE_CALL_WHILE_PARSING, null)); //"parse may not be called while parsing.");
    }
    
    Object arg;
    try {
      boolean ok = incrementalParser.parseSomeSetup(source);
      arg = ok ? Boolean.TRUE : Boolean.FALSE;
    }
    catch (Exception ex) {
      arg = ex;
    }
    return arg;
  }

  
  /** doMore() is a simple API which tells the coroutine parser
   * that we need more nodes.  This is intended to be called from one
   * of our partner coroutines, and serves both to encapsulate the
   * communication protocol and to avoid having to explicitly use the
   * CoroutineParser's coroutine ID number.
   *
   * @param parsemore If true, tells the incremental parser to generate
   * another chunk of output. If false, tells the parser that we're
   * satisfied and it can terminate parsing of this document.
   * @param appCoroutineID The coroutine ID number of the coroutine invoking
   * this method, so it can be resumed after the parser has responded to the
   * request.
   * @return Boolean.TRUE if the CoroutineParser believes more data may be available
   * for further parsing. Boolean.FALSE if parsing ran to completion.
   * Exception if the parser objected for some reason.
   * */
  public Object doMore (boolean parsemore, int appCoroutineID)
  {
    if(!parsemore)
      {
        fParseInProgress=false;
        return Boolean.FALSE;
      }

    Object arg;
    try {
      boolean keepgoing = incrementalParser.parseSome();
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
  
  /** doTerminate() is a simple API which tells the coroutine
   * parser to terminate itself.  This is intended to be called from
   * one of our partner coroutines, and serves both to encapsulate the
   * communication protocol and to avoid having to explicitly use the
   * CoroutineParser's coroutine ID number.
   *
   * Returns only after the CoroutineParser has acknowledged the request.
   *
   * @param appCoroutineID The coroutine ID number of the coroutine invoking
   * this method, so it can be resumed after the parser has responded to the
   * request.
   * */
  public void doTerminate(int appCoroutineID)
  {
    // No-op in IncrementalXerces?

    // Could release some data structures to promote garbage collection...
    incrementalParser=null;
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
    CoroutineSAXParser_Xerces parser=
      new CoroutineSAXParser_Xerces(co, appCoroutineID);
    int parserCoroutineID = parser.getParserCoroutineID();

    // Use a serializer as our sample output
    org.apache.xml.serialize.XMLSerializer trace;
    trace=new org.apache.xml.serialize.XMLSerializer(System.out,null);
    parser.setContentHandler(trace);
    parser.setLexHandler(trace);

    // Tell coroutine to begin parsing, run while parsing is in progress
    for(int arg=0;arg<args.length;++arg)
      {
        InputSource source = new InputSource(args[arg]);
        Object result=null;
        boolean more=true;
        /**    
          for(result = co.co_resume(source, appCoroutineID, parserCoroutineID);
          (result instanceof Boolean && ((Boolean)result)==Boolean.TRUE);
          result = co.co_resume(more, appCoroutineID, parserCoroutineID))
          **/
        for(result = parser.doParse(source, appCoroutineID);
            (result instanceof Boolean && ((Boolean)result)==Boolean.TRUE);
            result = parser.doMore(more, appCoroutineID))
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

    parser.doTerminate(appCoroutineID);
  }
  
} // class CoroutineSAXParser
