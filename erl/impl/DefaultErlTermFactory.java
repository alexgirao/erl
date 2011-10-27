package erl.impl;

import erl.*;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

/**
 * Default implementation of ErlTermFactory: uses net.util.jenco.erl.impl classes.
 */
public class DefaultErlTermFactory implements ErlTermFactory {

    public ErlAtom createAtom(String value) {
        return new ErlAtomImpl(value);
    }

    public ErlBinary createBinary(byte[] value) {
        return new ErlBinaryImpl(value);
    }

    public ErlNumber createNumber(Number value) {
        if ((value instanceof Byte) || (value instanceof Short) ||
	    (value instanceof Integer)) {
            return createNumber(value.intValue());
	} else if (value instanceof Long) {
            return createNumber(value.longValue());
        } else {
            return createNumber(value.doubleValue());
        }
    }

    public ErlFloat createNumber(float value) {
        return new ErlFloatImpl((double) value);
    }

    public ErlFloat createNumber(double value) {
        return new ErlFloatImpl(value);
    }

    public ErlInteger createNumber(int value) {
        return new ErlIntegerImpl(value);
    }

    public ErlLong createNumber(long value) {
        return new ErlLongImpl(value);
    }

    public ErlBigInteger createNumber(BigInteger value) {
        return new ErlBigIntegerImpl(value);
    }

    public ErlList createList() {
        return new ErlListNilImpl();
    }

    public ErlList createList(String utf8) {
        return new ErlListStringImpl(utf8);
    }

    public ErlList createList(byte bytes[]) {
	return new ErlListByteArrayImpl(bytes);
    }

    public ErlList createList(ErlTerm... terms) {
        return new ErlListTermsImpl(terms);
    }

    public ErlTuple createTuple(ErlTerm... terms) {
        return new ErlTupleImpl(terms);
    }

    public ErlRef createRef(byte[] bytes) {
        return new ErlRefImpl(bytes);
    }
}
