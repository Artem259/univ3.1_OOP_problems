package problem8;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static org.junit.Assert.assertEquals;

public class MyReentrantLockTest {
    private int threadsN;
    private int iterationsN;

    @Before
    public void setUp() {
        threadsN = 100;
        iterationsN = 10;
    }

    @Test
    public void testLock() {
        final ArrayList<Integer> list = new ArrayList<>();
        list.add(0);

        Runnable barrierRunnable = () -> {
            if (list.size() < iterationsN) {
                list.add(0);
            }
        };
        CyclicBarrier barrier = new CyclicBarrier(threadsN, barrierRunnable);
        MyReentrantLock lock = new MyReentrantLock();

        Runnable threadRunnable = () -> {
            for (int i=0; i<iterationsN; i++) {
                try {
                    lock.lock();
                    int index = list.size() - 1;
                    list.set(index, list.get(index)+1);
                    lock.unlock();
                    barrier.await();
                } catch (InterruptedException | BrokenBarrierException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        for (int i=0; i<threadsN-1; i++) {
            new Thread(threadRunnable).start();
        }
        threadRunnable.run();

        ArrayList<Integer> expectedList = new ArrayList<>();
        for (int i=0; i<iterationsN; i++) {
            expectedList.add(threadsN);
        }

        System.out.println(expectedList);
        assertEquals(expectedList, list);
    }

}