/* dir_list_sys.c */
#include <fcntl.h>
#include <unistd.h>
#include <dirent.h>
#include <sys/stat.h>
#include <string.h>
#include <stdio.h>    // only for snprintf to format numbers into strings

int main() {
    char buffer[512];
    DIR *dir;
    struct dirent *entry;
    struct stat file_stat;
    
    // Open current directory
    dir = opendir(".");
    if (dir == NULL) {
        return 1;
    }
    
    // Print header line using write()
    const char *header = "FileName                       Size\n";
    write(1, header, strlen(header));
    
    // Loop through entries with readdir()
    while ((entry = readdir(dir)) != NULL) {
        // For each entry, use stat() to get file size
        stat(entry->d_name, &file_stat);
        
        // Format output into buffer with snprintf(), then write() to fd 1
        int len = snprintf(buffer, sizeof(buffer), "%-30s %10ld\n", 
                          entry->d_name, file_stat.st_size);
        write(1, buffer, len);
    }
    
    // Close directory with closedir()
    closedir(dir);
    return 0;
}
