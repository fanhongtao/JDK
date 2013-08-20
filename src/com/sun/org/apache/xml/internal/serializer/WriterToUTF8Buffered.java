/*
 * Copyright 1999-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id: WriterToUTF8Buffered.java,v 1.5 2004/02/17 04:18:18 minchau Exp $
 */
package com.sun.org.apache.xml.internal.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;


/**
 * This class writes unicode characters to a byte stream (java.io.OutputStream)
 * as quickly as possible. It buffers the output in an internal
 * buffer which must be flushed to the OutputStream when done. This flushing
 * is done via the close() flush() or flushBuffer() method. 
 */
public final class WriterToUTF8Buffered extends Writer
{
    
  /** number of bytes that the byte buffer can hold.
   * This is a fixed constant is used rather than m_outputBytes.lenght for performance.
   */
  private static final int BYTES_MAX=16*1024;
  
  /** number of characters that the character buffer can hold.
   * This is 1/3 of the number of bytes because UTF-8 encoding
   * can expand one unicode character by up to 3 bytes.
   */
  private static final int CHARS_MAX=(BYTES_MAX/3);
  
 // private static final int 
  
  /** The byte stream to write to. (sc & sb remove final to compile in JDK 1.1.8) */
  private final OutputStream m_os;

  /**
   * The internal buffer where data is stored.
   * (sc & sb remove final to compile in JDK 1.1.8)
   */
  private final byte m_outputBytes[];
  
  private final char m_inputChars[];

  /**
   * The number of valid bytes in the buffer. This value is always
   * in the range <tt>0</tt> through <tt>m_outputBytes.length</tt>; elements
   * <tt>m_outputBytes[0]</tt> through <tt>m_outputBytes[count-1]</tt> contain valid
   * byte data.
   */
  private int count;

  /**
   * Create an buffered UTF-8 writer.
   *
   *
   * @param   out    the underlying output stream.
   *
   * @throws UnsupportedEncodingException
   */
  public WriterToUTF8Buffered(OutputStream out)
          throws UnsupportedEncodingException
  {
      m_os = out;
      // get 3 extra bytes to make buffer overflow checking simpler and faster
      // we won't have to keep checking for a few extra characters
      m_outputBytes = new byte[BYTES_MAX + 3];
      
      // Big enough to hold the input chars that will be transformed
      // into output bytes in m_ouputBytes.
      m_inputChars = new char[CHARS_MAX + 1];
      count = 0;
      
//      the old body of this constructor, before the buffersize was changed to a constant      
//      this(out, 8*1024);
  }

  /**
   * Create an buffered UTF-8 writer to write data to the
   * specified underlying output stream with the specified buffer
   * size.
   *
   * @param   out    the underlying output stream.
   * @param   size   the buffer size.
   * @exception IllegalArgumentException if size <= 0.
   */
//  public WriterToUTF8Buffered(final OutputStream out, final int size)
//  {
//
//    m_os = out;
//
//    if (size <= 0)
//    {
//      throw new IllegalArgumentException(
//        SerializerMessages.createMessage(SerializerErrorResources.ER_BUFFER_SIZE_LESSTHAN_ZERO, null)); //"Buffer size <= 0");
//    }
//
//    m_outputBytes = new byte[size];
//    count = 0;
//  }

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
    
    /* If we are close to the end of the buffer then flush it.
     * Remember the buffer can hold a few more bytes than BYTES_MAX
     */ 
    if (count >= BYTES_MAX)
        flushBuffer();

    if (c < 0x80)
    {
       m_outputBytes[count++] = (byte) (c);
    }
    else if (c < 0x800)
    {
      m_outputBytes[count++] = (byte) (0xc0 + (c >> 6));
      m_outputBytes[count++] = (byte) (0x80 + (c & 0x3f));
    }
    else
    {
      m_outputBytes[count++] = (byte) (0xe0 + (c >> 12));
      m_outputBytes[count++] = (byte) (0x80 + ((c >> 6) & 0x3f));
      m_outputBytes[count++] = (byte) (0x80 + (c & 0x3f));
    }
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

