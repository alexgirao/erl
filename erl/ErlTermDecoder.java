
package erl;

import java.nio.ByteBuffer;

public interface ErlTermDecoder
{
    public ErlTerm decode(ByteBuffer buf);
}
