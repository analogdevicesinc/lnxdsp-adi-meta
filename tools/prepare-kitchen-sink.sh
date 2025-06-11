#!/bin/bash

echo "Preparing kitchen sink sources..."

#exit to sources
cd ../../

# Jupyter
echo "Cloning Xilinx Jupyter meta layer..."
git clone https://github.com/Xilinx/meta-jupyter meta-xilinx
echo "Patching Xilinx meta layer..."
cd meta-xilinx
git apply ../meta-adi/tools/kitchen_sink_patches/000*.patch

# meta-oe
cd ..
echo "Patching OE meta layer..."
cd meta-openembedded/meta-oe/
git apply ../../meta-adi/tools/kitchen_sink_patches/graphviz.patch

echo "Done" 
echo "Please proceed to setup environment and build the image."
