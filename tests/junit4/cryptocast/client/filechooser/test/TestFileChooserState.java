package cryptocast.client.filechooser.test;

import java.io.File;
import java.util.Arrays;

import com.google.common.collect.ImmutableList;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import cryptocast.client.filechooser.DirectoryListElement;
import cryptocast.client.filechooser.FileChooserState;
import cryptocast.client.filechooser.FileListElement;
import cryptocast.client.filechooser.ListElement;
import cryptocast.client.filechooser.NavigateUpListElement;
import cryptocast.client.filechooser.test.*;

public class TestFileChooserState {
    @Test
    public void sortsCorrectly() {
        ListElement[] elements = {
            new FileListElement(mockRegularFile("/a/Z", null)),
            new FileListElement(mockRegularFile("/b/k", null)),
            new FileListElement(mockRegularFile("/c/A", null)),
            new NavigateUpListElement(mockDirectory("/", null, null)),
            new DirectoryListElement(mockDirectory("/a/Z", null, null)),
            new DirectoryListElement(mockDirectory("/a/k", null, null)),
            new DirectoryListElement(mockDirectory("/a/A", null, null)),
        };
        ListElement[] expected = {
            elements[3], elements[6], elements[5], elements[4],
            elements[2], elements[1], elements[0],
        };
        FileChooserState.sortElements(Arrays.asList(elements));
        assertArrayEquals(expected, elements);
    }

    @Test
    public void fromDirectoryWorksWithExistingDir() {
        File[] dirChildren = new File[2];
        File[] rootChildren = new File[1];
        File root = mockDirectory("/", null, rootChildren);
        File dir = mockDirectory("/foo", root, dirChildren);
        rootChildren[0] = dir;
        dirChildren[0] = mockDirectory("/foo/b", dir, null);
        dirChildren[1] = mockRegularFile("/foo/a", dir);
        ListElement[] expected = {
            new NavigateUpListElement(root),
            new DirectoryListElement(dirChildren[0]),
            new FileListElement(dirChildren[1]),
        };
        FileChooserState state = FileChooserState.fromDirectory(dir);
        assertEquals(ImmutableList.copyOf(expected), state.getItems());
        assertEquals(dir, state.getCurrentDir());
    }
    
    @Test
    public void fromDirectoryWorksWithBadDir() {
        File[] dirChildren = new File[2];
        File[] rootChildren = new File[1];
        File root = mockDirectory("/", null, rootChildren);
        File dir = mockDirectory("/foo", root, dirChildren);
        rootChildren[0] = dir;
        dirChildren[0] = mockDirectory("/foo/b", dir, null);
        dirChildren[1] = mockRegularFile("/foo/a", dir);
        File failDir = mockDirectory("/foo/b/bar", dirChildren[0], null);
        ListElement[] expected = {
            new NavigateUpListElement(root),
            new DirectoryListElement(dirChildren[0]),
            new FileListElement(dirChildren[1]),
        };
        FileChooserState state = FileChooserState.fromDirectory(failDir);
        assertEquals(ImmutableList.copyOf(expected),
                     state.getItems());
        assertEquals(dir, state.getCurrentDir());
    }
    
    private File mockRegularFile(String path, File parent) {
        return mockFile(path, parent, null, false, true);
    }
    
    private File mockDirectory(String path, File parent, File[] children) {
        return mockFile(path, parent, children, true, false);
    }
    
    private File mockFile(String path, File parent, File[] children, boolean isDir, boolean isFile) {
        File f = spy(new File(path));
        when(f.getParentFile()).thenReturn(parent);
        when(f.isFile()).thenReturn(isFile);
        when(f.isDirectory()).thenReturn(isDir);
        when(f.listFiles()).thenReturn(children);
        return f;
    }
}