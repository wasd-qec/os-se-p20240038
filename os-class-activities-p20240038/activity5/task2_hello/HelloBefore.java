package task2_hello;

/**
 * Before Semaphore Version - Intentionally unsafe ordering
 * Shows how concurrent threads print HELLO letters in arbitrary order
 */
public class HelloBefore {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== BEFORE: HELLO Ordering (No Semaphores) ===");
        System.out.println("Observe how concurrent workers print letters in unpredictable order:\n");
        
        for (int i = 1; i <= 10; i++) {
            System.out.print("Run " + i + ": ");
            
            Thread p1 = new Thread(() -> {
                System.out.print("H");
                System.out.print("E");
            });
            
            Thread p2 = new Thread(() -> {
                System.out.print("L");
                System.out.print("L");
            });
            
            Thread p3 = new Thread(() -> {
                System.out.print("O");
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
