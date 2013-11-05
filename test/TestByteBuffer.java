/* test ByteBuffer methods get() and getInt()
 */

package test;

import junit.framework.TestCase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.ByteOrder;
import java.nio.ByteBuffer;

public class TestByteBuffer extends TestCase
{
    public void testOrder() {
	ByteBuffer buf = ByteBuffer.allocate(1024);
	/* default order is network byte order (a.k.a. big endian */
	assertEquals(buf.order(), ByteOrder.BIG_ENDIAN);
	/* check */
	buf.putShort((short)0xBEEF);
	buf.putInt(0xCAFEBABE);
	buf.flip();
	assertEquals(buf.get(),(byte)0xBE);
	assertEquals(buf.get(),(byte)0xEF);
	assertEquals(buf.get(),(byte)0xCA);
	assertEquals(buf.get(),(byte)0xFE);
	assertEquals(buf.get(),(byte)0xBA);
	assertEquals(buf.get(),(byte)0xBE);
	/* eof? */
	assertEquals(buf.remaining(), 0);
    }
    public void testSignAndOffset() {
	ByteBuffer buf = ByteBuffer.allocate(1024);
	buf.put((byte)0xCA);
	buf.putShort((short)0xCAFE);
	buf.putInt(0xCAFEBABE);
	buf.put((byte)-54);
	buf.putShort((short)-13570);
	buf.putInt(-889275714);
	buf.flip();
	/* peek */
	assertEquals(buf.position(), 0);
	byte b = buf.get();       assertEquals(b,      buf.get(1+2+4));
	short s = buf.getShort(); assertEquals(s, buf.getShort(1+2+4+1));
	int i = buf.getInt();     assertEquals(i,   buf.getInt(1+2+4+1+2));
	assertEquals(buf.position(), 1+2+4);
	/* peek, offset does not change after advancing position */
	assertEquals(buf.get(0), buf.get(1+2+4));
	assertEquals(buf.get(0), -54);
	assertEquals(buf.getShort(1), buf.getShort(1+2+4+1));
	assertEquals(buf.getShort(1), -13570);
	assertEquals(buf.getInt(3), buf.getInt(1+2+4+1+2));
	assertEquals(buf.getInt(3), -889275714);
	/* read */
	assertEquals(buf.get(), -54);
	assertEquals(buf.getShort(), -13570);
	assertEquals(buf.getInt(), -889275714);
	/* eof? */
	assertEquals(buf.remaining(), 0);
	/* rewind */
	buf.rewind();
	assertEquals(buf.get(), -54);
	assertEquals(buf.getShort(), -13570);
	assertEquals(buf.getInt(), -889275714);
	assertEquals(buf.get(), -54);
	assertEquals(buf.getShort(), -13570);
	assertEquals(buf.getInt(), -889275714);
	assertEquals(buf.remaining(), 0);
    }
    /* reference: TestBasics.java
     */
    private static int readUnsignedByte(ByteBuffer buf) {
	return ((int)buf.get()) & 0xff;
    }
    private static int peekUnsignedByte(ByteBuffer buf, int index) {
	return ((int)buf.get(index)) & 0xff;
    }
    private static int readUnsignedShort(ByteBuffer buf) {
	return ((int)buf.getShort()) & 0xffff;
    }
    private static long readUnsignedInt(ByteBuffer buf) {
	return ((long)buf.getInt()) & 0xffffffffL;
    }
    public void testUnsignedByte() {
	ByteBuffer buf = ByteBuffer.allocate(1024);
	buf.putInt(0xCAFEBABE);
	buf.flip();
	assertEquals(peekUnsignedByte(buf,0), 0xCA);
	assertEquals(peekUnsignedByte(buf,1), 0xFE);
	assertEquals(peekUnsignedByte(buf,2), 0xBA);
	assertEquals(peekUnsignedByte(buf,3), 0xBE);
	assertEquals(readUnsignedByte(buf), 0xCA);
	assertEquals(readUnsignedByte(buf), 0xFE);
	assertEquals(readUnsignedByte(buf), 0xBA);
	assertEquals(readUnsignedByte(buf), 0xBE);
	/* eof? */
	assertEquals(buf.remaining(), 0);
    }
    public void testUnsignedShort() {
	ByteBuffer buf = ByteBuffer.allocate(1024);
	buf.putInt(0xCAFEBABE);
	buf.flip();
	assertEquals(readUnsignedShort(buf), 0xCAFE);
	assertEquals(readUnsignedShort(buf), 0xBABE);
	/* eof? */
	assertEquals(buf.remaining(), 0);
    }
    public void testUnsignedInt() {
	ByteBuffer buf = ByteBuffer.allocate(1024);
	buf.putInt(0xCAFEBABE);
	buf.flip();
	assertEquals(readUnsignedInt(buf), 0xCAFEBABEL);
	/* eof? */
	assertEquals(buf.remaining(), 0);
    }
    public void testPromotion() {
	ByteBuffer buf = ByteBuffer.allocate(1024);
	buf.putInt(0xCAFEBABE);
	buf.putInt(0x7AFEBABE);
	buf.flip();
	long v = buf.getInt();
	assertTrue(v < 0);
	v = buf.getInt();
	assertTrue(v > 0);
    }
}
