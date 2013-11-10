
/* implemented after otp_src_R14B04/lib/erl_interface/src/encode/encode_*.c
 */

/* an ErlTerm may not be fully encoded if ByteBuffer hasn't sufficient
 * room, the protocol must foreseee this
 */

package erl.impl;

import erl.*;

import java.nio.ByteBuffer;

import java.math.BigInteger;

/**
 * Default implementation of ErlTermEncoder
 */
public class DefaultErlTermEncoder implements ErlTermEncoder {
    private static void reverse(byte[] bytes) {
	int i = 0, j = bytes.length;
	while (i < j) {
	    byte tmp = bytes[i];
	    bytes[i++] = bytes[--j];
	    bytes[j] = tmp;
	}
    }
    private static void writeInteger(int v, ByteBuffer b) {
	if ((v & 0xff) == v) {
	    // 8-bit fit, unsigned only
	    b.put(ErlTerm.ERL_SMALL_INTEGER_EXT);
	    b.put((byte)v);
	} else {
	    b.put(ErlTerm.ERL_INTEGER_EXT);
	    b.putInt(v);  // big-endian 4-byte two's-complement
	}
    }
    private static void writeLong(long v, ByteBuffer b) {
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
	    buf[0] = ErlTerm.ERL_SMALL_BIG_EXT;
	    long abs;
	    if (v == 0x8000000000000000L) {
		/* the 0 and the least negative integer can't be negated, this
		 * is a consequence of the two's complement system design */
		arity = buf[1] = 8;
		buf[2] = 1;
		buf[3 + 7] = -128;
	    } else {
		if (v < 0) { abs = -v; buf[2] = 1; }
		else abs = v;
		// little-endian
		while (abs > 0) {
		    buf[3 + arity++] = (byte)abs;
		    abs >>= 8;
		}
		buf[1] = (byte)arity;
	    }
	    b.put(buf, 0, 3 + arity);
	}
    }
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
	    writeInteger(o.getValue(), b);
	    return null;
	}
	public Void visitLong(ErlLong o, ByteBuffer b) {
	    writeLong(o.getValue(), b);
	    return null;
	}
	public Void visitBigInteger(ErlBigInteger o, ByteBuffer b) {
	    BigInteger abs = o.getValue();
	    int bitlen = abs.bitLength();
	    if (bitlen < 32) {
		/* 31-bit magnitude at most plus sign bit */
		writeInteger(abs.intValue(), b);
		return null;
	    }
	    if (bitlen < 64) {
		/* 63-bit magnitude at most plus sign bit */
		writeLong(abs.longValue(), b);
		return null;
	    }
	    int signum = abs.signum();
	    if (signum < 0) abs = abs.negate();
	    if ((bitlen & 7) != 0) { /* (bitlen & 7) == (bitlen % 8) */
		/* there is room for the 0-sign bit */
		byte[] bytes = abs.toByteArray();
		reverse(bytes); /* big-endian to little endian */
		if (bytes.length <= 0xff) {
		    b.put(ErlTerm.ERL_SMALL_BIG_EXT);
		    b.put((byte)bytes.length);
		} else {
		    b.put(ErlTerm.ERL_LARGE_BIG_EXT);
		    b.putInt(bytes.length);
		}
		b.put((byte)(signum < 0 ? 1 : 0));
		b.put(bytes);
	    } else {
		/* to properly represent a positive value in two's complement
		 * system, the most significant bit of the most significant byte
		 * must be zero, if there is no room for such bit, toByteArray()
		 * add a zero-valued most significant byte to fulfill the
		 * requirement, this byte is not used in ERL_SMALL_BIG_EXT
		 * representation
		 */
		byte[] bytes = abs.toByteArray();
		reverse(bytes); /* big-endian to little endian */
		if (bytes.length <= 0xff) {
		    b.put(ErlTerm.ERL_SMALL_BIG_EXT);
		    b.put((byte)(bytes.length - 1));
		} else {
		    b.put(ErlTerm.ERL_LARGE_BIG_EXT);
		    b.putInt(bytes.length - 1);
		}
		b.put((byte)(signum < 0 ? 1 : 0));
		b.put(bytes, 0, bytes.length - 1);
	    }
	    return null;
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
		    b.put(o.getValue().getBytes("ISO-8859-1"));
		} catch (java.io.UnsupportedEncodingException e) {
		    throw new RuntimeException("failed to encode as ISO-8859-1");
		}
	    } else {
		b.put((byte)ErlTerm.ERL_LIST_EXT);
		b.putInt(arity);
		byte bytes[];
		try {
		    bytes = o.getValue().getBytes("ISO-8859-1");
		} catch (java.io.UnsupportedEncodingException e) {
		    throw new RuntimeException("failed to encode as ISO-8859-1");
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
