package cryptocast.client.fileChooser;

/**
 * Represents one element from the content of the SD card.
 */
public class ListElement implements Comparable<ListElement> {
    
    private String name;
    private String data;
    private String path;
    
    /**
     * Creates a new ListElement with the given attributes.
     * @param name The name of the ListElement.
     * @param data The kind of data this element consists of, for example folder or file.
     * @param path The path were this ListElement can be found.
     */
    public ListElement(String name, String data, String path) {
        this.name = name;
        this.data = data;
        this.path = path;
    }
    
    @Override
    public int compareTo(ListElement o) {
        // TODO Auto-generated method stub
        return 0;
    }
}
