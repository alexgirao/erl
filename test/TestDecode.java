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

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import java.util.Iterator;

import java.math.BigInteger;
import static java.math.BigInteger.ONE;

public class TestDecode extends TestCase
{
    /* "The largest and smallest value that can be encoded as an
     * integer (ERL_INTEGER_EXT)"
     *
     * reference: otp_src_R14B04/lib/erl_interface/include/ei.h
     *
     * note: the statement above is not true, ERL_INTEGER_EXT can hold
     * [-(1 << 31), (1 << 31) - 1], as is shown below and tested
     * against Erlang VM externally.
     */
    public static final int ERL_MAX = (1 << 27) - 1;
    public static final int ERL_MIN = -(1 << 27);

    public void testNumber() {
	ErlList root =
	    list(
		 //
		 number(0),
		 number(1),
		 number(2),
		 //
		 number(-1),
		 number(-2),
		 //
		 number(ERL_MIN + 1),
		 number(ERL_MIN),
		 number(ERL_MIN - 1),
		 number(ERL_MAX - 1),
		 number(ERL_MAX),
		 number(ERL_MAX + 1),
		 // 
		 number(-(1L << 31) + 1),
		 number(-(1L << 31)),
		 number(-(1L << 31) - 1), // ERL_SMALL_BIG_EXT, negative beyond 31-bit
		 //
		 number(((1L << 31) - 1) - 1),
		 number(((1L << 31) - 1)),
		 number(((1L << 31) - 1) + 1), // ERL_SMALL_BIG_EXT, positive beyond 31-bit
		 //
		 number(0xffffffffL),
		 number(-0xffffffffL),
		 //
		 number(0xffffffffL + 1),
		 number(-(0xffffffffL + 1))
		 );

	ByteBuffer buf = ByteBuffer.allocate(1024);
	buf.put((byte)ErlTerm.ERL_VERSION_MAGIC);
	ET.encode(buf, root);

	/* produce -> consume
	 */
	buf.flip();

	/*
	 */

	ErlTerm e = ET.decode(buf);

	assertTrue(e instanceof ErlListTerms);

	Iterator<ErlTerm> i = ((ErlListTerms)e).iterator();

	ErlTerm t = i.next();
	assertTrue(t instanceof ErlInteger);
	assertEquals(((ErlInteger)t).getValue(), 0);

	t = i.next();
	assertTrue(t instanceof ErlInteger);
	assertEquals(((ErlInteger)t).getValue(), 1);

	t = i.next();
	assertTrue(t instanceof ErlInteger);
	assertEquals(((ErlInteger)t).getValue(), 2);

	t = i.next();
	assertTrue(t instanceof ErlInteger);
	assertEquals(((ErlInteger)t).getValue(), -1);

	t = i.next();
	assertTrue(t instanceof ErlInteger);
	assertEquals(((ErlInteger)t).getValue(), -2);

	//

	t = i.next();
	assertTrue(t instanceof ErlInteger);
	assertEquals(((ErlInteger)t).getValue(), ERL_MIN + 1);

	t = i.next();
	assertTrue(t instanceof ErlInteger);
	assertEquals(((ErlInteger)t).getValue(), ERL_MIN);

	t = i.next();
	assertTrue(t instanceof ErlInteger);
	assertEquals(((ErlInteger)t).getValue(), ERL_MIN - 1);

	t = i.next();
	assertTrue(t instanceof ErlInteger);
	assertEquals(((ErlInteger)t).getValue(), ERL_MAX - 1);

	t = i.next();
	assertTrue(t instanceof ErlInteger);
	assertEquals(((ErlInteger)t).getValue(), ERL_MAX);

	t = i.next();
	assertTrue(t instanceof ErlInteger);
	assertEquals(((ErlInteger)t).getValue(), ERL_MAX + 1);

	//

	t = i.next();
	assertTrue(t instanceof ErlInteger);
	assertEquals(-(1L << 31) + 1, ((ErlInteger)t).getValue());

	t = i.next();
	assertTrue(t instanceof ErlInteger);
	assertEquals(-(1L << 31), ((ErlInteger)t).getValue());

	t = i.next();
	assertTrue(t instanceof ErlLong);
	assertEquals(-(1L << 31) - 1, ((ErlLong)t).getValue());

	//

	t = i.next();
	assertTrue(t instanceof ErlInteger);
	assertEquals(((1L << 31) - 1) - 1, ((ErlInteger)t).getValue());

	t = i.next();
	assertTrue(t instanceof ErlInteger);
	assertEquals(((1L << 31) - 1), ((ErlInteger)t).getValue());

	t = i.next();
	assertTrue(t instanceof ErlLong);
	assertEquals(((1L << 31) - 1) + 1, ((ErlLong)t).getValue());

	//

	t = i.next();
	assertTrue(t instanceof ErlLong);
	assertEquals(0xffffffffL, ((ErlLong)t).getValue());

	t = i.next();
	assertTrue(t instanceof ErlLong);
	assertEquals(-0xffffffffL, ((ErlLong)t).getValue());

	//

	t = i.next();
	assertTrue(t instanceof ErlLong);
	assertEquals(0xffffffffL + 1, ((ErlLong)t).getValue());

	t = i.next();
	assertTrue(t instanceof ErlLong);
	assertEquals(-(0xffffffffL + 1), ((ErlLong)t).getValue());

	//

	assertFalse(i.hasNext());
	assertEquals(buf.remaining(), 0);
    }

