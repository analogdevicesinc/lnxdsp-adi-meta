Running Tests
=============

Introduction
------------

The ``adsp-sc5xx-test`` image adds a set of test suites and tools that run directly on the
board. This page shows how to build the image and how to run each suite, along with the most
useful options. See :doc:`Image-Types` for the full package list.

The test image uses the same kernel configuration as the other images, so it can be used to
check a stock build without any changes.

.. list-table::
   :widths: 25 30 45
   :header-rows: 1

   * - Suite
     - Command
     - What it covers
   * - ptest suites
     - ``ptest-runner``
     - Upstream regression tests for core packages (see `Included ptest Suites`_)
   * - rt-tests ptest
     - ``ptest-runner rt-tests``
     - ``cyclictest`` scheduling latency under different ``stress-ng`` loads
   * - ADI CRC ptest
     - ``ptest-runner crc-tests``
     - ADI CRC hardware driver (see `ADI Crypto ptests`_)
   * - ADI crypto ptest (ADSP-SC598 SOM EZ-KIT)
     - ``ptest-runner crypto-tests``
     - ADI hash and cipher hardware drivers (see `ADI Crypto ptests`_)
   * - ADI board sanity ptest
     - ``ptest-runner adsp-sanity-tests``
     - gptimers, RPMsg to the SHARC cores, I2C, ALSA, Ethernet and PTP (see
       `ADI Board Sanity ptest`_)
   * - ADI driver ptests
     - ``ptest-runner adsp-dmacopy-test adsp-gptimer-test adsp-sram-mmap-test``
     - Memory DMA, gptimers and SRAM mapping (see `ADI Driver ptests`_)
   * - ADI SRAM allocation ptest (not ADSP-SC598 or ADSP-SC846)
     - ``ptest-runner adsp-l2-alloc-test``
     - Allocating from the on-chip SRAM pools (see `ADI Driver ptests`_)
   * - stress-ng
     - ``stress-ng``
     - CPU, memory, I/O and scheduler stress with result verification
   * - memtester
     - ``memtester``
     - User-space DDR memory test
   * - fio
     - ``fio``
     - Storage throughput, latency and data integrity
   * - rngtest
     - ``rngtest``
     - FIPS 140-2 randomness checks on an entropy source
   * - OP-TEE xtest (``adi-security`` only)
     - ``xtest`` / ``ptest-runner optee-test``
     - OP-TEE client API, trusted applications, secure storage and crypto
   * - Mbed TLS ptest (``adi-security`` only)
     - ``ptest-runner mbedtls``
     - Mbed TLS library test suites
   * - TEE smoke test (``adi-security`` only)
     - ``ptest-runner tee-smoke``
     - Linux-visible checks of the TF-A (BL31) and OP-TEE stack

Building the Test Image
-----------------------

The test suites are packaged with Yocto's ``ptest`` feature, which is enabled by default in
the ADI distros. Build the image the same way as any other image:

.. code-block:: shell

   KAS_MACHINE=adsp-sc598-som-ezkit kas shell config/kas/adsp-sc5xx.yml
   bitbake adsp-sc5xx-test

To include the OP-TEE, Mbed TLS and TEE smoke tests, build with the ``adi-security`` distro
(ADSP-SC598 only). See :doc:`../secure-boot/Secure-Boot-Support` for the required signing
keys:

.. code-block:: shell

   KAS_MACHINE=adsp-sc598-som-ezkit KAS_DISTRO=adi-security kas shell config/kas/adsp-sc5xx.yml
   bitbake adsp-sc5xx-test

Deploy the resulting ``adsp-sc5xx-test`` rootfs in the same way as the full image.

Test Data and Cleanup
~~~~~~~~~~~~~~~~~~~~~

