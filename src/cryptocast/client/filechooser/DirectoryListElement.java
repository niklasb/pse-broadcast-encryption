package cryptocast.client.filechooser;

import java.nio.file.Path;

/**
 * A list element in our file chooser, representing a directory.
 */
public class DirectoryListElement implements ListElement {
    /**
     * Creates a new instance.
     * @param path The path of the directory
     */
    public DirectoryListElement(Path path) { }

    /**
     * @return the path of the element
     */
    public Path getPath() { return null; }

    /**
     * @return The icon associated with this element
     */
    public Resource getIcon() { return null; }
}
