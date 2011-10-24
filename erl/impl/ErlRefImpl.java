package erl.impl;

import erl.ErlTerm;
import erl.ErlRef;

/**
 * Reference implementation.
 */
public class ErlRefImpl extends ErlBinaryRepBase implements ErlRef {

    public ErlRefImpl(byte[] buf) {
        super(buf);
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
        return false;
    }

    public boolean isEmptyList() {
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
        return true;
    }

    public int size() {
        throw new RuntimeException("ref size not available");
    }

    public <R,D> R accept(ErlTerm.ClassVisitor<R,D> v, D d)
    {
	return v.visit_ref(this, d);
    }
}
