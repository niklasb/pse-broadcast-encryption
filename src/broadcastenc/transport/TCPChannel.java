/**
 * This class implements Channel-based communication via TCP. 
 */
class TCPChannel extends MessageInChannel, MessageOutChannel {

	public TCPChannel(String ip, int port) { };
	
    public byte[] recv() { }
    public void send(byte[] message) { }
}