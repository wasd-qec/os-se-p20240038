# Class Activity 3 — Socket Communication & Multithreading

- **Student Name:** Rith Chankolboth
- **Student ID:** p20240038
- **Date:** 2026-03-26

---

## Task 1: TCP Socket Communication (C)

### Compilation & Execution

![Socket exchange](screenshots/task1_socket_exchange.png)

### Answers

1. **Role of `bind()` / Why client doesn't call it:**
   > `bind()` associates a socket with a specific local IP address and port number. The server calls `bind()` to listen on a well-known port so clients know where to connect. The client does not call it because the OS automatically assigns a random ephemeral port to the client socket when `connect()` is called.

2. **What `accept()` returns:**
   > It blocks until a client connects, then returns a **new socket file descriptor** dedicated to communication with that specific client. The original listening socket remains open to accept other incoming connections.

3. **Starting client before server:**
   > The client's `connect()` call fails with a "Connection refused" error because no server is listening on the target port yet.

4. **What `htons()` does:**
   > It converts a 16-bit integer (like port numbers) from Host Byte Order to Network Byte Order (Big-Endian), ensuring proper port interpretation across different CPU architectures.

5. **Socket call sequence diagram:**
   > ```text
   >        Server                                     Client
   >   socket()                                      socket()
   >      │                                             │
   >   bind()                                           │
   >      │                                             │
   >   listen()                                         │
   >      │                                             │
   >   accept() <─── (TCP Handshake Connection) ────> connect()
   >      │                                             │
   >   read()   <─────────── Request ───────────────  write()
   >      │                                             │
   >   write()  ──────────── Response ─────────────>  read()
   >      │                                             │
   >   close()                                       close()
   > ```

## Task 2: POSIX Threads (C)

### Output — Without Mutex (Race Condition)

![Threads output](screenshots/task2_threads_output.png)

### Output — With Mutex (Correct)

Correct synchronized output is shown in the same terminal output session, maintaining the shared counter at its correct sequential value without interleaved data corruption.

### Answers

1. **What is a race condition?**
   > A race condition occurs in concurrent programming when multiple threads access and modify shared resources simultaneously without synchronization, making the final outcome dependent on the execution order (scheduling) of the threads.

2. **What does `pthread_mutex_lock()` do?**
   > It acquires a lock on a mutex object. If another thread already holds the lock, the calling thread blocks (goes to sleep) until the lock is released.

3. **Removing `pthread_join()`:**
   > The main thread would exit immediately after spawning the child threads, terminating the process and cleaning up all threads before they can complete their work.

4. **Thread vs Process:**
   > A process is an isolated execution environment with its own address space, file descriptors, and resources. A thread is an execution path within a process that shares the process's memory space and resources with other sibling threads.

---

## Task 3: Java Multithreading

### ThreadDemo Output

![Java threading](screenshots/task3_java_output.png)

### RunnableDemo Output

```text
Main: creating threads with Runnable interface

[Download] Step 1 (Thread: downloader, ID: 19)
[Process] Step 1 (Thread: processor, ID: 20)
[Upload] Step 1 (Thread: uploader, ID: 21)
[Download] Step 2 (Thread: downloader, ID: 19)
[Upload] Step 2 (Thread: uploader, ID: 21)
[Process] Step 2 (Thread: processor, ID: 20)
[Download] Step 3 (Thread: downloader, ID: 19)
[Upload] Step 3 (Thread: uploader, ID: 21)
[Process] Step 3 (Thread: processor, ID: 20)

Main: all tasks completed.
```

### PoolDemo Output

