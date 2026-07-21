# On the armv7 platforms, uboot SPL needs to jump directly to OPTEE.
# However, tee.bin contains a relocation table before the _start symbol,
# so when it is packed into an LDR via the elf repackaging strategy,
# the LDR tool places the first block at the start of this relocation
# table not the start of the code. Because of how the LDR tool works,
# it seems to be difficult to place the first flag on a different block
# We need to either split tee.bin into two elfs (would prefer not to)
# or create this "executable" which is last in the load order so that
# the bootrom jumps to it, and then it jumps into the middle of the
# optee block as expected

.align 3
.global _start
.type _start, function
.text
_start:
	ldr	r0, =OPTEE_START_ADDRESS
	bx	r0
