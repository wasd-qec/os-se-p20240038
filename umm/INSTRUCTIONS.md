# QuantumTech Linux Systems Engineering Practical Final Exam Guide

This guide provides step-by-step instructions to compile, run, and verify all tasks of the final exam on the remote server (`ssh2.rathpisey.site`).

---

## 🛠️ Step 0: Remote Server Connection
Log into the remote server via SSH:
```bash
ssh se-rith-chankolboth@ssh2.rathpisey.site
# Password: Password123
```
Ensure your working directory is the cloned repository:
```bash
cd ~/os-se-p20240038
```

---

## 🧵 Part A: Threads, Kernel Mapping & Signals

### 1. Compiling the C Programs
Compile `thread_demo.c` (using the `-pthread` library flag) and `signal_demo.c` directly on the server:
```bash
gcc -pthread -o ~/os-se-p20240038/final-exam/partA_threads/thread_demo ~/os-se-p20240038/final-exam/partA_threads/thread_demo.c
gcc -o ~/os-se-p20240038/final-exam/partA_threads/signal_demo ~/os-se-p20240038/final-exam/partA_threads/signal_demo.c
```

### 2. Capturing Thread Mapping (LWP)
To verify the 1:1 user-to-kernel thread mapping (LWP), run the compiled binary in the background and check its threads using `ps -T -p` within the 10-second sleep window:
```bash
# Run in background
~/os-se-p20240038/final-exam/partA_threads/thread_demo &
DEMO_PID=$!

# Wait 2 seconds for threads to spawn, then capture thread list
sleep 2
ps -T -p $DEMO_PID > ~/os-se-p20240038/final-exam/partA_threads/thread_map.txt

# Wait for background task to complete
wait $DEMO_PID
```
Verify the generated `thread_map.txt`:
```bash
cat ~/os-se-p20240038/final-exam/partA_threads/thread_map.txt
```
*(You should see 1 main thread + 5 worker thread entries with distinct SPIDs).*

### 3. Demonstrating Signal Catching
Run `signal_demo` interactively:
```bash
~/os-se-p20240038/final-exam/partA_threads/signal_demo
```
While running, press `Ctrl+C` in your terminal. You should see the handler catch `SIGINT`, print a cleanup message, and exit cleanly. Take a terminal screenshot of this interaction for `a2_signal_catch.png`.

---

## 🔒 Part B: Files, Permissions & Special Bits

### 1. Creating the Tree and Setting Permissions
Create the directory structure and the private file inside the shared directory:
```bash
mkdir -p ~/os-se-p20240038/final-exam/partB_security/shared_dir
touch ~/os-se-p20240038/final-exam/partB_security/shared_dir/private_file
```
Apply the permissions using octal and symbolic modes:
```bash
# Private file: owner read/write only (Octal: 600, Symbolic: rw-------)
chmod 600 ~/os-se-p20240038/final-exam/partB_security/shared_dir/private_file

# Shared directory: traversable (+x) but not listable (-r) by others (Octal: 711, Symbolic: rwx--x--x)
chmod 711 ~/os-se-p20240038/final-exam/partB_security/shared_dir
```
Record the details into `perm_report.txt`:
```bash
ls -ld ~/os-se-p20240038/final-exam/partB_security/shared_dir > ~/os-se-p20240038/final-exam/partB_security/perm_report.txt
ls -l ~/os-se-p20240038/final-exam/partB_security/shared_dir/private_file >> ~/os-se-p20240038/final-exam/partB_security/perm_report.txt
stat ~/os-se-p20240038/final-exam/partB_security/shared_dir >> ~/os-se-p20240038/final-exam/partB_security/perm_report.txt
stat ~/os-se-p20240038/final-exam/partB_security/shared_dir/private_file >> ~/os-se-p20240038/final-exam/partB_security/perm_report.txt
```

