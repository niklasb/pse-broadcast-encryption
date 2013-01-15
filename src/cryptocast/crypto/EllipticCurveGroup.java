package cryptocast.crypto;

import java.math.BigInteger;
import java.util.List;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

import cryptocast.crypto.EllipticCurve.*;

public class EllipticCurveGroup<T, C extends EllipticCurve<T>>
              extends CyclicGroupOfPrimeOrder<Point<T>> {
    private C curve;
    private EllipticCurve.ConcretePoint<T> basePoint;
    private BigInteger basePointOrder;
    
    public C getCurve() { return curve; }
    public ConcretePoint<T> getBasePoint() { return basePoint; }
    public BigInteger getBasePointOrder() { return basePointOrder; }
    
    public EllipticCurveGroup(C curve,
            ConcretePoint<T> basePoint,
            BigInteger basePointOrder) {
        super(basePoint, basePointOrder);
        this.curve = curve;
        this.basePoint = basePoint;
        this.basePointOrder = basePointOrder;
    }
    
    @Override
    public Point<T> combine(Point<T> a, Point<T> b) {
        return curve.add(a, b);
    }
    
    @Override
    public Point<T> twice(Point<T> a) {
        return curve.twice(a);
    }
    
    @Override
    public Point<T> pow(Point<T> a, BigInteger k) {
        return curve.multiply(a, k);
    }
    
    @Override
    public Point<T> invert(Point<T> a) {
        return curve.negate(a);
    }
    
    @Override
    public Point<T> identity() {
        return curve.getInfinity();
    }
    
    /** 
     * Uses Shamir's trick to get much better performance. Uses {@link subList}, so you'd
     * better give it {@link ImmutableLists}.
     */
    @Override
    public Point<T> multiexp(List<Point<T>> bases, List<BigInteger> exponents) {
        return multiexpShamir(bases, exponents, 5);
    }
    
    public static EllipticCurveGroup<BigInteger, EllipticCurveOverFp> getNamedCurve(String name) {
        return fromBCParamSpecFp(ECNamedCurveTable.getParameterSpec(name));
    }
    
    private static EllipticCurveGroup<BigInteger, EllipticCurveOverFp> 
                        fromBCParamSpecFp(ECParameterSpec bcSpec) {
        ECCurve.Fp bcCurve = (ECCurve.Fp) bcSpec.getCurve();
        EllipticCurveOverFp curve = new EllipticCurveOverFp(
                new IntegersModuloPrime(bcCurve.getQ()),
                bcCurve.getA().toBigInteger(),
                bcCurve.getB().toBigInteger());
        ECPoint bcBasePoint = bcSpec.getG();
        ConcretePoint<BigInteger> basePoint = curve.getPoint(
                    bcBasePoint.getX().toBigInteger(),
                    bcBasePoint.getY().toBigInteger()
                    );
        return new EllipticCurveGroup<BigInteger, EllipticCurveOverFp>(
                curve, basePoint, 
                bcSpec.getN());
    }
}
