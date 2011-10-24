package erl;

/**
 * Marker for Reference term.
 */
public interface ErlRef extends ErlTerm {

    public byte[] getBufferRepresentation(boolean doCopy);

}