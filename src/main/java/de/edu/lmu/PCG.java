package de.edu.lmu;

public interface PCG  {
    void skipLong(long ulong);
    void skip(int uint);
    void newState();

    /**
     * will generate one state and always advance internal state by one. will split generated number into ints and will drop any over max
     * @param arr
     * @param start
     * @param max
     */
    void fillInto(int[] arr, int start, int max);
    int bitesPerIteration();
}
