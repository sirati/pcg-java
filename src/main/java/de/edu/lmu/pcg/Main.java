package de.edu.lmu.pcg;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        List<Class<? extends PCG>> pcgClasses = Arrays.asList(
                                                    PCG_XSH_RR.class,
                                                    PCG_XSH_RS.class,
                                                    PCG_RXS_M_XS_32.class,
                                                    PCG_RXS_M_XS_64.class,
                                                    PCG_XSL_RR.class);
        long seed = 42L;
        long size = 1L << 26;

        for (Class<? extends PCG> pcgClass : pcgClasses) {
            try {
                Object pcgInstance;
                Field stateField = pcgClass.getDeclaredField("state");
                if (stateField.getType().equals(int.class)) {
                    int seed32 = (int) seed;
                    pcgInstance = new PCGBuilder<>()
                            .type(pcgClass)
                            .seed(seed32)
                            .build();
                } else if (stateField.getType().equals(long.class)){
                    pcgInstance = new PCGBuilder<>()
                            .type(pcgClass)
                            .seed(seed)
                            .build();
                } else if (stateField.getType().equals(BigInteger.class)) {
                    BigInteger seed128 = new BigInteger(Long.toString(seed)+Long.toString(seed));
                    pcgInstance = new PCGBuilder<>()
                            .type(pcgClass)
                            .seed(seed128)
                            .build();
                } else {
                    throw new IllegalArgumentException("Unsupported seed for pcg class: " + pcgClass + "needs a state of " + stateField.getType());
                }
                // Output to file
                String filename = pcgClass.getSimpleName() + ".txt";
                outputToFile(pcgInstance, size, filename);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void outputToFile(Object pcg, long amount, String fileName) {
        long startTime = System.currentTimeMillis();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (int i = 0; i < amount; i++) {
                // String randomNumber = getNextNormalized(pcg); // Generates a random number between 0 and 99
                if (pcg instanceof PCGInt) {
                    int randomNumber = ((PCGInt) pcg).nextInt();
                    writer.write(Integer.toString(randomNumber));
                    writer.newLine(); // Adds a new line after each number
                } else {
                    long randomNumber = ((PCGLong) pcg).nextLong();
                    writer.write(Long.toString(randomNumber));
                    writer.newLine(); // Adds a new line after each number
                }
            }
            // Get the end time
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            double durationInSeconds = duration / 1000.0;
            System.out.println("Random numbers have been written to " + fileName + " in " + durationInSeconds + " seconds");
        } catch (IOException e) {
            System.err.println("An IOException was caught: " + e.getMessage());
        }
    }

    private static String getNextNormalized(Object pcg) {
        if (pcg instanceof PCGInt) {
            int rnd = ((PCGInt) pcg).nextInt();
            float normalized = (rnd & 0xFFFFFFFFL) / (float) (1L << 32);
            return Float.toString(normalized);
        } else if (pcg instanceof PCGLong) {
            long rnd = ((PCGLong) pcg).nextLong();
            double dividend = rnd >= 0 ? (double) rnd : (double) (rnd & Long.MAX_VALUE) + Math.pow(2,63);
            double divisor = Math.pow(2,64);
            // Normalize the long to a double in the range [0, 1)
            double normalized = dividend / divisor;
            return Double.toString(normalized);
        } else {
            throw new IllegalArgumentException("Unsupported PCG type: " + pcg.getClass().getName());
        }
    }
}