package cryptocast.crypto.test;

import static org.junit.Assert.*;

import org.junit.Test;

import cryptocast.crypto.ModularExponentiationGroup;

public class TestModularExponentiationGroup {
    @Test
    public void equalsWorks() {
        ModularExponentiationGroup g1 = ModularExponentiationGroup.getP1024Q160();
        ModularExponentiationGroup g2 = ModularExponentiationGroup.getP1024Q160();
        assertEquals(g1, g2);
    }
}
