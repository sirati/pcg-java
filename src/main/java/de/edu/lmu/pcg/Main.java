package de.edu.lmu.pcg;

public class Main {
    public static void main(String[] args) {
        var pcg = new PCG_RXS_M_XS_32((int)9223332041373072921L);
        for (int i = 0; i < 10; i++) {
            int rnd = pcg.nextInt();
            System.out.println("number " + i + ": " + rnd);
        }
    }
}