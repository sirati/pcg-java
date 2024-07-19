package de.edu.lmu.pcg;

public enum PCGImplementationVariant {
    JavaPrimitive,
    JavaVectoring21;

    public boolean isVectoring() {
        return this == JavaVectoring21;
    }
}
