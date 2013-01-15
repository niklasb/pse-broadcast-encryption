package cryptocast.crypto;

import java.math.BigInteger;

import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECFieldElement;

public class EllipticCurveOverFp extends EllipticCurve<BigInteger> {
    private IntegersModuloPrime field;
    private ECCurve.Fp bcCurve;
    
    public EllipticCurveOverFp(IntegersModuloPrime field,
                               BigInteger a,
                               BigInteger b) {
        this.field = field;
        this.a = a;
        this.b = b;
        bcCurve = new ECCurve.Fp(field.getP(), a, b);
    }
    
    @Override
    public IntegersModuloPrime getField() {
        return field;
    }

    @Override
    public Point<BigInteger> negate(Point<BigInteger> a) {
        if (isInfinity(a)) { return a; }
        ConcretePoint<BigInteger> ca = (ConcretePoint<BigInteger>) a;
        return getPoint(ca.getX(), field.negate(ca.getY()));
    }

    @Override
    public Point<BigInteger> add(Point<BigInteger> a, Point<BigInteger> b) {
        if (isInfinity(a)) { return b; }
        if (isInfinity(b)) { return a; }
        
        ConcretePoint<BigInteger> ca = (ConcretePoint<BigInteger>) a,
                                  cb = (ConcretePoint<BigInteger>) b;
        BigInteger ax = ca.getX(), bx = cb.getX(), 
                   ay = ca.getY(), by = cb.getY();
        
        if (ax.equals(bx)) {
            if (ay.equals(by)) {
                return twice(a);
            }
            return getInfinity();
        }
        
        BigInteger
        gamma = field.divide(
                  field.subtract(by, ay),
                  field.subtract(bx, ax)),
        x3 = field.subtract(
                field.subtract(field.square(gamma), ax),
                bx),
        y3 = field.subtract(
                field.multiply(gamma, field.subtract(ax, x3)),
                ay);
        return getPoint(x3, y3);
    }
    
    @Override
    public Point<BigInteger> twice(Point<BigInteger> a) {
        if (isInfinity(a)) { return a; }
        
        ConcretePoint<BigInteger> concreteA = (ConcretePoint<BigInteger>) a;
        BigInteger x = concreteA.getX(), y = concreteA.getY();
        
        if (field.isZero(y)) {
            return getInfinity();
        }
        
        BigInteger
        gamma = field.divide(
                  field.add(
                        field.multiply(field.three(), field.square(x)),
                        getA()),
                  field.multiply(field.two(), y)),

        x3 = field.subtract(
                field.square(gamma),
                field.multiply(field.two(), x)),
        y3 = field.subtract(
                field.multiply(gamma, field.subtract(x, x3)),
                y);
        
        return getPoint(x3, y3);
    }
    
//    @Override
//    public Point<BigInteger> multiply(Point<BigInteger> p, BigInteger k) {
//        Point<BigInteger> q = getInfinity();
//        for (int i = 0, len = k.bitLength(); i < len; i++) {
//            if (k.testBit(i)) {
//                q = add(q, p);
//            }
//            p = twice(p);
//        }
//        return q;
//    }
    
//    @Override
//    public Point<BigInteger> multiply(Point<BigInteger> p, BigInteger k) {
//        if (k.signum() == 0) {
//            return getInfinity();
//        }
//        BigInteger h = k.multiply(BigInteger.valueOf(3));
//
//        Point<BigInteger> neg = negate(p), R = p;
//
//        for (int i = h.bitLength() - 2; i > 0; --i) {
//            R = twice(R);
//
//            boolean hBit = h.testBit(i),
//                    kBit = k.testBit(i);
//
//            if (hBit != kBit) {
//                R = add(R, hBit ? p : neg);
//            }
//        }
//
//        return R;
//    }
    
    @Override
    public Point<BigInteger> multiply(Point<BigInteger> p, BigInteger k) {
        if (isInfinity(p)) { return p; }
        ConcretePoint<BigInteger> cp = (ConcretePoint<BigInteger>) p;
        ECPoint bcp = bcCurve.createPoint(cp.getX(), cp.getY(), false);
        bcp = bcp.multiply(k);
        if (bcp.isInfinity()) {
            return getInfinity();
        } else {
            return getPoint(bcp.getX().toBigInteger(), bcp.getY().toBigInteger());
        }
    }
}
