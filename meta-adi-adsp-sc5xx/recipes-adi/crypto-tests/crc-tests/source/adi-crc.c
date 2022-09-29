/*
 * Based on hmac.c from cryptodev repository
 *
 */
#include <stdio.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <stdint.h>

#include <sys/ioctl.h>
#include <crypto/cryptodev.h>

static uint8_t result[AALG_MAX_RESULT_LEN];

//Hash sources
static uint8_t test1_input[] = "\x01\x02\x03\x04\x05\x06\x07\x08\x09\x0a\x0b\x0c\x0d\x0e\x0f\x10\x11\x12\x13\x14\x15\x16\x17\x18\x19\x1a\x1b\x1c\x1d\x1e\x1f\x20\x21\x22\x23\x24\x25\x26\x27\x28";
static uint8_t test2_input[] = "\x01\x02\x03\x04\x05\x06\x07\x08\x09\x0a\x0b\x0c\x0d\x0e\x0f\x10\x11\x12\x13\x14\x15\x16\x17\x18\x19\x1a\x1b\x1c\x1d\x1e\x1f\x20\x21\x22\x23\x24\x25\x26";
static uint8_t test3_input[] = "\x01\x02\x03\x04\x05\x06\x07\x08\x09\x0a\x0b\x0c\x0d\x0e\x0f\x10\x11\x12\x13\x14\x15\x16\x17\x18\x19\x1a\x1b\x1c\x1d\x1e\x1f\x20\x21\x22\x23\x24\x25\x26\x27";
static uint8_t test4_input[] = "\x01\x02\x03\x04\x05\x06\x07\x08\x09\x0a\x0b\x0c\x0d\x0e\x0f\x10\x11\x12\x13\x14\x15\x16\x17\x18\x19\x1a\x1b\x1c\x1d\x1e\x1f\x20\x21\x22\x23\x24\x25\x26\x27\x28\x29\x2a\x2b\x2c\x2d\x2e\x2f\x30\x31\x32\x33\x34\x35\x36\x37\x38\x39\x3a\x3b\x3c\x3d\x3e\x3f\x40\x41\x42\x43\x44\x45\x46\x47\x48\x49\x4a\x4b\x4c\x4d\x4e\x4f\x50\x51\x52\x53\x54\x55\x56\x57\x58\x59\x5a\x5b\x5c\x5d\x5e\x5f\x60\x61\x62\x63\x64\x65\x66\x67\x68\x69\x6a\x6b\x6c\x6d\x6e\x6f\x70\x71\x72\x73\x74\x75\x76\x77\x78\x79\x7a\x7b\x7c\x7d\x7e\x7f\x80\x81\x82\x83\x84\x85\x86\x87\x88\x89\x8a\x8b\x8c\x8d\x8e\x8f\x90\x91\x92\x93\x94\x95\x96\x97\x98\x99\x9a\x9b\x9c\x9d\x9e\x9f\xa0\xa1\xa2\xa3\xa4\xa5\xa6\xa7\xa8\xa9\xaa\xab\xac\xad\xae\xaf\xb0\xb1\xb2\xb3\xb4\xb5\xb6\xb7\xb8\xb9\xba\xbb\xbc\xbd\xbe\xbf\xc0\xc1\xc2\xc3\xc4\xc5\xc6\xc7\xc8\xc9\xca\xcb\xcc\xcd\xce\xcf\xd0\xd1\xd2\xd3\xd4\xd5\xd6\xd7\xd8\xd9\xda\xdb\xdc\xdd\xde\xdf\xe0\xe1\xe2\xe3\xe4\xe5\xe6\xe7\xe8\xe9\xea\xeb\xec\xed\xee\xef\xf0";

//HMAC keys
static uint8_t test1_input_key[] = "\xff\xff\xff\xff";
static uint8_t test2_input_key[] = "\xff\xff\xff\xff";
static uint8_t test3_input_key[] = "\xff\xff\xff\xff";
static uint8_t test4_input_key[] = "\xff\xff\xff\xff";


