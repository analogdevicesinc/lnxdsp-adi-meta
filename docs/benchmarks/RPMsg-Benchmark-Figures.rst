=====
Intro
=====

The present page aims to measure the performance of RPMsg for different number of channels and packet and transfer sizes. This is indicative only and it can vary a lot from implementation to implementation, so use it only as a rough guide.

On the `Setup`_ section, there's examples on how to set up the benchmark and its endpoints, for different number of channels, transfer and packet sizes. Note that this can also vary in terms of device numbering, firmware filenames etc.

On the `Benchmarks`_ section, the results table is present for a number of different configurations.

Source code
===========

Source code for rpmsg-examples located at `https://github.com/analogdevicesinc/rpmsg-examples <https://github.com/analogdevicesinc/rpmsg-examples>`_, examples are compiled with CCES studio on Windows and copied over to
the ADSP-SC598 board.

Setup
=====

Setting up the endpoints
------------------------

Load firmware to SHARC(s)
~~~~~~~~~~~~~~~~~~~~~~~~~~

Generated file names of ldr files are:

.. code-block:: text

   rpmsg_echo_example_Core1.ldr
   rpmsg_echo_example_Core2.ldr

Load Core 1 firmware for benchmarking

.. code-block:: shell

   cd /lib/firmware
   echo stop > /sys/class/remoteproc/remoteproc0/state
   echo rpmsg_echo_example_Core1.ldr > /sys/class/remoteproc/remoteproc0/firmware
   echo start > /sys/class/remoteproc/remoteproc0/state

Bind channels
~~~~~~~~~~~~~

After firmware loaded and starter, there will be created endpoints under the:

.. code-block:: text

   /sys/bus/rpmsg/devices

Bind 1 channel

.. code-block:: shell

   rpmsg-bind-chardev -p virtio0.sharc-echo.-1. -n 1 -e 288 -s 100

Bind 8 channels

.. code-block:: shell

   rpmsg-bind-chardev -p virtio0.sharc-echo.-1. -n 8 -e 288 -s 100

Bind 32 channels

.. code-block:: shell

   rpmsg-bind-chardev -p virtio0.sharc-echo.-1. -n 32 -e 288 -s 100

Test channel(s)
~~~~~~~~~~~~~~~

Run 1 channel

.. code-block:: shell

   rpmsg-xmit-p /dev/rpmsg -n 1 -e 0 -t 1000000

Run 8 channels

.. code-block:: shell

   rpmsg-xmit-p /dev/rpmsg -n 8 -e 0 -t 1000000

Run 32 channels

.. code-block:: shell

   rpmsg-xmit-p /dev/rpmsg -n 32 -e 0 -t 1000000

Test different transfer sizes
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Run 10k transfers

.. code-block:: shell

   rpmsg-xmit-p /dev/rpmsg -n 1 -e 0 -t 10000

Run 100k transfers

.. code-block:: shell

   rpmsg-xmit-p /dev/rpmsg -n 1 -e 0 -t 100000

Run 1M transfers

.. code-block:: shell

   rpmsg-xmit-p /dev/rpmsg -n 1 -e 0 -t 1000000

Test different packet sizes
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

Set packet size 1

.. code-block:: shell

   rpmsg-xmit-p /dev/rpmsg -n 1 -e 0 -t 1000000 -s 1

Set packet size 32

.. code-block:: shell

   rpmsg-xmit-p /dev/rpmsg -n 1 -e 0 -t 1000000 -s 32

Set packet size 496

.. code-block:: shell

   rpmsg-xmit-p /dev/rpmsg -n 1 -e 0 -t 1000000 -s 496

Benchmarks
==========

1 Channel
---------

.. list-table::
   :header-rows: 1

   * - PACKET SIZE
     - TRANSFER SIZE
     - Single CH speed
   * - 1
     - 100
     - 14593
   * -
     - 10000
     - 14429
   * -
     - 100000
     - 14554
   * -
     - 1000000
     - 14521
   * -
     - 10000000
     - 14627
   * - 2
     - 100
     - 29262
   * -
     - 10000
     - 29015
   * -
     - 100000
     - 28722
   * -
     - 1000000
     - 28828
   * -
     - 10000000
     - 29160
   * - 16
     - 100
     - 244307
   * -
     - 10000
     - 233222
   * -
     - 100000
     - 227375
   * -
     - 1000000
     - 228350
   * -
     - 10000000
     - 228385
   * - 64
     - 100
     - 839190
   * -
     - 10000
     - 855061
   * -
     - 100000
     - 833525
   * -
     - 1000000
     - 824475
   * -
     - 10000000
     - 821174
   * - 256
     - 100
     - 2611392
   * -
     - 10000
     - 2498536
   * -
     - 100000
     - 2314672
   * -
     - 1000000
     - 2290975
   * -
     - 10000000
     - 2278242
   * - 496
     - 100
     - 4598496
   * -
     - 10000
     - 4904841
   * -
     - 100000
     - 5318444
   * -
     - 1000000
     - 5358093
   * -
     - 10000000
     - 5402480

