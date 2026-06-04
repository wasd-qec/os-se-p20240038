import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * After Semaphore Version - Safe with proper synchronization
 * Uses semaphores to protect the particle buffer
 */
public class ParticlesAfter {
    private static final int BUFFER_SIZE = 100;
    private static final int MAX_PAIRS = BUFFER_SIZE / 2; // 50 pairs
    private static final int NUM_PRODUCERS = 3;
    
    private static List<String> buffer = new ArrayList<>();
    private static int producedCount = 0;
    private static int packagedCount = 0;
    private static int pairIdCounter = 0;
    private static volatile boolean running = true;
    
    // Semaphores for synchronization
    private static Semaphore emptyPairs = new Semaphore(MAX_PAIRS);  // Available pair spaces
    private static Semaphore fullPairs = new Semaphore(0);           // Completed pairs available
    private static Semaphore mutex = new Semaphore(1);               // Buffer access protection
    
    static class Producer extends Thread {
        int machineId;
        
        Producer(int machineId) {
            this.machineId = machineId;
        }
        
        public void run() {
            while (running) {
                try {
                    // Produce a pair
                    int pairId = pairIdCounter++;
                    String p1 = "M" + machineId + "-" + pairId + "-P1";
                    String p2 = "M" + machineId + "-" + pairId + "-P2";
                    
                    // Wait for available pair space
                    emptyPairs.acquire();
                    
                    // Enter critical section
                    mutex.acquire();
                    try {
                        // Safety check
                        if (buffer.size() >= BUFFER_SIZE) {
                            System.err.println("The producing machine is broken");
                            running = false;
                            break;
                        }
                        
                        // Add both particles consecutively
                        buffer.add(p1);
                        buffer.add(p2);
                        producedCount++;
                        
                    } finally {
                        // Exit critical section
                        mutex.release();
                    }
                    
                    // Signal that a pair is available to package
                    fullPairs.release();
                    
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
    
    static class Consumer extends Thread {
        public void run() {
            while (running) {
                try {
                    // Wait for a completed pair to be available
                    fullPairs.acquire();
                    
                    // Enter critical section
                    mutex.acquire();
                    try {
                        // Safety check
                        if (buffer.size() < 2) {
                            System.err.println("The packaging machine is broken");
                            running = false;
                            break;
                        }
                        
                        // Remove two particles
                        String packaged1 = buffer.remove(0);
                        String packaged2 = buffer.remove(0);
                        
                        // Verify they belong to the same pair
                        String[] parts1 = packaged1.split("-");
                        String[] parts2 = packaged2.split("-");
                        
                        String machine1 = parts1[0];
                        String pairId1 = parts1[1];
                        String machine2 = parts2[0];
                        String pairId2 = parts2[1];
                        
                        if (!machine1.equals(machine2) || !pairId1.equals(pairId2)) {
                            System.err.println("Pairs are incorrect");
                            running = false;
                            break;
                        }
                        
                        packagedCount++;
                        
                    } finally {
                        // Exit critical section
                        mutex.release();
                    }
                    
                    // Signal that a pair space is now available
                    emptyPairs.release();
                    
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== AFTER: Safe Particle Buffer (With Semaphores) ===");
        System.out.println("Buffer capacity: 100 particles = 50 pairs");
        System.out.println("Producer threads: " + NUM_PRODUCERS + " | Consumer threads: 1");
        System.out.println("Synchronization: mutex, empty_pairs, full_pairs semaphores\n");
        
        // Create producer threads
        Thread[] producers = new Thread[NUM_PRODUCERS];
        for (int i = 1; i <= NUM_PRODUCERS; i++) {
            producers[i - 1] = new Producer(i);
            producers[i - 1].start();
        }
        
        // Create consumer thread
        Thread consumer = new Consumer();
        consumer.start();
        
        // Status printer
        Thread statusPrinter = new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(1000);
                    System.out.println("Produced pairs: " + producedCount + 
                                     " | Packaged pairs: " + packagedCount + 
                                     " | Buffer particles: " + buffer.size());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });
        statusPrinter.start();
        
        // Wait for all threads to finish
        for (int i = 0; i < NUM_PRODUCERS; i++) {
            producers[i].join();
        }
        consumer.join();
        statusPrinter.join();
        
        System.out.println("\n=== FINAL REPORT ===");
        System.out.println("Produced pairs: " + producedCount + 
                         " | Packaged pairs: " + packagedCount + 
                         " | Buffer particles: " + buffer.size());
    }
}
