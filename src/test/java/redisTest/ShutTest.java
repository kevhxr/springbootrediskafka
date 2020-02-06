package redisTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ShutTest {

    public static void main(String[] args) {
        ExecutorService es = Executors.newFixedThreadPool(5);
        es.submit(()->doWork(1));
        es.submit(()->doWork(2));

        es.shutdownNow();
        //es.submit(()->doWork(3));
    }


    public static void doWork(int num){
        try {
            Thread.sleep(num*1000);
            System.out.println(Thread.currentThread().getName()+":"+num);
        } catch (InterruptedException e) {
            System.out.println("here"+num);
        }
    }
}
