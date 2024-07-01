package de.edu.lmu.pcg.test;

import de.edu.lmu.pcg.PCG;

public interface TestConstructor<T extends PCG, Seed extends Number> {
    T create(Number seed);
}
