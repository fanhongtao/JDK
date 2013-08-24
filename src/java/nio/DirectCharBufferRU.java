/*
 * @(#)Direct-X-Buffer.java	1.50 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// -- This file was mechanically generated: Do not edit! -- //

package java.nio;

import sun.misc.Cleaner;
import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;
import sun.nio.ch.FileChannelImpl;


class DirectCharBufferRU



    extends DirectCharBufferU

    implements DirectBuffer
{






















































































































    // For duplicates and slices
    //
    DirectCharBufferRU(DirectBuffer db,	        // package-private
			       int mark, int pos, int lim, int cap,
			       int off)
    {








	super(db, mark, pos, lim, cap, off);

    }

    public CharBuffer slice() {
	int pos = this.position();
	int lim = this.limit();
	assert (pos <= lim);
	int rem = (pos <= lim ? lim - pos : 0);
	int off = (pos << 1);
        assert (off >= 0);
	return new DirectCharBufferRU(this, -1, 0, rem, rem, off);
    }

    public CharBuffer duplicate() {
	return new DirectCharBufferRU(this,
					      this.markValue(),
					      this.position(),
					      this.limit(),
					      this.capacity(),
					      0);
    }

    public CharBuffer asReadOnlyBuffer() {








	return duplicate();

    }


















































    public CharBuffer put(char x) {




	throw new ReadOnlyBufferException();

    }

    public CharBuffer put(int i, char x) {




	throw new ReadOnlyBufferException();

    }

    public CharBuffer put(CharBuffer src) {




































	throw new ReadOnlyBufferException();

    }

    public CharBuffer put(char[] src, int offset, int length) {






















	throw new ReadOnlyBufferException();

    }
    
    public CharBuffer compact() {











	throw new ReadOnlyBufferException();

    }

    public boolean isDirect() {
	return true;
    }

    public boolean isReadOnly() {
	return true;
    }




    public String toString(int start, int end) {
	if ((end > limit()) || (start > end))
	    throw new IndexOutOfBoundsException();
	try {
	    int len = end - start;
	    char[] ca = new char[len];
	    CharBuffer cb = CharBuffer.wrap(ca);
	    CharBuffer db = this.duplicate();
	    db.position(start);
	    db.limit(end);
	    cb.put(db);
	    return new String(ca);
	} catch (StringIndexOutOfBoundsException x) {
	    throw new IndexOutOfBoundsException();
	}
    }


    // --- Methods to support CharSequence ---

    public CharSequence subSequence(int start, int end) {
	int pos = position();
	int lim = limit();
	assert (pos <= lim);
	pos = (pos <= lim ? pos : lim);
	int len = lim - pos;

	if ((start < 0) || (end > len) || (start > end))
	    throw new IndexOutOfBoundsException();
	int sublen = end - start;
 	int off = (pos + start) << 1;
        assert (off >= 0);
	return new DirectCharBufferRU(this, -1, 0, sublen, sublen, off);
    }







    public ByteOrder order() {





	return ((ByteOrder.nativeOrder() != ByteOrder.BIG_ENDIAN)
		? ByteOrder.LITTLE_ENDIAN : ByteOrder.BIG_ENDIAN);

    }


























}
