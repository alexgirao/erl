package erl.impl;

import erl.ErlTerm;
import erl.ErlInteger;
import erl.ErlFloat;

import java.math.BigInteger;

/**
 * Integer implementation.
 */
public class ErlIntegerImpl implements ErlInteger {

    private final long value;

    public ErlIntegerImpl(long value) {
        this.value = value;
    }

    public int getIntValue() {
        return (int)value;
    }

    public long getLongValue() {
        return value;
    }

    public BigInteger getBigIntegerValue() {
        return new BigInteger(String.valueOf(value));
    }

    public double getFloatValue() {
        return (double)value;
    }

    @Override
    public int hashCode() {
        return new Long(value).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null &&
                ((obj instanceof ErlInteger) && ((ErlInteger)obj).getLongValue() == value
                || (obj instanceof ErlFloat) && ((ErlFloat)obj).getFloatValue() == value);
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
        return value >= 0x20 && value <= 0x7e || value >= 0xa0 && value <= 0xff;
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
	return v.visit_integer(this, d);
    }
}
