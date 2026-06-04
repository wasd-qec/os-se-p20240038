import java.util.concurrent.Semaphore;

class Account {
    String name;
    int balance;

    Account(String name, int balance) {
        this.name = name;
        this.balance = balance;
    }
}

class Transfer {
    static Semaphore mutex = new Semaphore(1);

    static void transfer(Account from, Account to, int amount) {
        String threadName = Thread.currentThread().getName();
        try {
            // Acquire the global mutex before performing any account operations
            mutex.acquire();
            try {
                // Sleep briefly to simulate work and allow concurrency tests
                Thread.sleep(100);

                from.balance -= amount;
                to.balance += amount;

                System.out.println(threadName + " transferred " + amount + " from " + from.name + " to " + to.name);
            } finally {
                // Always release the mutex in a finally block
                mutex.release();
            }
        } catch (InterruptedException e) {
            System.out.println(threadName + " was interrupted.");
        }
    }
}

public class DeadlockFixed {
    public static void main(String[] args) {
        Account account1 = new Account("Account-1", 1000);
        Account account2 = new Account("Account-2", 1000);

        System.out.println("Starting total: " + (account1.balance + account2.balance));
        System.out.println("Starting Balances:");
        System.out.println("Account-1 balance: " + account1.balance);
        System.out.println("Account-2 balance: " + account2.balance);
        System.out.println();

        Thread t1 = new Thread(() ->
                Transfer.transfer(account1, account2, 100),
                "Worker 1"
        );

        Thread t2 = new Thread(() ->
                Transfer.transfer(account2, account1, 200),
                "Worker 2"
        );

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println();
        System.out.println("Final Account-1: " + account1.balance);
        System.out.println("Final Account-2: " + account2.balance);
        System.out.println("Final total: " + (account1.balance + account2.balance));
        System.out.println("No deadlock occurred");
    }
}
