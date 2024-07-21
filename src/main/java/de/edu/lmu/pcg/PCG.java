package de.edu.lmu.pcg;

import de.edu.lmu.pcg.services.PCGCtorService;

import java.nio.ByteBuffer;

public interface PCG {
    void skipLong(long ulong);

    void skip(int uint);

    void newState();

    /**
     * will generate one state and always advance internal state by one. will split generated number into ints and will drop any over max
     *
     * @param arr
     * @param start
     * @param max
     */
    void fillOnceInto(int[] arr, int start, int max);

    /**
     * @param byteBuffer remaining space must be aligned to bytesPerIteration
     */
    void fill(ByteBuffer byteBuffer);

    int bitsPerIteration();

    default int bytesPerIteration() {
        return bitsPerIteration() / 8;
    }


    default PCGImplementationVariant getImplementationVariant() {
        return PCGCtorService.AVAILABLE_PCGS.get(this.getClass()).service().getImplementationVariant();
    }
}
