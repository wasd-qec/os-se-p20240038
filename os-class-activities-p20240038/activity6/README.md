# Class Activity 6 - Deadlock Simulation

- **Student Name:** Rith Chankolboth
- **Student ID:** p20240038
- **Programming Language Used:** Java

---

## Task 1: Deadlock Version

![Deadlock version](screenshots/task1_deadlock.png)

- **Shared resources:** Two bank account objects (`Account-1` and `Account-2`), each protected by their individual semaphores (`lock`).
- **Transaction 1:** Transfer 100 from `Account-1` to `Account-2`.
- **Transaction 2:** Transfer 200 from `Account-2` to `Account-1`.
- **Deadlock message shown:** `Deadlock detected: transactions are stuck`
- **Explanation of why the program got stuck:** 
  The program got stuck due to a circular wait scenario. `Thread-1` (running Transaction 1) acquired the lock for `Account-1` and slept for 100ms. During this delay, `Thread-2` (running Transaction 2) started and acquired the lock for `Account-2`. When `Thread-1` woke up, it attempted to acquire the lock for `Account-2` (held by `Thread-2`) and was forced to wait. Simultaneously, `Thread-2` attempted to acquire the lock for `Account-1` (held by `Thread-1`) and was also forced to wait. Since neither thread could release the lock it already held without obtaining the second lock, they became indefinitely blocked (deadlocked).

---

## Task 2: Deadlock Prevention Version

![Deadlock prevention](screenshots/task2_prevention.png)

- **Prevention strategy used:** Coarse-grained locking using a single global semaphore mutex to serialize all transfers.
- **Semaphore mutex initial value:** 1
- **Starting total:** 2000
- **Final total:** 2000
- **Did both transfers complete?** Yes, both transfers completed successfully.
- **Why no deadlock occurred:**
  Deadlock did not occur because the program enforced mutual exclusion on the entire transaction operation instead of locking accounts individually. When `Worker 1` acquired the global `mutex` semaphore, it locked the whole transaction process. `Worker 2` had to wait for the mutex to be released before it could start its transfer. This serialized the transaction operations, removing the "hold-and-wait" and "circular wait" conditions.

---

## Questions

1. **What are the two shared resources in your bank transaction simulation?**
   The two shared resources are the two `Account` objects: `Account-1` and `Account-2` (specifically, their internal state such as their `balance` fields and their individual semaphore `lock`s).

2. **Which line or section of your Task 1 program creates hold-and-wait?**
   In `DeadlockSimulation.java`, the section inside the `Transfer.transfer` method creates the hold-and-wait condition:
   ```java
   from.lock.acquire(); // Holds 'from' lock
   ...
   Thread.sleep(100);   // Continues to hold 'from' lock while sleeping
   ...
   to.lock.acquire();   // Waits for 'to' lock while holding 'from' lock
   ```
   A thread holds the lock to the source account (`from.lock`) and waits to acquire the lock to the destination account (`to.lock`).

3. **How does Task 1 create circular wait?**
   - `Thread-1` holds the lock for `Account-1` and is waiting for the lock on `Account-2`.
   - `Thread-2` holds the lock for `Account-2` and is waiting for the lock on `Account-1`.
   This creates a closed cycle of dependency: `Thread-1 -> Account-2 (held by Thread-2) -> Account-1 (held by Thread-1)`.

4. **Why does the Task 1 program need a watchdog or timeout?**
   Without a watchdog or timeout, the program would hang silently and indefinitely in the deadlock state. The watchdog thread monitors execution progress and prints a clear diagnostic message (`Deadlock detected: transactions are stuck`) to inform the user that a deadlock occurred, and exits the program cleanly.

5. **How does the single semaphore mutex prevent deadlock in Task 2?**
   The single semaphore `mutex` ensures that only one transfer thread can enter the bank transfer critical section at a time. The thread must acquire the global mutex before locking or modifying any account, and releases it only after completing the transaction. This guarantees that transactions are processed atomically and sequentially with respect to each other.

6. **Which of the four deadlock conditions does your Task 2 solution remove or avoid?**
   It removes/avoids **Hold-and-Wait** and **Circular Wait**. 
   - **Hold-and-Wait is removed** because a thread no longer holds one resource (an account lock) and waits for another; it acquires the single global mutex representing the right to transfer, and then accesses all required resources immediately without waiting.
   - **Circular Wait is avoided** because transactions execute sequentially, meaning there can be no cycle of threads waiting on resources held by one another.

7. **Why must the final total bank balance remain unchanged after both transfers?**
   This is a fundamental requirement of **data consistency** and **conservation of money**. Since money is only being transferred between accounts within the closed system, no money is created or destroyed. The sum of all account balances before the transfers must equal the sum of all balances after the transfers.

---

## Reflection

*What did this activity teach you about deadlock prevention in real systems such as banking, databases, or file systems?*
This activity demonstrates that while simple solutions like coarse-grained locking (e.g., a single global mutex) are highly effective at preventing deadlocks, they severely limit concurrency because only one operation can run at a time. In real-world systems like databases or high-frequency banking APIs, more fine-grained deadlock prevention techniques (such as strict lock ordering or non-blocking try-lock operations with timeouts and retries) are preferred to ensure both safety and high throughput.
