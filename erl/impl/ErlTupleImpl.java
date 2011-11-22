package erl.impl;

import erl.ErlTuple;
import erl.ErlTerm;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

/**
 * Tuple implementation.
 */
public class ErlTupleImpl implements ErlTuple {

    private final List<ErlTerm> terms;

    public ErlTupleImpl(ErlTerm ... terms) {
        if (terms == null || terms.length == 0) {
            throw new IllegalArgumentException("empty tuple construction");
        }
        this.terms = new ArrayList<ErlTerm>(terms.length);
        for (int i = 0 ; i < terms.length ; i++) {
            ErlTerm term = terms[i];
            if (term == null) {
                throw new IllegalArgumentException("null term in tuple construction at index "+i);
            }
            this.terms.add(term);
        }
    }


    public int arity() {
        return terms.size();
    }

    public ErlTerm element(int index) {
        return terms.get(index);
    }

    public Iterator<ErlTerm> iterator() {
        return new ErlTupleImplIterator(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || ! (obj instanceof ErlTuple)) {
            return false;
        } else {
            Iterator<ErlTerm> it1 = iterator();
            Iterator<ErlTerm> it2 = ((ErlTuple)obj).iterator();

            for (; it1.hasNext() && it2.hasNext() ;) {
                if (! it1.next().equals(it2.next())) {
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

    private class ErlTupleImplIterator implements Iterator<ErlTerm> {

        final Iterator<ErlTerm> iter;

        public ErlTupleImplIterator(ErlTupleImpl tuple) {
            iter = tuple.terms.listIterator();
        }

        public boolean hasNext() {
            return iter.hasNext();
        }

        public ErlTerm next() {
            return iter.next();
        }

        public void remove() {
            throw new IllegalAccessError("tuple is immutable");
        }
    }

    public <R,D> R accept(ErlTerm.ClassVisitor<R,D> v, D d)
    {
	return v.visitTuple(this, d);
    }
}
