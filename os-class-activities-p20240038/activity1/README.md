# Class Activity 1 — System Calls in Practice

- **Student Name:** Rith Chankolboth
- **Student ID:** p20240038
- **Date:** [Date of Submission]

---

## Warm-Up: Hello System Call

Screenshot of running `hello_syscall.c` on Linux:

![Hello syscall](screenshots/hello_syscall.png)

Screenshot of running `hello_winapi.c` on Windows (CMD/PowerShell/VS Code):

![Hello WinAPI](screenshots/hello_winapi.png)

Screenshot of running `copyfilesyscall.c` on Linux:

![Copy file syscall](screenshots/copyfilesyscall.png)

---

## Task 1: File Creator & Reader

### Part A — File Creator

**Describe your implementation:** [What differences did you notice between the library version and the system call version?]

**Version A — Library Functions (`file_creator_lib.c`):**

<!-- Screenshot: gcc -o file_creator_lib file_creator_lib.c && ./file_creator_lib && cat output.txt -->
![Task 1A - Library](screenshots/task1_creator_lib.png)

**Version B — POSIX System Calls (`file_creator_sys.c`):**

<!-- Screenshot: gcc -o file_creator_sys file_creator_sys.c && ./file_creator_sys && cat output.txt -->
![Task 1A - Syscall](screenshots/task1_creator_sys.png)

**Questions:**

1. **What flags did you pass to `open()`? What does each flag mean?**

   > O_WRONLY (write), O_CREAT (create if missing), O_TRUNC (erase existing content)

2. **What is `0644`? What does each digit represent?**

   > `0644` is the file permission in octal notation: first digit (0) = file type/special bits; second digit (6) = owner permissions (4=read + 2=write); third digit (4) = group permissions (read-only); fourth digit (4) = other/world permissions (read-only). So the file is readable and writable by the owner, readable by group and others.

3. **What does `fopen("output.txt", "w")` do internally that you had to do manually?**

   > `fopen()` internally calls `open()` with O_WRONLY | O_CREAT | O_TRUNC flags and file permission mode (typically 0666 masked by umask), returns a FILE pointer with buffering. In the syscall version, you manually call `open()`, write data with `write()`, and `close()` the file descriptor. `fopen()` abstracts these details and adds buffering via a stream interface.

### Part B — File Reader & Display

**Describe your implementation:** [ I use the `open()` function and store it directory as destination and pass it into read with read only permission from the output.txt. And then i create a buffer variable size of]

**Version A — Library Functions (`file_reader_lib.c`):**

![Task 1B - Library](screenshots/task1_reader_lib.png)

**Version B — POSIX System Calls (`file_reader_sys.c`):**

![Task 1B - Syscall](screenshots/task1_reader_sys.png)

**Questions:**

1. **What does `read()` return? How is this different from `fgets()`?**

   > [It returns the number of bytes read, it return 0 when it reach end of file but -1 if an error occure]

2. **Why do you need a loop when using `read()`? When does it stop?**

   > [the byte buffer is reading 256 bytes per loop iteration, so it is reading until the end of file]

---

## Task 2: Directory Listing & File Info

**Describe your implementation:** [Your notes]

### Version A — Library Functions (`dir_list_lib.c`)

![Task 2 - Version A](screenshots/task2_lib.png)

### Version B — System Calls (`dir_list_sys.c`)

![Task 2 - Version B](screenshots/task2_sys.png)

### Questions

1. **What struct does `readdir()` return? What fields does it contain?**

   > [return a pointer to struct dir entry which contains: d_ino, d_off, d_reclen, d_type, d_name\[\]]

2. **What information does `stat()` provide beyond file size?**

   > [return struct stat with data: permission, inode number, device id, hard link count, owner UID and GID, file size in bytes, block size and count, access time modifier, change time]

