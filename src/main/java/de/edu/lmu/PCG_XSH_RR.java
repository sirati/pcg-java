package de.edu.lmu;

public class PCG_XSH_RR implements PCG<PCG_XSH_RR>, PCGIntOutput{
    private long state;

    public PCG_XSH_RR(long seed) {
        this.state = seed;
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
        //todo!
        return 0;
    }
}
