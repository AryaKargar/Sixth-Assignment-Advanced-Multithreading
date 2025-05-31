package Banking;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankAccount {
    private final int id;
    private int balance;
    private final Lock lock = new ReentrantLock();

    public BankAccount(int id, int initialBalance) {
        this.id = id;
        this.balance = initialBalance;
    }

    public int getId(){
        return  id;
    }
    public int getBalance() {
        lock.lock();
        try {
            return balance;
        } finally {
            lock.unlock();
        }
    }


    public Lock getLock() {
        return lock;
    }

    public void deposit(int amount) {
        lock.lock();
        try {
            balance += amount;
        } finally {
            lock.unlock();
        }
    }

    public void withdraw(int amount) {
        lock.lock();
        try {
            balance -= amount;
        } finally {
            lock.unlock();
        }
    }

    public void transfer(BankAccount target, int amount) {
        BankAccount first = this;
        BankAccount second = target;

        // Acquire locks in a consistent global order (by account ID)
        if (this.id > target.id) {
            first = target;
            second = this;
        }

        first.getLock().lock();
        second.getLock().lock();

        try {
            this.balance -= amount;
            target.balance += amount;
        } finally {
            second.getLock().unlock();
            first.getLock().unlock();
        }
    }

}
