/* javac -cp .:OtpErlang-R13B01.jar t2b.java && java -ea -cp .:OtpErlang-R13B01.jar t2b
 */

import java.io.InputStream;
import java.io.IOException;
import java.io.FileOutputStream;

import com.ericsson.otp.erlang.*;

class t2b {
    public static void main(String[] Args) throws java.io.FileNotFoundException, java.io.IOException, OtpErlangDecodeException {
	OtpErlangTuple t0 = new OtpErlangTuple(new OtpErlangObject[] {
		new OtpErlangAtom("a_atom"),
		new OtpErlangInt(1),
		new OtpErlangDouble(1.618034),
		new OtpErlangString("a_string")
	    });

	OtpErlangList l0 = new OtpErlangList(new OtpErlangObject[] {
		new OtpErlangAtom("a_list"),
		new OtpErlangInt(0),
		new OtpErlangInt(1),
		new OtpErlangInt(1),
		new OtpErlangInt(2),
		new OtpErlangInt(3),
		new OtpErlangInt(5),
		new OtpErlangInt(8),
		new OtpErlangInt(13),
		new OtpErlangInt(21),
		new OtpErlangInt(34),
		new OtpErlangInt(55),
		new OtpErlangInt(89),
		new OtpErlangInt(144)
	    });

	OtpErlangList rootObj = new OtpErlangList(new OtpErlangObject[] {
		new OtpErlangInt(1),
		new OtpErlangDouble(1.618034),
		new OtpErlangAtom("a_atom"),
		new OtpErlangString("a_string"),
		new OtpErlangBoolean(true),
		new OtpErlangBoolean(false),
		t0,
		l0
	    });

	/*
	 */

	OtpOutputStream out = new OtpOutputStream();
	out.write_any(rootObj);

	byte[] buffer = out.toByteArray();

	String fileName = "t2b.java.bin";

	FileOutputStream fout = new FileOutputStream(fileName);
	fout.write((int) OtpExternal.versionTag);  // 131, 0x83
	fout.write(buffer);
	fout.flush();

	System.out.printf("wrote \"%s\"\n", fileName);

	fout.close();
    }
}
