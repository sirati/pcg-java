package de.edu.lmu;

public class PCG_XSH_RS implements PCG<PCG_XSH_RS>, PCGIntOutput {
    private long state;

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
        long xorshift   = this.state ^ (this.state >> 22);
        int randomshift = (int) (this.state >> 61);
        // probably we should have a next state method instead of using skip witch is slow!!
        newState();
        return (int) (xorshift >> (22 + randomshift));
    }
}
