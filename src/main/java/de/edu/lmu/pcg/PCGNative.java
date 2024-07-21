package de.edu.lmu.pcg;

import java.nio.ByteOrder;

/**
 * @param <MemorySegment> this uses a generic for MemorySegment as it's an unstable API and may not be the same class
 *                        depending on the JDK version
 */
public interface PCGNative<MemorySegment> extends PCG {
    void fillSegment(MemorySegment memorySegment, ByteOrder order);
}
