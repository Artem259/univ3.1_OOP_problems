package problem8;

public class MyReentrantLock {
    private final Object syncObject;
    private boolean isLocked;

    public MyReentrantLock() {
        this.syncObject = new Object();
        this.isLocked = false;
    }

    public void lock() throws InterruptedException {
        synchronized (syncObject) {
            while (isLocked) {
                syncObject.wait();
            }
            isLocked = true;
        }
    }

    public void unlock() {
        synchronized (syncObject) {
            isLocked = false;
            syncObject.notify();
        }
    }
}