The ``openssh``, ``libseccomp``, ``rt-tests`` and ``util-linux`` suites write logs, keys and
extracted test data into their ``/usr/lib/<suite>/ptest`` directories. The ADI layer wraps
each of these suites' ``run-ptest`` so that anything not shipped in the package is deleted
before and after every run, whether the suite passes, fails or is interrupted (for example
by pressing Ctrl+C in ``ptest-runner``). A suite that ``ptest-runner`` stops for a timeout is
killed with ``SIGKILL`` before it can clean up, so its files, and anything it left mounted in
the ptest directory, are removed at the start of the next run instead. The list of shipped
files is recorded at build time in ``/usr/lib/<suite>/ptest/.ptest-pristine``; files in
``.debug`` directories, which belong to the ``-dbg`` packages, are never removed.

To add the same cleanup to another ptest package, inherit the class from a ``.bbappend``
in your layer:

.. code-block:: bitbake

   inherit adsp-ptest-cleanup

ptest-runner
------------

``ptest-runner`` finds every installed ``run-ptest`` script under ``/usr/lib/*/ptest/`` and
runs it, collecting the ``PASS:``, ``FAIL:`` and ``SKIP:`` lines that each suite prints.

.. code-block:: shell

   # List the installed suites
   ptest-runner -l

   # Run every installed suite
   ptest-runner

   # Run only the named suites
   ptest-runner rt-tests

   # Run everything except one suite
   ptest-runner -e rt-tests

   # Allow each suite up to 15 minutes without output before it is stopped
   ptest-runner -t 900 rt-tests

   # Write a JUnit-style XML report for CI
   ptest-runner -x /tmp/ptest-results.xml

.. list-table::
   :widths: 25 75
   :header-rows: 1

   * - Option
     - Description
   * - ``-l``
     - List the available suites and exit.
   * - ``-d DIRS``
     - Search the given directories for ``*/ptest/run-ptest`` instead of ``/usr/lib``.
   * - ``-e NAMES``
     - Exclude the named suites (space separated).
   * - ``-t SECONDS``
     - Stop a suite if it produces no output for this long (default 300).
   * - ``-x FILE``
     - Write the results to an XML file (one test case per suite, with its duration).

Each suite's results are wrapped in ``BEGIN:`` / ``END:`` markers with a ``DURATION:`` line.
The final ``TOTAL: <n> FAIL: <m>`` line counts suites that exited with a non-zero status,
were killed by a signal or timed out. A suite can exit with status ``0`` even when some of
its test cases fail, and the XML report only records one entry per suite, so check for
individual ``FAIL:`` lines to find failing test cases:

.. code-block:: shell

   ptest-runner 2>&1 | tee /tmp/ptest.log
   grep '^FAIL:' /tmp/ptest.log

Included ptest Suites
~~~~~~~~~~~~~~~~~~~~~

.. list-table::
   :widths: 20 55
   :header-rows: 1

   * - Suite
     - What it covers
   * - ``adsp-dmacopy-test``
     - Memory DMA copies between SDRAM and SRAM, see `ADI Driver ptests`_
   * - ``adsp-gptimer-test``
     - A spare gptimer counts at the gptimer clock rate, see `ADI Driver ptests`_
   * - ``adsp-l2-alloc-test``
     - Allocating from the on-chip SRAM pools (not ADSP-SC598 or ADSP-SC846), see
       `ADI Driver ptests`_
   * - ``adsp-sanity-tests``
     - Board-level checks of the gptimers, RPMsg, I2C, ALSA, Ethernet and PTP, see
       `ADI Board Sanity ptest`_
   * - ``adsp-sram-mmap-test``
     - Mapping on-chip SRAM into user space, see `ADI Driver ptests`_
   * - ``busybox``
     - BusyBox applets used by the base system
   * - ``crc-tests``
     - ADI CRC hardware, see `ADI Crypto ptests`_
   * - ``crypto-tests``
     - ADI hash and cipher hardware, see `ADI Crypto ptests`_ (ADSP-SC598 SOM EZ-KIT only)
   * - ``libseccomp``
     - Seccomp syscall filtering, including live filters on the running kernel
   * - ``openssh``
     - ``ssh``, ``sshd``, ``scp``, ``sftp`` and key handling, see `openssh ptest`_
   * - ``rt-tests``
     - Real-time latency, see `rt-tests ptest`_
   * - ``util-linux``
     - ``mount``, ``fdisk``, ``lsblk``, ``hexdump``, ``lscpu`` and the other util-linux
       tools, see `util-linux ptest`_

