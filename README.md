# meta-adi Yocto meta layer for Analog Devices ADSP-SC5xx based EZ-KIT boards
---------------
This repository contains the Yocto meta layer that is used in the production of toolchains, uboot images, kernel and filesystem for ADSP-SC5xx EZ-KITs.

## Getting Started
Please refer to this repo's wiki for guidance in using Linux for ADSP-SC5xx: https://github.com/analogdevicesinc/lnxdsp-adi-meta/wiki

### Quick Evaluation
To quickly evaluate (i.e. build) what's on the 'main' branch, do:

```Shell
mkdir ./lnxdsp-main_$(date +"%Y-%m-%d")
cd ./lnxdsp-main_$(date +"%Y-%m-%d")
```
This will create a `lnxdsp-main_YYYY-MM-DD` directory and enter it.

```Shell
mkdir bin
curl http://commondatastorage.googleapis.com/git-repo-downloads/repo > ./bin/repo
chmod a+x ./bin/repo
./bin/repo init \
 -u https://github.com/analogdevicesinc/lnxdsp-repo-manifest.git \
 -m main.xml
./bin/repo sync
```

Now, inside the project directory that was created in the previous step, a `bin` directory will be created and the `repo` tool will be downoaded to it. The tool will be instructed to download the various `lnxdsp` sources (including this repo) according to the `main` branch.

What remains is picking a board to build for, e.g. the ADSP-SC598-SOM-EZKIT:
```Shell
source setup-environment -m adsp-sc598-som-ezkit
bitbake adsp-sc5xx-minimal
```
This will result in the `minimal` image being built. The [wiki](https://github.com/analogdevicesinc/lnxdsp-adi-meta/wiki) has detailed guides for building and loading the artefacts to all supported boards.

## Licensing
Please refer to the LICENSE.md file in this repository for more information regarding licensing.

## GlencOS / Demo branch instructions

```shell
# Fetch sources
mkdir gxp2-glencos; cd gxp2-glencos
mkdir bin
curl http://commondatastorage.googleapis.com/git-repo-downloads/repo > ./bin/repo
./bin/repo init \
   -u https://github.com/OliverGaskellADI/lnxdsp-repo-manifest.git \
   -b glencos \
   -m glencos.xml
./bin/repo sync

# Setup build environment
source setup-environment -m adsp-sc598-som-ezkit -b build
cp ../sources/meta-adi/tools/bblayers.conf ./conf/  # Update bblayers to add the jupyter layer

# Build demo image
bitbake adsp-sc5xx-demo
```

You should then setup the system as normal, and boot using `nfsboot`.
> Note that the root filesystem will be called `adsp-sc5xx-demo-adsp-sc598-som-ezkit.rootfs.tar.xz` - Yocto 5 adds the `.rootfs` and the image name is `adsp-sc5xx-demo`.

### Configuring Jupyter

On the target machine:

```shell
# Set hostname
# Replace HOSTNAME with a unique hostname, e.g. my-sc598
echo HOSTNAME > /etc/hostname

# Setup Jupyter Config
mkdir -pv /root/.jupyter/
cat << EOF > /root/.jupyter/jupyter_lab_config.py
# Configuration file for lab.
import platform
c = get_config()  # noqa
c.ServerApp.allow_origin = '*'
c.ServerApp.allow_remote_access = True
c.ServerApp.allow_root = True
c.ServerApp.ip = platform.node() + ".local"
EOF
```

### Setting up Swap

Setting up swap memory over NFS gives the system access to a large swap partition, meaning memory-intensive applications like Jupyter can still run on the board, despite the small ~200MB of actual memory available to Linux on the ARM core.

On the build machine: (assuming your NFS filesystem is setup in `/romfs`)

```shell
# Create a 2GB swapfile
sudo dd if=/dev/zero of=/romfs/swapfile bs=1M count=2000
```

On the board:

```shell
# Create a loop device for the swapfile
losetup /dev/loop0 /swapfile

# Setup swap
mkswap /dev/loop0
swapon /dev/loop0
```

### Run Jupyter

```shell
# Run Jupyter
jupyter lab

# Once Jupyter starts, a link will be printed that can be used to access it remotely - it should start with your hostname.
# E.g., http://my-sc598.local:8888/lab?token=...
```