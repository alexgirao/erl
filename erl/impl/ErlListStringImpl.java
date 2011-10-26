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

    public ErlListStringImpl(String utf8) {
	if (utf8 == null || utf8.length() == 0) {
	    throw new IllegalArgumentException("empty list");
	}
	this.utf8 = utf8;
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
        throw new RuntimeException("not implemented");
    }

    @Override
    public boolean equals(Object obj) {
        throw new RuntimeException("not implemented");
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
        throw new RuntimeException("not implemented");
    }

    private class ErlListImplIterator implements Iterator<ErlTerm> {
        private int counter = 0;
	private ErlTerm terms[];
        private ErlListImplIterator(ErlTerm terms[]) {
            this.terms = terms;
        }
        public boolean hasNext() {
            return counter < terms.length;
        }
        public ErlTerm next() {
	    return terms[counter++];
        }
        public void remove() {
            throw new UnsupportedOperationException("list is immutable");
        }
    }

    public <R,D> R accept(ErlTerm.ClassVisitor<R,D> v, D d) {
	return v.visit_list(this, d);
    }
}
