# Introduction

This page contains information about using and working with the U-Boot environment.

# About the Environment

The U-Boot envionrment is a text-based key-value store within U-Boot, used to store information such as board-specific commands, run-time configuration options, network information and settings, filesystem settings and more.

Some Examples include:
- The board's IP address - can be set manually or by DHCP
- Names of files used to boot the system - such as a fitimage, jffs2 filesystem, etc.
- Commands to update various parts of the system, such as U-Boot, the fitImage, linux's rootfs, etc.
- Autoboot options, such as the boot delay and key used to stop autoboot

By default (on ADSP boards), a persistent copy of the environment is stored in the SPI Flash. This is loaded automatically on boot to populate the in-memory environment. If no valid environment is present in the flash - e.g. if the flash has been erased or corrupted - a default environment is loaded instead.

More information on the environment, and many of the environment variables used by U-Boot, see [Environment Variables](https://docs.u-boot.org/en/latest/usage/environment.html) in the U-Boot docs.

# Interacting with the Environment

## `env` command

U-Boot provides the `env` command to interact with the environment. The basic commands are:

- `env set <name> [value]` (alias `setenv`) - set value `name` to `value`, or deletes `name` if `value` is not present.

- `env print [name]` (alias `printenv`) - print value `name`, or the whole environment if `name` is not present.

- `env default (-a | name ...)` - set one of more values specified by `name` to their default values, or reset the entire environment if `-a` specified. 
    
    _(Note: only modifies the in-memory copy - also run `saveenv` to reset the persistent copy)_

- `env load` and `env save` (alias `saveenv`) - load/save the environment from persistent storage - uses the SPI Flash on ADSP boards.

For a full command listing, see [env command](https://docs.u-boot.org/en/latest/usage/cmd/env.html) in the U-Boot docs.

The `edit <name>` command is also provided to interactively edit the contents of environment variable `name`. 

## Using Environment Variables

There are two main ways to use environment variables:
- The `run` command will execute the contents of a variable. For example, the `update_spi` command is stored as an environment variable, so is executed using `run update_spi`.
- Using curly-brace notation, similarly to Unix shells. For example, `tftpboot ${tftpserverip}` will boot using the IP stored in `tftpserverip` as the server address. 

# The Persistent Environment

By default, the environment is stored on the SPI Flash on ADSP boards. The default location varies by board:

| Boards | Location |
|-|-:|
| SC573, SC584, SC589 | `0xD0000` |
| SC594, SC598-ezlite | `0x100000` |
| SC598-ezkit | `0x180000` |

This is controlled by the config option `CONFIG_ENV_OFFSET`, usually set in the defconfig file for the relevant board (e.g. `configs/sc598-som-ezkit-spl_defconfig` for the SC598, running without Falcon enabled). Modifying the `CONFIG_ENV_OFFSET=...` line in the relevant file will change where it is stored.

**BEWARE** - the persistent environment is not updated when flashing U-Boot. Any changes previously made to the environment, either manually or by a U-Boot configuration option, may persist after updating U-Boot and cause unexpected behaviour. It is therefore recommended to reset the environment to the default after changing U-Boot configuration options, using:

```
=> env default -f -a
=> env save
```

# The Default Environment

As previously described, when U-Boot is unable to load a valid environment from the persistent storage, it will load the default environment. This is defined by a number of configuration options, within the `include/configs` directory in U-Boot's source. 

Some common values are defined under `CFG_EXTRA_ENV_SETTINGS` in [sc_adi_common.h](https://github.com/analogdevicesinc/lnxdsp-u-boot/blob/main/include/configs/sc_adi_common.h#L201), with board specific options being defined with `ADI_ENV_SETTINGS` in the relevant board file - for example, for SC598, in [sc598-som.h](https://github.com/analogdevicesinc/lnxdsp-u-boot/blob/main/include/configs/sc598-som.h#L141).

The default environment can also be manually loaded using the `env default` command, described in [`env` command](#env-command).