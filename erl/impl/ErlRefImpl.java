package erl.impl;

import erl.ErlTerm;
import erl.ErlRef;

/**
 * Reference implementation.
 */
public class ErlRefImpl extends ErlBinaryRepBase implements ErlRef {

    public ErlRefImpl(byte[] buf) {
        super(buf);
    }

    public int size() {
        throw new RuntimeException("ref size not available");
    }

    public <R,D> R accept(ErlTerm.ClassVisitor<R,D> v, D d)
    {
	return v.visitRef(this, d);
    }
}
