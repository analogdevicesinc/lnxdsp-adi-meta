/* Copyright (C). Analog Devices Inc. 2020 All Rights Reserved
**
** Simple TCP socket listener. Waiting for a connection on SERVER_PORT and
** for the message TRIGGER_WORD to be sent to the server.
** By default encountering the trigger word will cause the application to exit
** returning the value GOOD_EXIT_VALUE to the shell so it can determine if it 
** is to wake the SHARC core.
** There is a point below where you can add code to wake the SHARC core via C source.
** This will ideally be faster than returning to the shell.
*/

#include <stdio.h>
#include <stdlib.h>
#include <sys/epoll.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <resolv.h>
#include <unistd.h>
#include <string.h>
#include "fastboot.h"

#define GOOD_EXIT_VALUE 55
#define BACKLOG 10
#define BUFFER_SIZE 1024

#define BUILD_FOR_TARGET 1

#if BUILD_FOR_TARGET
#define NOTIFY_DEV "/sys/remotekobj/test"
#endif

#define VERBOSE 0
/* Set useUDP to 1 to receive UDP broadcast, set to 0 to use TCP/IP */
int useUDP = 1;
int useTCP = 0;

int main(int argc, const char *argv) {
    int sock;
    int err;
    int i;
    struct sockaddr_in server_addr = {0};
#if BUILD_FOR_TARGET
    int sharc_fd;
#endif

    if ( useUDP && useTCP ) {
        printf("Error: Cannot use both UDP and TCP mode at same time\n");
        exit(-1);
    }
    // Spawn a child process and allow that to continue so system boot can continue
    if ( fork() != 0 ) {
        exit(0);
    }
#if VERBOSE
    if ( useUDP )
        puts("Fastboot Listener:Started in UDP mode\n");
    else
        puts("Fastboot Listener:Started in TCP/IP mode\n");
#endif
#if BUILD_FOR_TARGET
    // Open the system device used to signal the SHARC core
    sharc_fd = open(NOTIFY_DEV, O_RDWR);
    if ( sharc_fd < 0 ) {
        perror("Failed to open SHARC notify device");
        exit(-1);
    }
#endif
    // Create a socket
    err = ( sock = socket(AF_INET,useUDP?SOCK_DGRAM:SOCK_STREAM,0));
    if (err == -1) {
        perror("Failed to create a socket");
        exit(-1);
    }

    // Configure the socket to be TCP/IP and receive requests on our port
    server_addr.sin_family = AF_INET;
    server_addr.sin_addr.s_addr = htonl(INADDR_ANY);
    server_addr.sin_port = htons(SERVER_PORT);

    // Atempt to bind to the port
    err = bind(sock, (struct sockaddr *)&server_addr, sizeof(server_addr));
    if ( err ) {
        perror("FASTBOOT ERROR:");
        printf("failed to bind to port %d\n",SERVER_PORT);
        exit(-1);
    }

    // Listen for connections if we are in TCP mode
    if ( useTCP ) {
        err = listen(sock,BACKLOG);
        if ( err == -1 ) {
            perror("FASTBOOT ERROR:");
            printf("Error: Failed to listen on socket\n");
            exit(-1);
        }
#if VERBOSE
        printf("Listening for connections on port %d\n",SERVER_PORT);
#endif
    } else {
#if VERBOSE
        printf("Listening for broadcast on port %d\n",SERVER_PORT);
#endif
    }
    for ( ;; ) {
        struct sockaddr_in client_addr;
        int client_length;
        int connection_fd;
        socklen_t addrlen = sizeof(client_addr);
        char buffer[BUFFER_SIZE+1];

        if ( useUDP ) {
            i = recvfrom(sock, buffer, BUFFER_SIZE, 0,
                                   (struct sockaddr *)&client_addr, &addrlen);
            if ( i > 0 ) {
                buffer[i] = '\0';
#if VERBOSE
                printf("Server received UDP broadcast: %s\n",buffer);
#endif
            }
        } else {
            // Accept the incoming connection
            client_length = sizeof(client_addr);
            err = ( connection_fd = accept(sock, (struct sockaddr *)&client_addr, &client_length));
            if ( err == -1 ) {
                perror("FASTBOOT ERROR:");
                perror("accept failed");
                exit(-1);
            }
#if VERBOSE
            printf("Connection with client established\n");
            printf("Expecting trigger word \"%s\" to be sent\n",TRIGGER_WORD);
#endif
            // read data from the client
            i = read(connection_fd,buffer,BUFFER_SIZE);
        }
#if VERBOSE
        printf("Comparing buffer...\n");
#endif
        if ( strncmp(buffer,TRIGGER_WORD,strlen(TRIGGER_WORD)) == 0 ) {
            if ( useTCP ) {
                // close that socket before the client reconnects
                close(connection_fd);

            }
            close(sock);
#if VERBOSE
            perror("FASTBOOT LISTENER: DONE\n");
            printf("Trigger word encountered!\n");
            printf("Returning with value of %d. Check for this value in your shell script\n",
                GOOD_EXIT_VALUE);

#endif
#if BUILD_FOR_TARGET
            // Turn on LED 10
            char gpio_buffer[BUFFER_SIZE] = "49";
            int gpio_export_fd = open("/sys/class/gpio/export", O_WRONLY);
            if ( gpio_export_fd == 0 ) {
                perror("Failed to open GPIO export device\n");
                exit(-1);
            }
            write(gpio_export_fd,&gpio_buffer,strlen(gpio_buffer));
            close(gpio_export_fd);
            char led_buffer[BUFFER_SIZE] = "high";
            int led10_fd = open("/sys/class/gpio/gpio49/direction", O_WRONLY);
            if ( led10_fd == 0 ) {
                perror("Failed to open LED10 GPIO\n");
                exit(-1);
            }
            write(led10_fd,led_buffer,strlen(led_buffer));
            close(led10_fd);
            // Send an interrupt to the SHARC core to notify it that we have received a gong
            i =read(sharc_fd,&buffer,sizeof(int));
            close(sharc_fd);
#endif
#if VERBOSE
            puts("Fastboot listener: received wakeup\n");
#endif
            exit(GOOD_EXIT_VALUE);

        }
#if VERBOSE
        else {
            printf("NO match\n");
        }
#endif
    }
}
