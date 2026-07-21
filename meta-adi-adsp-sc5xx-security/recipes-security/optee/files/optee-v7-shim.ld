OUTPUT_FORMAT("elf32-littlearm")
OUTPUT_ARCH(arm)
ENTRY(_start)
MEMORY {
	RAM (rwx): ORIGIN = 0xc0000000, LENGTH = 0x0010001c
}
SECTIONS
{
	. = 0xc0000000;
	_start = .;
	.text . : {
		*(.text*)
		. = ALIGN(4);
	} >RAM
	/DISCARD/ : {
		*(.dynsym .dynamic .interp .note.gnu.build-id)
	}
}

