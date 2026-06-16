# Remote Server SSH & Git Forwarding Guide

This guide contains the setup steps and commands for connecting to the remote server, sharing your local Git authentication, and configuring VS Code to connect directly.

---

## 1. SSH Agent Forwarding (Pushing Git from Server)
To push changes to GitHub directly from the server without configuring credentials or passwords on the server, you can forward your local machine's loaded SSH keys:

### Step A: On your local WSL terminal
Start your local agent and add your SSH key:
```bash
# Start the ssh-agent if it is not already running
eval $(ssh-agent -s)

# Add your GitHub private SSH key to the agent
ssh-add ~/.ssh/id_ed25519
```

### Step B: Connect with Forwarding Enabled (`-A`)
Connect to the server using the `-A` flag:
```bash
ssh -A se-rith-chankolboth@ssh2.rathpisey.site
```

### Step C: Configure Git identity on the server (First time only)
```bash
git config --global user.name "both"
git config --global user.email "rithchankolboth@gmail.com"
```

Once connected this way, you can run `git push origin main` on the server terminal and it will authenticate using your local machine's key.

---

## 2. Setting Up VS Code Remote - SSH with WSL Configuration
To open files and run commands directly in the server using VS Code:

1. Open **Settings** in VS Code (`Ctrl + ,`).
2. Search for: `remote.SSH.configFile`
3. Under the **Remote.SSH: Config File** setting, enter your WSL config path:
   ```text
   \\wsl.localhost\Ubuntu-22.04\home\both\.ssh\config
   ```
4. Press `Ctrl + Shift + P` and choose **Remote-SSH: Connect to Host...** and select `ssh2.rathpisey.site` from the list.

*Note: You must have `cloudflared.exe` installed on your Windows system PATH to handle the proxy command when connecting from Windows.*
