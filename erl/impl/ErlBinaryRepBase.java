package erl.impl;

import java.util.Arrays;

/**
 * Base class for terms represented by binaries.
 */
public abstract class ErlBinaryRepBase {

    private final byte[] buf;

    protected ErlBinaryRepBase(byte[] buf) {
        if (buf == null) {
            throw new IllegalArgumentException("null buf in constructor");
        }
        this.buf = buf;
    }

    public byte[] getBufferRepresentation(boolean doCopy) {
        if (doCopy) {
            byte[] ret = new byte[buf.length];
            System.arraycopy(buf, 0, ret, 0, buf.length);
            return ret;
        } else {
            return buf;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return obj != null && this.getClass().isInstance(obj) &&
                Arrays.equals(buf, ((ErlBinaryRepBase) obj).getBufferRepresentation(false));
    }

    @Override
    public int hashCode() {
        return buf.hashCode();
    }

    public int size() {
        return buf.length;
    }

}