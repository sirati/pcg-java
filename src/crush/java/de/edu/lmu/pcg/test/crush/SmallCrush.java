package de.edu.lmu.pcg.test.crush;

import de.edu.lmu.pcg.PCG_RXS_M_XS_32;
import org.junit.Test;

import java.util.Random;

public class SmallCrush {

    @Test
    public void testCrush() {
        Adapter.testCrush(new PCG_RXS_M_XS_32(new Random().nextInt()));
    }
}
