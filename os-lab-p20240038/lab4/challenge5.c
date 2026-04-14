#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/wait.h>

int main() {
    pid_t pid;
    
    for (int i = 0; i < 3; i++) {
        pid = fork();

        if (pid < 0) {
            perror("fork failed");
            exit(1);
        } else if (pid == 0) {
            // Child process
            printf("Child %d: PID = %d, Parent PID = %d\n", i + 1, getpid(), getppid());
            sleep(60);  // Sleep so you can observe with ps
            printf("Child %d exiting\n", i + 1);
            exit(0);
        }
    }

    // Parent waits for all children
    for (int i = 0; i < 3; i++) {
        wait(NULL);
    }

    printf("Parent process exiting\n");
    return 0;
}

