#!/usr/bin/env python3
"""Experiment 12: interactive FTP client built on sockets.
Usage: python3 ftp_client.py [host] [port]
Client commands: ls, pwd, cd <dir>, get <file>, put <file>, quit"""
import os, re, socket, sys


class FTP:
    def __init__(self, host, port):
        self.s = socket.create_connection((host, port))
        self.f = self.s.makefile('rwb', buffering=0)
        self.reply()

    def reply(self):
        l = self.f.readline().decode().rstrip()
        print('S:', l)
        return l

    def cmd(self, c):
        self.f.write((c + '\r\n').encode())
        return self.reply()

    def pasv(self):
        r = self.cmd('PASV')
        n = list(map(int, re.search(r'\((.*?)\)', r).group(1).split(',')))
        return socket.create_connection(('.'.join(map(str, n[:4])), n[4] * 256 + n[5]))

    def ls(self, arg=''):
        d = self.pasv(); self.cmd('LIST ' + arg)
        data = b''
        while c := d.recv(4096): data += c
        d.close(); print(data.decode()); self.reply()

    def get(self, name):
        d = self.pasv(); r = self.cmd('RETR ' + name)
        if r.startswith('150'):
            with open(os.path.basename(name), 'wb') as fh:
                while c := d.recv(4096): fh.write(c)
            self.reply()
        d.close()

    def put(self, name):
        d = self.pasv(); r = self.cmd('STOR ' + os.path.basename(name))
        if r.startswith('150'):
            with open(name, 'rb') as fh:
                while c := fh.read(4096): d.sendall(c)
            d.close(); self.reply()


if __name__ == '__main__':
    host = sys.argv[1] if len(sys.argv) > 1 else 'localhost'
    port = int(sys.argv[2]) if len(sys.argv) > 2 else 2121
    c = FTP(host, port)
    c.cmd('USER ' + input('user: ')); c.cmd('PASS ' + input('password: '))
    while True:
        try:
            line = input('ftp> ').strip()
        except EOFError:
            line = 'quit'
        op, _, arg = line.partition(' ')
        if op == 'ls': c.ls(arg)
        elif op == 'pwd': c.cmd('PWD')
        elif op == 'cd': c.cmd('CWD ' + arg)
        elif op == 'get': c.get(arg)
        elif op == 'put': c.put(arg)
        elif op == 'quit': c.cmd('QUIT'); break
        else: print('commands: ls pwd cd get put quit')
