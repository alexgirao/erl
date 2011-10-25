package erl.impl;

import erl.ErlTerm;
import erl.ErlAtom;

/**
 * Atom implementation.
 */
public class ErlAtomImpl implements ErlAtom {

    private final String value;

    public ErlAtomImpl(String value) {
        if (value == null || value.length() == 0) {
            throw new IllegalArgumentException("empty atom");
        }
	if (value.length() > ErlTerm.MAXATOMLEN) {
            throw new IllegalArgumentException("atom too large");
	}
        for (int i = 0, ilen = value.length(); i < ilen; i++) {
            char c = value.charAt(i);
            if (c < 0 || c > 255) {
                throw new IllegalArgumentException("invalid char in atom '"+value+"' pos "+i+": "+(int)c);
            }
        }
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && (obj instanceof ErlAtom)
                && ((ErlAtom)obj).getValue().equals(getValue());
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    public boolean isAtom() {
        return true;
    }

    public boolean isBoolean() {
        return isTrue() || isFalse();
    }

    public boolean isTrue() {
        return value.equals("true");
    }

    public boolean isFalse() {
        return value.equals("false");
    }

    public boolean isList() {
        return false;
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
        throw new RuntimeException("tuple size not available");
    }

    public <R,D> R accept(ErlTerm.ClassVisitor<R,D> v, D d)
    {
	return v.visit_atom(this, d);
    }
}
