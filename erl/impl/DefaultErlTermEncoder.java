
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
public class DefaultErlTermEncoder implements ErlTermEncoder {
    private static ErlTerm.ClassVisitor<Void,ByteBuffer> ev = new ErlTerm.ClassVisitor<Void,ByteBuffer>() {
	private void writeLE(ByteBuffer buf, long n, final int b) {
	    for (int i = 0; i < b; i++) {
		buf.put((byte)(n & 0xff));
		n >>= 8;
	    }
	}
	public Void visitAtom(ErlAtom o, ByteBuffer b) {
	    String v = o.getValue();
	    b.put(ErlTerm.ERL_ATOM_EXT);
	    b.putShort((short)v.length());
	    b.put(v.getBytes());
	    return null;
	}
	public Void visitBinary(ErlBinary o, ByteBuffer b) {
	    byte v[] = o.getBuffer(false);
	    b.put(ErlTerm.ERL_BINARY_EXT);
	    b.putInt(v.length);
	    b.put(v);
	    return null;
	}
	public Void visitFloat(ErlFloat o, ByteBuffer b) {
	    b.put(ErlTerm.NEW_FLOAT_EXT);
	    b.putDouble(o.getValue());
	    return null;
	}
	public Void visitInteger(ErlInteger o, ByteBuffer b) {
	    /* from OtpOutputStream.java
	     */

	    /* force the same logic at otp_R14B04/lib/erl_interface-3.7.5/src/encode/encode_longlong.c
	     */
	    boolean unsigned = false;

	    /*
	     * If v<0 and unsigned==true the value
	     * java.lang.Long.MAX_VALUE-java.lang.Long.MIN_VALUE+1+v is written, i.e
	     * v is regarded as unsigned two's complement.
	     */
	    long v = o.getValue();
	    if ((v & 0xffL) == v) {
		// will fit in one byte
		b.put(ErlTerm.ERL_SMALL_INTEGER_EXT);
		b.put((byte)v);
	    } else {
		// note that v != 0L
		if (v < 0 && unsigned || v < ErlTerm.ERL_MIN || v > ErlTerm.ERL_MAX) {
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
	public Void visitBigInteger(ErlBigInteger o, ByteBuffer b) {
	    throw new RuntimeException("not implemented");
	}
	public Void visitLong(ErlLong o, ByteBuffer b) {
	    throw new RuntimeException("not implemented");
	}
	public Void visitListByteArray(ErlListByteArray o, ByteBuffer b) {
	    int arity = o.size();

	    b.put((byte)ErlTerm.ERL_LIST_EXT);
	    b.putInt(arity);

	    // TODO: write items

	    return null;
	}
	public Void visitListNil(ErlListNil o, ByteBuffer b) {
	    b.put((byte)ErlTerm.ERL_NIL_EXT);
	    return null;
	}
	public Void visitListString(ErlListString o, ByteBuffer b) {
	    int arity = o.size();

	    b.put((byte)ErlTerm.ERL_LIST_EXT);
	    b.putInt(arity);

	    // TODO: write items

	    throw new RuntimeException("not implemented");
	}
	public Void visitListTerms(ErlListTerms o, ByteBuffer b) {
	    int arity = o.size();

	    b.put((byte)ErlTerm.ERL_LIST_EXT);
	    b.putInt(arity);

	    // TODO: write items

	    throw new RuntimeException("not implemented");
	}
	public Void visitRef(ErlRef o, ByteBuffer b) {
	    throw new RuntimeException("not implemented");
	    //return null;
	}
	public Void visitTuple(ErlTuple o, ByteBuffer b) {
	    int arity = o.size();
	    if (arity < 0xff) {
		b.put((byte)ErlTerm.ERL_SMALL_TUPLE_EXT);
		b.put((byte)arity);
	    } else {
		b.put((byte)ErlTerm.ERL_LARGE_TUPLE_EXT);
		b.putInt(arity);
	    }
	    return null;
	}
    };

    public void encode(ByteBuffer buf, ErlTerm t) {
	t.accept(ev, buf);
    }
}
