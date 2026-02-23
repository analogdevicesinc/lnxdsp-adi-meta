======================
Setting Up Your Host PC
======================

The build system is currently supported on host PCs running **Ubuntu 22.04 LTS 64-bit**.

""""""""""""""""""""""""""""
Installing Required Packages
""""""""""""""""""""""""""""

In order to build and deploy Linux to your ADSP-SC5xx development board you will need to install the following packages on your host PC.

.. shell::

   sudo apt-get update
   sudo apt-get install -y gawk wget git-core diffstat unzip texinfo gcc-multilib build-essential chrpath socat libsdl1.2-dev xterm u-boot-tools openssl curl tftpd-hpa python3 zstd liblz4-tool

"""""""""""""""""
Configure Minicom
"""""""""""""""""

In order to communicate with the U-Boot bootloader, a UART connection must be made between the host PC and the development board. It is recommended that you use minicom to do this. Minicom must be configured to connect to U-Boot correctly.

On the host PC open a terminal and execute the following commands:

.. shell::

   $ sudo apt-get install -y minicom
   $ sudo minicom -s

               +-----[configuration]------+
               | Filenames and paths      |
               | File transfer protocols  |
               | Serial port setup        |
               | Modem and dialing        |
               | Screen and keyboard      |
               | Save setup as dfl        |
               | Save setup as..          |
               | Exit                     |
               | Exit from Minicom        |
               +--------------------------+


   # Select Serial port setup
        Set Serial Device to /dev/ttyUSB0
        Set Bps/Par/Bits to 115200 8N1
        Set Hardware Flow Control to No

        Close the Serial port setup option by press Esc
    Select Save setup as dfl
    Select Exit

.. note::

   ``/dev/ttyUSB0`` might not correspond to the serial port of the board on every system. You can determine which ``/dev`` entry your board uses by running ``ls -l /dev/ttyUSB*`` twice, once when the serial port of the board is plugged in, and once when it isn't.

""""""""""""""""""""""
Configure TFTP Service
""""""""""""""""""""""

A TFTP server on the host is used to transfer images to the development board.
Install and configure.

.. shell::

   sudo vi /etc/default/tftpd-hpa

   #Replace the existing file with the following
   TFTP_USERNAME="tftp"
   TFTP_DIRECTORY="/tftpboot"
   TFTP_ADDRESS="0.0.0.0:69"
   TFTP_OPTIONS="--secure"
   #End of File

.. shell::

   sudo mkdir /tftpboot
   sudo chmod 777 /tftpboot
   sudo systemctl restart tftpd-hpa

""""""""""""""""""""
Configure NFS Server
""""""""""""""""""""

For NFS boot we use the Network File System which is stored in local Ubuntu Host. This is suggested when you do application development. To setup the NFS server:

First, create a directory to store the file system for the target:

.. shell::

   sudo mkdir /romfs/
   sudo chmod 777 /romfs/

Then, install the required package:

.. shell::

   sudo apt-get install nfs-kernel-server
   sudo vi /etc/exports

Add the following line:

.. shell::

   /romfs *(rw,sync,no_root_squash,no_subtree_check)

Start the NFS server:

.. shell::

   sudo systemctl start nfs-kernel-server

We can verify that the NFS service is running by executing:

.. shell::

   sudo systemctl status nfs-kernel-server

The output will indicate that the server is active, i.e.

.. code-block:: text

   ● nfs-server.service - NFS server and services
        Loaded: loaded (/lib/systemd/system/nfs-server.service; enabled; vendor preset: enabled)
       Drop-In: /run/systemd/generator/nfs-server.service.d
                └─order-with-mounts.conf
        Active: active (exited) since Tue 2022-09-06 14:38:31 BST; 3 months 14 days ago
      Main PID: 953 (code=exited, status=0/SUCCESS)
         Tasks: 0 (limit: 18797)
        Memory: 0B
        CGroup: /system.slice/nfs-server.service

   Sep 06 14:38:29 $YOUR_HOSTNAME systemd[1]: Starting NFS server and services...
   Sep 06 14:38:31 $YOUR_HOSTNAME systemd[1]: Finished NFS server and services.

If it's reported as inactive, wait a few moments and check the status again.

""""""""""""""""""""""""""""""""""""""""""""
Configuring USB permissions for ICE debugger
""""""""""""""""""""""""""""""""""""""""""""

In order to allow OpenOCD to use the ICE debugger, we need to provide the user appropriate access via udev.

On the host PC create a group called ``adiusb`` and add the user which will be accessing the ICE debugger to it. In this case we will be adding whichever user is currently logged into the session.

.. shell::

   sudo groupadd adiusb
   sudo usermod -a -G adiusb $USER

We notify udev about permissions to provide this usergroup by adding a rule to it.

.. shell::

   sudo vi /etc/udev/rules.d/adi.rules

Add the following content to the file

.. code-block:: text

   ATTRS{idVendor}=="064b", GROUP="adiusb"
   ATTRS{idVendor}=="0d28", ATTRS{idProduct}=="0204", GROUP="adiusb"
   ATTRS{idVendor}=="03eb", ATTRS{idProduct}=="6124", GROUP="adiusb"

Save and restart the system.
