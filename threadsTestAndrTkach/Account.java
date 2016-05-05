package lesson7.test.threadsTestAndrTkach;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by arpi on 30.04.2016.
 */
public class Account {
    private int balance;
    private Lock lock;

    public int getBalance() {
        return balance;
    }

    public Lock getLock() {
        return lock;
    }

    public Account(int initialBalance) {
        this.balance = initialBalance;
        this.lock = new ReentrantLock();
    }

    public void withdraw(int amount) {
        balance -= amount;
    }

    public void deposit(int amount) {
        balance += amount;
    }
}
