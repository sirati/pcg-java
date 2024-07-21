package de.edu.lmu.pcg.test;

import de.edu.lmu.pcg.U128;
import de.edu.lmu.pcg.Util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class PCGUtilTest {

    // PARAMETERS
    // ==========

    static Stream<Object[]> intParameter() {
        int[] values = {123456789, 987654321, 555555555};
        int[] steps = {1, 10, 100};

        List<Object[]> parameters = new ArrayList<>();
        for (int value : values) {
            for (int step : steps) {
                parameters.add(new Object[]{value, step});
            }
        }
        return parameters.stream();
    }

    static Stream<Object[]> longParameter() {
        long[] values = {123456789L, 987654321L, 5555555555555555555L};
        long[] steps = {1L, 10L, 100L};

        List<Object[]> parameters = new ArrayList<>();
        for (long value : values) {
            for (long step : steps) {
                parameters.add(new Object[]{value, step});
            }
        }
        return parameters.stream();
    }

    static Stream<Object[]> bigIntParameter() {
        BigInteger[] values = {
                new BigInteger("123456789123456789123456789"),
                new BigInteger("987654321987654321987654321"),
                new BigInteger("5555555555555555555555555555555555")
        };
        long[] steps = {1L, 10L, 100L};

        List<Object[]> parameters = new ArrayList<>();
        for (BigInteger value : values) {
            for (long step : steps) {
                parameters.add(new Object[]{value, step});
            }
        }
        return parameters.stream();
    }

    static Stream<Object[]> zeroOrNegativeStepParameter() {
        return Stream.of(
                new Object[]{123456789, 0},
                new Object[]{987654321L, 0L},
                new Object[]{new BigInteger("123456789123456789123456789"), 0L},
                new Object[]{123456789, -1},
                new Object[]{987654321L, -1L},
                new Object[]{new BigInteger("123456789123456789123456789"), -1L}
        );
    }



    // TESTS
    // =====

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

    @ParameterizedTest
    @MethodSource("bigIntParameter")
    public void testSkipBigInteger(BigInteger seed, long steps) {
        BigInteger initState = seed;
        BigInteger repeatedState = initState;

        for (long i = 0; i < steps; i++) {
            repeatedState = Util.new128State(repeatedState);
        }

        BigInteger skippedState = Util.skip128(new U128(initState), steps).toBigInteger();
        System.out.println("Seed: " + initState + ", Steps: " + steps + ", Expected: " + repeatedState + ", Actual: " + skippedState);
        Assertions.assertEquals(repeatedState, skippedState, "Failed for seed: " + initState + " with steps: " + steps);
    }

    @ParameterizedTest
    @MethodSource("zeroOrNegativeStepParameter")
    public void testSkipZeroSteps(Object seed, long steps) {
        if (seed instanceof Integer) {
            int initState = (int) seed;
            Assertions.assertThrows(IllegalArgumentException.class, () -> Util.skip(initState, (int) steps));
        } else if (seed instanceof Long) {
            long initState = (long) seed;
            Assertions.assertThrows(IllegalArgumentException.class, () -> Util.skipLong(initState, steps));
        } else if (seed instanceof BigInteger) {
            BigInteger initState = (BigInteger) seed;
            Assertions.assertThrows(IllegalArgumentException.class, () -> Util.skip128(new U128(initState), steps));
        }
    }
}