    public void testB() {
	ErlList root =
	    list(
		 number(1),
		 number(1.618034),
		 atom("a_atom"),
		 list("a_string"),
		 atom("true"),
		 atom("false"),
		 tuple(
		       atom("a_atom"),
		       number(1),
		       number(1.618034),
		       list("a_string")
		       )
		 );

	ByteBuffer buf = ByteBuffer.allocate(1024);
	buf.put((byte)ErlTerm.ERL_VERSION_MAGIC);
	ET.encode(buf, root);

	/* produce -> consume
	 */
	buf.flip();

	/*
	 */

	ErlTerm e = ET.decode(buf);

	assertTrue(e instanceof ErlListTerms);

	Iterator<ErlTerm> i = ((ErlListTerms)e).iterator();

	ErlTerm t = i.next();
	assertTrue(t instanceof ErlInteger);
	assertEquals(1, ((ErlInteger)t).getValue());

	t = i.next();
	assertTrue(t instanceof ErlFloat);
	assertEquals(1.618034, ((ErlFloat)t).getValue(), 0.0000001 /* epsilon */);

	t = i.next();
	assertTrue(t instanceof ErlAtom);
	assertEquals("a_atom", ((ErlAtom)t).getValue());

	t = i.next();
	assertTrue(t instanceof ErlListString);
	assertEquals("a_string", ((ErlListString)t).getValue());

	t = i.next();
	assertTrue(t instanceof ErlAtom);
	assertEquals("true", ((ErlAtom)t).getValue());
	assertTrue(((ErlAtom)t).isTrue());

	t = i.next();
	assertTrue(t instanceof ErlAtom);
	assertEquals("false", ((ErlAtom)t).getValue());
	assertTrue(((ErlAtom)t).isFalse());

	t = i.next();
	assertTrue(t instanceof ErlTuple);
	assertEquals(4, ((ErlTuple)t).arity());

	ErlTerm ti = ((ErlTuple)t).element(0);
	assertTrue(ti instanceof ErlAtom);
	ti = ((ErlTuple)t).element(1);
	assertTrue(ti instanceof ErlInteger);
	ti = ((ErlTuple)t).element(2);
	assertTrue(ti instanceof ErlFloat);
	ti = ((ErlTuple)t).element(3);
	assertTrue(ti instanceof ErlListString);

	assertFalse(i.hasNext());
	assertEquals(buf.remaining(), 0);
    }

