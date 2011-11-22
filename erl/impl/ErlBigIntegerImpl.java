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

    public ErlBigIntegerImpl(byte bytes[]) {
        this.value = new BigInteger(bytes);
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

    public int size() {
        throw new RuntimeException("integer/number size not available");
    }

    public <R,D> R accept(ErlTerm.ClassVisitor<R,D> v, D d)
    {
	return v.visitBigInteger(this, d);
    }
}
