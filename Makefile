
.PHONY: all clean test

all:
	make -C out all

clean:
	make -C out clean

test:
	make -C out test

erl.jar: all
	jar cfm $@ MANIFEST.MF -C out erl
