/*
 * @(#)Direct-X-Buffer.java	1.38 02/03/08
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

// -- This file was mechanically generated: Do not edit! -- //

package java.nio;

import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;
import sun.nio.ch.FileChannelImpl;


class DirectByteBuffer

    extends MappedByteBuffer



    implements DirectBuffer
{



    // Cached unsafe-access object
    protected static final Unsafe unsafe = Bits.unsafe();

    // Cached unaligned-access capability
    protected static final boolean unaligned = Bits.unaligned();

    // Base address, used in all indexing calculations
    // NOTE: moved up to Buffer.java for speed in JNI GetDirectBufferAddress
    //    protected long address;

    // True iff this buffer should be freed
    protected boolean allocated;

    // If this buffer is a view of another buffer then we keep a reference to
    // that buffer so that its memory isn't freed before we're done with it
    protected Object viewedBuffer = null;

    public Object viewedBuffer() {
        return viewedBuffer;
    }





    DirectByteBuffer(int cap) {			// package-private

	super(-1, 0, cap, cap, false);
	int ps = Bits.pageSize();
	long a = unsafe.allocateMemory(cap + ps);
	unsafe.setMemory(a, cap + ps, (byte) 0);
        if ((a % ps) != 0) {
            // Create allocated temporary buffer pointing at beginning of
            // storage for proper cleanup
            DirectByteBuffer tmpBuf =
                new DirectByteBuffer(a, cap + ps, true, false);
            address = a + ps - (a % ps);
            allocated = false;
            viewedBuffer = tmpBuf;
        } else {
            // Memory is already aligned; just use it
            address = a;
            allocated = true;
        }



    }



    private DirectByteBuffer(long addr, int cap, boolean alloc, boolean mapped) {
        super(-1, 0, cap, cap, mapped);
        address = addr;
        allocated = alloc;
    }

    // Invoked only by JNI: NewDirectByteBuffer(void*, long)
    //
    private DirectByteBuffer(long addr, int cap) {
        this(addr, cap, false, false);
    }



    DirectByteBuffer(int cap,			// package-private
			   long addr, int off,
			   boolean mapped)
    {

        super(-1, 0, cap, cap, mapped);
	int ps = Bits.pageSize();
        DirectByteBuffer tmpBuf = null;
        // address must be page aligned
        assert ((addr % ps) == 0);
        tmpBuf = new DirectByteBuffer(addr, cap + off, false, mapped);
        allocated = false;
        address = addr + off;
        viewedBuffer = tmpBuf;




    }
























    DirectByteBuffer(DirectBuffer db,	        // package-private
			       int mark, int pos, int lim, int cap,
			       int off)
    {

	super(mark, pos, lim, cap);
	address = db.address() + off;
	allocated = false;
	viewedBuffer = db;



    }

    public ByteBuffer slice() {
	int pos = this.position();
	int lim = this.limit();
	assert (pos <= lim);
	int rem = (pos <= lim ? lim - pos : 0);
	int off = (pos << 0);
	return new DirectByteBuffer(this, -1, 0, rem, rem, off);
    }

    public ByteBuffer duplicate() {
	return new DirectByteBuffer(this,
					      this.markValue(),
					      this.position(),
					      this.limit(),
					      this.capacity(),
					      0);
    }

    public ByteBuffer asReadOnlyBuffer() {

	return new DirectByteBufferR(this,
					   this.markValue(),
					   this.position(),
					   this.limit(),
					   this.capacity(),
					   0);



    }



    public long address() {
	return address;
    }

    private long ix(int i) {
	return address + (i << 0);
    }

    public byte get() {
	return (unsafe.getByte(ix(nextGetIndex())));
    }

    public byte get(int i) {
	return (unsafe.getByte(ix(checkIndex(i))));
    }

    public ByteBuffer get(byte[] dst, int offset, int length) {

        if ((length << 0) > Bits.JNI_COPY_TO_ARRAY_THRESHOLD) {
            checkBounds(offset, length, dst.length);
            int pos = position();
            int lim = limit();
            assert (pos <= lim);
            int rem = (pos <= lim ? lim - pos : 0);
            if (length > rem)
                throw new BufferUnderflowException();

            if (order() != ByteOrder.nativeOrder())
                Bits.copyToByteArray(ix(pos), dst,
                                       offset << 0,
                                       length << 0);
            else
                Bits.copyToByteArray(ix(pos), dst,
                                     offset << 0,
                                     length << 0);
            position(pos + length);
        } else {
            super.get(dst, offset, length);
        }
        return this;



    }



    public ByteBuffer put(byte x) {

	unsafe.putByte(ix(nextPutIndex()), (x));
	return this;



    }

    public ByteBuffer put(int i, byte x) {

	unsafe.putByte(ix(checkIndex(i)), (x));
	return this;



    }

    public ByteBuffer put(ByteBuffer src) {

	if (src instanceof DirectByteBuffer) {
	    if (src == this)
		throw new IllegalArgumentException();
	    DirectByteBuffer sb = (DirectByteBuffer)src;

	    int spos = sb.position();
	    int slim = sb.limit();
	    assert (spos <= slim);
	    int srem = (spos <= slim ? slim - spos : 0);

	    int pos = position();
	    int lim = limit();
	    assert (pos <= lim);
	    int rem = (pos <= lim ? lim - pos : 0);

	    if (srem > rem)
		throw new BufferOverflowException();
 	    unsafe.copyMemory(sb.ix(spos), ix(pos), srem << 0);
 	    sb.position(spos + srem);
 	    position(pos + srem);
	} else {
	    super.put(src);
	}
	return this;



    }

    public ByteBuffer put(byte[] src, int offset, int length) {

        if ((length << 0) > Bits.JNI_COPY_FROM_ARRAY_THRESHOLD) {
            checkBounds(offset, length, src.length);
            int pos = position();
            int lim = limit();
            assert (pos <= lim);
            int rem = (pos <= lim ? lim - pos : 0);
            if (length > rem)
                throw new BufferOverflowException();

            if (order() != ByteOrder.nativeOrder())
                Bits.copyFromByteArray(src, offset << 0,
                                         ix(pos), length << 0);
            else
                Bits.copyFromByteArray(src, offset << 0,
                                       ix(pos), length << 0);
            position(pos + length);
        } else {
            super.put(src, offset, length);
        }
        return this;



    }

    public ByteBuffer compact() {

	int pos = position();
	int lim = limit();
	assert (pos <= lim);
	int rem = (pos <= lim ? lim - pos : 0);

 	unsafe.copyMemory(ix(pos), ix(0), rem << 0);
 	position(rem);
	limit(capacity());
	return this;



    }

    public boolean isDirect() {
	return true;
    }

    public boolean isReadOnly() {
	return false;
    }




























































    byte _get(int i) {				// package-private
	return unsafe.getByte(address + i);
    }

    void _put(int i, byte b) {			// package-private

	unsafe.putByte(address + i, b);



    }

    protected void finalize() {		
	if (allocated)
	    free();
	else if (isAMappedBuffer && viewedBuffer == null) {
            // Only unmap the root buffer
	    FileChannelImpl.unmap(this);
	    isAMappedBuffer = false;
	}
    }

    synchronized void free() {				// package-private
	if (allocated) {
	    unsafe.freeMemory(this.address);
	    allocated = false;
	}
    }




    private char getChar(long a) {
	if (unaligned) {
	    char x = unsafe.getChar(a);
	    return (nativeByteOrder ? x : Bits.swap(x));
	}
	return Bits.getChar(a, bigEndian);
    }

    public char getChar() {
	return getChar(ix(nextGetIndex((1 << 1))));
    }

    public char getChar(int i) {
	return getChar(ix(checkIndex(i, (1 << 1))));
    }



    private ByteBuffer putChar(long a, char x) {

	if (unaligned)
	    unsafe.putChar(a, nativeByteOrder ? x : Bits.swap(x));
	else
	    Bits.putChar(a, x, bigEndian);
	return this;



    }

    public ByteBuffer putChar(char x) {

	putChar(ix(nextPutIndex((1 << 1))), x);
	return this;



    }

    public ByteBuffer putChar(int i, char x) {

	putChar(ix(checkIndex(i, (1 << 1))), x);
	return this;



    }

    public CharBuffer asCharBuffer() {
	int off = this.position();
	int lim = this.limit();
	assert (off <= lim);
	int rem = (off <= lim ? lim - off : 0);

	int size = rem >> 1;
 	if (!unaligned && ((address + off) % (1 << 1) != 0)) {
	    return (bigEndian
		    ? (CharBuffer)(new ByteBufferAsCharBufferB(this,
								       -1,
								       0,
								       size,
								       size,
								       off))
		    : (CharBuffer)(new ByteBufferAsCharBufferL(this,
								       -1,
								       0,
								       size,
								       size,
								       off)));
	} else {
	    return (nativeByteOrder
		    ? (CharBuffer)(new DirectCharBufferU(this,
								 -1,
								 0,
								 size,
								 size,
								 off))
		    : (CharBuffer)(new DirectCharBufferS(this,
								 -1,
								 0,
								 size,
								 size,
								 off)));
	}
    }




    private short getShort(long a) {
	if (unaligned) {
	    short x = unsafe.getShort(a);
	    return (nativeByteOrder ? x : Bits.swap(x));
	}
	return Bits.getShort(a, bigEndian);
    }

    public short getShort() {
	return getShort(ix(nextGetIndex((1 << 1))));
    }

    public short getShort(int i) {
	return getShort(ix(checkIndex(i, (1 << 1))));
    }



    private ByteBuffer putShort(long a, short x) {

	if (unaligned)
	    unsafe.putShort(a, nativeByteOrder ? x : Bits.swap(x));
	else
	    Bits.putShort(a, x, bigEndian);
	return this;



    }

    public ByteBuffer putShort(short x) {

	putShort(ix(nextPutIndex((1 << 1))), x);
	return this;



    }

    public ByteBuffer putShort(int i, short x) {

	putShort(ix(checkIndex(i, (1 << 1))), x);
	return this;



    }

    public ShortBuffer asShortBuffer() {
	int off = this.position();
	int lim = this.limit();
	assert (off <= lim);
	int rem = (off <= lim ? lim - off : 0);

	int size = rem >> 1;
 	if (!unaligned && ((address + off) % (1 << 1) != 0)) {
	    return (bigEndian
		    ? (ShortBuffer)(new ByteBufferAsShortBufferB(this,
								       -1,
								       0,
								       size,
								       size,
								       off))
		    : (ShortBuffer)(new ByteBufferAsShortBufferL(this,
								       -1,
								       0,
								       size,
								       size,
								       off)));
	} else {
	    return (nativeByteOrder
		    ? (ShortBuffer)(new DirectShortBufferU(this,
								 -1,
								 0,
								 size,
								 size,
								 off))
		    : (ShortBuffer)(new DirectShortBufferS(this,
								 -1,
								 0,
								 size,
								 size,
								 off)));
	}
    }




    private int getInt(long a) {
	if (unaligned) {
	    int x = unsafe.getInt(a);
	    return (nativeByteOrder ? x : Bits.swap(x));
	}
	return Bits.getInt(a, bigEndian);
    }

    public int getInt() {
	return getInt(ix(nextGetIndex((1 << 2))));
    }

    public int getInt(int i) {
	return getInt(ix(checkIndex(i, (1 << 2))));
    }



    private ByteBuffer putInt(long a, int x) {

	if (unaligned)
	    unsafe.putInt(a, nativeByteOrder ? x : Bits.swap(x));
	else
	    Bits.putInt(a, x, bigEndian);
	return this;



    }

    public ByteBuffer putInt(int x) {

	putInt(ix(nextPutIndex((1 << 2))), x);
	return this;



    }

    public ByteBuffer putInt(int i, int x) {

	putInt(ix(checkIndex(i, (1 << 2))), x);
	return this;



    }

    public IntBuffer asIntBuffer() {
	int off = this.position();
	int lim = this.limit();
	assert (off <= lim);
	int rem = (off <= lim ? lim - off : 0);

	int size = rem >> 2;
 	if (!unaligned && ((address + off) % (1 << 2) != 0)) {
	    return (bigEndian
		    ? (IntBuffer)(new ByteBufferAsIntBufferB(this,
								       -1,
								       0,
								       size,
								       size,
								       off))
		    : (IntBuffer)(new ByteBufferAsIntBufferL(this,
								       -1,
								       0,
								       size,
								       size,
								       off)));
	} else {
	    return (nativeByteOrder
		    ? (IntBuffer)(new DirectIntBufferU(this,
								 -1,
								 0,
								 size,
								 size,
								 off))
		    : (IntBuffer)(new DirectIntBufferS(this,
								 -1,
								 0,
								 size,
								 size,
								 off)));
	}
    }




    private long getLong(long a) {
	if (unaligned) {
	    long x = unsafe.getLong(a);
	    return (nativeByteOrder ? x : Bits.swap(x));
	}
	return Bits.getLong(a, bigEndian);
    }

    public long getLong() {
	return getLong(ix(nextGetIndex((1 << 3))));
    }

    public long getLong(int i) {
	return getLong(ix(checkIndex(i, (1 << 3))));
    }



    private ByteBuffer putLong(long a, long x) {

	if (unaligned)
	    unsafe.putLong(a, nativeByteOrder ? x : Bits.swap(x));
	else
	    Bits.putLong(a, x, bigEndian);
	return this;



    }

    public ByteBuffer putLong(long x) {

	putLong(ix(nextPutIndex((1 << 3))), x);
	return this;



    }

    public ByteBuffer putLong(int i, long x) {

	putLong(ix(checkIndex(i, (1 << 3))), x);
	return this;



    }

    public LongBuffer asLongBuffer() {
	int off = this.position();
	int lim = this.limit();
	assert (off <= lim);
	int rem = (off <= lim ? lim - off : 0);

	int size = rem >> 3;
 	if (!unaligned && ((address + off) % (1 << 3) != 0)) {
	    return (bigEndian
		    ? (LongBuffer)(new ByteBufferAsLongBufferB(this,
								       -1,
								       0,
								       size,
								       size,
								       off))
		    : (LongBuffer)(new ByteBufferAsLongBufferL(this,
								       -1,
								       0,
								       size,
								       size,
								       off)));
	} else {
	    return (nativeByteOrder
		    ? (LongBuffer)(new DirectLongBufferU(this,
								 -1,
								 0,
								 size,
								 size,
								 off))
		    : (LongBuffer)(new DirectLongBufferS(this,
								 -1,
								 0,
								 size,
								 size,
								 off)));
	}
    }




    private float getFloat(long a) {
	if (unaligned) {
	    float x = unsafe.getFloat(a);
	    return (nativeByteOrder ? x : Bits.swap(x));
	}
	return Bits.getFloat(a, bigEndian);
    }

    public float getFloat() {
	return getFloat(ix(nextGetIndex((1 << 2))));
    }

    public float getFloat(int i) {
	return getFloat(ix(checkIndex(i, (1 << 2))));
    }



    private ByteBuffer putFloat(long a, float x) {

	if (unaligned)
	    unsafe.putFloat(a, nativeByteOrder ? x : Bits.swap(x));
	else
	    Bits.putFloat(a, x, bigEndian);
	return this;



    }

    public ByteBuffer putFloat(float x) {

	putFloat(ix(nextPutIndex((1 << 2))), x);
	return this;



    }

    public ByteBuffer putFloat(int i, float x) {

	putFloat(ix(checkIndex(i, (1 << 2))), x);
	return this;



    }

    public FloatBuffer asFloatBuffer() {
	int off = this.position();
	int lim = this.limit();
	assert (off <= lim);
	int rem = (off <= lim ? lim - off : 0);

	int size = rem >> 2;
 	if (!unaligned && ((address + off) % (1 << 2) != 0)) {
	    return (bigEndian
		    ? (FloatBuffer)(new ByteBufferAsFloatBufferB(this,
								       -1,
								       0,
								       size,
								       size,
								       off))
		    : (FloatBuffer)(new ByteBufferAsFloatBufferL(this,
								       -1,
								       0,
								       size,
								       size,
								       off)));
	} else {
	    return (nativeByteOrder
		    ? (FloatBuffer)(new DirectFloatBufferU(this,
								 -1,
								 0,
								 size,
								 size,
								 off))
		    : (FloatBuffer)(new DirectFloatBufferS(this,
								 -1,
								 0,
								 size,
								 size,
								 off)));
	}
    }




    private double getDouble(long a) {
	if (unaligned) {
	    double x = unsafe.getDouble(a);
	    return (nativeByteOrder ? x : Bits.swap(x));
	}
	return Bits.getDouble(a, bigEndian);
    }

    public double getDouble() {
	return getDouble(ix(nextGetIndex((1 << 3))));
    }

    public double getDouble(int i) {
	return getDouble(ix(checkIndex(i, (1 << 3))));
    }



    private ByteBuffer putDouble(long a, double x) {

	if (unaligned)
	    unsafe.putDouble(a, nativeByteOrder ? x : Bits.swap(x));
	else
	    Bits.putDouble(a, x, bigEndian);
	return this;



    }

    public ByteBuffer putDouble(double x) {

	putDouble(ix(nextPutIndex((1 << 3))), x);
	return this;



    }

    public ByteBuffer putDouble(int i, double x) {

	putDouble(ix(checkIndex(i, (1 << 3))), x);
	return this;



    }

    public DoubleBuffer asDoubleBuffer() {
	int off = this.position();
	int lim = this.limit();
	assert (off <= lim);
	int rem = (off <= lim ? lim - off : 0);

	int size = rem >> 3;
 	if (!unaligned && ((address + off) % (1 << 3) != 0)) {
	    return (bigEndian
		    ? (DoubleBuffer)(new ByteBufferAsDoubleBufferB(this,
								       -1,
								       0,
								       size,
								       size,
								       off))
		    : (DoubleBuffer)(new ByteBufferAsDoubleBufferL(this,
								       -1,
								       0,
								       size,
								       size,
								       off)));
	} else {
	    return (nativeByteOrder
		    ? (DoubleBuffer)(new DirectDoubleBufferU(this,
								 -1,
								 0,
								 size,
								 size,
								 off))
		    : (DoubleBuffer)(new DirectDoubleBufferS(this,
								 -1,
								 0,
								 size,
								 size,
								 off)));
	}
    }

}