    public void testSmallBigPoorChoice() {
	/* some of the values encoded below with ERL_SMALL_BIG_EXT
	 * could be represented with ERL_INTEGER_EXT
	 */

	ByteBuffer buf = ByteBuffer.allocate(1024);

	/* positive tests */

	buf.put(ErlTerm.ERL_SMALL_BIG_EXT);
	buf.put((byte)4); // arity
	buf.put((byte)0); // sign
	buf.put(new byte[]{-2,-1,-1,127}); // absolute value, little-endian

	/* 0x7fffffff is the largest positive value, it is not
	 * possible to go beyond without crossing the sign bit */
	buf.put(ErlTerm.ERL_SMALL_BIG_EXT);
	buf.put((byte)4); // arity
	buf.put((byte)0); // sign
	buf.put(new byte[]{-1,-1,-1,127}); // absolute value, little-endian

	/* 0x80000000 requires 33-bit, 32 for the magnitude and 1 for
	 * the sign, so this will be put in a 64-bit integer (long) */
	buf.put(ErlTerm.ERL_SMALL_BIG_EXT);
	buf.put((byte)4); // arity
	buf.put((byte)0); // sign
	buf.put(new byte[]{0,0,0,-128}); // absolute value, little-endian

	/* negative tests */

	buf.put(ErlTerm.ERL_SMALL_BIG_EXT);
	buf.put((byte)4); // arity
	buf.put((byte)1); // sign
	buf.put(new byte[]{-2,-1,-1,127}); // absolute value, little-endian

	buf.put(ErlTerm.ERL_SMALL_BIG_EXT);
	buf.put((byte)4); // arity
	buf.put((byte)1); // sign
	buf.put(new byte[]{-1,-1,-1,127}); // absolute value, little-endian

	/* 0x80000000 is not -0, but the least negative value in two's
	 * complement */
	buf.put(ErlTerm.ERL_SMALL_BIG_EXT);
	buf.put((byte)4); // arity
	buf.put((byte)1); // sign
	buf.put(new byte[]{0,0,0,-128}); // absolute value, little-endian

	/* 0x80000001 is closer to negative infinity in the absolute context of
	 * ERL_SMALL_BIG_EXT, but is closer to zero in two's complement, so it
	 * can't be represented within a 32-bit integer */
	buf.put(ErlTerm.ERL_SMALL_BIG_EXT);
	buf.put((byte)4); // arity
	buf.put((byte)1); // sign
	buf.put(new byte[]{1,0,0,-128}); // absolute value, little-endian

	/* produce -> consume
	 */
	buf.flip();

	/* positive tests */

	ErlTerm e = ET.decode(buf);
	assertTrue(e instanceof ErlInteger);
	assertEquals(((ErlInteger)e).getValue(), 0x7fffffff - 1);
	assertEquals(((ErlInteger)e).getValue(), Integer.MAX_VALUE - 1);

	e = ET.decode(buf);
	assertTrue(e instanceof ErlInteger);
	assertEquals(((ErlInteger)e).getValue(), 0x7fffffff);
	assertEquals(((ErlInteger)e).getValue(), 2147483647);
	assertEquals(((ErlInteger)e).getValue(), Integer.MAX_VALUE);

	e = ET.decode(buf);
	assertTrue(e instanceof ErlLong);
	assertEquals(((ErlLong)e).getValue(), 0x80000000L);
	assertEquals(((ErlLong)e).getValue(), 2147483648L);
	assertEquals(((ErlLong)e).getValue(), Integer.MAX_VALUE + 1L);

	/* negative tests */

	e = ET.decode(buf);
	assertTrue(e instanceof ErlInteger);
	assertEquals(((ErlInteger)e).getValue(), -(0x7fffffff - 1));
	assertEquals(((ErlInteger)e).getValue(), Integer.MIN_VALUE + 2);

	e = ET.decode(buf);
	assertTrue(e instanceof ErlInteger);
	assertEquals(((ErlInteger)e).getValue(), -0x7fffffff);
	assertEquals(((ErlInteger)e).getValue(), -2147483647);
	assertEquals(((ErlInteger)e).getValue(), Integer.MIN_VALUE + 1);

	e = ET.decode(buf);
	assertTrue(e instanceof ErlInteger);
	assertEquals(((ErlInteger)e).getValue(), -0x7fffffff - 1);
	assertEquals(((ErlInteger)e).getValue(), 0x80000000);
	assertEquals(((ErlInteger)e).getValue(), -2147483648);
	assertEquals(((ErlInteger)e).getValue(), Integer.MIN_VALUE);
	assertEquals(((ErlInteger)e).getValue(), (int)0x80000000L);

	e = ET.decode(buf);
	assertTrue(e instanceof ErlLong);
	assertEquals(((ErlLong)e).getValue(), -0x80000001L);
	assertEquals(((ErlLong)e).getValue(), -2147483649L);
	assertEquals(((ErlLong)e).getValue(), Integer.MIN_VALUE - 1L);

	assertEquals(buf.remaining(), 0);
    }

