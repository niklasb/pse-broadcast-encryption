package cryptocast.server;

import cryptocast.crypto.naorpinkas.NaorPinkasIdentity;
import cryptocast.crypto.naorpinkas.NaorPinkasServer;

public class NaorPinkasServerData extends ServerData<NaorPinkasIdentity> {
    private static final long serialVersionUID = -3851019202331587330L;
    protected NaorPinkasServer npServer;
    
    public NaorPinkasServerData(NaorPinkasServer npServer) {
        super(npServer, npServer);
        this.npServer = npServer;
    }
}
