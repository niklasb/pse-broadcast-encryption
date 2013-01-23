package cryptocast.server;

import com.beust.jcommander.*;

/**
 * A Command line option parser.
 */
public class OptParse {
    public static class WithHelp {        
        @Parameter(names = { "-h", "--help" }, help = true, 
                   description = "Show this help")
        /**
         * Whether to print usage message.
         */
        protected boolean help;
    }

    /**
     * Parses the program's arguments
     * 
     * @param jcOpts jcommander options for parsing.
     * @param programName Program name.
     * @param argv Program arguments.
     * @return parsed options.
     */
    public static <T extends WithHelp> T parseArgs(
            T jcOpts, String programName, String[] argv) {
        JCommander jc = new JCommander(jcOpts);
        jc.setProgramName(programName);
        try {
            jc.parse(argv);
        } catch (ParameterException e) {
            System.err.println("Parameter error: " + e.getMessage());
            System.err.println();
            jc.usage(); 
            System.exit(1);
        }
        if (jcOpts.help) {
            jc.usage();
            System.exit(0);
        }
        return jcOpts;
    }
}
