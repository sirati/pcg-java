package de.edu.lmu.pcg;

import de.edu.lmu.pcg.services.PCGCtorService;

public class PCG_XSH_RS implements PCGInt, SeedMarker<Long> {
    public static class CtorService implements PCGCtorService.SeedU64<PCG_XSH_RS> {
        @Override
        public PCG_XSH_RS create(long seed) {
            return new PCG_XSH_RS(seed);
        }
    }

    private long state;

    public static PCG_XSH_RS createFromNumber(Number seed) {
        return new PCG_XSH_RS(seed.longValue());
    }

    public PCG_XSH_RS(long seed) {
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
        this.skipLong(uint);
    }

    @Override
    public int nextInt() {
        long xorshift = this.state ^ (this.state >> 22);
        int randomshift = (int) (this.state >> 61);
        newState();
        return (int) (xorshift >> (22 + randomshift));
    }
}
