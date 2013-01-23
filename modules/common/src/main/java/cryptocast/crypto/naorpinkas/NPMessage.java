package cryptocast.crypto.naorpinkas;

import java.math.BigInteger;
import java.util.List;

import com.google.common.collect.ImmutableList;

import cryptocast.crypto.*;

/**
 * This class represents a NP message sent from the
 * server to the clients.
 */
public class NPMessage<T, G extends CyclicGroupOfPrimeOrder<T>> {
    private byte[] encryptedSecret;
    private BigInteger r;
    private G group;
    int t;
    private ImmutableList<BigInteger> lagrangeCoefficients;
    private ImmutableList<NPShare<T, G>> shares;

    /**
     * Creates a new instance of NaorPinkasMessage with the given parameters.
     * 
     * @param t The degree of the polynomial.
     * @param r The $r$ value.
     * @param encryptedSecret A value, from which we can restore the secret
     *                        together with the value $g^{r \cdot P(0)}$
     * @param schnorr The schnorr group.
     * @param lagrange The lagrange coefficients belonging to the shares.
     * @param shares The shares.
     */
    public NPMessage(int t, BigInteger r, byte[] encryptedSecret, 
                             G group,
                             List<BigInteger> lagrangeCoefficients,
                             List<NPShare<T, G>> shares) {
        assert shares.size() == t && lagrangeCoefficients.size() == t;
        this.t = t;
        this.r = r;
        this.encryptedSecret = encryptedSecret;
        this.group = group;
        this.lagrangeCoefficients = ImmutableList.copyOf(lagrangeCoefficients);
        this.shares = ImmutableList.copyOf(shares);
    }

    /**
     * @return The value $t$.
     */
    public int getT() { return t; }
    
    /**
     * @return The group.
     */
    public G getGroup() {
        return group;
    }
    
    /**
     * @return The encrypted secret.
     */
    public byte[] getEncryptedSecret() { return encryptedSecret; }
    
    /**
     * @return The value $r$.
     */
    public BigInteger getR() { return r; }
    
    /**
     * @return The shares.
     */
    public ImmutableList<NPShare<T, G>> getShares() { return shares; }
    
    /**
     * @return The lagrange coefficients belonging to the shares.
     */
    public ImmutableList<BigInteger> getLagrangeCoeffs() { 
        return lagrangeCoefficients; 
    }
}
