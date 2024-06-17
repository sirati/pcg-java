package de.edu.lmu;

public interface PCG<TSelf extends PCG<TSelf> & PCGOutput>  {
    void skipLong(long ulong);
    void skip(int uint);
    void newState();
}
