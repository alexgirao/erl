
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
    private static int readByte(ByteBuffer buf) {
	final byte v = buf.get();
	return v >= 0 ? v : v + 0x100; // 256
    }
    private static int peekByte(ByteBuffer buf, int index) {
	final byte v = buf.get(index);
	return v >= 0 ? v : v + 0x100; // 256
    }
    public static int readShort(ByteBuffer buf) {
	final short v = buf.getShort();
	return v >= 0 ? v : v + 0x10000; // 65536
    }
    public static long readInt(ByteBuffer buf) {
	final int v = buf.getInt();
	return v >= 0 ? v : v + 0x100000000L; // 4294967296
    }
    private ErlTerm decode0(ByteBuffer buf) {
	final byte btag = buf.get();
	final int tag = btag >= 0 ? btag : btag + 256;
	long arity, v;
	int sign;
	ErlTerm terms[];

	//System.out.format("********** buf.position()=%d, decoding tag=%d (0x%x)\n", buf.position(), tag, tag);

	switch (tag) {
        case ERL_SMALL_INTEGER_EXT:
	    final byte b = buf.get();
	    /* all small integers are unsigned ranging from 0 to 255
	     */
	    return new ErlIntegerImpl(b >= 0 ? b : b + 256);
        case ERL_INTEGER_EXT:
	    return new ErlIntegerImpl(buf.getInt());
        case ERL_FLOAT_EXT:
	    throw new RuntimeException("not implemented");
        case NEW_FLOAT_EXT:
	    return new ErlFloatImpl(buf.getDouble());
        case ERL_ATOM_EXT:
	    {
		int size = readShort(buf);
		byte bytes[] = new byte[size];
		buf.get(bytes);
		return new ErlAtomImpl(bytes, false /* copy? */);
	    }
        case ERL_REFERENCE_EXT:
        case ERL_NEW_REFERENCE_EXT:
        case ERL_PORT_EXT:
        case ERL_PID_EXT:
	    throw new RuntimeException("not implemented");
        case ERL_SMALL_TUPLE_EXT:
	    arity = readByte(buf);
	    if ((arity & 0xff) != arity) {
		throw new IllegalArgumentException("tuple arity greater than 8-bit");
	    }
	    terms = new ErlTerm[(int)arity];
	    for (int i=0; i<arity; i++) {
		terms[i] = decode0(buf);
	    }
	    return new ErlTupleImpl(terms);
        case ERL_LARGE_TUPLE_EXT:
	    arity = readInt(buf);
	    if ((arity & 0x7fffffff) != arity) {
		/* arrays can't be indexed beyond positive integer (32-bit)
		 */
		throw new IllegalArgumentException("tuple arity greater than 31-bit");
	    }
	    terms = new ErlTerm[(int)arity];
	    for (int i=0; i<arity; i++) {
		terms[i] = decode0(buf);
	    }
	    return new ErlTupleImpl(terms);
        case ERL_LIST_EXT:
	    arity = buf.getInt();
	    if ((arity & 0x7fffffff) != arity) {
		/* arrays can't be indexed beyond positive integer (31-bit)
		 */
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
		arity = readInt(buf);
		sign = readByte(buf);
		if (((arity + 1) & 0x7fffffff) != (arity + 1)) {
		    /* arrays can't be indexed beyond positive integer (31-bit)
		     */
		    throw new IllegalArgumentException("big integer arity too large");
		}
	    } else {
		arity = readByte(buf);
		sign = readByte(buf);
	    }

	    //System.out.format("arity=%d\n", arity);

	    final byte nb[] = new byte[(int)arity + 1];

	    // Value is read as little endian. The big end is augumented
	    // with one zero byte to make the value 2's complement positive.
	    buf.get(nb, 0, (int)arity);

	    /* try to decode int/long based on size (little-endian)
	     */

	    switch ((int)arity) {
	    case 4:
		v = ((nb[3] & 0xffL) << 24) |
		    ((nb[2] & 0xffL) << 16) |
		    ((nb[1] & 0xffL) << 8) |
		    (nb[0] & 0xffL);

		/* the assertion below is just to show that it is
		 * an unoptimized approach to encode a negative
		 * number absolute value within the 31-bit range
		 * as a ERL_SMALL_BIG_EXT, ERL_INTEGER_EXT should
		 * be used instead
		 */
		assert (v & 0x7fffffff) != v;

		if (sign == 1) {
		    return new ErlLongImpl(-v);
		}
		return new ErlLongImpl(v);
	    case 5:
		v = ((nb[4] & 0xffL) << 32) +
		    ((nb[3] & 0xffL) << 24) +
		    ((nb[2] & 0xffL) << 16) +
		    ((nb[1] & 0xffL) << 8) +
		    (nb[0] & 0xffL);

		if (sign == 1) {
		    return new ErlLongImpl(-v);
		}
		return new ErlLongImpl(v);
	    default:
		if (arity <= 8) {
		    throw new RuntimeException(String.format("exhaustion, arity=%d", arity));
		}
	    }

	    /* reverse the array to make it big endian
	     */

	    for (int i = 0, j = nb.length; i < j--; i++) {
	    	// Swap [i] with [j]
	    	final byte tmp = nb[i];
	    	nb[i] = nb[j];
	    	nb[j] = tmp;
	    }

	    if (sign != 0) {
	    	// 2's complement negate the big endian value in the array
	    	int c = 1; // Carry
	    	for (int j = nb.length; j-- > 0;) {
	    	    c = (~nb[j] & 0xFF) + c;
	    	    nb[j] = (byte) c;
	    	    c >>= 8;
	    	}
	    }

	    throw new RuntimeException("wip: see OtpInputStream.byte_array_to_long()");
	    //return new ErlBigIntegerImpl(nb);
        case ERL_STRING_EXT:
	    /* since ERL_STRING_EXT contains a byte array, we can
	     * assume it is a LATIN-1 (ISO-8859-1) encoding, which
	     * accepts full octet range
	     */
	    {
		byte bytes[] = new byte[readShort(buf)];
		buf.get(bytes);
		try {
		    return new ErlListStringImpl(new String(bytes, "ISO-8859-1"));
		} catch (java.io.UnsupportedEncodingException e) {
		    throw new RuntimeException("failed to decode \"ISO-8859-1\"");
		}
	    }
        case ERL_BINARY_EXT:
        case ERL_NEW_FUN_EXT:
        case ERL_FUN_EXT:
	    throw new RuntimeException(String.format("******************** wip: tag=%d (0x%x)", tag, tag));
	default:
	    throw new RuntimeException(String.format("exhaustion, tag=%d (0x%x)", tag, tag));
	}
	//return null;
    }
    public ErlTerm decode(ByteBuffer buf) {
	final byte btag = buf.get(0); // peek
	final int tag = btag >= 0 ? btag : btag + 256;
	if (tag == ERL_VERSION_MAGIC) {
	    buf.get(); /* skip */
	}
	return decode0(buf);
    }
}
