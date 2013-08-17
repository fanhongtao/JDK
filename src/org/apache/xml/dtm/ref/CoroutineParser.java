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
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.XMLReader;

/** <p>CoroutineParser is an API for parser threads that operate as
 * coroutines. See CoroutineSAXParser and CoroutineSAXParser_Xerces
 * for examples.</p>
 *
 * <p>&lt;grumble&gt; I'd like the interface to require a specific form
 * for either the base constructor or a static factory method. Java
 * doesn't allow us to specify either, so I'll just document them
 * here:
 *
 * <ul>
 * <li>public CoroutineParser(CoroutineManager co, int appCoroutine);</li>
 * <li>public CoroutineParser createCoroutineParser(CoroutineManager co, int appCoroutine);</li>
 * </ul>
 *
 * &lt;/grumble&gt;</p>
 *
 * @deprecated Since the ability to start a parse via the
 * coroutine protocol was not being used and was complicating design.
 * See {@link IncrementalSAXSource}.
 * */
public interface CoroutineParser {

    /** @return the coroutine ID number for this CoroutineParser object.
     * Note that this isn't useful unless you know which CoroutineManager
     * you're talking to. Also note that the do...() methods encapsulate
     * the common transactions with the CoroutineParser, so you shouldn't
     * need this in most cases.
     * */
    public int getParserCoroutineID();

    /** @return the CoroutineManager for this CoroutineParser object.
     * If you're using the do...() methods, applications should only
     * need to talk to the CoroutineManager once, to obtain the
     * application's Coroutine ID.
     * */
    public CoroutineManager getCoroutineManager();

  /** Register a SAX-style content handler for us to output to */
  public void setContentHandler(ContentHandler handler);

  /**  Register a SAX-style lexical handler for us to output to
   *  Not all parsers support this...
   *
   * %REVIEW% Not called setLexicalHandler because Xalan uses that name
   * internally, which causes subclassing nuisances. 
   */
  public void setLexHandler(org.xml.sax.ext.LexicalHandler handler);

  /* The run() method is required in CoroutineParsers that run as
   * threads (of course)... but it isn't part of our API, and
   * shouldn't be declared here.
   * */

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
   * @param appCoroutine The coroutine ID number of the coroutine invoking
   * this method, so it can be resumed after the parser has responded to the
   * request.
   * @return Boolean.TRUE if the CoroutineParser believes more data may be available
   * for further parsing. Boolean.FALSE if parsing ran to completion.
   * Exception if the parser objected for some reason.
   * */
  public Object doParse(InputSource source, int appCoroutine);

  /** doMore() is a simple API which tells the coroutine parser
   * that we need more nodes.  This is intended to be called from one
   * of our partner coroutines, and serves both to encapsulate the
   * communication protocol and to avoid having to explicitly use the
   * CoroutineParser's coroutine ID number.
   *
   * @param parsemore If true, tells the incremental parser to generate
   * another chunk of output. If false, tells the parser that we're
   * satisfied and it can terminate parsing of this document.
   * @param appCoroutine The coroutine ID number of the coroutine invoking
   * this method, so it can be resumed after the parser has responded to the
   * request.
   * @return Boolean.TRUE if the CoroutineParser believes more data may be available
   * for further parsing. Boolean.FALSE if parsing ran to completion.
   * Exception if the parser objected for some reason.
   * */
  public Object doMore (boolean parsemore, int appCoroutine);

  /** doTerminate() is a simple API which tells the coroutine
   * parser to terminate itself.  This is intended to be called from
   * one of our partner coroutines, and serves both to encapsulate the
   * communication protocol and to avoid having to explicitly use the
   * CoroutineParser's coroutine ID number.
   *
   * Returns only after the CoroutineParser has acknowledged the request.
   *
   * @param appCoroutine The coroutine ID number of the coroutine invoking
   * this method, so it can be resumed after the parser has responded to the
   * request.
   * */
  public void doTerminate(int appCoroutine);


    /**
     *  Initialize the coroutine parser. Same parameters could be passed
     *  in a non-default constructor, or by using using Class.forName and
     *  newInstance and then calling init()
     */
  public void init( CoroutineManager co, int appCoroutineID, XMLReader parser );

} // class CoroutineParser
