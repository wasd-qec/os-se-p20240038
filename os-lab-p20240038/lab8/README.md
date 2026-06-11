# OS Lab 8 Submission - The Quantum Widget Exploit

- **Student Name:** Se Rith Chankolboth
- **Student ID:** p20240038
- **Partner Username:** se-chea-seavhong

---

## Task Output Files

Make sure all of the following files are present in your `lab8/` folder:

- [x] `observations.txt`
- [x] `task0_warmup.txt`
- [x] `task1_validation.txt`
- [x] `task2_audit.txt`
- [x] `task4_mutex.txt`
- [x] `task5_red_blue.txt`
- [x] `task6_dropzone.txt`
- [x] `task7_cleanup.txt`
- [x] `scripts/arg_viewer`
- [x] `scripts/quantum_probe`
- [x] `scripts/buy_widget`
- [x] `scripts/bot_swarm`
- [x] `scripts/create_dropzone`
- [x] `scripts/cleanup`

---

## Screenshots

Insert your screenshots below.

### Screenshot 1 - Level 0: Bash Warm-Up Scripts
Show `arg_viewer` explaining `$0`, `$1`, `$2`, `$#`, and `$?`, then show `quantum_probe` using a condition and a loop.

![warmup](images/level0_warmup.png)

---

### Screenshot 2 - Level 2: Audit Trails
Show input validation, a successful sale, failed transactions, final inventory, and `sales.log`.

![audit trails](images/level2_audit.png)

---

### Screenshot 3 - Level 4: Mutex Patch
Show `inventory.txt` exactly `0` after the patched `bot_swarm`, plus the last five lines of `sales.log`.

![mutex patch](images/level4_mutex.png)

---

### Screenshot 4 - Level 5: Red Team vs. Blue Team
Show `public_api` permissions, inventory, and sales log evidence that your classmate executed your API.

![red blue](images/level5_red_blue.png)

---

### Screenshot 5 - Level 6: Secure Drop Zone
Show the sticky bit in `ls -ld` output and evidence that your partner could not delete your file.

![drop zone](images/level6_dropzone.png)

---

### Screenshot 6 - Level 7: Forensic Cleanup
Show `tree` or `ls -R` output proving `.log`, `.csv`, and `.tmp` files were sorted into folders.

![cleanup](images/level7_cleanup.png)

---

## Race Condition Observations

Summarize your five vulnerable `bot_swarm` runs from `observations.txt`:

| Run | Final Inventory | Notes |
|:---:|----------------:|-------|
| 1 | 16 | Overlapping updates led to 16 remaining widgets. |
| 2 | 24 | High lock contention simulation showed 24 remaining widgets. |
| 3 | 12 | Only 88% of requested purchases were processed due to overlapping check-to-use times. |
| 4 | 20 | Scheduled context switches lost 10 update steps. |
| 5 | 18 | Race condition caused 18 units to remain unsold. |

---

## Answers to Lab Questions

1. **In `arg_viewer`, what did `$0`, `$1`, `$2`, `$#`, and `$?` mean when you ran the script?**
   > * `$0` is the script's filename/path (`arg_viewer`).
   > * `$1` is the first command-line argument passed (e.g., `Alice`).
   > * `$2` is the second command-line argument passed (e.g., `3`).
   > * `$#` is the total count of arguments passed (e.g., `2` or `0`).
   > * `$?` is the exit code of the most recently executed command (`0` for success, non-zero for failure).

2. **What does TOC-TOU mean, and where did it appear in the vulnerable `buy_widget` script?**
   > TOC-TOU stands for **Time-of-Check to Time-of-Use**. In the vulnerable script, it appeared between checking the inventory level in `inventory.txt` and updating the file. Because multiple processes ran in the background concurrently, one process read the check before another wrote the updated use, causing lost sales count updates.

3. **Why did `bot_swarm` sometimes leave inventory values other than `0` before the patch?**
   > Since the processes executed in parallel without synchronization, multiple instances read the same inventory level (e.g., `100`) before any wrote their new calculations. Consequently, both wrote back `98`, wasting one of the sales updates and leaving inventory unsold at the end of the swarm.

4. **What part of the script is the critical section, and why must it be protected?**
   > The critical section is the sequence where the shared inventory file is read, verified, updated, and logged. It must be protected to ensure data consistency and prevent concurrent writes from corrupting the file or losing transaction updates.

5. **How does `flock -x` enforce mutual exclusion between concurrent processes?**
   > It obtains an exclusive advisory lock on a dedicated lock file descriptor. If another process holds the lock, any subsequent process attempting to lock it blocks and waits until the lock is released, allowing only one process inside the critical section at a time.

6. **Which permissions did you use to let a classmate run your API without giving full access to your home directory?**
   > * `chmod o+x "$HOME"` to allow traversal to the directory.
   > * `chmod 755 ~/public_api` to list and enter the folder.
   > * `chmod o+rx ~/public_api/buy_widget` to execute the script.
   > * `chmod o+rw ~/public_api/inventory.txt ~/public_api/sales.log ~/public_api/inventory.lock` to modify the files during run.

7. **Why does the sticky bit protect files in a shared drop zone?**
   > The sticky bit (`+t`) ensures that only the file's owner (or the directory owner / root) can rename or delete files within that directory, even if other users have write access (`+w`) to the directory.

8. **What defensive scripting practice from this lab would you use in a real production script?**
   > I would use strict regular expression input validation (e.g., `[[ "$cycles" =~ ^[1-9][0-9]*$ ]]`), absolute script path anchoring (`"$(dirname "${BASH_SOURCE[0]}")"`), and file locking using `flock` to handle concurrency safely.

---

## Reflection

> This lab taught me how easy it is to introduce race conditions (TOC-TOU) in Unix/Linux scripts when working with shared file updates. I learned how file locking (`flock`) enforces mutual exclusion, how home folder and sticky-bit permissions apply the principle of least privilege in multi-user Linux environments, and how to script validations defensively.
