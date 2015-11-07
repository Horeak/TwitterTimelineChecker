package Console;

import java.io.IOException;
import java.io.OutputStream;

public class ConsoleOutputStream extends OutputStream {

	@Override
	public void write( int b ) throws IOException {
		ProgramWindow.stream.write(b);
	}
}