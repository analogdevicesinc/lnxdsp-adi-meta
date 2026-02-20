========================
USB Gadget Audio Example
========================

Introduction
------------

This page provides a guide on how to use the USB Gadget Audio on ADSP-SC5XX boards. It provides the capability for the board to be used (and appear to Host PCs) as an audio card.

.. note::
   The USB Gadget Audio feature can be enabled when following the Getting Started Guides (version 3.0.0 or later). You need to append the following line to the ``conf/local.conf`` file - ``DISTRO_FEATURES:append = " adi_usb_gadget_audio"`` - before building the image.

Hardware Configuration
----------------------

Connect the USB micro-B plug cable into the USB HS/OTG Device port, as showing below:

.. image:: https://github.com/analogdevicesinc/lnxdsp-adi-meta/assets/110021710/1c72a9cf-90e9-42eb-a5f5-ac0ffc7dc4af
   :width: 400

Usage
-----

Capture and Play on-board
~~~~~~~~~~~~~~~~~~~~~~~~~

Type the following into the target board:

.. code-block:: shell

   root@adsp-sc589-mini:~# ls /dev/snd/
   by-path  controlC0  controlC1  pcmC0D0c  pcmC0D0p  pcmC1D0c  pcmC1D0p  timer
   root@adsp-sc589-mini:~# modprobe g_audio c_srate=48000
   g_audio gadget: Linux USB Audio Gadget, version: Feb 2, 2012
   g_audio gadget: g_audio ready

A new sound card ``UAC2Gadget`` is generated both for playback and capture on the EZ-KIT board:

.. code-block:: shell

   root@adsp-sc589-mini:~# arecord -l
   **** List of CAPTURE Hardware Devices ****
   card 0: sc5xxasoccard [sc5xx-asoc-card], device 0: adau1761 adau-hifi-0 []
     Subdevices: 1/1
     Subdevice #0: subdevice #0
   card 1: UAC2Gadget [UAC2_Gadget], device 0: UAC2 PCM [UAC2 PCM]
     Subdevices: 1/1
     Subdevice #0: subdevice #0
   root@adsp-sc589-mini:~# aplay -l
   **** List of PLAYBACK Hardware Devices ****
   card 0: sc5xxasoccard [sc5xx-asoc-card], device 0: adau1761 adau-hifi-0 []
     Subdevices: 1/1
     Subdevice #0: subdevice #0
   card 1: UAC2Gadget [UAC2_Gadget], device 0: UAC2 PCM [UAC2 PCM]
     Subdevices: 1/1
     Subdevice #0: subdevice #0

Record audio data from the new generated UAC2 sound card (arecord ``card 1``, ``device 0``) and play the recorded data via normal playback device (aplay ``card 0``, ``device 1``)

.. code-block:: shell

   root@adsp-sc589-mini:~# arecord -f dat -t wav -D hw:1,0 -c 2 -r 48000 -f S16_LE |aplay -D hw:0,0
   Recording WAVE 'stdin' : Signed 16 bit Little Endian, Rate 48000 Hz, Stereo
   Playing WAVE 'stdin' : Signed 16 bit Little Endian, Rate 48000 Hz, Stereo

Play audio over USB on Host
~~~~~~~~~~~~~~~~~~~~~~~~~~~~

With following command you should be able to see the USB Gadget Audio device is there on your host PC:

.. code-block:: shell

   test@madara:~$ lsusb
   Bus 002 Device 033: ID 1d6b:0101 Linux Foundation Audio Gadget

Then, list the available sound card on host PC, to decide which sound card is associated with the EZ-KIT board

.. code-block:: shell

   test@madara:~$ cat /proc/asound/cards
    0 [PCH            ]: HDA-Intel - HDA Intel PCH
                         HDA Intel PCH at 0xf7d00000 irq 28
    1 [Gadget         ]: USB-Audio - Linux USB Audio Gadget
                         Linux 4.19.0-yocto-standard with musb-hdrc Linux USB Audio Gadget at usb-0000:0

   test@madara:~$ aplay -l
   **** List of PLAYBACK Hardware Devices ****
   card 0: PCH [HDA Intel PCH], device 0: CX20641 Analog [CX20641 Analog]
     Subdevices: 1/1
     Subdevice #0: subdevice #0
   card 0: PCH [HDA Intel PCH], device 3: HDMI 0 [HDMI 0]
     Subdevices: 1/1
     Subdevice #0: subdevice #0
   card 1: Gadget [Linux USB Audio Gadget], device 0: USB Audio [USB Audio]
     Subdevices: 1/1
     Subdevice #0: subdevice #0
   test@madara:~$ arecord -l
   **** List of CAPTURE Hardware Devices ****
   card 0: PCH [HDA Intel PCH], device 0: CX20641 Analog [CX20641 Analog]
     Subdevices: 1/1
     Subdevice #0: subdevice #0
   card 0: PCH [HDA Intel PCH], device 2: CX20641 Alt Analog [CX20641 Alt Analog]
     Subdevices: 1/1
     Subdevice #0: subdevice #0
   card 1: Gadget [Linux USB Audio Gadget], device 0: USB Audio [USB Audio]
     Subdevices: 1/1
     Subdevice #0: subdevice #0

USB Audio Gadget is the sound card 1 (aplay ``card 1``, ``device 0``).

We can play an audio file:

.. code-block:: shell

   test@madara:~$ aplay -D hw:1,0 sample_s16_le.wav
   Playing WAVE 'sample_s16_le.wav' : Signed 16 bit Little Endian, Rate 48000 Hz, Stereo

and hear the music playing out from the headset.