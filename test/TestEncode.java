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

public class TestEncode extends TestCase
{
    public void writeToFile(String filename, ByteBuffer buf, boolean append) throws java.io.IOException
    {
	File file = new File(filename);
	FileChannel ch = new FileOutputStream(file, append).getChannel();
	ch.write(buf);
	ch.close();
    }
    public void testNumber() throws java.io.IOException
    {
	ByteBuffer buf = ByteBuffer.allocate(1024);

	buf.put((byte)ErlTerm.ERL_VERSION_MAGIC);

	ErlList root =
	    list(
		 number(0),
		 number(1),
		 number(2),
		 number(-1),
		 number(-2),
		 number(ErlTerm.ERL_MIN + 1),
		 number(ErlTerm.ERL_MAX - 1),
		 number(ErlTerm.ERL_MIN),
		 number(ErlTerm.ERL_MAX),
		 number(ErlTerm.ERL_MIN - 1),
		 number(ErlTerm.ERL_MAX + 1)
		 );

	ET.encode(buf, root);

	buf.flip();
	writeToFile("TestEncode.out", buf, false);
    }
    public static void main(String args[]) throws java.io.IOException
    {
	TestEncode t = new TestEncode();
	t.testNumber();
    }
    public void testB()
    {
	ByteBuffer buf = ByteBuffer.allocate(1024);
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

	for (ErlTerm i:root) {
	    ET.encode(buf, i);
	}
    }
}
