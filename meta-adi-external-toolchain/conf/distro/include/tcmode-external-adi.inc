require tcmode-external.inc

#Modify path as necessary
EXTERNAL_TOOLCHAIN = "/opt/analog/cces-linux-add-in/1.3.1/ARM/arm-linux-gnueabi"
EXTERNAL_TOOLCHAIN_BAREMETAL = "/opt/analog/cces/2.8.3/ARM/arm-none-eabi/bin"

#Set to your machine, looks like possibilities in the precompiled chains are:
#  sc589_rev_any, sc573_rev_any, sc589_rev_none, sc573_rev_none
ANALOG_DEVICES_PLATFORM_LIBDIR = "sc589_rev_any"

GLIBC_GENERATE_LOCALES = "en_US.UTF-8"
SECURITY_STACK_PROTECTOR_remove = "-fstack-protector-strong"
OLDEST_KERNEL = "4.4.0"