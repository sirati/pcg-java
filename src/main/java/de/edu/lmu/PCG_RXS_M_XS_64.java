package de.edu.lmu;

public class PCG_RXS_M_XS_64 implements PCGLong {

    private static final long MCG_MULTIPLIER = Long.parseUnsignedLong("12605985483714917081");

    private long state;

    public PCG_RXS_M_XS_64(long seed) {
        this.state = seed;
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
        long mask = (1 << 5) -1;
        long upper5Bits = (this.state >> (64-5)) & mask;

        //permutation
        long xorshifted = this.state ^ (this.state >> 5+upper5Bits);
        long multiplied = xorshifted * MCG_MULTIPLIER;
        newState();
        return multiplied ^ (multiplied >> 43);
    }
}
