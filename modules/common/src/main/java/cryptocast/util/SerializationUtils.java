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
/**
 * Provides several serialization utility methods.
 */
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

    /**
     * Reads a file and returns a serializable object.
     * 
     * @param file The file to read from.
     * @return A serializable object.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static <T extends Serializable> T readFromFile(File file) 
               throws IOException, ClassNotFoundException {
        InputStream fis = new FileInputStream(file);
        try {
            return readFromStream(fis);
        } finally {
            fis.close();
        }
    }

    /**
     * Writes a serializable object into an output stream.
     * 
     * @param out The output stream.
     * @param obj The object to write.
     * @throws IOException
     */
    public static void writeToStream(OutputStream out, Serializable obj)
                                        throws IOException {
        ObjectOutputStream objOut = new ObjectOutputStream(out);
        try {
            objOut.writeObject(obj);
        } finally {
            objOut.close();
        }
    }
   
    /**
     * Writes a serializable object into a file.
     * 
     * @param file The file to write in.
     * @param obj The object to write.
     * @throws IOException
     */
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
