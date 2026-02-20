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

Now, inside the project directory that was created in the previous step, a `bin` directory will be created and the `repo` tool will be downloaded to it. The tool will be instructed to download the various `lnxdsp` sources (including this repo) according to the `main` branch.

What remains is picking a board to build for, e.g. the ADSP-SC598-SOM-EZKIT:
```Shell
source setup-environment -m adsp-sc598-som-ezkit
bitbake adsp-sc5xx-minimal
```
This will result in the `minimal` image being built. The [wiki](https://github.com/analogdevicesinc/lnxdsp-adi-meta/wiki) has detailed guides for building and loading the artefacts to all supported boards.

## Licensing
Please refer to the LICENSE.md file in this repository for more information regarding licensing.
