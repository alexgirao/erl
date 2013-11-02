/*
 * TestEncode serialize terms and compare against the expected output,
 * it also output the encoded bytes to file, to be checked externally:
 *
 * erl:
 *   Input = "out/TestEncode-testNumber.out".
 *   Verify = Input ++ ".verify".
 *   case file:read_file(Input) of {ok, Data} -> Data end.
 *   Term = binary_to_term(Data).
 *   file:write_file(Verify, term_to_binary(Term, [{minor_version, 1}])).
 *   halt().
 *
 * sh:
 *   sha1sum out/TestEncode-testNumber.out out/TestEncode-testNumber.out.verify
 *
 */

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

import static erl.ErlTerm.*;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import java.util.Arrays;

public class TestEncode extends TestCase
{
    /* "The largest and smallest value that can be encoded as an
     * integer (ERL_INTEGER_EXT)"
     *
     * reference: otp_src_R14B04/lib/erl_interface/include/ei.h
     *
     * note: the statement above is not true, ERL_INTEGER_EXT can hold
     * [-(1 << 31), (1 << 31) - 1], as is shown below.
     */
    public static final int ERL_MAX = (1 << 27) - 1;
    public static final int ERL_MIN = -(1 << 27);

    public void writeToFile(String filename, ByteBuffer buf, boolean append) throws java.io.IOException
    {
	File file = new File(filename);
	FileChannel ch = new FileOutputStream(file, append).getChannel();
	ch.write(buf);
	ch.close();
    }
    public String getString(ByteBuffer buf, int n) {
	byte b[] = new byte[n];
	buf.get(b);
	try {
	    return new String(b, "ISO-8859-1");
	} catch (java.io.UnsupportedEncodingException e) {
	    throw new RuntimeException("failed to decode \"ISO-8859-1\"");
	}
    }
    public void testNumber() throws java.io.IOException
    {
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

	/* flip buffer to consume (produce -> consume)
	 */

	buf.flip();

	/* serialize to file
	 */

	writeToFile("TestEncode-testNumber.out", buf, false);
	buf.rewind();

	/* validate
	 */

	assertEquals(buf.get(), (byte)ErlTerm.ERL_VERSION_MAGIC);

	byte buf_list_header[] = new byte[5];
	byte buf_small_integer[] = new byte[2];
	byte buf_integer[] = new byte[5];
	byte buf_small_big_4[] = new byte[7]; // tag + size + sign + 4 bytes
	byte buf_small_big_5[] = new byte[8]; // likewise

	buf.get(buf_list_header);
	assertTrue(Arrays.equals(buf_list_header,
				 new byte[]{ERL_LIST_EXT, 0, 0, 0, (byte)root.arity()}));

	// numbers are encoded as little-endian

	buf.get(buf_small_integer);
	assertTrue(Arrays.equals(buf_small_integer,
				 new byte[]{ERL_SMALL_INTEGER_EXT, 0}));
	buf.get(buf_small_integer);
	assertTrue(Arrays.equals(buf_small_integer,
				 new byte[]{ERL_SMALL_INTEGER_EXT, 1}));

	buf.get(buf_small_integer);
	assertTrue(Arrays.equals(buf_small_integer,
				 new byte[]{ERL_SMALL_INTEGER_EXT, 2}));

	//

	buf.get(buf_integer);
	assertTrue(Arrays.equals(buf_integer,
				 new byte[]{ERL_INTEGER_EXT, -1, -1, -1, -1}));

	buf.get(buf_integer);
	assertTrue(Arrays.equals(buf_integer,
				 new byte[]{ERL_INTEGER_EXT, -1, -1, -1, -2}));

	//

	buf.get(buf_integer);
	assertTrue(Arrays.equals(buf_integer,
				 new byte[]{ERL_INTEGER_EXT, (byte)0xf8, 0, 0, 1}));

	buf.get(buf_integer);
	assertTrue(Arrays.equals(buf_integer,
				 new byte[]{ERL_INTEGER_EXT, (byte)0xf8, 0, 0, 0}));

	buf.get(buf_integer);
	assertTrue(Arrays.equals(buf_integer,
				 new byte[]{ERL_INTEGER_EXT, (byte)0xf7, -1, -1, -1}));

	buf.get(buf_integer);
	assertTrue(Arrays.equals(buf_integer,
				 new byte[]{ERL_INTEGER_EXT, (byte)0x07, -1, -1, -2}));

	buf.get(buf_integer);
	assertTrue(Arrays.equals(buf_integer,
				 new byte[]{ERL_INTEGER_EXT, (byte)0x07, -1, -1, -1}));

	buf.get(buf_integer);
	assertTrue(Arrays.equals(buf_integer,
				 new byte[]{ERL_INTEGER_EXT, (byte)0x08, 0, 0, 0}));

	//

	buf.get(buf_integer);
	assertTrue(Arrays.equals(buf_integer,
				 new byte[]{ERL_INTEGER_EXT, (byte)0x80, 0, 0, 1}));

	buf.get(buf_integer);
	assertTrue(Arrays.equals(buf_integer,
				 new byte[]{ERL_INTEGER_EXT, (byte)0x80, 0, 0, 0}));

	buf.get(buf_small_big_4);
	assertTrue(Arrays.equals(buf_small_big_4,
				 new byte[]{ERL_SMALL_BIG_EXT, 4, 1, 1, 0, 0, (byte)0x80}));

	//

	buf.get(buf_integer);
	assertTrue(Arrays.equals(buf_integer,
				 new byte[]{ERL_INTEGER_EXT, (byte)0x7f, -1, -1, -2}));

	buf.get(buf_integer);
	assertTrue(Arrays.equals(buf_integer,
				 new byte[]{ERL_INTEGER_EXT, (byte)0x7f, -1, -1, -1}));
	
	buf.get(buf_small_big_4);
	assertTrue(Arrays.equals(buf_small_big_4,
				 new byte[]{ERL_SMALL_BIG_EXT, 4, 0, 0, 0, 0, (byte)0x80}));

	//

	buf.get(buf_small_big_4);
	assertTrue(Arrays.equals(buf_small_big_4,
				 new byte[]{ERL_SMALL_BIG_EXT, 4, 0, -1, -1, -1, -1}));

	buf.get(buf_small_big_4);
	assertTrue(Arrays.equals(buf_small_big_4,
				 new byte[]{ERL_SMALL_BIG_EXT, 4, 1, -1, -1, -1, -1}));

	//

	buf.get(buf_small_big_5);
	assertTrue(Arrays.equals(buf_small_big_5,
				 new byte[]{ERL_SMALL_BIG_EXT, 5, 0, 0, 0, 0, 0, 1}));

	buf.get(buf_small_big_5);
	assertTrue(Arrays.equals(buf_small_big_5,
				 new byte[]{ERL_SMALL_BIG_EXT, 5, 1, 0, 0, 0, 0, 1}));

	//

	assertEquals(buf.get(), ERL_NIL_EXT);
	assertEquals(buf.remaining(), 0);
    }
    public void testB() throws java.io.IOException
    {
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
		       ),
		 list(
		      atom("a_list"),
		      number(0),
		      number(1),
		      number(1),
		      number(2),
		      number(3),
		      number(5),
		      number(8),
		      number(13),
		      number(21),
		      number(34),
		      number(55),
		      number(89),
		      number(144)
		      )
		 );

	ByteBuffer buf = ByteBuffer.allocate(1024);
	buf.put((byte)ErlTerm.ERL_VERSION_MAGIC);
	ET.encode(buf, root);

	/* flip buffer to consume (produce -> consume)
	 */

	buf.flip();

	/* serialize to file
	 */

	writeToFile("TestEncode-testB.out", buf, false);
	buf.rewind();

	/* validate
	 */

	assertEquals(buf.get(), (byte)ErlTerm.ERL_VERSION_MAGIC);

	byte buf_list_header[] = new byte[5];
	byte buf_small_integer[] = new byte[2];
	byte buf_integer[] = new byte[5];
	byte buf_small_big_4[] = new byte[7];
	byte buf_float[] = new byte[9];

	buf.get(buf_list_header);
	assertTrue(Arrays.equals(buf_list_header,
					   new byte[]{ERL_LIST_EXT, 0, 0, 0, (byte)root.arity()}));

	buf.get(buf_small_integer);
	assertTrue(Arrays.equals(buf_small_integer,
					   new byte[]{ERL_SMALL_INTEGER_EXT, 1}));

	assertEquals(buf.get(), NEW_FLOAT_EXT);
	assertEquals(String.format("%.6f", buf.getDouble()), "1.618034");

	assertEquals(buf.get(), ERL_ATOM_EXT);
	assertEquals(buf.getShort(), 6);
	assertEquals(getString(buf, 6), "a_atom");

	assertEquals(buf.get(), ERL_STRING_EXT);
	assertEquals(buf.getShort(), 8);
	assertEquals(getString(buf, 8), "a_string");

	assertEquals(buf.get(), ERL_ATOM_EXT);
	assertEquals(buf.getShort(), 4);
	assertEquals(getString(buf, 4), "true");

	assertEquals(buf.get(), ERL_ATOM_EXT);
	assertEquals(buf.getShort(), 5);
	assertEquals(getString(buf, 5), "false");

	assertEquals(buf.get(), ERL_SMALL_TUPLE_EXT);
	assertEquals(buf.get(), 4);

	assertEquals(buf.get(), ERL_ATOM_EXT);
	assertEquals(buf.getShort(), 6);
	assertEquals(getString(buf, 6), "a_atom");

	buf.get(buf_small_integer);
	assertTrue(Arrays.equals(buf_small_integer,
					   new byte[]{ERL_SMALL_INTEGER_EXT, 1}));

	assertEquals(buf.get(), NEW_FLOAT_EXT);
	//assertEquals(String.format("%.6f", buf.getDouble()), "1.618034");
	assertEquals(1.618034, buf.getDouble(), 0.0000001 /* epsilon */);

	assertEquals(buf.get(), ERL_STRING_EXT);
	assertEquals(buf.getShort(), 8);
	assertEquals(getString(buf, 8), "a_string");

	buf.get(buf_list_header);
	assertTrue(Arrays.equals(buf_list_header,
					   new byte[]{ERL_LIST_EXT, 0, 0, 0, 14}));

	assertEquals(buf.get(), ERL_ATOM_EXT);
	assertEquals(buf.getShort(), 6);
	assertEquals(getString(buf, 6), "a_list");

	int sum = 0;
	assertEquals(buf.get(), ERL_SMALL_INTEGER_EXT); sum += buf.get();
	assertEquals(buf.get(), ERL_SMALL_INTEGER_EXT); sum += buf.get();
	assertEquals(buf.get(), ERL_SMALL_INTEGER_EXT); sum += buf.get();
	assertEquals(buf.get(), ERL_SMALL_INTEGER_EXT); sum += buf.get();
	assertEquals(buf.get(), ERL_SMALL_INTEGER_EXT); sum += buf.get();
	assertEquals(buf.get(), ERL_SMALL_INTEGER_EXT); sum += buf.get();
	assertEquals(buf.get(), ERL_SMALL_INTEGER_EXT); sum += buf.get();
	assertEquals(buf.get(), ERL_SMALL_INTEGER_EXT); sum += buf.get();
	assertEquals(buf.get(), ERL_SMALL_INTEGER_EXT); sum += buf.get();
	assertEquals(buf.get(), ERL_SMALL_INTEGER_EXT); sum += buf.get();
	assertEquals(buf.get(), ERL_SMALL_INTEGER_EXT); sum += buf.get();
	assertEquals(buf.get(), ERL_SMALL_INTEGER_EXT); sum += buf.get();
	/* 144 is signed byte -112 (144 - 256), all small integers are
	 * unsigned
	 */
	assertEquals(buf.get(), ERL_SMALL_INTEGER_EXT); sum += buf.get() + 256;
	assertEquals(sum, 0+1+1+2+3+5+8+13+21+34+55+89+144);

	assertEquals(buf.get(), ERL_NIL_EXT);
	assertEquals(buf.get(), ERL_NIL_EXT);
	assertEquals(buf.remaining(), 0);
    }
}
