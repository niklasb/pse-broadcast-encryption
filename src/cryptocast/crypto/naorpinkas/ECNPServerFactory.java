package cryptocast.crypto.naorpinkas;

import java.math.BigInteger;

import cryptocast.crypto.*;

public class ECNPServerFactory implements NPServerFactory {
    public ECNPServer construct(int t, 
                       EllipticCurveGroup<BigInteger, EllipticCurveOverFp> g) {
        return new ECNPServer(NPServerContext.generate(t, g));
    }
    
    @Override
    public ECNPServer construct(int t) {
        return construct(t, EllipticCurveGroup.getNamedFpCurve("secp160r1"));
    }
}