Other ptest packages can be added to the image with ``IMAGE_INSTALL``, for example
``IMAGE_INSTALL:append = " xz-ptest"``.

util-linux ptest
~~~~~~~~~~~~~~~~

The ADI layer removes from ``util-linux-ptest`` the tests that cannot pass on the ADSP-SC5xx
boards, mostly because they need kernel options the ADI configurations do not enable, or
hardware such as an RTC. The list of tests is held in ``UTIL_LINUX_PTEST_EXCLUDE`` and can be
overridden, for example in ``local.conf``:

.. code-block:: bitbake

   # Run the hwclock tests on a board with an RTC driver enabled
   UTIL_LINUX_PTEST_EXCLUDE:remove:pn-util-linux = "hwclock/show"

Excluded tests are not installed. To run selected util-linux components, call the test
script directly:

.. code-block:: shell

   cd /usr/lib/util-linux/ptest
   ./tests/run.sh --use-system-commands --parsable lsblk fdisk

On boards that reserve DDR for the SHARC cores the full suite runs in about 200 MB of
memory; avoid running other memory-heavy workloads at the same time.

openssh ptest
~~~~~~~~~~~~~

Runs the OpenSSH regression tests against a local ``sshd`` started by the suite. The ADI
layer skips the ``dynamic-forward`` test (SOCKS dynamic port forwarding, ``ssh -D``), which
fails on the ADSP-SC598. Skipped tests are reported as ``skip test <name>.sh`` in the log
rather than as ``SKIP:`` lines. The list is held in ``OPENSSH_PTEST_SKIP`` (test names
without ``.sh``) and can be changed in ``local.conf``:

.. code-block:: bitbake

   # Also skip the static port forwarding test
   OPENSSH_PTEST_SKIP:append:pn-openssh = " forwarding"

To run selected tests directly, use the regress ``Makefile`` with ``LTESTS``:

.. code-block:: shell

   cd /usr/lib/openssh/ptest/regress
   export TEST_SSH_SSH=ssh TEST_SHELL=sh SKIP_UNIT=1
   make BUILDDIR=$PWD/.. .OBJDIR=$PWD .CURDIR=$PWD SUDO="" LTESTS="connect keyscan" t-exec

rt-tests ptest
~~~~~~~~~~~~~~

Runs ``cyclictest`` six times: with no load and then with ``stress-ng`` CPU, disk, I/O,
virtual-memory and combined loads. Each run prints the minimum, average and maximum wakeup
latency in microseconds. A ``PASS`` means the measurement completed; the suite does not
apply a latency limit, so compare the reported maximum against your own requirement. The
suite takes about 10 minutes on the ADSP-SC598.

.. code-block:: shell

   ptest-runner -t 900 rt-tests

Example output for one load case:

.. code-block:: text

   #   Test case (6/6): rt_bmark.intlat.full
   ...
   # Min: 3 us
   # Avg: 21.8 us
   # Max: 415 us
   rt_bmark.intlat.full [Min/us,Avg/us,Max/us]:
   3,21.8,415
   PASS: rt_bmark.intlat.full

To run ``cyclictest`` directly with your own settings:

.. code-block:: shell

   # 10 minutes, SCHED_FIFO priority 95, 1 ms interval, latency histogram up to 400 us
   cyclictest -m -S -p 95 -i 1000 -D 10m -h 400 -q

.. list-table::
   :widths: 25 75
   :header-rows: 1

   * - Option
     - Description
   * - ``-m``
     - Lock memory to avoid page faults during the measurement.
   * - ``-S``
     - One measurement thread per CPU, all at the same priority.
   * - ``-p PRIO``
     - Real-time priority of the measurement threads.
   * - ``-i US``
     - Wakeup interval in microseconds.
   * - ``-D TIME``
     - Test duration, for example ``60``, ``10m`` or ``1h``.
   * - ``-l LOOPS``
     - Stop after this many wakeups instead of a fixed duration.
   * - ``-h US``
     - Print a latency histogram up to ``US`` microseconds at the end of the run.
   * - ``-q``
     - Print only the summary when the run ends.

