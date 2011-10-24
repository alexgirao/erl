
/* implemented after otp_src_R14B04/lib/erl_interface/src/encode/encode_*.c
 */

package erl.impl;

import erl.*;

import java.nio.ByteBuffer;

/**
 * Default implementation of ErlTermEncoder
 */
public class DefaultErlTermEncoder implements ErlTermEncoder
{
    private static ErlTerm.ClassVisitor<Void,ByteBuffer> ev = new ErlTerm.ClassVisitor<Void,ByteBuffer>() {
	public Void visit_atom(ErlTerm o, ByteBuffer b)
	{
	    System.out.println("WIP!");
	    return null;
	}
	public Void visit_binary(ErlTerm o, ByteBuffer d) {return null;}
	public Void visit_float(ErlTerm o, ByteBuffer d) {return null;}
	public Void visit_integer(ErlTerm o, ByteBuffer d) {return null;}
	public Void visit_list(ErlTerm o, ByteBuffer d) {return null;}
	public Void visit_ref(ErlTerm o, ByteBuffer d) {return null;}
	public Void visit_tuple(ErlTerm o, ByteBuffer d) {return null;}
    };

    public void encode(ByteBuffer buf, ErlTerm t)
    {
	t.accept(ev, buf);
    }
}
