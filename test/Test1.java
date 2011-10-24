package test;

import junit.framework.TestCase;

import erl.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static erl.ET.list;
import static erl.ET.atom;
import static erl.ET.binary;

import java.util.Arrays;

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
        ErlNumber n0 = ET.number(0);
        ErlNumber n1 = ET.number(1);
        ErlNumber n2 = ET.number(1.0);
        ErlNumber n3 = ET.number(105);
        ErlNumber n4 = ET.number(1);

        assertEquals(n0, ET.number(0));
        assertEquals(n1, ET.number(1));
        assertEquals(n2, ET.number(1.0));
        assertEquals(n3.getLongValue(), 105);
    }

    public void testFloats() throws Exception
    {
        ErlNumber n0 = ET.number(0.0);
        ErlNumber n1 = ET.number(1.0);
        ErlNumber n2 = ET.number(105.5555);
        ErlNumber n3 = ET.number(105.5555);

        assertEquals(n0, ET.number(0.0));
        assertEquals(n0, ET.number(0));
        assertEquals(n1, ET.number(1));
        assertEquals(n1, ET.number(1.0));
        assertEquals(n2, ET.number(105.5555));
        assertFalse(n2.equals(ET.number(105)));
        assertFalse(n2.equals(ET.number(106)));
        assertFalse(ET.number(105).equals(n2));
        assertFalse(ET.number(106).equals(n2));
        assertFalse(n2.equals(ET.number(1.0)));
    }

    public void testLists() throws Exception
    {
        ErlList l1 = ET.list(1, 2, "hello", ET.tuple(ET.atom("byebye"), ET.atom("xxx"), 2, 3));
        ErlList l2 = ET.list(1, 2, "hello", ET.tuple(ET.atom("byebye"), ET.atom("xxx"), 2, 3));
        ErlList l3 = ET.list(1, "hello", ET.tuple(ET.atom("byebye"), ET.atom("xxx"), 2, 3));

        assertEquals(l1, l2);
        assertFalse(l1.equals(l3));

        ErlTerm[] terms1 = new ErlTerm[]{
                ET.number(1), ET.number(2), ET.list("hello"), ET.tuple(ET.atom("byebye"), ET.atom("xxx"), 2, 3)
        };

        int i = 0;
        for (ErlTerm it: l1) {
            assertEquals(it, terms1[i++]);
        }

        assertEquals(l1.hd(), ET.number(1));
        assertEquals(l1.tl().hd(), ET.number(2));
        assertEquals(l1.tl().tl().hd(), ET.list("hello"));
        assertEquals(l1.tl().tl().tl().hd(), ET.tuple(ET.atom("byebye"), ET.atom("xxx"), 2, 3));
    }

    public void _testRefs() throws Exception
    {
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
