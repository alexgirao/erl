package erl.impl;

import erl.ErlTerm;
import erl.ErlFloat;
import erl.ErlNumber;
import erl.ErlInteger;
import erl.ErlLong;

import java.math.BigInteger;

/**
 * Float implementation.
 */
public class ErlFloatImpl implements ErlFloat {

    private final double value;

    public ErlFloatImpl(Double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null &&
	    ((obj instanceof ErlFloat) && ((ErlFloat)obj).getValue() == value
	     || (obj instanceof ErlLong) && ((ErlLong)obj).getValue() == value
	     || (obj instanceof ErlInteger) && ((ErlInteger)obj).getValue() == value);
    }

    @Override
    public int hashCode() {
        return new Double(value).hashCode();
    }

    public int size() {
        throw new RuntimeException("float/number size not available");
    }

    public <R,D> R accept(ErlTerm.ClassVisitor<R,D> v, D d)
    {
	return v.visitFloat(this, d);
    }
}