ADI Crypto ptests
~~~~~~~~~~~~~~~~~

The test image installs ``crc-tests-ptest`` on every board, and on the ADSP-SC598 SOM
EZ-KIT it also installs ``crypto-tests-ptest``. They test the ADI CRC hardware and the ADI
security packet engine (PKTE) through ``cryptodev`` (``/dev/crypto``) and OpenSSL's
``devcrypto`` engine.

``crypto-tests`` checks that:

* ``sha256``, ``sha224``, ``sha1``, ``md5``, ``cbc(aes)``, ``cbc(des)``, ``cbc(des3_ede)``
  and ``ecb(aes)`` are registered by the ``adi`` driver in ``/proc/crypto``.
* The SHA-256, SHA-224, SHA-1 and MD5 hash and HMAC known-answer tests give the right
  results through ``/dev/crypto``, and each session is served by the ``adi`` driver
  (``[cryptodev]`` results).
* OpenSSL's ``devcrypto`` engine loads, and AES-256-CBC, DES-CBC, 3DES and AES-256-ECB
  encryption and decryption of 1 KB to 8 MB inputs through the engine match OpenSSL's
  software results (``[OpenSSL]`` results).

``crc-tests`` checks that ``hmac(crc32)`` is registered by the ``adi`` driver in
``/proc/crypto``, and runs CRC-32 known-answer tests on the ADI CRC hardware through
``/dev/crypto``, checking that each session is served by the ``adi`` driver.

.. code-block:: shell

   ptest-runner crypto-tests crc-tests

   # Or run the scripts directly; test data is kept in a temporary directory
   sh /crypto/adi-hash.sh
   sh /crypto/adi-skcipher.sh
   sh /crypto/adi-crc.sh

Example output:

.. code-block:: text

   PASS: sha256 [adi] registered
   PASS: SHA256 HMAC [cryptodev] (4 bytes, key 4 bytes)
   PASS: devcrypto engine [OpenSSL]
   PASS: aes-256-cbc [OpenSSL] (encrypt, 1M)
   PASS: CRC32B [cryptodev] (8192 bytes)

The suites need the ADI crypto drivers. ``CONFIG_CRYPTO_DEV_ADI_CRC`` is enabled by the
``crypto.cfg`` kernel fragment, and ``CONFIG_CRYPTO_DEV_ADI_PKTE`` by the ``pkte.cfg``
fragment, which is only applied for the ``adsp-sc598-som-ezkit`` machine.

With the ``adi-security`` distro, OP-TEE makes the packet engine secure-only. The security
layer disables the PKTE in the Linux device tree and does not install
``crypto-tests-ptest``; ``crc-tests-ptest`` is still installed.

ADI Board Sanity ptest
~~~~~~~~~~~~~~~~~~~~~~

``adsp-sanity-tests`` runs the board-level checks from ``lnxdsp-sanity-checks.sh`` in
``lnxdsp-scripts``. A check is reported as ``SKIP`` when the board does not have the
hardware or it is not set up, for example the RPMsg checks on the ADSP-SC846, which has no
echo firmware, or the link and ping checks when no Ethernet cable is connected.

