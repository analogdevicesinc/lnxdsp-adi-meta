/*
 * User space application to load a standalone Blackfin ELF
 * into the second core of a dual core Blackfin (like BF561).
 *
 * Copyright 2005-2009 Analog Devices Inc.
 *
 * Enter bugs at http://blackfin.uclinux.org/
 *
 * Licensed under the GPL-2 or later.
 */

#include <elf.h>
#include <errno.h>
#include <fcntl.h>
#include <getopt.h>
#include <link.h>
#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/ioctl.h>
#include <sys/mman.h>
#include <sys/stat.h>
#include <sys/sysinfo.h>
#include <sys/ioctl.h>

#include "icc.h"

#define GETOPT_FLAGS "h"
#define a_argument required_argument
static struct option const long_opts[] = {
  {"start", a_argument,  NULL, 0x1},
  {"stop",  a_argument,  NULL, 0x2},
  {"svect1", a_argument,  NULL, 0x3},
  {"svect2", a_argument,  NULL, 0x4},
  {"help",  no_argument, NULL, 'h'},
  {NULL,    no_argument, NULL, 0x0}
};

int main(int argc, char *argv[])
{
  int i;
  struct stat stat;
  void *buf;
  unsigned int task_init_addr, task_exit_addr;
  struct sm_packet pkt;
  int fd = open("/dev/corectrl", O_RDWR);

  if (fd < 0) {
    perror("unable to open /dev/corectrl");
    exit(10);
  }


  while ((i=getopt_long(argc, argv, GETOPT_FLAGS, long_opts, NULL)) != -1) {
    switch (i) {
      int coreid=0, ret=0;
      unsigned int svect;
      case 0x1:
        coreid = atoi(optarg);
        printf("Test core %d start\n", coreid);
        ret = ioctl(fd, CMD_CORE_START, coreid);
        printf("Test core %d end: %d\n", coreid, ret);
        break;
      case 0x2:
        coreid = atoi(optarg);
        printf("Test core %d stop\n", coreid);
        ret = ioctl(fd, CMD_CORE_STOP, coreid);
        printf("Test core %d end: %d\n", coreid, ret);
        break;
      case 0x3:
        svect = strtoul(optarg, 0, 16);
        printf("Test setting svect to 0x%08x, for core_id %d\n", svect, 1);
        ret = ioctl(fd, CMD_SET_SVECT1, svect);
        printf("ret: %d\n", ret);
        break;
      case 0x4:
        svect = strtoul(optarg, 0, 16);
        printf("Test setting svect to 0x%08x, for core_id %d\n", svect, 2);
        ret = ioctl(fd, CMD_SET_SVECT2, svect);
        break;
      case 'h':
      default:
        printf("Valid options\n--start CORE_NUM\n--stop CORE_NUM\n");
    }
  }

  close(fd);

  return 0;
}
