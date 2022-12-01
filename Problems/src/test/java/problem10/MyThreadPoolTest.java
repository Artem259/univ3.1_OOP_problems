package problem10;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class MyThreadPoolTest {
    private AtomicInteger atomic;

    @Before
    public void setUp() {
        atomic = new AtomicInteger(0);
    }

    @Test
    public void submit() throws InterruptedException {
        final int tasks = 100000;
        final int threads = 1;

        Runnable runnable = () -> atomic.incrementAndGet();
        MyThreadPool threadPool = new MyThreadPool(threads);
        for (int i=0; i<tasks; i++) {
            threadPool.submit(runnable);
        }

        while (threadPool.waitingInQueue() != 0) {
            Thread.sleep(10);
        }
        assertEquals(tasks, atomic.get());
    }
}
