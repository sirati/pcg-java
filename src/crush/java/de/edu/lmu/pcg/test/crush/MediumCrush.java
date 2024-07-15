package de.edu.lmu.pcg.test.crush;

import de.edu.lmu.pcg.PCG_RXS_M_XS_32;
import de.edu.lmu.pcg.PCG_XSL_RR;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Random;

public class MediumCrush {

    @Test
    public void testCrush() {
        Adapter.mediumCrush(new PCG_RXS_M_XS_32(new Random().nextInt()));
    }
}
