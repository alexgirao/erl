package erl;

/**
 * Marker for Erlang tuples.
 */
public interface ErlTuple extends ErlTerm, Iterable<ErlTerm> {

    public int arity();

    /* first element is index 0
     */
    public ErlTerm element(int index);
}
