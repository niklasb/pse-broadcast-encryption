package cryptocast.server.programs;

import cryptocast.server.*;
import cryptocast.util.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

import com.beust.jcommander.Parameter;

/**
 * The main method to start the server.
 */
public final class Server {
    static class Options extends OptParse.WithHelp {
        @Parameter(names = { "-l", "--logfile" }, description = "Log output file")
        private File logFile = FileUtils.expandPath("~/.cryptocast/cryptocast.log");

        @Parameter(names = { "-f", "--database" }, description = "User database file")
        private File databaseFile = FileUtils.expandPath("~/.cryptocast/users.db");

        @Parameter(names = { "-p", "--port" }, description = "Listen port")
        private int listenPort = 21337;

        @Parameter(names = { "-b", "--bind-address"}, description = "Listen address")
        private String listenAddr = "127.0.0.1";
    }
    
    /**
     * @param args command line arguments
     */
    public static void main(String[] argv) throws Exception {
        Options opts = OptParse.parseArgs(new Options(), "cryptocast-server", argv);
        
        Controller control = Controller.start(
                opts.databaseFile,
                new InetSocketAddress(opts.listenAddr, opts.listenPort));
        new Shell(new BufferedReader(new InputStreamReader(System.in)), 
                  System.out, System.err, control).run();
    }
}
