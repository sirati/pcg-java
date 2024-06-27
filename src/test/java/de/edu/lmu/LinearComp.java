package de.edu.lmu;
// SSJ = Stochastic Simulation in Java

// linear complexity

// can run in only a couple of seconds
// reveals problems in several widely-used RNGs that are not exposed by running the SmallCrush Battery

// takes down every generator that is just a linear-feedback shift register generator, including generalized ones like XorShift



import umontreal.ssj.rng.RandomStreamBase;
import java.util.Arrays;

public class LinearComp {

    private static void runLinearCompTest(String className, long seed) {
        try {
            // Load the RNG class
            Class<?> rngClass = Class.forName("de.edu.lmu." + className);
            RandomStreamBase rng = (RandomStreamBase) rngClass.getConstructor(long.class).newInstance(seed);

            // Linear complexity test sizes
            int[] sizes = {5000};

            for (int size : sizes) {
                System.out.println("Running LinearComp test for " + className + " with size " + size);
                linearCompTest(rng, size);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void linearCompTest(RandomStreamBase rng, int size) {
        // Implement your linear complexity test here
        // This is just a placeholder for the actual test logic
        int[] sequence = new int[size];
        for (int i = 0; i < size; i++) {
            sequence[i] = rng.nextInt(2); // Generating a sequence of random bits
        }

        // Perform the linear complexity test on the sequence
        int linearComplexity = calculateLinearComplexity(sequence);
        System.out.println("Linear complexity for size " + size + ": " + linearComplexity);
    }

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

    public static void main(String[] args) {
        // List of PCG versions to test
        String[] pcgClasses = {
            "PCG_XSH_RR",
            "PCG_XSH_RS"
        };

        long seed = 123456789;

        for (String className : pcgClasses) {
            runLinearCompTest(className, seed);
        }
    }
}
