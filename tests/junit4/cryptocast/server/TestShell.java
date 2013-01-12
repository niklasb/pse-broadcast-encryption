package cryptocast.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import cryptocast.util.CommandLineInterface.Exit;
import cryptocast.util.InteractiveCommandLineInterface.CommandError;

public class TestShell {
    private Shell sut;
    @Mock private BufferedReader in;
    @Mock private PrintStream out;
    @Mock private PrintStream err;
    @Mock private Controller control;
    
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        sut = new Shell(in, out, err, control);
    }
    
    @Test(expected=CommandError.class)
    public void invalidCommand() throws Exit, CommandError {
        String[] args = {""};
        sut.performCommand("42", args);
    }

    @Test
    public void HelpAdd() throws CommandError, Exit {
        String[] args = {"add"};
        sut.performCommand("help", args);
        verify(out).printf("Usage: %s %s\n", "add", "<name>");
    }
    
    @Test
    public void showHelp() throws CommandError, Exit {
        String[] args = {};
        sut.performCommand("help", args);
        verify(out).println("Use `help <cmd>' to get more information about a specific command");
    }
    
    @Test
    public void reInit() throws CommandError, Exit, IOException {
        String[] args = {"100"};
        sut.performCommand("init", args);
        sut.performCommand("init", args);
        verify(control, times(2)).reinitializeCrypto(100);
    }
    
    @Test(expected=CommandError.class)
    public void initInvalid() throws CommandError, Exit, IOException {
        String[] args = {"-1"};
        sut.performCommand("init", args);
    }

}
