package cryptocast.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.sound.sampled.UnsupportedAudioFileException;

import org.junit.Before;
import org.junit.Test;

import static cryptocast.util.FileUtils.expandPath;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.base.Optional;

import cryptocast.crypto.*;
import cryptocast.crypto.naorpinkas.*;
import cryptocast.util.CommandLineInterface.Exit;
import cryptocast.util.InteractiveCommandLineInterface.CommandError;

public class TestShell {
    private Shell sut;
    SchnorrNaorPinkasServer npServer = 
            (SchnorrNaorPinkasServer) new SchnorrNaorPinkasServerFactory().construct(10);
    @Mock private BufferedReader in;
    @Mock private PrintStream out;
    @Mock private PrintStream err;
    @Mock private Controller control;
    @Mock private NaorPinkasServerData model;
    
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
    
    @Test
    public void mp3Stream() throws CommandError, Exit, IOException, UnsupportedAudioFileException {
        String[] args = {"C://test.mp3"};
        sut.performCommand("stream-mp3", args);
        File file = expandPath(args[0]);
        verify(control).streamAudio(file);
    }
    
    @Test
    public void revokeOne() throws CommandError, Exit, NoMoreRevocationsPossibleError {
        String name = "bob";
        String[] args = {name};
        when(control.getModel()).thenReturn(model);
        ArrayList<User<NaorPinkasIdentity>> users = new ArrayList<User<NaorPinkasIdentity>>();
        User<NaorPinkasIdentity> myBob = new User<NaorPinkasIdentity>(name, npServer.getIdentity(3));
        users.add(myBob);
        when(model.getUserByName(name)).thenReturn(Optional.fromNullable(myBob));
        when(model.revoke(users)).thenReturn(true);
        sut.performCommand("revoke", args);
        verify(model).revoke(users);
    }
    
    @Test
    public void revokeSeveral() throws CommandError, Exit, NoMoreRevocationsPossibleError {
        String name1 = "bob";
        String name2 = "alice";
        String name3 = "james";
        String[] args = {name1, name2, name3};
        when(control.getModel()).thenReturn(model);
        ArrayList<User<NaorPinkasIdentity>> users = new ArrayList<User<NaorPinkasIdentity>>();
        User<NaorPinkasIdentity> myBob = new User<NaorPinkasIdentity>(name1, npServer.getIdentity(3));
        User<NaorPinkasIdentity> myAlice = new User<NaorPinkasIdentity>(name2, npServer.getIdentity(5));
        User<NaorPinkasIdentity> myJames = new User<NaorPinkasIdentity>(name3, npServer.getIdentity(7));
        users.add(myBob);
        users.add(myAlice);
        users.add(myJames);
        when(model.getUserByName(name1)).thenReturn(Optional.fromNullable(myBob));
        when(model.getUserByName(name2)).thenReturn(Optional.fromNullable(myAlice));
        when(model.getUserByName(name3)).thenReturn(Optional.fromNullable(myJames));
        when(model.revoke(users)).thenReturn(true);
        sut.performCommand("revoke", args);
        verify(model).revoke(users);
    }
    
    @Test
    public void unrevoke() throws CommandError, Exit, NoMoreRevocationsPossibleError {
        String name = "alice";
        String[] args = {name};
        when(control.getModel()).thenReturn(model);
        User<NaorPinkasIdentity> myAlice = new User<NaorPinkasIdentity>(name, npServer.getIdentity(12));
        when(model.getUserByName(name)).thenReturn(Optional.fromNullable(myAlice));
        when(model.unrevoke(myAlice)).thenReturn(true);
        sut.performCommand("unrevoke", args);
        verify(model).unrevoke(myAlice);
    }
    
    @Test
    public void parseNotAnInt() {
        Optional<Integer> result = Shell.parseInt("?!/($%?");
        assertFalse(result.isPresent());
    }
    
    @Test
    public void parseRealInts() {
        Optional<Integer> result = Shell.parseInt("-12");
        assertTrue(result.isPresent());
        int expected = -12;
        assertTrue(expected == result.get());
    }

}
