package erl.impl;

import erl.ErlList;
import erl.ErlListTerms;
import erl.ErlTerm;

import java.util.Iterator;

public class ErlListTermsImpl implements ErlListTerms {

    private final ErlTerm terms[];

    public ErlListTermsImpl(ErlTerm ... terms) {
        if (terms == null || terms.length == 0) {
	    throw new IllegalArgumentException("empty list");
        }
	this.terms = terms;
    }

    public ErlListTermsImpl(ErlTerm term) {
	if (term == null) {
	    term = new ErlListNilImpl();
	}
	terms = new ErlTerm[] {term};
    }

    public ErlListTermsImpl(ErlTerm head, ErlTerm ... tail) {
	if (head == null) {
	    head = new ErlListNilImpl();
	}
        if (tail == null || tail.length == 0) {
            terms = new ErlTerm[] {head};
        } else {
	    terms = new ErlTerm[1 + tail.length];
	    terms[0] = head;
	    System.arraycopy(tail, 0, terms, 1, tail.length);
        }
    }

    /*
     */

    public ErlTerm hd() {
        if (terms == null) {
            return null;
        } else {
            return terms[0];
        }
    }

    public ErlList tl() {
        if (terms == null) {
            throw new NullPointerException("empty list has no tail");
        } else {
            ErlTerm ret[] = new ErlTerm[terms.length - 1];
            System.arraycopy(terms, 1, ret, 0, ret.length);
            return new ErlListTermsImpl(ret);
        }
    }

    public ErlList insert(ErlTerm term) {
        return new ErlListTermsImpl(term, terms);
    }

    public ErlList append(ErlList list) {
        throw new RuntimeException("not implemented");
    }

    public Iterator<ErlTerm> iterator() {
        return new ErlListImplIterator(terms);
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
        return terms == null;
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
        return terms.length;
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
	return v.visitList(this, d);
    }
}
