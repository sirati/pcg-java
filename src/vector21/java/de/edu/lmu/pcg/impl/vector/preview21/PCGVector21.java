package de.edu.lmu.pcg.impl.vector.preview21;

import de.edu.lmu.pcg.*;
import de.edu.lmu.pcg.services.PCGCtorService;

import java.lang.foreign.MemorySegment;
import java.nio.ByteBuffer;

@SuppressWarnings("preview")
public interface PCGVector21 extends PCGNative<MemorySegment> {
    interface Marker<T extends PCG & SeedTypeMarker<Seed> & PCGVector21, Seed extends Number> extends PCGCtorService.Vectorized<T, Seed, MemorySegment> {
    }

    @Override
    default void fill(ByteBuffer byteBuffer) {
        fillSegment(MemorySegment.ofBuffer(byteBuffer), byteBuffer.order());
    }

    interface U32 extends PCGVector21, PCGInt {
        @Override
        default void fill(ByteBuffer byteBuffer) {
            PCGVector21.super.fill(byteBuffer);
        }
    }


    interface U64 extends PCGVector21, PCGLong {
        @Override
        default void fill(ByteBuffer byteBuffer) {
            PCGVector21.super.fill(byteBuffer);
        }
    }
}
