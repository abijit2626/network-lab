#!/usr/bin/env python3
"""Experiment 11: SMTP client written directly on sockets.
Usage: python3 smtp_client.py [host] [port] [from] [to] [subject] [body]"""
import socket, sys


def send_mail(host, port, sender, rcpt, subject, body):
    s = socket.create_connection((host, port))
    f = s.makefile('rwb', buffering=0)

    def reply():
        while True:
            l = f.readline().decode().rstrip()
            print('S:', l)
            if l[3:4] != '-':
                return int(l[:3])

    def cmd(c, ok):
        print('C:', c)
        f.write((c + '\r\n').encode())
        code = reply()
        if code != ok:
            raise RuntimeError(f'unexpected reply {code} to {c}')

    reply()
    cmd('HELO client.local', 250)
    cmd(f'MAIL FROM:<{sender}>', 250)
    cmd(f'RCPT TO:<{rcpt}>', 250)
    cmd('DATA', 354)
    msg = f'From: {sender}\r\nTo: {rcpt}\r\nSubject: {subject}\r\n\r\n{body}'
    msg = msg.replace('\n.', '\n..')
    f.write((msg + '\r\n.\r\n').encode())
    reply()
    cmd('QUIT', 221)
    s.close()


if __name__ == '__main__':
    a = sys.argv[1:] + [None] * 6
    send_mail(a[0] or 'localhost', int(a[1] or 2525), a[2] or 'alice@lab.local', a[3] or 'bob@lab.local',
              a[4] or 'Test mail', a[5] or 'Hello from the SMTP lab client.')
