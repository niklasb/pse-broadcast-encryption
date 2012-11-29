package broadcastenc.transport;

/**
 * This class implements Channel-based communication via TCP. 
 */
class TCPChannel extends MulitOutChannel {

    /**
     * Creates a TCP Channel which connects to the given ip.
     * @param ip ip to connect to
     * @param port port to use
     */
	public TCPChannel(InetAddress ip, int port) { };
	
	/**
	 * Receives bytes via the channel.
	 * @return the received bytes
	 */
    public byte[] recv() { }
    /**
	 * Sends bytes via the channel.
	 * @param data the data to send
	 */
    public void send(byte[] data) { }
}