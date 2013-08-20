/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 2000-2002 The Apache Software Foundation.  All rights 
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

package com.sun.org.apache.xerces.internal.xni;

/**
 * This class is used as a structure to pass text contained in the underlying
 * character buffer of the scanner. The offset and length fields allow the
 * buffer to be re-used without creating new character arrays.
 * <p>
 * <strong>Note:</strong> Methods that are passed an XMLString structure
 * should consider the contents read-only and not make any modifications
 * to the contents of the buffer. The method receiving this structure
 * should also not modify the offset and length if this structure (or
 * the values of this structure) are passed to another method.
 * <p>
 * <strong>Note:</strong> Methods that are passed an XMLString structure
 * are required to copy the information out of the buffer if it is to be
 * saved for use beyond the scope of the method. The contents of the 
 * structure are volatile and the contents of the character buffer cannot
 * be assured once the method that is passed this structure returns.
 * Therefore, methods passed this structure should not save any reference
 * to the structure or the character array contained in the structure.
 *
 * @author Eric Ye, IBM
 * @author Andy Clark, IBM
 *
 * @version $Id: XMLString.java,v 1.4 2002/01/29 01:15:19 lehors Exp $
 */
public class XMLString {

    //
    // Data
    //

    /** The character array. */
    public char[] ch;

    /** The offset into the character array. */
    public int offset;

    /** The length of characters from the offset. */
    public int length;

    //
    // Constructors
    //

    /** Default constructor. */
    public XMLString() {
    } // <init>()

    /**
     * Constructs an XMLString structure preset with the specified
     * values.
     * 
     * @param ch     The character array.
     * @param offset The offset into the character array.
     * @param length The length of characters from the offset.
     */
    public XMLString(char[] ch, int offset, int length) {
        setValues(ch, offset, length);
    } // <init>(char[],int,int)

    /**
     * Constructs an XMLString structure with copies of the values in
     * the given structure.
     * <p>
     * <strong>Note:</strong> This does not copy the character array;
     * only the reference to the array is copied.
     *
     * @param string The XMLString to copy.
     */
    public XMLString(XMLString string) {
        setValues(string);
    } // <init>(XMLString)

    //
    // Public methods
    //

    /**
     * Initializes the contents of the XMLString structure with the
     * specified values.
     * 
     * @param ch     The character array.
     * @param offset The offset into the character array.
     * @param length The length of characters from the offset.
     */
    public void setValues(char[] ch, int offset, int length) {
        this.ch = ch;
        this.offset = offset;
        this.length = length;
    } // setValues(char[],int,int)

    /**
     * Initializes the contents of the XMLString structure with copies
     * of the given string structure.
     * <p>
     * <strong>Note:</strong> This does not copy the character array;
     * only the reference to the array is copied.
     * 
     * @param s
     */
    public void setValues(XMLString s) {
        setValues(s.ch, s.offset, s.length);
    } // setValues(XMLString)

    /** Resets all of the values to their defaults. */
    public void clear() {
        this.ch = null;
        this.offset = 0;
        this.length = -1;
    } // clear()

    /**
     * Returns true if the contents of this XMLString structure and
     * the specified array are equal.
     * 
     * @param ch     The character array.
     * @param offset The offset into the character array.
     * @param length The length of characters from the offset.
     */
    public boolean equals(char[] ch, int offset, int length) {
        if (ch == null) {
            return false;
        }
        if (this.length != length) {
            return false;
        }

        for (int i=0; i<length; i++) {
            if (this.ch[this.offset+i] != ch[offset+i] ) {
                return false;
            }
        }
        return true;
    } // equals(char[],int,int):boolean

    /**
     * Returns true if the contents of this XMLString structure and
     * the specified string are equal.
     * 
     * @param s The string to compare.
     */
    public boolean equals(String s) {
        if (s == null) {
            return false;
        }
        if ( length != s.length() ) {
            return false;
        }

        // is this faster than call s.toCharArray first and compare the 
        // two arrays directly, which will possibly involve creating a
        // new char array object.
        for (int i=0; i<length; i++) {
            if (ch[offset+i] != s.charAt(i)) {
                return false;
            }
        }

        return true;
    } // equals(String):boolean

    //
    // Object methods
    //

    /** Returns a string representation of this object. */
    public String toString() {
        return length > 0 ? new String(ch, offset, length) : "";
    } // toString():String

} // class XMLString
