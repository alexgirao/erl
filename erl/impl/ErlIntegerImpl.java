package erl.impl;

import erl.ErlTerm;
import erl.ErlInteger;
import erl.ErlLong;
import erl.ErlFloat;

import java.math.BigInteger;

/**
 * Integer implementation.
 */
public class ErlIntegerImpl implements ErlInteger {

    private final int value;

    public ErlIntegerImpl(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return new Long(value).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null &&
	    ((obj instanceof ErlInteger) && ((ErlInteger)obj).getValue() == value
	     || (obj instanceof ErlLong) && ((ErlLong)obj).getValue() == value
	     || (obj instanceof ErlFloat) && ((ErlFloat)obj).getValue() == value);
    }

    public int size() {
        throw new RuntimeException("integer/number size not available");
    }

    public <R,D> R accept(ErlTerm.ClassVisitor<R,D> v, D d)
    {
	return v.visitInteger(this, d);
    }
}
