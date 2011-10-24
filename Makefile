
.PHONY: all clean test

CLASSES=\
  erl/ET.class \
  erl/ErlAtom.class \
  erl/ErlBinary.class \
  erl/ErlFloat.class \
  erl/ErlInteger.class \
  erl/ErlList.class \
  erl/ErlNumber.class \
  erl/ErlRef.class \
  erl/ErlTerm.class \
  erl/ErlTermFactory.class \
  erl/ErlTuple.class \
  erl/ErlTermEncoder.class \
  erl/ErlTermDecoder.class \
  erl/impl/DefaultErlTermFactory.class \
  erl/impl/DefaultErlTermDecoder.class \
  erl/impl/DefaultErlTermEncoder.class \
  erl/impl/ErlAtomImpl.class \
  erl/impl/ErlBinaryImpl.class \
  erl/impl/ErlBinaryRepBase.class \
  erl/impl/ErlFloatImpl.class \
  erl/impl/ErlIntegerImpl.class \
  erl/impl/ErlListImpl.class \
  erl/impl/ErlRefImpl.class \
  erl/impl/ErlTupleImpl.class \
  test/Test1.class \
  test/Test2.class \
  test/TestImpl1.class \
  test/TestClassVisitor.class \
  test/TestEncode.class

all: $(CLASSES)

$(CLASSES): %.class: %.java
	javac -cp "lib/junit-4.8.2.jar:." -g $<

clean:
	find -iname '*.class' | xargs rm -f

test: all
	java -ea -cp "lib/junit-4.8.2.jar:." junit.textui.TestRunner test.TestImpl1
	java -ea -cp "lib/junit-4.8.2.jar:." junit.textui.TestRunner test.Test1
	java -ea -cp "lib/junit-4.8.2.jar:." junit.textui.TestRunner test.Test2
	java -ea -cp "lib/junit-4.8.2.jar:." junit.textui.TestRunner test.TestClassVisitor
	java -ea -cp "lib/junit-4.8.2.jar:." junit.textui.TestRunner test.TestEncode
