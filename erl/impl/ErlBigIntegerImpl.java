package erl.impl;

import erl.ErlTerm;
import erl.ErlInteger;
import erl.ErlLong;
import erl.ErlFloat;
import erl.ErlBigInteger;

import java.math.BigInteger;

/**
 * BigInteger implementation.
 */
public class ErlBigIntegerImpl implements ErlBigInteger {

    private final BigInteger value;

    public ErlBigIntegerImpl(BigInteger value) {
        this.value = value;
    }

    public BigInteger getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (obj instanceof ErlBigInteger) {
	    return ((ErlBigInteger)obj).getValue().equals(value);
	}
	String s = value.toString();
	if (obj instanceof ErlLong) {
	    return s.equals(Long.toString(((ErlLong)obj).getValue()));
	}
	if (obj instanceof ErlInteger) {
	    return s.equals(Integer.toString(((ErlInteger)obj).getValue()));
	}
	if (obj instanceof ErlFloat) {
	    return s.equals(Double.toString(((ErlFloat)obj).getValue()));
	}
	return false;
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
        return true;
    }

    public boolean isInteger() {
        return true;
    }

    public boolean isFloat() {
        return false;
    }

    public boolean isLatin1Char() {
	throw new RuntimeException("not implemented");
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
        throw new RuntimeException("integer/number size not available");
    }

    public <R,D> R accept(ErlTerm.ClassVisitor<R,D> v, D d)
    {
	return v.visit_biginteger(this, d);
    }
}
