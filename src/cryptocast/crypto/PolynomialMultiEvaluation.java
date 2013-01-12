package cryptocast.crypto;

import java.math.BigInteger;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import cryptocast.util.NativeUtils;

public class PolynomialMultiEvaluation {
    // only use native function if n is large enough
    private static int NATIVE_THRESHOLD_N = 1;
    
    // set some fast defaults
    private int numThreads = 4, 
                chunkSize = 1024;

    private static final Logger log = LoggerFactory
            .getLogger(PolynomialMultiEvaluation.class);
    
    private ImmutableList<BigInteger> xs;
    private byte[][] pointsTwoComplements;
    
    private static boolean haveNative = 
            NativeUtils.tryToLoadNativeLibOrLogFailure("PolynomialMultiEvaluation", log);
    
    public PolynomialMultiEvaluation(List<BigInteger> xs, int numThreads, int chunkSize) {
        this.xs = ImmutableList.copyOf(xs);
        this.chunkSize = chunkSize;
        this.numThreads = numThreads;
    }
    
    public PolynomialMultiEvaluation(List<BigInteger> xs) {
        this.xs = ImmutableList.copyOf(xs);
    }
    
    public ImmutableList<BigInteger> evaluate(Polynomial<BigInteger> poly) {
        if (haveNative && poly.getField() instanceof IntegersModuloPrime 
                && xs.size() >= NATIVE_THRESHOLD_N) {
            BigInteger mod = ((IntegersModuloPrime) poly.getField()).getP();
            byte[][] points = getPointsTwoComplements();
            byte[][] coeffs = NativeUtils.bigIntListToTwoComplements(poly.getCoefficients());
            byte[][] result = nativeMultiEval(points, coeffs, mod.toByteArray(), 
                    numThreads, chunkSize);
            return NativeUtils.twoComplementsToBigIntList(result);
        } else {
            return poly.evaluateMulti(xs);
        }
    }
    
    private static native byte[][] nativeMultiEval(
            byte[][] points, byte[][] coefficients, byte[] mod, int numThreads, int chunkSize);
    
    private byte[][] getPointsTwoComplements() {
        if (pointsTwoComplements != null) {
            return pointsTwoComplements;
        }
        return pointsTwoComplements = NativeUtils.bigIntListToTwoComplements(xs);
    }
}
