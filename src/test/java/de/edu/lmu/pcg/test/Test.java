package de.edu.lmu.pcg.test;
import de.edu.lmu.pcg.*;
import de.edu.lmu.pcg.services.PCGCtorService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Test {
    // test if the Java implementation returns the same results as the C implementation
    public static void main(String[] args) {
        var footest = (PCGLong & SeedTypeMarker<?>) new PCGBuilder<>().type("PCG_RXS_M_XS_64").seed(1L).build();
        footest.nextLong();

        var pcgClasses = PCGCtorService.AVAILABLE_PCGS.values();
        final var seedU64 = 42L;
        final var seedU128 = new U128(seedU64, seedU64);

        int size = 5000;

        // For all PCGs: First get results generated in C, second generate Java numbers and check differences
        for (var pcgClass_desc : pcgClasses) {
            var pcgInstance = pcgClass_desc.service().create(seedU128);
                if (pcgInstance instanceof PCGInt) {
                    // test PCGs with 32bit output
                    List<Integer> cNums = new ArrayList<Integer>();
                    try {
                        System.out.println("Trying: " + pcgClass_desc.cls_PCG().getSimpleName());
                        read32BitFile(cNums, pcgClass_desc.cls_PCG().getSimpleName());
                    } catch (IOException ignored) {}
                    int failed = test32BitOutput(pcgInstance, cNums, size);
                    System.out.println("There were " + failed + " numbers different for " + pcgClass_desc.cls_PCG().getSimpleName() + ".");
                } else {
                    // test PCGs with 64bit output
                    List<Long> cNums = new ArrayList<Long>();
                    try {
                        System.out.println("Trying: " + pcgClass_desc.cls_PCG().getSimpleName());
                        read64BitFile(cNums, pcgClass_desc.cls_PCG().getSimpleName());
                    } catch (IOException ignored) {}
                    int failed = test64BitOutput(pcgInstance, cNums, size);
                    System.out.println("There were " + failed + " numbers different for " + pcgClass_desc.cls_PCG().getSimpleName() + ".");
                }
            }

    }

    private static void read32BitFile(List<Integer> cNums, String name) throws IOException {
        //read results from C implementation
        DataInputStream in = new DataInputStream(new FileInputStream("src/test/resources/results_C/C_" + name + ".bin"));
        while (in.available()>0) {
            cNums.add(in.readInt());
        }
        in.close();
    }

    private static void read64BitFile(List<Long> cNums , String name) throws IOException {
        //read results from C implementation
        DataInputStream in = new DataInputStream(new FileInputStream("src/test/resources/results_C/C_" + name + ".bin"));
        while (in.available()>0) {
                cNums.add(in.readLong());
        }
        in.close();
    }

    private static int test32BitOutput(Object pcg, List<Integer> cNums, int size) {
        // count numbers that differ in C and Java implementation on $size numbers
        int failedNumbers = 0;
        System.out.println(cNums);
        for (int i = 1; i <= size; i++) {
            int randomNumber = ((PCGInt) pcg).nextInt();
            if (cNums.get(i) != randomNumber) {
                failedNumbers += 1;
            }
        }
        return failedNumbers;
    }

    private static int test64BitOutput(Object pcg, List<Long> cNums, int size) {
        // count numbers that differ in C and Java implementation on $size numbers
        int failedNumbers = 0;
        System.out.println(cNums);
        for (int i = 1; i <= size; i++) {
            long randomNumber = ((PCGLong) pcg).nextLong();
            if (cNums.get(i) != randomNumber) {
                failedNumbers += 1;
            }
        }
        return failedNumbers;
    }
}
