package de.edu.lmu.pcg.test.crush;

import de.edu.lmu.pcg.*;
import org.junit.Test;

import java.util.Random;

public class C3_BigCrush {

    @Test
    public void crush_PCG_RXS_M_XS_32() {
        Adapter.bigCrush(new PCG_RXS_M_XS_32(42));
    }
    @Test
    public void crush_PCG_RXS_M_XS_64() {
        Adapter.bigCrush(new PCG_RXS_M_XS_64(42L));
    }
    @Test
    public void crush_PCG_XSH_RR() {
        Adapter.bigCrush(new PCG_XSH_RR(42));
    }
    @Test
    public void crush_PCG_PCG_XSH_RS() {
        Adapter.bigCrush(new PCG_XSH_RS(42));
    }
    @Test
    public void crush_PCG_XSL_RR() {
        Adapter.bigCrush(new PCG_XSL_RR(0, 42));
    }
}
