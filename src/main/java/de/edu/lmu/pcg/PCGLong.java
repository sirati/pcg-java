package de.edu.lmu.pcg;

import java.nio.ByteBuffer;

public interface PCGLong extends PCG {
    long nextLong();

    default int bitsPerIteration() {
        return 32;
    }

    @Override
    default void fillOnceInto(int[] arr, int start, int max) {
        long next = nextLong();
        if (max == 0) return;
        arr[start] = (int) next;
        if (max == 1) return;
        arr[start + 1] = (int) (next >>> 32);
    }

    @Override
    default void fill(ByteBuffer byteBuffer) {
        var longBuffer = byteBuffer.asLongBuffer();
        var remaining = longBuffer.remaining();
        for (long i = 0; i < remaining; i++) {
            longBuffer.put(nextLong());
        }
    }
}
