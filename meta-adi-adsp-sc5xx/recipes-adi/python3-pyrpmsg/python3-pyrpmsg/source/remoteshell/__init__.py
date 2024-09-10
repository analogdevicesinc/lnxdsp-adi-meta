#! /usr/bin/env python3

"""remoteshell: An OO-structured way to do shell-like interfaces over a network."""

from __future__ import annotations

import os
import readline
import socket
from typing import Callable

from .colours import Colours, DummyColours

Callback = Callable[["Receiver", list[str]], tuple[int, str]]

NET_CHUNK_SIZE = 1024
NET_TYPE_SIZE = 128


class Sender:
    """A device which presents an interactive shell, and sends commands over the network.

    Will act like a server - opens a port and waits for connections.
    """

    def __init__(self, hostname: str, port: int = 5000, colour: bool = True):
        self.sock = socket.socket()
        self.sock.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        self.sock.bind((hostname, port))

        self.command_queue = []

        if colour:
            self.c = Colours
        else:
            self.c = DummyColours

    def wait_connection(self) -> tuple[socket.socket, str]:
        """Wait for a connection, and return the opened socket and connected address."""
        self.sock.listen(2)
        return self.sock.accept()

    def send_command(self, conn: socket.socket, command: str) -> None:
        """Send a command."""
        data = command.encode()
        conn.send(f"command {len(data)}".encode())
        conn.send(data)

    def send_binary(self, conn: socket.socket, filename: str) -> None:
        """Send a binary file."""
        fsize = os.path.getsize(filename)
        conn.send(f"binary {fsize}".encode())

        data = conn.recv(NET_TYPE_SIZE)
        while data != "binary_ready".encode():
            data = conn.recv(NET_TYPE_SIZE)

        with open(filename, "rb") as f:
            while read_bytes := f.read(NET_CHUNK_SIZE):
                conn.send(read_bytes)

    def main_loop(self) -> None:
        """Main event loop. Provides an interactive console with which to send commands over the network."""
        conn, addr = self.wait_connection()
        print(f"Connected to {addr}")
        ret = 0

        while True:
            if self.command_queue:
                command = self.command_queue[0]
                del self.command_queue[0]
            else:
                try:
                    command = input(
                        f"{self.c.Fore.RED if ret else self.c.Fore.GREEN}({ret:3d}){self.c.RESET} -> {self.c.Fore.CYAN}"
                    )
                except KeyboardInterrupt:
                    print()
                    continue
                except EOFError:
                    command = "quit"
                    print(command)

            print(f"{self.c.RESET}", end="")

            if command.strip() == "quit":
                print("Exiting...")
                conn.close()
                self.sock.close()
                return

            if len(command.split(";")) > 1:
                self.command_queue = command.split(";")
                self.command_queue = list(map(lambda x: x.strip(), self.command_queue))
                continue

            self.send_command(conn, command)

            type_info = conn.recv(NET_TYPE_SIZE).decode().strip().split(" ")
            while type_info[0] != "command_output":
                match type_info[0]:
                    case "request_binary":
                        binary_fn = type_info[1]
                        self.send_binary(conn, binary_fn)
                    case _:
                        print(f"Error in type info, got unexpected type <{type_info[0]}>")

                type_info = conn.recv(NET_TYPE_SIZE).decode().strip().split(" ")

            _, s, ret = type_info
            s = int(s)
            ret = int(ret)

            output_bytes = bytes()
            while len(output_bytes) < s:
                output_bytes += conn.recv(NET_CHUNK_SIZE)

            output = output_bytes.decode()

            if output:
                print(output)


class Receiver:
    """A device which receives commands from some sender.

    Acts like a client - will attempt to connect to some already-running remote host.
    """

    def __init__(self):
        self.sock = socket.socket()
        self.connected = False

        self.callbacks: dict[str, Callback] = {}

    def connect(self, host: str, port: int = 5000) -> None:
        """Connect to a Sender."""
        self.sock.connect((host, port))
        self.connected = True

    def bind(self, command: str, callback: Callback) -> None:
        """Bind a command to a callback.

        Upon some command of format <command> <arg1> <arg2> ... being received, callback([command, arg1, arg2, ...])
        will be executed.
        """
        self.callbacks[command] = callback

    def receive_command(self, size: int) -> tuple[int, str]:
        """Recieve and command over the network and call its callback.

        Returns the callback's return code and output.
        """
        command_raw = bytes()
        while len(command_raw) < size:
            command_raw += self.sock.recv(NET_CHUNK_SIZE)

        command = command_raw.decode().strip().split(" ")

        # print(f"Executing command: {command}")

        try:
            return self.callbacks[command[0]](self, command)
        except KeyError:
            return -1, "Command not found."

    def receive_binary(self) -> bytes:
        """Recieve a binary, and return as a bytes object."""
        net_type = self.sock.recv(NET_TYPE_SIZE).decode()
        _, s = net_type.strip().split(" ")
        s = int(s)

        self.sock.send("binary_ready".encode())

        binary = bytes()
        while len(binary) < s:
            binary += self.sock.recv(NET_CHUNK_SIZE)

        return binary

    def request_binary(self, filename: str) -> bytes:
        """Request a binary with given filename, and return as bytes."""
        self.sock.send(f"request_binary {filename}".encode())

        return self.receive_binary()

    def main_loop(self, net_chunk_size: int = 1024) -> None:
        """Main loop of the shell.

        Should be executed after the interface has been fully set up.
        """
        if not self.connected:
            print("Receiver must connected before starting the main loop.\nCall .connect(host[, port]).")
            return

        while True:
            net_type = self.sock.recv(NET_TYPE_SIZE).decode()
            t, s = net_type.strip().split(" ")
            s = int(s)

            match t:
                case "command":
                    ret, output = self.receive_command(s)
                    output_bytes = output.encode()
                    self.sock.send(f"command_output {len(output_bytes)} {ret}".encode())
                    self.sock.send(output_bytes)
                case _:
                    print(f"Network error: unexpected type <{t}> of size <{s}>.")
                    continue


def test_callback(recv: Receiver, args: list[str]) -> tuple[int, str]:
    """Callback for testing."""
    return 0, " ".join(args)


def test_binary(recv: Receiver, args: list[str]) -> tuple[int, str]:
    """Callback for binary testing."""
    recv.sock.send(f"request_binary {args[1]}".encode())

    bin = recv.receive_binary()

    return 0, f"Received binary, size {len(bin)}."
