package erl.impl;

import erl.ErlFloat;
import erl.ErlNumber;

import java.math.BigInteger;

/**
 * Float implementation.
 */
public class ErlFloatImpl implements ErlFloat {

    private final double value;

    public ErlFloatImpl(Double value) {
        this.value = value;
    }

    public int getIntValue() {
        return (int)value;
    }

    public long getLongValue() {
        return (long)value;
    }

    public BigInteger getBigIntegerValue() {
        return new BigInteger(String.valueOf(value));
    }

    public double getFloatValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && (obj instanceof ErlNumber)
                && value == ((ErlNumber)obj).getFloatValue();
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

    public boolean isEmptyList() {
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
}