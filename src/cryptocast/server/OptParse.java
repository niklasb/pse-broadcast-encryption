package cryptocast.server;

import com.beust.jcommander.*;

public class OptParse {
    public static class WithHelp {        
        @Parameter(names = { "-h", "--help" }, help = true, 
                   description = "Show this help")
        protected boolean help;
    }

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
