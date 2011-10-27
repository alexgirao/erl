package erl.impl;

import erl.ErlTerm;
import erl.ErlFloat;
import erl.ErlNumber;
import erl.ErlInteger;

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
	     || (obj instanceof ErlInteger) && ((ErlInteger)obj).getValue() == value);
    }

    @Override
    public int hashCode() {
        return new Double(value).hashCode();
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
        return false; /*Math.round(value) == value;*/
    }

    public boolean isFloat() {
        return true;
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
        throw new RuntimeException("float/number size not available");
    }

    public <R,D> R accept(ErlTerm.ClassVisitor<R,D> v, D d)
    {
	return v.visitFloat(this, d);
    }
}
