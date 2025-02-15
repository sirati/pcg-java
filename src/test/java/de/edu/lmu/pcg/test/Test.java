package de.edu.lmu.pcg.test;

import de.edu.lmu.pcg.*;
import de.edu.lmu.pcg.services.PCGCtorService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.stream.Stream;

public class Test {
    // test if the Java implementation returns the same results as the C implementation
    public static void main(String[] args) {
        compareCAndJavaResults();
    }

    public static Stream<PCGCtorService<?, ?>> rngCtorProvider() {
        return Util.rngCtorProvider();
    }

    @ParameterizedTest()
    @MethodSource("rngCtorProvider")
    <T extends PCG & SeedTypeMarker<?>> void compareCAndJavaResultIndividually(PCGCtorService<T, ?> constructor) throws Exception {
        // list of PCG versions to test
        U128 seed = new U128(0, 42L);

        var pcgInstance = constructor.create(seed);
        if (pcgInstance instanceof PCGInt) {
            checkCFileWith32Bit((PCGInt) pcgInstance, pcgInstance.getClass().getSimpleName());
        } else {
            checkCFileWith64Bit((PCGLong) pcgInstance, pcgInstance.getClass().getSimpleName());
        }
    }

    private static void compareCAndJavaResults() {
        var footest = (PCGLong & SeedTypeMarker<?>) new PCGBuilder<>().type("PCG_RXS_M_XS_64").seed(1L).build();
        footest.nextLong();

        var pcgClasses = PCGCtorService.AVAILABLE_PCGS.values();
        final var seedU64 = 42L;
        final var seedU128 = new U128(0, seedU64);

        // For all PCGs: First get results generated in C, second generate Java numbers and check differences
        for (var pcgClass_desc : pcgClasses) {
            var pcgInstance = pcgClass_desc.service().create(seedU128);
            if (pcgInstance instanceof PCGInt) {
                // test PCGs with 32bit output
                try {
                    checkCFileWith32Bit((PCGInt) pcgInstance, pcgClass_desc.cls_PCG().getSimpleName());
                } catch (Exception e) {
                    System.out.println("There was an error.");
                    System.out.println(e);
                }
            } else {
                // test PCGs with 64bit output
                try {
                    checkCFileWith64Bit((PCGLong) pcgInstance, pcgClass_desc.cls_PCG().getSimpleName());
                } catch (Exception e) {
                    System.out.println("There was an error: ");
                    System.out.println(e);
                }
            }
        }
    }

    private static void checkCFileWith32Bit(PCGInt pcg, String name) throws Exception {
        int failedBytes = 0;
        //read results from C implementation
        try (RandomAccessFile file = new RandomAccessFile(new File("src/test/resources/results_C/C_" + name + ".bin"), "r")) {
            //Get file channel in read-only mode
            FileChannel fileChannel = file.getChannel();

            //Get direct byte buffer access using channel.map() operation
            MappedByteBuffer bufferC = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());

            //create and fill ByteBuffer with Java PCG
            var bufferJava = java.nio.ByteBuffer.allocate(bufferC.capacity());
            pcg.fill(bufferJava);

            //check how often bufferVector and bufferManual are not equal
            for (int i = 0; i < bufferC.capacity(); i++) {
                if (bufferC.get(i) != bufferJava.get(i)) {
                    failedBytes += 1;
                }
            }

            //output test Results
            System.out.println("For " + name + ":");
            if (failedBytes == 0) {
                System.out.println("Results are equal!" + "\n");
            } else {
                System.out.println("Results are in " + failedBytes + " Bytes not equal." + "\n");
            }
        }
    }

    private static void checkCFileWith64Bit(PCGLong pcg, String name) throws Exception {
        int failedBytes = 0;
        //read results from C implementation
        try (RandomAccessFile file = new RandomAccessFile(new File("src/test/resources/results_C/C_" + name + ".bin"), "r")) {
            //Get file channel in read-only mode
            FileChannel fileChannel = file.getChannel();

            //Get direct byte buffer access using channel.map() operation
            MappedByteBuffer bufferC = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());

            //create and fill ByteBuffer with Java PCG
            var bufferJava = java.nio.ByteBuffer.allocate(bufferC.capacity());
            pcg.fill(bufferJava);


            //IntStream.range(0, 40000).map(i-> bufferC.get(i)).toArray()
            //check how often bufferVector and bufferManual are not equal
            for (int i = 0; i < bufferC.limit(); i++) {
                if (bufferC.get(i) != bufferJava.get(i)) {
                    failedBytes += 1;
                }
            }

            //output test Results
            System.out.println("For " + name + ":");
            if (failedBytes == 0) {
                System.out.println("Results are equal!" + "\n");
            } else {
                System.out.println("Results are in " + failedBytes + " Bytes not equal." + "\n");
            }
        }
    }
}
