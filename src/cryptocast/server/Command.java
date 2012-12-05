package cryptocast.server;

import java.util.Map;

/**
 * This enum represents the commands available in the
 */
public enum Command {
    ADD_USER    ("addUser",
                     "addUser <name>",
                     "adds a new user to the group of recipients"),
    REVOKE_USER ("revokeUser",
                     "revokeUser <name>",
                     "revokes a user"),
    ;

    public static Map<String, Command> commands;
    static {
        for (Command cmd : Command.class.getEnumConstants())
            commands.put(cmd.getName(), cmd);
    }

    private String name;
    private String syntax;
    private String description;

    /**
     * Creates a new Command with the given values
     * @param name The name of the command
     * @param syntax The syntax of the command
     * @param description A short description of the functionality
     */
    private Command(String name, String syntax, String description) {
        this.name = name;
        this.syntax = syntax;
        this.description = description;
    }

    /** @return The command name */
    public String getName() { return name; }

    /** @return The command syntax */
    public String getSyntax() { return syntax; }

    /** @return The command description */
    public String getDescription() { return description; }
}
