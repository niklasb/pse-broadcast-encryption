package cryptocast.comm;

import java.io.IOException;

public class DecoratingMessageOutChannel extends MessageOutChannel {
    private MessageOutChannel inner;
    private byte[] prefix;
    private byte[] suffix;

    public DecoratingMessageOutChannel(MessageOutChannel inner,
                                       byte[] prefix,
                                       byte[] suffix) {
        this.inner = inner;
        this.prefix = prefix;
        this.suffix = suffix;
    }

    @Override
    public void sendMessage(byte[] data, int offset, int len)
            throws IOException {
        byte[] buffer = new byte[len + prefix.length + suffix.length];
        System.arraycopy(prefix, 0, buffer, 0, prefix.length);
        System.arraycopy(data, offset, buffer, prefix.length, len);
        System.arraycopy(suffix, 0, buffer, prefix.length + len, suffix.length);
        inner.sendMessage(buffer);
    }
}