.. list-table::
   :widths: 35 65
   :header-rows: 1

   * - Test
     - What it checks
   * - ``gptimer-counter-running``
     - The gptimer used as the clocksource is counting, read through its counter device in
       ``/sys/bus/counter``.
   * - ``rpmsg-echo``, ``rpmsg-echo-cap``
     - Binds an RPMsg character device to the first ``sharc-echo`` and ``sharc-echo-cap``
       channels with ``rpmsg-bind-chardev``, sends ``hello`` and checks that the replies
       start with ``hello`` and ``HELLO``.
   * - ``i2c-buses-present``, ``i2c-<n>``
     - At least one I2C bus is registered, and ``i2cdetect -y`` can scan each bus. Bus 0 is
       not scanned on the ADSP-SC598 boards.
   * - ``alsa-card-registered``, ``alsa-playback-device``
     - When the device tree has an ``adi,sc5xx-asoc-card`` sound node, a sound card is
       registered and ``aplay -l`` lists a playback device.
   * - ``alsa-playback``, ``alsa-capture``
     - Plays 5 seconds of silence on the first playback device and records 5 seconds from
       the first capture device, at 48 kHz. Each must finish without an underrun or
       overrun and take 5 seconds to within 5%, which checks that the audio DMA and the
       codec sample clock run at the right rate. The capture must be the expected size.
       Nothing is audible, and the capture check is skipped when there is no capture
       device.
   * - ``eth-present``, ``eth-link``, ``eth-ping-gateway``
     - An Ethernet interface exists, has a link, and can ping the default gateway.
   * - ``eth-ping-iperf-server``, ``eth-iperf3``
     - Pings ``ADI_PTEST_IPERF_SERVER`` and runs a 15 second ``iperf3`` test against it.
       Skipped when the variable is not set.
   * - ``ptp<n>-capabilities``, ``ptp<n>-running``
     - ``testptp`` can read the capabilities of each PTP hardware clock, and the clock
       advances by about one second in one second.

The RPMsg checks need the RPMsg echo example firmware (``rpmsg-echo-example``), which is
installed with the default ``linux_only_audio`` distro feature. With the
``adi_sharc_alsa_audio`` or ``adi_hybrid_audio`` features the SHARC cores run the audio
firmware instead, and the RPMsg checks are skipped.

.. code-block:: shell

   ptest-runner adsp-sanity-tests

   # Also ping an iperf3 server and test throughput to it (run "iperf3 -s" on the server)
   ADI_PTEST_IPERF_SERVER=192.168.1.10 ptest-runner adsp-sanity-tests

Example output:

.. code-block:: text

   gptimer0 count: 1855734042, then 2015734157 one second later
   PASS: gptimer-counter-running
   virtio0.sharc-echo.-1.288: sent 'hello', received 'hello => echo from Core1'
   PASS: rpmsg-echo
   SKIP: i2c-0 (not probed on this board)
   PASS: i2c-1
   alsa-playback: played 5 seconds of audio in 5.18 seconds
   PASS: alsa-playback
   SKIP: eth-iperf3 (ADI_PTEST_IPERF_SERVER is not set)

ADI Driver ptests
~~~~~~~~~~~~~~~~~

These suites test ADI drivers and peripherals:

``adsp-dmacopy-test``
   Loads the ``dmacopy_module`` kernel module, which requests a memory DMA (MDMA) channel
   through the dmaengine API and copies 4 byte to 64 KB buffers SDRAM to SDRAM, SDRAM to
   SRAM and back, and SRAM to SRAM. Each copy is run 32-bit, 16-bit and 8-bit aligned, and
   the data and the canaries around each buffer are checked. The SRAM buffers come from the
   ``adi,sram-mmap`` reserved region (ADSP-SC598, ADSP-SC846) or the SRAM pool (ARMv7
   boards), and the SRAM copies are skipped if neither is found.

``adsp-gptimer-test``
   Loads the ``hello_gptimer`` kernel module, which runs gptimer 5 in continuous PWM mode
   for 100 ms, measured with the kernel clock, and checks that it counted within 2% of the
   gptimer clock rate. The timer's previous configuration is restored afterwards. Load the
   module by hand with ``modprobe hello_gptimer timer_id=<n>`` to test another timer; the
   module refuses to use the clocksource and clockevent timers. On the ADSP-SC598 and
   ADSP-SC846 the kernel clock is the Arm generic timer, so this also checks the gptimer
   clock rate. On the ARMv7 boards the kernel clock is gptimer 0, which runs from the same
   clock, so it only checks that the two timers agree.

``adsp-l2-alloc-test``
   Loads the ``hello_l2_alloc`` kernel module, which allocates blocks of 32 bytes to 64 KB
   from each on-chip SRAM pool created by the ``mmio-sram`` driver. It checks that each
   block is inside the SRAM, aligned and does not overlap the others, that two patterns
   written to the blocks read back, and that the pool is back to its original size after
   the blocks are freed. The ADSP-SC598 and ADSP-SC846 have no SRAM pools (their SRAM is
   reserved memory used by ``sram_mmap``), so the test image does not install the suite on
   those boards.

