package erl;

/**
 * A data object representing an Erlang term.
 */
public interface ErlTerm {

    public boolean isAtom();
    public boolean isBoolean();
    public boolean isTrue();
    public boolean isFalse();

    public boolean isList();
    public boolean isEmptyList();

    public boolean isTuple();

    public boolean isNumber();
    public boolean isInteger();
    public boolean isFloat();
    public boolean isLatin1Char();
    public boolean isUnicodeChar();

    public boolean isBinary();

    public boolean isRef();

    public int size();

    interface ClassVisitor<R,D> {
	R visit_atom(ErlTerm o, D d);
	R visit_binary(ErlTerm o, D d);
	R visit_float(ErlTerm o, D d);
	R visit_integer(ErlTerm o, D d);
	R visit_list(ErlTerm o, D d);
	R visit_ref(ErlTerm o, D d);
	R visit_tuple(ErlTerm o, D d);
    }

    public <R,D> R accept(ClassVisitor<R,D> v, D d);
}