```text
Main: creating thread pool with 2 threads for 6 tasks

Task 2 started on thread pool-1-thread-2 (ID: 20)
Task 1 started on thread pool-1-thread-1 (ID: 19)
Task 2 completed on thread pool-1-thread-2
Task 1 completed on thread pool-1-thread-1
Task 4 started on thread pool-1-thread-1 (ID: 19)
Task 3 started on thread pool-1-thread-2 (ID: 20)
Task 4 completed on thread pool-1-thread-1
Task 5 started on thread pool-1-thread-1 (ID: 19)
Task 3 completed on thread pool-1-thread-2
Task 6 started on thread pool-1-thread-2 (ID: 20)
Task 5 completed on thread pool-1-thread-1
Task 6 completed on thread pool-1-thread-2

Main: all tasks completed. Pool shut down.
```

### Answers

1. **Thread vs Runnable:**
   > In Java, `Thread` is a class that represents a thread of execution, while `Runnable` is a functional interface defining a `run()` method. Implementing `Runnable` separates the task logic from the execution thread, allowing the class to inherit from other classes (since Java has single inheritance).

2. **Pool size limiting concurrency:**
   > An `ExecutorService` thread pool restricts the maximum number of threads running concurrently. If the pool size is 2, only 2 tasks can run in parallel; others wait in a queue.

3. **thread.join() in Java:**
   > It blocks the current calling thread until the target thread finishes execution, similar to POSIX `pthread_join()`.

4. **ExecutorService advantages:**
   > It manages a pool of worker threads, reducing the overhead of thread creation and deletion, controls concurrency limits, and decouples task submission from execution details.

---

## Task 4: Observing Threads

### Linux — `ps -eLf` Output

```text
UID          PID    PPID     LWP  C NLWP AMASK TTY          TIME CMD
both        4732    4525    4732  1   14     - pts/0    00:00:01 java ThreadDemo
both        4732    4525    4733  0   14     - pts/0    00:00:00 java ThreadDemo
both        4732    4525    4734  0   14     - pts/0    00:00:00 java ThreadDemo
both        4732    4525    4735  0   14     - pts/0    00:00:00 java ThreadDemo
both        4732    4525    4736  0   14     - pts/0    00:00:00 java ThreadDemo
both        4732    4525    4737  0   14     - pts/0    00:00:00 java ThreadDemo
both        4732    4525    4738  0   14     - pts/0    00:00:00 java ThreadDemo
both        4732    4525    4739  0   14     - pts/0    00:00:00 java ThreadDemo
both        4732    4525    4740  0   14     - pts/0    00:00:00 java ThreadDemo
both        4732    4525    4741  0   14     - pts/0    00:00:00 java ThreadDemo
both        4732    4525    4742  0   14     - pts/0    00:00:00 java ThreadDemo
both        4732    4525    4743  0   14     - pts/0    00:00:00 java ThreadDemo
both        4732    4525    4744  0   14     - pts/0    00:00:00 java ThreadDemo
both        4732    4525    4745  0   14     - pts/0    00:00:00 java ThreadDemo
```

### Linux — htop Thread View

![htop threads](screenshots/task4_htop_threads.png)

### Windows — Task Manager

![Task Manager threads](screenshots/task4_taskmanager_threads.png)

### Answers

1. **LWP column meaning:**
   > LWP stands for Lightweight Process, which represents the kernel-level thread identifier (TID) in Linux's 1:1 thread mapping model.

2. **/proc/PID/task/ count:**
   > The number of subdirectories in `/proc/PID/task/` corresponds exactly to the number of active threads (LWPs) belonging to that process.

3. **Extra Java threads:**
   > Java processes run extra system threads for garbage collection (GC), HotSpot JIT compilation, finalization, and signal handling.

4. **Linux vs Windows thread viewing:**
   > Linux uses tools like `ps -eLf`, `htop`, or `/proc/PID/task` to view threads by PID/LWP. Windows uses Task Manager (with the "Threads" column enabled in Details), Process Explorer, or Resource Monitor.

---

## Reflection

> This activity taught me the foundational mechanics of concurrent execution and networking. Working with TCP sockets in C highlighted the details of network connection setups and port mappings, while C and Java multithreading illustrated the critical need for thread synchronization to avoid race conditions. Understanding these primitives at the OS level is key to designing high-performance concurrent applications like web servers and database systems.
