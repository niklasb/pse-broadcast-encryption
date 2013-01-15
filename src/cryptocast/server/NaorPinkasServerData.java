package cryptocast.server;

import cryptocast.crypto.naorpinkas.*;

/**
 * Server data according to Naor-Pinkas.
 */
public class NaorPinkasServerData extends ServerData<NaorPinkasIdentity> {
    private static final long serialVersionUID = -3851019202331587330L;
    
    /**
     * A Naor-Pinkas server.
     */
    protected NaorPinkasServerInterface npServer;
    
    /**
     * Creates NaorPinkas server data with the given Naor-Pinkas server.
     * @param npServer A Naor-Pinkas server.
     */
    public NaorPinkasServerData(NaorPinkasServerInterface npServer) {
        super(npServer, npServer);
        this.npServer = npServer;
    }
}
