/*
 * The Apache Software License, Version 1.1
 *
 *
 * Copyright (c) 1999-2003 The Apache Software Foundation.  All rights 
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
package com.sun.org.apache.xml.internal.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

/**
 * This class writes ASCII to a byte stream as quickly as possible.  For the
 * moment it does not do buffering, though I reserve the right to do some
 * buffering down the line if I can prove that it will be faster even if the
 * output stream is buffered.
 */
public class WriterToUTF8 extends Writer
{

    /** A flag to force flush output to System.out when stepping with a debugger (debug only!) */
    private static final boolean DEBUG_OUT = false;

    /** The byte stream to write to. */
    private final OutputStream m_os;

    /**
     * Create an unbuffered UTF-8 writer.
     *
     *
     * @param os The byte stream to write to.
     *
     * @throws UnsupportedEncodingException
     */
    public WriterToUTF8(OutputStream os) throws UnsupportedEncodingException
    {
        m_os = os;
    }

    /**
     * Write a single character.  The character to be written is contained in
     * the 16 low-order bits of the given integer value; the 16 high-order bits
     * are ignored.
     *
     * <p> Subclasses that intend to support efficient single-character output
     * should override this method.
     *
     * @param c  int specifying a character to be written.
     * @exception  IOException  If an I/O error occurs
     */
    public void write(final int c) throws IOException
    {

        if (c < 0x80)
            m_os.write(c);
        else if (c < 0x800)
        {
            m_os.write(0xc0 + (c >> 6));
            m_os.write(0x80 + (c & 0x3f));
        }
        else
        {
            m_os.write(0xe0 + (c >> 12));
            m_os.write(0x80 + ((c >> 6) & 0x3f));
            m_os.write(0x80 + (c & 0x3f));
        }

        if (DEBUG_OUT)
        {
            if (c < 0x80)
            {
                char ch = (char) c;
                System.out.print(ch);
            }
            else if (c < 0x800)
            {
                System.out.print(0xc0 + (c >> 6));
                System.out.print(0x80 + (c & 0x3f));
            }
            else
            {
                System.out.print(0xe0 + (c >> 12));
                System.out.print(0x80 + ((c >> 6) & 0x3f));
                System.out.print(0x80 + (c & 0x3f));
            }
            System.out.flush();
        }
        return;
    }

    /**
     * Write a portion of an array of characters.
     *
     * @param  chars  Array of characters
     * @param  start   Offset from which to start writing characters
     * @param  length   Number of characters to write
     *
     * @exception  IOException  If an I/O error occurs
     *
     * @throws java.io.IOException
     */
    public void write(final char chars[], final int start, final int length)
        throws java.io.IOException
    {

        final OutputStream os = m_os;

        int n = length + start;
        for (int i = start; i < n; i++)
        {
            final char c = chars[i];

            if (c < 0x80)
                os.write(c);
            else if (c < 0x800)
            {
                os.write(0xc0 + (c >> 6));
                os.write(0x80 + (c & 0x3f));
            }
            else
            {
                os.write(0xe0 + (c >> 12));
                os.write(0x80 + ((c >> 6) & 0x3f));
                os.write(0x80 + (c & 0x3f));
            }
        }

        if (DEBUG_OUT)
        {
            for (int i = start; i < n; i++)
            {
                final char c = chars[i];

                if (c < 0x80)
                    System.out.print(c);
                else if (c < 0x800)
                {
                    System.out.print(0xc0 + (c >> 6));
                    System.out.print(0x80 + (c & 0x3f));
                }
                else
                {
                    System.out.print(0xe0 + (c >> 12));
                    System.out.print(0x80 + ((c >> 6) & 0x3f));
                    System.out.print(0x80 + (c & 0x3f));
                }
            }
            System.out.flush();
        }
        return;
    }

    /**
     * Write a string.
     *
     * @param  s  String to be written
     *
     * @exception  IOException  If an I/O error occurs
     */
    public void write(final String s) throws IOException
    {

        final int n = s.length();
        final OutputStream os = m_os;

        for (int i = 0; i < n; i++)
        {
            final char c = s.charAt(i);

            if (c < 0x80)
                os.write(c);
            else if (c < 0x800)
            {
                os.write(0xc0 + (c >> 6));
                os.write(0x80 + (c & 0x3f));
            }
            else
            {
                os.write(0xe0 + (c >> 12));
                os.write(0x80 + ((c >> 6) & 0x3f));
                os.write(0x80 + (c & 0x3f));
            }
        }

        if (DEBUG_OUT)
        {

            for (int i = 0; i < n; i++)
            {
                final char c = s.charAt(i);

                if (c < 0x80)
                    System.out.print(c);
                else if (c < 0x800)
                {
                    System.out.print(0xc0 + (c >> 6));
                    System.out.print(0x80 + (c & 0x3f));
                }
                else
                {
                    System.out.print(0xe0 + (c >> 12));
                    System.out.print(0x80 + ((c >> 6) & 0x3f));
                    System.out.print(0x80 + (c & 0x3f));
                }
            }
            System.out.flush();
        }
        return;
    }

    /**
     * Flush the stream.  If the stream has saved any characters from the
     * various write() methods in a buffer, write them immediately to their
     * intended destination.  Then, if that destination is another character or
     * byte stream, flush it.  Thus one flush() invocation will flush all the
     * buffers in a chain of Writers and OutputStreams.
     *
     * @exception  IOException  If an I/O error occurs
     *
     * @throws java.io.IOException
     */
    public void flush() throws java.io.IOException
    {
        m_os.flush();
    }

    /**
     * Close the stream, flushing it first.  Once a stream has been closed,
     * further write() or flush() invocations will cause an IOException to be
     * thrown.  Closing a previously-closed stream, however, has no effect.
     *
     * @exception  IOException  If an I/O error occurs
     *
     * @throws java.io.IOException
     */
    public void close() throws java.io.IOException
    {
        m_os.close();
    }

    /**
     * Get the output stream where the events will be serialized to.
     *
     * @return reference to the result stream, or null of only a writer was
     * set.
     */
    public OutputStream getOutputStream()
    {
        return m_os;
    }
}
