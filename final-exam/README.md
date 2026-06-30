# Final Exam — Rith Chankolboth

<!-- ===== COVER SHEET — required first section. Fill EVERY line. ===== -->
```
Student name: Rith Chankolboth
Student ID: p20240038
Server username: se-rith-chankolboth
Exam scenario value (COMPANY / PRODUCT): QuantumTech / Reactor Core
Date & start time: June 30, 2026 at 2:00 PM
AI assistant used (name/none): Antigravity
```

> Exact commands per part are in `commands.md`. Live-curveball answers are in `live_mods.md`.

---

## Part A — Threads, Kernel Mapping & Signals

**Screenshots**

![A1 — all workers + summary](partA_threads/images/a1_thread_run.png)
![A2 — signal_demo: commands + interaction (signal sent, cleanup msg, clean exit)](partA_threads/images/a2_signal_catch.png)

**Written (one short answer)**

- **Why does a worker thread's joined result reach the main thread, but a forked child's value would not?**
  Threads share the same virtual address space (including the heap and global variables) of the parent process. When a thread exits, its return value is stored in shared memory where the main thread can access it upon joining. In contrast, `fork()` spawns a separate child process with its own copy of the address space. Any modifications made by the child occur in its independent memory space, meaning its values cannot reach the parent process directly without explicit Inter-Process Communication (IPC).

**Anything not completed:** none

---

## Part B — Files, Permissions & Special Bits

**Screenshot**

![B1 — setuid / setgid / sticky bits + id output](partB_security/images/b1_special_bits.png)

**Written (one short answer)**

- **Translate your private file's final octal mode into the 9-char symbolic string**
  `rw-------` (octal `600` for owner read/write only)

**Anything not completed:** none

---

## Part C — Bash Scripting, PATH & Safe File Scanning

**Screenshot**

![C1 — collector run + consolidated report](partC_scripting/images/c1_collector_run.png)

**Written (one short answer)**

- **Why did `greeter` fail to run by name before you added your `bin` directory to PATH?**
  When a command is run by name (without an absolute or relative path prefix), the shell searches for the corresponding executable file only in the directories specified by the `$PATH` environment variable. Before `~/bin` was added to `$PATH`, the shell could not locate the script, resulting in a "command not found" error.

**Anything not completed:** none

---

## Part D — Concurrency, a Race Condition & File Locking

**Screenshot**

![D2 — swarm lands at the correct stock after the lock](partD_secure/images/d2_patched.png)

**Written (one short answer)**

- **Why did the unpatched `swarm` sometimes leave more stock than the correct final value (with `80` stock and `40` concurrent buyers)?**
  Because of the lost-update race condition. Without locking, multiple processes concurrently executed the read-modify-write sequence. Several processes read the same initial/current stock value before any of them wrote the updated decremented stock. As a result, their writes overwrote each other, resulting in fewer effective decrements than the actual number of purchases made.

**Anything not completed:** none

---

## Part E — Backups, Archiving & cron Automation

**Screenshot**

![E1 — only RETAIN_N archives remain + log](partE_automation/images/e1_backup_retention.png)

**Written (one short answer)**

- **Archiving vs compression — which one actually shrank the bytes, and why?**
  Compression (`gzip`) actually shrank the bytes by using compression algorithms to find and remove redundancies in the files. Archiving (`tar`) simply packages/bundles multiple files and directories into a single file container without shrinking it (and actually adds minor metadata/header overhead).

**Anything not completed:** none
