package de.edu.lmu.pcg.impl.vector.preview21;

import de.edu.lmu.pcg.PCGImplementationVariant;
import de.edu.lmu.pcg.U128;
import de.edu.lmu.pcg.services.PCGCtorService;

import java.math.BigInteger;

public class PCG_XSL_RR extends de.edu.lmu.pcg.PCG_XSL_RR {
    public PCG_XSL_RR(BigInteger seed) {
        super(seed);
    }

    public static class CtorService implements PCGCtorService.SeedU128<PCG_XSL_RR> {
        @Override
        public PCG_XSL_RR create(U128 seed) {
            return new PCG_XSL_RR(seed.toBigInteger());
        }
        @Override
        public PCGImplementationVariant getImplementationVariant() {
            return PCGImplementationVariant.JavaVectoring21;
        }
    }

}
