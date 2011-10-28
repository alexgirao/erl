
.PHONY: all clean test

JAVA_SOURCES=\
  erl/ET.java \
  erl/ErlAtom.java \
  erl/ErlBigInteger.java \
  erl/ErlBinary.java \
  erl/ErlFloat.java \
  erl/ErlInteger.java \
  erl/ErlList.java \
  erl/ErlListByteArray.java \
  erl/ErlListNil.java \
  erl/ErlListString.java \
  erl/ErlListTerms.java \
  erl/ErlLong.java \
  erl/ErlNumber.java \
  erl/ErlRef.java \
  erl/ErlTerm.java \
  erl/ErlTermDecoder.java \
  erl/ErlTermEncoder.java \
  erl/ErlTermFactory.java \
  erl/ErlTuple.java \
  erl/impl/DefaultErlTermDecoder.java \
  erl/impl/DefaultErlTermEncoder.java \
  erl/impl/DefaultErlTermFactory.java \
  erl/impl/ErlAtomImpl.java \
  erl/impl/ErlBigIntegerImpl.java \
  erl/impl/ErlBinaryImpl.java \
  erl/impl/ErlBinaryRepBase.java \
  erl/impl/ErlFloatImpl.java \
  erl/impl/ErlIntegerImpl.java \
  erl/impl/ErlListByteArrayImpl.java \
  erl/impl/ErlListNilImpl.java \
  erl/impl/ErlListStringImpl.java \
  erl/impl/ErlListTermsImpl.java \
  erl/impl/ErlLongImpl.java \
  erl/impl/ErlRefImpl.java \
  erl/impl/ErlTupleImpl.java \
  test/Test1.java \
  test/Test2.java \
  test/TestClassVisitor.java \
  test/TestEncode.java \
  test/TestImpl1.java

JAVA_CLASSES = $(JAVA_SOURCES:%.java=%.class)

all: $(JAVA_CLASSES)

$(JAVA_CLASSES): %.class: %.java
	javac -cp "lib/junit-4.8.2.jar:." -g $<

clean:
	find -iname '*.class' | xargs rm -f

test: all
	java -ea -cp "lib/junit-4.8.2.jar:." junit.textui.TestRunner test.TestImpl1
	java -ea -cp "lib/junit-4.8.2.jar:." junit.textui.TestRunner test.Test1
	java -ea -cp "lib/junit-4.8.2.jar:." junit.textui.TestRunner test.Test2
	java -ea -cp "lib/junit-4.8.2.jar:." junit.textui.TestRunner test.TestClassVisitor
	java -ea -cp "lib/junit-4.8.2.jar:." junit.textui.TestRunner test.TestEncode
