package erl.impl;

import erl.ErlTerm;
import erl.ErlBinary;

/**
 * Binary implementation.
 */
public class ErlBinaryImpl extends ErlBinaryRepBase implements ErlBinary {

    public ErlBinaryImpl(byte[] buf) {
        super(buf);
    }

    public byte[] getBuffer(boolean doCopy) {
        return getBufferRepresentation(doCopy);
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
        return true;
    }

    public boolean isRef() {
        return false;
    }

    public <R,D> R accept(ErlTerm.ClassVisitor<R,D> v, D d)
    {
	return v.visitBinary(this, d);
    }
}
