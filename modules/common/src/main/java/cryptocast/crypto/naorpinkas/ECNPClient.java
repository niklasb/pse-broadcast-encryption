package cryptocast.crypto.naorpinkas;

import java.math.BigInteger;

import com.google.common.io.ByteArrayDataInput;

import cryptocast.crypto.*;
import cryptocast.crypto.EllipticCurveOverFp.*;

public class ECNPClient extends 
         NPClient<Point, 
                  EllipticCurveGroup<BigInteger, Point, EllipticCurveOverFp>>  {
    public ECNPClient(NPKey<Point, 
                            EllipticCurveGroup<BigInteger, Point, EllipticCurveOverFp>> key) {
        super(key);
    }

    @Override
    protected NPShare<Point, 
                      EllipticCurveGroup<BigInteger, Point, EllipticCurveOverFp>> 
            readShare(
                           ByteArrayDataInput in) {
        BigInteger i = new BigInteger(readBytes(in));
        BigInteger x = new BigInteger(readBytes(in));
        byte info = in.readByte();
        Point grpi = getGroup().getCurve().uncompress(new CompressedPoint(x, info));
        return new NPShare<Point, 
                           EllipticCurveGroup<BigInteger, Point, EllipticCurveOverFp>>(
                                   i, grpi, getGroup());
    }

    @Override
    protected byte[] decryptSecretWithItem(byte[] encryptedSecret, Point p) 
                          throws DecryptionError {
        BigInteger key = getGroup().getCurve().getAffineCoords(p).get().getX();
        return CryptoUtils.decryptAndHash(encryptedSecret, key.toByteArray());
    }
}
