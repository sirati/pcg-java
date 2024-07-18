package de.edu.lmu.pcg.test.crush;


/**
 * to be used by at most 2 threads, ensures that the critical section is only executed by one thread at a time
 * and that afterward the other thread HAS TO execute the critical section
 */
public class InterleavingMutex {
    private boolean flag = false;
    private boolean active = false;

    public synchronized void lock(boolean self) {
        if (!active) {
            active = true;
            flag = self;
        }

        while (flag != self) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public synchronized void unlock(boolean self) {
        flag = !self;
        notify();
    }

    public void criticalSection(boolean self, Runnable criticalSection) {
        lock(self);
        criticalSection.run();
        unlock(self);
    }

    public void wait(boolean self) {
        lock(self);
        unlock(self);
    }
}