//Results
static uint8_t test1_crc32_expected[] = "\x84\x0c\x8d\xa2";
static uint8_t test2_crc32_expected[] = "\x8c\x58\xec\xb7";
static uint8_t test3_crc32_expected[] = "\xdc\x50\x28\x7b";
static uint8_t test4_crc32_expected[] = "\x10\x19\x4a\x5c";

void test_hash(int cfd, uint8_t * input, uint8_t * expected, uint8_t * key, uint32_t alg){
	int i;
	struct session_op sess;
#ifdef CIOCGSESSINFO
	struct session_info_op siop;
#endif
	struct crypt_op cryp;
	uint32_t length;

	switch(alg){
		case CRYPTO_CRC32_HMAC:
			length = 4;
			break;
	}

	memset(&sess, 0, sizeof(sess));
	memset(&cryp, 0, sizeof(cryp));

	/* SHA2_256 plain test */
	memset(result, 0, sizeof(result));

	switch(alg){
		case CRYPTO_CRC32_HMAC:
			sess.cipher = 0;
			sess.mackey = key;
			sess.mackeylen = strlen(key);
			sess.mac = alg;
			if (ioctl(cfd, CIOCGSESSION, &sess)) {
				perror("ioctl(CIOCGSESSION)");
				return 1;
			}
			break;
	}


#ifdef CIOCGSESSINFO
	siop.ses = sess.ses;
	if (ioctl(cfd, CIOCGSESSINFO, &siop)) {
		perror("ioctl(CIOCGSESSINFO)");
		return 1;
	}

	printf("Using %s with driver %s\n", siop.hash_info.cra_name, siop.hash_info.cra_driver_name);

#endif

	cryp.ses = sess.ses;
	cryp.len = strlen(input);
	cryp.src = (uint8_t*)input;
	cryp.mac = result;
	cryp.op = COP_ENCRYPT;
	if (ioctl(cfd, CIOCCRYPT, &cryp)) {
		perror("ioctl(CIOCCRYPT)");
		return 1;
	}

	switch(alg){
		case CRYPTO_CRC32_HMAC:
			printf("HMAC HMAC, input length: %d bytes, key length: %d\n", cryp.len, sess.mackeylen);
			break;
	}

	printf("calculated: ");
	for (i=0;i<length;i++) {
		printf("%.2x", (uint8_t)result[i]);
	}
	printf("\nexpected: ");
	for (i=0;i<length;i++) {
		printf("%.2x", (uint8_t)expected[i]);
	}
	printf("\n");
	if (memcmp(result, expected, length)!=0) {
		printf("TEST: failed\n\n");
	} else {
		printf("TEST: passed\n\n");
	}

	/* Finish crypto session */
	if (ioctl(cfd, CIOCFSESSION, &sess.ses)) {
		perror("ioctl(CIOCFSESSION)");
		return 1;
	}

}


static int test_crypto(int cfd) {
	test_hash(cfd, test1_input, test1_crc32_expected, test1_input_key, CRYPTO_CRC32_HMAC);
	test_hash(cfd, test2_input, test2_crc32_expected, test2_input_key, CRYPTO_CRC32_HMAC);
	test_hash(cfd, test3_input, test3_crc32_expected, test3_input_key, CRYPTO_CRC32_HMAC);
	test_hash(cfd, test4_input, test4_crc32_expected, test4_input_key, CRYPTO_CRC32_HMAC);

	return 0;
}

int main(int argc, char** argv){
	int fd = -1, cfd = -1;

	/* Open the crypto device */
	fd = open("/dev/crypto", O_RDWR, 0);
	if (fd < 0) {
		perror("open(/dev/crypto)");
		return 1;
	}

	/* Clone file descriptor */
	if (ioctl(fd, CRIOGET, &cfd)) {
		perror("ioctl(CRIOGET)");
		return 1;
	}

	/* Set close-on-exec (not really needed here) */
	if (fcntl(cfd, F_SETFD, 1) == -1) {
		perror("fcntl(F_SETFD)");
		return 1;
	}

	/* Run the test itself */
	if (test_crypto(cfd))
		return 1;

	/* Close cloned descriptor */
	if (close(cfd)) {
		perror("close(cfd)");
		return 1;
	}

	/* Close the original descriptor */
	if (close(fd)) {
		perror("close(fd)");
		return 1;
	}

	return 0;
}