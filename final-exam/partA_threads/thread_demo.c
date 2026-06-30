#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/syscall.h>

#define NUM_THREADS 5

void* worker(void* arg) {
    int id = *((int*)arg);
    free(arg);
    
    // Get Thread ID / LWP ID on Linux
    pid_t tid = syscall(SYS_gettid);
    long val = (id + 1) * 100;
    
    printf("Worker %d (LWP ID: %d) starting... Computed Value: %ld\n", id, tid, val);
    
    // Sleep to allow mapping observation
    sleep(10);
    
    printf("Worker %d (LWP ID: %d) exiting.\n", id, tid);
    return (void*)val;
}

int main() {
    pthread_t threads[NUM_THREADS];
    pid_t pid = getpid();
    printf("Main thread PID: %d. Spawning %d workers...\n", pid, NUM_THREADS);
    
    for (int i = 0; i < NUM_THREADS; i++) {
        int* arg = malloc(sizeof(*arg));
        *arg = i;
        if (pthread_create(&threads[i], NULL, worker, arg) != 0) {
            perror("pthread_create failed");
            return 1;
        }
    }
    
    long total = 0;
    for (int i = 0; i < NUM_THREADS; i++) {
        void* retval;
        if (pthread_join(threads[i], &retval) != 0) {
            perror("pthread_join failed");
            return 1;
        }
        long val = (long)retval;
        printf("Joined worker %d, returned value: %ld\n", i, val);
        total += val;
    }
    
    // Spawn extra workers after originals have joined
    const int EXTRA_WORKERS = 3;
    pthread_t extra_threads[EXTRA_WORKERS];
    for (int i = 0; i < EXTRA_WORKERS; i++) {
        int* arg = malloc(sizeof(*arg));
        *arg = NUM_THREADS + i; // Distinct IDs for extra workers
        if (pthread_create(&extra_threads[i], NULL, worker, arg) != 0) {
            perror("pthread_create (extra) failed");
            return 1;
        }
    }
    for (int i = 0; i < EXTRA_WORKERS; i++) {
        void* retval;
        if (pthread_join(extra_threads[i], &retval) != 0) {
            perror("pthread_join (extra) failed");
            return 1;
        }
        long val = (long)retval;
        printf("Joined extra worker %d, returned value: %ld\n", i, val);
        total += val;
    }
    
    printf("Summary: Total collected value = %ld\n", total);
    return 0;
}
