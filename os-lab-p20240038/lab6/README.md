# OS Lab 6 Submission — Linux Security, Users, Groups & File Permissions

- **Student Name:** Rith Chankolboth
- **Student ID:** p20240038

---

## Task Output Files

Make sure all of the following files are present in your `lab6/` folder:

- [x] `task1_users.txt`
- [x] `task2_groups.txt`
- [x] `task3_permissions.txt`
- [x] `task3_stat_output.txt`
- [x] `task4_special_bits.txt`
- [x] `task5_acl.txt`
- [x] `security_lab/whoami_suid.c`
- [x] `challenge_dropbox.txt`

---

## Screenshots

Insert your screenshots below.

### Screenshot 1 — Task 1: User Creation
Show `cat task1_users.txt` confirming both `dev_alice` and `dev_bob` accounts exist.
<!-- Insert your screenshot below: -->
![user creation](images/task1_user_creation.png)

---

### Screenshot 2 — Task 1: User Modification
Show the updated `/etc/passwd` entry for `dev_alice` with the GECOS comment field.
<!-- Insert your screenshot below: -->
![user modify](images/task1_user_modify.png)

---

### Screenshot 3 — Task 2: Group Setup
Show `cat task2_groups.txt` with group membership for both users.
<!-- Insert your screenshot below: -->
![group setup](images/task2_group_setup.png)

---

### Screenshot 4 — Task 2: Multiple Group Membership
Show `id dev_alice` confirming membership in both `devteam` and `auditors`.
<!-- Insert your screenshot below: -->
![multi group](images/task2_multi_group.png)

---

### Screenshot 5 — Task 3: Directory Permissions
Show `cat task3_permissions.txt` with `drwxrwx---` on the project directory.
<!-- Insert your screenshot below: -->
![dir permissions](images/task3_dir_permissions.png)

---

### Screenshot 6 — Task 3: Access Denied
Show the `Permission denied` error when `temp_user` tries to access the project directory.
<!-- Insert your screenshot below: -->
![access denied](images/task3_access_denied.png)

---

### Screenshot 7 — Task 4: setgid Bit
Show the directory listing with `s` in the group execute position, and `bob_file.txt` inheriting the `devteam` group.
<!-- Insert your screenshot below: -->
![setgid](images/task4_setgid.png)

---

### Screenshot 8 — Task 4: Sticky Bit
Show the `t` bit in the directory listing and the `Operation not permitted` error when `dev_bob` tries to delete `dev_alice`'s file.
<!-- Insert your screenshot below: -->
![sticky bit](images/task4_sticky_bit.png)

---

### Screenshot 9 — Task 4: setuid Bit
Show `ls -l whoami_suid` with `s` in the owner execute position and the program's UID output.
<!-- Insert your screenshot below: -->
![setuid](images/task4_setuid.png)

---

### Screenshot 10 — Task 5: ACL Directory
Show `getfacl /opt/techcorp/devproject` with the `auditors` ACE.
<!-- Insert your screenshot below: -->
![acl dir](images/task5_acl_dir.png)

---

### Screenshot 11 — Task 5: ACL Access Test
Show `dev_alice` successfully accessing the file and `temp_user` being denied.
<!-- Insert your screenshot below: -->
![acl test](images/task5_acl_test.png)

---

### Screenshot 12 — Task 5: ACL Output File
Show `cat task5_acl.txt` with the full ACL entries.
<!-- Insert your screenshot below: -->
![acl output](images/task5_acl_output.png)

---

### Screenshot 13 — Challenge: Secure Shared Drop-Box
Show the final `ls -ld /opt/techcorp/dropbox` and the failed deletion attempt.
<!-- Insert your screenshot below: -->
![challenge dropbox](images/challenge_dropbox.png)

---

## Answers to Lab Questions

1. **What is the difference between `userdel` and `userdel -r`?**
   > `userdel <username>` only deletes the user account from system database files (e.g., `/etc/passwd`, `/etc/shadow`, `/etc/group`), but leaves the user's home directory and files intact.
   > In contrast, `userdel -r <username>` recursively removes the user account along with their home directory and mail spool, cleaning up their files on the system.

2. **Why is it safer to use `visudo` instead of directly editing `/etc/sudoers`?**
   > Directly editing `/etc/sudoers` is risky because any syntax errors can lock all users (including root) out of administrative privileges, preventing `sudo` from working.
   > `visudo` is safer because it:
   > 1. Locks the file to prevent concurrent modifications.
   > 2. Performs an automatic syntax check before saving. If errors are found, it alerts you and asks for corrections rather than saving invalid configuration.

3. **What happens when a `setgid` directory contains files created by different users? What benefit does this provide for team collaboration?**
   > When a directory has the `setgid` bit set, any files created inside it automatically inherit the directory's group ownership rather than the primary group of the creating user. The individual files are still owned by the respective users who created them.
   > This is highly beneficial for team collaboration because it ensures all created files are instantly accessible (read/write depending on group permissions) to all members of the collaborative group without requiring manual permission adjustments or `chgrp` commands.

4. **What limitation of standard Unix permissions does the ACL system solve?**
   > Standard Unix permissions (UGO - User, Group, Others) are limited to defining access rights for a single owner, a single primary group, and everyone else. They cannot grant custom, distinct permissions to multiple specific users or groups.
   > Access Control Lists (ACLs) solve this limitation by enabling fine-grained, entry-specific permissions, allowing you to define distinct read/write/execute rights for multiple individual users (`user:alice:rw-`, `user:bob:r--`) and multiple groups (`group:auditors:r-x`) on the same file or directory.

