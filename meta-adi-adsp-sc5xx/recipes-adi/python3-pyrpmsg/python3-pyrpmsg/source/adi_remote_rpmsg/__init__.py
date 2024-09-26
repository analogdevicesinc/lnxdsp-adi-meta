#! /usr/bin/env python3

import sys
from pathlib import Path
import subprocess

from pyrpmsg import RPMsgCore, RPMsgEndpoint
from remoteshell import Receiver, Sender, test_callback
from utils import getarg


def CLI(argv: list[str], colour: bool = True, reverse: bool = False) -> None:
    """CLI Application to interface with a remote DSP device."""
    hostname = getarg(argv, "h", "hostname")
    port = getarg(argv, "p", "port")

    if hostname is None:
        print("Please specify a hostname with -h/--hostname.")
        sys.exit(1)

    if port is None:
        s = Sender(hostname, colour=colour, reverse=reverse)
    else:
        s = Sender(hostname, int(port), colour=colour, reverse=reverse)

    s.command_queue.append("detect_cores")
    s.command_queue.append("detect_endpoints")

    s.main_loop()


class Remote:
    """Remote DSP device application."""

    def __init__(self, argv: list[str], reverse: bool = False):
        self.hostname = getarg(argv, "h", "hostname")
        self.port = getarg(argv, "p", "port")

        if self.hostname is None:
            print("Please specify a hostname with -h/--hostname.")
            sys.exit(1)
        if self.port is None:
            self.port = 5000

        self.cores: dict[int, RPMsgCore] = {}
        self.endpoints: list[RPMsgEndpoint | None] = []

        self.default_core: int | None = None
        self.default_endpoint: int | None = None

        if reverse:
            self.r = Receiver(
                hostname=self.hostname,
                port=self.port,
                reverse=True,
            )
        else:
            self.r = Receiver()
        self.r.bind("test", test_callback)
        self.r.bind("create_c", self.c_create_c)
        self.r.bind("detect_cores", self.c_detect_cores)
        self.r.bind("create_e", self.c_create_e)
        self.r.bind("detect_endpoints", self.c_detect_endpoints)
        self.r.bind("start", self.c_start)
        self.r.bind("stop", self.c_stop)
        self.r.bind("load_fw", self.c_load_fw)
        self.r.bind("write", self.c_write)
        self.r.bind("lazy_write", self.c_lazy_write)
        self.r.bind("lazy_clear", self.c_lazy_clear)
        self.r.bind("read", self.c_read)
        self.r.bind("bind", self.c_bind)
        self.r.bind("open_e", self.c_open_e)
        self.r.bind("close_e", self.c_close_e)
        self.r.bind("use_c", self.c_use_c)
        self.r.bind("use_e", self.c_use_e)
        self.r.bind("aplay", self.c_aplay)

        if not reverse:
            if self.port is None:
                self.r.connect(self.hostname)
            else:
                self.r.connect(self.hostname, int(self.port))
        else:
            self.r.wait_connection()

        self.r.main_loop()

    def c_detect_cores(self, r: Receiver, args: list[str]) -> tuple[int, str]:
        """Detect all cores."""
        for c in RPMsgCore.DetectCores():
            self.cores[c.core_id] = c

        strkeys = list(map(str, self.cores.keys()))

        return 0, f"Detected cores {', '.join(strkeys)}"

    def c_detect_endpoints(self, r: Receiver, args: list[str]) -> tuple[int, str]:
        """Detect all existing endpoints."""
        output = []
        for e in RPMsgEndpoint.DetectEndpoints():
            self.endpoints.append(e)
            e.open()
            output.append(f"\t{len(self.endpoints) - 1}: {e.device_path}")

        output_s = '\n'.join(output)

        return 0, f"Detected and opened endpoints:\n{output_s}"

    def c_create_c(self, r: Receiver, args: list[str]) -> tuple[int, str]:
        """Open Core."""
        core_n = getarg(args, "c", "core")
        if core_n is None:
            core_n = self.default_core

        if core_n is None:
            return -1, "Specify a core with -c/--core."

        self.cores[int(core_n)] = (RPMsgCore(int(core_n)))
        return 0, f"Created Core {core_n}."

    def c_create_e(self, r: Receiver, args: list[str]) -> tuple[int, str]:
        """Opens Existing Endpoint."""
        endpoint_n = getarg(args, "n", "endpointn")
        if endpoint_n is None:
            return -1, "Specify an endpoint number with -n/--endpointn."

        self.endpoints.append(RPMsgEndpoint(
            Path(f"/dev/rpmsg{endpoint_n}")
        ))
        return 0, f"Created Interface {endpoint_n}"

    def c_start(self, r: Receiver, args: list[str]) -> tuple[int, str]:
        """Start Core."""
        core_n = getarg(args, "c", "core")
        if core_n is None:
            core_n = self.default_core

        if core_n is None:
            return -1, "Specify a core with -c/--core."

        if int(core_n) not in self.cores.keys():
            return -1, f"Core {core_n} doesn't exist. Call open_c to open cores."

        self.cores[int(core_n)].start()
        return 0, f"Started core {core_n}."

    def c_stop(self, r: Receiver, args: list[str]) -> tuple[int, str]:
        """Stop Core."""
        core_n = getarg(args, "c", "core")
        if core_n is None:
            core_n = self.default_core

        if core_n is None:
            return -1, "Specify a core with -c/--core."

        if int(core_n) not in self.cores.keys():
            return -1, f"Core {core_n} doesn't exist. Call open_c to open cores."

        self.cores[int(core_n)].stop()
        return 0, f"Stopped core {core_n}."

    def c_load_fw(self, r: Receiver, args: list[str]) -> tuple[int, str]:
        """Load firmware to core."""
        core_n = getarg(args, "c", "core")
        if core_n is None:
            core_n = self.default_core

        if core_n is None:
            return -1, "Specify a core with -c/--core."

        fname = getarg(args, "f", "firmware")
        if fname is None:
            return -1, "Specify a filename with -f/--firmware."

        binary = self.r.request_binary(fname)
        c = self.cores[int(core_n)]

        c.stop()
        c.load_fw_binary(binary)
        c.start()
        return 0, "Loaded firmware."

    def c_write(self, r: Receiver, args: list[str]) -> tuple[int, str]:
        """Write data to an endpoint."""
        endpoint_n = getarg(args, "e", "endpoint")
        if endpoint_n is None:
            endpoint_n = self.default_endpoint

        if endpoint_n is None:
            return -1, "Specify an endpoint with -e/--endpoint."

        ep = self.endpoints[int(endpoint_n)]
        if ep is None:
            return -1, f"Endpoint {endpoint_n} has been closed/deleted."

        data = args[-1]

        try:
            ep.write(data)
        except OSError as e:
            return -1, str(e)

        return 0, ""

    def c_lazy_write(self, r: Receiver, args: list[str]) -> tuple[int, str]:
        """Lazily write data to an endpoint."""
        endpoint_n = getarg(args, "e", "endpoint")
        if endpoint_n is None:
            endpoint_n = self.default_endpoint

        if endpoint_n is None:
            return -1, "Specify an endpoint with -e/--endpoint."

        ep = self.endpoints[int(endpoint_n)]
        if ep is None:
            return -1, f"Endpoint {endpoint_n} has been closed/deleted."

        data = args[-1]
        ep.lazy_write(data)

        return 0, ""

    def c_lazy_clear(self, r: Receiver, args: list[str]) -> tuple[int, str]:
        """Clear lazy queue of an endpoint."""
        endpoint_n = getarg(args, "e", "endpoint")
        if endpoint_n is None:
            endpoint_n = self.default_endpoint

        if endpoint_n is None:
            return -1, "Specify an endpoint with -e/--endpoint."

        ep = self.endpoints[int(endpoint_n)]
        if ep is None:
            return -1, f"Endpoint {endpoint_n} has been closed/deleted."

        ep.lazy_clear()

        return 0, f"Cleared Lazy Write queue on endpoint {endpoint_n}."

    def c_read(self, r: Receiver, args: list[str]) -> tuple[int, str]:
        """Read data from an interface."""
        endpoint_n = getarg(args, "e", "endpoint")
        if endpoint_n is None:
            endpoint_n = self.default_endpoint

        if endpoint_n is None:
            return -1, "Specify a endpoint with -e/--endpoint."

        ep = self.endpoints[int(endpoint_n)]
        if ep is None:
            return -1, f"Endpoint {endpoint_n} has been closed/deleted."

        try:
            size = int(args[-1])
        except ValueError:
            return -1, "Last argument to read must be the size of data to read."

        read_data = ep.read(size)

        return 0, read_data

    def c_bind(self, r: Receiver, args: list[str]) -> tuple[int, str]:
        """Bind endpoint."""
        p, s = getarg(args, "d", "device"), getarg(args, "s", "srcaddr")
        if p is None:
            return -1, "Specify a device name with -d/--device."
        if s is None:
            return -1, "Specify a source address with -s/--srcaddr."
        try:
            s = int(s)
        except ValueError:
            return -1, "Source address must be an integer."

        ep = RPMsgEndpoint.Bind(p, s)
        if ep is None:
            return -1, "Failed to bind device."

        self.endpoints.append(ep)

        try:
            ep.open()
        except OSError:
            return -1, f"Bound device, but failed to open. Endpoint number is {len(self.endpoints)-1}"

        return 0, f"Bound and opened device, endpoint number is {len(self.endpoints)-1}"

    def c_open_e(self, r: Receiver, args: list[str]) -> tuple[int, str]:
        """Open endpoint device."""
        endpoint_n = getarg(args, "e", "endpoint")
        if endpoint_n is None:
            endpoint_n = self.default_endpoint

        if endpoint_n is None:
            return -1, "Specify an endpoint with -e/--endpoint."

        ep = self.endpoints[int(endpoint_n)]
        if ep is None:
            return -1, f"Endpoint {endpoint_n} has been closed/deleted."

        ep.open()

        return 0, ""

    def c_close_e(self, r: Receiver, args: list[str]) -> tuple[int, str]:
        """Close endpoint device."""
        endpoint_n = getarg(args, "e", "endpoint")
        if endpoint_n is None:
            endpoint_n = self.default_endpoint

        if endpoint_n is None:
            return -1, "Specify an endpoint with -e/--endpoint."

        ep = self.endpoints[int(endpoint_n)]
        if ep is None:
            return -1, f"Endpoint {endpoint_n} has been closed/deleted."

        ep.close()

        return 0, ""

    def c_use_c(self, r: Receiver, args: list[str]) -> tuple[int, str]:
        """Sets the default core."""
        if "-r" in args or "--reset" in args:
            self.default_core = None
            return 0, "Reset default core."

        core_n = getarg(args, "c", "core")
        if core_n is None:
            return -1, "Specify a default core with -c/--core, or -r to reset."

        self.default_core = int(core_n)
        return 0, f"Set default core to {core_n}"

    def c_use_e(self, r: Receiver, args: list[str]) -> tuple[int, str]:
        """Sets the default endpoint."""
        if "-r" in args or "--reset" in args:
            self.default_endpoint = None
            return 0, "Reset default endpoint."

        endpoint_n = getarg(args, "e", "endpoint")
        if endpoint_n is None:
            return -1, "Specify a default endpoint with -e/--endpoint, or -r to reset."

        self.default_endpoint = int(endpoint_n)
        return 0, f"Set default endpoint to {endpoint_n}"

    def c_aplay(self, r: Receiver, args: list[str]) -> tuple[int, str]:
        """Interface to `aplay`."""
        if "-h" in args or "--help" in args:
            return 0, """\
aplay interface.
> aplay [options] [filename]

Options:
-h  --help:
    Display this help.
-d  --device:
    PCM device to use.
-l  --list:
    List available PCM devices.
-f  --filename
    Filename of audio file to play.
"""
        if "-l" in args or "--list" in args:
            cmd = ["aplay", "-L"]
            result = subprocess.run(cmd, stdout=subprocess.PIPE)

            return result.returncode, result.stdout.decode()

        dev = getarg(args, "d", "device")
        filename = getarg(args, "f", "filename")

        if filename is None:
            return -1, "Specify a filename with -f/--filename."

        audiobinary = self.r.request_binary(filename)

        cmd = ["aplay"]
        if dev:
            cmd += ["-D", dev]

        aplay = subprocess.Popen(cmd, stdout=subprocess.PIPE,  stdin=subprocess.PIPE)

        stdout, _ = aplay.communicate(input=audiobinary)

        return aplay.returncode, stdout.decode()
