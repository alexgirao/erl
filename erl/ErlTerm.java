
package erl;

/**
 * A data object representing an Erlang term.
 */
public interface ErlTerm
{
    /* from otp_src_R14B04/lib/erl_interface/include/ei.h
     */
    public static final int MAXATOMLEN = 255;

    public static final byte ERL_SMALL_INTEGER_EXT = 'a';
    public static final byte ERL_INTEGER_EXT       = 'b';
    public static final byte ERL_FLOAT_EXT         = 'c';
    public static final byte NEW_FLOAT_EXT         = 'F';
    public static final byte ERL_ATOM_EXT          = 'd';
    public static final byte ERL_REFERENCE_EXT     = 'e';
    public static final byte ERL_NEW_REFERENCE_EXT = 'r';
    public static final byte ERL_PORT_EXT          = 'f';
    public static final byte ERL_PID_EXT           = 'g';
    public static final byte ERL_SMALL_TUPLE_EXT   = 'h';
    public static final byte ERL_LARGE_TUPLE_EXT   = 'i';
    public static final byte ERL_NIL_EXT           = 'j';
    public static final byte ERL_STRING_EXT        = 'k';
    public static final byte ERL_LIST_EXT          = 'l';
    public static final byte ERL_BINARY_EXT        = 'm';
    public static final byte ERL_SMALL_BIG_EXT     = 'n';
    public static final byte ERL_LARGE_BIG_EXT     = 'o';
    public static final byte ERL_NEW_FUN_EXT	   = 'p';
    public static final byte ERL_FUN_EXT	   = 'u';

    public static final int ERL_VERSION_MAGIC = 131; /* 130 in erlang 4.2 */

    /* The largest and smallest value that can be encoded as an
     * integer (ERL_INTEGER_EXT)
     */
    public static final int ERL_MAX = (1 << 27) - 1;
    public static final int ERL_MIN = -(1 << 27);

    /*
     */

    public boolean isAtom();
    public boolean isBoolean();
    public boolean isTrue();
    public boolean isFalse();

    public boolean isList();
    public boolean isNil();

    public boolean isTuple();

    public boolean isNumber();
    public boolean isInteger();
    public boolean isLong();
    public boolean isFloat();
    public boolean isLatin1Char();
    public boolean isUnicodeChar();

    public boolean isBinary();

    public boolean isRef();

    public int size();

    interface ClassVisitor<R,D>
    {
	R visitAtom(ErlAtom o, D d);
	R visitBinary(ErlBinary o, D d);
	R visitFloat(ErlFloat o, D d);
	R visitInteger(ErlInteger o, D d);
	R visitBigInteger(ErlBigInteger o, D d);
	R visitLong(ErlLong o, D d);
	R visitListByteArray(ErlListByteArray o, D d);
	R visitListNil(ErlListNil o, D d);
	R visitListString(ErlListString o, D d);
	R visitListTerms(ErlListTerms o, D d);
	R visitRef(ErlRef o, D d);
	R visitTuple(ErlTuple o, D d);
    }

    public <R,D> R accept(ClassVisitor<R,D> v, D d);
}
