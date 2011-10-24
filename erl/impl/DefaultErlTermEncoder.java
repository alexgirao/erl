
/* implemented after otp_src_R14B04/lib/erl_interface/src/encode/encode_*.c
 */

/* a ErlTerm may not be fully encoded if ByteBuffer hasn't sufficient
 * room, the protocol must handle this (don't know how yet)
 */

package erl.impl;

import erl.*;

import java.nio.ByteBuffer;

/**
 * Default implementation of ErlTermEncoder
 */
public class DefaultErlTermEncoder implements ErlTermEncoder
{
    private static ErlTerm.ClassVisitor<Void,ByteBuffer> ev = new ErlTerm.ClassVisitor<Void,ByteBuffer>() {
	private void writeLE(ByteBuffer buf, long n, final int b) {
	    for (int i = 0; i < b; i++) {
		buf.put((byte)(n & 0xff));
		n >>= 8;
	    }
	}

	public Void visit_atom(ErlAtom o, ByteBuffer b)
	{
	    String v = o.getValue();
	    b.put(ErlTerm.ERL_ATOM_EXT);
	    b.putShort((short)v.length());
	    b.put(v.getBytes());
	    return null;
	}
	public Void visit_binary(ErlBinary o, ByteBuffer b)
	{
	    byte v[] = o.getBuffer(false);
	    b.put(ErlTerm.ERL_BINARY_EXT);
	    b.putInt(v.length);
	    b.put(v);
	    return null;
	}
	public Void visit_float(ErlFloat o, ByteBuffer b)
	{
	    b.put(ErlTerm.NEW_FLOAT_EXT);
	    b.putDouble(o.getFloatValue());
	    return null;
	}
	public Void visit_integer(ErlInteger o, ByteBuffer b)
	{
	    /* from OtpOutputStream.java
	     */
	    /*
	     * If v<0 and unsigned==true the value
	     * java.lang.Long.MAX_VALUE-java.lang.Long.MIN_VALUE+1+v is written, i.e
	     * v is regarded as unsigned two's complement.
	     */
	    boolean unsigned = true;
	    long v = o.getLongValue();
	    if ((v & 0xffL) == v) {
		// will fit in one byte
		b.put(ErlTerm.ERL_SMALL_INTEGER_EXT);
		b.put((byte)v);
	    } else {
		// note that v != 0L
		if (v < 0 && unsigned || v < ErlTerm.ERL_MIN
		    || v > ErlTerm.ERL_MAX) {
		    // some kind of bignum
		    final long abs = unsigned ? v : v < 0 ? -v : v;
		    final int sign = unsigned ? 0 : v < 0 ? 1 : 0;
		    int n;
		    long mask;
		    for (mask = 0xFFFFffffL, n = 4; (abs & mask) != abs; n++, mask = mask << 8 | 0xffL) {
			; // count nonzero bytes
		    }
		    b.put(ErlTerm.ERL_SMALL_BIG_EXT);
		    b.put((byte)n); // length
		    b.put((byte)sign); // sign
		    writeLE(b, abs, n); // value. obs! little endian
		} else {
		    b.put(ErlTerm.ERL_INTEGER_EXT);
		    b.putInt((int)v);
		}
	    }
	    return null;
	}
	public Void visit_list(ErlList o, ByteBuffer d) {return null;}
	public Void visit_ref(ErlRef o, ByteBuffer d) {return null;}
	public Void visit_tuple(ErlTuple o, ByteBuffer d) {return null;}
    };

    public void encode(ByteBuffer buf, ErlTerm t)
    {
	t.accept(ev, buf);
    }
}