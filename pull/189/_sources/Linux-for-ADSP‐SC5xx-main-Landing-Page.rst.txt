Linux for ADSP-SC5xx Processors (main)
======================================

These pages document the ``main`` branch of
`lnxdsp-adi-meta <https://github.com/analogdevicesinc/lnxdsp-adi-meta>`_, the
latest development version of Linux for ADSP-SC5xx. The sources are fetched and
built with `kas <https://kas.readthedocs.io>`_.

.. note::

   The ``main`` branch is under active development and is not a tested release.
   For a stable version, use the latest release,
   :doc:`Linux for ADSP-SC5xx 5.1.0 <Linux-for-ADSP‐SC5xx-5.1.0-Landing-Page>`.

Supported Boards
----------------

.. list-table::
   :header-rows: 1
   :widths: 40 20 40

   * - Development Board
     - Board Revision
     - Getting Started Guide
   * - `ADSP-SC598 SOM <https://www.analog.com/en/design-center/evaluation-hardware-and-software/evaluation-boards-kits/EV-SC598-SOM.html>`_ and `EV-SOMCRR-EZKIT <https://www.analog.com/en/design-center/evaluation-hardware-and-software/evaluation-boards-kits/ev-somcrr-ezkit.html>`_ carrier board
     - Rev A/B/C/D/E (ADSP-SC598 SOM), Rev A/D (REV-SOMCRR-EZKIT)
     - :doc:`Getting Started with ADSP-SC598 <getting-started/Getting-Started-with-ADSP‐SC598-(Linux-for-ADSP‐SC5xx-Processors-main)>`
   * - `ADSP-SC846 SOM <https://www.analog.com/en/resources/evaluation-hardware-and-software/evaluation-boards-kits/ev-sc846-som.html>`_ and `EV-SOMCRR2-EZKIT <https://www.analog.com/en/resources/evaluation-hardware-and-software/evaluation-boards-kits/ev-somcrr2-ezkit.html>`_ carrier board
     - Rev A/B (ADSP_SC846 SOM), Rev A/B (EV-SOMCRR2-EZKIT)
     - :doc:`Getting Started with ADSP-SC846 <getting-started/Getting-Started-with-ADSP‐SC846-(Linux-for-ADSP‐SC5xx-Processors-main)>`
   * - `ADSP-SC594 SOM <https://www.analog.com/en/design-center/evaluation-hardware-and-software/evaluation-boards-kits/EV-SC594-SOM.html>`_ and `EV-SOMCRR-EZKIT <https://www.analog.com/en/design-center/evaluation-hardware-and-software/evaluation-boards-kits/ev-somcrr-ezkit.html>`_ carrier board
     - 1.0 Rev B or later
     - :doc:`Getting Started with ADSP-SC594 <getting-started/Getting-Started-with-ADSP‐SC594-(Linux-for-ADSP‐SC5xx-Processors-main)>`
   * - `ADSP-SC589-MINI <https://www.analog.com/en/design-center/evaluation-hardware-and-software/evaluation-boards-kits/sharc-audio-module.html>`_
     - 1.5 or later
     - :doc:`Getting Started with ADSP-SC589 MINI <getting-started/Getting-Started-with-ADSP‐SC589‐MINI-(Linux-for-ADSP‐SC5xx-Processors-main)>`
   * - `ADSP-SC573 EZLITE <https://www.analog.com/en/resources/evaluation-hardware-and-software/evaluation-boards-kits/sc573ezkit.html>`_
     - 1.5 or later
     - :doc:`Getting Started with ADSP-SC573 EZLITE <getting-started/Getting-Started-with-ADSP‐SC573-(Linux-for-ADSP‐SC5xx-Processors-main)>`

Building of the Linux software requires a host PC running a recent flavour Linux.
Analog Devices uses `64-bit Ubuntu 22.04 LTS <https://discourse.ubuntu.com/t/jammy-jellyfish-release-notes/24668>`_ for its building and testing of Linux for ADSP-SC5xx.
If you are new to Linux development we strongly recommend using a host PC with this installation of Linux.
If you are confident in resolving variant specific problems feel free to use your `favourite distro <https://www.techradar.com/uk/best/best-linux-distros>`_.

Changes Since the Latest Release
--------------------------------

The changes on ``main`` since the latest release can be found in the
`commit history <https://github.com/analogdevicesinc/lnxdsp-adi-meta/commits/main>`_.

Guides
------

* :doc:`Getting Started with ADSP-SC598 <getting-started/Getting-Started-with-ADSP‐SC598-(Linux-for-ADSP‐SC5xx-Processors-main)>`
* :doc:`Getting Started with ADSP-SC846 <getting-started/Getting-Started-with-ADSP‐SC846-(Linux-for-ADSP‐SC5xx-Processors-main)>`
* :doc:`Getting Started with ADSP-SC594 <getting-started/Getting-Started-with-ADSP‐SC594-(Linux-for-ADSP‐SC5xx-Processors-main)>`
* :doc:`Getting Started with ADSP-SC589-MINI <getting-started/Getting-Started-with-ADSP‐SC589‐MINI-(Linux-for-ADSP‐SC5xx-Processors-main)>`
* :doc:`Getting Started with ADSP-SC573 <getting-started/Getting-Started-with-ADSP‐SC573-(Linux-for-ADSP‐SC5xx-Processors-main)>`
* :doc:`Secure Boot Support <secure-boot/Secure-Boot-Support-in-main>`

.. toctree::
   :hidden:
   :titlesonly:

   getting-started/Getting-Started-with-ADSP‐SC598-(Linux-for-ADSP‐SC5xx-Processors-main)
   getting-started/Getting-Started-with-ADSP‐SC846-(Linux-for-ADSP‐SC5xx-Processors-main)
   getting-started/Getting-Started-with-ADSP‐SC594-(Linux-for-ADSP‐SC5xx-Processors-main)
   getting-started/Getting-Started-with-ADSP‐SC589‐MINI-(Linux-for-ADSP‐SC5xx-Processors-main)
   getting-started/Getting-Started-with-ADSP‐SC573-(Linux-for-ADSP‐SC5xx-Processors-main)
