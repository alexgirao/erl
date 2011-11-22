
package erl.impl;

import erl.ErlList;
import erl.ErlListNil;
import erl.ErlTerm;

import java.util.ArrayList;
import java.util.Iterator;

public class ErlListNilImpl implements ErlListNil {
    public ErlListNilImpl() {
    }

    public ErlTerm hd() {
	throw new IllegalArgumentException("list is nil");
    }

    public ErlList tl() {
	throw new IllegalArgumentException("list is nil");
    }

    public ErlList insert(ErlTerm term) {
	return new ErlListTermsImpl(term);
    }

    public ErlList append(ErlList list) {
	return list;
   }

    public Iterator<ErlTerm> iterator() {
	return (new ArrayList<ErlTerm>()).iterator();
    }

    @Override
    public boolean equals(Object obj) {
	return obj != null && (obj instanceof ErlListNilImpl);
    }

    @Override
    public int hashCode() {
	return 0;
    }

    public int arity() {
        return 0;
    }

    public <R,D> R accept(ErlTerm.ClassVisitor<R,D> v, D d) {
	return v.visitListNil(this, d);
    }
}
