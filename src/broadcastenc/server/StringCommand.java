package broadcastenc.server;

/**
 * This class represents a command consisting of a String.
 */
public class StringCommand {
    
    private Controller receiver;
    private Shell sender;
    private String[] information;
    
    public StringCommand(Controller receiver, Shell sender, String[] information) {
        super();
        this.receiver = receiver;
        this.sender = sender;
        this.information = information;
    }
    
    /**
     * Tells the receiver to execute the specified command
     */
    public void compute() {
        
    }
           
    
    
}
