package de.edu.lmu.pcg;
import de.edu.lmu.pcg.services.PCGCtorService;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        //omg java is so cursed....
        var footest = (PCGLong & SeedTypeMarker<?>) new PCGBuilder<>().type("PCG_RXS_M_XS_64").seed(1L).build();
        footest.nextLong();

        var pcgClasses = PCGCtorService.AVAILABLE_PCGS.values();
        final var seedU64 = 42L;
        final var seedU128 = new U128(seedU64, seedU64);

        long size = 1L << 26;

        for (var pcgClass_desc : pcgClasses) {
            try {
                var pcgInstance = pcgClass_desc.service().create(seedU128);
                // Output to file
                String filename = pcgClass_desc.cls_PCG().getSimpleName() + ".txt";
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
}