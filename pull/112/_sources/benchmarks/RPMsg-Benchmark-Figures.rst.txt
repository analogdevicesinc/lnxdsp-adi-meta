RPMsg Benchmark Figures
=======================

Introduction
------------

This page presents performance measurements for RPMsg communication with different channel counts, packet sizes, and transfer sizes. These results are indicative only and can vary significantly between implementations. Use them as a rough guide for performance expectations.

The `Setup`_ section provides examples for configuring benchmark endpoints with various channel, transfer, and packet size configurations. Note that device numbering and firmware filenames may vary by system.

The `Benchmark Results`_ section contains performance tables for multiple configurations.

Source Code
-----------

Source code for rpmsg-examples is available at `https://github.com/analogdevicesinc/rpmsg-examples <https://github.com/analogdevicesinc/rpmsg-examples>`_. Examples are compiled with CCES Studio on Windows and copied to the ADSP-SC598 board.

Setup
-----

Loading Firmware
~~~~~~~~~~~~~~~~

Generated LDR file names:

.. code-block:: text

   rpmsg_echo_example_Core1.ldr
   rpmsg_echo_example_Core2.ldr

Load Core 1 firmware for benchmarking:

.. code-block:: shell

   cd /lib/firmware
   echo stop > /sys/class/remoteproc/remoteproc0/state
   echo rpmsg_echo_example_Core1.ldr > /sys/class/remoteproc/remoteproc0/firmware
   echo start > /sys/class/remoteproc/remoteproc0/state

Binding Channels
~~~~~~~~~~~~~~~~

After firmware is loaded and started, endpoints are created under:

.. code-block:: text

   /sys/bus/rpmsg/devices

Bind 1 channel:

.. code-block:: shell

   rpmsg-bind-chardev -p virtio0.sharc-echo.-1. -n 1 -e 288 -s 100

Bind 8 channels:

.. code-block:: shell

   rpmsg-bind-chardev -p virtio0.sharc-echo.-1. -n 8 -e 288 -s 100

Bind 32 channels:

.. code-block:: shell

   rpmsg-bind-chardev -p virtio0.sharc-echo.-1. -n 32 -e 288 -s 100

Testing Channels
~~~~~~~~~~~~~~~~

Run 1 channel:

.. code-block:: shell

   rpmsg-xmit-p /dev/rpmsg -n 1 -e 0 -t 1000000

Run 8 channels:

.. code-block:: shell

   rpmsg-xmit-p /dev/rpmsg -n 8 -e 0 -t 1000000

Run 32 channels:

.. code-block:: shell

   rpmsg-xmit-p /dev/rpmsg -n 32 -e 0 -t 1000000

Testing Different Transfer Sizes
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Run 10k transfers:

.. code-block:: shell

   rpmsg-xmit-p /dev/rpmsg -n 1 -e 0 -t 10000

Run 100k transfers:

.. code-block:: shell

   rpmsg-xmit-p /dev/rpmsg -n 1 -e 0 -t 100000

Run 1M transfers:

.. code-block:: shell

   rpmsg-xmit-p /dev/rpmsg -n 1 -e 0 -t 1000000

Testing Different Packet Sizes
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Set packet size 1:

.. code-block:: shell

   rpmsg-xmit-p /dev/rpmsg -n 1 -e 0 -t 1000000 -s 1

Set packet size 32:

.. code-block:: shell

   rpmsg-xmit-p /dev/rpmsg -n 1 -e 0 -t 1000000 -s 32

Set packet size 496:

.. code-block:: shell

   rpmsg-xmit-p /dev/rpmsg -n 1 -e 0 -t 1000000 -s 496

Benchmark Results
-----------------

1 Channel
~~~~~~~~~

.. list-table::
   :header-rows: 1
   :widths: 20 25 25

   * - Packet Size (bytes)
     - Transfer Size
     - Speed (bytes/sec)
   * - 1
     - 100
     - 14593
   * - 1
     - 10000
     - 14429
   * - 1
     - 100000
     - 14554
   * - 1
     - 1000000
     - 14521
   * - 1
     - 10000000
     - 14627
   * - 2
     - 100
     - 29262
   * - 2
     - 10000
     - 29015
   * - 2
     - 100000
     - 28722
   * - 2
     - 1000000
     - 28828
   * - 2
     - 10000000
     - 29160
   * - 16
     - 100
     - 244307
   * - 16
     - 10000
     - 233222
   * - 16
     - 100000
     - 227375
   * - 16
     - 1000000
     - 228350
   * - 16
     - 10000000
     - 228385
   * - 64
     - 100
     - 839190
   * - 64
     - 10000
     - 855061
   * - 64
     - 100000
     - 833525
   * - 64
     - 1000000
     - 824475
   * - 64
     - 10000000
     - 821174
   * - 256
     - 100
     - 2611392
   * - 256
     - 10000
     - 2498536
   * - 256
     - 100000
     - 2314672
   * - 256
     - 1000000
     - 2290975
   * - 256
     - 10000000
     - 2278242
   * - 496
     - 100
     - 4598496
   * - 496
     - 10000
     - 4904841
   * - 496
     - 100000
     - 5318444
   * - 496
     - 1000000
     - 5358093
   * - 496
     - 10000000
     - 5402480

