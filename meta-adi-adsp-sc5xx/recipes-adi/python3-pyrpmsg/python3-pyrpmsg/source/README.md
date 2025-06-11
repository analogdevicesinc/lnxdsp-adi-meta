# pyrpmsg

```
############
##  ########
##     #####    Analog Devices, Inc.
##        ##
##     #####    Remote RPMsg Interface
##  ########
############
```

## Installation

### Dependencies

In Yocto, add the following to your `conf/local.conf`:
```
IMAGE_INSTALL += " python3 python3-modules rpmsg-utils "
```

The entire repository is required both on the remote and the host. It is recommended to use git to clone this onto the host, and then copy to the remote from there.

Also, some modification is required to `rpmsg-utils`. Run the following from your yocto build directory:

```bash
devtool modify rpmsg-utils
cd workspace/sources/rpmsg-utils
curl https://gist.githubusercontent.com/OliverGaskellADI/43ec9e5690338370eba4699d290a70eb/raw/a8681ef49f2510611ec2ffd268a125ea8a2dc0ca/0001-Emit-device-path-after-binding.patch | patch -p1
```

This will allow modification of rpmsg-utils, fetch a patch file and apply it.

Finally, build the distribution as normal with yocto, and boot into linux.

## Usage

To run CLI (on the host):
```bash
python3 -m adi_remote_rpmsg -h <hostname> -c
```

To run remote:
```bash
python3 -m adi_remote_rpmsg -h <hostname> -r
```

Where `<hostname>` is the IP address/hostname of the CLI device.

Also, `-p` or `--port` can be used to specify a non-default port.

### Example Usage

Here, we bind two RPMsg endpoints, specifying the device name (from `/sys/bus/rpmsg/devices/`) and a source address to bind to.

Then, `use_e` sets the default endpoint to endpoint 0 - we then perform a number of writes and reads to this endpoint. Finally, we write and read to endpoint 1, using the `-e` option.

```
$ python3 -m adi_remote_rpmsg -c -h 10.37.65.102
Connected to ('10.37.65.181', 55282)
Detected cores 0, 1
Detected and opened endpoints:

(  0) -> bind -d virtio0.sharc-echo.-1.151 -s 100
Bound and opened device, endpoint number is 0
(  0) -> bind -d virtio0.sharc-echo-cap.-1.161 -s 101
Bound and opened device, endpoint number is 1
(  0) -> use_e -e 0
Set default endpoint to 0
(  0) -> lazy_write abcd
(  0) -> lazy_write defg
(  0) -> write ghij
(  0) -> read 100
abcd => echo from Core1

(  0) -> read 100
defg => echo from Core1

(  0) -> read 100
ghij => echo from Core1

(  0) -> write -e 1 abcd
(  0) -> read -e 1 100
ABCD => capitalized echo from Core1

(  0) -> quit
Exiting...
```

### Full command reference

`[args]` specifies optional arguments, `<args>` specifies something that is required. If optional arguments `-c` or `-e` are omitted, the default core/endpoint number is used, if set.

```
# Reading/Writing to/from endpoints
write [-e <endpoint number>] <data>
lazy_write [-e <endpoint number>] <data>
lazy_clear [-e <endpoint number>]
read [-e <endpoint number>] <size>

# Set default core/endpoint
use_c -c <core number>
use_e -e <endpoint number>

# Remove default core/endpoint
use_c -r
use_e -r

# Core Control
start [-c <core number>]
stop [-c <core number>]
load_fw [-c <core number>] -f <firmware path>

# Endpoint Control
bind -p <device name> -s <source address>
open_e [-e <endpoint number>]
close_e [-e <endpoint number>]

# Misc
quit
```
