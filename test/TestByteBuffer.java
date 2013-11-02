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
	buf.flip();
	/* peek */
	assertEquals(buf.get(0), -((~0xCA & 0xFF) + 1));
	assertEquals(buf.get(0), -54);
	assertEquals(buf.getShort(1), -((~0xCAFE & 0xFFFF) + 1));
	assertEquals(buf.getShort(1), -13570);
	assertEquals(buf.getInt(3), -((~0xCAFEBABEL & 0xFFFFFFFFL) + 1));
	assertEquals(buf.getInt(3), -889275714);
	/* read */
	assertEquals(buf.get(), -54);
	assertEquals(buf.getShort(), -13570);
	assertEquals(buf.getInt(), -889275714);
	/* eof? */
	assertEquals(buf.remaining(), 0);
    }
    private static int readUnsignedByte(ByteBuffer buf) {
	final byte v = buf.get();
	return v >= 0 ? v : v + 0x100; // 256
    }
    private static int peekUnsignedByte(ByteBuffer buf, int index) {
	final byte v = buf.get(index);
	return v >= 0 ? v : v + 0x100; // 256
    }
    private static int readUnsignedShort(ByteBuffer buf) {
	final short v = buf.getShort();
	return v >= 0 ? v : v + 0x10000; // 65536
    }
    private static long readUnsignedInt(ByteBuffer buf) {
	final int v = buf.getInt();
	return v >= 0 ? v : v + 0x100000000L; // 4294967296
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
}