``adsp-sram-mmap-test``
   Maps a page of on-chip SRAM through each ``/dev/sram_mmap*`` device and checks that two
   patterns written to it read back correctly.

The kernel modules run their tests when they are loaded and fail to load if a test fails;
``run-ptest`` prints the kernel log from the test. To run a test by hand:

.. code-block:: shell

   ptest-runner adsp-dmacopy-test adsp-gptimer-test adsp-sram-mmap-test

   # Not on the ADSP-SC598 or ADSP-SC846
   ptest-runner adsp-l2-alloc-test

   modprobe dmacopy_module && rmmod dmacopy_module
   dmesg | tail -n 40

   sram_mmap /dev/sram_mmap0

stress-ng
---------

``stress-ng`` loads the CPU, memory and I/O subsystems. With ``--verify`` each stressor
checks its own results, so a non-zero exit status indicates a failure.

.. code-block:: shell

   # CPU and memory stress for 60 s, verifying results
   stress-ng --cpu 1 --vm 1 --vm-bytes 64M --verify --timeout 60s --metrics-brief

   # Run every CPU stressor method in turn
   stress-ng --cpu 1 --cpu-method all --verify --timeout 60s

   # Context-switch and timer stress
   stress-ng --switch 1 --timer 1 --timeout 60s --metrics-brief

.. list-table::
   :widths: 30 70
   :header-rows: 1

   * - Option
     - Description
   * - ``--cpu N``
     - Start ``N`` CPU stressors (``0`` uses one per online CPU).
   * - ``--vm N`` / ``--vm-bytes SIZE``
     - Start ``N`` memory stressors, each using ``SIZE`` bytes.
   * - ``--io N`` / ``--hdd N``
     - Start ``N`` sync or disk-write stressors.
   * - ``--switch N`` / ``--timer N``
     - Start ``N`` context-switch or timer-interrupt stressors.
   * - ``--verify``
     - Check stressor results and fail on mismatches.
   * - ``--timeout T``
     - Stop after ``T`` (for example ``60s``, ``10m``).
   * - ``--metrics-brief``
     - Print a throughput summary for each stressor.

.. note::
   Keep ``--vm-bytes`` well below the free memory reported by ``free -m``. Memory reserved
   for the SHARC cores is not available to Linux.

memtester
---------

``memtester`` allocates and locks a block of memory and runs a series of pattern tests on
it. The exit status is ``0`` when all patterns pass.

.. code-block:: shell

   # Test 64 MB of memory, one pass
   memtester 64M 1

   # Test 32 MB of memory, three passes
   memtester 32M 3

The first argument is the amount of memory to test (``B``, ``K``, ``M`` or ``G`` suffix) and
the optional second argument is the number of loops. Use ``free -m`` to see how much memory
is available, and leave headroom for the rest of the system.

fio
---

``fio`` measures storage performance and can verify data integrity. Point ``--filename`` at
a file on the storage under test. Each job reports ``err= 0`` and ``fio`` exits with status
``0`` when all I/O and verification succeed.

.. code-block:: shell

   # Sequential write then read with CRC32C verification
   fio --name=verify --filename=/root/fio.dat --size=64M --rw=write --bs=128k \
       --direct=1 --verify=crc32c --do_verify=1

   # 4k random read/write for 60 s
   fio --name=randrw --filename=/root/fio.dat --size=64M --rw=randrw --bs=4k \
       --direct=1 --runtime=60 --time_based --group_reporting

   rm /root/fio.dat

.. list-table::
   :widths: 30 70
   :header-rows: 1

   * - Option
     - Description
   * - ``--filename``
     - File or block device to test.
   * - ``--size``
     - Size of the test file or region.
   * - ``--rw``
     - Access pattern: ``read``, ``write``, ``randread``, ``randwrite``, ``randrw``.
   * - ``--bs``
     - Block size for each I/O.
   * - ``--direct=1``
     - Bypass the page cache.
   * - ``--verify``
     - Write a checksum with each block and check it on read back.
   * - ``--runtime`` / ``--time_based``
     - Run for a fixed time rather than a fixed amount of data.

