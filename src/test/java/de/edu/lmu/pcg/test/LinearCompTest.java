package de.edu.lmu.pcg.test;

import de.edu.lmu.pcg.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.stream.Stream;

public class LinearCompTest {

    public static Stream<TestConstructor<?, U128>> rngCtorProvider() {
        return Util.rngCtorProvider();
    }

    @ParameterizedTest
    @MethodSource("rngCtorProvider")
    <T extends PCG> void runLinearCompTestOnIndividual(TestConstructor<T, U128> constructor) {
        // list of PCG versions to test
        U128 seed = new U128( 873625184L,873625184L);
        runLinearCompTest(constructor, seed);

    }
    private static <T extends PCG>  void runLinearCompTest(TestConstructor<T, U128> constructor, U128 seed) {
        T rng = constructor.create(seed);

        // linear complexity test sizes
        int[] sizes = {50}; // default 5000

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
            rng.fillOnceInto(sequence, i, advance);
        }
        // deal with corner case of size not being divisible by advance
        if (i > size) {
            rng.fillOnceInto(sequence, i - advance, i - size);
        }

        // perform linear complexity test on sequence
        int linearComplexity = calculateLinearComplexity(sequence);
//        System.out.println("Generated sequence for " + rng.getClass().getSimpleName() + ": " + Arrays.toString(sequence));
//        System.out.println("Linear complexity for size " + size + ": " + linearComplexity);
        if (linearComplexity < 10) {
            throw new RuntimeException("Linear complexity less than 10: " + linearComplexity);
        } else {
            System.out.println("SUCCESS!");
        }
    }

    // Berlekamp-Massey algorithm for linear complexity of a binary sequence
   
    private static int calculateLinearComplexity(int[] sequence) {
        int n = sequence.length;
//        System.out.println("Generated sequence: " + Arrays.toString(sequence));
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
//            System.out.println("Initial d: " + d);
            for (int i = 1; i <= l; i++) {
                // ^ (bitwise XOR) copies the bit if it is set in one operand but not both
                d ^= c[i] * sequence[N - i];
            }

//            System.out.println("N = " + N + ", initial d = " + sequence[N] + ", updated d = " + d);

            if (d != 0) { // discrepancy d is found

//                System.out.println("Updating c and possibly b");

                System.arraycopy(c, 0, t, 0, n);
                for (int i = 0; i < n - (N - m); i++) {
                    c[N - m + i] ^= b[i];
                    // when a discrepancy d is detected, c is updated by XOR-ing it with a shifted version of b
                }

//                System.out.println("Updated c: " + Arrays.toString(c));

                // l is updated whenever a discrepancy d is found and the current polynomial c is adjusted
                // if a significant discrepancy is found (i.e., l <= N / 2), l is updated to reflect the new increased complexity of the LFSR
                
                // if (l <= N / 2) { // original
                if (l <= N / 2 + (Math.random() - 0.5) * N / 10) { // adding some randomization to the threshold
                    l = N + 1 - l;
                    m = N;
                    System.arraycopy(t, 0, b, 0, n);
//                    System.out.println("Updated b: " + Arrays.toString(b));
                }
            }
            N++;
        }
        return l;
    }
}