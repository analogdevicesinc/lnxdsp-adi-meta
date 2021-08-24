

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <errno.h>
#include <sys/ioctl.h>
#include <fcntl.h>
#include <dirent.h>
#include <linux/rpmsg.h>

#define MAX_PATH_LEN 512
#define RPMSG_BUS_PATH "/sys/bus/rpmsg"
#define RPMSG_CHARDEV_DRIVER "rpmsg_chrdev"
#define RPMSG_CHARDEV_CTRL "rpmsg_ctrl"


int rpmsg_bind(char *device)
{
	int fd;
	int ret;
	char path[MAX_PATH_LEN];
	DIR *directory;
	struct dirent *dir_entry;
	struct rpmsg_endpoint_info ept_info;

	/* Override driver of the device with rpmsg_chardev */
	snprintf(path, MAX_PATH_LEN, "%s/devices/%s/driver_override",	RPMSG_BUS_PATH, device);
	fd = open(path, O_WRONLY);
	if (fd < 0) {
		fprintf(stderr, "Can't open %s: %s\n", path, strerror(errno));
		return -EINVAL;
	}
	ret = write(fd, RPMSG_CHARDEV_DRIVER, strlen(RPMSG_CHARDEV_DRIVER) + 1);
	if (ret < 0) {
		fprintf(stderr, "Can't write \"%s\" to %s: %s\n", RPMSG_CHARDEV_DRIVER, path, strerror(errno));
		return -EINVAL;
	}
	close(fd);

	/* Bind the device to rpmsg_chrdev driver */
	snprintf(path, MAX_PATH_LEN, "%s/drivers/%s/bind", RPMSG_BUS_PATH, RPMSG_CHARDEV_DRIVER);
	fd = open(path, O_WRONLY);
	if (fd < 0) {
		fprintf(stderr, "Can't open %s: %s\n", path, strerror(errno));
		return -EINVAL;
	}
	ret = write(fd, device, strlen(device) + 1);
	if (ret < 0) {
		fprintf(stderr, "Can't write \"%s\" to %s: %s\n", device, path, strerror(errno));
		return -EINVAL;
	}
	close(fd);

	/* Find rpmsg_ctrl */
	snprintf(path, MAX_PATH_LEN, "%s/devices/%s/rpmsg", RPMSG_BUS_PATH, device);
	directory = opendir(path);
	if (directory == NULL) {
		fprintf(stderr, "Can't open dir %s: %s\n", path, strerror(errno));
		return -EINVAL;
	}
	fd = -1;
	while ((dir_entry = readdir(directory)) != NULL) {
		if (!strncmp(dir_entry->d_name, RPMSG_CHARDEV_CTRL, strlen(RPMSG_CHARDEV_CTRL))) {
			snprintf(path, MAX_PATH_LEN, "/dev/%s", dir_entry->d_name);
			fd = open(path, O_RDWR | O_NONBLOCK);
			if (fd < 0) {
				fprintf(stderr, "Can't open "RPMSG_CHARDEV_CTRL" %s: %s\n", path, strerror(errno));
				closedir(directory);
				return -EINVAL;
			}
			break;
		}
	}
	closedir(directory);
	if(fd < 0){
		fprintf(stderr, "Can't find "RPMSG_CHARDEV_CTRL" for %s\n", device);
		return -EINVAL;
	}

	/* Create endpoint for rpmsg char device */
	snprintf(ept_info.name, sizeof(ept_info.name), "chrdev_%s", device);
	ept_info.src = 0;
	ept_info.dst = 0xFFFFFFFF;
	ret = ioctl(fd, RPMSG_CREATE_EPT_IOCTL, &ept_info);
	if (ret){
		fprintf(stderr, "Can't create endpoint %s: %s\n", ept_info.name, strerror(errno));
		close(fd);
		return -EINVAL;
	}
	close(fd);

	return 0;
}

int rpmsg_unbind(char *device)
{
	int fd;
	int ret;
	char path[MAX_PATH_LEN];
	char *rpmsg_chrdev_driver = RPMSG_CHARDEV_DRIVER;

	/* Unbind the device from rpmsg_chrdev driver */
	snprintf(path, MAX_PATH_LEN, "%s/drivers/%s/unbind", RPMSG_BUS_PATH, rpmsg_chrdev_driver);
	fd = open(path, O_WRONLY);
	if (fd < 0) {
		fprintf(stderr, "Can't open %s: %s\n", path, strerror(errno));
		return -EINVAL;
	}
	ret = write(fd, device, strlen(device) + 1);
	if (ret < 0) {
		fprintf(stderr, "Can't write \"%s\" to %s: %s\n", device, path, strerror(errno));
		close(fd);
		return -EINVAL;
	}
	
	close(fd);
	return 0;
}

void usage(void){
	printf("Usage: rpmsg-bind-chardev [-u] -d rpmsg_device_path\n"
				 "		-u	unbind specified device\n"
				 "		-d	path to the rpmsg device\n"
				 "		-h	usage\n"
	);
	exit(1);
}

int main(int argc, char *argv[]){
	int ret, i, j;

	int opt;
	int bind = 1;
	char *rpmsg_device=NULL;
	
	while ((opt = getopt(argc, argv, "ud:h?")) != -1) {
		switch (opt) {
		case 'u':
			bind = 0;
			break;
		case 'd':
			rpmsg_device = optarg;
			break;
		default:
			usage();
			break;
		}
	}

	if(rpmsg_device == NULL){
		usage();
	}

	if(bind){
		ret = rpmsg_bind(rpmsg_device);
	}else{
		ret = rpmsg_unbind(rpmsg_device);
	}

	if(ret < 0){
		if(bind)
			rpmsg_unbind(rpmsg_device);
		exit(1);
	}else{
		exit(0);
	}
}
