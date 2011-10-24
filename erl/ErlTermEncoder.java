
package erl;

import java.nio.ByteBuffer;

public interface ErlTermEncoder
{
    public void encode(ByteBuffer buf, ErlTerm t);
}