    public void testSmallBig5() {
	ByteBuffer buf = ByteBuffer.allocate(1024);

	buf.put(ErlTerm.ERL_SMALL_BIG_EXT);
	buf.put((byte)5); // arity
	buf.put((byte)0); // sign
	buf.put(new byte[]{-1,-1,-1,-1,-1}); // absolute value, little-endian

	buf.put(ErlTerm.ERL_SMALL_BIG_EXT);
	buf.put((byte)5); // arity
	buf.put((byte)1); // sign
	buf.put(new byte[]{-1,-1,-1,-1,-1}); // absolute value, little-endian

	/* produce -> consume
	 */
	buf.flip();

	ErlTerm e = ET.decode(buf);
	assertTrue(e instanceof ErlLong);
	assertEquals(((ErlLong)e).getValue(), 0xffffffffffL);

	e = ET.decode(buf);
	assertTrue(e instanceof ErlLong);
	assertEquals(((ErlLong)e).getValue(), -0xffffffffffL);

	assertEquals(buf.remaining(), 0);
    }

    public void testSmallBig6() {
	ByteBuffer buf = ByteBuffer.allocate(1024);

	buf.put(ErlTerm.ERL_SMALL_BIG_EXT);
	buf.put((byte)6); // arity
	buf.put((byte)0); // sign
	buf.put(new byte[]{-1,-1,-1,-1,-1,-1}); // absolute value, little-endian

	buf.put(ErlTerm.ERL_SMALL_BIG_EXT);
	buf.put((byte)6); // arity
	buf.put((byte)1); // sign
	buf.put(new byte[]{-1,-1,-1,-1,-1,-1}); // absolute value, little-endian

	/* produce -> consume
	 */
	buf.flip();

	ErlTerm e = ET.decode(buf);
	assertTrue(e instanceof ErlLong);
	assertEquals(((ErlLong)e).getValue(), 0xffffffffffffL);

	e = ET.decode(buf);
	assertTrue(e instanceof ErlLong);
	assertEquals(((ErlLong)e).getValue(), -0xffffffffffffL);

	assertEquals(buf.remaining(), 0);
    }

