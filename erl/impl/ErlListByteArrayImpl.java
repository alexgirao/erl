package erl.impl;

import erl.ErlList;
import erl.ErlListByteArray;
import erl.ErlTerm;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;

/**
 * List implementation.
 * This is naive, in that every element is an object (no space optimization for strings).
 */
public class ErlListByteArrayImpl implements ErlListByteArray {

    private final byte bytes[];

    public byte[] getValue() {
	return bytes;
    }

    public ErlListByteArrayImpl(byte bytes[], boolean copy) {
	if (copy) {
	    this.bytes = new byte[bytes.length];
	    System.arraycopy(bytes, 0, this.bytes, 0, bytes.length);
	} else {
	    this.bytes = bytes;
	}
    }

    public ErlListByteArrayImpl(byte bytes[]) {
	this(bytes, true);
    }

    /*
     */

    public ErlTerm hd() {
	return new ErlIntegerImpl(bytes[0]);
    }

    public ErlList tl() {
	byte ret[] = new byte[bytes.length - 1];
	System.arraycopy(bytes, 1, ret, 0, ret.length);
	return new ErlListByteArrayImpl(ret, false);
    }

    public ErlList insert(ErlTerm term) {
        throw new RuntimeException("not implemented");
    }

    public ErlList append(ErlList list) {
        throw new RuntimeException("not implemented");
    }

    public Iterator<ErlTerm> iterator() {
        return new ErlListImplIterator(bytes);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || ! (obj instanceof ErlList)) {
            return false;
        } else {
            Iterator<ErlTerm> it1 = iterator();
            Iterator<ErlTerm> it2 = ((ErlList)obj).iterator();
            while (it1.hasNext() && it2.hasNext()) {
                if (!it1.next().equals(it2.next())) {
                    return false;
                }
            }
            if (it1.hasNext() || it2.hasNext()) {
                return false;
            }
            return true;
        }
    }

    @Override
    public int hashCode() {
        int hc = 0;
        for (ErlTerm it: this) {
            hc ^= it.hashCode();
        }
        return hc;
    }

    public int arity() {
        return bytes.length;
    }

    private class ErlListImplIterator implements Iterator<ErlTerm> {
        private int counter = 0;
	private byte bytes[];
        private ErlListImplIterator(byte bytes[]) {
            this.bytes = bytes;
        }
        public boolean hasNext() {
            return counter < bytes.length;
        }
        public ErlTerm next() {
	    return new ErlIntegerImpl(bytes[counter++] & 0xFF);
        }
        public void remove() {
            throw new UnsupportedOperationException("list is immutable");
        }
    }

    public <R,D> R accept(ErlTerm.ClassVisitor<R,D> v, D d) {
	return v.visitListByteArray(this, d);
    }
}
