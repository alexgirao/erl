package erl;

/**
 * Marker for Erlang lists.
 */
public interface ErlList extends ErlTerm, Iterable<ErlTerm> {

    public ErlTerm hd();

    public ErlList tl();

    public ErlList append(ErlList list);

    public ErlList insert(ErlTerm term);

    public String getLatin1StringValue();

    public String getUtf8StringValue();

    public String getUnicodeStringValue();
}
