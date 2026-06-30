# OS Lab Final Exam — Linux Systems Engineering (Practical)

| | |
|---|---|
| **Course** | Operating Systems |
| **Exam Title** | Practical Final — Scripting, Threads, Permissions, Concurrency & Automation |
| **Chapters** | Labs 5–10 (Threads & Signals, Linux Security, Bash Automation, Race Conditions & Locking, Backups & cron) |
| **Duration** | **2 Hours** |
| **Exam Type** | **Individual** — no pair work, no shared deliverables |
| **Environment** | The shared Linux server, your assigned student account only |
| **Submission** | **git push** of your course repo (labs + activities + this exam) |
| **Total Marks** | 100 |

---

## ⚠️ READ THIS ENTIRE DOCUMENT BEFORE YOU TOUCH THE KEYBOARD

You have **2 hours**. The exam is mostly about **writing and running scripts** and
proving you understand the commands behind them. Read the whole paper, plan, then
work. This paper gives **instructions and outcomes only** — not the commands, flags,
or scripts. Choosing the right tool for each task is exactly what is graded.

---

## Exam Rules

1. **Server only.** All work on the shared Linux server, your own account.
2. **No `sudo`.** Every task is scoped to your own files and processes.
3. **Script naming.** Every script is **lowercase, `snake_case`, NO extension**
   (no `.sh`). A script named `do_thing.sh` scores zero for that deliverable.
4. **Individual work.** No partner tasks. Sharing answers/files is misconduct.
5. **AI tools.** Allowed, but: record the **exact commands you ran** per part in
   `commands.md`; several questions need **your own scenario values / output**;
   generic answers that ignore your numbers earn nothing. You must be able to
   explain any line you submit.
6. **Submission is by git.** Your work lives **inside your existing course repo**
   and is delivered by committing and pushing — the same repo that holds your
   previous labs and activities (see Submission).
7. **Time-boxing.** If something stalls, record what you saw, move on, return later.

---

## Scenario

**Your scenario for this exam** — memorise these; every part refers to them:

| Item | Your value |
|------|-----------|
| Company | QuantumTech |
| Product (your purchase script) | Reactor Core  →  `buy_reactor_core` |
| Initial stock | 80 |
| Concurrent buyers (swarm) | 40 |
| Worker threads | 5 |
| Backups to retain | 3 |
| Cron test interval | every 1 minute |
| One-shot timed job fires at | 14:35 today |
| Your student ID | p20240038 |

**Narrative:** You are the on-call systems engineer at **`QuantumTech`**. A launch of
the limited-run **`Reactor Core`** is hours away. Overnight the platform team left a
punch-list: prove the backend can run multithreaded and shut down cleanly, lock down
the files, build the operator scripts, harden the purchase script against concurrent
abuse, and stand up an automated backup pipeline. Each part is one punch-list item.

---

## Naming, Folders & Submission

Your exam work lives **inside your existing course git repository** — the same repo
that already holds your labs and activities — in a new top-level folder:

```text
~/os-se-p20240038/final-exam/
```

Runnable scripts live in `~/bin/` (as in the labs), but a **copy** of every script
you write must be committed under the matching `partX/scripts/` folder. Use this
exact tree; only `p20240038` changes.

```text
~/os-se-p20240038/
├── os-lab-p20240038/        ← your existing labs (already committed)
├── os-class-activities-p20240038/   ← your existing activities
└── final-exam/                    ← NEW — this exam
    ├── README.md                  ← cover sheet + all written answers
    ├── commands.md                ← exact commands you ran, per part (A–E)
    ├── live_mods.md               ← answers to the live curveballs
    ├── partA_threads/
    │   ├── thread_demo.c
    │   ├── signal_demo.c
    │   ├── thread_map.txt
    │   └── images/
    │       ├── a1_thread_run.png
    │       ├── a2_signal_catch.png
    │       └── live_a.png
    ├── partB_security/
    │   ├── perm_report.txt
    │   ├── setuid_demo.c
    │   └── images/
    │       └── b1_special_bits.png
    ├── partC_scripting/
    │   ├── scripts/
    │   │   ├── greeter             ← no extension
    │   │   └── collector           ← no extension
    │   ├── path_report.txt
    │   └── images/
    │       └── c1_collector_run.png
    ├── partD_secure/
    │   ├── scripts/
    │   │   ├── buy_reactor_core ← purchase script, no extension
    │   │   └── swarm               ← concurrency driver, no extension
    │   ├── observations.txt
    │   └── images/
    │       ├── d2_patched.png
    │       └── live_d.png
    └── partE_automation/
        ├── scripts/
        │   ├── backup_project      ← no extension
        │   ├── timed_job           ← no extension
        │   └── backup_exam         ← no extension (tars the final-exam folder)
        ├── logs/
        │   ├── cron_recurring.log  ← output of the every-1-minute job
        │   └── cron_oneshot.log    ← output of the one-shot 14:35 job
        ├── cron_report.txt
        └── images/
            ├── e1_backup_retention.png
            └── live_e.png
```

