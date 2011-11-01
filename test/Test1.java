package test;

import junit.framework.TestCase;

import erl.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static erl.ET.list;
import static erl.ET.atom;
import static erl.ET.binary;
import static erl.ET.number;
import static erl.ET.tuple;

import java.util.Arrays;
import java.util.Iterator;

/**
 * Basic tests for ErlTerm implementations.
 */
public class Test1 extends TestCase
{
    public void testAtoms()
    {
        ErlAtom a1 = atom("abc");
        ErlAtom a2 = atom("xyz");

        assertEquals(a1.getValue(), "abc");
        assertEquals(a2.getValue(), "xyz");

        assertEquals(a1, a1);
        assertFalse(a1.equals(a2));
        assertEquals(a1, atom("abc"));
    }

    public void testBinaries() throws Exception
    {
        ErlBinary b1 = binary(new byte[]{
                (byte)0xaa,0x55,0x66,(byte)0x99,(byte)0x88});
        ErlBinary empty = binary(new byte[]{});
        ErlBinary b2 = binary("hello byebye".getBytes("UTF-8"));
        ErlBinary b1Copy = binary(b1.getBuffer(false));

        assertEquals(b1.getBuffer(false).length, 5);
        assertTrue(Arrays.equals(b1.getBuffer(false), new byte[]{
                (byte)0xaa,0x55,0x66,(byte)0x99,(byte)0x88}));

        assertEquals(empty.getBuffer(false).length, 0);
        assertTrue(Arrays.equals(empty.getBuffer(false), new byte[]{}));

        assertFalse(b1.equals(empty));
        assertFalse(b1.equals(b2));
        assertEquals(b1, b1Copy);

        assertEquals("hello byebye", new String(b2.getBuffer(false), "UTF-8"));

        assertTrue(Arrays.equals(b1.getBuffer(true), b1.getBuffer(false)));
        assertFalse(b1.getBuffer(true) == b1.getBuffer(false));
        assertTrue(Arrays.equals(b1.getBuffer(true), b1.getBuffer(false)));

    }

    public void testIntegers() throws Exception
    {
        ErlNumber n0 = number(0);
        ErlNumber n1 = number(1);
        ErlNumber n2 = number(1.0);
        ErlNumber n3 = number(105L);
        ErlNumber n4 = number(1);

        assertEquals(n0, number(0));
        assertEquals(n1, number(1));
        assertEquals(n2, number(1.0));

	assertTrue(n1 instanceof ErlInteger);
	assertTrue(n2 instanceof ErlFloat);
	assertTrue(n3 instanceof ErlLong);
        assertEquals(((ErlLong)n3).getValue(), 105);
    }

    public void testFloats() throws Exception
    {
        ErlNumber n0 = number(0.0);
        ErlNumber n1 = number(1.0);
        ErlNumber n2 = number(105.5555);
        ErlNumber n3 = number(105.5555);

        assertEquals(n0, number(0.0));
        assertEquals(n0, number(0));
        assertEquals(n1, number(1));
        assertEquals(n1, number(1.0));
        assertEquals(n2, number(105.5555));
        assertFalse(n2.equals(number(105)));
        assertFalse(n2.equals(number(106)));
        assertFalse(number(105).equals(n2));
        assertFalse(number(106).equals(n2));
        assertFalse(n2.equals(number(1.0)));
    }

    public void testLists() throws Exception
    {
        ErlList l1 = list(1, 2, "hello", tuple(atom("byebye"), atom("xxx"), 2, 3));
        ErlList l2 = list(1, 2, "hello", tuple(atom("byebye"), atom("xxx"), 2, 3));
        ErlList l3 = list(1, "hello", tuple(atom("byebye"), atom("xxx"), 2, 3));

        assertEquals(l1, l2);
        assertFalse(l1.equals(l3));

        ErlTerm[] terms1 = new ErlTerm[]{
                number(1), number(2), list("hello"), tuple(atom("byebye"), atom("xxx"), 2, 3)
        };

        int i = 0;
        for (ErlTerm it: l1) {
            assertEquals(it, terms1[i++]);
        }

        assertEquals(l1.hd(), number(1));
        assertEquals(l1.tl().hd(), number(2));
        assertEquals(l1.tl().tl().hd(), list("hello"));
        assertEquals(l1.tl().tl().tl().hd(), tuple(atom("byebye"), atom("xxx"), 2, 3));
    }

    public void testListIteration() throws Exception {
	StringBuilder b = new StringBuilder();
        ErlList l1 = list(1, atom("a_atom"));
	ErlList l2 = list("ca\u00e7\u00e3o");
	int l2bytes[] = new int[l2.size()];
	int l2bytes_i = 0;
	int l2cmp[] = new int[]{0x63, 0x61, 0xc3, 0xa7, 0xc3, 0xa3, 0x6f};
	ErlList l3 = list(new byte[]{1,2,3});

	// terms

	Iterator<ErlTerm> i = l1.iterator();
	while (i.hasNext()) {
	    ErlTerm t = i.next();
	    if (t instanceof ErlInteger) {
		b.append(((ErlInteger)t).getValue());
	    } else if (t instanceof ErlAtom) {
		b.append(((ErlAtom)t).getValue());
	    } else {
		throw new Exception("exhaustion");
	    }
	}

	assertEquals("1a_atom", b.toString());

	b.setLength(0);

	// string

	i = l2.iterator();
	while (i.hasNext()) {
	    ErlTerm t = i.next();
	    assertTrue(t instanceof ErlInteger);
	    l2bytes[l2bytes_i++] = (int)((ErlInteger)t).getValue();
	}

	assertTrue(Arrays.equals(l2bytes, l2cmp));

	// byte array
	i = l3.iterator();
	assertEquals(((ErlInteger)(i.next())).getValue(), 1);
	assertEquals(((ErlInteger)(i.next())).getValue(), 2);
	assertEquals(((ErlInteger)(i.next())).getValue(), 3);
    }

    public void _testRefs() throws Exception {
        /*
        OtpErlangRef or1 = new OtpErlangRef("xxx", 12345, 0);
        OtpOutputStream os1 = new OtpOutputStream(200);
        or1.encode(os1);

        OtpErlangRef or2 = new OtpErlangRef("xxx", 23456, 0);
        OtpOutputStream os2 = new OtpOutputStream(200);
        or2.encode(os2);


        ErlRef r1 = new ErlRefImpl(os1.toByteArray());
        ErlRef r2 = new ErlRefImpl(os2.toByteArray());
        ErlRef r3 = new ErlRefImpl(os1.toByteArray());

        assertFalse(or1.equals(or2));
        assertFalse(r1.equals(r2));
        org.junit.Assert.assertEquals(r1, r3);
          */
    }

    // TODO: Test Tuples.
    public void _testTuples() throws Exception
    {
        //fail("tuple unit tests not implemented");
    }
}
