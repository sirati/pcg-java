package de.edu.lmu.pcg;

import java.lang.reflect.Array;
import java.math.BigInteger;

public final class Util {
    public static final BigInteger MASK_128 = new BigInteger("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF", 16);
    public static final BigInteger MASK_64 = new BigInteger("FFFFFFFFFFFFFFFF", 16);

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
    public static final BigInteger bigModulus = BigInteger.ONE.shiftLeft(127);

    public static final long longMultiplier = 6364136223846793005L;
    public static final long longIncrement = 1442695040888963407L;
    public static final long longMod = 1L << 63;

    public static final int intMultiplier = 747796405;
    public static final int intIncrement = -1403630843;
    public static final int intMod = 1 << 31;

    // (base^exponent) % modulus
    private static long modExp(long base, long exponent, long modulus) {
        long result = 1;
        base = base % modulus;
        while (exponent > 0) {
            if ((exponent & 1) == 1) {
                result = (result * base) % modulus;
            }
            exponent = exponent >> 1;
            base = (base * base) % modulus;
        }
        return result;
    }

    // modular multiplicative inverse of an under modulus
    private static long modInverse(long a, long modulus) {
        return modExp(a, modulus - 2, modulus);
    }

    // skip method for long type
    public static long skipLong(long state, long steps) {
        // a^i % m
        long a_i = modExp(longMultiplier, steps, longMod);

        // (a^i - 1) % m
        long a_i_minus_1 = (a_i - 1 + longMod) % longMod;

        // modular multiplicative inverse of (a - 1) % m
        long a_minus_1 = (longMultiplier - 1 + longMod) % longMod;
        long inverse_a_minus_1 = modInverse(a_minus_1, longMod);

        // c * (a^i - 1) / (a - 1) % m
        long factor = (a_i_minus_1 * inverse_a_minus_1) % longMod;
        long offset = (longIncrement * factor) % longMod;

        long newState = (a_i * state + offset) % longMod;

        return newState;
    }

    // skip method for int type
    public static int skip(int state, int steps) {
        // a^i % m
        int a_i = (int) modExp(intMultiplier, steps, intMod);

        // (a^i - 1) % m
        int a_i_minus_1 = (a_i - 1 + intMod) % intMod;

        // modular multiplicative inverse of (a - 1) % m
        int a_minus_1 = (intMultiplier - 1 + intMod) % intMod;
        int inverse_a_minus_1 = (int) modInverse(a_minus_1, intMod);

        // c * (a^i - 1) / (a - 1) % m
        int factor = (a_i_minus_1 * inverse_a_minus_1) % intMod;
        int offset = (intIncrement * factor) % intMod;

        int newState = (a_i * state + offset) % intMod;

        return newState;
    }

    public static BigInteger skip128(BigInteger state, long steps) {
        BigInteger _steps = BigInteger.valueOf(steps);
        // a^i % m
        BigInteger a_i = u128Multiplier.modPow(_steps, bigModulus);

        // (a^i - 1) % m
        BigInteger a_i_minus_1 = a_i.subtract(BigInteger.ONE).add(bigModulus).mod(bigModulus);

        // modular multiplicative inverse of (a - 1) % m
        BigInteger a_minus_1 = (u128Multiplier.subtract(BigInteger.ONE).add(bigModulus)).mod(bigModulus);
        BigInteger inverse_a_minus_1 = a_minus_1.modInverse(bigModulus);

        // c * (a^i - 1) / (a - 1) % m
        BigInteger factor = (a_i_minus_1.multiply(inverse_a_minus_1)).mod(bigModulus);
        BigInteger offset = (u128Increment.multiply(factor)).mod(bigModulus);

        BigInteger newState = (a_i.multiply(state).add(offset)).mod(bigModulus);

        return newState;
    }

    public static int newIntState(int state) {
        //no need for mod as the side of the state is 32 bits, which is automatically a mod 2^32
        return intMultiplier * state + intIncrement;
    }

    public static long newLongState(long state) {
        //no need for mod as the side of the state is 64 bits, which is automatically a mod 2^64
        return longMultiplier * state + longIncrement; // (a * state + offset) % m;
    }

    public static BigInteger new128State(BigInteger state) {
        return state
                .multiply(u128Multiplier)
                .and(Util.MASK_128)
                .add(u128Increment)
                .and(Util.MASK_128);
    }

    public static Class<?> toWrapperClass(Class<?> primitiveClass) {
        if (!primitiveClass.isPrimitive()) {
            throw new IllegalArgumentException("The provided class is not a primitive type");
        }
        return Array.get(Array.newInstance(primitiveClass, 1), 0).getClass();
    }
}
