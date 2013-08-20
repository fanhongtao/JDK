/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2001, 2002 The Apache Software Foundation.
 * All rights reserved.
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

package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.DOMLocator;
import org.w3c.dom.Node;


/**
 * <code>DOMLocatorImpl</code> is an implementaion that describes a location (e.g. 
 * where an error occured).
 * <p>See also the <a href='http://www.w3.org/TR/2001/WD-DOM-Level-3-Core-20010913'>Document Object Model (DOM) Level 3 Core Specification</a>.
 *
 * @author Gopal Sharma, SUN Microsystems Inc.
 * @version $Id: DOMLocatorImpl.java,v 1.8 2003/11/13 22:47:15 elena Exp $
 */
 
public class DOMLocatorImpl implements DOMLocator {

    //
    // Data
    //

   /**
    * The column number where the error occured, 
    * or -1 if there is no column number available.
    */
   public int fColumnNumber = -1;

   /**
    * The line number where the error occured, 
    * or -1 if there is no line number available.
    */
   public int fLineNumber = -1;

   /** related data node*/
   public Node fRelatedNode = null;

   /**
    * The URI where the error occured, 
    * or null if there is no URI available.
    */
   public String fUri = null;

   /**
    * The byte offset into the input source this locator is pointing to or -1 
    * if there is no byte offset available
    */
   public int fByteOffset = -1;
   
   /**
    * The UTF-16, as defined in [Unicode] and Amendment 1 of [ISO/IEC 10646], 
    * offset into the input source this locator is pointing to or -1 if there 
    * is no UTF-16 offset available.
    */
   public int fUtf16Offset = -1;
           
   //
   // Constructors
   //

   public DOMLocatorImpl(){
   }

   public DOMLocatorImpl (int lineNumber, int columnNumber, String uri ){
	fLineNumber = lineNumber ;
	fColumnNumber = columnNumber ;
	fUri = uri;
   } // DOMLocatorImpl (int lineNumber, int columnNumber, String uri )

   public DOMLocatorImpl (int lineNumber, int columnNumber, int byteoffset, Node relatedData, String uri ){
	fLineNumber = lineNumber ;
	fColumnNumber = columnNumber ;
	fByteOffset = byteoffset ;
	fRelatedNode = relatedData ;
	fUri = uri;
   } // DOMLocatorImpl (int lineNumber, int columnNumber, int offset, Node errorNode, String uri )

   public DOMLocatorImpl (int lineNumber, int columnNumber, int byteoffset, Node relatedData, String uri, int utf16Offsert ){
	fLineNumber = lineNumber ;
	fColumnNumber = columnNumber ;
	fByteOffset = byteoffset ;
	fRelatedNode = relatedData ;
	fUri = uri;
	fUtf16Offset = utf16Offsert;
   } // DOMLocatorImpl (int lineNumber, int columnNumber, int offset, Node errorNode, String uri )


  /**
   * The line number where the error occured, or -1 if there is no line 
   * number available.
   */
   public int getLineNumber(){
 	return fLineNumber;
   }

  /**
   * The column number where the error occured, or -1 if there is no column 
   * number available.
   */
  public int getColumnNumber(){
	return fColumnNumber;
  }


  /**
   * The URI where the error occured, or null if there is no URI available.
   */
  public String getUri(){
	return fUri;
  }


  public Node getRelatedNode(){
    return fRelatedNode;
  }
  

  /**
   * The byte offset into the input source this locator is pointing to or -1 
   * if there is no byte offset available
   */
  public int getByteOffset(){
	return fByteOffset;
  }

  /**
   * The UTF-16, as defined in [Unicode] and Amendment 1 of [ISO/IEC 10646], 
   * offset into the input source this locator is pointing to or -1 if there 
   * is no UTF-16 offset available.
   */
  public int getUtf16Offset(){
	return fUtf16Offset;
  }

}// class DOMLocatorImpl
