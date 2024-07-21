package de.edu.lmu.pcg;

import java.nio.ByteBuffer;

public interface PCGInt extends PCG {
    int nextInt();
    default int bitsPerIteration() {
        return 32;
    }

    @Override
    default void fillOnceInto(int[] arr, int start, int max) {
        int next = nextInt();
        if (max == 0)return;
        arr[start] = next;
    }

    @Override
    default void fill(ByteBuffer byteBuffer) {
        var intBuffer = byteBuffer.asIntBuffer();
        var remaining = intBuffer.remaining();
        for (int i = 0; i < remaining; i++) {
            intBuffer.put(nextInt());
        }
    }
}
