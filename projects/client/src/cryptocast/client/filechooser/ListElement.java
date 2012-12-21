package cryptocast.client.filechooser;


/**
 * A list element in our file chooser, representing an element on the file system.
 */
public interface ListElement {
    /**
     * @return The path of the element
     */
    public String getPath();

    /**
     * @return The icon associated with this element.
     */
    public String getIcon();
}
