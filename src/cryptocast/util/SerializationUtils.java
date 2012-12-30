package cryptocast.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class SerializationUtils {    
    @SuppressWarnings("unchecked")
    public static <T extends Serializable> T readFromStream(InputStream in) 
               throws IOException, ClassNotFoundException {
        ObjectInputStream objIn = new ObjectInputStream(in);
        try {
            return (T)objIn.readObject();
        } finally {
            objIn.close();
        }
    }

    public static <T extends Serializable> T readFromFile(File file) 
               throws IOException, ClassNotFoundException {
        InputStream fis = new FileInputStream(file);
        try {
            return readFromStream(fis);
        } finally {
            fis.close();
        }
    }

    public static void writeToStream(OutputStream out, Serializable obj)
                                        throws IOException {
        ObjectOutputStream objOut = new ObjectOutputStream(out);
        try {
            objOut.writeObject(obj);
        } finally {
            objOut.close();
        }
    }
    
    public static void writeToFile(File file, Serializable obj) 
                                        throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        try {
            writeToStream(fos, obj);
        } finally {
            fos.close();
        }
    }
}
