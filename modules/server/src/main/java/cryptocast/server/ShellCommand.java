package cryptocast.server;

/**
 * Represents the commands available in the shell.
 */
public class ShellCommand {
    private String name, syntax, shortDesc, longDesc;

    /**
     * Creates a new Command with the given values
     * @param name The name of the command
     * @param syntax The syntax of the command
     * @param shortDesc A short description of the functionality
     * @param longDesc A long description of the funktionality
     */
    public ShellCommand(String name, String syntax, String shortDesc, String longDesc) {
        this.name = name;
        this.syntax = syntax;
        this.shortDesc = shortDesc;
        this.longDesc = longDesc;
    }
    
    /**
     * Creates a new Command with the given values.
     * 
     * @param name The name of the command.
     * @param syntax The syntax of the command.
     * @param description A short description of the functionality.
     */
    public ShellCommand(String name, String syntax, String shortDesc) {
        this.name = name;
        this.syntax = syntax;
        this.shortDesc = shortDesc;
        this.longDesc = null;
    }

    /** @return The command name */
    public String getName() { return name; }
    /** @return The command syntax */
    public String getSyntax() { return syntax; }
    /** @return The short command description */
    public String getShortDesc() { return shortDesc; }
    /** @return The long command description */
    public String getLongDesc() { return longDesc; }
}
