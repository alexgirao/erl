/* these are just basic, a priori assumptions
 */

package test;

import junit.framework.TestCase;

import erl.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestBasics extends TestCase
{
    public void testCastToByte() {
	short s = 0xff; /* cast int to short */
	int i = 0xff;
	assertTrue(s > 0);
	assertTrue(i > 0);
	assertEquals((byte)s, (byte)-1);
	assertEquals((byte)i, (byte)-1);
	assertEquals((byte)0xff, (byte)-1); /* likewise, cast from constant */
    }
    public void testLiteralByte() {
	byte i = (byte)0xff;  /* complaint without explicit cast: possible loss of precision */
	byte j = (byte)255;   /* consistent with the hexadecimal literal */
	byte k = -1;
	assertEquals(i,j);
	assertEquals(i,k);
    }
    public void testLiteralShort() {
	short i = (short)0xffff;  /* complaint without explicit cast: possible loss of precision */
	short j = (short)65535L;  /* consistent with the hexadecimal literal */
	short k = -1;
	assertEquals(i,j);
	assertEquals(i,k);
    }
    public void testLiteralIntA() {
	int i = (int)0xffffffffL; /* complaint without explicit cast: possible loss of precision */
	int j = (int)4294967295L; /* consistent with the hexadecimal literal */
	int k = -1;
	assertEquals(i,j);
	assertEquals(i,k);
    }
    public void testLiteralIntB() {
	int i = 0xffffffff; /* no complaint and no need to cast (it is an int already), even though
			     * 0xffffffff = 4294967295. I guess practical use was decisive to such
			     * allowance */
	int j = (int)4294967295L; /* inconsistent, since can't use literal 4294967295 as with the
				   * hexadecimal case */
	int k = -1;
	assertEquals(i,j);
	assertEquals(i,k);
    }
    public void testNegate() {
	/* test two's complement negative integers */
	assertEquals((byte)0xCA, -54);
	assertEquals((short)0xCAFE, -13570);
	assertEquals(0xCAFEBABE, -889275714);
	/* how to find the negative integer when the sign bit is set */
	assertEquals((byte)0xCA,    -((~0xCA        &        0xFF) + 1));
	assertEquals((short)0xCAFE, -((~0xCAFE      &      0xFFFF) + 1));
	assertEquals(0xCAFEBABE,    -((~0xCAFEBABEL & 0xFFFFFFFFL) + 1));
	assertEquals(0xCAFEBABE,    -(~0xCAFEBABE + 1));
    }
    public void testPromotion() {
	/* in two's complement systems (i.e., all modern systems), type
	 * promotion just fills the left bits with the sign bit */
	byte b = (byte)0xca;
	assertEquals(0xca << 8, 0xca00); /* only shift (0xca is already an integer) */
	assertEquals(b << 8, 0xffffca00); /* promote and shift, a common mistake */
	assertEquals((b & 0xff) << 8, 0xca00); /* promote, filter and shift */
	assertEquals((int)b, (0xca | ~0xff)); /* cpu/compiler promotion and manual promotion */
    }
}