    // We multiply the length by three since this is the maximum length
    // of the characters that we can put into the buffer.  It is possible
    // for each Unicode character to expand to three bytes.

    int lengthx3 = 3*length;

    if (lengthx3 >= BYTES_MAX - count)
    {
      // The requested length is greater than the unused part of the buffer
      flushBuffer();

      if (lengthx3 >= BYTES_MAX)
      {
        /*
         * The requested length exceeds the size of the buffer.
         * Cut the buffer up into chunks, each of which will
         * not cause an overflow to the output buffer m_outputBytes,
         * and make multiple recursive calls.
         * Be careful about integer overflows in multiplication.
         */
        final int chunks = 1 + length/CHARS_MAX;
        int end_chunk = start;
        for (int chunk = 1; chunk <= chunks; chunk++)
        {
            int start_chunk = end_chunk;
            end_chunk = start + (int) ((((long) length) * chunk) / chunks);
            int len_chunk = (end_chunk - start_chunk);
            this.write(chars,start_chunk, len_chunk);
        }
        return;
      }
    }



    final int n = length+start;
    final byte[] buf_loc = m_outputBytes; // local reference for faster access
    int count_loc = count;      // local integer for faster access
    int i = start;
    {
        /* This block could be omitted and the code would produce
         * the same result. But this block exists to give the JIT
         * a better chance of optimizing a tight and common loop which
         * occurs when writing out ASCII characters. 
         */ 
        char c;
        for(; i < n && (c = chars[i])< 0x80 ; i++ )
            buf_loc[count_loc++] = (byte)c;
    }
    for (; i < n; i++)
    {

      final char c = chars[i];

      if (c < 0x80)
        buf_loc[count_loc++] = (byte) (c);
      else if (c < 0x800)
      {
        buf_loc[count_loc++] = (byte) (0xc0 + (c >> 6));
        buf_loc[count_loc++] = (byte) (0x80 + (c & 0x3f));
      }
      else
      {
        buf_loc[count_loc++] = (byte) (0xe0 + (c >> 12));
        buf_loc[count_loc++] = (byte) (0x80 + ((c >> 6) & 0x3f));
        buf_loc[count_loc++] = (byte) (0x80 + (c & 0x3f));
      }
    }
    // Store the local integer back into the instance variable
    count = count_loc;

  }
  
  /**
   * Writes out the character array 
   * @param chars a character array with only ASCII characters, so
   * the UTF-8 encoding is optimized.
   * @param start the first character in the input array
   * @param length the number of characters in the input array
   */
  private void directWrite(final char chars[], final int start, final int length)
          throws java.io.IOException
  {



    if (length >= BYTES_MAX - count)
    {
      // The requested length is greater than the unused part of the buffer
      flushBuffer();

      if (length >= BYTES_MAX)
      {
        /*
         * The requested length exceeds the size of the buffer.
         * Cut the buffer up into chunks, each of which will
         * not cause an overflow to the output buffer m_outputBytes,
         * and make multiple recursive calls.
         */          
        int chunks = 1 + length/CHARS_MAX;
        for (int chunk =0 ; chunk < chunks; chunk++)
        {
            int start_chunk = start + ((length*chunk)/chunks);
            int end_chunk   = start + ((length*(chunk+1))/chunks);
            int len_chunk = (end_chunk - start_chunk);
            this.directWrite(chars,start_chunk, len_chunk);
        }
        return;
      }
    }

    final int n = length+start;
    final byte[] buf_loc = m_outputBytes; // local reference for faster access
    int count_loc = count;      // local integer for faster access
    for(int i=start; i < n ; i++ )
        buf_loc[count_loc++] = (byte) buf_loc[i];
    // Store the local integer back into the instance variable
    count = count_loc;
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

    // We multiply the length by three since this is the maximum length
    // of the characters that we can put into the buffer.  It is possible
    // for each Unicode character to expand to three bytes.
    final int length = s.length();
    int lengthx3 = 3*length;

    if (lengthx3 >= BYTES_MAX - count)
    {
      // The requested length is greater than the unused part of the buffer
      flushBuffer();

      if (lengthx3 >= BYTES_MAX)
      {
        /*
         * The requested length exceeds the size of the buffer,
         * so break it up in chunks that don't exceed the buffer size.
         */
         final int start = 0;
         int chunks = 1 + length/CHARS_MAX;
         for (int chunk =0 ; chunk < chunks; chunk++)
         {
             int start_chunk = start + ((length*chunk)/chunks);
             int end_chunk   = start + ((length*(chunk+1))/chunks);
             int len_chunk = (end_chunk - start_chunk);
             s.getChars(start_chunk,end_chunk, m_inputChars,0);
             this.write(m_inputChars,0, len_chunk);
         }
         return;
      }
    }


    s.getChars(0, length , m_inputChars, 0);
    final char[] chars = m_inputChars;
    final int n = length;
    final byte[] buf_loc = m_outputBytes; // local reference for faster access
    int count_loc = count;      // local integer for faster access
    int i = 0;
    {
        /* This block could be omitted and the code would produce
         * the same result. But this block exists to give the JIT
         * a better chance of optimizing a tight and common loop which
         * occurs when writing out ASCII characters. 
         */ 
        char c;
        for(; i < n && (c = chars[i])< 0x80 ; i++ )
            buf_loc[count_loc++] = (byte)c;
    }
    for (; i < n; i++)
    {

      final char c = chars[i];

      if (c < 0x80)
        buf_loc[count_loc++] = (byte) (c);
      else if (c < 0x800)
      {
        buf_loc[count_loc++] = (byte) (0xc0 + (c >> 6));
        buf_loc[count_loc++] = (byte) (0x80 + (c & 0x3f));
      }
      else
      {
        buf_loc[count_loc++] = (byte) (0xe0 + (c >> 12));
        buf_loc[count_loc++] = (byte) (0x80 + ((c >> 6) & 0x3f));
        buf_loc[count_loc++] = (byte) (0x80 + (c & 0x3f));
      }
    }
    // Store the local integer back into the instance variable
    count = count_loc;

  }

  /**
   * Flush the internal buffer
   *
   * @throws IOException
   */
  public void flushBuffer() throws IOException
  {

    if (count > 0)
    {
      m_os.write(m_outputBytes, 0, count);

      count = 0;
    }
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
    flushBuffer();
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
    flushBuffer();
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
  
  /**
   * 
   * @param s A string with only ASCII characters
   * @throws IOException
   */
  public void directWrite(final String s) throws IOException
  {

    final int length = s.length();
    
    if (length >= BYTES_MAX - count)
    {
      // The requested length is greater than the unused part of the buffer
      flushBuffer();

      if (length >= BYTES_MAX)
      {
        /*
         * The requested length exceeds the size of the buffer,
         * so don't bother to buffer this one, just write it out
         * directly. The buffer is already flushed so this is a 
         * safe thing to do.
         */
         final int start = 0;
         int chunks = 1 + length/CHARS_MAX;
         for (int chunk =0 ; chunk < chunks; chunk++)
         {
             int start_chunk = start + ((length*chunk)/chunks);
             int end_chunk   = start + ((length*(chunk+1))/chunks);
             int len_chunk = (end_chunk - start_chunk);
             s.getChars(start_chunk,end_chunk, m_inputChars,0);
             this.directWrite(m_inputChars,0, len_chunk);
         }
        return;
      }
    }


    s.getChars(0, length , m_inputChars, 0);
    final char[] chars = m_inputChars;
    final byte[] buf_loc = m_outputBytes; // local reference for faster access
    int count_loc = count;      // local integer for faster access
    int i = 0;
    while( i < length) 
        buf_loc[count_loc++] = (byte)chars[i++];

 
    // Store the local integer back into the instance variable
    count = count_loc;

  }
}
