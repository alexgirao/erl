package erl.impl;

import erl.ErlList;
import erl.ErlTerm;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;

/**
 * List implementation.
 * This is naive, in that every element is an object (no space optimization for strings).
 */
public class ErlListImpl implements ErlList {

    private final ErlTerm items[];

    public ErlListImpl() {
        items = null;
    }

    public ErlListImpl(ErlTerm ... terms) {
        if (terms == null || terms.length == 0) {
            items = null;
        } else {
	    items = terms;
        }
    }

    public ErlListImpl(ErlTerm head, ErlTerm ... tail) {
	if (head == null) {
	    head = new ErlListImpl(); // nil
	}
        if (tail == null || tail.length == 0) {
            items = new ErlTerm[] {head};
        } else {
	    items = new ErlTerm[1 + tail.length];
	    items[0] = head;
	    System.arraycopy(tail, 0, items, 1, tail.length);
        }
    }

    public ErlListImpl(String utf8) {
        throw new RuntimeException("not implemented");
    }

    public ErlListImpl(byte bytes[]) {
        throw new RuntimeException("not implemented");
    }

    /*
     */

    public ErlTerm hd() {
        if (items == null) {
            return null;
        } else {
            return items[0];
        }
    }

    public ErlList tl() {
        if (items == null) {
            throw new NullPointerException("empty list has no tail");
        } else {
            ErlTerm ret[] = new ErlTerm[items.length - 1];
            System.arraycopy(items, 1, ret, 0, ret.length);
            return new ErlListImpl(ret);
        }
    }

    public ErlList insert(ErlTerm term) {
        return new ErlListImpl(term, items);
    }

    public ErlList append(ErlList list) {
        throw new RuntimeException("not implemented");
    }

    public Iterator<ErlTerm> iterator() {
        return new ErlListImplIterator(items);
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
        return items == null;
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
        return items.length;
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
