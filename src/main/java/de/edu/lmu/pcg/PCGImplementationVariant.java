package de.edu.lmu.pcg;

import de.edu.lmu.pcg.services.PCGCtorService;

import java.util.EnumMap;

public enum PCGImplementationVariant {
    JavaPrimitive,
    JavaVectoring,
    NativeProvided;

    private static final PCGImplementationVariant[] standardPriority = {NativeProvided, JavaVectoring, JavaPrimitive};

    public boolean isVectoring() {
        return this == JavaVectoring;
    }

    public static class PrioMap extends EnumMap<PCGImplementationVariant, PCGCtorService.PCGCtorServiceDescriptor<?, ?>> {
        public PrioMap() {
            super(PCGImplementationVariant.class);
        }

        public PCGCtorService.PCGCtorServiceDescriptor<?, ?> get() {
            return get(standardPriority);
        }


        public PCGCtorService.PCGCtorServiceDescriptor<?, ?> get(PCGImplementationVariant... prios) {
            for (PCGImplementationVariant prio : prios) {
                var service = super.get(prio);
                if (service != null) {
                    return service;
                }
            }
            return prios != standardPriority ? get() : null;
        }


        public PCGCtorService.PCGCtorServiceDescriptor<?, ?> get(PCGImplementationVariant prios) {
            return get(new PCGImplementationVariant[]{prios});
        }
    }
}
