#! /usr/bin/env python3

"""Python interface for RPMsg devices."""

from __future__ import annotations

import os
import re
import subprocess
from datetime import datetime
from io import FileIO
from pathlib import Path
from typing import Optional


class RPMsgCore:
    """A physical RPMsg core."""

    remoteproc_dir = Path("/sys/class/remoteproc/")

    def __init__(self, core_id: int):
        self.core_id = core_id

    @classmethod
    def DetectCores(cls) -> list[RPMsgCore]:
        """Automatically detect cores and return them all as a list."""
        regex = re.compile(r"remoteproc(\d+)")
        result = []

        for core in os.listdir(cls.remoteproc_dir):
            if m := regex.match(core):
                core_id = int(m.group(1))
                result.append(
                    cls(core_id)
                )

        return result

    def set_state(self, state: str) -> None:
        """Set device state with /sys/class/remoteproc/*/state."""
        with open(
            self.remoteproc_dir / ("remoteproc" + str(self.core_id)) / "state", "wb", buffering=0
        ) as f:
            f.write(state.encode())

    def start(self) -> None:
        """Start the remote device."""
        self.set_state("start")

    def stop(self) -> None:
        """Stop the remote device."""
        self.set_state("stop")

    def set_fw(self, filename: str) -> None:
        """Set the firmware filename."""
        with open(
            self.remoteproc_dir / ("remoteproc" + str(self.core_id)) / "firmware", "wb", buffering=0
        ) as f:
            f.write(filename.encode())

    def load_fw_binary(self, binary: bytes, _filename: Optional[str] = None) -> None:
        """Load a firmware binary."""
        if _filename is None:
            filename = "adsp_fw_" + datetime.now().strftime("%Y%m%d_%H%M%S") + ".ldr"
        else:
            filename = _filename

        full_filename = "/lib/firmware/" + filename

        with open(full_filename, "wb") as f:
            f.write(binary)

        self.set_fw(filename)


class RPMsgEndpoint:
    """An interface to an RPMsg core, usually in /dev/rpmsg*"""

    def __init__(self, device_path: Optional[Path] = None):
        self.device_file: Optional[FileIO] = None
        self.device_path: Optional[Path] = device_path
        self.w_queue: list[str] = []

    @classmethod
    def Bind(cls, device_name: str, src_addr: int) -> RPMsgEndpoint | None:
        """Create an endpoint, bound to the chardev driver."""
        regex = re.compile(r"^(.*[^\d])(\d+)$")
        match = regex.match(device_name)

        if match is None:
            raise ValueError(f"<{device_name}> is not a valid device name.")

        dev, e = match.group(1), match.group(2)

        print(f"{dev=}, {e=}")

        ep = cls()
        ret = ep._bind(dev, int(e), src_addr)

        if ret:
            return None
        else:
            return ep

    @classmethod
    def DetectEndpoints(cls) -> list[RPMsgEndpoint]:
        """Automatically detect endpoints and return them all as a list."""
        regex = re.compile(r"rpmsg(\d+)")
        result = []

        for device in os.listdir("/dev/"):
            if m := regex.match(device):
                result.append(
                    cls(Path("/dev/" + m.group()))
                )

        return result

    def open(self) -> None:
        """Open the interface."""
        if self.device_path is None:
            self.device_file = None
            print("No interface assigned, call .bind().")

        try:
            self.device_file = open(self.device_path, "r+b", buffering=0)
        except FileNotFoundError:
            self.device_file = None
            print("Failed to open interface - have you called .bind()?")

    def close(self) -> None:
        """Close the interface."""
        self.sync()
        if self.device_file is not None:
            self.device_file.close()
            self.device_file = None

    def write(self, data: str, sync: bool = True) -> None:
        """Write a string to the interface."""
        if sync:
            self.sync()

        if self.device_file is not None:
            self.device_file.write(data.encode())
        else:
            raise IOError("Open interface before writing.")

        print("Wrote", data)

    def read(self, size: int) -> str:
        """Read a string of size `size` from the interface."""
        self.sync()
        if self.device_file is not None:
            data = self.device_file.read(size).decode()
            print("Read", data)
            return data
        else:
            raise IOError("Open interface before reading.")

    def lazy_write(self, data: str) -> None:
        """Queue data to be written.

        Returns the number of queued items.
        """
        self.w_queue.append(data)

    def lazy_clear(self) -> None:
        """Clear lazy queue."""
        self.w_queue = []

    def sync(self) -> None:
        """Flush the async write queue.

        Should be performed at the start of any synchronous operation.
        """
        for w in self.w_queue:
            try:
                self.write(w, sync=False)
            except OSError:
                raise IOError("Open interface before reading.")
                return

        self.w_queue = []

    def _bind(self, dev: str, e: int, s: int) -> int:
        """Bind an interface."""
        self.close()

        cmd = ["rpmsg-bind-chardev", "-p", dev, "-n", "1", "-e", str(e), "-s", str(s)]
        result = subprocess.run(cmd, stdout=subprocess.PIPE)
        output = result.stdout.decode()

        regex = re.compile(r"rpmsg device: \/dev\/rpmsg_ctrl(\d+)")
        if (match := regex.search(output)):
            self.device_path = Path("/dev/rpmsg" + match.group(1))
            return 0
        else:
            print("Bind Failed! Output was:\n" + output)
            return -1


if __name__ == "__main__":
    print("pyrpmsg test mode!")
    match input("test mode? "):
        case "bind":
            c0 = RPMsgCore(0)
            c0.stop()
            c0.set_fw("adi_adsp_core1_fw.ldr")
            c0.start()

            i0 = RPMsgEndpoint.Bind("virtio0.sharc-echo.-1.151", 100)

            print(f"i0 bound to {i0.device_path}")

            i0.open()
            i0.write("Bound Successfully!")
            print(i0.read(200))
            i0.close()
        case _:
            print("Unrecognised mode.")
