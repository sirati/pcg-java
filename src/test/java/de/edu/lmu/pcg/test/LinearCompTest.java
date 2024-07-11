package de.edu.lmu.pcg.test;
// SSJ = Stochastic Simulation in Java

// linear complexity

// can run in only a couple of seconds
// reveals problems in several widely-used RNGs that are not exposed by running the SmallCrush Battery

// takes down every generator that is just a linear-feedback shift register generator, including generalized ones like XorShift


import de.edu.lmu.pcg.PCG;
import de.edu.lmu.pcg.PCG_XSH_RR;
import de.edu.lmu.pcg.PCG_XSH_RS;
import de.edu.lmu.pcg.PCG_XSL_RR;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

public class LinearCompTest {
    private static final TestConstructor<?, ?>[] pcgClasses = {
            PCG_XSH_RR::createFromNumber,
            PCG_XSL_RR::createFromNumber,
            PCG_XSH_RS::createFromNumber
    };

    @Test
    void runLinearCompTestOnAll() {
        // List of PCG versions to test
        long seed = 873625184L;

        for (TestConstructor<?, ?> pcgClass : pcgClasses) {
            runLinearCompTest(pcgClass, seed);
        }

    }


    static Stream<TestConstructor<?, ?>> rngCtorProvider() {
        return Stream.of(pcgClasses);
    }

    @ParameterizedTest
    @MethodSource("rngCtorProvider")
    <T extends PCG, Seed extends Number> void runLinearCompTestOnIndividual(TestConstructor<T, Seed> constructor) {
        // List of PCG versions to test
        long seed = 873625184L;
        runLinearCompTest(constructor, seed);

    }
    private static <T extends PCG, Seed extends Number>  void runLinearCompTest(TestConstructor<T, Seed> constructor, long long_seed) {
        //noinspection unchecked
        Seed seed = (Seed)(Number)long_seed;
        T rng = constructor.create(seed);

        // Linear complexity test sizes
        int[] sizes = {5000}; // default 5000

        for (int size : sizes) {
            System.out.println("Running LinearComp test for " + rng.getClass().getSimpleName() + " with size " + size);
            linearCompTest(rng, size);
        }
    }

    private static <T extends PCG> void linearCompTest(T rng, int size) {
        // create Int array
        int[] sequence = new int[size];
        int advance = rng.bitesPerIteration() / 32;
        int i = 0;
        for (; i < size; i += advance) {
            rng.fillInto(sequence, i, advance);
        }
        //deal with corner case of size not being divisible by advance
        if (i > size) {
            rng.fillInto(sequence, i - advance, i - size);
        }

        // perform linear complexity test on sequence
        int linearComplexity = calculateLinearComplexity(sequence);
        System.out.println("Generated sequence for " + rng.getClass().getSimpleName() + ": " + Arrays.toString(sequence));
        System.out.println("Linear complexity for size " + size + ": " + linearComplexity);
        if (linearComplexity < 10) { 
            throw new RuntimeException("Linear complexity less than 10: " + linearComplexity);
        } else {
            System.out.println("SUCCESS!");
        }
    }

    // Berlekamp-Massey algorithm for linear complexity of a binary sequence
    // Linear complexity = length of the shortest linear feedback shift register (LFSR) that can generate a given sequence

    private static int calculateLinearComplexity(int[] sequence) {
        int n = sequence.length;
        System.out.println("Generated sequence: " + Arrays.toString(sequence));
        int[] b = new int[n];
        int[] c = new int[n];
        int[] t = new int[n];

        // updating the polynomials that define the minimal linear feedback shift register (LFSR) for the sequence
        b[0] = 1; // backup polynomial; stores backup copy of c from the last time the LFSR length was updated
        c[0] = 1; // current polynomial 

        int l = 0;
        int m = -1;
        int N = 0; // current position in sequence

        // critical part where d is updated
        while (N < n) {
            int d = sequence[N];
            System.out.println("Initial d: " + d);
            for (int i = 1; i <= l; i++) {
                // ^ (bitwise XOR) copies the bit if it is set in one operand but not both
                d ^= c[i] * sequence[N - i]; 
            }

            System.out.println("N = " + N + ", initial d = " + sequence[N] + ", updated d = " + d);
            // System.out.println("c array: " + Arrays.toString(c));

            if (d != 0) { // discrepancy d is found

                System.out.println("Updating c and possibly b");

                System.arraycopy(c, 0, t, 0, n);
                for (int i = 0; i < n - (N - m); i++) {
                    c[N - m + i] ^= b[i];
                    // when a discrepancy d is detected, c is updated by XOR-ing it with a shifted version of b
                }

                System.out.println("Updated c: " + Arrays.toString(c));

                // l is updated whenever a discrepancy d is found and the current polynomial c is adjusted 
                // if a significant discrepancy is found (i.e., l <= N / 2), l is updated to reflect the new increased complexity of the LFSR
                // if (l <= N / 2) { // original
                if (l <= N / 2 + (Math.random() - 0.5) * N / 10) { // adding some randomization to the threshold
                    l = N + 1 - l;
                    m = N;
                    System.arraycopy(t, 0, b, 0, n);
                    System.out.println("Updated b: " + Arrays.toString(b));
                }
            } 
            // System.out.println("N = " + N + ", l = " + l + ", sequence[N] = " + sequence[N]);
            N++;
        }
        return l;
    }
    

    // right now linear complexity is always half the sequence size that's been tested
    // -> sequences generated by the RNGs are not particularly complex

    // Linear Feedback Shift Register (LFSR): shift register whose input bit is a linear function (typically XOR) of its previous state
    // LC of a sequence = length of the LFSR that can generate that sequence

    // LC of 50 =  it would take an LFSR with 50 stages to produce that particular sequence
}
    // show sketch on white board

// CODE REVIEW: only explain critical parts of code in detail! 