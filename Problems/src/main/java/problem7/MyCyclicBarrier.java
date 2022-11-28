package problem7;

public class MyCyclicBarrier {
    private int threadsLeft;
    private final int threads;
    private final Runnable finalAction;
    private long iterationId;

    public MyCyclicBarrier(int threads) {
        if (threads <= 0) {
            throw new IllegalArgumentException();
        }
        this.threads = threads;
        this.threadsLeft = threads;
        this.finalAction = null;
    }

    public MyCyclicBarrier(int threads, Runnable finalAction) {
        if (threads <= 0) {
            throw new IllegalArgumentException();
        }
        this.threads = threads;
        this.threadsLeft = threads;
        this.finalAction = finalAction;
    }

    public synchronized void await() throws InterruptedException {
        this.threadsLeft--;
        if (threadsLeft != 0) {
            waitingProcess();
        } else {
            finalizeIteration();
        }
    }

    private synchronized void waitingProcess() throws InterruptedException {
        long currIteration = this.iterationId;
        while(true) {
            wait();
            if (currIteration != this.iterationId) {
                return;
            }
        }
    }

    private synchronized void finalizeIteration() {
        try {
            if (this.finalAction != null) {
                this.finalAction.run();
            }
        } finally {
            this.threadsLeft = this.threads;
            this.iterationId++;
            notifyAll();
        }
    }
}
