package de.edu.lmu.pcg.impl.vector.preview21;

public class TestMain {

    public static final int CAPACITY = 1024;// * 1024;

    public static void main(String[] args) {
        var pcgManual = new de.edu.lmu.pcg.PCG_XSH_RS(42);
        var pcgVector = new de.edu.lmu.pcg.impl.vector.preview21.PCG_XSH_RS(42);

        var bufferVector = java.nio.ByteBuffer.allocate(CAPACITY);
        var bufferManual = java.nio.ByteBuffer.allocate(CAPACITY);

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
       bufferVector.flip();
       bufferManual.flip();
        for (int i = 0; i < bufferVector.limit(); i++) {
            if (bufferVector.get(i) != bufferManual.get(i)) {
                System.out.println("Buffers are not equal");
                return;
            }
        }
        System.out.println("Buffers are equal");
    }
}
