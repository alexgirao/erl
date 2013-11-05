
/* implemented after reverse engineering from DefaultErlTermEncoder.java and
 * other references
 *
 * reference: otp_src_R15B03/lib/jinterface/java_src/com/ericsson/otp/erlang/OtpInputStream.java
 * reference: otp_src_R15B03/lib/erl_interface/src/decode/decode_*.c
 */

package erl.impl;

import erl.*;

import static erl.ErlTerm.*;

import java.nio.ByteBuffer;
import java.math.BigInteger;

/**
 * Default implementation of ErlTermDecoder
 */
public class DefaultErlTermDecoder implements ErlTermDecoder
{
    private static int readUnsignedByte(ByteBuffer buf) {
	return ((int)buf.get()) & 0xff;
    }
    private static int peekUnsignedByte(ByteBuffer buf, int index) {
	return ((int)buf.get(index)) & 0xff;
    }
    private static int readUnsignedShort(ByteBuffer buf) {
	return ((int)buf.getShort()) & 0xffff;
    }
    private ErlTerm decode0(ByteBuffer buf) {
	final int tag = readUnsignedByte(buf);
	long arity, v, mag;
	int size;
	boolean sign;
	ErlTerm terms[];
	byte bytes[];

	//System.out.format("********** buf.position()=%d, decoding tag=%d (0x%x)\n", buf.position(), tag, tag);

	switch (tag) {
        case ERL_SMALL_INTEGER_EXT:
	    /* all small integers are unsigned ranging from 0 to 255
	     */
	    return new ErlIntegerImpl(readUnsignedByte(buf));
        case ERL_INTEGER_EXT:
	    return new ErlIntegerImpl(buf.getInt());
        case ERL_FLOAT_EXT:
	    throw new RuntimeException("float not IEEE-754, please use {minor_version, 1} in term_to_binary/2");
        case NEW_FLOAT_EXT:
	    return new ErlFloatImpl(buf.getDouble());
        case ERL_ATOM_EXT:
	    bytes = new byte[readUnsignedShort(buf)];
	    buf.get(bytes);
	    return new ErlAtomImpl(bytes, false /* copy? */);
        case ERL_REFERENCE_EXT:
        case ERL_NEW_REFERENCE_EXT:
        case ERL_PORT_EXT:
        case ERL_PID_EXT:
	    throw new RuntimeException("not implemented");
        case ERL_SMALL_TUPLE_EXT:
	    arity = readUnsignedByte(buf);
	    terms = new ErlTerm[(int)arity];
	    for (int i=0; i<arity; i++) {
		terms[i] = decode0(buf);
	    }
	    return new ErlTupleImpl(terms);
        case ERL_LARGE_TUPLE_EXT:
	    arity = buf.getInt();
	    if (arity < 0) {
		throw new IllegalArgumentException("tuple arity greater than 31-bit");
	    }
	    terms = new ErlTerm[(int)arity];
	    for (int i=0; i<arity; i++) {
		terms[i] = decode0(buf);
	    }
	    return new ErlTupleImpl(terms);
        case ERL_LIST_EXT:
	    arity = buf.getInt();
	    if (arity < 0) {
		throw new IllegalArgumentException("list arity greater than 31-bit");
	    }
	    terms = new ErlTerm[(int)arity];
	    for (int i=0; i<arity; i++) {
		terms[i] = decode0(buf);
	    }
	    return new ErlListTermsImpl(decode0(buf) /* lastTail */, terms);
        case ERL_NIL_EXT:
	    return null;
        case ERL_LARGE_BIG_EXT:
        case ERL_SMALL_BIG_EXT:
	    if (tag == ERL_LARGE_BIG_EXT) {
		arity = buf.getInt();
		if (arity < 0) {
		    throw new IllegalArgumentException("integer arity greater than 31-bit");
		}
		sign = readUnsignedByte(buf) != 0;
	    } else {
		arity = readUnsignedByte(buf);
		sign = readUnsignedByte(buf) != 0;
	    }

	    if (arity <= 8) {
		final byte b[] = new byte[8];
		buf.get(b, 0, (int)arity);

		switch ((int)arity) {
		case 4:
		    v = ((b[3] & 0xffL) << 24) | ((b[2] & 0xffL) << 16) |
			((b[1] & 0xffL) <<  8) |  (b[0] & 0xffL);
		    break;
		case 5:
		    v = ((b[4] & 0xffL) << 32) |
			((b[3] & 0xffL) << 24) | ((b[2] & 0xffL) << 16) |
			((b[1] & 0xffL) <<  8) |  (b[0] & 0xffL);
		    break;
		case 6:
		    v = ((b[5] & 0xffL) << 40) | ((b[4] & 0xffL) << 32) |
			((b[3] & 0xffL) << 24) | ((b[2] & 0xffL) << 16) |
			((b[1] & 0xffL) <<  8) |  (b[0] & 0xffL);
		    break;
		case 7:
		    v = ((b[6] & 0xffL) << 48) |
			((b[5] & 0xffL) << 40) | ((b[4] & 0xffL) << 32) |
			((b[3] & 0xffL) << 24) | ((b[2] & 0xffL) << 16) |
			((b[1] & 0xffL) <<  8) |  (b[0] & 0xffL);
		    break;
		case 8:
		    v = ((b[7] & 0xffL) << 56) | ((b[6] & 0xffL) << 48) |
			((b[5] & 0xffL) << 40) | ((b[4] & 0xffL) << 32) |
			((b[3] & 0xffL) << 24) | ((b[2] & 0xffL) << 16) |
			((b[1] & 0xffL) <<  8) |  (b[0] & 0xffL);
		    if (v < 0) {
			/* 64th bit set, absolute value too large, may need a BigInteger */
			if (sign && v == 0x8000000000000000L) {
			    /* 64-bit two's complement can represent only one absolute value in the 64-bit
			     * range, because the negative range of -(1L) to -(1L + (~0L >>> 1)) indeed reach
			     * the first 64th bit absolute value (i.e., 0x8000000000000000L), unlike the
			     * positive range of 0L to 0L + (~0L >>> 1), the difference in the range happens
			     * because there is no need to represent -0, thus the negative range is shifted by
			     * one toward the negative infinity, reaching 0x8000000000000000L, which is indeed
			     * the least negative integer that has the same absolute value of the ordinary
			     * binary representation, this happens once in every two's complement system.
			     *
			     * reference: perl -le'print((1<<63) - (1 + (~0 >> 1)))'
			     * reference: TestBasics.java
			     *
			     */
			    return new ErlLongImpl(v);
			}
			BigInteger big = BigInteger.valueOf(v & 0x7fffffffffffffffL).setBit(63 /* 64th bit */);
			return new ErlBigIntegerImpl(sign ? big.negate() : big);
		    }
		    break;
		default:
		    throw new RuntimeException("too lower arity for big integer type");
		}

		if (sign) { v = -v; mag = ~v; } else mag = v;
		if ((mag & 0x7fffffffL) == mag) {
		    /* 31-bit magnitude plus sign fit in 32-bit */
		    return new ErlIntegerImpl((int)v);
		}
		return new ErlLongImpl(v);
	    }

	    bytes = new byte[(int)arity];
	    buf.get(bytes, 0, (int)arity);
	    { /* little endian to big endian */
		int i = 0, j = (int)arity;
		while (i < j) {
		    byte tmp = bytes[i];
		    bytes[i++] = bytes[--j];
		    bytes[j] = tmp;
		}
	    }
	    return new ErlBigIntegerImpl(new BigInteger(sign ? -1 : 1, bytes));
        case ERL_STRING_EXT:
	    /* since ERL_STRING_EXT contains a byte array, we can
	     * assume it is a LATIN-1 (ISO-8859-1) encoding, which
	     * accepts full octet range
	     */
	    bytes = new byte[readUnsignedShort(buf)];
	    buf.get(bytes);
	    try {
		return new ErlListStringImpl(new String(bytes, "ISO-8859-1"));
	    } catch (java.io.UnsupportedEncodingException e) {
		throw new RuntimeException("failed to decode \"ISO-8859-1\"");
	    }
        case ERL_BINARY_EXT:
	    size = buf.getInt();
	    if (size < 0) {
		throw new IllegalArgumentException("binary size greater than 31-bit");
	    }
	    bytes = new byte[size];
	    buf.get(bytes);
	    return new ErlBinaryImpl(bytes);
        case ERL_NEW_FUN_EXT:
        case ERL_FUN_EXT:
	    throw new RuntimeException(String.format("not implemented, tag=%d (0x%x)", tag, tag));
	default:
	    throw new RuntimeException(String.format("exhaustion, tag=%d (0x%x)", tag, tag));
	}
	//return null;
    }
    public ErlTerm decode(ByteBuffer buf) {
	final int tag = peekUnsignedByte(buf, buf.position());
	if (tag == ERL_VERSION_MAGIC) {
	    buf.get(); /* skip */
	}
	return decode0(buf);
    }
}
