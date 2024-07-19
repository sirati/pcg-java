package de.edu.lmu.pcg.impl.vector.preview21;

import de.edu.lmu.pcg.PCGImplementationVariant;
import de.edu.lmu.pcg.services.PCGCtorService;

public class PCG_RXS_M_XS_32 extends de.edu.lmu.pcg.PCG_RXS_M_XS_32 {
    public PCG_RXS_M_XS_32(int seed) {
        super(seed);
    }

    public static class CtorService implements PCGCtorService.SeedU32<PCG_RXS_M_XS_32> {

        @Override
        public PCG_RXS_M_XS_32 create(int seed) {
            return null;
        }
        @Override
        public PCGImplementationVariant getImplementationVariant() {
            return PCGImplementationVariant.JavaVectoring21;
        }
    }
}
