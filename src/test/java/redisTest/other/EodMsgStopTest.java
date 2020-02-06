package redisTest.other;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

public class EodMsgStopTest {

    private ExecutorService eodMsgExecutor;
    private ExecutorService normaldMsgExecutor;
    private boolean reachEod = false;
    private Queue<Integer> msgQueue = new LinkedBlockingQueue<>();


    public EodMsgStopTest(ExecutorService eodMsgExecutor, ExecutorService normaldMsgExecutor) {
        this.eodMsgExecutor = eodMsgExecutor;
        this.normaldMsgExecutor = normaldMsgExecutor;
    }

    public void onMessage() {
        int msg = 0;
        while (msg < 20) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("[Producer] produce new msg:" + msg);
            msgQueue.add(msg);
            msg++;
            processMessage();
        }
        for (Integer integer : msgQueue) {
            System.out.print(integer + ",");
        }
        eodMsgExecutor.shutdown();
        normaldMsgExecutor.shutdown();
    }

    public void processMessage() {
        if (!reachEod) {
            int msgVal = msgQueue.remove();
            if (msgVal > 0 && msgVal % 4 == 0) {
                System.out.println("[E-Consumer] EOD message reached");
                reachEod = true;
                processEodMsg(msgVal);
            } else {
                System.out.println("[N-Consumer] Normal message reached");
                processNormalMsg(msgVal);
            }
        }
    }

    private void processEodMsg(int msgType) {
        if (!eodMsgExecutor.isShutdown()) {
            eodMsgExecutor.submit(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("[E-Consumer] received and processed EODMsg: " + msgType);
            });
        }
    }

    private void processNormalMsg(int msgType) {
        if (!eodMsgExecutor.isShutdown()) {
            eodMsgExecutor.submit(() -> {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("[N-Consumer] received and processed NormalMsg: " + msgType);
            });
        }
    }


    public static void main(String[] args) {
        ExecutorService eodMsgExecutor = MsgExecutorPool.generateThreadPool();
        ExecutorService normaldMsgExecutor = MsgExecutorPool.generateThreadPool();
        EodMsgStopTest msgHandler = new EodMsgStopTest(eodMsgExecutor, normaldMsgExecutor);
        msgHandler.onMessage();

    }
}
