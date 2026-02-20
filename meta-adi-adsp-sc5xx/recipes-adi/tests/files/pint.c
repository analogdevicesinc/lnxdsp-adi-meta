
#include <errno.h>
#include <fcntl.h>
#include <stdlib.h>
#include <stdio.h>
#include <sys/epoll.h>
#include <unistd.h>

int test_pint(int fd) {
	int epollfd, res;
	struct epoll_event event, events;
	char buf[10] = {0};
	size_t readlen;

	// Dummy read to move to the end of the file, then we wait for changes
	readlen = read(fd, buf, 1);
	if (readlen != 1) {
		printf("read incorrect number of bytes: %zu\n", readlen);
		return -EINVAL;
	}

	epollfd = epoll_create1(0);
	if (epollfd < 0) {
		printf("failed to create epoll: %d\n", epollfd);
		return res;
	}

	event.events = EPOLLPRI | EPOLLERR;
	event.data.fd = fd;
	res = epoll_ctl(epollfd, EPOLL_CTL_ADD, fd, &event);
	if (res < 0) {
		printf("failed to add to epoll: %d\n", res);
		return res;
	}

	res = epoll_wait(epollfd, &events, 1, -1);
	if (res < 0) {
		printf("epoll_wait failed: %d\n", res);
		return res;
	}

	lseek(fd, 0, SEEK_SET);
	readlen = read(fd, buf, 1);
	if (readlen != 1) {
		printf("read incorrect number of bytes after interrupt: %zu\n", readlen);
		return -EINVAL;
	}

	printf("Got interrupt, value is now: %s\n", buf);
	return 0;
}

int main(int argc, char **argv) {
	char path[256];
	int gpio;

	if (argc != 2) {
		printf(" Usage:\n");
		printf("     %s N\n", argv[0]);
		printf(" where N is the GPIO number to monitor for an interrupt event\n");
		return 1;
	}

	snprintf(path, sizeof(path), "/sys/class/gpio/gpio%s/value", argv[1]);
	gpio = open(path, O_RDONLY);

	if (!gpio) {
		printf("Unable to open %s, errno = %d\n", path, errno);
		return 1;
	}

	return test_pint(gpio);
}
