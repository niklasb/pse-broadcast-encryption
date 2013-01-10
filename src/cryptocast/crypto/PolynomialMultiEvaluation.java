package cryptocast.crypto;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

public class PolynomialMultiEvaluation {
    // set some fast defaults
    private int numThreads = 4, 
                chunkSize = 1024;

    private static final Logger log = LoggerFactory
            .getLogger(PolynomialMultiEvaluation.class);
    
    private ImmutableList<BigInteger> xs;
    private byte[][] pointsTwoComplements;
    
    private static boolean haveNative = false;
    static {
        try {
            System.loadLibrary("PolynomialMultiEvaluation");
            haveNative = true;
        } catch (Error e) {
            log.warn("Could not load native PolynomialMultiEvaluation library", e);
        }
    }
    
    public PolynomialMultiEvaluation(List<BigInteger> xs, int numThreads, int chunkSize) {
        this.xs = ImmutableList.copyOf(xs);
        this.chunkSize = chunkSize;
        this.numThreads = numThreads;
    }
    
    public PolynomialMultiEvaluation(List<BigInteger> xs) {
        this.xs = ImmutableList.copyOf(xs);
    }
    
    public ImmutableList<BigInteger> evaluate(Polynomial<BigInteger> poly) {
        if (haveNative && poly.getField() instanceof IntegersModuloPrime && xs.size() > 0) {
            BigInteger mod = ((IntegersModuloPrime) poly.getField()).getP();
            byte[][] points = getPointsTwoComplements();
            byte[][] coeffs = bigIntListToTwoComplements(poly.getCoefficients());
            byte[][] result = nativeMultiEval(points, coeffs, mod.toByteArray(), 
                    numThreads, chunkSize);
            ImmutableList.Builder<BigInteger> builder = ImmutableList.builder();
            for (int i = 0, len = result.length; i < len; ++i) {
                builder.add(new BigInteger(result[i]));
            }
            return builder.build();
        } else {
            return poly.evaluateMulti(xs);
        }
    }
    
    private static native byte[][] nativeMultiEval(
            byte[][] points, byte[][] coefficients, byte[] mod, int numThreads, int chunkSize);
    
    private byte[][] bigIntListToTwoComplements(List<BigInteger> bigInts) {
        int len = bigInts.size();
        byte[][] result = new byte[len][];
        Iterator<BigInteger> x = bigInts.iterator();
        for (int i = 0; i < len; ++i) {
            result[i] = x.next().toByteArray();
        }
        return result;
    }
    
    private byte[][] getPointsTwoComplements() {
        if (pointsTwoComplements != null) {
            return pointsTwoComplements;
        }
        return pointsTwoComplements = bigIntListToTwoComplements(xs);
    }
}
