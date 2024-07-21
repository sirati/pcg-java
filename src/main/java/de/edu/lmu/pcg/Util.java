package de.edu.lmu.pcg;

import java.lang.reflect.Array;
import java.math.BigInteger;

public final class Util {
    private Util() {
    }

    public static final long u128MultiplierHigh = 2549297995355413924L;
    public static final long u128MultiplierLow = 4865540595714422341L;
    public static final BigInteger u128Multiplier = BigInteger.valueOf(u128MultiplierHigh).shiftLeft(64)
            .or(BigInteger.valueOf(u128MultiplierLow));
    public static final long u128IncrementHigh = 6364136223846793005L;
    public static final long u128IncrementLow = 1442695040888963407L;
    public static final BigInteger u128Increment = BigInteger.valueOf(u128IncrementHigh).shiftLeft(64)
            .or(BigInteger.valueOf(u128IncrementLow));
    public static final BigInteger u128Modulus = BigInteger.ONE.shiftLeft(127);

    public static final long longMultiplier = 6364136223846793005L;
    public static final long longIncrement = 1442695040888963407L;
    public static final long longMod = 1L << 63;

    public static final int intMultiplier = 747796405;
    public static final int intIncrement = -1403630843;
    public static final int intMod = 1 << 31;


    /*
    Modification of internal state
    ==============================
    s_0 = initial state (seed)
    s_i = internal states i
    g = multiplier
    c = increment
    m = exponent of two for modulus

    Linear congruential generator:
    s_i+1 = s_i * g + c  mod 2^m

    Skipping for seekability (4.3.1 pcg-paper):
    s_k = s_0 * g^k + c * (g^k - 1/g - 1)  mod 2^m

    used algorithm for skipping: https://mcnp.lanl.gov/pdf_files/Proceedings_1994_TotANS_Brown_202--203.pdf
     */



    // DATATYPE INT
    // ============

    // calculate g^k mod 2^m
    // algorithm G in paper
    private static int modExpInt(int multiplier, int steps, int modulus) {
        int result = 1;
        multiplier = multiplier % modulus;
        while (steps > 0) {
            if ((steps & 1) == 1) {
                result = (result * multiplier) % modulus;
            }
            multiplier = (multiplier * multiplier) % modulus;
            steps = steps >>> 1;
        }
        return result;
    }

    // calculate c * (g^k - 1/g - 1)  mod 2^m
    // algorithm C in paper
    private static int modIncInt(int increment, int multiplier, int steps, int modulus) {
        int result = 0;
        while (steps > 0) {
            if ((steps & 1) == 1) {
                result = (result * multiplier + increment) % modulus;
            }
            increment = (increment * (multiplier + 1)) % modulus;
            multiplier = (multiplier * multiplier) % modulus;
            steps = steps >>> 1;
        }
        return result;
    }

    // skip method for int type
    public static int skip(int state, int steps) {
        checkPositive(steps);
        int g = (int) modExpInt(intMultiplier, steps, intMod);
        int c = (int) modIncInt(intIncrement, intMultiplier, steps, intMod);
        return (state*g + c) % intMod;
    }

    public static int newIntState(int state) {
        int multiplied = (intMultiplier * state) % intMod;
        return (multiplied + intIncrement) % intMod;
    }



    //  DATATYPE LONG
    //  =============

    // calculate g^k mod 2^m
    // algorithm G in paper
    private static long modExpLong(long multiplier, long steps, long modulus) {
        long result = 1;
        multiplier = multiplier % modulus;
        while (steps > 0) {
            if ((steps & 1) == 1) {
                result = (result * multiplier) % modulus;
            }
            multiplier = (multiplier * multiplier) % modulus;
            steps = steps >>> 1;
        }
        return result;
    }

    // calculate c * (g^k - 1/g - 1)  mod 2^m
    // algorithm C in paper
    private static long modIncLong(long increment, long multiplier, long steps, long modulus) {
        long result = 0;
        while (steps > 0) {
            if ((steps & 1) == 1) {
                result = (result * multiplier + increment) % modulus;
            }
            increment = (increment * (multiplier + 1)) % modulus;
            multiplier = (multiplier * multiplier) % modulus;
            steps = steps >>> 1;
        }
        return result;
    }

    // skip method for long type
    public static long skipLong(long state, long steps) {
        checkPositive(steps);
        long g = modExpLong(longMultiplier, steps, longMod);
        long c = modIncLong(longIncrement, longMultiplier, steps, longMod);
        return (state*g + c) % longMod;
    }

    public static long newLongState(long state) {
        long multiplied = (longMultiplier * state) % longMod;
        return (multiplied + longIncrement) % longMod; // (a * state + offset) % m;
    }



    //  DATATYPE BIGINTEGER
    //  ===================

    // calculate g^k mod 2^m
    // algorithm G in paper
    private static BigInteger modExpBigInt(BigInteger multiplier, long steps, BigInteger modulus) {
        BigInteger result = BigInteger.ONE;
        multiplier = multiplier.mod(u128Modulus);
        while (steps > 0) {
            if ((steps & 1) == 1) {
                result = (result.multiply(multiplier)).mod(modulus);
            }
            multiplier = multiplier.multiply(multiplier).mod(modulus);
            steps = steps >>> 1;
        }
        return result;
    }

    // calculate c * (g^k - 1/g - 1)  mod 2^m
    // algorithm C in paper
    private static BigInteger modIncBigInt(BigInteger increment, BigInteger multiplier, long steps, BigInteger modulus) {
        BigInteger result = BigInteger.ZERO;
        while (steps > 0) {
            if ((steps & 1) == 1) {
                result = (result.multiply(multiplier)).add(increment).mod(modulus);
            }
            increment = increment.multiply(multiplier.add(BigInteger.ONE)).mod(modulus);
            multiplier = (multiplier.multiply(multiplier)).mod(modulus);
            steps = steps >>> 1;
        }
        return result;
    }

    // skip method for BigInteger type
    public static BigInteger skip128(BigInteger state, long steps) {
        checkPositive(steps);
        BigInteger g = modExpBigInt(u128Multiplier, steps, u128Modulus);
        BigInteger c = modIncBigInt(u128Increment, u128Multiplier, steps, u128Modulus);
        return (state.multiply(g).add(c)).mod(u128Modulus);
    }

    public static BigInteger new128State(BigInteger state) {
        BigInteger multiplied = state.multiply(u128Multiplier).mod(u128Modulus);
        return (multiplied.add(u128Increment)).mod(u128Modulus);
    }


    // OTHER UTILS
    // ===========

    private static void checkPositive(long toCheck) {
        if (toCheck <= 0) {
            throw new IllegalArgumentException("Steps needs to be positive. Provided: " + toCheck);
        }
    }

    public static Class<?> toWrapperClass(Class<?> primitiveClass) {
        if (!primitiveClass.isPrimitive()) {
            throw new IllegalArgumentException("The provided class is not a primitive type");
        }
        return Array.get(Array.newInstance(primitiveClass, 1), 0).getClass();
    }
}
