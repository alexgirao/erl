package erl;

import erl.impl.DefaultErlTermFactory;
import erl.impl.DefaultErlTermEncoder;
import erl.impl.DefaultErlTermDecoder;

import java.nio.ByteBuffer;

import java.io.UnsupportedEncodingException;

/**
 * Erlang Term Easy Creation Co.
 */
public class ET {

    private static final ErlTermFactory factory = new DefaultErlTermFactory();
    private static final ErlTermEncoder encoder = new DefaultErlTermEncoder();
    private static final ErlTermDecoder decoder = new DefaultErlTermDecoder();

    // Atoms:

    public static ErlAtom atom(String value) {
        return factory.createAtom(value);
    }

    // Binaries:

    public static ErlBinary binary() {
        return factory.createBinary(new byte[0]);
    }

    public static ErlBinary binary(byte ... bytes) {
        return factory.createBinary(bytes);
    }

    public static ErlBinary binary(Number ... bytes) {
        byte[] actualBytes = new byte[bytes.length];
        for (int i = 0 ; i < bytes.length ; i++) {
            actualBytes[i] = bytes[i].byteValue();
        }
        return factory.createBinary(actualBytes);
    }

    public static ErlBinary binary(String utf8) {
        byte[] bytes;
        try {
            bytes = utf8.getBytes("UTF-8");
            return binary(bytes);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }


    // Numbers:

    public static ErlNumber number(Number n) {
        return factory.createNumber(n);
    }

    public static ErlNumber number(int n) {
        return factory.createNumber(n);
    }

    public static ErlNumber number(long n) {
        return factory.createNumber(n);
    }

    public static ErlNumber number(float n) {
        return factory.createNumber(n);
    }

    public static ErlNumber number(double n) {
        return factory.createNumber(n);
    }


    // Lists:

    public static ErlList list() {
        return factory.createList();
    }

    public static ErlList list(String utf8) {
        return list(utf8, true);
    }

    public static ErlList list(String utf8, boolean toBytes) {
        try {
            return factory.createList(utf8, toBytes);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static ErlList list(Object ... obj) {
        return factory.createList(mkTerms(obj));
    }


    // Refs:

    public static ErlRef ref(byte[] bytes) {
        return factory.createRef(bytes);
    }


    // Tuples:

    public static ErlTuple tuple(Object ... obj) {
        return factory.createTuple(mkTerms(obj));
    }


    // Patterns:

    // Utils:

    private static ErlTerm[] mkTerms(Object ... obj)
    {
        ErlTerm[] terms = new ErlTerm[obj.length];
        for (int i = 0 ; i < terms.length ; i++) {
            Object it = obj[i];
            ErlTerm term;

            if (it instanceof ErlTerm) {
                term = (ErlTerm) it;
            } else if (it instanceof Number) {
                term = number((Number)it);
            } else if (it instanceof String) {
                term = list((String)it);
            } else if (it instanceof Boolean) {
                term = atom(it.equals(Boolean.TRUE) ? "true" : "false");
            } else {
                throw new RuntimeException("cannot convert "+it.getClass()+" at index "+i);
            }

            terms[i] = term;
        }

        return terms;
    }

    public static void encode(ByteBuffer buf, ErlTerm t)
    {
	encoder.encode(buf, t);
    }
}
