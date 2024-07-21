package de.edu.lmu.pcg.impl.vector.preview21;

import de.edu.lmu.pcg.PCGBuilder;
import de.edu.lmu.pcg.PCG_XSH_RS;

import static de.edu.lmu.pcg.PCGImplementationVariant.JavaPrimitive;
import static de.edu.lmu.pcg.PCGImplementationVariant.JavaVectoring;

public class TestMain {

    public static final int CAPACITY = 1024 * 1024 * 1024;

    public static void main(String[] args) {
        var builder = new PCGBuilder<>().type(PCG_XSH_RS.class).seed(42L);
        //var builder = new PCGBuilder<>().type(PCG_XSH_RR.class).seed(42L);

        var pcgManual = builder.preferred_variant(JavaPrimitive).build();
        var pcgVector = builder.preferred_variant(JavaVectoring).build();
        if (pcgManual.getImplementationVariant() == pcgVector.getImplementationVariant())
            throw new AssertionError(STR."Implementation variants are equal: \{pcgManual.getImplementationVariant()}");

        var bufferVector = java.nio.ByteBuffer.allocateDirect(CAPACITY);
        var bufferManual = java.nio.ByteBuffer.allocateDirect(CAPACITY);

        //check millis
        long start = System.currentTimeMillis();
        pcgVector.fill(bufferVector);
        long end = System.currentTimeMillis();
        System.out.println("Vectorized took: " + (end - start) + "ms");
        start = System.currentTimeMillis();
        pcgManual.fill(bufferManual);
        end = System.currentTimeMillis();
        System.out.println("Manual took: " + (end - start) + "ms");

        //check if bufferVector and bufferManual are equal
        if (bufferVector.capacity() != bufferManual.capacity() || bufferVector.limit() != CAPACITY || bufferManual.limit() != bufferVector.limit())
            throw new AssertionError("Something is wrong with the buffers, they are not equal in capacity or limit. Equals check with produce false positives.");

        for (int i = 0; i < bufferVector.capacity(); i++) {
            if (bufferVector.get(i) != bufferManual.get(i)) {
                System.out.println(STR."Buffers are not equal at: \{i}");
                return;
            }
        }
        System.out.println("Buffers are equal");
    }
}
