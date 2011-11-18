
package erl;

import java.util.Iterator;

public interface ErlListTerms extends ErlList {
    public Iterator<ErlTerm> iterator();
}
