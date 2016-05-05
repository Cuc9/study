package lesson7.test.threadsTestAndrTkach;

import jdk.nashorn.internal.codegen.CompilerConstants;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created by arpi on 02.05.2016.
 */
public class Transfer implements Callable<Boolean> {
    Account acc1, acc2;
    int amount;
    private static final long WAIT_SEC = 2000;

    public Transfer(Account accFron, Account accTo, int amount) {
        this.acc1 = accFron;
        this.acc2 = accTo;
        this.amount = amount;
    }

    @Override
    public Boolean call() throws Exception {
        Random rnd = new Random();
        if (acc1.getBalance() < amount) {
            throw new IOException();
        }
        System.out.print("Try to lock a1 - ");
        if (acc1.getLock().tryLock(WAIT_SEC, TimeUnit.MILLISECONDS)) {
            try {
                System.out.print("OK. Try to lock a2 - ");
                if (acc2.getLock().tryLock(WAIT_SEC,TimeUnit.MILLISECONDS)){
                    System.out.println("OK.");
                    try {
                        acc1.withdraw(amount);
                        acc2.deposit(amount);
                        Thread.sleep(rnd.nextInt(3)*1000);
                    }finally {
                        acc2.getLock().unlock();
                        System.out.println("Release a2");
                    }
                } else {
                    System.out.println("Too much time waiting.");
                    return false;
                }
            }finally {
                acc1.getLock().unlock();
                System.out.println("Release a1");
            }
        } else {
            System.out.println("Too much time waiting.");
            return false;
        }
        System.out.println("Transfer OK");
        return true;
    }
}
