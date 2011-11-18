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

public class TestDecode extends TestCase
{
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
		 number(ErlTerm.ERL_MIN + 1),
		 number(ErlTerm.ERL_MIN),
		 number(ErlTerm.ERL_MIN - 1),
		 number(ErlTerm.ERL_MAX - 1),
		 number(ErlTerm.ERL_MAX),
		 number(ErlTerm.ERL_MAX + 1),
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
		 // //
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
	assertEquals(((ErlInteger)t).getValue(), ErlTerm.ERL_MIN + 1);

	t = i.next();
	assertTrue(t instanceof ErlInteger);
	assertEquals(((ErlInteger)t).getValue(), ErlTerm.ERL_MIN);

	t = i.next();
	assertTrue(t instanceof ErlInteger);
	assertEquals(((ErlInteger)t).getValue(), ErlTerm.ERL_MIN - 1);

	t = i.next();
	assertTrue(t instanceof ErlInteger);
	assertEquals(((ErlInteger)t).getValue(), ErlTerm.ERL_MAX - 1);

	t = i.next();
	assertTrue(t instanceof ErlInteger);
	assertEquals(((ErlInteger)t).getValue(), ErlTerm.ERL_MAX);

	t = i.next();
	assertTrue(t instanceof ErlInteger);
	assertEquals(((ErlInteger)t).getValue(), ErlTerm.ERL_MAX + 1);

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
    }
    public void _testB() throws java.io.IOException
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

	/* produce -> consume
	 */
	buf.flip();

	/*
	 */
	ErlTerm e = ET.decode(buf);
    }
}
