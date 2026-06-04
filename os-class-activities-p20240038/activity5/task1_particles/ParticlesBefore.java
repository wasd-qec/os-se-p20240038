import java.util.ArrayList;
import java.util.List;

/**
 * Before Semaphore Version - Intentionally unsafe
 * Shows step-by-step how race conditions lead to errors
 */
public class ParticlesBefore {
    private static final int BUFFER_SIZE = 100;
    private static final int NUM_PRODUCERS = 3;
    
    private static List<String> buffer = new ArrayList<>();
    private static int producedCount = 0;
    private static int packagedCount = 0;
    private static int pairIdCounter = 0;
    private static volatile boolean running = true;
    private static Object printLock = new Object();  // Just for clean console output
    private static String mode = "empty";
    
    static class Producer extends Thread {
        int machineId;
        
        Producer(int machineId) {
            this.machineId = machineId;
        }
        
        public void run() {
            if (mode.equals("empty")) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            while (running) {
                try {
                    int pairId = pairIdCounter++;
                    String p1 = "M" + machineId + "-" + pairId + "-P1";
                    String p2 = "M" + machineId + "-" + pairId + "-P2";
                    
                    synchronized (printLock) {
                        System.out.println("[Producer " + machineId + "] Creating pair " + pairId);
                        System.out.println("  -> Particle 1: " + p1);
                    }
                    
                    Thread.sleep(5);  // Simulate work
                    
                    // RACE CONDITION: Check buffer size (not atomic)
                    if (buffer.size() >= BUFFER_SIZE) {
                        System.err.println("The producing machine is broken");
                        running = false;
                        break;
                    }
                    
                    // Add P1 (UNSYNCHRONIZED - race condition opportunity)
                    buffer.add(p1);
                    synchronized (printLock) {
                        System.out.println("  -> Added P1 to buffer (size now: " + buffer.size() + ")");
                    }
                    
                    // Intentional delay here - allows consumer to interfere
                    Thread.sleep(3);
                    
                    synchronized (printLock) {
                        System.out.println("  -> Particle 2: " + p2);
                    }
                    
                    // Add P2 (UNSYNCHRONIZED - race condition opportunity)
                    buffer.add(p2);
                    synchronized (printLock) {
                        System.out.println("  -> Added P2 to buffer (size now: " + buffer.size() + ")");
                    }
                    
                    producedCount++;
                    
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
    
    static class Consumer extends Thread {
        public void run() {
            if (mode.equals("full")) {
                return;
            }
            if (mode.equals("incorrect")) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
            while (running) {
                try {
                    Thread.sleep(8);  // Consumer runs slightly slower
                    
                    // RACE CONDITION: Check if buffer has particles (not atomic)
                    if (buffer.isEmpty()) {
                        System.err.println("\n!!! RACE CONDITION DETECTED !!!");
                        System.err.println("The packaging machine is broken");
                        running = false;
                        break;
                    }
                    
                    String packaged1 = null;
                    String packaged2 = null;
                    
                    synchronized (printLock) {
                        System.out.println("[Consumer] Attempting to package particles...");
                        System.out.println("  -> Buffer has " + buffer.size() + " particles");
                        System.out.println("  -> Fetching particle 1...");
                    }
                    
                    // RACE CONDITION: Between check and remove, another thread could empty buffer
                    packaged1 = buffer.remove(0);
                    synchronized (printLock) {
                        System.out.println("  -> Fetched: " + packaged1);
                    }
                    
                    // Check if second particle still available
                    if (buffer.isEmpty()) {
                        System.err.println("\n!!! RACE CONDITION DETECTED !!!");
                        System.err.println("The packaging machine is broken");
                        running = false;
                        break;
                    }
                    
                    Thread.sleep(4);  // Delay between fetching particles
                    
                    synchronized (printLock) {
                        System.out.println("  -> Buffer has " + buffer.size() + " particles");
                        System.out.println("  -> Fetching particle 2...");
                    }
                    
                    packaged2 = buffer.remove(0);
                    synchronized (printLock) {
                        System.out.println("  -> Fetched: " + packaged2);
                    }
                    
                    // Verify they're from same pair
                    String[] parts1 = packaged1.split("-");
                    String[] parts2 = packaged2.split("-");
                    
                    String machine1 = parts1[0];
                    String pairId1 = parts1[1];
                    String machine2 = parts2[0];
                    String pairId2 = parts2[1];
                    
                    if (!machine1.equals(machine2) || !pairId1.equals(pairId2)) {
                        System.err.println("\n!!! RACE CONDITION DETECTED !!!");
                        System.err.println("Pairs are incorrect");
                        System.err.println("  Expected: " + machine1 + "-" + pairId1 + " but got " + machine2 + "-" + pairId2);
                        running = false;
                        break;
                    }
                    
                    packagedCount++;
                    synchronized (printLock) {
                        System.out.println("  -> Packaged pair: " + packaged1 + " + " + packaged2);
                        System.out.println();
                    }
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
    
    public static void main(String[] args) throws InterruptedException {
        if (args.length > 0) {
            String arg = args[0].toLowerCase();
            if (arg.equals("full") || arg.equals("incorrect") || arg.equals("empty")) {
                mode = arg;
            } else {
                System.out.println("Invalid mode. Supported modes: empty, full, incorrect.");
                System.exit(1);
            }
        } else {
            System.out.println("Choose simulation mode for race condition demonstration:");
            System.out.println("  1. Empty Buffer Error (The packaging machine is broken) - Default");
            System.out.println("  2. Full Buffer Error (The producing machine is broken)");
            System.out.println("  3. Incorrect Pair Error (Pairs are incorrect)");
            System.out.print("Enter choice (1-3) or press Enter for default: ");
            try {
                java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
                String input = reader.readLine();
                if (input != null) {
                    input = input.trim();
                    if (input.equals("2")) {
                        mode = "full";
                    } else if (input.equals("3")) {
                        mode = "incorrect";
                    } else {
                        mode = "empty";
                    }
                }
            } catch (java.io.IOException e) {
                mode = "empty";
            }
        }
        System.out.println("Running in " + mode.toUpperCase() + " mode.");
        System.out.println();

        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.println("║  BEFORE: Unsafe Particle Buffer (No Semaphores)              ║");
        System.out.println("║  Watch as race conditions cause synchronization errors       ║");
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
        System.out.println();
        System.out.println("This version has NO synchronization.");
        System.out.println("Race conditions will occur between producers and consumer.");
        System.out.println("Watch the detailed steps as particles are produced and consumed.");
        System.out.println();
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println();
        
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
            int lastProduced = 0;
            int lastPackaged = 0;
            int printCount = 0;
            while (running) {
                try {
                    Thread.sleep(2000);
                    if (printCount > 0 && printCount % 5 == 0) {
                        synchronized (printLock) {
                            System.out.println("\n--- STATUS UPDATE ---");
                            System.out.println("Produced pairs: " + producedCount + 
                                             " | Packaged pairs: " + packagedCount + 
                                             " | Buffer particles: " + buffer.size());
                            System.out.println("---\n");
                        }
                    }
                    printCount++;
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
        
        System.out.println("\n═══════════════════════════════════════════════════════════════");
        System.out.println("FINAL REPORT");
        System.out.println("═══════════════════════════════════════════════════════════════");
        System.out.println("Produced pairs: " + producedCount);
        System.out.println("Packaged pairs: " + packagedCount);
        System.out.println("Buffer particles: " + buffer.size());
        System.out.println("\nConclusion: Race condition caused synchronization failure!");
    }
}
