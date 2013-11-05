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
    /* reference: TestBasics.java
     */
    public void testBitLengthVsBitCount() {
	/* 1 = 0b01 */
	assertEquals(BigInteger.valueOf(1).bitLength(), 1);
	assertEquals(BigInteger.valueOf(1).bitCount(),  1);
	/* 2 = 0b010 */
	assertEquals(BigInteger.valueOf(2).bitLength(), 2);
	assertEquals(BigInteger.valueOf(2).bitCount(),  1);
	/* 3 = 0b011 */
	assertEquals(BigInteger.valueOf(3).bitLength(), 2);
	assertEquals(BigInteger.valueOf(3).bitCount(),  2);
	/* -1 = 0b11, mag = 0b0 */
	assertEquals(BigInteger.valueOf(-1).bitLength(), 0);
	assertEquals(BigInteger.valueOf(-1).bitCount(),  0);
	/* -2 = 0b110, mag = 0b1 */
	assertEquals(BigInteger.valueOf(-2).bitLength(), 1);
	assertEquals(BigInteger.valueOf(-2).bitCount(),  1);
	/* -3 = 0b101, mag = 0b10 */
	assertEquals(BigInteger.valueOf(-3).bitLength(), 2);
	assertEquals(BigInteger.valueOf(-3).bitCount(),  1);

	/* bitLength for positive numbers follow the documentation:
	 * "equivalent to the number of bits in the ordinary binary
	 * representation", for negative numbers it seems to be the
	 * same quote now applied to the two's complement magnitude,
	 * i.e. ~value
	 */

	/* 31 = 0b011111 */
	assertEquals(BigInteger.valueOf(31).bitLength(), 5);
	assertEquals(BigInteger.valueOf(31).bitCount(),  5);
	/* 32 = 0b0100000 */
	assertEquals(BigInteger.valueOf(32).bitLength(), 6);
	assertEquals(BigInteger.valueOf(32).bitCount(),  1);

	/* now there is a very strong clue that bitCount is the number
	 * of bits that are TRUE for positive values, now the
	 * documentation makes sense: "number of bits in the two's
	 * complement representation that differ from its sign bit"
	 */

	/* -31 = 0b100001, mag = 0b11110 */
	assertEquals(BigInteger.valueOf(-31).bitLength(), 5);
	assertEquals(BigInteger.valueOf(-31).bitCount(),  4);
	/* -32 = 0b1100000, mag = 0b0011111 */
	assertEquals(BigInteger.valueOf(-32).bitLength(), 5);
	assertEquals(BigInteger.valueOf(-32).bitCount(),  5);

	/* ... bitCount turns out to be straightforward and can be counted
	 * independently of the sign
	 */
	assertEquals(BigInteger.valueOf(Integer.MIN_VALUE).bitLength(), 31);
	assertEquals(BigInteger.valueOf(Integer.MAX_VALUE).bitLength(), 31);

	assertEquals(BigInteger.valueOf(Integer.MIN_VALUE - 1L).bitLength(), 32);
	assertEquals(BigInteger.valueOf(Integer.MAX_VALUE + 1L).bitLength(), 32);

	/* ... as expected.
	 */
    }
}
