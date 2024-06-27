package de.edu.lmu;

public interface TestConstructor<T extends PCG, Seed extends Number> {
    T create(Number seed);
}
