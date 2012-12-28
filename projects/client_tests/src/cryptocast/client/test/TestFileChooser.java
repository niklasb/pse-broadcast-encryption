package cryptocast.client.test;

import java.io.File;
import java.util.ArrayList;

import junit.framework.TestCase;
import cryptocast.client.filechooser.*;



public class TestFileChooser extends TestCase {

    protected static File[] expectedList = new File[] {new DirectoryMock(), new DirectoryMockB(),
        new FileMock(), new DirectoryMockB(), new FileMock()};
    
    public void testCanListDirectory() {
        FileChooser fileChooser = new FileChooser();
        DirectoryMock directoryMock = new DirectoryMock();
        ArrayList<ListElement> actualList;

        actualList = fileChooser.listDirectory(directoryMock);
        
        assertNotNull(actualList);
        assertEquals(actualList.size(), expectedList.length);
        // assert correct order (first folders, second files)
        assertEquals(actualList.get(0).getPath().toString(), "Da");
        assertEquals(actualList.get(1).getPath().toString(), "Db");
        assertEquals(actualList.get(2).getPath().toString(), "Db");
        assertEquals(actualList.get(3).getPath().toString(), "F");
        assertEquals(actualList.get(4).getPath().toString(), "F");

       
    }
}


class DirectoryMock extends File {
    private static final long serialVersionUID = -648718268307601037L;

    public DirectoryMock() {
        super("");
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
    public String toString() {
        return "Da";
    }
    @Override
    public File[] listFiles() {
        return TestFileChooser.expectedList;
    }
}
class DirectoryMockB extends File {
    private static final long serialVersionUID = -648718268307601037L;

    public DirectoryMockB() {
        super("");
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
    public String toString() {
        return "Db";
    }
    @Override
    public File[] listFiles() {
        return TestFileChooser.expectedList;
    }
}

class FileMock extends File {
    private static final long serialVersionUID = -648718268307601037L;

    public FileMock() {
        super("");
    }
    public boolean isDirectory() {
        return false;
    }
    @Override
    public boolean isFile() {
        return true;
    }
    @Override
    public File[] listFiles() {
        return  TestFileChooser.expectedList;
    }
    public String toString() {
        return "F";
    }
    
}