    public void testSmallBig7() {
	ByteBuffer buf = ByteBuffer.allocate(1024);

	buf.put(ErlTerm.ERL_SMALL_BIG_EXT);
	buf.put((byte)7); // arity
	buf.put((byte)0); // sign
	buf.put(new byte[]{-1,-1,-1,-1,-1,-1,-1}); // absolute value, little-endian

	buf.put(ErlTerm.ERL_SMALL_BIG_EXT);
	buf.put((byte)7); // arity
	buf.put((byte)1); // sign
	buf.put(new byte[]{-1,-1,-1,-1,-1,-1,-1}); // absolute value, little-endian

	/* produce -> consume
	 */
	buf.flip();

	ErlTerm e = ET.decode(buf);
	assertTrue(e instanceof ErlLong);
	assertEquals(((ErlLong)e).getValue(), 0xffffffffffffffL);

	e = ET.decode(buf);
	assertTrue(e instanceof ErlLong);
	assertEquals(((ErlLong)e).getValue(), -0xffffffffffffffL);

	assertEquals(buf.remaining(), 0);
    }

    public void testLongBoundary() {
	ByteBuffer buf = ByteBuffer.allocate(1024);

	/* positive tests */

	buf.put(ErlTerm.ERL_SMALL_BIG_EXT);
	buf.put((byte)8); // arity
	buf.put((byte)0); // sign
	buf.put(new byte[]{-2,-1,-1,-1,-1,-1,-1,127}); // absolute value, little-endian

	buf.put(ErlTerm.ERL_SMALL_BIG_EXT);
	buf.put((byte)8); // arity
	buf.put((byte)0); // sign
	buf.put(new byte[]{-1,-1,-1,-1,-1,-1,-1,127}); // absolute value, little-endian

	buf.put(ErlTerm.ERL_SMALL_BIG_EXT);
	buf.put((byte)8); // arity
	buf.put((byte)0); // sign
	buf.put(new byte[]{0,0,0,0,0,0,0,-128}); // absolute value, little-endian

	/* negative tests */

	buf.put(ErlTerm.ERL_SMALL_BIG_EXT);
	buf.put((byte)8); // arity
	buf.put((byte)1); // sign
	buf.put(new byte[]{-2,-1,-1,-1,-1,-1,-1,127}); // absolute value, little-endian

	buf.put(ErlTerm.ERL_SMALL_BIG_EXT);
	buf.put((byte)8); // arity
	buf.put((byte)1); // sign
	buf.put(new byte[]{-1,-1,-1,-1,-1,-1,-1,127}); // absolute value, little-endian

	buf.put(ErlTerm.ERL_SMALL_BIG_EXT);
	buf.put((byte)8); // arity
	buf.put((byte)1); // sign
	buf.put(new byte[]{0,0,0,0,0,0,0,-128}); // absolute value, little-endian

	buf.put(ErlTerm.ERL_SMALL_BIG_EXT);
	buf.put((byte)8); // arity
	buf.put((byte)1); // sign
	buf.put(new byte[]{1,0,0,0,0,0,0,-128}); // absolute value, little-endian

	/* produce -> consume
	 */
	buf.flip();

	/* positive tests */

	ErlTerm e = ET.decode(buf);
	assertTrue(e instanceof ErlLong);
	assertEquals(((ErlLong)e).getValue(), 0x7fffffffffffffffL - 1);
	assertEquals(((ErlLong)e).getValue(), Long.MAX_VALUE - 1);

	e = ET.decode(buf);
	assertTrue(e instanceof ErlLong);
	assertEquals(((ErlLong)e).getValue(), 0x7fffffffffffffffL);
	assertEquals(((ErlLong)e).getValue(), Long.MAX_VALUE);

	e = ET.decode(buf);
	assertTrue(e instanceof ErlBigInteger);
	assertEquals(((ErlBigInteger)e).getValue(),
		     BigInteger.valueOf(Long.MAX_VALUE).add(ONE));

	/* negative tests */

	e = ET.decode(buf);
	assertTrue(e instanceof ErlLong);
	assertEquals(((ErlLong)e).getValue(), -(0x7fffffffffffffffL - 1));
	assertEquals(((ErlLong)e).getValue(), Long.MIN_VALUE + 2);

	e = ET.decode(buf);
	assertTrue(e instanceof ErlLong);
	assertEquals(((ErlLong)e).getValue(), -0x7fffffffffffffffL);
	assertEquals(((ErlLong)e).getValue(), Long.MIN_VALUE + 1);

	e = ET.decode(buf);
	assertTrue(e instanceof ErlLong);
	assertEquals(((ErlLong)e).getValue(), -0x7fffffffffffffffL - 1);
	assertEquals(((ErlLong)e).getValue(), 0x8000000000000000L);
	assertEquals(((ErlLong)e).getValue(), Long.MIN_VALUE);

	e = ET.decode(buf);
	assertTrue(e instanceof ErlBigInteger);
	assertEquals(((ErlBigInteger)e).getValue(),
		     BigInteger.valueOf(Long.MIN_VALUE).subtract(ONE));

	assertEquals(buf.remaining(), 0);
    }

