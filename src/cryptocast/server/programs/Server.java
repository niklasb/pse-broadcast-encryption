package cryptocast.server.programs;

import cryptocast.server.*;
import cryptocast.util.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;

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

        @Parameter(names = { "-l", "--listen-address"}, description = "Listen address")
        private String listenAddr = "127.0.0.1";
    }
    
    /**
     * @param args command line arguments
     */
    public static void main(String[] argv) throws Exception {
        Options opts = OptParse.parseArgs(new Options(), "server", argv);
        
        System.out.println("Logging to " + opts.logFile);
        initLogging(opts.logFile, Level.WARN);
        
        Controller control = Controller.start(
                opts.databaseFile,
                new InetSocketAddress(opts.listenAddr, opts.listenPort));
        new Shell(new BufferedReader(new InputStreamReader(System.in)), 
                  System.out, System.err, control).run();
    }
    
    private static void initLogging(File logFile, Level stderrLevel) {
        LoggerContext ctx = (LoggerContext)LoggerFactory.getILoggerFactory();
        Logger rootLogger = ctx.getLogger(Logger.ROOT_LOGGER_NAME);
        
        rootLogger.detachAndStopAllAppenders();

        PatternLayoutEncoder plFile = new PatternLayoutEncoder();
        plFile.setContext(ctx);
        plFile.setPattern("%d %5p %t [%c:%L] %m%n)");
        plFile.start();
        
        FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
        fileAppender.setContext(ctx);
        fileAppender.setName("file");
        fileAppender.setFile(logFile.getAbsolutePath());
        fileAppender.setEncoder(plFile);
        fileAppender.start();
        rootLogger.addAppender(fileAppender);
        
        PatternLayoutEncoder plCon = new PatternLayoutEncoder();
        plCon.setContext(ctx);
        plCon.setPattern("%5p %t [%c:%L] %m%n)");
        plCon.start();
        
        ThresholdFilter conFilter = new ThresholdFilter();
        conFilter.setContext(ctx);
        conFilter.setLevel(stderrLevel.toString());
        conFilter.start();
        
        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<ILoggingEvent>();
        consoleAppender.setContext(ctx);
        consoleAppender.setEncoder(plCon);
        consoleAppender.setName("console");
        consoleAppender.addFilter(conFilter);
        consoleAppender.start();
        rootLogger.addAppender(consoleAppender);
    }
}
