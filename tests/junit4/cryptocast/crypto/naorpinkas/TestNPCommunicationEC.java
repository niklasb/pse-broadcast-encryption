package cryptocast.crypto.naorpinkas;

import java.math.BigInteger;
import cryptocast.crypto.*;
import cryptocast.crypto.EllipticCurve.Point;

public class TestNPCommunicationEC
                extends TestNPCommunication<Point<BigInteger>, 
                         EllipticCurveGroup<BigInteger, EllipticCurveOverFp>> {

    @Override
    protected NPClient<Point<BigInteger>, EllipticCurveGroup<BigInteger, EllipticCurveOverFp>>
           makeClient(NPKey<Point<BigInteger>, EllipticCurveGroup<BigInteger, EllipticCurveOverFp>> key) {
        return new ECNPClient(key);
    }

    @Override
    protected NPServer<Point<BigInteger>, EllipticCurveGroup<BigInteger, EllipticCurveOverFp>> 
                 makeServer(int t) {
        return new ECNPServerFactory().construct(t);
    }
}
