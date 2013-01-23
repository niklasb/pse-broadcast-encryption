package cryptocast.client.filechooser;

import java.io.File;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

/**
 * A list element in our file chooser, representing an element on the file system.
 */
public interface ListElement {    
    /**
     * @return The path of the element
     */
    public File getPath();

    /**
     * @return The icon associated with this element.
     */
    public Drawable getIcon(Resources res);
}
