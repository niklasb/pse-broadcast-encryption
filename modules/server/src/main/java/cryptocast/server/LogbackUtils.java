package cryptocast.server;

import java.io.File;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.filter.ThresholdFilter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;

/**
 * Helper functions to work with Logback.
 */
public class LogbackUtils {
    private static final String DEFAULT_STDERR_PATTERN = "%5p [%c{0}:%L] %m%n)";
    private static final String DEFAULT_FILE_PATTERN = "%d %5p %t [%c:%L] %m%n)";

    private static LoggerContext ctx = (LoggerContext)LoggerFactory.getILoggerFactory();
    private static Logger rootLogger = ctx.getLogger(Logger.ROOT_LOGGER_NAME);

    public static void removeAllAppenders() {
        rootLogger.detachAndStopAllAppenders();
    }

    public static void setRootLogLevel(Level level) {
        rootLogger.setLevel(level);
    }

    public static void addFileAppender(File logFile, Level level, String pattern) {
        PatternLayoutEncoder plFile = new PatternLayoutEncoder();
        plFile.setContext(ctx);
        plFile.setPattern(pattern);
        plFile.start();

        FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
        fileAppender.setContext(ctx);
        fileAppender.setName("file");
        fileAppender.setFile(logFile.getAbsolutePath());
        fileAppender.setEncoder(plFile);
        fileAppender.addFilter(makeThresholdFilter(level));
        fileAppender.start();
        rootLogger.addAppender(fileAppender);
    }

    public static void addFileAppender(File logFile, Level level) {
        addFileAppender(logFile, level, DEFAULT_FILE_PATTERN);
    }

    public static void addStderrLogger(Level level, String pattern) {
        PatternLayoutEncoder plCon = new PatternLayoutEncoder();
        plCon.setContext(ctx);
        plCon.setPattern(pattern);
        plCon.start();

        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<ILoggingEvent>();
        consoleAppender.setContext(ctx);
        consoleAppender.setEncoder(plCon);
        consoleAppender.setName("console");
        consoleAppender.addFilter(makeThresholdFilter(level));
        consoleAppender.start();
        rootLogger.addAppender(consoleAppender);
    }

    public static void addStderrLogger(Level level) {
        addStderrLogger(level, DEFAULT_STDERR_PATTERN);
    }

    public static ThresholdFilter makeThresholdFilter(Level level) {
        ThresholdFilter conFilter = new ThresholdFilter();
        conFilter.setContext(ctx);
        conFilter.setLevel(level.toString());
        conFilter.start();
        return conFilter;
    }
}