8 Channels
~~~~~~~~~~

.. list-table::
   :header-rows: 1
   :widths: 20 25 25 25

   * - Packet Size (bytes)
     - Transfer Size
     - Single Channel Speed (bytes/sec)
     - Average Channel Speed (bytes/sec)
   * - 1
     - 100
     - 15383
     - 1923
   * - 1
     - 10000
     - 15379
     - 1922
   * - 1
     - 100000
     - 15325
     - 1916
   * - 2
     - 100
     - 18300
     - 2288
   * - 2
     - 10000
     - 28289
     - 3536
   * - 2
     - 100000
     - 29352
     - 3669
   * - 2
     - 1000000
     - 29794
     - 3724
   * - 16
     - 100
     - 251359
     - 31420
   * - 16
     - 10000
     - 232645
     - 29081
   * - 16
     - 100000
     - 237663
     - 29708
   * - 16
     - 1000000
     - 245774
     - 30722
   * - 16
     - 10000000
     - 236035
     - 29504
   * - 64
     - 100
     - 1514642
     - 189330
   * - 64
     - 10000
     - 1061882
     - 132735
   * - 64
     - 100000
     - 1010483
     - 126310
   * - 64
     - 1000000
     - 957566
     - 119696
   * - 64
     - 10000000
     - 997557
     - 124695
   * - 256
     - 100
     - 5835056
     - 729382
   * - 256
     - 10000
     - 4184189
     - 523024
   * - 256
     - 100000
     - 3407874
     - 425984
   * - 256
     - 1000000
     - 3532256
     - 441532
   * - 256
     - 10000000
     - 2505826
     - 313228
   * - 496
     - 100
     - 12892509
     - 1611564
   * - 496
     - 10000
     - 8742713
     - 1092839
   * - 496
     - 100000
     - 7078932
     - 884867
   * - 496
     - 1000000
     - 7143015
     - 892877
   * - 496
     - 10000000
     - 7184777
     - 898097

32 Channels
~~~~~~~~~~~

.. list-table::
   :header-rows: 1
   :widths: 20 25 25 25

   * - Packet Size (bytes)
     - Transfer Size
     - Single Channel Speed (bytes/sec)
     - Average Channel Speed (bytes/sec)
   * - 1
     - 100
     - 12464
     - 390
   * - 1
     - 10000
     - 12790
     - 400
   * - 1
     - 100000
     - 12775
     - 399
   * - 2
     - 100
     - 26479
     - 827
   * - 2
     - 10000
     - 25364
     - 793
   * - 2
     - 100000
     - 25339
     - 792
   * - 16
     - 100
     - 351299
     - 10978
   * - 16
     - 10000
     - 205900
     - 6434
   * - 16
     - 100000
     - 197167
     - 6161
   * - 64
     - 100
     - 13584233
     - 424507
   * - 64
     - 10000
     - 877851
     - 27433
   * - 64
     - 100000
     - 753868
     - 23558
   * - 64
     - 1000000
     - 742513
     - 23204
   * - 256
     - 100
     - 23018872
     - 719340
   * - 256
     - 10000
     - 8198836
     - 256214
   * - 256
     - 100000
     - 2925997
     - 91437
   * - 256
     - 1000000
     - 2768895
     - 86528
   * - 256
     - 10000000
     - 25052788426826
     - 87138
   * - 496
     - 100
     - 44478888
     - 1389965
   * - 496
     - 10000
     - 33994508
     - 1062328
   * - 496
     - 100000
     - 6907303
     - 215853
   * - 496
     - 1000000
     - 6170902
     - 192841
   * - 496
     - 10000000
     - 6076338
     - 189886

Key Observations
~~~~~~~~~~~~~~~~

1. **Single Channel Performance**: For 1 channel with 1-byte packets, maximum throughput is 12-15k packets/second. Bandwidth scales linearly up to the maximum packet size of 496 bytes.

2. **Multi-Channel Behavior**: All multi-channel benchmarks follow a similar pattern - total bandwidth remains relatively constant while per-channel bandwidth decreases proportionally as channels are added.

3. **RPMsg-Lite Configuration**: No tuning was performed on RPMsg-Lite for these benchmarks.

4. **Memory Performance Impact**: Moving VRing to DDR reduces performance by approximately 10-20%.

Performance Visualization
-------------------------

Single Channel Bandwidth
~~~~~~~~~~~~~~~~~~~~~~~~

Bandwidth per channel for different total channel counts:

.. figure:: https://github.com/analogdevicesinc/lnxdsp-adi-meta/assets/110021710/d432a42b-484d-4f7c-b984-aa0ddca149a0
   :alt: Single Channel Bandwidth Performance

   Bandwidth per channel vs. total number of channels

Total Bandwidth
~~~~~~~~~~~~~~~

Total bandwidth across all channels for different channel counts:

.. figure:: https://github.com/analogdevicesinc/lnxdsp-adi-meta/assets/110021710/11f72b73-7b00-42ac-ab4e-6b98eeed5195
   :alt: Total Bandwidth Performance

   Total bandwidth vs. number of channels
