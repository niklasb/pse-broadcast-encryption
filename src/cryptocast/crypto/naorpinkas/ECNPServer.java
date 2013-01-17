package cryptocast.crypto.naorpinkas;

import java.math.BigInteger;

import com.google.common.base.Optional;
import com.google.common.io.ByteArrayDataOutput;

import cryptocast.crypto.*;
import cryptocast.crypto.EllipticCurveOverFp.*;

public class ECNPServer 
      extends NPServer<Point, 
                       EllipticCurveGroup<BigInteger, Point, EllipticCurveOverFp>> {
    private static final long serialVersionUID = 1656507589897819277L;

    protected ECNPServer(
            NPServerContext<Point, 
                            EllipticCurveGroup<BigInteger, Point, EllipticCurveOverFp>> ctx) {
        super(ctx);
    }

    private EllipticCurveOverFp getCurve() {
        return getContext().getGroup().getCurve();
    }
    
    @Override
    protected Optional<byte[]> encryptSecretWithItem(byte[] secret, Point p) {
        // check if secret is small enough to be properly encrypted.
        // subtract some space to be sure
        if (secret.length >= getCurve().getField().getP().bitLength() / 8 - 2) {
            return Optional.absent();
        }
        byte[] bytes = new byte[secret.length + 1];
        // explicitly set the sign bit so we can safely remove it on the other side
        bytes[0] = 0x01;
        System.arraycopy(secret, 0, bytes, 1, secret.length);
        BigInteger secretAsNum = new BigInteger(bytes);
        // it's quite unlikely that we hit the jackpot (infinity), so
        // we just take the risk :)
        BigInteger key = getCurve().getAffineCoords(p).get().getX();
        return Optional.of(secretAsNum.xor(key).toByteArray());
    }

    @Override
    protected void writeShare(ByteArrayDataOutput out, 
            NPShare<Point,
                    EllipticCurveGroup<BigInteger, Point, EllipticCurveOverFp>> share) {
        putBytes(out, share.getI().toByteArray());
        CompressedPoint cp = getContext().getGroup().getCurve().compress(share.getGRPI());
        putBytes(out, cp.getX().toByteArray());
        out.writeByte(cp.getInfo());
    }
}
