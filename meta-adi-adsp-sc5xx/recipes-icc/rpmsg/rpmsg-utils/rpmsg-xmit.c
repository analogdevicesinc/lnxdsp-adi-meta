#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <signal.h>

#define DEFAULT_MSG_LEN 256
#define MAX_MSG_LEN (512-16) /* The default size of rpmsg buffer is 512B with 16B header */

int fd = 0;
char msg_to_send[MAX_MSG_LEN];
char msg_received[MAX_MSG_LEN];
ssize_t msglen = DEFAULT_MSG_LEN;

void signal_handler(__attribute__((unused)) const int signum) {
    exit(EXIT_FAILURE);
}

void exit_cleanup(void){
	if(fd) close(fd);
}

void print_help(void){
	fprintf(stderr, "Usage: rpmsg-xmit [-n msglen ] <device_file>\n");
	fprintf(stderr, "  -n 	message length send at a time to device file, default msglen=%d\n", DEFAULT_MSG_LEN);
}

int main (int argc, char **argv)
{
	int c;
	char *end;
	ssize_t len;
	opterr = 0;

	signal(SIGTERM, signal_handler);
	signal(SIGINT, signal_handler);
	atexit(exit_cleanup);

	while ((c = getopt (argc, argv, "n:h")) != -1)
		switch (c)
			{
			case 'n':
				msglen = strtol(optarg, &end, 10);
				if (*end !=0 ){
					fprintf(stderr, "Wrong format: msglen must be decimal\n");
					print_help();
					exit(EXIT_FAILURE);
				}
				if (msglen < 0){
					fprintf(stderr, "Wrong format: msglen must be positive\n");
					print_help();
					exit(EXIT_FAILURE);
				}
				break;
			case 'h':
				print_help();
				exit(EXIT_SUCCESS);
				break;
			case '?':
			default:
				exit(EXIT_FAILURE);
			}

	if (optind >= argc){
		fprintf(stderr, "Device file argument missing\n");
		print_help();
		exit(EXIT_FAILURE);
	}

	fd = open(argv[optind], O_RDWR);
	if(fd < 0){
		perror(argv[optind]);
		exit(EXIT_FAILURE);
	}

	while(1){
		len = fread(msg_to_send, msglen, 1, stdin);
		if(len == 0){
			if(feof(stdin)){
				exit(EXIT_SUCCESS);
			}
			if(ferror(stdin)){
				perror("Error: Read from stdin failed");
				exit(EXIT_FAILURE);
			}
			perror("Unknown error: Read from stdin failed");
			exit(EXIT_FAILURE);
		}

		len = write(fd, msg_to_send, msglen);
		len = read(fd, msg_received, MAX_MSG_LEN);

		len = fwrite(msg_received, len, 1, stdout);
		if(len == 0 ){
			if(feof(stdout)){
				exit(EXIT_SUCCESS);
			}
			if(ferror(stdout)){
				perror("Error: Write to stdout failed");
				exit(EXIT_FAILURE);
			}
			perror("Unknown error: Write to stdout failed");
			exit(EXIT_FAILURE);
		}
	}

	exit(EXIT_SUCCESS);
	return 0;
}
