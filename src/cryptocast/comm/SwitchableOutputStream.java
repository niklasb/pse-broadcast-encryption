package cryptocast.comm;

import java.io.*;

public class SwitchableOutputStream extends OutputStream {
    private OutputStream output;
    
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
