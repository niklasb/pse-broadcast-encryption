package cryptocast.crypto.naorpinkas;

import java.math.BigInteger;
import cryptocast.crypto.*;
import cryptocast.crypto.EllipticCurveOverFp.Point;

public class TestNPCommunicationEC
                extends TestNPCommunication<Point, 
                         EllipticCurveGroup<BigInteger, Point, EllipticCurveOverFp>> {

    @Override
    protected NPClient<Point, EllipticCurveGroup<BigInteger, Point, EllipticCurveOverFp>>
           makeClient(NPKey<Point, EllipticCurveGroup<BigInteger, Point, EllipticCurveOverFp>> key) {
        return new ECNPClient(key);
    }

    @Override
    protected NPServer<Point, EllipticCurveGroup<BigInteger, Point, EllipticCurveOverFp>> 
                 makeServer(int t) {
        return new ECNPServerFactory().construct(t);
    }
}
