package erl;

import java.math.BigInteger;
import java.io.UnsupportedEncodingException;

/**
 * A factory for ErlTerm's.
 */
public interface ErlTermFactory {

    /**
     * Create an atom from a latin1 string.
     * @param value A latin1 string containing the atom's value.
     * @return      An atom object.
     */
    public ErlAtom createAtom(String value);

    /**
     * Create a binary from a byte buffer.
     * @param value Byte buffer.
     * @return      A binary object.
     */
    public ErlBinary createBinary(byte[] value);

    /**
     * Create a number from a Number object.
     * @param value A Number.
     * @return      An ErlNumber representing 'value'.
     */
    public ErlNumber createNumber(Number value);

    /**
     * Create a float.
     * @param value Float value
     * @return      A float object.
     */
    public ErlFloat createNumber(float value);

    /**
     * Create a float.
     * @param value Double value
     * @return      A float object.
     */
    public ErlFloat createNumber(double value);

    /**
     * Create an integer.
     * @param value Int value
     * @return      An integer object.
     */
    public ErlInteger createNumber(int value);

    /**
     * Create an integer.
     * @param value Long value
     * @return      An integer object.
     */
    public ErlInteger createNumber(long value);

    /**
     * Create an integer.
     * @param value BigInt value
     * @return      An integer object.
     */
    public ErlInteger createNumber(BigInteger value);

    /**
     * Create an empty list.
     * @return An empty list object.
     */
    public ErlList createList();

    /**
     * Create a list from a UTF-8 encoded string.
     * @param utf8      UTF-8 encoded string.
     * @param toBytes   If true, the list will contain bytes instead of
     *                  Unicode point values.
     * @return A list object.
     * @throws java.io.UnsupportedEncodingException if encoding not supported.
     */
    public ErlList createList(String utf8, boolean toBytes) throws UnsupportedEncodingException;

    /**
     * Create a list from erlang terms.
     * @param terms Erlang terms.
     * @return A list object.
     */
    public ErlList createList(ErlTerm ... terms);

    /**
     * Create a ref from a serialized byte array.
     * Please ensure that bytes represents a true ref!
     * @param bytes Result of term_to_binary(Ref).
     * @return A ref object.
     */
    ErlRef createRef(byte[] bytes);

    /**
     * Create a tuple from Erlang terms.
     * @param terms Erlang terms.
     * @return A tuple object.
     */
    public ErlTuple createTuple(ErlTerm ... terms);

}