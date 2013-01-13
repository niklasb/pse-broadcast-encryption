package cryptocast.crypto.naorpinkas;

import java.math.BigInteger;

import cryptocast.crypto.*;

public abstract class NaorPinkasGroup {
    public abstract Field<BigInteger> getPolynomialField();
}
