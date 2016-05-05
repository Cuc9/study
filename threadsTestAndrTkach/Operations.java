package lesson7.test.threadsTestAndrTkach;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by arpi on 30.04.2016.
 */
public class Operations {
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
                if (acc2.getLock().tryLock(WAIT_SEC,TimeUnit.MILLISECONDS)){
                    System.out.println("OK.");
                    try {
                        acc1.withdraw(amount);
                        acc2.deposit(amount);
                    }finally {
                        acc2.getLock().unlock();
                        System.out.println("Release a2");
                    }
                }
            }finally {
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
        new Thread(new Runnable() {
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
        transfer(b, a, 500);
    }
}
