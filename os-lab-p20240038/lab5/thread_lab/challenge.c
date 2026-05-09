#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <signal.h>
#include <pthread.h>

volatile int keep_running = 1;

// Signal handler for SIGINT
void sig_handler(int signo) {
    if (signo == SIGINT) {
        printf("\n[Signal Caught] Received SIGINT. Signaling threads to exit...\n");
        keep_running = 0;
    }
}

// Worker thread function
void* worker_thread(void* arg) {
    int thread_id = *(int*)arg;
    
    printf("Thread %d started\n", thread_id);
    
    while (keep_running) {
        printf("Thread %d is working (TID: %lu)\n", thread_id, pthread_self());
        sleep(1);
    }
    
    printf("Thread %d exiting cleanly\n", thread_id);
    pthread_exit(NULL);
}

int main() {
    pthread_t tid1, tid2;
    int id1 = 1, id2 = 2;
    
    // Register signal handler for SIGINT
    if (signal(SIGINT, sig_handler) == SIG_ERR) {
        printf("Cannot catch SIGINT\n");
        exit(1);
    }
    
    printf("Main process with PID: %d\n", getpid());
    printf("Spawning 2 worker threads...\n");
    printf("Press Ctrl+C to trigger graceful shutdown\n\n");
    
    // Spawn two worker threads
    if (pthread_create(&tid1, NULL, worker_thread, &id1) != 0) {
        perror("Failed to create thread 1");
        exit(1);
    }
    
    if (pthread_create(&tid2, NULL, worker_thread, &id2) != 0) {
        perror("Failed to create thread 2");
        exit(1);
    }
    
    // Wait for both threads to complete
    pthread_join(tid1, NULL);
    pthread_join(tid2, NULL);
    
    printf("All threads cleanly exited. Goodbye.\n");
    return 0;
}