> **`commands.md`**: one section per part (`## Part A` … `## Part E`); paste the
> exact commands you ran, in order, in fenced code blocks. Graded for command
> competency and is your defence if output is questioned.

> **Starter templates (zip).** A `final-exam-templates.zip` sits **next to this paper**
> in `~/exam-paper/`. Unzip it into your `final-exam/` folder to get `README.md`,
> `commands.md` and `live_mods.md` skeletons (structure + the questions only — no
> answers, no commands), then fill them in:
> `cd ~/os-se-p20240038/final-exam && unzip ~/exam-paper/final-exam-templates.zip`

### Submission (git)

Your exam is submitted by **committing and pushing your course repo** — the same one
you used all semester:

```text
cd ~/os-se-p20240038
git add .
git commit -m "Final exam"
git push
```

Pushing carries your `final-exam/` folder **together with** your existing labs and
activities. The grader reads your pushed repo; commit timestamps establish when each
file was submitted, so **commit before the deadline**. A part that is not committed
and pushed does not exist for grading.

---

## Live Modification Tasks (issued by the invigilator during the exam)

> **Read now, but you cannot start these yet.** They are intentionally withheld.

A portion of **Parts A, D and E's** marks is reserved for a **live curveball**. There
are **three in total — one each for Parts A, D and E** — released together at a single
announced checkpoint late in the exam, and not printed in this paper. Each curveball
**edits an artefact you already built** and depends on **state that only exists on your
machine at that moment** (your PIDs, your current stock value, your scripts). They
cannot be pre-generated.

**How they work:**

1. All three are released **together, once**, at a single announced checkpoint **late
   in the exam** — not at the start.
2. You get **one combined time-box** (typically ~20 minutes) for all three.
3. Each builds **on top of your existing artefact** — do not start over.
4. Record in `live_mods.md`, per curveball: (a) the announced instruction, (b) the
   exact command(s) you ran, (c) the **live value(s)** it acted on (your PID / stock
   / timestamp), and (d) a screenshot `live_<letter>.png` in that part's `images/`.
5. An answer that doesn't reference **your own** live values, or that could have been
   written before the announcement, scores zero for that curveball.

---

## Part A — Threads, Kernel Mapping & Signals  *(Lab 5)*  — 18 marks

**Goal:** make a program concurrent, observe the 1:1 user→kernel thread mapping, and
handle a signal for graceful shutdown.

1. **A1 — Concurrency.** Write `thread_demo.c` spawning exactly `5`
   worker threads; each prints its thread id and a computed value, and the main
   thread joins all and collects a result from each before a final summary. Compile
   (mind the threading flag) and run.
   - 📸 `a1_thread_run.png` — output showing all `5` workers + summary.
2. **A2 — Mapping.** While a multithreaded process of yours is alive, capture evidence
   that each user thread is backed by one kernel-scheduled entity (LWP) into
   `thread_map.txt` (shared PID, distinct per-thread ids).
3. **A3 — Signals.** Write `signal_demo.c` that installs handlers for an interactive
   interrupt and a polite termination signal, loops, and on either prints a cleanup
   message and exits cleanly. Demonstrate catching the interactive interrupt.
   - 📸 `a2_signal_catch.png` — show **the commands you ran and the live interaction**:
     compiling/running the program, the loop, the signal you sent, and the cleanup
     message + clean exit (a sample of the input→output exchange).

**Written (one short answer):** Why does a worker thread's joined result reach the main
thread, but a forked child's value would not?

---

## Part B — Files, Permissions & Special Bits  *(Lab 6)*  — 18 marks

**Goal:** fine-grained access control entirely within files/directories you own — no
account creation, no `sudo`.

1. **B1 — Permission story.** Create a small tree with a shared sub-directory and a
   private file. Using octal and symbolic notation, make the private file
   owner-read/write only, and the shared dir traversable but not listable by others.
   Record `ls -l`/`ls -ld` + a `stat` into `perm_report.txt`.
2. **B2 — Special bits.** On a directory you own, demonstrate **setgid** (group
   inheritance) and the **sticky bit** (only owner deletes own file). Compile
   `setuid_demo.c` (prints real + effective uid), set the setuid bit, and explain why
   it does not escalate to root here.
   - 📸 `b1_special_bits.png` — the `s`/`t` bits and the setuid binary's id output.
**Written (one short answer):** Translate your private file's final octal mode into the
nine-character symbolic string (e.g. `600` → `rw-------`).

---

## Part C — Bash Scripting, PATH & Safe File Scanning  *(Lab 7)*  — 22 marks

**Goal:** write real scripts (no extensions), make them first-class commands via
`PATH`, and scan the filesystem safely.

1. **C1 — `greeter`.** A script that prints a dynamic banner: current user, number of
   logged-in users, uptime, and one random line from an array of ≥3 messages. Must
   use a variable, an array, and command substitution. Make it run **by name** (not
   `./greeter`) via your `PATH`; record `PATH` + the resolved location into
   `path_report.txt`.
2. **C2 — `collector`.** A script that loops over a set of directories you create,
   tests each candidate file for existence and readability **before** touching it,
   reads the readable ones, and writes a consolidated report — **skipping** unreadable
   or missing files without crashing.
   - 📸 `c1_collector_run.png` — `collector` running and the report it produced.

