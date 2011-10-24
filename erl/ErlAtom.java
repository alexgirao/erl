package erl;

/**
 * Marker for Erlang Atoms.
 */
public interface ErlAtom extends ErlTerm {

    /**
     * Get the atom's string value.
     * @return The value as a string.
     */
    public String getValue();

}
