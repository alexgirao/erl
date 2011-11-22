package erl.impl;

import erl.ErlTerm;
import erl.ErlInteger;
import erl.ErlFloat;
import erl.ErlLong;

/**
 * Long implementation.
 */
public class ErlLongImpl implements ErlLong {

    private final long value;

    public ErlLongImpl(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return new Long(value).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null &&
	    ((obj instanceof ErlLong) && ((ErlLong)obj).getValue() == value
	     || (obj instanceof ErlInteger) && ((ErlInteger)obj).getValue() == value
	     || (obj instanceof ErlFloat) && ((ErlFloat)obj).getValue() == value);
    }

    public int size() {
        throw new RuntimeException("integer/number size not available");
    }

    public <R,D> R accept(ErlTerm.ClassVisitor<R,D> v, D d)
    {
	return v.visitLong(this, d);
    }
}
