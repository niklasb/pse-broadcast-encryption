package cryptocast.crypto;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

public class EllipticCurveGroup<T, P, C extends EllipticCurve<T, P>>
              extends CyclicGroupOfPrimeOrder<P>
              implements Serializable {
    private static final long serialVersionUID = 1276532022589756079L;
    
    private C curve;
    private P basePoint;
    private BigInteger basePointOrder;
    
    public C getCurve() { return curve; }
    public P getBasePoint() { return basePoint; }
    public BigInteger getBasePointOrder() { return basePointOrder; }
    
    public EllipticCurveGroup(C curve,
            P basePoint,
            BigInteger basePointOrder) {
        super(basePoint, basePointOrder);
        this.curve = curve;
        this.basePoint = basePoint;
        this.basePointOrder = basePointOrder;
    }
    
    @Override
    public P combine(P a, P b) {
        return curve.add(a, b);
    }
    
    @Override
    public P twice(P a) {
        return curve.twice(a);
    }
    
    @Override
    public P pow(P a, BigInteger k) {
        return curve.multiply(a, k);
    }
    
    @Override
    public P invert(P a) {
        return curve.negate(a);
    }
    
    @Override
    public P identity() {
        return curve.getInfinity();
    }
    
    /** 
     * Uses Shamir's trick to get much better performance. Uses {@link subList}, so you'd
     * better give it {@link ImmutableLists}.
     */
    @Override
    public P multiexp(List<P> bases, List<BigInteger> exponents) {
        return multiexpShamir(bases, exponents, 5);
    }
    
    public static EllipticCurveGroup<BigInteger, EllipticCurveOverFp.Point, EllipticCurveOverFp>
                               getNamedFpCurve(String name) {
        return fromBCParamSpecFp(ECNamedCurveTable.getParameterSpec(name));
    }
    
    private static EllipticCurveGroup<BigInteger, EllipticCurveOverFp.Point, EllipticCurveOverFp> 
                        fromBCParamSpecFp(ECParameterSpec bcSpec) {
        ECCurve.Fp bcCurve = (ECCurve.Fp) bcSpec.getCurve();
        EllipticCurveOverFp curve = new EllipticCurveOverFp(
                new IntegersModuloPrime(bcCurve.getQ()),
                bcCurve.getA().toBigInteger(),
                bcCurve.getB().toBigInteger());
        ECPoint bcBasePoint = bcSpec.getG();
        EllipticCurveOverFp.Point basePoint = curve.getPoint(
                    bcBasePoint.getX().toBigInteger(),
                    bcBasePoint.getY().toBigInteger()
                    );
        return new EllipticCurveGroup<BigInteger, EllipticCurveOverFp.Point, EllipticCurveOverFp>(
                curve, basePoint, 
                bcSpec.getN());
    }
}
