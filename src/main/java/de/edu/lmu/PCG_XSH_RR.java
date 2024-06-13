package de.edu.lmu;

// 64 bit state to 32 bit output
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
        // permutation
        int shiftedInt = (int) ((this.state ^ (this.state >> 18)) >> 27);
        int rotationDistance = (int) (this.state >> 59);
        this.state = Integer.rotateRight(shiftedInt, rotationDistance);
        return (int) this.state;
    }
}
