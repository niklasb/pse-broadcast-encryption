package cryptocast.client.filechooser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

/**
 * Represents the state of a file chooser.
 */
public class FileChooserState {
    private File currentDir;
    private ImmutableList<ListElement> items;
    
    private FileChooserState(File currentDir, ImmutableList<ListElement> items) {
        this.currentDir = currentDir;
        this.items = items;
    }
    

    /**
     * Returns the state of the file chooser from the given directory and file pattern.
     * 
     * @param dir The directory of the file.
     * @param filePattern The pattern of the file.
     * @return the state of the file chooser from the given directory and file pattern.
     */
    public static FileChooserState fromDirectory(File dir, String filePattern) {
        File[] files;
        while (null == (files = dir.listFiles())) {
            dir = dir.getParentFile();
        }
        List<ListElement> result = new ArrayList<ListElement>();
        File parent = dir.getParentFile();
        if (parent != null) {
            result.add(new NavigateUpListElement(parent));
        }
        for (File f : files) {
            if (f.isDirectory()) {
                result.add(new DirectoryListElement(f));
            } else if (f.getAbsolutePath().matches(filePattern)) {
                result.add(new FileListElement(f));
            }
        }
        sortElements(result);
        return new FileChooserState(dir, 
                ImmutableList.<ListElement>builder().addAll(result).build());
    }
    
    /**
     * Returns the state of the file chooser from the given directory.
     * 
     * @param dir The directory of the file.
     * @return the state of the file chooser from the given directory.
     */
    public static FileChooserState fromDirectory(File dir) {
        return fromDirectory(dir, ".*");
    }
    
    /**
     * Sorts the given list.
     * 
     * @param list The list to sort.
     */
    public static void sortElements(List<ListElement> list) {
        Collections.sort(list, new ElementComparator());
    }
    
    /**
     * Returns the directory of the file.
     * 
     * @return The directory of the file.
     */
    public File getCurrentDir() { return currentDir; }
    public ImmutableList<ListElement> getItems() { return items; }
}

/**
 * A comparator for ListElements based on their type id.
 */
class ElementComparator implements Comparator<ListElement> {
    @Override
    public int compare(ListElement e1, ListElement e2) {
        return ComparisonChain.start()
                .compare(getTypeId(e1), getTypeId(e2))
                .compare(e1.toString(), e2.toString(), 
                        Ordering.from(String.CASE_INSENSITIVE_ORDER))
                .result();
    }
    
    private int getTypeId(ListElement e) {
        if (e instanceof NavigateUpListElement) { return 0; }
        if (e instanceof DirectoryListElement) { return 1; }
        else { return 2; }
    }
}