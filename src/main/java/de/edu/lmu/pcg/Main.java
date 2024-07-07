package de.edu.lmu.pcg;
import de.edu.lmu.pcg.services.PCGCtorService;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        //var footest = new PCGBuilder<>().type(PCG_RXS_M_XS_32.class).seed(1L).build();

        var pcgClasses = PCGCtorService.AVAILABLE_PCGS.values();
        final var seedU64 = 9223332041373072921L;
        final var seedU128 = new U128(seedU64, seedU64);

        long size = 1L << 26;

        for (var pcgClass_desc : pcgClasses) {
            try {
                var pcgInstance = pcgClass_desc.service().create(seedU128);
                // Output to file
                String filename = pcgClass_desc.cls_PCG().getSimpleName() + ".txt";
                //outputToFile(pcgInstance, size, filename);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void outputToFile(Object pcg, long amount, String fileName) {
        long startTime = System.currentTimeMillis();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (int i = 0; i < amount; i++) {
                String randomNumber = getNextNormalized(pcg); // Generates a random number between 0 and 99
                writer.write(randomNumber);
                writer.newLine(); // Adds a new line after each number
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