package de.edu.lmu.pcg.test.crush;

import de.edu.lmu.pcg.PCG_RXS_M_XS_32;
import de.edu.lmu.pcg.PCG_XSH_RR;
import de.edu.lmu.pcg.PCG_XSL_RR;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Random;

public class SmallCrush {

    @Test
    public void testCrush() {
        var seed =new BigInteger(128, new Random());// new Random().nextInt();
        System.out.println(STR."Seed: \{seed} (0x\{seed.toString(16)})");
        Adapter.smallCrush(new PCG_XSL_RR(seed));
    }


    @Test
    public void testCrush2() {
        Adapter.smallCrush(new PCG_RXS_M_XS_32(new Random().nextInt()));
    }
}
