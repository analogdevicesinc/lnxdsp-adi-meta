===================================
Compiling and running a Rust kernel
===================================

Yocto 5 onward, all supported boards have now been ported to kernel 6.12. As a result, it is now possible to compile the linux kernel with Rust support for ADSP-SC598 boards.

Requirements
============

* rust (installed via rustup)
* Linux kernel source code
* LLVM
* clang
* Aarch64 GCC compiler

Run the following to install the prerequisite packages on ubuntu 22.04:

.. code-block:: shell

   sudo apt install llvm libclang-dev gcc-aarch64-linux-gnu -y

Getting started
===============

1) On your host/build machine, install rust via `rustup <https://www.rust-lang.org/tools/install>`_

2) Clone the Linux kernel tree you wish to use. In our case, we will use `this <https://github.com/analogdevicesinc/linux/tree/adsp-main-6.12>:doc:`_.

3) Once cloned, configure the kernel using either ``ARCH=arm64 CROSS_COMPILE=aarch64-linux-gnu- make menuconfig/defconfig/olddefconfig``

4) Set up the rust environment:

.. code-block:: text

   cd linux

Edit the following file ``scripts/min-tool-version.sh`` with the following change:

.. code-block:: shell

   diff --git a/scripts/min-tool-version.sh b/scripts/min-tool-version.sh
   index 91c91201212c..dd09a4ed0e96 100755
   --- a/scripts/min-tool-version.sh
   +++ b/scripts/min-tool-version.sh
   @@ -33,7 +33,7 @@ llvm)
           fi
           ;;
    rustc)
   -       echo 1.78.0
   +       echo 1.82.0
           ;;
    bindgen)
           echo 0.65.1

Set the package/crate versions using the script

.. code-block:: shell

   rustup override set $(scripts/min-tool-version.sh rustc)
   rustup component add rust-src
   cargo install --locked bindgen-cli
   export PATH=$PATH:~/.cargo/bin

Now setup the target architecture

.. code-block:: shell

   rustup target add aarch64-unknown-linux-gnu

5) Check if rust is now available to be compiled into the kernel:

.. code-block:: shell

   ARCH=arm64 CROSS_COMPILE=aarch64-linux-gnu- make rustavailable

You should see something like this:

.. code-block:: shell

   Rust is available!

Now, we are ready to enable Rust kernel support!

Start the kernel config with ``menuconfig`` and enable ``CONFIG_RUST``:

.. code-block:: text

   General Setup
       -> Rust support

To enable a kernel rust sample, head to the following and enable the ``minimal`` example:

.. code-block:: text

   Kernel hacking
       -> Sample kernel code
           -> Rust samples
                 -> minimal

You can select it as a built-in module and an external kernel module. For this guide, we will keep it as a built-in module.

Now compile the kernel with the following:

.. code-block:: shell

   ARCH=arm64 CROSS_COMPILE=aarch64-linux-gnu- make -j$(nproc)

You should be able to see rust modules
being compiled:

.. code-block:: shell

     RUSTC L rust/core.o
     EXPORTS rust/exports_core_generated.h
     RUSTC P rust/libmacros.so
     BINDGEN rust/bindings/bindings_generated.rs
     BINDGEN rust/bindings/bindings_helpers_generated.rs
     RUSTC L rust/compiler_builtins.o
     RUSTC L rust/alloc.o
     EXPORTS rust/exports_alloc_generated.h
     CC      rust/helpers/helpers.o
     EXPORTS rust/exports_helpers_generated.h
     RUSTC L rust/bindings.o

Once this is done, follow the custom fitImage compilation guide `here <Yocto-Linux-Kernel-development>` and generate your own fitImage.

Proceed to boot into the fitImage from your preferred boot method. Once in, you should be able to spot the following in the kernel logs:

.. code-block:: shell

   [    1.467785] virtio_rpmsg_bus virtio1: creating channel sharc-echo-cap addr 0xa2
   [    1.475113] virtio_rpmsg_bus virtio1: rpmsg host is online
   [    1.480488] rproc-virtio rproc-virtio.1.auto: registered virtio1 (type 7)
   [    1.487253] remoteproc remoteproc1: remote processor core2-rproc is now up
   [    1.802327] rust_minimal: Rust minimal sample (init)
   [    1.807237] rust_minimal: Am I built-in? true
   [    1.811804] NET: Registered PF_PACKET protocol family

References
==========

* https://www.kernel.org/doc/html/v6.1/rust/quick-start.html
* https://docs.kernel.org/rust/quick-start.html
* :doc:`Yocto-Linux-Kernel-development`