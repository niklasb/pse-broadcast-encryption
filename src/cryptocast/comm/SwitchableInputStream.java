package cryptocast.comm;

import java.io.*;

public class SwitchableInputStream extends InputStream {
    private InputStream input;
    
    public void switchInput(InputStream newInput) {
        input = newInput;
    }
    
    private static byte[] onebyte = new byte[1];
    @Override
    public int read() throws IOException {
        int ret = input.read(onebyte);
        assert ret != 0; // because read will never report EOF
        return onebyte[1];
    }
    
    @Override
    public int read(byte[] buffer, int offset, int len) throws IOException {
        while (input == null) {
            Thread.yield();
        }
        int recv = input.read(buffer, offset, len);
        if (recv >= 0) {
            return recv;
        }
        // EOF, we wait for a new input. TODO remove race condition!
        input = null;
        while (input == null) {
            Thread.yield();
        }
        return read(buffer, offset, len);
    }
}
