
import java.nio.ByteBuffer;

public class bb00
{
    static void debug_buf(ByteBuffer buf)
    {
	System.out.printf("buf.position()=%d, buf.limit()=%d, buf.capacity()=%d, buf.remaining()=%d\n",
			  buf.position(), buf.limit(), buf.capacity(), buf.remaining());
    }

    public static void main(String args[])
    {
	byte buf0[] = new byte[10];
	ByteBuffer buf = ByteBuffer.wrap(buf0);

	for (int i=0;;i++) {
	    debug_buf(buf);
	    try {
		buf.putInt(i);
	    } catch (java.nio.BufferOverflowException e) {
		/* buf.position() bytes successfully written
		 */
		debug_buf(buf);
		break;
	    }
	}
    }
}