8 Channels
----------

.. list-table::
   :header-rows: 1

   * - PACKET SIZE
     - TRANSFER SIZE
     - Single CH speed
     - Average CH Speed
   * - 1
     - 100
     - 15383
     - 1923
   * -
     - 10000
     - 15379
     - 1922
   * -
     - 100000
     - 15325
     - 1916
   * - 2
     - 100
     - 18300
     - 2288
   * -
     - 10000
     - 28289
     - 3536
   * -
     - 100000
     - 29352
     - 3669
   * -
     - 1000000
     - 29794
     - 3724
   * - 16
     - 100
     - 251359
     - 31420
   * -
     - 10000
     - 232645
     - 29081
   * -
     - 100000
     - 237663
     - 29708
   * -
     - 1000000
     - 245774
     - 30722
   * -
     - 10000000
     - 236035
     - 29504
   * - 64
     - 100
     - 1514642
     - 189330
   * -
     - 10000
     - 1061882
     - 132735
   * -
     - 100000
     - 1010483
     - 126310
   * -
     - 1000000
     - 957566
     - 119696
   * -
     - 10000000
     - 997557
     - 124695
   * - 256
     - 100
     - 5835056
     - 729382
   * -
     - 10000
     - 4184189
     - 523024
   * -
     - 100000
     - 3407874
     - 425984
   * -
     - 1000000
     - 3532256
     - 441532
   * -
     - 10000000
     - 2505826
     - 313228
   * - 496
     - 100
     - 12892509
     - 1611564
   * -
     - 10000
     - 8742713
     - 1092839
   * -
     - 100000
     - 7078932
     - 884867
   * -
     - 1000000
     - 7143015
     - 892877
   * -
     - 10000000
     - 7184777
     - 898097

32 Channels
-----------

.. list-table::
   :header-rows: 1

   * - PACKET SIZE
     - TRANSFER SIZE
     - Single CH speed
     - Average CH Speed
   * - 1
     - 100
     - 12464
     - 390
   * -
     - 10000
     - 12790
     - 400
   * -
     - 100000
     - 12775
     - 399
   * - 2
     - 100
     - 26479
     - 827
   * -
     - 10000
     - 25364
     - 793
   * -
     - 100000
     - 25339
     - 792
   * - 16
     - 100
     - 351299
     - 10978
   * -
     - 10000
     - 205900
     - 6434
   * -
     - 100000
     - 197167
     - 6161
   * - 64
     - 100
     - 13584233
     - 424507
   * -
     - 10000
     - 877851
     - 27433
   * -
     - 100000
     - 753868
     - 23558
   * -
     - 1000000
     - 742513
     - 23204
   * - 256
     - 100
     - 23018872
     - 719340
   * -
     - 10000
     - 8198836
     - 256214
   * -
     - 100000
     - 2925997
     - 91437
   * -
     - 1000000
     - 2768895
     - 86528
   * -
     - 10000000
     - 25052788426826
     - 87138
   * - 496
     - 100
     - 44478888
     - 1389965
   * -
     - 10000
     - 33994508
     - 1062328
   * -
     - 100000
     - 6907303
     - 215853
   * -
     - 1000000
     - 6170902
     - 192841
   * -
     - 10000000
     - 6076338
     - 189886

Notes
~~~~~

1. For channels=1 & packet size=1, it's easy to observer that the maximum number of packets per second is 12-15k, and the bandwidth linearly follows the increase to the maximum 496 bytes. All other (multi-channel) benchmark numbers seem to follow the same pattern, i.e. the total bandwidth stays similar, and the bandwidth per channel drops as channels are added.

2. No changes were made to tune RPMsg-lite

3. Moving Vring  to DDR reduces the performance by approximately 10-20%

Benchmark Plots
===============

Single Channel Performance
--------------------------

Bandwidth per channel for different number of total channels

.. figure:: https://github.com/analogdevicesinc/lnxdsp-adi-meta/assets/110021710/d432a42b-484d-4f7c-b984-aa0ddca149a0
   :alt: image-2023-9-12_15-4-3-1

Total Bandwidth
---------------

Total bandwidth for different number of total channels

.. figure:: https://github.com/analogdevicesinc/lnxdsp-adi-meta/assets/110021710/11f72b73-7b00-42ac-ab4e-6b98eeed5195
   :alt: image-2023-9-12_15-14-9-1