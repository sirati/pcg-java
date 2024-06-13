package de.edu.lmu;

public class Main {
    public static void main(String[] args) {
        var pcg = new PCG_XSH_RR(9223332041373072921L);
        var num = pcg.nextInt();
        System.out.println("num: " + num);
    }
}