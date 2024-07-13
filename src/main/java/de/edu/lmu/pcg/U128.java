package de.edu.lmu.pcg;

import java.math.BigInteger;

public class U128 extends Number {
    public final long hi;
    public final long lo;

    public U128(long hi, long lo) {
        this.hi = hi;
        this.lo = lo;
    }

    @Override
    public int intValue() {
        return (int) lo;
    }

    @Override
    public long longValue() {
        return lo;
    }

    @Override
    public float floatValue() {
        return lo;
    }

    @Override
    public double doubleValue() {
        return hi * Math.pow(2, 64) + lo;
    }

    public BigInteger toBigInteger() {
        return BigInteger.valueOf(hi).shiftLeft(64).add(BigInteger.valueOf(lo));
    }
}
