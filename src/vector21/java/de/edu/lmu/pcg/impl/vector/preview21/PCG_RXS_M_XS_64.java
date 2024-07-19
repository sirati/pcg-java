package de.edu.lmu.pcg.impl.vector.preview21;

import de.edu.lmu.pcg.PCGImplementationVariant;
import de.edu.lmu.pcg.PCGNative;
import de.edu.lmu.pcg.services.PCGCtorService;

import java.lang.foreign.MemorySegment;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("preview")
public class PCG_RXS_M_XS_64 extends de.edu.lmu.pcg.PCG_RXS_M_XS_64 implements PCGVector21 {
    public static class CtorService implements PCGCtorService.SeedU64<PCG_RXS_M_XS_64>,
            PCGVector21.Marker<PCG_RXS_M_XS_64, Long> {
        @Override
        public PCG_RXS_M_XS_64 create(long seed) {
            return new PCG_RXS_M_XS_64(seed);
        }
    }

    public PCG_RXS_M_XS_64(long seed) {
        super(seed);
    }

    @Override
    public void fill(ByteBuffer byteBuffer) {
        PCGVector21.super.fill(byteBuffer);
    }

    @Override
    public void fillSegment(MemorySegment memorySegment, ByteOrder order) {

    }



}
