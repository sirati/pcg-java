package de.edu.lmu.pcg.test;

import de.edu.lmu.pcg.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


import java.math.BigInteger;

public class TestUtil {

    @Test
    public void testSkipInt() {
        int initState = 12345;
        int steps = 1;
        int repeatedState = initState;

        for (int i = 0; i < steps; i++) {
            repeatedState = Util.newIntState(repeatedState);
        }

        int skippedState = Util.skip(initState, steps);
        Assertions.assertEquals(repeatedState, skippedState);
    }

    @Test
    public void testSkipLong() {
        long initState = 123456789L;
        long steps = 1;
        long repeatedState = initState;

        for (long i = 0; i < steps; i++) {
            repeatedState = Util.newLongState(repeatedState);
        }

        long skippedState = Util.skipLong(initState, steps);
        Assertions.assertEquals(repeatedState, skippedState);
    }

    @Test
    public void testSkipBigInteger() {
        BigInteger initState = new BigInteger("123456789123456789123456789");
        long steps = 1;
        BigInteger repeatedState = initState;

        for (long i = 0; i < steps; i++) {
            repeatedState = Util.new128State(repeatedState);
        }

        BigInteger skippedState = Util.skip128(initState, steps);
        Assertions.assertEquals(repeatedState, skippedState);
    }
}
