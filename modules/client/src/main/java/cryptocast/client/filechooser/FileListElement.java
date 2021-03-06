package cryptocast.client.filechooser;

import java.io.File;

import cryptocast.client.R;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;


/**
 * A list element in our file chooser, representing a file.
 */
public class FileListElement implements ListElement {
    private File path;

    /**
     * Creates a new instance.
     * @param path The path of the file
     */
    public FileListElement(File path) {
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
    public Drawable getIcon(Resources res) {
        return res.getDrawable(R.drawable.file24);
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