    /* integer array to byte array */
    private static byte[] itob(int ... ints) {
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
	return bytes;
    }

    private static void reverse(byte[] bytes) {
	int i = 0, j = bytes.length;
	while (i < j) {
	    byte tmp = bytes[i];
	    bytes[i++] = bytes[--j];
	    bytes[j] = tmp;
	}
    }

    public void testLarge() {
	/* 1 << 234 = 27606985387162255149739023449108101809804435888681546220650096895197184
	 *
	 * v = 9876543210
	 * (((((v) * 10^10 + v) * 10^10 + v) * 10^10 + v) * 10^10 + v) * 10^10 + v =
	 *     987654321098765432109876543210987654321098765432109876543210
	 */

	BigInteger a = new BigInteger("27606985387162255149739023449108101809804435888681546220650096895197184");
	BigInteger b = new BigInteger("987654321098765432109876543210987654321098765432109876543210");

	byte[] ab = itob(new int[]{4,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0});
	byte[] bb = itob(new int[]{0,157,87,168,63,51,237,184,212,204,81,7,228,243,211,44,203,150,53,30,103,215,81,198,126,234});

	assertEquals(a, new BigInteger(ab));
	assertEquals(b, new BigInteger(bb));

	/* big endian to little endian, as required by ERL_SMALL_BIG_EXT
	 */
	reverse(ab);
	reverse(bb);

	ByteBuffer buf = ByteBuffer.allocate(1024);

	/* positive tests */

	buf.put(ErlTerm.ERL_SMALL_BIG_EXT);
	buf.put((byte)ab.length); // arity
	buf.put((byte)0); // sign
	buf.put(ab); // absolute value, little-endian

	buf.put(ErlTerm.ERL_SMALL_BIG_EXT);
	buf.put((byte)bb.length); // arity
	buf.put((byte)0); // sign
	buf.put(bb); // absolute value, little-endian

	/* negative tests */

	buf.put(ErlTerm.ERL_SMALL_BIG_EXT);
	buf.put((byte)ab.length); // arity
	buf.put((byte)1); // sign
	buf.put(ab); // absolute value, little-endian

	buf.put(ErlTerm.ERL_SMALL_BIG_EXT);
	buf.put((byte)bb.length); // arity
	buf.put((byte)1); // sign
	buf.put(bb); // absolute value, little-endian

	/* produce -> consume
	 */
	buf.flip();

	/* positive tests */

	ErlTerm e = ET.decode(buf);
	assertTrue(e instanceof ErlBigInteger);
	assertEquals(((ErlBigInteger)e).getValue(), a);

	e = ET.decode(buf);
	assertTrue(e instanceof ErlBigInteger);
	assertEquals(((ErlBigInteger)e).getValue(), b);

	/* negative tests */

	e = ET.decode(buf);
	assertTrue(e instanceof ErlBigInteger);
	assertEquals(((ErlBigInteger)e).getValue(), a.negate());

	e = ET.decode(buf);
	assertTrue(e instanceof ErlBigInteger);
	assertEquals(((ErlBigInteger)e).getValue(), b.negate());

	assertEquals(buf.remaining(), 0);
    }
}
