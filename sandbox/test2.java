
class test2 {
    interface Visitor<R,D,T> {
	R visit_O1(O1 o, D d);
	R visit_O2(O2 o, D d);
    }

    abstract class O {
	abstract <R,D,T> R accept(Visitor<R,D,T> v, D d);
    }

    class O1 extends O {
	O1(int v) {
	    this.v = v;
	}

	<R,D,T> R accept(Visitor<R,D,T> v, D d) {
	    return v.visit_O1(this, d);
	}

	int v;
    }

    class O2 extends O {
	O2(double v) {
	    this.v = v;
	}

	<R,D,T> R accept(Visitor<R,D,T> v, D d) {
	    return v.visit_O2(this, d);
	}

	double v;
    }

    Visitor<String,Integer,Void> v = new Visitor<String,Integer,Void>() {
	public String visit_O1(O1 o, Integer d) {
	    return String.valueOf(d + o.v);
	}

	public String visit_O2(O2 o, Integer d) {
	    return String.format("%.2f", d * o.v);
	}
    };

    void doit() {
	O1 o1 = new O1(2);
	O2 o2 = new O2(2);

	System.out.println("o1.accept(v,0): [" + o1.accept(v,3) + "]");
	System.out.println("o2.accept(v,0): [" + o2.accept(v,3) + "]");
    }

    /*
     */

    public static void main(String[] args) {
	test2 t = new test2();
	t.doit();
    }
}
