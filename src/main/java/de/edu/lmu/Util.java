package de.edu.lmu;

public final class Util {
    private Util() {}

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

    // modular multiplicative inverse of a under modulus
    private static long modInverse(long a, long modulus) {
        return modExp(a, modulus - 2, modulus);
    }

    // skip method for long type
    static long skipLong(long state, long steps) {
        long a = 6364136223846793005L; // example multiplier
        long c = 1442695040888963407L; // example increment
        long m = 1L << 64; // 2^64, the modulus

        // a^i % m
        long a_i = modExp(a, steps, m);

        // (a^i - 1) % m
        long a_i_minus_1 = (a_i - 1 + m) % m;

        // modular multiplicative inverse of (a - 1) % m
        long a_minus_1 = (a - 1 + m) % m;
        long inverse_a_minus_1 = modInverse(a_minus_1, m);

        // c * (a^i - 1) / (a - 1) % m
        long factor = (a_i_minus_1 * inverse_a_minus_1) % m;
        long offset = (c * factor) % m;

        long newState = (a_i * state + offset) % m;

        // debugging
        System.out.println("a_i: " + a_i);
        System.out.println("a_i_minus_1: " + a_i_minus_1);
        System.out.println("inverse_a_minus_1: " + inverse_a_minus_1);
        System.out.println("factor: " + factor);
        System.out.println("offset: " + offset);
        System.out.println("newState: " + newState);

        // new state
        return newState;
    }

    // skip method for int type
    static int skip(int state, int steps) {
        int a = 1664525; // multiplier 
        int c = 1013904223; // increment
        int m = 1 << 31; // 2^31, the modulus for 32-bit integers

        // a^i % m
        int a_i = (int)modExp(a, steps, m);

        // (a^i - 1) % m
        int a_i_minus_1 = (a_i - 1 + m) % m;

        // modular multiplicative inverse of (a - 1) % m
        int a_minus_1 = (a - 1 + m) % m;
        int inverse_a_minus_1 = (int)modInverse(a_minus_1, m);

        // c * (a^i - 1) / (a - 1) % m
        int factor = (a_i_minus_1 * inverse_a_minus_1) % m;
        int offset = (c * factor) % m;

        int newState = (a_i * state + offset) % m;

        // debugging
        System.out.println("a_i: " + a_i);
        System.out.println("a_i_minus_1: " + a_i_minus_1);
        System.out.println("inverse_a_minus_1: " + inverse_a_minus_1);
        System.out.println("factor: " + factor);
        System.out.println("offset: " + offset);
        System.out.println("newState: " + newState);

        // new state
        return newState;
    }

    public static void main(String[] args) {
        long initialLongState = 42L;
        long skipStepsLong = 10L;

        int initialIntState = 42;
        int skipStepsInt = 10;

        long newLongState = Util.skipLong(initialLongState, skipStepsLong);
        int newIntState = Util.skip(initialIntState, skipStepsInt);

        System.out.println("New long state after skipping: " + newLongState);
        System.out.println("New int state after skipping: " + newIntState);
    }
}
