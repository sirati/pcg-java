package de.edu.lmu.pcg;

public interface PCGInt extends PCG {
    int nextInt();
    default int bitesPerIteration() {
        return 32;
    }

    @Override
    default void fillInto(int[] arr, int start, int max) {
        int next = nextInt();
        if (max == 0)return;
        arr[start] = next;
    }
}
