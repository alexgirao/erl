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

}