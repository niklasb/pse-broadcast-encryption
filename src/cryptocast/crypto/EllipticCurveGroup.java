package cryptocast.crypto;

import java.math.BigInteger;

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
    
    public static EllipticCurveGroup<BigInteger, EllipticCurveOverFp>
            getPrime192V1() {
        return fromBCParamSpecFp(ECNamedCurveTable.getParameterSpec("prime192v1"));
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
    
    @Override
    public Point<T> combine(Point<T> a, Point<T> b) {
        return curve.add(a, b);
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
}
