package cryptocast.client.bufferedmediaplayer;

import java.io.File;

public interface BufferedFileListener {
    public void addBufferedFile(File file);
    public void updateBufferProgress(int percentage);
}
