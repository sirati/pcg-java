package de.edu.lmu.pcg;

public enum PCGImplementationVariant {
    JavaPrimitive,
    JavaVectoring,
    NativeProvided;

    public boolean isVectoring() {
        return this == JavaVectoring;
    }
}
