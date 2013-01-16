package cryptocast.crypto.naorpinkas;

import java.math.BigInteger;
import cryptocast.crypto.*;

public class TestNPCommunicationSchnorr 
                extends TestNPCommunication<BigInteger, SchnorrGroup> {
    @Override
    protected NPClient<BigInteger, SchnorrGroup> makeClient(
                 NPKey<BigInteger, SchnorrGroup> key) {
        return new SchnorrNPClient(key);
    }

    @Override
    protected NPServer<BigInteger, SchnorrGroup> makeServer(int t) {
        return new SchnorrNPServerFactory().construct(t);
    }
}
