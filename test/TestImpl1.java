package test;

import erl.*;
import erl.impl.ErlAtomImpl;
import erl.impl.ErlBinaryImpl;
import erl.impl.ErlRefImpl;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import junit.framework.TestCase;

import java.util.Arrays;

/**
 * Basic tests for ErlTerm implementations.
 */
public class TestImpl1 extends TestCase {

    public void testAtoms() {
        ErlAtom a1 = new ErlAtomImpl("abc");
        ErlAtom a2 = new ErlAtomImpl("xyz");

        org.junit.Assert.assertEquals(a1.getValue(), "abc");
        org.junit.Assert.assertEquals(a2.getValue(), "xyz");

        org.junit.Assert.assertEquals(a1, a1);
        assertFalse(a1.equals(a2));
        org.junit.Assert.assertEquals(a1, new ErlAtomImpl("abc"));
    }

    public void testBinaries() throws Exception {
        ErlBinary b1 = new ErlBinaryImpl(new byte[]{
                (byte)0xaa,0x55,0x66,(byte)0x99,(byte)0x88});
        ErlBinary empty = new ErlBinaryImpl(new byte[]{});
        ErlBinary b2 = new ErlBinaryImpl("hello byebye".getBytes("ISO-8859-1"));
        ErlBinary b1Copy = new ErlBinaryImpl(b1.getBuffer(false));

        org.junit.Assert.assertEquals(b1.getBuffer(false).length, 5);
        assertTrue(Arrays.equals(b1.getBuffer(false), new byte[]{
                (byte)0xaa,0x55,0x66,(byte)0x99,(byte)0x88}));

        org.junit.Assert.assertEquals(empty.getBuffer(false).length, 0);
        assertTrue(Arrays.equals(empty.getBuffer(false), new byte[]{}));

        assertFalse(b1.equals(empty));
        assertFalse(b1.equals(b2));
        org.junit.Assert.assertEquals(b1, b1Copy);

        assertEquals("hello byebye", new String(b2.getBuffer(false), "ISO-8859-1"));

        assertTrue(Arrays.equals(b1.getBuffer(true), b1.getBuffer(false)));
        assertFalse(b1.getBuffer(true) == b1.getBuffer(false));
        assertTrue(Arrays.equals(b1.getBuffer(true), b1.getBuffer(false)));

    }

    public void testString() {
	ErlList l1 = ET.list("hello world of real possibilities!");
    }

    public void _testLists() throws Exception {
        ErlList l1 = ET.list(1, 2, "hello", ET.tuple(ET.atom("byebye"), ET.atom("xxx"), 2, 3));
        ErlList l2 = ET.list(1, 2, "hello", ET.tuple(ET.atom("byebye"), ET.atom("xxx"), 2, 3));
        ErlList l3 = ET.list(1, "hello", ET.tuple(ET.atom("byebye"), ET.atom("xxx"), 2, 3));

        org.junit.Assert.assertEquals(l1, l2);
        assertFalse(l1.equals(l3));

        ErlTerm[] terms1 = new ErlTerm[]{
                ET.number(1), ET.number(2), ET.list("hello"), ET.tuple(ET.atom("byebye"), ET.atom("xxx"), 2, 3)
        };

        int i = 0;
        for (ErlTerm it: l1) {
            org.junit.Assert.assertEquals(it, terms1[i++]);
        }

        org.junit.Assert.assertEquals(l1.hd(), ET.number(1));
        org.junit.Assert.assertEquals(l1.tl().hd(), ET.number(2));
        org.junit.Assert.assertEquals(l1.tl().tl().hd(), ET.list("hello"));
        org.junit.Assert.assertEquals(l1.tl().tl().tl().hd(), ET.tuple(ET.atom("byebye"), ET.atom("xxx"), 2, 3));
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
    public void _testTuples() throws Exception {
        //fail("tuple unit tests not implemented");
    }
}
