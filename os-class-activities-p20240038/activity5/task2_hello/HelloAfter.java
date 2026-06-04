package task2_hello;

import java.util.concurrent.Semaphore;

/**
 * After Semaphore Version - Safe and ordered execution
 * Uses semaphores to coordinate the printing sequence
 */
public class HelloAfter {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== AFTER: HELLO Ordering (With Semaphores) ===");
        System.out.println("Observe how semaphores force the correct 'HELLO' sequence:\n");
        
        for (int i = 1; i <= 10; i++) {
            System.out.print("Run " + i + ": ");
            
            Semaphore startH = new Semaphore(1);
            Semaphore afterE = new Semaphore(0);
            Semaphore afterL1 = new Semaphore(0);
            Semaphore afterL2 = new Semaphore(0);
            
            Thread p1 = new Thread(() -> {
                try {
                    startH.acquire();
                    System.out.print("H");
                    System.out.print("E");
                    afterE.release();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            
            Thread p2 = new Thread(() -> {
                try {
                    afterE.acquire();
                    System.out.print("L");
                    afterL1.release();
                    
                    afterL1.acquire();
                    System.out.print("L");
                    afterL2.release();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            
            Thread p3 = new Thread(() -> {
                try {
                    afterL2.acquire();
                    System.out.print("O");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            
            p1.start();
            p2.start();
            p3.start();
            
            p1.join();
            p2.join();
            p3.join();
            
            System.out.println();
            Thread.sleep(10); // Small sleep between runs for readability
        }
    }
}
