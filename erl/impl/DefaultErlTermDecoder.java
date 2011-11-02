
package erl.impl;

import erl.*;

import static erl.ErlTerm.*;

import java.nio.ByteBuffer;

/**
 * Default implementation of ErlTermDecoder
 */
public class DefaultErlTermDecoder implements ErlTermDecoder
{
    private ErlTerm decode0(ByteBuffer buf) {
	final int tag = buf.get();
	int arity;
	ErlTerm terms[];

	switch (tag) {
        case ERL_SMALL_INTEGER_EXT:
	    byte b = buf.get();
	    /* all small integers are unsigned ranging from 0 to 255
	     */
	    return new ErlIntegerImpl(b >= 0 ? b : b + 256);
        case ERL_INTEGER_EXT:
	    return new ErlIntegerImpl(buf.getInt());
        case ERL_FLOAT_EXT:
	    throw new RuntimeException("not implemented");
        case NEW_FLOAT_EXT:
	    return new ErlFloatImpl(buf.getDouble());
        case ERL_ATOM_EXT:
	    int size = buf.getShort();
	    byte bytes[] = new byte[size];
	    return new ErlAtomImpl(bytes, false /* copy? */);
        case ERL_REFERENCE_EXT:
        case ERL_NEW_REFERENCE_EXT:
        case ERL_PORT_EXT:
        case ERL_PID_EXT:
	    throw new RuntimeException("not implemented");
        case ERL_SMALL_TUPLE_EXT:
	    arity = buf.get();
	    if (arity < 0) arity += 256;
	    terms = new ErlTerm[arity];
	    for (int i=0; i<arity; i++) {
		terms[i] = decode0(buf);
	    }
	    return new ErlTupleImpl(terms);
        case ERL_LARGE_TUPLE_EXT:
	    arity = buf.getInt();
	    if (arity < 0) {
		/* probably an encoding error or data corruption
		 */
		throw new RuntimeException("tuple too large to decode");
	    }
	    terms = new ErlTerm[arity];
	    for (int i=0; i<arity; i++) {
		terms[i] = decode0(buf);
	    }
	    return new ErlTupleImpl(terms);
        case ERL_NIL_EXT:
        case ERL_STRING_EXT:
        case ERL_LIST_EXT:
        case ERL_BINARY_EXT:
        case ERL_SMALL_BIG_EXT:
        case ERL_LARGE_BIG_EXT:
        case ERL_NEW_FUN_EXT:
        case ERL_FUN_EXT:
	    break;
	default:
	    throw new RuntimeException("exhaustion");
	}
	return null;
    }
    public ErlTerm decode(ByteBuffer buf) {
	int tag = buf.get(0);
	if (tag == ERL_VERSION_MAGIC) {
	    buf.get(); /* skip */
	}
	return decode0(buf);
    }
}
