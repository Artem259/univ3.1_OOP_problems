package problem7;

public class MyCyclicBarrier {
    private final Object syncObject;
    private int threadsLeft;
    private final int threads;
    private final Runnable finalAction;
    private volatile long iterationId;

    public MyCyclicBarrier(int threads) {
        if (threads <= 0) {
            throw new IllegalArgumentException();
        }
        this.syncObject = new Object();
        this.threads = threads;
        this.threadsLeft = threads;
        this.finalAction = null;
    }

    public MyCyclicBarrier(int threads, Runnable finalAction) {
        if (threads <= 0) {
            throw new IllegalArgumentException();
        }
        this.syncObject = new Object();
        this.threads = threads;
        this.threadsLeft = threads;
        this.finalAction = finalAction;
    }

    public void await() throws InterruptedException {
        synchronized (syncObject) {
            this.threadsLeft--;
            if (threadsLeft != 0) {
                waitingProcess();
            } else {
                finalizeIteration();
            }
        }
    }

    private void waitingProcess() throws InterruptedException {
        synchronized (syncObject) {
            long currIteration = this.iterationId;
            while(true) {
                syncObject.wait();
                if (currIteration != this.iterationId) {
                    return;
                }
            }
        }
    }

    private void finalizeIteration() {
        synchronized (syncObject) {
            try {
                if (this.finalAction != null) {
                    this.finalAction.run();
                }
            } finally {
                this.threadsLeft = this.threads;
                this.iterationId++;
                syncObject.notifyAll();
            }
        }
    }
}
