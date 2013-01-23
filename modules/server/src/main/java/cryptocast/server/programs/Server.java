package cryptocast.server.programs;

import cryptocast.crypto.naorpinkas.*;
import cryptocast.server.*;
import cryptocast.util.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;

import com.beust.jcommander.Parameter;

/**
 * The main method to start the server.
 */
public final class Server {    
    private static final Logger log = LoggerFactory
            .getLogger(Server.Options.class);
    
    static class Options extends OptParse.WithHelp {
        @Parameter(names = { "-l", "--logfile" }, description = "Log output file")
        private File logFile = FileUtils.expandPath("~/.cryptocast/cryptocast.log");

        @Parameter(names = { "-f", "--database" }, description = "User database file")
        private File databaseFile = FileUtils.expandPath("~/.cryptocast/users.db");

        @Parameter(names = { "-p", "--port" }, description = "Listen port")
        private int listenPort = 21337;

        @Parameter(names = { "-b", "--listen-address"}, description = "Listen address")
        private String listenAddr = "0.0.0.0";
        
        @Parameter(names = { "-i", "--key-broadcast-interval" }, description = "Key broadcast interval in seconds")
        private int keyBroadcastIntervalSecs = 15;
        
        @Parameter(names = { "-g", "--group" }, description = "The group structure to use ('ec' or 'schnorr')")
        private String group = "schnorr";
        
        @Parameter(names = { "-v", "--verbosity" }, 
                   description = "Control the level of debug output on STDERR (trace, debug, info, warn, error)")
        private String verbosity = "info";
        
        @Parameter(names = { "--trace" }, description = "Enable tracing")
        private boolean tracing = false;
    }
    
    /**
     * @param args command line arguments
     */
    public static void main(String[] argv) throws Exception {
        Options opts = OptParse.parseArgs(new Options(), "server", argv);
        Level logLevel = Level.toLevel(opts.verbosity);
        
        System.out.printf("Logging to STDERR (level %s) and %s\n", logLevel, opts.logFile);
        if (opts.tracing || Level.TRACE.isGreaterOrEqual(logLevel)) {
            LogbackUtils.setRootLogLevel(Level.TRACE);
        }
        LogbackUtils.removeAllAppenders();
        LogbackUtils.addFileAppender(opts.logFile, Level.ALL);
        LogbackUtils.addStderrLogger(logLevel);
        
        NPServerFactory factory = null;
        if (opts.group.equals("schnorr")) {
            log.info("Using a schnorr group over GF(p) with p = 1024 bits");
            factory = new SchnorrNPServerFactory();
        } else if (opts.group.equals("ec")) {
            log.info("Using an elliptic curve over GF(p) with p = 160 bits "
                   + "and a generating point of order q = 160 bits");
            factory = new ECNPServerFactory();
        } else {
            System.err.println("Invalid group: " + opts.group);
            System.exit(1);
        }
        Controller control = Controller.start(
                opts.databaseFile,
                new InetSocketAddress(opts.listenAddr, opts.listenPort),
                opts.keyBroadcastIntervalSecs, factory);
        new Shell(new BufferedReader(new InputStreamReader(System.in)), 
                  System.out, System.err, control).run();
    }
}
