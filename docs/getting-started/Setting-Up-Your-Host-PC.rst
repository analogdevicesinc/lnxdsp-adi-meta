Setting Up Your Host PC
=======================

The build system is currently supported on host PCs running Ubuntu 22.04 (LTS)
(see `Containerfile#L1
<https://github.com/analogdevicesinc/lnxdsp-adi-meta/blob/main/ci/Containerfile#L1>`_).

Installing Required Packages
----------------------------

In order to build and deploy Linux to your ADSP-SC5xx development board you will need to install the following packages on your host PC.

.. code-block:: shell

   sudo apt-get update
   sudo apt-get install -y gawk wget git-core diffstat unzip texinfo gcc-multilib build-essential chrpath socat libsdl1.2-dev xterm u-boot-tools openssl curl tftpd-hpa python3 zstd liblz4-tool

Configure Minicom
-----------------

In order to communicate with the U-Boot bootloader, a UART connection must be made between the host PC and the development board. It is recommended that you use minicom to do this. Minicom must be configured to connect to U-Boot correctly.

On the host PC open a terminal and execute the following commands:

.. code-block:: shell

   sudo apt-get install -y minicom
   sudo minicom -s

.. code-block:: text

   The minicom menu will appear as follows:

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

Configure TFTP Service
----------------------

A TFTP server on the host is used to transfer images to the development board.
Install and configure.

.. code-block:: shell

   sudo vi /etc/default/tftpd-hpa

.. code-block:: text

   #Replace the existing file with the following
   TFTP_USERNAME="tftp"
   TFTP_DIRECTORY="/tftpboot"
   TFTP_ADDRESS="0.0.0.0:69"
   TFTP_OPTIONS="--secure"
   #End of File

.. code-block:: shell

   sudo mkdir /tftpboot
   sudo chmod 777 /tftpboot
   sudo systemctl restart tftpd-hpa

Configure NFS Server
--------------------

For NFS boot we use the Network File System which is stored in local Ubuntu Host. This is suggested when you do application development. To setup the NFS server:

First, create a directory to store the file system for the target:

.. code-block:: shell

   sudo mkdir /romfs/
   sudo chmod 777 /romfs/

Then, install the required package:

.. code-block:: shell

   sudo apt-get install nfs-kernel-server
   sudo vi /etc/exports

Add the following line:

.. code-block:: shell

   /romfs *(rw,sync,no_root_squash,no_subtree_check)

Start the NFS server:

.. code-block:: shell

   sudo systemctl start nfs-kernel-server

We can verify that the NFS service is running by executing:

.. code-block:: shell

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

Configure DHCP Server
---------------------

A DHCP server on the host lets the development board get an IP address
automatically, instead of you setting ``ipaddr`` and ``serverip`` by hand in
U-Boot after every reset.

The steps below use the following example configuration:

=========================== ==================================
Setting                     Value
=========================== ==================================
Host interface to the board ``enxa0cec8e24382`` (USB Ethernet)
Host IP address             ``192.168.1.30/24``
Board subnet                ``192.168.1.0/24``
DHCP address pool           ``192.168.1.150`` – ``192.168.1.200``
TFTP server (next-server)   ``192.168.1.30``
=========================== ==================================

First, find the name of the interface that connects to the board:

.. code-block:: shell

   ip -br link

Give that interface a static IP address. Create a netplan file for it:

.. code-block:: shell

   sudo vi /etc/netplan/02-board-net.yaml

Add the following content. Replace ``enxa0cec8e24382`` with your interface
name:

.. code-block:: yaml

   network:
     version: 2
     renderer: NetworkManager
     ethernets:
       enxa0cec8e24382:
         addresses: [192.168.1.30/24]
         dhcp4: no

Apply the configuration:

.. code-block:: shell

   sudo chmod 600 /etc/netplan/02-board-net.yaml
   sudo netplan apply

Next, install the DHCP server:

.. code-block:: shell

   sudo apt-get install -y isc-dhcp-server

Configure the server to listen only on the interface connected to the board:

.. code-block:: shell

   sudo vi /etc/default/isc-dhcp-server

.. code-block:: text

   #Replace the existing file with the following
   INTERFACESv4="enxa0cec8e24382"
   INTERFACESv6=""
   #End of File

Configure the subnet and address pool:

.. code-block:: shell

   sudo vi /etc/dhcp/dhcpd.conf

.. code-block:: text

   #Replace the existing file with the following
   default-lease-time 600;
   max-lease-time 7200;
   ddns-update-style none;
   authoritative;

   subnet 192.168.1.0 netmask 255.255.255.0 {
       range 192.168.1.150 192.168.1.200;
       option routers 192.168.1.30;
       option broadcast-address 192.168.1.255;
       next-server 192.168.1.30;
   }
   #End of File

