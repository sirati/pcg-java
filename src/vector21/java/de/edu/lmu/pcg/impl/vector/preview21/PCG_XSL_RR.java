package de.edu.lmu.pcg.impl.vector.preview21;

import de.edu.lmu.pcg.PCGImplementationVariant;
import de.edu.lmu.pcg.PCGNative;
import de.edu.lmu.pcg.U128;
import de.edu.lmu.pcg.services.PCGCtorService;

import java.lang.foreign.MemorySegment;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@SuppressWarnings("preview")
public class PCG_XSL_RR extends de.edu.lmu.pcg.PCG_XSL_RR implements PCGVector21.U64 {
    public static class CtorService implements PCGCtorService.SeedU128<PCG_XSL_RR>,
            PCGVector21.Marker<PCG_XSL_RR, U128> {
        @Override
        public PCG_XSL_RR create(U128 seed) {
            return new PCG_XSL_RR(seed.toBigInteger());
        }
    }

    public PCG_XSL_RR(BigInteger seed) {
        super(seed);
    }
    public PCG_XSL_RR(U128 seed) {
        super(seed);
    }
    public PCG_XSL_RR(long seedLower, long seedUpper) {
        super(seedLower, seedUpper);
    }

    @Override
    public void fillSegment(MemorySegment memorySegment, ByteOrder order) {
        throw new UnsupportedOperationException("Not implemented yet. Fallback on naive implementation.");

    }

}
