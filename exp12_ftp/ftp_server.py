#!/usr/bin/env python3
"""Experiment 12: simplified FTP (RFC 959 subset) server with separate control & data connections.
Commands: USER PASS PWD CWD LIST RETR STOR QUIT, PASV for data connection.  Serves ./ftp_root.
Usage: python3 ftp_server.py [port]  (default 2121)"""
import os, socket, sys, threading

ROOT = os.path.abspath('ftp_root')
USERS = {'student': 'lab123', 'anonymous': ''}


def safe(cwd, p):
    full = os.path.abspath(os.path.join(ROOT, cwd.lstrip('/'), p) if not p.startswith('/') else os.path.join(ROOT, p.lstrip('/')))
    return full if full == ROOT or full.startswith(ROOT + os.sep) else None


def session(conn):
    f = conn.makefile('rwb', buffering=0)
    say = lambda s: f.write((s + '\r\n').encode())
    say('220 Lab FTP server ready')
    user, authed, cwd, pasv = None, False, '/', None
    host = conn.getsockname()[0]

    def data_conn():
        nonlocal pasv
        if not pasv:
            say('425 Use PASV first'); return None
        say('150 Opening data connection')
        d, _ = pasv.accept(); pasv.close(); pasv = None
        return d

    while True:
        l = f.readline()
        if not l:
            break
        line = l.decode(errors='replace').strip()
        cmd, _, arg = line.partition(' ')
        cmd = cmd.upper()
        print('C:', line if cmd != 'PASS' else 'PASS ****')
        if cmd == 'USER':
            user = arg; say('331 Password required')
        elif cmd == 'PASS':
            if user in USERS and USERS[user] in ('', arg):
                authed = True; say('230 Login successful')
            else:
                say('530 Login incorrect')
        elif cmd == 'QUIT':
            say('221 Goodbye'); break
        elif not authed:
            say('530 Please login')
        elif cmd == 'PWD':
            say(f'257 "{cwd}"')
        elif cmd == 'CWD':
            t = safe(cwd, arg)
            if t and os.path.isdir(t):
                cwd = '/' + os.path.relpath(t, ROOT).replace('.', '', 1) if t != ROOT else '/'
                cwd = cwd.replace('//', '/'); say('250 OK')
            else:
                say('550 No such directory')
        elif cmd == 'PASV':
            if pasv: pasv.close()
            pasv = socket.socket(); pasv.bind((host, 0)); pasv.listen(1)
            p = pasv.getsockname()[1]
            say(f"227 Entering Passive Mode ({host.replace('.', ',')},{p >> 8},{p & 255})")
        elif cmd == 'LIST':
            t = safe(cwd, arg or '.')
            if not t or not os.path.isdir(t):
                say('550 Failed'); continue
            d = data_conn()
            if d:
                for n in sorted(os.listdir(t)):
                    p = os.path.join(t, n)
                    d.sendall(f"{'d' if os.path.isdir(p) else '-'} {os.path.getsize(p):>10} {n}\r\n".encode())
                d.close(); say('226 Transfer complete')
        elif cmd == 'RETR':
            t = safe(cwd, arg)
            if not t or not os.path.isfile(t):
                say('550 File not found'); continue
            d = data_conn()
            if d:
                with open(t, 'rb') as fh:
                    while chunk := fh.read(4096):
                        d.sendall(chunk)
                d.close(); say('226 Transfer complete')
        elif cmd == 'STOR':
            t = safe(cwd, arg)
            if not t:
                say('550 Denied'); continue
            d = data_conn()
            if d:
                with open(t, 'wb') as fh:
                    while chunk := d.recv(4096):
                        fh.write(chunk)
                d.close(); say('226 Transfer complete')
        else:
            say('502 Command not implemented')
    if pasv: pasv.close()
    conn.close()


if __name__ == '__main__':
    port = int(sys.argv[1]) if len(sys.argv) > 1 else 2121
    os.makedirs(ROOT, exist_ok=True)
    s = socket.socket(); s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    s.bind(('', port)); s.listen(5)
    print(f'FTP server on port {port}, root={ROOT}, login student/lab123')
    while True:
        c, _ = s.accept()
        threading.Thread(target=session, args=(c,), daemon=True).start()
