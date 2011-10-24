/* javac -cp OtpErlang-R13B01.jar b2t.java && java -ea -cp .:OtpErlang-R13B01.jar b2t
 */

import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;

import com.ericsson.otp.erlang.*;

class b2t {
    public static void main(String[] Args) throws Exception {
	show_binary_term("t2b.escript.bin");
	show_binary_term("t2b.java.bin");
    }

    public static void show_binary_term(String fileName) throws Exception {
	byte[] buffer = getBytesFromFile(new File(fileName));
	OtpInputStream in = new OtpInputStream(buffer);

	int versionTag = in.read1();
	if (versionTag != OtpExternal.versionTag) {
	    throw new OtpErlangDecodeException("versionTag doesn't match");
	}
	    
	System.out.printf("read %s\n", fileName);

	int pos_a = in.getPos();
	OtpErlangObject e_obj = in.read_any();
	int pos_b = in.getPos();
	in.close();

	System.out.printf("0x%08x - 0x%08x: %s (total 0x%08x)\n", pos_a, pos_b, e_obj, buffer.length);
	System.out.flush();
    }

    public static byte[] getBytesFromFile(File file) throws IOException {
	InputStream is = new FileInputStream(file);
	long length = file.length();

	if (length > Integer.MAX_VALUE) {
	    throw new IOException("file too large");
	}

	byte[] bytes = new byte[(int)length];

	int offset = 0;
	int numRead = 0;
	while (offset < bytes.length
	       && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
	    offset += numRead;
	}

	if (offset < bytes.length) {
	    throw new IOException("Could not completely read file " + file.getName());
	}

	is.close();
	return bytes;
    }
}
