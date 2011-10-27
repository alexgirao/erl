package erl.impl;

import erl.ErlList;
import erl.ErlTerm;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;

/**
 * List implementation.
 * This is naive, in that every element is an object (no space optimization for strings).
 */
public class ErlListStringImpl implements ErlList {

    private final String utf8;
    private final byte utf8bytes[];

    public ErlListStringImpl(String utf8) {
	if (utf8 == null || utf8.length() == 0) {
	    throw new IllegalArgumentException("empty list");
	}
	this.utf8 = utf8;
	try {
	    this.utf8bytes = utf8.getBytes("UTF-8");
	} catch (java.io.UnsupportedEncodingException e) {
	    throw new RuntimeException("unsupported encoding exception");
	}
    }

    /*
     */

    public ErlTerm hd() {
        throw new RuntimeException("not implemented");
    }

    public ErlList tl() {
        throw new RuntimeException("not implemented");
    }

    public ErlList insert(ErlTerm term) {
        throw new RuntimeException("not implemented");
    }

    public ErlList append(ErlList list) {
        throw new RuntimeException("not implemented");
    }

    public Iterator<ErlTerm> iterator() {
        return new ErlListImplIterator(utf8bytes);
    }

    @Override
    public boolean equals(Object obj) {
	return obj != null && this.getClass().isInstance(obj) && ((ErlListStringImpl)obj).utf8.equals(utf8);
    }

    public boolean isAtom() {
        return false;
    }

    public boolean isBoolean() {
        return false;
    }

    public boolean isTrue() {
        return false;
    }

    public boolean isFalse() {
        return false;
    }

    public boolean isList() {
        return true;
    }

    public boolean isNil() {
        return false;
    }

    public boolean isTuple() {
        return false;
    }

    public boolean isNumber() {
        return false;
    }

    public boolean isInteger() {
        return false;
    }

    public boolean isFloat() {
        return false;
    }

    public boolean isLatin1Char() {
        return false;
    }

    public boolean isUnicodeChar() {
        return false;
    }

    public boolean isBinary() {
        return false;
    }

    public boolean isRef() {
        return false;
    }

    public int size() {
        return utf8bytes.length;
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
	return v.visit_list(this, d);
    }
}
