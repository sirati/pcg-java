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

import java.util.stream.Stream;

public class LinearCompTest {
    private static final TestConstructor<?, ?>[] pcgClasses = {
            PCG_XSH_RR::createFromNumber,
            PCG_XSL_RR::createFromNumber,
            PCG_XSH_RS::createFromNumber
    };

    //@Test
    void runLinearCompTestOnAll() {
        // List of PCG versions to test
        long seed = 123456789;

        for (TestConstructor<?, ?> pcgClass : pcgClasses) {
            runLinearCompTest(pcgClass, seed);
        }

    }


    static Stream<TestConstructor<?, ?>> rngCtorProvider() {
        return Stream.of(pcgClasses);
    }

    //@ParameterizedTest
    //@MethodSource("rngCtorProvider")
    <T extends PCG, Seed extends Number> void runLinearCompTestOnIndividual(TestConstructor<T, Seed> constructor) {
        // List of PCG versions to test
        long seed = 123456789;
        runLinearCompTest(constructor, seed);

    }
    private static <T extends PCG, Seed extends Number>  void runLinearCompTest(TestConstructor<T, Seed> constructor, long long_seed) {
        //noinspection unchecked
        Seed seed = (Seed)(Number)long_seed;
        T rng = constructor.create(seed);

        // Linear complexity test sizes
        int[] sizes = {5000};

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


        // Perform the linear complexity test on the sequence
        int linearComplexity = calculateLinearComplexity(sequence);
        System.out.println("Linear complexity for size " + size + ": " + linearComplexity);
        throw new RuntimeException();
    }

    //todo this is wrong, delete and manually rewrite
    private static int calculateLinearComplexity(int[] sequence) {
        int n = sequence.length;
        int[] b = new int[n];
        int[] c = new int[n];
        int[] t = new int[n];

        b[0] = 1;
        c[0] = 1;

        int l = 0;
        int m = -1;
        int N = 0;

        while (N < n) {
            int d = sequence[N];
            for (int i = 1; i <= l; i++) {
                d ^= c[i] * sequence[N - i];
            }

            if (d == 1) {
                System.arraycopy(c, 0, t, 0, n);
                for (int i = 0; i < n - (N - m); i++) {
                    c[N - m + i] ^= b[i];
                }

                if (l <= N / 2) {
                    l = N + 1 - l;
                    m = N;
                    System.arraycopy(t, 0, b, 0, n);
                }
            }
            N++;
        }
        return l;
    }

}
