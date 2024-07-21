package de.edu.lmu.pcg.test.crush;

import de.edu.lmu.pcg.*;
import de.edu.lmu.pcg.services.PCGCtorService;
import de.edu.lmu.pcg.test.TestConstructor;
import de.edu.lmu.pcg.test.Util;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.util.Random;
import java.util.stream.Stream;

public class SmallCrush {

    @Test
    public void testCrush() {
        var seed =new BigInteger(128, new Random());// new Random().nextInt();
        System.out.println(STR."Seed: \{seed} (0x\{seed.toString(16)})");
        Adapter.smallCrush(new PCG_XSL_RR(seed));
    }


    static Stream<PCGCtorService<?, ?>> rngCtorProvider() {
        return Util.rngCtorProvider();
    }

    @ParameterizedTest
    @MethodSource("rngCtorProvider")
    <T extends PCG & SeedTypeMarker<?>> void smallCrush(PCGCtorService<T, ?> constructor) {
        var rnd = new Random();
        var seed =new U128(rnd.nextLong(), rnd.nextLong());
        System.out.println(STR."Seed: \{seed} (0x\{Long.toHexString(seed.hi)}{Long.toHexString(seed.lo)})");
        Adapter.smallCrush(constructor.create(seed));
    }

    @Test
    public void testCrush2() {
        Adapter.smallCrush(new PCG_RXS_M_XS_32(new Random().nextInt()));
    }
}
