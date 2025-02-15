package de.edu.lmu.pcg;

import de.edu.lmu.pcg.services.PCGCtorService;

public class PCG_RXS_M_XS_64 implements PCGLong, SeedTypeMarker<Long> {
    public static class CtorService implements PCGCtorService.SeedU64<PCG_RXS_M_XS_64> {
        @Override
        public PCG_RXS_M_XS_64 create(long seed) {
            return new PCG_RXS_M_XS_64(seed);
        }
    }

    private static final long MCG_MULTIPLIER = Long.parseUnsignedLong("12605985483714917081");

    protected long state;

    public PCG_RXS_M_XS_64(long seed) {
        this.state = Util.newLongState(0) + seed;
        newState();
    }

    @Override
    public void newState() {
        this.state = Util.newLongState(this.state);
    }

    @Override
    public void skipLong(long ulong) {
        this.state = Util.skipLong(this.state, ulong);
    }

    @Override
    public void skip(int uint) {
        this.skipLong((long) uint);
    }

    @Override
    public long nextLong() {
        long mask = (1 << 5) - 1;
        long upper5Bits = (this.state >>> (64 - 5)) & mask;

        //permutation
        long xorshifted = this.state ^ (this.state >>> 5 + upper5Bits);
        long multiplied = xorshifted * MCG_MULTIPLIER;
        newState();
        return multiplied ^ (multiplied >>> 43);
    }
}
