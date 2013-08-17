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
import org.xml.sax.SAXParseException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.Locator;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;
import java.io.IOException;
import org.apache.xml.dtm.ref.CoroutineManager;

import org.apache.xalan.res.XSLTErrorResources;
import org.apache.xalan.res.XSLMessages;


/** <p>CoroutineSAXParser runs a SAX2 parser in a coroutine to achieve
 * incremental parsing. Output from the parser will still be issued
 * via callbacks, which will need to be recieved and acted upon by an
 * appopriate handler. But those callbacks will pass through a
 * counting stage which periodically yields control back to the other
 * coroutines in this set.</p>
 *
 * <p>For a brief usage example, see the unit-test main() method.</p>
 *
 * @deprecated Since the ability to start a parse via the
 * coroutine protocol was not being used and was complicating design.
 * See {@link IncrementalSAXSource_Filter}.
 * */
public class CoroutineSAXParser
implements CoroutineParser, Runnable, ContentHandler, LexicalHandler, ErrorHandler
{

  boolean DEBUG=false; //Internal status report

  //
  // Data
  //

  private CoroutineManager fCoroutineManager = null;
  private int fAppCoroutineID = -1;
  private int fParserCoroutineID = -1;
  private boolean fParseInProgress=false;
  private XMLReader fXMLReader=null;
  private boolean fRunningInThread=false;
  private ContentHandler clientContentHandler=null; // %REVIEW% support multiple?
  private LexicalHandler clientLexicalHandler=null; // %REVIEW% support multiple?
  private ErrorHandler clientErrorHandler=null; // %REVIEW% support multiple?
  private int eventcounter;
  private int frequency=5;

  // Horrendous kluge to run filter to completion. See co_yield()'s
  // internal comments for details. "Is this any way to run a railroad?"
  private boolean fNeverYieldAgain=false;
  

  //
  // Constructors
  //

  public CoroutineSAXParser() {
  }
  
  /** Create a CoroutineSAXParser which is not yet bound to a specific
   * SAX event source.
   *
   * THIS VERSION DOES NOT LAUNCH A THREAD! It is presumed that the
   * application coroutine will be started in a secondary thread and
   * will be waiting for our first yield(), and that the parser will
   * be run in the main thread.
   * 
   * Status: Experimental
   * 
   * @see setXMLReader
   * */
  public CoroutineSAXParser(CoroutineManager co, int appCoroutineID)
  {
    this.init( co, appCoroutineID, null );
  }

  /** Wrap a SAX2 XMLReader (parser or other event source)
   * in a CoroutineSAXParser. This version launches the CoroutineSAXParser
   * in a thread, and prepares it to invoke the parser from that thread
   * upon request.
   *
   * @see doParse
   */
  public CoroutineSAXParser(CoroutineManager co, int appCoroutineID,
                            XMLReader parser) {
    this.init( co, appCoroutineID, parser );
  }

  //
  // Public methods
  //

  public void init( CoroutineManager co, int appCoroutineID, XMLReader parser ) {
    fXMLReader=null;    // No reader yet

    eventcounter=frequency;

    fCoroutineManager = co;
    fAppCoroutineID = appCoroutineID;
    fParserCoroutineID = co.co_joinCoroutineSet(-1);
    if (fParserCoroutineID == -1)
      throw new RuntimeException(XSLMessages.createMessage(XSLTErrorResources.ER_COJOINROUTINESET_FAILED, null)); //"co_joinCoroutineSet() failed");

    fRunningInThread=false; // Unless overridden by the other constructor

    if( parser!=null ) {
      setXMLReader(parser);
    
      fRunningInThread=true;
      org.apache.xalan.transformer.TransformerImpl.runTransformThread(this);
      //Thread t = new Thread(this);
      //t.setDaemon(false);
      //t.start();
    }
    
    // System.err.println(fRunningInThread ? "." : "*"); 
  }
    
  /** Bind to the XMLReader. This operation is ignored if the reader has
   * previously been set.
   *
   * Just a convenience routine; obviously you can explicitly register
   * this as a listener with the same effect.
   *
   * %REVIEW% Should it unbind from the previous reader if there is one?
   * %REVIEW% Do we really need to set fXMLReader???
   *
   * %TBD% This is a quick-hack solution. I'm not convinced that it's
   * adequate. In particular, since in this model parser.parse() is
   * invoked from outside rather than from our run() loop, I'm not
   * sure the end-of-file response is being delivered properly.
   * (There were questions of double-yields in earlier versions of the code.)
   * */
  public void setXMLReader(XMLReader parser)
  {
    if(fXMLReader!=null) return;
    
    fXMLReader=parser;
    fXMLReader.setContentHandler(this);
    fXMLReader.setErrorHandler(this); // to report fatal errors in filtering mode

    // Not supported by all SAX2 parsers:
    try 
      {
        fXMLReader.setProperty("http://xml.org/sax/properties/lexical-handler",
                              this);
      }
    catch(SAXNotRecognizedException e)
      {
        // Nothing we can do about it
      }
    catch(SAXNotSupportedException e)
      {
        // Nothing we can do about it
      }

    // Should we also bind as other varieties of handler?
    // (DTDHandler and so on)
  }

  // Register a content handler for us to output to
  public void setContentHandler(ContentHandler handler)
  {
    clientContentHandler=handler;
  }
  // Register a lexical handler for us to output to
  // Not all parsers support this...
  // ??? Should we register directly on the parser?
  // NOTE NAME -- subclassing issue in the Xerces version
  public void setLexHandler(LexicalHandler handler)
  {
    clientLexicalHandler=handler;
  }
  // Register an error handler for us to output to
  // NOTE NAME -- subclassing issue in the Xerces version
  public void setErrHandler(ErrorHandler handler)
  {
    clientErrorHandler=handler;
  }

  // Set the number of events between resumes of our coroutine
  // Immediately resets number of events before _next_ resume as well.
  public void setReturnFrequency(int events)
  {
    if(events<1) events=1;
    frequency=eventcounter=events;
  }
  
  //
  // ContentHandler methods
  // These  pass the data to our client ContentHandler...
  // but they also count the number of events passing through,
  // and resume our coroutine each time that counter hits zero and
  // is reset.
  //
  // Note that for everything except endDocument, we do the count-and-yield
  // BEFORE passing the call along. I'm hoping that this will encourage JIT
  // compilers to realize that these are tail-calls, reducing the expense of
  // the additional layer of data flow.
  //
  // %REVIEW% Glenn suggests that pausing after endElement, endDocument,
  // and characters may be sufficient. I actually may not want to
  // stop after characters, since in our application these wind up being
  // concatenated before they're processed... but that risks huge blocks of
  // text causing greater than usual readahead. (Unlikely? Consider the
  // possibility of a large base-64 block in a SOAP stream.)
  //
  public void characters(char[] ch, int start, int length)
       throws org.xml.sax.SAXException
  {
    if(--eventcounter<=0)
      {
        co_yield(true);
        eventcounter=frequency;
      }
    if(clientContentHandler!=null)
      clientContentHandler.characters(ch,start,length);
  }
  public void endDocument() 
       throws org.xml.sax.SAXException
  {
    // EXCEPTION: In this case we need to run the event BEFORE we yield.
    if(clientContentHandler!=null)
      clientContentHandler.endDocument();

    eventcounter=0;	
    co_yield(false);
  }
  public void endElement(java.lang.String namespaceURI, java.lang.String localName,
      java.lang.String qName) 
       throws org.xml.sax.SAXException
  {
    if(--eventcounter<=0)
      {
        co_yield(true);
        eventcounter=frequency;
      }
    if(clientContentHandler!=null)
      clientContentHandler.endElement(namespaceURI,localName,qName);
  }
  public void endPrefixMapping(java.lang.String prefix) 
       throws org.xml.sax.SAXException
  {
    if(--eventcounter<=0)
      {
        co_yield(true);
        eventcounter=frequency;
      }
    if(clientContentHandler!=null)
      clientContentHandler.endPrefixMapping(prefix);
  }
  public void ignorableWhitespace(char[] ch, int start, int length) 
       throws org.xml.sax.SAXException
  {
    if(--eventcounter<=0)
      {
        co_yield(true);
        eventcounter=frequency;
      }
    if(clientContentHandler!=null)
      clientContentHandler.ignorableWhitespace(ch,start,length);
  }
  public void processingInstruction(java.lang.String target, java.lang.String data) 
       throws org.xml.sax.SAXException
  {
    if(--eventcounter<=0)
      {
        co_yield(true);
        eventcounter=frequency;
      }
    if(clientContentHandler!=null)
      clientContentHandler.processingInstruction(target,data);
  }
  public void setDocumentLocator(Locator locator) 
  {
    if(--eventcounter<=0)
      {
        // This can cause a hang.  -sb
        // co_yield(true);
        eventcounter=frequency;
      }
    if(clientContentHandler!=null)
      clientContentHandler.setDocumentLocator(locator);
  }
  public void skippedEntity(java.lang.String name) 
       throws org.xml.sax.SAXException
  {
    if(--eventcounter<=0)
      {
        co_yield(true);
        eventcounter=frequency;
      }
    if(clientContentHandler!=null)
      clientContentHandler.skippedEntity(name);
  }
  public void startDocument() 
       throws org.xml.sax.SAXException
  {
    if(--eventcounter<=0)
      {
        co_yield(true);
        eventcounter=frequency;
      }
    if(clientContentHandler!=null)
      clientContentHandler.startDocument();
  }
  public void startElement(java.lang.String namespaceURI, java.lang.String localName,
      java.lang.String qName, Attributes atts) 
       throws org.xml.sax.SAXException
  {
    if(--eventcounter<=0)
      {
        co_yield(true);
        eventcounter=frequency;
      }
    if(clientContentHandler!=null)
      clientContentHandler.startElement(namespaceURI, localName, qName, atts);
  }
  public void startPrefixMapping(java.lang.String prefix, java.lang.String uri) 
       throws org.xml.sax.SAXException
  {
    if(--eventcounter<=0)
      {
        co_yield(true);
        eventcounter=frequency;
      }
    if(clientContentHandler!=null)
      clientContentHandler.startPrefixMapping(prefix,uri);
  }

  //
  // LexicalHandler support. Not all SAX2 parsers support these events
  // but we may want to pass them through when they exist...
  //
  // %REVIEW% These do NOT currently affect the eventcounter; I'm asserting
  // that they're rare enough that it makes little or no sense to
  // pause after them. As such, it may make more sense for folks who
  // actually want to use them to register directly with the parser.
  // But I want 'em here for now, to remind us to recheck this assertion!
  //
  public void comment(char[] ch, int start, int length) 
       throws org.xml.sax.SAXException
  {
    if(null!=clientLexicalHandler)
      clientLexicalHandler.comment(ch,start,length);
  }
  public void endCDATA() 
       throws org.xml.sax.SAXException
  {
    if(null!=clientLexicalHandler)
      clientLexicalHandler.endCDATA();
  }
  public void endDTD() 
       throws org.xml.sax.SAXException
  {
    if(null!=clientLexicalHandler)
      clientLexicalHandler.endDTD();
  }
  public void endEntity(java.lang.String name) 
       throws org.xml.sax.SAXException
  {
    if(null!=clientLexicalHandler)
      clientLexicalHandler.endEntity(name);
  }
  public void startCDATA() 
       throws org.xml.sax.SAXException
  {
    if(null!=clientLexicalHandler)
      clientLexicalHandler.startCDATA();
  }
  public void startDTD(java.lang.String name, java.lang.String publicId,
      java.lang.String systemId) 
       throws org.xml.sax.SAXException
  {
    if(null!=clientLexicalHandler)
      clientLexicalHandler. startDTD(name, publicId, systemId);
  }
  public void startEntity(java.lang.String name) 
       throws org.xml.sax.SAXException
  {
    if(null!=clientLexicalHandler)
      clientLexicalHandler.startEntity(name);
  }

  //
  // ErrorHandler support.
  //
  // PROBLEM: Xerces is apparently _not_ calling the ErrorHandler for
  // exceptions thrown by the ContentHandler, which prevents us from
  // handling this properly when running in filtering mode with Xerces
  // as our event source.  It's unclear whether this is a Xerces bug
  // or a SAX design flaw.
  // 
  // %REVIEW% Current solution: In filtering mode, it is REQUIRED that
  // event source make sure this method is invoked if the event stream
  // abends before endDocument is delivered. If that means explicitly calling
  // us in the exception handling code because it won't be delivered as part
  // of the normal SAX ErrorHandler stream, that's fine; Not Our Problem.
  //
  public void error(SAXParseException exception) throws SAXException
  {
    if(null!=clientErrorHandler)
      clientErrorHandler.error(exception);
  }
  
  public void fatalError(SAXParseException exception) throws SAXException
  {
    // When we're in filtering mode we need to make sure that
    // we terminate the parsing coroutine transaction -- announce
    // the problem and shut down the dialog.

    // PROBLEM: Xerces is apparently _not_ calling the ErrorHandler for
    // exceptions thrown by the ContentHandler... specifically, doTerminate.
    // %TBD% NEED A SOLUTION!
    
    if(!fRunningInThread)
      {
        try
          {
            fCoroutineManager.co_exit_to(exception,
                                         fParserCoroutineID,fAppCoroutineID);
            // %TBD% Do we need to wait for terminate?
          }
        catch(NoSuchMethodException e)
          {
            // Shouldn't happen unless we've miscoded our coroutine logic
            // "Shut down the garbage smashers on the detention level!"
            e.printStackTrace(System.err);
            fCoroutineManager.co_exit(fParserCoroutineID);

            // No need to throw shutdownException; we're already
            // in the process of murdering the parser.
          }
      }

    if(null!=clientErrorHandler)
      clientErrorHandler.error(exception);
  }
  
  public void warning(SAXParseException exception) throws SAXException
  {
    if(null!=clientErrorHandler)
      clientErrorHandler.error(exception);
  }
  

  //
  // coroutine support
  //

  public int getParserCoroutineID() {
    return fParserCoroutineID;
  }

  /** @return the CoroutineManager this CoroutineParser object is bound to.
   * If you're using the do...() methods, applications should only
   * need to talk to the CoroutineManager once, to obtain the
   * application's Coroutine ID.
   * */
  public CoroutineManager getCoroutineManager()
  {
    return fCoroutineManager;
  }

  /** <p>In the SAX delegation code, I've inlined the count-down in
   * the hope of encouraging compilers to deliver better
   * performance. However, if we subclass (eg to directly connect the
   * output to a DTM builder), that would require calling super in
   * order to run that logic... which seems inelegant.  Hence this
   * routine for the convenience of subclasses: every [frequency]
   * invocations, issue a co_yield.</p>
   *
   * @param moreExepcted Should always be true unless this is being called
   * at the end of endDocument() handling.
   * */
  void count_and_yield(boolean moreExpected)
  {
    if(!moreExpected) eventcounter=0;
    
    if(--eventcounter<=0)
      {
        co_yield(true);
        eventcounter=frequency;
      }
  }
  

  /**
   * Co_Yield handles coroutine interactions while a parse is in progress.
   * It will resume with 
   *   co_resume(Boolean.TRUE, ...) on success with more to parse.
   *   co_resume(Boolean.FALSE, ...) on success after endDocument.
   *
   * When control is passed back it may indicate
   *
   *      null            terminate this coroutine.
   *                      Issues
   *                          co_exit_to(null, ...)
   *			  and throws UserRequestedShutdownException
   *
   *      Boolean.TRUE    indication to continue parsing the current document.
   *			  Resumes normal SAX parsing.
   *
   *      Boolean.FALSE   indication to discontinue parsing and reset.
   *			  Throws UserRequestedStopException
   *			  to return control to the run() loop.
   */
  private void co_yield(boolean moreRemains)
  {
    // Horrendous kluge to run filter to completion. See below.
    if(fNeverYieldAgain)
      return;
    
    Object arg= moreRemains ? Boolean.TRUE : Boolean.FALSE;
    
    // %REVIEW% End-of-file behavior. When moreRemains==false, we have
    // just ended parsing the document and are about to return
    // from the parser.
    //
    // If we were invoked from the run() loop, we have to provide
    // the coroutine argument returned above as the next command
    // to be processed in that loop.
    //
    // Conversely, if we've been running in response to
    // fXMLReader.parse() invoked directly, we will not be returning
    // to that loop. In that case, the question becomes one of what we
    // should do instead. The simplest answer would seem to be to do a
    // co_exit_to immediately, since we don't have a command loop to
    // continue to talk to.
    if(!moreRemains)
      {
        if(fRunningInThread)
          {
            // Just return. The command loop in run() will send the
            // "we're done" announcement and request the next command.
            return; // let the parser terminate itself
          }
          
        else try 
          {
            // Forced Termination dialog. Say we're done, wait for a
            // termination request (don't accept anything else), and
            // shut down.
            arg = fCoroutineManager.co_resume(Boolean.FALSE, fParserCoroutineID,
                                              fAppCoroutineID);
            while(arg!=null)
              {
                // %REVIEW% On short documents, our timing-window fix may mean
                // arg is actually TRUE (waiting for nodes). In that case
                // we want to return FALSE (parsing complete). If we get TRUE
                // at any other time, it's a dialog error, but I don't want
                // to deal with inventing yet another value right now.
                // RIGHT FIX will be to redesign the whole coroutine
                // communications protocol.
                if(arg==Boolean.TRUE)
                {
                  arg=Boolean.FALSE;
                }
                else
                {
                  System.err.println(
                    "Filtering CoroutineSAXParser: unexpected resume parameter, "
                    +arg.getClass()+" with value=\""+arg+'"');
                  // If you don't do this, it can loop forever with the above
                  // error printing out.  -sb
                  arg = new RuntimeException(
                    "Filtering CoroutineSAXParser: unexpected resume parameter, "
                    +arg.getClass()+" with value=\""+arg+'"');
                }
                arg = fCoroutineManager.co_resume(arg, fParserCoroutineID,
                                                  fAppCoroutineID);
              }
            
            fCoroutineManager.co_exit_to(arg, fParserCoroutineID, fAppCoroutineID);
            return; // let the parser return
          }
        catch(java.lang.NoSuchMethodException e)
          {
            // Shouldn't happen unless we've miscoded our coroutine logic
            // "Shut down the garbage smashers on the detention level!"
            e.printStackTrace(System.err);
            fCoroutineManager.co_exit(fParserCoroutineID);
            throw shutdownException;
          }
      } // if moreRemains


    else try
      {
        arg = fCoroutineManager.co_resume(arg, fParserCoroutineID, fAppCoroutineID);

        // %REVIEW% I'm really not happy with the following:
        //
        // If we're running in filter mode, driven by an external SAX
        // event source, and have been been yielded back to with
        // Boolean.FALSE (doMore(false)) there are two additional
        // problems.
        // 
        // First: Scott tells me that our technique of throwing an
        // exception from the ContentHandler to terminate SAX parsing,
        // while almost a universal practice in the SAX community, is not
        // acceptable because we can't force this contract upon the event
        // generator. (Though he feels we _can_ force them to accept a
        // contract to explicitly send us a fatalError event if parsing
        // terminates due to some other SAXException... basically, it's
        // the old "this is a normal condition so it shouldn't be an
        // exception" rationalle, which has some validity to it.)
        // Instead, and despite the wasted cycles, he wants me to simply
        // let the event stream run to completion without passing the
        // events along to our own client. That requires disabling
        // co_yeild() as well. IF AND WHEN SAX ADDS AN OFFICIAL
        // STOP-PARSING-EARLY OPERATION, we can leverage that.
        //
        // Second: The current architecture of CoroutineSAXParser's
        // coroutine transactions assumes that doMore(false) will be
        // followed by either doParse(newInputSource), or
        // doTerminate(). We must complete that coroutine dialog.
        // 
        // "Black magic is a matter of symbolism and intent."
        // -- Randall Garrett
        //
        // %TBD% We _MUST_ get away from this architecture and switch
        // to CoroutineSAXFilter, just so we don't have to go through
        // this "no, I don't want another file, thank you very much"
        // transaction. In our application we should never need it,
        // and the only justification for running the parse within a
        // coroutine request -- capturing SAXExceptions -- could be
        // handled per the above discussion.
        // 
        if(!fRunningInThread && arg==Boolean.FALSE)
          {
            clientContentHandler=null;
            clientLexicalHandler=null;
            // Anyone else?
        
            fNeverYieldAgain=true; // Horrendous kluge parsing to completion:

            // Forced Termination dialog. Say we're done, wait for a
            // termination request (don't accept anything else), and
            // shut down.
            arg = fCoroutineManager.co_resume(Boolean.FALSE, fParserCoroutineID,
                                              fAppCoroutineID);
            while(arg!=null)
              {
                String msg="Filtering CoroutineSAXParser: "+
                  "unexpected resume parameter, "+
                  arg.getClass()+" with value=\""+arg+'"';
                System.err.println(msg);
                // If you don't do this, it can loop forever with the above
                // error printing out.  -sb
                arg = new RuntimeException(msg);
                arg = fCoroutineManager.co_resume(arg, fParserCoroutineID,
                                                  fAppCoroutineID);
              }
            
            fCoroutineManager.co_exit_to(arg, fParserCoroutineID, fAppCoroutineID);
            return; // let the parser run to completion and return
          }

        if (arg == null) {
          fCoroutineManager.co_exit_to(arg, fParserCoroutineID, fAppCoroutineID);
          // %REVIEW% For some reason the (arg == null) condition is occuring.
          // You can replicate this sometimes with:
          // testo attribset\attribset01 -flavor th -edump
          // Or with Crimson running, output\output01.
          throw new UserRequestedShutdownException();
          // throw shutdownException;
        }

        else if (arg instanceof Boolean) {
          boolean keepgoing = ((Boolean)arg).booleanValue();
          if (!keepgoing)
            throw stopException;
          }

        else // Unexpected!
          {
            System.err.println(
                  "Active CoroutineSAXParser: unexpected resume parameter, "
                  +arg.getClass
                  ()+" with value=\""+arg+'"');
            System.err.println("\tStopping parser rather than risk deadlock");
            throw new RuntimeException(XSLMessages.createMessage(XSLTErrorResources.ER_COROUTINE_PARAM, new Object[]{arg})); //"Coroutine parameter error ("+arg+')');
          }

      }
    catch(java.lang.NoSuchMethodException e)
      {
        // Shouldn't happen unless we've miscoded our coroutine logic
        // "Shut down the garbage smashers on the detention level!"
        e.printStackTrace(System.err);
        fCoroutineManager.co_exit(fParserCoroutineID);
        throw new UserRequestedShutdownException();
        // throw shutdownException;
      }
  }


  /** Between parser executions, wait for our partner to resume us and
   * tell us what to do:
   *
   *      null            terminate this coroutine.
   *                      exits with:
   *                          co_exit_to(null, ...)
   *                      expects next:
   *                          nothing, we have terminated the thread.
   *
   *      InputSource     setup to read from this source.
   *                      resumes with:
   *                          co_resume(Boolean.TRUE, ...) on partial parse
   *                          co_resume(Boolean.FALSE, ...) on complete parse
   *                          co_resume(Exception, ...) on error.
   *
   * %REVEIW% Should this be able to set listeners? Partner coroutine ID?
   * */
  public void run() {
    try 
      {
        for(Object arg=fCoroutineManager.co_entry_pause(fParserCoroutineID);
            true;
            arg=fCoroutineManager.co_resume(arg, fParserCoroutineID, fAppCoroutineID))
          {
            
            // Shut down requested.
            if (arg == null) {
              if(DEBUG)System.out.println("CoroutineSAXParser at-rest shutdown requested");
              fCoroutineManager.co_exit_to(arg, fParserCoroutineID, fAppCoroutineID);
              break;
            }
            
            // Start-Parse requested
            // For the duration of this operation, all coroutine handshaking
            // will occur in the co_yield method. That's the nice thing about
            // coroutines; they give us a way to hand off control from the
            // middle of a synchronous method.
            if (arg instanceof InputSource) {
              try {
              if(DEBUG)System.out.println("Inactive CoroutineSAXParser new parse "+arg);
                fXMLReader.parse((InputSource)arg);
                // Tell caller we returned from parsing
                arg=Boolean.FALSE;
              }

              catch (SAXException ex) {
                Exception inner=ex.getException();
                if(inner instanceof UserRequestedStopException){
                  if(DEBUG)System.out.println("Active CoroutineSAXParser user stop exception");
                  arg=Boolean.FALSE;
                }
                else if(inner instanceof UserRequestedShutdownException){
                  if(DEBUG)System.out.println("Active CoroutineSAXParser user shutdown exception");
                  break;
                }
                else {
                  if(DEBUG)System.out.println("Active CoroutineSAXParser UNEXPECTED SAX exception: "+ex);
                  arg=ex;		  
                }
                
              }
              catch(Exception ex)
                {
                  if(DEBUG)System.out.println("Active CoroutineSAXParser non-SAX exception: "+ex);
                  arg = ex;
                }
              
            }

            else // Unexpected!
              {
                System.err.println(
                  "Inactive CoroutineSAXParser: unexpected resume parameter, "
                  +arg.getClass()+" with value=\""+arg+'"');
                  
                // If you don't do this, it can loop forever with the above
                // error printing out.  -sb
                arg = new RuntimeException(
                  "Inactive CoroutineSAXParser: unexpected resume parameter, "
                  +arg.getClass()+" with value=\""+arg+'"');
              }

          } // end while
      } // end try
    catch(java.lang.NoSuchMethodException e)
      {
        // Shouldn't happen unless we've miscoded our coroutine logic
        // "CPO, shut down the garbage smashers on the detention level!"
        e.printStackTrace(System.err);
        fCoroutineManager.co_exit(fParserCoroutineID);
      }
  }

  /** Used so co_yield can return control to run for early parse
   * termination.  */
  class UserRequestedStopException extends RuntimeException
  {
  }
  
  /** %REVIEW% Should be static, but can't be because internal class */
  final UserRequestedStopException stopException=new UserRequestedStopException();

  /** Used so co_yield can return control to run for coroutine thread
   * termination.  */
  class UserRequestedShutdownException extends RuntimeException
  {
  }

  /** %REVIEW% Should be static, but can't be because internal class */
  final UserRequestedShutdownException shutdownException = new UserRequestedShutdownException();

  //================================================================
  /** doParse() is a simple API which tells the coroutine parser
   * to begin reading from a file.  This is intended to be called from one
   * of our partner coroutines, and serves both to encapsulate the
   * communication protocol and to avoid having to explicitly use the
   * CoroutineParser's coroutine ID number.
   *
   * %REVIEW%: If we are NOT running in a thread (if we bound to an
   * externally invoked XMLReader), this operation is a no-op. I don't
   * _think_ it can safely synchronously invoke that reader's parse()
   * operation, or doMore(), which would be the obvious alternatives....
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
    // %REVIEW% I'm not wild about this solution...
    if(!fRunningInThread)
      return Boolean.TRUE; // "Yes, we expect to deliver events."

    try 
      {
        Object result=    
          fCoroutineManager.co_resume(source, appCoroutineID, fParserCoroutineID);
        
        // %REVIEW% Better error reporting needed... though most of these
        // should never arise during normal operation.
        // Should this rethrow the parse exception?
        if (result instanceof Exception) {
          if(result instanceof SAXException)
          {
            SAXException se = (SAXException)result;
            Exception e = se.getException();
            if(null != e)
            {
              e.printStackTrace();
            }
            else
            {
              System.out.println("\nParser threw exception:");
              se.printStackTrace();
            }
          }
          else
          {
            System.out.println("\nParser threw exception:");
            ((Exception)result).printStackTrace();
          }
        }

        return result;
      }

    // SHOULD NEVER OCCUR, since the coroutine number and coroutine manager
    // are those previously established for this CoroutineSAXParser...
    // So I'm just going to return it as a parsing exception, for now.
    catch(NoSuchMethodException e)
      {
        return e;
      }
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
  public Object doMore(boolean parsemore, int appCoroutineID)
  {
    try 
      {
        Object result =
          fCoroutineManager.co_resume(parsemore?Boolean.TRUE:Boolean.FALSE,
                                      appCoroutineID, fParserCoroutineID);
        
        // %REVIEW% Better error reporting needed
        if (result == null)
          {
            System.out.println("\nUNEXPECTED: Parser doMore says shut down prematurely.\n");
          }
        else if (result instanceof Exception) {
          System.out.println("\nParser threw exception:");
          ((Exception)result).printStackTrace();
        }
        
        return result;
      }
  
    // SHOULD NEVER OCCUR, since the coroutine number and coroutine manager
    // are those previously established for this CoroutineSAXParser...
    // So I'm just going to return it as a parsing exception, for now.
    catch(NoSuchMethodException e)
      {
        return e;
      }
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
    try
      {
        Object result=Boolean.FALSE; // Dummy initial value

        // Timing problem diagnostic/force-to-completion code; better to fix the problem.
        //while(result!=null)
          result = fCoroutineManager.co_resume(null, appCoroutineID, fParserCoroutineID);

        // Debugging; shouldn't arise in normal operation
        if(result!=null)
        {
          RuntimeException re = new RuntimeException(XSLMessages.createMessage(XSLTErrorResources.ER_PARSER_DOTERMINATE_ANSWERS, new Object[]{result})); //"\nUNEXPECTED: Parser doTerminate answers "+result);
          // System.out.println("\nUNEXPECTED: Parser doTerminate answers "+result);
          re.printStackTrace();
          // throw re;
        }
      }
    catch(java.lang.NoSuchMethodException e)
      {
        // That's OK; if it doesn't exist, we don't need to terminate it
      }
  }

//  //================================================================
//  /** Simple unit test. Attempt coroutine parsing of document indicated
//   * by first argument (as a URI), report progress.
//   */
//  public static void main(String args[])
//  {
//    System.out.println("Starting...");
//
//    org.xml.sax.XMLReader theSAXParser=
//      new org.apache.xerces.parsers.SAXParser();
//    
//    CoroutineManager co = new CoroutineManager();
//    int appCoroutineID = co.co_joinCoroutineSet(-1);
//    if (appCoroutineID == -1)
//      {
//        System.out.println("ERROR: Couldn't allocate coroutine number.\n");
//        return;
//      }
//    CoroutineSAXParser parser=
//      new CoroutineSAXParser(co, appCoroutineID, theSAXParser);
//    int parserCoroutineID = parser.getParserCoroutineID();
//
//    // Use a serializer as our sample output
//    org.apache.xml.serialize.XMLSerializer trace;
//    trace=new org.apache.xml.serialize.XMLSerializer(System.out,null);
//    parser.setContentHandler(trace);
//    parser.setLexHandler(trace);
//
//    // Tell coroutine to begin parsing, run while parsing is in progress
//    for(int arg=0;arg<args.length;++arg)
//      {
//        InputSource source = new InputSource(args[arg]);
//        Object result=null;
//        boolean more=true;
//        for(result = parser.doParse(source, appCoroutineID);
//            (result instanceof Boolean && ((Boolean)result)==Boolean.TRUE);
//            result = parser.doMore(more, appCoroutineID))
//          {
//            System.out.println("\nSome parsing successful, trying more.\n");
//            
//            // Special test: Terminate parsing early.
//            if(arg+1<args.length && "!".equals(args[arg+1]))
//              {
//                ++arg;
//                more=false;
//              }
//            
//          }
//        
//        if (result instanceof Boolean && ((Boolean)result)==Boolean.FALSE)
//          {
//            System.out.println("\nParser ended (EOF or on request).\n");
//          }
//        else if (result == null) {
//          System.out.println("\nUNEXPECTED: Parser says shut down prematurely.\n");
//        }
//        else if (result instanceof Exception) {
//          System.out.println("\nParser threw exception:");
//          ((Exception)result).printStackTrace();
//        }
//        
//      }
//
//    parser.doTerminate(appCoroutineID);
//  }
  
} // class CoroutineSAXParser
