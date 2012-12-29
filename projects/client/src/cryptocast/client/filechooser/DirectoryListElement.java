package cryptocast.client.filechooser;

import java.io.File;
import android.graphics.Bitmap;

/**
 * A list element in our file chooser representing a directory.
 */
public class DirectoryListElement implements ListElement {
    private File path;

    /**
     * Creates a new instance.
     * @param path The path of the directory
     */
    public DirectoryListElement(File path) {
        this.path = path;
    }
    
    /**
     * @return The path of the element
     */
    public File getPath() {
        return path;
    }

    /**
     * @return The icon associated with this element.
     */
    public Bitmap getIcon() {
        return null;
    }
    
    /**
     * @return A string representation of this element.
     */
    public String toString() {
        return path.getName();
    }
    
    @Override
    public boolean equals(Object other_) {
        if (other_ == null || other_.getClass() != getClass()) { return false; }
        ListElement other = (ListElement) other_;
        return getPath() == other.getPath();
    }
}
