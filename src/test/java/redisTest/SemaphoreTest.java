package redisTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class SemaphoreTest {

    public int a = 0;
    Semaphore semaphore = new Semaphore(0);

    public ReentrantLock lock = new ReentrantLock();


    public static void main(String[] args) throws InterruptedException {
        SemaphoreTest semaphoreTest = new SemaphoreTest();
        ExecutorService executorService = Executors.newFixedThreadPool(5);
/*        executorService.submit(() -> semaphoreTest.third());
        executorService.submit(() -> semaphoreTest.second());
        executorService.submit(() -> semaphoreTest.first());*/
        System.out.println(3%2);
        executorService.shutdown();
    }

    public void first(){
        System.out.println(1);
        semaphore.release();
        a = 1;
    }

    public void second(){
        while(a != 1) {
        }
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(2);
        semaphore.release();
        a = 2;
    }

    public void third(){
        while(a != 2) {
        }

        try {
            semaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(3);
    }
}
