package de.edu.lmu.pcg;

import java.math.BigInteger;

import static de.edu.lmu.pcg.Util.MASK_64;

public class U128 extends Number {
    public final long hi;
    public final long lo;

    public U128(long hi, long lo) {
        this.hi = hi;
        this.lo = lo;
    }

    public U128(BigInteger value) {
        this(value.and(MASK_64).longValue(), value.shiftRight(64).and(MASK_64).longValue());
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
        return  new BigInteger(1, new byte[]{
                (byte)(hi >>> 56), (byte)(hi >>> 48), (byte)(hi >>> 40), (byte)(hi >>> 32),
                (byte)(hi >>> 24), (byte)(hi >>> 16), (byte)(hi >>> 8), (byte)hi,
                (byte)(lo >>> 56), (byte)(lo >>> 48), (byte)(lo >>> 40), (byte)(lo >>> 32),
                (byte)(lo >>> 24), (byte)(lo >>> 16), (byte)(lo >>> 8), (byte)lo
        });
    }

    public String toHexString() {
        return String.format("%016x%016x", hi, lo);
    }
}
