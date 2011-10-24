package erl;

/**
 * Marker for Erlang tuples.
 */
public interface ErlTuple extends ErlTerm, Iterable<ErlTerm> {

    public int getArity();

    public ErlTerm getElement(int index);

}