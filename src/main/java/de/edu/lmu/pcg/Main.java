package de.edu.lmu.pcg;

public class Main {
    public static void main(String[] args) {
        var pcg = new PCGBuilder<>()
                .type(PCG_RXS_M_XS_64.class)
                .seed(9223332041373072921L)
                .build();

        //var pcg = new PCG_RXS_M_XS_32((int)9223332041373072921L);
        for (int i = 0; i < 10; i++) {
            long rnd = pcg.nextLong();
            System.out.println("number " + i + ": " + rnd);
        }
    }
}