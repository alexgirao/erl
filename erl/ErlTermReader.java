package erl;

/**
 * I read encoded ErlTerms from a stream.
 */
public interface ErlTermReader {

    /**
     * Retrieve next term from stream.
     * Encapsulating block/array is completely abstracted.
     * @return Next ErlTerm from the stream, or null.
     */
    public ErlTerm read();

}