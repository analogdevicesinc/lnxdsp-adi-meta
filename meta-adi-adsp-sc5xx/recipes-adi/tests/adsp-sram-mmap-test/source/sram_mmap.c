// SPDX-License-Identifier: GPL-2.0-only
/*
 * Copyright Analog Devices Inc.
 *
 * Maps a page of on-chip SRAM through an sram_mmap device, writes two
 * patterns to it and checks that they read back correctly.
 */
#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <sys/mman.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>

/* Write a pattern to the mapped SRAM and read it back */
static int check_pattern(volatile unsigned char *memory, int size, unsigned char xor)
{
	int i, errors = 0;

	for (i = 0; i < size; i++)
		memory[i] = (unsigned char)i ^ xor;

	for (i = 0; i < size; i++) {
		unsigned char expected = (unsigned char)i ^ xor;

		if (memory[i] != expected) {
			if (errors < 8)
				fprintf(stderr, "offset 0x%x: read 0x%02x, expected 0x%02x\n",
					i, memory[i], expected);
			errors++;
		}
	}

	return errors;
}

int main(int argc,char** argv)
{
	int handle ;
	const int size = 4096 ;
	volatile unsigned char *memory;
	int errors;

	if (argc < 2 || argc > 3)
	{
		fprintf(stderr,"usage sram_mmap device write|read \n");
		exit(-1);
	}
	handle = open(argv[1],O_RDWR);
	if (handle < 0)
	{
		fprintf(stderr,"Failed to open %s: %s\n",argv[1],strerror(errno));
		exit(-1);
	}
	memory = mmap(0,size,PROT_READ|PROT_WRITE,MAP_FILE|MAP_SHARED,handle,0);
	if (memory == MAP_FAILED)
	{
		fprintf(stderr,"mmap failed: %s\n",strerror(errno));
		close(handle);
		exit(1) ;
	}

	errors = check_pattern(memory, size, 0x00);
	errors += check_pattern(memory, size, 0xff);

	munmap((void *)memory,size);
	close(handle);

	if (errors) {
		fprintf(stdout,"mmap fail: %d bytes did not read back correctly.\n",errors);
		return 1;
	}

	fprintf(stdout,"mmap pass.\n");
	return 0;
}
