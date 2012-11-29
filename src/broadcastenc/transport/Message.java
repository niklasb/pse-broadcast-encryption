package broadcastenc.transport;

/**
 * This class represents a Message which can be packetized, sent and then parsed.
 */
public class Message {
    /** A message which contains a key update. */
    public static final KEY_UPDATE; 
    /** A message which contains user data. */
    public static final USER_DATA; 
    
    
    /** This message's type. */
    private int type;
    /** This message's data. */
    private byte[] data;
    
    
    /**
     * Creates a Message of the given type. 
     * @param type this message's type
     */
    public Message(int type) { }
    
    /**
     * Set this message's data.
     * @param data the data
     */
    public void setData(byte[] data) { }
    /**
     * Returns this message's data.
     * @return this message's data
     */
    public data[] getData() { }

}