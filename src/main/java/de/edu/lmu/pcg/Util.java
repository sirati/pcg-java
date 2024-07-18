package de.edu.lmu.pcg;

import java.lang.reflect.Array;
import java.math.BigInteger;

public final class Util {
    private Util() {
    }

    private static final long multiplierHigh = 2549297995355413924L;
    private static final long multiplierLow = 4865540595714422341L;
    private static final BigInteger bigMultiplier = BigInteger.valueOf(multiplierHigh).shiftLeft(64)
            .or(BigInteger.valueOf(multiplierLow));
    private static final long incrementHigh = 6364136223846793005L;
    private static final long incrementLow = 1442695040888963407L;
    private static final BigInteger bigIncrement = BigInteger.valueOf(incrementHigh).shiftLeft(64)
            .or(BigInteger.valueOf(incrementLow));
    private static final BigInteger bigModulus = BigInteger.ONE.shiftLeft(127);

    private static final long longMultiplier = 6364136223846793005L;
    private static final long longIncrement = 1442695040888963407L;
    private static final long longMod = 1L << 63;

    private static final int intMultiplier = 1664525;
    private static final int intIncrement = 1013904223;
    private static final int intMod = 1 << 31;

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

    // TODO: modExp muss auch für 128bit implementiert werden
    private static BigInteger bigModExp(BigInteger base, BigInteger exponent, BigInteger modulus) {
        return BigInteger.ONE;
    }

    // modular multiplicative inverse of an under modulus
    private static long modInverse(long a, long modulus) {
        return modExp(a, modulus - 2, modulus);
    }

    // TODO: modInverse muss auch für 128bit implementiert werden
    private static BigInteger bigModInverse(BigInteger a, BigInteger modulus) {
        return BigInteger.ONE;
    }

    // skip method for long type
    static long skipLong(long state, long steps) {
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
    static int skip(int state, int steps) {
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
        BigInteger a_i = bigModExp(bigMultiplier, _steps, bigModulus);

        // (a^i - 1) % m
        BigInteger a_i_minus_1 = a_i.subtract(BigInteger.ONE).add(bigModulus).mod(bigModulus);

        // modular multiplicative inverse of (a - 1) % m
        BigInteger a_minus_1 = (bigMultiplier.subtract(BigInteger.ONE).add(bigModulus)).mod(bigModulus);
        BigInteger inverse_a_minus_1 = bigModInverse(a_minus_1, bigModulus);

        // c * (a^i - 1) / (a - 1) % m
        BigInteger factor = (a_i_minus_1.multiply(inverse_a_minus_1)).mod(bigModulus);
        BigInteger offset = (bigIncrement.multiply(factor)).mod(bigModulus);

        BigInteger newState = (a_i.multiply(state).add(offset)).mod(bigModulus);

        return newState;
    }

    public static void printConstant(String[] args) {
        //log all consts for debugging
        System.out.println("longMultiplier: " + longMultiplier);
        System.out.println("longIncrement: " + longIncrement);
        System.out.println("longMod: " + longMod);
        System.out.println("intMultiplier: " + intMultiplier);
        System.out.println("intIncrement: " + intIncrement);
        System.out.println("intMod: " + intMod);
        System.out.println("bigMultiplier: " + bigMultiplier);
        System.out.println("bigIncrement: " + bigIncrement);
        System.out.println("bigModulus: " + bigModulus);

    }

    public static int newIntState(int state) {
        int multiplied = (intMultiplier * state) % intMod;
        return (multiplied + intIncrement) % intMod;
    }

    public static long newLongState(long state) {
        long multiplied = (longMultiplier * state) % longMod;
        return (multiplied + longIncrement) % longMod; // (a * state + offset) % m;
    }

    public static BigInteger new128State(BigInteger state) {
        BigInteger multiplied = state.multiply(bigMultiplier).mod(bigModulus);
        return (multiplied.add(bigIncrement)).mod(bigModulus);
    }

    public static Class<?> toWrapperClass(Class<?> primitiveClass) {
        if (!primitiveClass.isPrimitive()) {
            throw new IllegalArgumentException("The provided class is not a primitive type");
        }
        return Array.get(Array.newInstance(primitiveClass, 1), 0).getClass();
    }
}
