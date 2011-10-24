package erl;

/**
 * I write encoded ErlTerms to a stream.
 */
public interface ErlTermWriter {

    /**
     * Essentially, write(term, true).
     * One should always use this instead of write(ErlTerm,boolean), since
     * blocks are only supported under Jenco streams.
     * @param term  ErlTerm.
     */
    public void write(ErlTerm term);

    /**
     * Write out an ErlTerm to the stream, optionally leaving the
     * current encapsulating block/array open.
     * If the writer had left the block open, this will be added to
     * the last block. Otherwise a new block will be started.
     * @param term      ErlTerm to write.
     * @param endBlock  If true, the block will be closed, otherwise
     *                  it will remain open.
     */
    public void write(ErlTerm term, boolean endBlock);

}