3. **Why can't you `write()` a number directly — why do you need `snprintf()` first?**

   > [write operates on raw bytes that's why we need snprintf to convert numbers to its string representation]

---

## Optional Bonus: Windows API (`file_creator_win.c`)

Screenshot of running on Windows:

![Task 1 - Windows](screenshots/task1_win.png)

### Bonus Questions

1. **Why does Windows use `HANDLE` instead of integer file descriptors?**

   > Windows uses `HANDLE` objects, which are opaque pointers or indexes to entries in a process-specific kernel handle table, because the Windows kernel uses an object-oriented architecture where objects (processes, threads, events, files) are managed in kernel memory and protected from direct user-space manipulation. Integer file descriptors in Unix/POSIX are indexes into a file descriptor table representing only files and sockets, whereas Windows handles represent a broader variety of system objects with fine-grained access security attributes.

2. **What is the Windows equivalent of POSIX `fork()`? Why is it different?**

   > The closest Windows equivalent is `CreateProcess()`. It is different because `fork()` duplicates the parent process's entire memory space, file descriptor table, and context to create a child process executing from the same instruction point, whereas `CreateProcess()` creates a new, independent process from scratch by loading a specific executable binary, setting up a fresh virtual address space, and running from its main entry point.

3. **Can you use POSIX calls on Windows?**

   > Yes, POSIX calls can be used on Windows through compatibility layers or libraries. Historically, Windows included a POSIX subsystem. Today, developers use subsystems like WSL (Windows Subsystem for Linux), compilation suites like MinGW/MSYS2, or POSIX-emulation runtime layers like Cygwin which translate POSIX calls into Windows API calls.

---

## Task 3: strace Analysis

**Describe what you observed:** [Why is the sys call slower than the library]

### strace Output — Library Version (File Creator)

<!-- Screenshot: strace -e trace=openat,read,write,close ./file_creator_lib -->
<!-- IMPORTANT: Highlight/annotate the key system calls in your screenshot -->
![strace - Library version File Creator](screenshots/strace_lib_creator.png)

### strace Output — System Call Version (File Creator)

<!-- Screenshot: strace -e trace=openat,read,write,close ./file_creator_sys -->
<!-- IMPORTANT: Highlight/annotate the key system calls in your screenshot -->
![strace - System call version File Creator](screenshots/strace_sys_creator.png)

### strace Output — Library Version (File Reader or Dir Listing)

![strace - Library version](screenshots/strace_lib_reader.png)

### strace Output — System Call Version (File Reader or Dir Listing)

![strace - System call version](screenshots/strace_sys_reader.png)

### strace -c Summary Comparison

<!-- Screenshot of `strace -c` output for both versions -->
![strace summary - Library](screenshots/strace_summary_lib.png)
![strace summary - Syscall](screenshots/strace_summary_sys.png)

### Questions

1. **How many system calls does the library version make compared to the system call version?**

   > The library version makes slightly more system calls during startup and initialization (e.g. 38 vs 33 calls in this setup). However, for I/O operations specifically, the library version uses buffered writing which batches multiple standard outputs into fewer system level write calls, while the direct syscall version issues a system call for every single write command.

2. **What extra system calls appear in the library version? What do they do?**

   > System calls like `brk` and `mmap` appear in the library version. `brk` is used to change the data segment size (allocating heap memory for stdio buffers), and `mmap` maps files or devices into memory (often used to map the C standard library `libc.so` or establish buffered allocation zones). `fstat` retrieves file status information to check buffer settings.

3. **How many `write()` calls does `fprintf()` actually produce?**

   > Because of library-level buffering (standard I/O buffering), `fprintf()` batches the data in user-space buffers and only calls `write()` when the buffer is full, when `fflush()` is called, or when the file is closed. In this scenario, it produces exactly **1** system `write()` call for the entire batch of outputs.

4. **In your own words, what is the real difference between a library function and a system call?**

   > A library function (like `printf()` or `fopen()`) runs entirely in user space, offering convenience wrappers, abstractions, and performance optimizations (like buffering). A system call (like `write()` or `open()`) is an entry point into the operating system kernel, requiring a context switch (CPU privilege mode change from user mode to kernel mode) to perform hardware or OS-level operations.

---

## Task 4: Exploring OS Structure

### System Information

> 📸 Screenshot of `uname -a`, `/proc/cpuinfo`, `/proc/meminfo`, `/proc/version`, `/proc/uptime`:

![System Info](screenshots/task4_system_info.png)

### Process Information

> 📸 Screenshot of `/proc/self/status`, `/proc/self/maps`, `ps aux`:

![Process Info](screenshots/task4_process_info.png)

### Kernel Modules

> 📸 Screenshot of `lsmod` and `modinfo`:

![Kernel Modules](screenshots/task4_modules.png)

### OS Layers Diagram

> 📸 Your diagram of the OS layers, labeled with real data from your system:

![OS Layers Diagram](screenshots/task4_os_layers_diagram.png)

### Questions

1. **What is `/proc`? Is it a real filesystem on disk?**

   > `/proc` is a virtual filesystem (known as procfs) that does not exist on disk. It is generated dynamically by the Linux kernel in memory to expose kernel data structures, system information (like cpuinfo, meminfo), and process state in a structured filesystem layout.

2. **Monolithic kernel vs. microkernel — which type does Linux use?**

   > Linux uses a monolithic kernel architecture. This means all operating system services (VFS, virtual memory, scheduler, device drivers) run within the same kernel space and share the same address space for maximum performance, although Linux is modular and allows loading kernel modules dynamically.

3. **What memory regions do you see in `/proc/self/maps`?**

   > You see memory regions representing the text segment (code), data segment (initialized/uninitialized variables), heap (dynamic allocation), shared libraries (`.so` files), stack (local variables and thread stacks), and virtual mapping regions (like `vvar`, `vdso`, `vsyscall`).

4. **Break down the kernel version string from `uname -a`.**

   > For a kernel version string like `6.8.0-31-generic`: `6` is the major version, `8` is the minor version, `0` is the patch level, `31` is the ABI/distribution release number, and `generic` indicates the kernel configuration profile for general-purpose hardware.

5. **How does `/proc` show that the OS is an intermediary between programs and hardware?**

   > It provides user-space programs with a read/write file interface to hardware-specific details (like `/proc/cpuinfo` or `/proc/meminfo`) and process runtime structures. Programs read `/proc` files to learn about CPU or memory state without directly interacting with system hardware buses.

---

## Reflection

What did you learn from this activity? What was the most surprising difference between library functions and system calls?

> This activity clearly demonstrated the performance differences between direct system calls and library functions. I learned that calling a system call for every tiny byte of input or output creates substantial context-switching overhead because the CPU has to switch from user mode to kernel mode repeatedly. Library functions like `fprintf` optimize this by buffering the data in user space and writing it in batches, which dramatically increases execution speed. It was surprising to see how `/proc` exposes the inner workings of the operating system as virtual files.