Enable and restart the DHCP server:

.. code-block:: shell

   sudo systemctl enable isc-dhcp-server
   sudo systemctl restart isc-dhcp-server

To check that the DHCP server is running, execute:

.. code-block:: shell

   sudo systemctl status isc-dhcp-server

The output should show that the server is ``active (running)``.

.. code-block:: shell

   sudo journalctl -u isc-dhcp-server

.. note::

   ``isc-dhcp-server`` also installs an IPv6 service called
   ``isc-dhcp-server6``. This service fails to start because no IPv6
   interfaces are configured. You can ignore this failure, or disable the
   service with ``sudo systemctl disable --now isc-dhcp-server6``.

In U-Boot, run ``dhcp`` to get an address from the server. 

Configure the Firewall
----------------------

If the host PC runs a firewall, such as ``ufw``, it must allow incoming
connections from the board to the services configured above. The
following table lists the ports that each boot type needs on the host:

.. list-table::
   :header-rows: 1
   :widths: 30 25 45

   * - Boot type
     - Host ports
     - Used for
   * - ``nfsboot`` (also used to program the boot media)
     - 67/udp
     - DHCP. Only needed if you use ``dhcp`` in U-Boot.
   * -
     - 8000/tcp
     - Python web server. U-Boot downloads the ``fitImage`` with ``wget``.
   * -
     - 111/tcp
     - rpcbind. The kernel uses it to look up the NFS ports.
   * -
     - 2049/tcp
     - NFS server (``nfsd``).
   * -
     - 20048/tcp
     - NFS mount daemon (``rpc.mountd``). Requires a fixed port, see below.
   * - ``ramboot``
     - 67/udp, 8000/tcp
     - DHCP (optional) and the Python web server, as for ``nfsboot``.


The simplest setup trusts all traffic that arrives on the network interface
connected to the board. This covers every boot type. Replace
``enxa0cec8e24382`` with your interface name:

.. code-block:: shell

   sudo ufw allow in on enxa0cec8e24382 to any port 67 proto udp
   sudo ufw allow in on enxa0cec8e24382 from 192.168.1.0/24

The DHCP rule is separate because a board that doesn't have an address yet
sends its DHCP request from ``0.0.0.0``.

To open only the ports listed in the table, first give ``rpc.mountd`` a
fixed port. Otherwise it picks a random port each time it starts. Edit the
NFS configuration:

.. code-block:: shell

   sudo vi /etc/nfs.conf

In the ``[mountd]`` section, set:

.. code-block:: text

   [mountd]
   port=20048

Restart the NFS server and check that ``mountd`` now uses port 20048:

.. code-block:: shell

   sudo systemctl restart nfs-kernel-server
   rpcinfo -p | grep mountd

Then add a rule for each port:

.. code-block:: shell

   sudo ufw allow in on enxa0cec8e24382 to any port 67 proto udp
   sudo ufw allow in on enxa0cec8e24382 from 192.168.1.0/24 to any port 69 proto udp
   sudo ufw allow in on enxa0cec8e24382 from 192.168.1.0/24 to any port 111 proto tcp
   sudo ufw allow in on enxa0cec8e24382 from 192.168.1.0/24 to any port 2049 proto tcp
   sudo ufw allow in on enxa0cec8e24382 from 192.168.1.0/24 to any port 8000 proto tcp
   sudo ufw allow in on enxa0cec8e24382 from 192.168.1.0/24 to any port 20048 proto tcp

Check the active rules:

.. code-block:: shell

   sudo ufw status verbose

.. note::

   If the board connects to the host over your office or corporate LAN, not a
   dedicated interface, leave out ``in on enxa0cec8e24382``. Replace
   ``192.168.1.0/24`` with the board's subnet, or with the board's IP
   address.

Configuring USB permissions for ICE debugger
--------------------------------------------

In order to allow OpenOCD to use the ICE debugger, we need to provide the user appropriate access via udev.

On the host PC create a group called ``adiusb`` and add the user which will be accessing the ICE debugger to it. In this case we will be adding whichever user is currently logged into the session.

.. code-block:: shell

   sudo groupadd adiusb
   sudo usermod -a -G adiusb $USER

We notify udev about permissions to provide this usergroup by adding a rule to it.

.. code-block:: shell

   sudo vi /etc/udev/rules.d/adi.rules

Add the following content to the file

.. code-block:: text

   ATTRS{idVendor}=="064b", GROUP="adiusb"
   ATTRS{idVendor}=="0d28", ATTRS{idProduct}=="0204", GROUP="adiusb"
   ATTRS{idVendor}=="03eb", ATTRS{idProduct}=="6124", GROUP="adiusb"

Save and restart the system.
