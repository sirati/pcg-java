package de.edu.lmu.pcg.test;

import de.edu.lmu.pcg.PCG;
import de.edu.lmu.pcg.PCGBuilder;
import de.edu.lmu.pcg.U128;
import de.edu.lmu.pcg.services.PCGCtorService;

import java.util.Collection;
import java.util.stream.Stream;

public class Util {
    public static Stream<PCGCtorService<?, ?>> rngCtorProvider() {
        return (/*(Collection<PCGCtorService<?, ?>>)(Collection)*/PCGCtorService.AVAILABLE_PCGS.values())
                .stream().map(PCGCtorService.PCGCtorServiceDescriptor::service);
    }
}
