package de.edu.lmu.pcg.test;

import de.edu.lmu.pcg.Util;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.util.stream.Stream;

public class TestUtil {

    static Stream<Object[]> intParameter() {
        return Stream.of(
                new Object[]{12345, 1},
                new Object[]{23456, 10},
                new Object[]{123456789, 100}
        );
    }

    static Stream<Object[]> longParameter() {
        return Stream.of(
                new Object[]{123456789L, 1},
                new Object[]{987654321L, 10},
                new Object[]{123234345456567678L, 100}
        );
    }

//    static Stream<Object[]> bigIntParameter() {
//        return Stream.of(
//                new Object[]{new BigInteger("123456789123456789123456789"), 1},
//                new Object[]{new BigInteger("987654321987654321987654321"), 10},
//                new Object[]{new BigInteger("5555555555555555555555555555555555"), 100}
//        );
//    }

    @ParameterizedTest
    @MethodSource("intParameter")
    public void testSkipInt(int seed, int steps) {
        int initState = seed;
        int repeatedState = initState;

        for (int i = 0; i < steps; i++) {
            repeatedState = Util.newIntState(repeatedState);
        }

        int skippedState = Util.skip(initState, steps);
        System.out.println("Seed: " + initState + ", Steps: " + steps + ", Expected: " + repeatedState + ", Actual: " + skippedState);
        Assertions.assertEquals(repeatedState, skippedState, "Failed for seed: " + initState + " with steps: " + steps);
    }

    @ParameterizedTest
    @MethodSource("longParameter")
    public void testSkipLong(long seed, long steps) {
        long initState = seed;
        long repeatedState = initState;

        for (long i = 0; i < steps; i++) {
            repeatedState = Util.newLongState(repeatedState);
        }

        long skippedState = Util.skipLong(initState, steps);
        System.out.println("Seed: " + initState + ", Steps: " + steps + ", Expected: " + repeatedState + ", Actual: " + skippedState);
        Assertions.assertEquals(repeatedState, skippedState, "Failed for seed: " + initState + " with steps: " + steps);
    }

//    @ParameterizedTest
//    @MethodSource("bigIntParameter")
//    public void testSkipBigInteger(BigInteger seed, long steps) {
//        BigInteger initState = seed;
//        BigInteger repeatedState = initState;
//
//        for (long i = 0; i < steps; i++) {
//            repeatedState = Util.new128State(repeatedState);
//        }
//
//        BigInteger skippedState = Util.skip128(initState, steps);
//        System.out.println("Seed: " + initState + ", Steps: " + steps + ", Expected: " + repeatedState + ", Actual: " + skippedState);
//        Assertions.assertEquals(repeatedState, skippedState, "Failed for seed: " + initState + " with steps: " + steps);
//    }
}

