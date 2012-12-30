package cryptocast.crypto;

import static org.junit.Assert.*;

import org.junit.Test;

import cryptocast.crypto.SchnorrGroup;

public class TestModularExponentiationGroup {
    @Test
    public void equalsWorks() {
        SchnorrGroup g1 = SchnorrGroup.getP1024Q160();
        SchnorrGroup g2 = SchnorrGroup.getP1024Q160();
        assertEquals(g1, g2);
    }
}
