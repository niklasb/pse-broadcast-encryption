package cryptocast.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SerializationUtils {
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T readFromFile(File file) 
               throws IOException, ClassNotFoundException {
        InputStream fis = new FileInputStream(file);
        ObjectInputStream in;
        try {
            in = new ObjectInputStream(fis);
            return (T)in.readObject();
        } finally {
            fis.close();
        }
    }
    
    public static void writeToFile(File file, Serializable obj) 
                                        throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject(obj);
        out.close();
        FileOutputStream fos = new FileOutputStream(file);
        try {
            fos.write(bos.toByteArray());
        } finally {
            fos.close();
        }
    }
}
