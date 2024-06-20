package de.edu.lmu;

// SSJ = Stochastic Simulation in Java

// linear complexity

// can run in only a couple of seconds
// reveals problems in several widely-used RNGs that are not exposed by running the SmallCrush Battery

// takes down every generator that is just a linear-feedback shift register generator, including generalized ones like XorShift

import umontreal.ssj.rng.RandomStreamBase;
import umontreal.ssj.tests.TestU01;

public class LinearComp {

    private static void runLinearCompTest(String className, long seed) {
        try {
            // load the RNG class
            Class<?> rngClass = Class.forName("de.edu.lmu." + className);
            RandomStreamBase rng = (RandomStreamBase) rngClass.getConstructor(long.class).newInstance(seed);

            // linear complexity test sizes
            // int[] sizes = {5000, 25000, 50000, 75000};
            int[] sizes = {5000};

            for (int size : sizes) {
                System.out.println("Running LinearComp test for " + className + " with size " + size);
                TestU01.linearCompTest(rng, size);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

