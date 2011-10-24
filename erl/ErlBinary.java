package erl;

/**
 * Marker for Erlang Binaries.
 */
public interface ErlBinary extends ErlTerm {

    public byte[] getBuffer(boolean doCopy);

}