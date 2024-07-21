package de.edu.lmu.pcg.impl.vector.preview21;

import de.edu.lmu.pcg.PCGImplementationVariant;
import de.edu.lmu.pcg.PCGNative;
import de.edu.lmu.pcg.services.PCGCtorService;

import java.lang.foreign.MemorySegment;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("preview")
public class PCG_RXS_M_XS_32 extends de.edu.lmu.pcg.PCG_RXS_M_XS_32 implements PCGVector21 {
    public static class CtorService implements PCGCtorService.SeedU32<PCG_RXS_M_XS_32>, PCGVector21.Marker<PCG_RXS_M_XS_32, Integer>  {
        @Override
        public PCG_RXS_M_XS_32 create(int seed) {
            return new PCG_RXS_M_XS_32(seed);
        }
    }

    public PCG_RXS_M_XS_32(int seed) {
        super(seed);
    }

    @Override
    public void fill(ByteBuffer byteBuffer) {
        PCGVector21.super.fill(byteBuffer);
    }

    @Override
    public void fillSegment(MemorySegment memorySegment, ByteOrder order) {
        throw new UnsupportedOperationException("Not implemented yet. Fallback on naive implementation.");
    }
}
