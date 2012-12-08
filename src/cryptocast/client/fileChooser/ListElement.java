package cryptocast.client.fileChooser;

public class ListElement implements Comparable<ListElement> {
    
    private String name;
    private String data;
    private String path;
    
    /**
     * Creates a new Option with the given attributes.
     * @param name The name of the option.
     * @param data The kind of data this option consists of, for example folder or file.
     * @param path The path were this option can be found.
     */
    public ListElement(String name,String data,String path) {
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
