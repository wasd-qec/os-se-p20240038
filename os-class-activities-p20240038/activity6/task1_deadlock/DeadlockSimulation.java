import java.util.concurrent.Semaphore;

class Account {
    String name;
    int balance;
    Semaphore lock = new Semaphore(1);

    Account(String name, int balance) {
        this.name = name;
        this.balance = balance;
    }
}

class Transfer {
    static volatile String t1Status = "Starting";
    static volatile String t2Status = "Starting";
    static volatile boolean t1Finished = false;
    static volatile boolean t2Finished = false;

    static void transfer(Account from, Account to, int amount) {
        String threadName = Thread.currentThread().getName();
        try {
            updateStatus(threadName, "trying to lock FROM " + from.name);
            System.out.println(threadName + " trying to lock FROM " + from.name);
            
            from.lock.acquire();
            
            updateStatus(threadName, "locked FROM " + from.name + ", trying to lock TO " + to.name);
            System.out.println(threadName + " locked FROM " + from.name);

            // Delay to ensure the other thread has time to acquire its first lock
            Thread.sleep(100);

            System.out.println(threadName + " trying to lock TO " + to.name);
            
            to.lock.acquire();
            
            updateStatus(threadName, "locked TO " + to.name);
            System.out.println(threadName + " locked TO " + to.name);

            from.balance -= amount;
            to.balance += amount;

            System.out.println(threadName + " transfer completed");
            updateStatus(threadName, "completed");
            setFinished(threadName);

            to.lock.release();
            from.lock.release();
        } catch (InterruptedException e) {
            System.out.println(threadName + " was interrupted.");
        }
    }

    private static void updateStatus(String threadName, String status) {
        if (threadName.equals("Thread-1")) {
            t1Status = status;
        } else if (threadName.equals("Thread-2")) {
            t2Status = status;
        }
    }

    private static void setFinished(String threadName) {
        if (threadName.equals("Thread-1")) {
            t1Finished = true;
        } else if (threadName.equals("Thread-2")) {
            t2Finished = true;
        }
    }
}

public class DeadlockSimulation {
    public static void main(String[] args) {
        Account account1 = new Account("Account-1", 1000);
        Account account2 = new Account("Account-2", 1000);

        System.out.println("Starting Balances:");
        System.out.println("Account-1 balance: " + account1.balance);
        System.out.println("Account-2 balance: " + account2.balance);
        System.out.println("Starting total: " + (account1.balance + account2.balance));
        System.out.println();

        Thread t1 = new Thread(() ->
                Transfer.transfer(account1, account2, 100),
                "Thread-1"
        );

        Thread t2 = new Thread(() ->
                Transfer.transfer(account2, account1, 200),
                "Thread-2"
        );

        t1.start();
        t2.start();

        // Watchdog thread to detect deadlock
        Thread watchdog = new Thread(() -> {
            try {
                // Wait for 3 seconds to see if transfers complete
                Thread.sleep(3000);
                if (!Transfer.t1Finished || !Transfer.t2Finished) {
                    System.out.println("\nDeadlock detected: transactions are stuck");
                    
                    // Show current lock state / waiting info
                    printWaitingState("Thread-1", Transfer.t1Status, "Account-2");
                    printWaitingState("Thread-2", Transfer.t2Status, "Account-1");

                    System.out.println("\nFinal Balances (in deadlock):");
                    System.out.println("Account-1 balance: " + account1.balance);
                    System.out.println("Account-2 balance: " + account2.balance);
                    System.out.println("Final total: " + (account1.balance + account2.balance));
                    
                    // Exit cleanly since it is in deadlock
                    System.exit(0);
                }
            } catch (InterruptedException e) {
                // Watchdog interrupted
            }
        });
        watchdog.setDaemon(true);
        watchdog.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void printWaitingState(String threadName, String status, String targetAccount) {
        if (status.contains("trying to lock TO")) {
            System.out.println(threadName + " is waiting for " + targetAccount);
        } else {
            System.out.println(threadName + " is in state: " + status);
        }
    }
}
