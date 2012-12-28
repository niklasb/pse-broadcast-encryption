package cryptocast.client.filechooser.test;

import java.io.File;
import java.util.Arrays;

import junit.framework.TestCase;
import cryptocast.client.filechooser.*;

public class TestFileChooserState extends TestCase {
    public void testSortsCorrectly() {
        ListElement[] elements = {
            new FileListElement(new FileMock("/a/Z")),
            new FileListElement(new FileMock("/b/k")),
            new FileListElement(new FileMock("/c/A")),
            new NavigateUpListElement(new DirectoryMock("/")),
            new DirectoryListElement(new DirectoryMock("/a/Z")),
            new DirectoryListElement(new DirectoryMock("/b/k")),
            new DirectoryListElement(new DirectoryMock("/c/A")),
        };
        ListElement[] expected = {
            elements[3], elements[6], elements[5], elements[4],
            elements[2], elements[1], elements[0],
        };
        FileChooserState.sortElements(Arrays.asList(elements));
        assertEquals(Arrays.asList(expected), Arrays.asList(elements));
    }
    
    public void testFromDirectoryRegular() {
        fail("not implemented");
    }
    
    public void testFromDirectoryDeleted() {
        fail("not implemented");
    }
}


class DirectoryMock extends File {
    private static final long serialVersionUID = 1;
    private File[] children;
    
    public DirectoryMock(String path, File[] children) {
        super(path);
        this.children = children;
    }
    public DirectoryMock(String path) {
        super(path);
        this.children = new File[0];
    }
    
    @Override
    public boolean isDirectory() {
        return true;
    }
    
    @Override
    public boolean isFile() {
        return false;
    }
    
    @Override
    public File[] listFiles() {
        return children;
    }
}

class FileMock extends File {
    private static final long serialVersionUID = 1;

    public FileMock(String path) {
        super(path);
    }
    
    @Override
    public boolean isDirectory() {
        return false;
    }
    
    @Override
    public boolean isFile() {
        return true;
    }
    
    @Override
    public File[] listFiles() {
        return null;
    }
}