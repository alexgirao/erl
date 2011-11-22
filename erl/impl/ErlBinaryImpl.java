package erl.impl;

import erl.ErlTerm;
import erl.ErlBinary;

/**
 * Binary implementation.
 */
public class ErlBinaryImpl extends ErlBinaryRepBase implements ErlBinary {

    public ErlBinaryImpl(byte[] buf) {
        super(buf);
    }

    public byte[] getBuffer(boolean doCopy) {
        return getBufferRepresentation(doCopy);
    }

    public <R,D> R accept(ErlTerm.ClassVisitor<R,D> v, D d)
    {
	return v.visitBinary(this, d);
    }
}
