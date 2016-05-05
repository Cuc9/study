package lesson7.test.threadsTestAndrTkach;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by arpi on 02.05.2016.
 */
public class Operations2 {

    private static final long WAIT_SEC = 900;

    static void transfer(Account acc1, Account acc2, int amount) throws IOException, InterruptedException {
        if (acc1.getBalance() < amount) {
            throw new IOException();
        }
        System.out.print("Try to lock a1 - ");
        if (acc1.getLock().tryLock(WAIT_SEC, TimeUnit.MILLISECONDS)) {
            try {
                System.out.print("OK. Try to lock a2 - ");
                Thread.sleep(3000);
                if (acc2.getLock().tryLock(WAIT_SEC, TimeUnit.MILLISECONDS)) {
                    System.out.println("OK.");
                    try {
                        acc1.withdraw(amount);
                        acc2.deposit(amount);
                    } finally {
                        acc2.getLock().unlock();
                        System.out.println("Release a2");
                    }
                }
            } finally {
                acc1.getLock().unlock();
                System.out.println("Release a1");
            }
        } else {
            System.out.println("Too much time waiting.");
        }
        /*synchronized (acc1){
            System.out.println("lock a1");
            try {
                Thread.sleep(1000);
            }catch (InterruptedException e){}
            System.out.println("Try to lock a2");
            synchronized (acc2){
                System.out.println("lock a2");
                acc1.withdraw(amount);
                acc2.deposit(amount);
            }
        }*/
        System.out.println("Transfer OK");
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        final Account a = new Account(1000);
        final Account b = new Account(2000);
        //new Thread().start();
        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    transfer(a, b, 50);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
        System.out.println("Main transfer started");
        transfer(b, a, 500);*/
        List<Future<Boolean>> list = new ArrayList<>();
        Random rnd = new Random();
        ExecutorService service = Executors.newFixedThreadPool(3);
        for (int i = 0; i < 10; i++) {
            Future<Boolean> f = service.submit(new Transfer(a, b, rnd.nextInt(200)));
            list.add(f);
        }
        service.shutdown();
        service.awaitTermination(5000, TimeUnit.MILLISECONDS);
        System.out.println();
        for (Future<Boolean> elem : list){
            try {
                System.out.println(elem.get());
            }catch (Exception e){}
        }
    }
}
