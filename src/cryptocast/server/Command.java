package cryptocast.server;

/**
 * This class represents a command and saves all legal commands.
 */
public class Command {
    /**
     * This Collection contains all possible commands.
     */
    protected static final Command[] commands = 
            { new Command("addUser", "addUser 'name' tries to add a user to the stream", "")};
    
    //argument description? can this class check arguments of a command?
    private String cmd;
    private String shortDescription;
    private String longDescription;
    
    /**
     * Creates a new Command with the given values
     * @param cmd 
     * @param description
     */
    private Command(String cmd, String shortDescription, String longDescription) {
        super();
        this.cmd = cmd;
        this.longDescription = longDescription;
        this.shortDescription = shortDescription;
    } 
    
}
