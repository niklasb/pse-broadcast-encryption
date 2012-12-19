package cryptocast.server;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Represents the commands available in the shell.
 */
public enum ShellCommand {
    HELP (
            "help",
            "help",
            "Shows the command line help",
            null),
    ADD_USER (
            "addUser",
            "addUser <name>",
            "Adds a new user to the group of recipients",
            null),
    REVOKE_USER (
            "revokeUser",
            "revokeUser <name>",
            "Revokes a user",
            null),
    ;

    public static SortedMap<String, ShellCommand> commands = 
            new TreeMap<String, ShellCommand>();
    static {
        for (ShellCommand cmd : ShellCommand.class.getEnumConstants()) {
            commands.put(cmd.getName(), cmd);
        }
    }

    private String name, syntax, shortDesc, longDesc;

    /**
     * Creates a new Command with the given values
     * @param name The name of the command
     * @param syntax The syntax of the command
     * @param description A short description of the functionality
     */
    private ShellCommand(String name, String syntax, String shortDesc, String longDesc) {
        this.name = name;
        this.syntax = syntax;
        this.shortDesc = shortDesc;
        this.longDesc = longDesc != null ? longDesc : shortDesc;
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
