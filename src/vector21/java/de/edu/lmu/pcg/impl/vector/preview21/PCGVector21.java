package de.edu.lmu.pcg.impl.vector.preview21;

import de.edu.lmu.pcg.PCG;
import de.edu.lmu.pcg.PCGNative;
import de.edu.lmu.pcg.SeedTypeMarker;
import de.edu.lmu.pcg.services.PCGCtorService;

import java.lang.foreign.MemorySegment;
import java.nio.ByteBuffer;

@SuppressWarnings("preview")
public interface PCGVector21 extends PCGNative<MemorySegment> {
    interface Marker<T extends PCG & SeedTypeMarker<Seed> & PCGVector21, Seed extends Number> extends PCGCtorService.Vectorized<T, Seed, MemorySegment> { }

    @Override
    default void fill(ByteBuffer byteBuffer) {
        fillSegment(MemorySegment.ofBuffer(byteBuffer), byteBuffer.order());
    }
}
