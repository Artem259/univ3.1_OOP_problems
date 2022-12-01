package problem10;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class MyThreadPool {
    private final Worker[] workers;
    private final LinkedBlockingQueue<Runnable> queue;
    private volatile boolean isInterrupted;
    private final Object syncObj;

    public MyThreadPool(int threads) {
        this.queue = new LinkedBlockingQueue<>();
        this.workers = new Worker[threads];
        this.isInterrupted = false;
        this.syncObj = new Object();
        for (int i=0; i<threads; i++) {
            workers[i] = new Worker();
            workers[i].start();
        }
    }

    public void submit(Runnable task) {
        synchronized (syncObj) {
            queue.add(task);
            syncObj.notify();
        }
    }

    public void shutdown() {
        synchronized (syncObj) {
            isInterrupted = true;
            syncObj.notifyAll();
        }
    }

    public ArrayList<Runnable> shutdownNow() {
        ArrayList<Runnable> unfinished = new ArrayList<>();
        while (!queue.isEmpty()) {
            unfinished.add(queue.poll());
        }
        for (Worker worker : workers) {
            worker.interrupt();
        }
        shutdown();
        return unfinished;
    }

    public boolean isShutdown() {
        return isInterrupted;
    }

    public int waitingInQueue() {
        return queue.size();
    }

    private class Worker extends Thread {
        @Override
        public void run() {
            Runnable task;
            while (!isInterrupted) {
                synchronized (syncObj) {
                    if (queue.isEmpty()) {
                        try {
                            syncObj.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                task = queue.poll();
                if (task != null) {
                    try {
                        task.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
