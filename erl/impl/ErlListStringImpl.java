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

    private final String str;
    private final int codepointCount;

    public String getValue() {
	return str;
    }

    public ErlListStringImpl(String str) {
	if (str == null || str.length() == 0) {
	    throw new IllegalArgumentException("empty list");
	}
	this.str = str;
	this.codepointCount = str.codePointCount(0, str.length());
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
	return obj != null && this.getClass().isInstance(obj) && ((ErlListStringImpl)obj).str.equals(str);
    }

    public int arity() {
        return this.codepointCount;
    }

    /* from otp_src_R14B04/lib/jinterface/java_src/com/ericsson/otp/erlang/OtpErlangString.java
     */
    public int[] stringToCodePoints() {
	final int m = codepointCount;
	final int [] codePoints = new int[m];
	for (int i = 0, j = 0;  j < m;  i = str.offsetByCodePoints(i, 1), j++) {
	    codePoints[j] = str.codePointAt(i);
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
