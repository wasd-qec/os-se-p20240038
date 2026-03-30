/* file_reader_sys.c */
#include <fcntl.h>
#include <unistd.h>

int main() {
    char buffer[256];
    // YOUR CODE HERE
    // 1. Open "output.txt" for reading using open()
    int fd = open("output.txt", O_RDONLY);
    if (fd == -1) {
        return 1;
    }
    
    // 2. Read content into buffer using read() in a loop
    // 3. Write the content to the terminal (fd 1) using write()
    ssize_t bytes_read;
    while ((bytes_read = read(fd, buffer, sizeof(buffer))) > 0) {
        write(1, buffer, bytes_read);
    }
    
    // 4. Close the file using close()
    close(fd);
    return 0;
}