#include <stdio.h>
#include <stdlib.h>
#include <signal.h>
#include <unistd.h>

void handle_signal(int sig) {
    if (sig == SIGINT) {
        printf("\nCaught SIGINT (Interactive Interrupt)!\n");
    } else if (sig == SIGTERM) {
        printf("\nCaught SIGTERM (Polite Termination)!\n");
    }
    printf("Performing cleanup... releasing resources... Done.\n");
    printf("Exiting cleanly.\n");
    exit(0);
}

int main() {
    printf("Signal Demo running. PID: %d. Waiting for signals (SIGINT/SIGTERM)...\n", getpid());
    
    // Install handlers
    if (signal(SIGINT, handle_signal) == SIG_ERR) {
        perror("Error registering SIGINT handler");
        return 1;
    }
    if (signal(SIGTERM, handle_signal) == SIG_ERR) {
        perror("Error registering SIGTERM handler");
        return 1;
    }
    
    // Infinite loop waiting for signal
    while (1) {
        printf("Running loop...\n");
        sleep(2);
    }
    
    return 0;
}
