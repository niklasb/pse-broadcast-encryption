package cryptocast.comm;

import java.io.*;

/**
 * An output stream where the actual destination can be changed dynamically
 */
public class SwitchableOutputStream extends OutputStream {
    private OutputStream output;

    /**
     * Sets a new output stream.
     * @param newOutput The new sink
     */
    public void switchOutput(OutputStream newOutput) {
        this.output = newOutput;
    }

    @Override
    public void write(int b) throws IOException {
        if (output == null) { return; }
        output.write(b);
    }

    @Override
    public void write(byte[] buffer, int offset, int len) throws IOException {
        if (output == null) { return; }
        output.write(buffer, offset, len);
    }
}
