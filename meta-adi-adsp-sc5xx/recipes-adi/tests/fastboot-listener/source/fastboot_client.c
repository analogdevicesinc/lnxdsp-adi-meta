/* Copyright (C) Analog Devices Inc. 2020 All Rights Reserved
** Fastboot client. Used to trigger an action on a remote server by sending a message over TCP/IP
*/

#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <resolv.h>
#include <unistd.h>
#include <arpa/inet.h>
#include <signal.h>
#include "fastboot.h"

const char *server_ip;
unsigned port;
int useUDP = 1;
int useTCP = 0;

unsigned long long pings = 0;

void sig(int sig) {
    printf("Terminating after %lld messages\n",pings);
    exit(-1);
}

int main(int argc, const char *argv[]) {
    int sock = 0;
    int err;
    struct sockaddr_in server_addr = {0};
    int app_done = 0, ping_done = 0;

    int run_forever = 0;
    int arg_pos = 1;

    signal(SIGINT,sig);
    if ( useUDP && useTCP ) {
        printf("Error: Cannot run in both TCP/IP and UDP modes\n");
        exit(-1);
    }
    if ( argc == 1 ) {
        server_ip = SERVER_IP;
        port = SERVER_PORT;
    } else if (strncmp(argv[1],"-forever",8) == 0 ) {
        printf("Will run application forever\n");
        run_forever = 1;
        arg_pos++;
    }
    if ( argc == 3 || ( argc == 4 && run_forever) ) {
        server_ip = argv[arg_pos];
        port = atoi(argv[arg_pos+1]);
    } else {
        printf("Error: Invalid arguments\n");
        printf("Usage: %s <IP ADDR> <PORT>\n",argv[0]);
        printf("       IP ADDR will be ignored in UDP mode\n");
        exit(-1);
    }
    if ( useUDP )
        printf("Client will broadcast datagram on port %d\n",port);
    else
        printf("Client will attempt to connect to %s %d\n",server_ip,port);
    printf("Sleep time between wake up requests: %d uSeconds\n",SLEEP_TIME);
    while ( !app_done ) {
        // Create a socket
        err = ( sock = socket(AF_INET,(useUDP?SOCK_DGRAM:SOCK_STREAM),0));
        if (err == -1) {
            perror("Failed to create a socket");
            exit(-1);
        }

        // Configure the socket to send requests on our port
        memset(&server_addr, 0, sizeof(server_addr));
        server_addr.sin_family = AF_INET;
        server_addr.sin_port = htons(port);

        // Set up the IP
        if (inet_pton(AF_INET,server_ip, &server_addr.sin_addr)<=0) {
            printf("Invalid Address: %s\n",server_ip);
            exit(-1);
        }

        // TCP: Connect to the server
        if ( useTCP ) {
            while ( !ping_done ) {
                if (connect( sock, (struct sockaddr *)&server_addr, sizeof(server_addr)) >= 0 ) {
                    printf("Connected to server\n");
                    ping_done = 1;
                } else {
                    pings++;
                    usleep(SLEEP_TIME);
                }
            }
            printf("Connected. Sending trigger word\n");
            printf("%Ld pings before establishing connection\n",pings);
            if ( send(sock, TRIGGER_WORD, strlen(TRIGGER_WORD), 0) == -1 ) {
                perror("Failure to send message\n");
                exit(-1);
            }
            printf("Success: Wake up request sent by client and received\n");
            close(sock);
            ping_done = 0;
            usleep(SLEEP_TIME);
            if ( !run_forever ) {
                app_done = 1;
            }
        } else { /*UDP */
            // Keep firing out the gong message. Not too frequently.
            printf("UDP mode will run forever, no method for receipt available\n");
            while ( !app_done ) {
                if ( sendto(sock, TRIGGER_WORD, strlen(TRIGGER_WORD), 0,
                            (struct sockaddr *)&server_addr, sizeof(server_addr)) < 0 ) {
                                perror("Failed to send message by UDP\n");
                                exit(-1);
                            }
                            usleep(SLEEP_TIME);
                            pings++;
            }
        }
    }
}
