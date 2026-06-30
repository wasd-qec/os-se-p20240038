# commands.md — exact commands I ran, per part

## Part A — Threads, Mapping & Signals

```bash
# Compile the C programs on the server
gcc -pthread -o ~/os-se-p20240038/final-exam/partA_threads/thread_demo ~/os-se-p20240038/final-exam/partA_threads/thread_demo.c
gcc -o ~/os-se-p20240038/final-exam/partA_threads/signal_demo ~/os-se-p20240038/final-exam/partA_threads/signal_demo.c

# Run thread_demo in background and capture kernel thread mapping (LWP)
~/os-se-p20240038/final-exam/partA_threads/thread_demo &
DEMO_PID=$!
sleep 2
ps -T -p $DEMO_PID > ~/os-se-p20240038/final-exam/partA_threads/thread_map.txt
wait $DEMO_PID

# Run signal_demo and send interactive interrupt (Ctrl+C)
~/os-se-p20240038/final-exam/partA_threads/signal_demo
```

## Part B — Permissions, Special Bits & ACLs

```bash
# Build permission tree with shared dir and private file
mkdir -p ~/os-se-p20240038/final-exam/partB_security/shared_dir
touch ~/os-se-p20240038/final-exam/partB_security/shared_dir/private_file

# Set fine-grained permissions: owner rw only for file, and traverse-only (execute) for directory for others
chmod 600 ~/os-se-p20240038/final-exam/partB_security/shared_dir/private_file
chmod 711 ~/os-se-p20240038/final-exam/partB_security/shared_dir

# SetGID group inheritance example directory
mkdir -p ~/os-se-p20240038/final-exam/partB_security/setgid_dir
chmod g+s ~/os-se-p20240038/final-exam/partB_security/setgid_dir

# Sticky bit directory
mkdir -p ~/os-se-p20240038/final-exam/partB_security/sticky_dir
chmod +t ~/os-se-p20240038/final-exam/partB_security/sticky_dir

# Compile and set setuid bit on setuid_demo
gcc -o ~/os-se-p20240038/final-exam/partB_security/setuid_demo ~/os-se-p20240038/final-exam/partB_security/setuid_demo.c
chmod u+s ~/os-se-p20240038/final-exam/partB_security/setuid_demo

# Run setuid_demo binary
~/os-se-p20240038/final-exam/partB_security/setuid_demo

# Set ACL on private file
setfacl -m u:se-rith-chankolboth:rw ~/os-se-p20240038/final-exam/partB_security/shared_dir/private_file
getfacl ~/os-se-p20240038/final-exam/partB_security/shared_dir/private_file

# Generate report
ls -ld ~/os-se-p20240038/final-exam/partB_security/shared_dir > ~/os-se-p20240038/final-exam/partB_security/perm_report.txt
ls -l ~/os-se-p20240038/final-exam/partB_security/shared_dir/private_file >> ~/os-se-p20240038/final-exam/partB_security/perm_report.txt
stat ~/os-se-p20240038/final-exam/partB_security/shared_dir >> ~/os-se-p20240038/final-exam/partB_security/perm_report.txt
stat ~/os-se-p20240038/final-exam/partB_security/shared_dir/private_file >> ~/os-se-p20240038/final-exam/partB_security/perm_report.txt
```

## Part C — Bash Scripting, PATH & Safe Scanning

```bash
# Set up bin folder and place greeter and collector inside
mkdir -p ~/bin
cp ~/os-se-p20240038/final-exam/partC_scripting/scripts/* ~/bin/
chmod +x ~/bin/*

# Add bin to path in ~/.bashrc and load it
echo 'export PATH="$HOME/bin:$PATH"' >> ~/.bashrc
source ~/.bashrc

# Run greeter by name and generate path report
which greeter
greeter
echo "PATH: $PATH" > ~/os-se-p20240038/final-exam/partC_scripting/path_report.txt
which greeter >> ~/os-se-p20240038/final-exam/partC_scripting/path_report.txt

# Run collector over test dirs
mkdir -p ~/test_collector/dir1 ~/test_collector/dir2
echo "This is file A" > ~/test_collector/dir1/fileA.txt
echo "This is file B" > ~/test_collector/dir2/fileB.txt
touch ~/test_collector/dir1/unreadable.txt && chmod 000 ~/test_collector/dir1/unreadable.txt
cd ~/os-se-p20240038/final-exam/partC_scripting
collector ~/test_collector/dir1 ~/test_collector/dir2
```

## Part D — Race Condition & flock

```bash
# Copy scripts to bin
cp ~/os-se-p20240038/final-exam/partD_secure/scripts/* ~/bin/
chmod +x ~/bin/*

# Test unpatched swarm (flock commented out in buy_reactor_core)
sed -i 's/flock -x 200/# flock -x 200/' ~/bin/buy_reactor_core
sed -i 's/flock -u 200/# flock -u 200/' ~/bin/buy_reactor_core
echo "80" > ~/stock.txt
rm -f ~/sales.log
swarm
cat ~/stock.txt

# Test patched swarm with flock restored
sed -i 's/# flock -x 200/flock -x 200/' ~/bin/buy_reactor_core
sed -i 's/# flock -u 200/flock -u 200/' ~/bin/buy_reactor_core
echo "80" > ~/stock.txt
rm -f ~/sales.log
swarm
cat ~/stock.txt
```

## Part E — Backups & cron

```bash
# Copy scripts to ~/bin
cp ~/os-se-p20240038/final-exam/partE_automation/scripts/* ~/bin/
chmod +x ~/bin/*

# Run backup_project to verify retention logic
mkdir -p ~/test_project ~/project_backups
touch ~/test_project/file1.txt ~/test_project/file2.txt
backup_project ~/test_project ~/project_backups
backup_project ~/test_project ~/project_backups
backup_project ~/test_project ~/project_backups
backup_project ~/test_project ~/project_backups
ls -la ~/project_backups

# Setup user crontab
crontab -e

# Generate cron report
crontab -l > ~/os-se-p20240038/final-exam/partE_automation/cron_report.txt
echo -e "\n=== cron_recurring.log ===" >> ~/os-se-p20240038/final-exam/partE_automation/cron_report.txt
cat ~/os-se-p20240038/final-exam/partE_automation/logs/cron_recurring.log >> ~/os-se-p20240038/final-exam/partE_automation/cron_report.txt
echo -e "\n=== cron_oneshot.log ===" >> ~/os-se-p20240038/final-exam/partE_automation/cron_report.txt
cat ~/os-se-p20240038/final-exam/partE_automation/logs/cron_oneshot.log >> ~/os-se-p20240038/final-exam/partE_automation/cron_report.txt
echo -e "\n=== ~/exam-backups/ listing ===" >> ~/os-se-p20240038/final-exam/partE_automation/cron_report.txt
ls -la ~/exam-backups >> ~/os-se-p20240038/final-exam/partE_automation/cron_report.txt
```
