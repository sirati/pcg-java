package de.edu.lmu.pcg.impl.vector.preview21;

import de.edu.lmu.pcg.PCGImplementationVariant;
import de.edu.lmu.pcg.services.PCGCtorService;

public class PCG_XSH_RR extends de.edu.lmu.pcg.PCG_XSH_RR {
    public static class CtorService implements PCGCtorService.SeedU64<PCG_XSH_RR> {
        @Override
        public PCG_XSH_RR create(long seed) {
            return new PCG_XSH_RR(seed);
        }
        @Override
        public PCGImplementationVariant getImplementationVariant() {
            return PCGImplementationVariant.JavaVectoring21;
        }
    }

    public PCG_XSH_RR(long seed) {
        super(seed);
    }
}
