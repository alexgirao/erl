package erl.impl;

import erl.ErlTerm;
import erl.ErlAtom;

/**
 * Atom implementation.
 */
public class ErlAtomImpl implements ErlAtom {

    private final String value;
    private final byte bytes[];

    public ErlAtomImpl(String value) {
        if (value == null || value.length() == 0) {
            throw new IllegalArgumentException("empty atom");
        }
	if (value.length() > ErlTerm.MAXATOMLEN) {
            throw new IllegalArgumentException("atom too large");
	}
        for (int i = 0, ilen = value.length(); i < ilen; i++) {
            char c = value.charAt(i);
            if (c < 0 || c > 255) {
                throw new IllegalArgumentException("invalid char in atom '"+value+"' pos "+i+": "+(int)c);
            }
        }
        this.value = value;
	this.bytes = value.getBytes();
    }

    public ErlAtomImpl(byte bytes[], boolean copy) {
	if (bytes.length == 0) {
            throw new IllegalArgumentException("empty atom");
	}
	if (copy) {
	    this.bytes = new byte[bytes.length];
	    System.arraycopy(bytes, 0, this.bytes, 0, bytes.length);
	} else {
	    this.bytes = bytes;
	}
	try {
	    this.value = new String(this.bytes, "ISO-8859-1");
	} catch (java.io.UnsupportedEncodingException e) {
	    throw new RuntimeException("failed to decode \"ISO-8859-1\"");
	}
    }

    public String getValue() {
        return value;
    }

    public byte[] getBytes() {
        return bytes;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && (obj instanceof ErlAtom)
                && ((ErlAtom)obj).getValue().equals(getValue());
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    public boolean isTrue() {
        return value.equals("true");
    }

    public boolean isFalse() {
        return value.equals("false");
    }

    public int size() {
        throw new RuntimeException("tuple size not available");
    }

    public <R,D> R accept(ErlTerm.ClassVisitor<R,D> v, D d)
    {
	return v.visitAtom(this, d);
    }
}
