
CLASSES=\
  erl/ErlTermFactory.class \
  erl/ErlBinary.class \
  erl/ErlFloat.class \
  erl/ErlNumber.class \
  erl/ErlInteger.class \
  erl/ET.class \
  erl/ErlAtom.class \
  erl/ErlTuple.class \
  erl/ErlRef.class \
  erl/impl/ErlRefImpl.class \
  erl/impl/ErlIntegerImpl.class \
  erl/impl/ErlBinaryImpl.class \
  erl/impl/ErlFloatImpl.class \
  erl/impl/ErlBinaryRepBase.class \
  erl/impl/ErlTupleImpl.class \
  erl/impl/ErlListImpl.class \
  erl/impl/DefaultErlTermFactory.class \
  erl/impl/ErlAtomImpl.class \
  erl/ErlList.class \
  erl/ErlTermReader.class \
  erl/ErlTerm.class \
  erl/ErlTermWriter.class \
  test/TestImpl1.class \
  test/Test1.class

all: $(CLASSES)

$(CLASSES): %.class: %.java
	javac -cp "lib/junit-4.8.2.jar:." -g $<

tests:
	java -ea -cp "lib/junit-4.8.2.jar:." junit.textui.TestRunner test.TestImpl1
	java -ea -cp "lib/junit-4.8.2.jar:." junit.textui.TestRunner test.Test1
