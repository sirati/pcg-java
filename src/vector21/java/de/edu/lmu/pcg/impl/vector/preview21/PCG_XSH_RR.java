package de.edu.lmu.pcg.impl.vector.preview21;

import de.edu.lmu.pcg.PCGImplementationVariant;
import de.edu.lmu.pcg.PCGNative;
import de.edu.lmu.pcg.services.PCGCtorService;

import java.lang.foreign.MemorySegment;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("preview")
public class PCG_XSH_RR extends de.edu.lmu.pcg.PCG_XSH_RR implements PCGVector21 {
    public static class CtorService implements PCGCtorService.SeedU64<PCG_XSH_RR>,
            PCGVector21.Marker<PCG_XSH_RR, Long> {
        @Override
        public PCG_XSH_RR create(long seed) {
            return new PCG_XSH_RR(seed);
        }
    }

    public PCG_XSH_RR(long seed) {
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
