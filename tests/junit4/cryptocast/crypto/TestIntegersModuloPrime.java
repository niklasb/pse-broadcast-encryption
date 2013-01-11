package cryptocast.crypto;

import static org.junit.Assert.*;

import java.math.BigInteger;

import org.junit.*;

public class TestIntegersModuloPrime {
    private IntegersModuloPrime smallSut =
            new IntegersModuloPrime(BigInteger.valueOf(11));
    private IntegersModuloPrime largeSut =
            SchnorrGroup.getP1024Q160().getFieldModP();
    
    @Test
    public void pow() {
        assertEquals(BigInteger.valueOf(9), 
                smallSut.pow(BigInteger.valueOf(3), BigInteger.valueOf(1000000002)));
        
        BigInteger q = SchnorrGroup.getP1024Q160().getQ();
        BigInteger expected = new BigInteger(
                "9dab40a861a4f4e58fb56e9b4f5c2036d385773495200d4" +
                "12234738610aa88093a36218df86e941b6d7c87ceee2230" +
                "ae1ed6050b017b30a84763aae61072d5f341afc1688b501" +
                "a8a02920ee0670b62e0b7797b260feca1fef464c43b5f97" +
                "9d87fa01b5b2064011aaa8b8f28bf012bf1ad5db6aea6f5" +
                "3ba233a69a611de82b191", 16);
        assertEquals(expected, largeSut.pow(q, q));
    }
}