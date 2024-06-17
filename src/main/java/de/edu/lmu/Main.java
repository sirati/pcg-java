package de.edu.lmu;

public class Main {
    public static void main(String[] args) {
        var pcg = new PCG_XSH_RR(9223332041373072921L);
        var num = pcg.nextInt();
        System.out.println("num: " + num);

        var pcg2 = new PCG_XSH_RS(9223332041373072921L);
        for (int i = 0; i < 10; i++) {
            int rnd = pcg2.nextInt();
            System.out.println("number " + i + ": " + rnd);
        }
    }
}