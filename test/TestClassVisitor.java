package test;

import junit.framework.TestCase;

import erl.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import static erl.ET.list;
import static erl.ET.atom;
import static erl.ET.binary;
import static erl.ET.number;
import static erl.ET.tuple;

import java.util.Arrays;

public class TestClassVisitor extends TestCase
{
    static ErlTerm.ClassVisitor<String,Void> v1 = new ErlTerm.ClassVisitor<String,Void>() {
	public String visitAtom(ErlAtom o, Void d) {return "atom";}
	public String visitBinary(ErlBinary o, Void d) {return "binary";}
	public String visitFloat(ErlFloat o, Void d) {return "float";}
	public String visitInteger(ErlInteger o, Void d) {return "integer";}
	public String visitList(ErlList o, Void d) {return "list";}
	public String visitRef(ErlRef o, Void d) {return "ref";}
	public String visitTuple(ErlTuple o, Void d) {return "tuple";}
	public String visitBigInteger(ErlBigInteger o, Void v) {return "biginteger";};
	public String visitLong(ErlLong o, Void v) {return "long";};
    };

    public void testA()
    {
	ErlList root =
	    list(
		 number(1),
		 number(1.618034),
		 atom("a_atom"),
		 list("a_string"),
		 atom("true"),
		 atom("false"),
		 tuple(
		       atom("a_atom"),
		       number(1),
		       number(1.618034),
		       list("a_string")
		       ),
		 list(
		      atom("a_list"),
		      number(0),
		      number(1),
		      number(1),
		      number(2),
		      number(3),
		      number(5),
		      number(8),
		      number(13),
		      number(21),
		      number(34),
		      number(55),
		      number(89),
		      number(144)
		      )
		 );

	for (ErlTerm i:root) {
	    String s = i.accept(v1, null);
	    if (i.isNumber()) {
		if (i.isFloat()) {
		    assertEquals(s, "float");
		} else if (i.isInteger()) {
		    assertEquals(s, "integer");
		} else {
		    fail("exhaustion");
		}
	    } else if (i.isAtom()) {
		assertEquals(s, "atom");
	    } else if (i.isTuple()) {
		assertEquals(s, "tuple");
	    } else if (i.isList()) {
		assertEquals(s, "list");
	    } else {
		fail("exhaustion");
	    }
	}
    }
}
