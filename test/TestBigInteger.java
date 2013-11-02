/* BigInteger and two's complement interplay
 */

package test;

import junit.framework.TestCase;

import erl.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;
import static java.math.BigInteger.ONE;

public class TestBigInteger extends TestCase
{
    /* bytes to decimal string */
    private static String btod(int ... ints) {
        byte[] bytes = new byte[ints.length];
        for (int i = 0, j = ints.length, v, mag; i < j; i++) {
	    v = ints[i];
	    if ((v & 0xff) != v) {
		/* not in [0,255] */
		mag = v < 0 ? ~v : v;
		if ((mag & 0x7f) != mag) {
		    /* not in [-128,127] */
		    throw new IllegalArgumentException("magnitude greater than 7-bit");
		}
	    }
	    /* accept [-128,255] with [128,255] mapping to
	     * [-128,-1]
	     */
	    bytes[i] = (byte)v;
        }
	return (new BigInteger(bytes)).toString();
    }
    private static int[] not(int ... ints) {
        for (int i = 0, j = ints.length; i < j; i++) {
	    ints[i] = ~ints[i] & 0xff;
	}
	return ints;
    }
    public void testByteToDecimalString() throws java.io.IOException
    {
	/* does BigInteger ignore *extra* leading zeroes? this also shows that BigInteger uses big
	 * endian order */
	assertEquals(btod(0,0,0,0,0,0,0,0,0,0,1),"1");
	assertEquals(btod(0,0,0,0,0,0,0,0,0,1),"1");
	assertEquals(btod(0,0,0,0,0,0,0,0,1),"1");
	assertEquals(btod(0,0,0,0,0,0,0,1),"1");
	assertEquals(btod(0,0,0,0,0,0,1),"1");
	assertEquals(btod(0,0,0,0,0,1),"1");
	assertEquals(btod(0,0,0,0,1),"1");
	assertEquals(btod(0,0,0,1),"1");
	assertEquals(btod(0,0,1),"1");
	assertEquals(btod(0,1),"1");

	/* must fail */
	try { btod(-130); assertTrue(false); } catch (IllegalArgumentException e) {}
	try { btod(-129); assertTrue(false); } catch (IllegalArgumentException e) {}

	/* [-128,127] */
	assertEquals(btod(-128),"-128");
	assertEquals(btod(-2),"-2");
	assertEquals(btod(-1),"-1");
	assertEquals(btod(1),"1");
	assertEquals(btod(2),"2");
	assertEquals(btod(127),"127");

	/* [128,255] map to [-128,-1] */
	assertEquals(btod(128),"-128");
	assertEquals(btod(129),"-127");
	assertEquals(btod(254),"-2");
	assertEquals(btod(255),"-1");

	/* must fail */
	try { btod(256); assertTrue(false); } catch (IllegalArgumentException e) {}
	try { btod(257); assertTrue(false); } catch (IllegalArgumentException e) {}
    }
    public void testSign() {
	/* no surprise here, the most significant bit on the most significant
	 * byte determines the integer sign in two's complement notation, the
	 * same strategy used by modern processors
	 */
	assertEquals(btod(  0, 255), Integer.toString(  0xff));
	assertEquals(btod(127, 255), Integer.toString(0x7fff));
	assertEquals(btod(128, 255), Integer.toString(-((~0x80ff & 0xffff) + 1)));
	assertEquals(btod(255, 255), Integer.toString(-((~0xffff & 0xffff) + 1)));
	assertEquals(btod(0x7f),  "127");
	assertEquals(btod(0x80), "-128");
	assertEquals(btod(0xff),   "-1");
    }
    /* test large integers and two's complement of large integers */
    public void testLarge() throws java.io.IOException
    {
	/* 1 << 234 = 27606985387162255149739023449108101809804435888681546220650096895197184
	 *
	 * v = 9876543210
	 * (((((v) * 10^10 + v) * 10^10 + v) * 10^10 + v) * 10^10 + v) * 10^10 + v =
	 *     987654321098765432109876543210987654321098765432109876543210
	 */

	BigInteger a = new BigInteger("27606985387162255149739023449108101809804435888681546220650096895197184");
	BigInteger b = new BigInteger("987654321098765432109876543210987654321098765432109876543210");

	int[] ia = new int[]{4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	int[] ib = new int[]{0,157,87,168,63,51,237,184,212,204,81,7,228,243,211,44,203,150,53,30,103,215,81,198,126,234};

	/* test btod() with positive integer */
	assertEquals(a.toString(), btod(ia));
	assertEquals(b.toString(), btod(ib));

	/* test BigInteger NOT operator and two's complement internal representation */
	assertEquals(b.negate().toString(), "-" + b.toString());
	assertEquals(b.negate().toString(), b.not().add(ONE).toString());

	/* test not() and btod() with negative integer */
	assertEquals(b.not().toString(), btod(not(ib)));
    }
}
