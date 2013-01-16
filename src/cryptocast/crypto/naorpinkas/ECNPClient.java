package cryptocast.crypto.naorpinkas;

import java.math.BigInteger;

import com.google.common.io.ByteArrayDataInput;

import cryptocast.crypto.*;
import cryptocast.crypto.EllipticCurve.*;
import cryptocast.crypto.EllipticCurveOverFp.*;
import cryptocast.util.ArrayUtils;

public class ECNPClient extends 
         NPClient<Point<BigInteger>, 
                  EllipticCurveGroup<BigInteger, EllipticCurveOverFp>>  {
    public ECNPClient(NPKey<Point<BigInteger>, 
                            EllipticCurveGroup<BigInteger, EllipticCurveOverFp>> key) {
        super(key);
    }

    @Override
    protected NPShare<Point<BigInteger>, 
                      EllipticCurveGroup<BigInteger, EllipticCurveOverFp>> 
            readShare(
                           ByteArrayDataInput in) {
        BigInteger i = new BigInteger(readBytes(in));
        BigInteger x = new BigInteger(readBytes(in));
        byte info = in.readByte();
        Point<BigInteger> grpi = 
                getGroup().getCurve().uncompress(new CompressedPoint(x, info));
        return new NPShare<Point<BigInteger>, 
                           EllipticCurveGroup<BigInteger, EllipticCurveOverFp>>(
                                   i, grpi, getGroup());
    }

    @Override
    protected byte[] decryptSecretWithItem(byte[] encryptedSecret,
                                           Point<BigInteger> p) {
        BigInteger xor = new BigInteger(encryptedSecret);
        BigInteger key = ((ConcretePoint<BigInteger>) p).getX();
        byte[] bytes = key.xor(xor).toByteArray();
        return ArrayUtils.copyOfRange(bytes, 1, bytes.length);
    }
}
