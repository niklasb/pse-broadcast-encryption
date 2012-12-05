package cryptocast.server;

/**
 * This class represents a command and saves all legal commands.
 */
public final class Command {
    /**
     * This Collection contains all possible commands.
     */
    protected static final Command[] COMMANDS = 
            { new Command("addUser", "addUser <name>", "addUser 'name' tries to add a user to the stream", "")};
    
    //argument description? syntax
    private String cmd;
    private String syntax;
    private String shortDescription;
    private String longDescription;
    
    /**
     * Creates a new Command with the given values
     * @param cmd 
     * @param description
     */
    private Command(String cmd, String syntax, String shortDescription, String longDescription) {
        super();
        this.cmd = cmd;
        this.syntax = syntax;
        this.longDescription = longDescription;
        this.shortDescription = shortDescription;
    } 
    
}
