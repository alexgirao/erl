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

    private static class Elem {
        public final Elem next;
        public final ErlTerm termValue;

        private Elem(Elem next, ErlTerm termValue) {
            if (termValue == null) {
                throw new IllegalArgumentException("null element in list construction");
            }
            this.next = next;
            this.termValue = termValue;
        }
    }

    private final Elem head;


    public ErlListImpl() {
        head = null;
    }

    private ErlListImpl(Elem head) {
        this.head = head;
    }

    public ErlListImpl(ErlTerm ... terms) {
        if (terms == null || terms.length == 0) {
            head = null;
        } else {
            Elem tmp = null;
            for (int i = terms.length - 1 ; i >= 0 ; i--) {
                tmp = new Elem(tmp, terms[i]);
            }
            head = tmp;
        }
    }

    public ErlListImpl(String utf8, boolean toBytes)
            throws UnsupportedEncodingException {
        if (utf8 == null || utf8.length() == 0) {
            head = null;
        } else if (toBytes) {
            byte[] bytes = utf8.getBytes("UTF-8");
            Elem tmp = null;
            for (int i = bytes.length - 1 ; i >= 0 ; i--) {
                tmp = new Elem(tmp, new ErlIntegerImpl(bytes[i]));
            }
            head = tmp;
        } else {
            Elem tmp = null;
            for (int i = utf8.length() - 1 ; i >= 0 ; i--) {
                tmp = new Elem(tmp, new ErlIntegerImpl(utf8.charAt(i)));
            }
            head = tmp;
        }
    }


    public ErlTerm hd() {
        if (head == null) {
            return null;
        } else {
            return head.termValue;
        }
    }

    public ErlList tl() {
        if (head == null) {
            throw new NullPointerException("empty list has no tail");
        } else {
            return new ErlListImpl(head.next);
        }
    }

    public ErlList append(ErlList list) {
        throw new RuntimeException("not implemented");
    }

    public ErlList insert(ErlTerm term) {
        return new ErlListImpl(new Elem(head, term));
    }

    public String getLatin1StringValue() {
        throw new RuntimeException("not implemented");
    }

    public String getUtf8StringValue() {
        throw new RuntimeException("not implemented");
    }

    public String getUnicodeStringValue() {
        throw new RuntimeException("not implemented");
    }

    public Iterator<ErlTerm> iterator() {
        return new ErlListImplIterator(head);
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

    public boolean isEmptyList() {
        return head == null;
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
        int i = 0;
        for (Elem ptr = head ; ptr != null ; ptr = ptr.next) {
            i++;
        }
        return i;
    }

    private class ErlListImplIterator implements Iterator<ErlTerm> {

        private Elem next;

        private ErlListImplIterator(Elem next) {
            this.next = next;
        }

        public boolean hasNext() {
            return next != null;
        }

        public ErlTerm next() {
            if (next == null) {
                return null;
            }
            ErlTerm ret = next.termValue;
            next = next.next;
            return ret;
        }

        public void remove() {
            throw new IllegalAccessError("list is immutable");
        }
    }

    public <R,D> R accept(ErlTerm.ClassVisitor<R,D> v, D d)
    {
	return v.visit_list(this, d);
    }
}
