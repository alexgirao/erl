
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
	    final int v = o.getValue();
	    if ((v & 0xff) == v) {
		// 8-bit/octet fit
		b.put(ErlTerm.ERL_SMALL_INTEGER_EXT);
		b.put((byte)v);
	    } else {
		b.put(ErlTerm.ERL_INTEGER_EXT);
		b.putInt(v);  // big-endian 4-byte two's-complement system
	    }
	    return null;
	}
	public Void visitLong(ErlLong o, ByteBuffer b) {
	    final long v = o.getValue();
	    final long mag = v < 0 ? ~v : v;
	    if ((v & 0xff) == v) {
		// 8-bit fit, unsigned only
		b.put(ErlTerm.ERL_SMALL_INTEGER_EXT);
		b.put((byte)v);
	    } else if ((mag & 0x7fffffffL) == mag) {
		// 31-bit fit, msb is sign
		b.put(ErlTerm.ERL_INTEGER_EXT);
		b.putInt((int)v);  // big-endian 4-byte two's-complement system
	    } else {
		byte buf[] = new byte[3 + 8]; // a long can't hold more than 64-bit/8-octet
		int arity = 0;
		long abs = v < 0 ? -v : v;
		buf[0] = ErlTerm.ERL_SMALL_BIG_EXT;
		buf[2] = (byte)(v < 0 ? 1 : 0); // sign
		// little-endian
		while (abs > 0) {
		    buf[3 + arity] = (byte)abs;
		    abs >>= 8;
		    arity++;
		}
		buf[1] = (byte)arity;
		b.put(buf, 0, 3 + arity);
	    }
	    return null;
	}
	public Void visitBigInteger(ErlBigInteger o, ByteBuffer b) {
	    throw new RuntimeException("not implemented");
	}
	public Void visitListByteArray(ErlListByteArray o, ByteBuffer b) {
	    int arity = o.arity();
	    if (arity <= 0xffff) {
		b.put((byte)ErlTerm.ERL_STRING_EXT);
		b.putShort((short)arity);
		b.put(o.getValue());
	    } else {
		b.put((byte)ErlTerm.ERL_LIST_EXT);
		b.putInt(arity);
		for (byte v:o.getValue()) {
		    b.put(ErlTerm.ERL_SMALL_INTEGER_EXT);
		    b.put(v);
		}
	    }
	    return null;
	}
	public Void visitListNil(ErlListNil o, ByteBuffer b) {
	    b.put((byte)ErlTerm.ERL_NIL_EXT);
	    return null;
	}
	public Void visitListString(ErlListString o, ByteBuffer b) {
	    int arity = o.arity();
	    if (arity <= 0xffff) {
		b.put((byte)ErlTerm.ERL_STRING_EXT);
		b.putShort((short)arity);
		try {
		    b.put(o.getValue().getBytes("UTF-8"));
		} catch (java.io.UnsupportedEncodingException e) {
		    throw new RuntimeException("failed to encode as UTF-8");
		}
	    } else {
		b.put((byte)ErlTerm.ERL_LIST_EXT);
		b.putInt(arity);
		byte bytes[];
		try {
		    bytes = o.getValue().getBytes("UTF-8");
		} catch (java.io.UnsupportedEncodingException e) {
		    throw new RuntimeException("failed to encode as UTF-8");
		}
		for (byte v:bytes) {
		    b.put(ErlTerm.ERL_SMALL_INTEGER_EXT);
		    b.put(v);
		}
		b.put((byte)ErlTerm.ERL_NIL_EXT);
	    }
	    return null;
	}
	public Void visitListTerms(ErlListTerms o, ByteBuffer b) {
	    int arity = o.arity();
	    b.put((byte)ErlTerm.ERL_LIST_EXT);
	    b.putInt(arity);
	    for (ErlTerm i:o) {
		i.accept(this, b);
	    }
	    b.put((byte)ErlTerm.ERL_NIL_EXT);
	    return null;
	}
	public Void visitRef(ErlRef o, ByteBuffer b) {
	    throw new RuntimeException("not implemented");
	}
	public Void visitTuple(ErlTuple o, ByteBuffer b) {
	    int arity = o.arity();
	    if (arity < 0xff) {
		b.put((byte)ErlTerm.ERL_SMALL_TUPLE_EXT);
		b.put((byte)arity);
	    } else {
		b.put((byte)ErlTerm.ERL_LARGE_TUPLE_EXT);
		b.putInt(arity);
	    }
	    for (ErlTerm i:o) {
		i.accept(this, b);
	    }
	    return null;
	}
    };

    public void encode(ByteBuffer buf, ErlTerm t) {
	t.accept(ev, buf);
    }
}
