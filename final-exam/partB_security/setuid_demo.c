#include <stdio.h>
#include <unistd.h>
#include <sys/types.h>

int main() {
    uid_t ruid = getuid();
    uid_t euid = geteuid();
    
    printf("Real UID: %d\n", ruid);
    printf("Effective UID: %d\n", euid);
    
    if (ruid != euid) {
        printf("Setuid bit is working! EUID is different from RUID.\n");
    } else {
        printf("Real and Effective UIDs are the same.\n");
    }
    
    return 0;
}
