package de.edu.lmu.pcg;

public interface PCGLong extends PCG {
    long nextLong();

    default int bitesPerIteration() {
        return 32;
    }

    @Override
    default void fillInto(int[] arr, int start, int max) {
        long next = nextLong();
        if (max == 0) return;
        arr[start] = (int) next;
        if (max == 1) return;
        arr[start + 1] = (int) (next >>> 32);
    }
}