**Written (one short answer):** Why did `greeter` fail to run by name before you added
your `bin` directory to `PATH`?

---

## Part D — Concurrency, a Race Condition & File Locking  *(Lab 8)*  — 20 marks

**Goal:** reproduce a Time-of-Check-to-Time-of-Use race on a shared counter, then
patch it with one OS-level lock. You play the "botnet" yourself — no partner, no
deadlock theory, just the race and its fix.

1. **D1 — The engine.** Write `buy_reactor_core`: takes a buyer name and a
   positive-integer quantity, rejects bad input, reads a stock file initialised to
   `80`, and on success decrements stock and appends a sales-log line
   that includes **your real student id**.
2. **D2 — The race.** Write `swarm`: launches `40` concurrent purchases in
   the background and waits. Reset stock to `80`, run several times, and
   record the **final stock after each run** in `observations.txt`. Unpatched runs
   *should* be inconsistent; if your environment makes the race hard to trigger, say so
   in `observations.txt` — **grading rewards the correct lock in D3**, not luck in
   reproducing the race.
3. **D3 — The patch.** Wrap the read-modify-write critical section in a single
   exclusive advisory lock so only one process mutates stock at a time. Anchor file
   paths so it works from any directory. Re-run the swarm and show the stock lands at
   the correct value every time.
   - 📸 `d2_patched.png` — the swarm ending at the deterministic correct stock.

**Written (one short answer):** Why did the unpatched `swarm` sometimes leave **more**
stock than the correct final value (with `80` stock and `40`
concurrent buyers)?

---

## Part E — Backups, Archiving & cron Automation  *(Lab 10)*  — 22 marks

**Goal:** build a retention-aware backup script, run scheduled jobs unattended via your
**per-user** crontab (no `sudo`, no `/etc/cron*`), and schedule a `tar` backup of your
exam folder.

1. **E1 — `backup_project`.** Create a small sample project (a few files in a couple
   of sub-folders). Write `backup_project` to produce a **timestamped, compressed**
   archive on each run and keep only the newest `3` archives, pruning the
   rest. Run it enough times that pruning happens.
   - 📸 `e1_backup_retention.png` — exactly `3` archives left + the log.
2. **E2 — Scheduled job (two entries → two separate log files).** Write `timed_job`: it
   appends a clearly identifiable timestamped line to the output file it is given. In
   your **per-user crontab**, add two entries using **separate, clearly-named log files
   under `partE_automation/logs/`** (absolute paths):
   - a **recurring** entry firing `every 1 minute` (test interval) → output to
     **`cron_recurring.log`**;
   - a **one-shot** entry set for **`14:35` today** → output to
     **`cron_oneshot.log`**.
   Wait for both to fire; capture `crontab -l` plus both log files into `cron_report.txt`.
3. **E3 — Scheduled backup of your exam folder.** Write `backup_exam`: it uses `tar` to
   make a **timestamped, compressed** archive of your whole **`final-exam/`** folder.
   Write the archives to a clearly-defined location **outside** the repo so the backup
   never tars itself — exactly: **`~/exam-backups/final-exam-<timestamp>.tar.gz`**
   (create `~/exam-backups/` if it doesn't exist). Add per-user crontab entries that run
   `backup_exam` **on a short interval** *and* **once at exactly `16:00` (4:00 PM)
   today**. Include those crontab lines and a listing of `~/exam-backups/` in
   `cron_report.txt`.

**Written (one short answer):** Archiving vs compression — which one actually shrank the
bytes, and why?

---

## `README.md` Cover Sheet (required first section)

Begin `README.md` with exactly this header, filled in:

```text
Student name:
Student ID:
Server username:
Exam scenario value (COMPANY / PRODUCT):
Date & start time:
AI assistant used (name/none):
```

Then, per part (A–E): embed each required screenshot, give your written answers, and
note anything you could not complete.

---

## Grading Rubric (100 marks)

| Part | Topic | Marks |
|:----:|-------|:-----:|
| A | Threads, kernel mapping, signals | 18 |
| B | Permissions & special bits | 18 |
| C | Bash scripting, PATH, safe file scanning | 22 |
| D | Race condition + `flock` patch | 20 |
| E | Backup retention + per-user cron | 22 |
| **Total** | | **100** |

Within each part: **working artefact / correct behaviour**, **evidence — screenshots +
reports**, and **written understanding tied to your own scenario values**. **Parts A, D
and E** additionally carry a **live curveball (~15% of that part)**; **B and C have no
curveball**, so those marks fall on artefact, evidence and written understanding. The
live curveball marks are real-time only. A forbidden extension, any use of `sudo`, or
answers that ignore your specific scenario numbers lose the relevant marks.

---

## Integrity & Command-Competency Note

This paper withholds commands on purpose. You may consult an AI assistant, but the
grade rests on you proving — through `commands.md`, scenario-specific answers, and
correct, safe choices — that **you** know which tool, flag, and permission does what.
Be ready to explain any line you submit.