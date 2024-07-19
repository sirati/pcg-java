package de.edu.lmu.pcg.impl.vector.preview21;

import de.edu.lmu.pcg.PCGImplementationVariant;
import de.edu.lmu.pcg.services.PCGCtorService;

public class PCG_RXS_M_XS_64 extends de.edu.lmu.pcg.PCG_RXS_M_XS_64 {
    public static class CtorService implements PCGCtorService.SeedU64<PCG_RXS_M_XS_64> {
        @Override
        public PCG_RXS_M_XS_64 create(long seed) {
            return new PCG_RXS_M_XS_64(seed);
        }
        @Override
        public PCGImplementationVariant getImplementationVariant() {
            return PCGImplementationVariant.JavaVectoring21;
        }
    }

    public PCG_RXS_M_XS_64(long seed) {
        super(seed);
    }



}
