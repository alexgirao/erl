package erl.impl;

import erl.ErlList;
import erl.ErlListString;
import erl.ErlTerm;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;

/**
 * List implementation.
 * This is naive, in that every element is an object (no space optimization for strings).
 */
public class ErlListStringImpl implements ErlListString {

    private final String utf8;
    private final int codepointCount;

    public String getValue() {
	return utf8;
    }

    public ErlListStringImpl(String utf8) {
	if (utf8 == null || utf8.length() == 0) {
	    throw new IllegalArgumentException("empty list");
	}
	this.utf8 = utf8;
	this.codepointCount = utf8.codePointCount(0, utf8.length());
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
        return new ErlListImplIterator(stringToCodePoints());
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

    public boolean isLong() {
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
        return this.codepointCount;
    }

    /* from otp_src_R14B04/lib/jinterface/java_src/com/ericsson/otp/erlang/OtpErlangString.java
     */
    public int[] stringToCodePoints() {
	final int m = codepointCount;
	final int [] codePoints = new int[m];
	for (int i = 0, j = 0;  j < m;  i = utf8.offsetByCodePoints(i, 1), j++) {
	    codePoints[j] = utf8.codePointAt(i);
	}
	return codePoints;
    }

    private class ErlListImplIterator implements Iterator<ErlTerm> {
        private int counter = 0;
	private int codepoints[];
        private ErlListImplIterator(int codepoints[]) {
            this.codepoints = codepoints;
        }
        public boolean hasNext() {
            return counter < codepoints.length;
        }
        public ErlTerm next() {
	    return new ErlIntegerImpl(codepoints[counter++] & 0xFF);
        }
        public void remove() {
            throw new UnsupportedOperationException("list is immutable");
        }
    }

    public <R,D> R accept(ErlTerm.ClassVisitor<R,D> v, D d) {
	return v.visitListString(this, d);
    }
}
