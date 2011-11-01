
package erl.impl;

import erl.ErlList;
import erl.ErlListNil;
import erl.ErlTerm;

import java.util.ArrayList;
import java.util.Iterator;

public class ErlListNilImpl implements ErlListNil {
    public ErlListNilImpl() {
    }

    public ErlTerm hd() {
	throw new IllegalArgumentException("list is nil");
    }

    public ErlList tl() {
	throw new IllegalArgumentException("list is nil");
    }

    public ErlList insert(ErlTerm term) {
	return new ErlListTermsImpl(term);
    }

    public ErlList append(ErlList list) {
	return list;
   }

    public Iterator<ErlTerm> iterator() {
	return (new ArrayList<ErlTerm>()).iterator();
    }

    @Override
    public boolean equals(Object obj) {
	return obj != null && (obj instanceof ErlListNilImpl);
    }

    @Override
    public int hashCode() {
	return 0;
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
        return true;
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
        return 0;
    }

    public <R,D> R accept(ErlTerm.ClassVisitor<R,D> v, D d) {
	return v.visitListNil(this, d);
    }
}