.. warning::
   Passing a block device such as ``/dev/mmcblk0`` to ``--filename`` overwrites its
   contents. Use a file on a mounted filesystem unless the device is a scratch device.

rngtest
-------

``rngtest`` runs the FIPS 140-2 statistical tests on data read from standard input. It exits
with status ``0`` if no blocks fail.

.. code-block:: shell

   # Test 100 blocks (20000 bits each) from the kernel random number generator
   rngtest -c 100 < /dev/random

   # Test a hardware RNG, if one is registered
   cat /sys/class/misc/hw_random/rng_current
   rngtest -c 100 < /dev/hwrng

If ``rng_current`` shows ``none``, no hardware RNG driver is registered and reading
``/dev/hwrng`` fails with ``No such device``. On the ``adi-security`` distro the OP-TEE RNG
is registered as ``optee-rng``.

.. list-table::
   :widths: 30 70
   :header-rows: 1

   * - Option
     - Description
   * - ``-c N``
     - Stop after ``N`` 20000-bit blocks.
   * - ``-b N``
     - Print statistics every ``N`` blocks.
   * - ``-t N``
     - Print statistics every ``N`` seconds.
   * - ``-p``
     - Pipe mode: pass the blocks that pass the tests through to standard output.

Security Test Suites (``adi-security``)
---------------------------------------

The following suites are only installed when the image is built with the ``adi-security``
distro for the ADSP-SC598. They require the ``tee-supplicant`` service, which is started at
boot:

.. code-block:: shell

   systemctl status tee-supplicant

TEE Smoke Test
~~~~~~~~~~~~~~

Checks, from Linux, that TF-A and OP-TEE are running and reachable: the PSCI and SMC Calling
Convention probe messages, the OP-TEE driver and ``/dev/tee*`` devices, a running
``tee-supplicant``, the OP-TEE random number generator and the OP-TEE "hello world"
example. The probe message checks are reported as ``SKIP`` if the start of the boot log
has already been overwritten in the kernel ring buffer.

.. code-block:: shell

   ptest-runner tee-smoke

OP-TEE xtest
~~~~~~~~~~~~

``xtest`` exercises the OP-TEE client API, the test trusted applications, secure storage and
the cryptographic services. Clear any secure-storage objects left by a previous run first.

.. code-block:: shell

   xtest --clear-storage

   # Default suites (regression and, if enabled, pkcs11)
   xtest

   # Run specific suites
   xtest -t regression
   xtest -t regression+pkcs11
   xtest -t benchmark

   # Run or exclude specific tests (substring match on the test ID)
   xtest -t regression 4001 4003
   xtest -t regression -x 1033

   # Include optional tests
   xtest -l 15

   # Performance applets
   xtest --aes-perf
   xtest --hash-perf
   xtest --asym-perf

The same tests can be run through ``ptest-runner``, which converts the output to
``PASS:`` / ``FAIL:`` lines.

.. code-block:: shell

   ptest-runner -t 1800 optee-test

.. list-table::
   :widths: 30 70
   :header-rows: 1

   * - Option
     - Description
   * - ``-t SUITE``
     - ``regression``, ``benchmark`` or ``pkcs11``. Join several with ``+``.
   * - ``-x ID``
     - Exclude tests matching ``ID``. May be repeated.
   * - ``-l LEVEL``
     - Test level ``0``-``15``. Higher levels add optional tests.
   * - ``--clear-storage``
     - Delete persistent objects left by a previous run.
   * - ``--aes-perf`` / ``--hash-perf`` / ``--asym-perf``
     - Crypto performance applets (pass ``-h`` for their options).

Mbed TLS ptest
~~~~~~~~~~~~~~

Runs the Mbed TLS ``test_suite_*`` programs against the installed library.

.. code-block:: shell

   ptest-runner mbedtls

   # Run a single suite directly
   cd /usr/lib/mbedtls/ptest/tests
   ./test_suite_aes.ecb -v

