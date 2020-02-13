package redisTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SemaphoreTest {


    Semaphore semaphore = new Semaphore(0);

    public static void main(String[] args) throws InterruptedException {
        SemaphoreTest semaphoreTest = new SemaphoreTest();
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        executorService.submit(() -> semaphoreTest.doTest(1));
        executorService.submit(() -> semaphoreTest.doRelease());

        executorService.shutdown();
    }

    public void doTest(int a) {
        while(true) {
            System.out.println("start!!!" + a);
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("done!!!" + a);

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public void doRelease() {

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        semaphore.release();
        System.out.println("release!!!");
    }
}