### 2. Demonstrating Special Bits
Compile the `setuid_demo.c` file and set the `setuid` bit:
```bash
gcc -o ~/os-se-p20240038/final-exam/partB_security/setuid_demo ~/os-se-p20240038/final-exam/partB_security/setuid_demo.c
chmod u+s ~/os-se-p20240038/final-exam/partB_security/setuid_demo
```
Execute it:
```bash
~/os-se-p20240038/final-exam/partB_security/setuid_demo
```
*(It prints your Real UID and Effective UID. Since it's owned by you and not root, it runs under your permissions and does not escalate privilege to root).*

---

## 🐚 Part C: Bash Scripting & PATH

### 1. First-Class Commands via PATH
Create your personal `~/bin` directory and copy the scripts there, making them executable:
```bash
mkdir -p ~/bin
cp ~/os-se-p20240038/final-exam/partC_scripting/scripts/* ~/bin/
chmod +x ~/bin/*
```
Add `~/bin` to your `PATH` in `~/.bashrc` (if not already present):
```bash
echo 'export PATH="$HOME/bin:$PATH"' >> ~/.bashrc
source ~/.bashrc
```
Confirm `greeter` runs by name, and record PATH information into `path_report.txt`:
```bash
which greeter
greeter
echo "PATH: $PATH" > ~/os-se-p20240038/final-exam/partC_scripting/path_report.txt
which greeter >> ~/os-se-p20240038/final-exam/partC_scripting/path_report.txt
```

### 2. Safe Scanning with `collector`
Create test directories with readable and unreadable files:
```bash
mkdir -p ~/test_collector/dir1 ~/test_collector/dir2
echo "This is file A" > ~/test_collector/dir1/fileA.txt
echo "This is file B" > ~/test_collector/dir2/fileB.txt
touch ~/test_collector/dir1/unreadable.txt && chmod 000 ~/test_collector/dir1/unreadable.txt
```
Run `collector` to scan the test directories and output `consolidated_report.txt`:
```bash
cd ~/os-se-p20240038/final-exam/partC_scripting
collector ~/test_collector/dir1 ~/test_collector/dir2
```
Verify the contents of `consolidated_report.txt` *(it should skip `unreadable.txt` without crashing)*.

---

## 🏎️ Part D: Concurrency, Race Conditions & Locking

### 1. Copying Scripts
Copy scripts to `~/bin`:
```bash
cp ~/os-se-p20240038/final-exam/partD_secure/scripts/* ~/bin/
chmod +x ~/bin/*
```

### 2. Observing the Unpatched Race Condition
Temporarily comment out the locking lines in the compiled `buy_reactor_core` script inside `~/bin`:
```bash
sed -i 's/flock -x 200/# flock -x 200/' ~/bin/buy_reactor_core
sed -i 's/flock -u 200/# flock -u 200/' ~/bin/buy_reactor_core
```
Reset the stock file and run the swarm:
```bash
echo "80" > ~/stock.txt
rm -f ~/sales.log
swarm
```
Check the final stock value:
```bash
cat ~/stock.txt
```
Repeat this multiple times. Record the final stock after each run in `observations.txt` *(due to race conditions, the final stock will be inconsistent, often leaving more than 40 or showing errors)*.

### 3. Verification with locking (flock)
Uncomment the `flock` lines in `~/bin/buy_reactor_core`:
```bash
sed -i 's/# flock -x 200/flock -x 200/' ~/bin/buy_reactor_core
sed -i 's/# flock -u 200/flock -u 200/' ~/bin/buy_reactor_core
```
Reset the stock and run the swarm:
```bash
echo "80" > ~/stock.txt
rm -f ~/sales.log
swarm
```
Check the final stock value:
```bash
cat ~/stock.txt
```
*(The stock should land at exactly `40` every single time without errors).*

---

## 📅 Part E: Backups, Archiving & cron Automation

### 1. Verifying Retention with `backup_project`
Copy the scripts to `~/bin`:
```bash
cp ~/os-se-p20240038/final-exam/partE_automation/scripts/* ~/bin/
chmod +x ~/bin/*
```
Create a test project directory:
```bash
mkdir -p ~/test_project
touch ~/test_project/{file1,file2}.txt
mkdir -p ~/project_backups
```
Run `backup_project` 4 times to verify it prunes older backups:
```bash
backup_project ~/test_project ~/project_backups
sleep 1
backup_project ~/test_project ~/project_backups
sleep 1
backup_project ~/test_project ~/project_backups
sleep 1
backup_project ~/test_project ~/project_backups
```
Listing `~/project_backups` should show exactly **3** newest files:
```bash
ls -la ~/project_backups
```

### 2. Setting Up Crontab Schedules
Edit your user crontab (`crontab -e`) and append the following lines (replace `se-rith-chankolboth` with your exact server username if different):
```text
# E2: Recurring every minute
* * * * * /home/se-rith-chankolboth/bin/timed_job /home/se-rith-chankolboth/os-se-p20240038/final-exam/partE_automation/logs/cron_recurring.log

# E2: One-shot at 14:35 today (June 30)
35 14 30 6 * /home/se-rith-chankolboth/bin/timed_job /home/se-rith-chankolboth/os-se-p20240038/final-exam/partE_automation/logs/cron_oneshot.log

# E3: Backup exam on a short interval (every 5 mins)
*/5 * * * * /home/se-rith-chankolboth/bin/backup_exam

# E3: Backup exam once at 16:00 today (June 30)
0 16 30 6 * /home/se-rith-chankolboth/bin/backup_exam
```

### 3. Collecting Cron Reports
Verify that `cron_recurring.log` updates every minute. After `14:35`, verify that `cron_oneshot.log` contains the timed entry.
Create the `cron_report.txt` containing the required outputs:
```bash
crontab -l > ~/os-se-p20240038/final-exam/partE_automation/cron_report.txt
echo -e "\n=== cron_recurring.log ===" >> ~/os-se-p20240038/final-exam/partE_automation/cron_report.txt
cat ~/os-se-p20240038/final-exam/partE_automation/logs/cron_recurring.log >> ~/os-se-p20240038/final-exam/partE_automation/cron_report.txt
echo -e "\n=== cron_oneshot.log ===" >> ~/os-se-p20240038/final-exam/partE_automation/cron_report.txt
cat ~/os-se-p20240038/final-exam/partE_automation/logs/cron_oneshot.log >> ~/os-se-p20240038/final-exam/partE_automation/cron_report.txt
echo -e "\n=== ~/exam-backups/ listing ===" >> ~/os-se-p20240038/final-exam/partE_automation/cron_report.txt
ls -la ~/exam-backups >> ~/os-se-p20240038/final-exam/partE_automation/cron_report.txt
```

---

## 📤 Submission (Git)
Once you have run all components, taken screenshots, and updated `README.md` and `commands.md`, commit and push your repository to submit the exam:
```bash
cd ~/os-se-p20240038
git add .
git commit -m "Final exam deliverables"
git push
```
