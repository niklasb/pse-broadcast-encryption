package cryptocast.crypto.naorpinkas;

import java.math.BigInteger;

import cryptocast.crypto.*;
import cryptocast.crypto.EllipticCurveOverFp.*;

/** Creates instances of NP servers that use the variant based on elliptic
 * curves. */
public class ECNPServerFactory implements NPServerFactory {
    /**
     * @param t The parameter $t$ of the NP scheme.
     * @param g An EC group.
     * @return An NP server instance.
     */
    public ECNPServer construct(int t,
                       EllipticCurveGroup<BigInteger, Point, EllipticCurveOverFp> g) {
        return new ECNPServer(NPServerContext.generate(t, g));
    }

    @Override
    public ECNPServer construct(int t) {
        return construct(t, EllipticCurveGroup.getSecp160R1());
    }
}
