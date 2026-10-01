#!/usr/bin/env python3
"""Experiment 11: minimal SMTP server (RFC 5321 subset) - stores mail in ./mailbox/.
Usage: python3 smtp_server.py [port]   (default 2525; port 25 needs root)"""
import os, socket, sys, threading, time


def handle(conn, addr):
    f = conn.makefile('rwb', buffering=0)
    send = lambda s: (f.write((s + '\r\n').encode()), print('S:', s))
    send('220 labsmtp.local ESMTP ready')
    sender, rcpts = None, []
    while True:
        line = f.readline()
        if not line:
            break
        line = line.decode(errors='replace').strip()
        print('C:', line)
        cmd = line.upper()
        if cmd.startswith(('HELO', 'EHLO')):
            send('250 labsmtp.local Hello ' + (line.split(' ', 1)[1] if ' ' in line else 'client'))
        elif cmd.startswith('MAIL FROM:'):
            sender = line[10:].strip(); rcpts = []; send('250 OK')
        elif cmd.startswith('RCPT TO:'):
            if sender is None:
                send('503 Bad sequence of commands')
            else:
                rcpts.append(line[8:].strip()); send('250 OK')
        elif cmd == 'DATA':
            if not rcpts:
                send('503 Need RCPT first'); continue
            send('354 End data with <CR><LF>.<CR><LF>')
            lines = []
            while True:
                l = f.readline().decode(errors='replace').rstrip('\r\n')
                if l == '.':
                    break
                lines.append(l[1:] if l.startswith('..') else l)
            os.makedirs('mailbox', exist_ok=True)
            name = f"mailbox/{int(time.time()*1000)}.eml"
            with open(name, 'w') as m:
                m.write(f"X-Envelope-From: {sender}\nX-Envelope-To: {', '.join(rcpts)}\n" + '\n'.join(lines) + '\n')
            send(f'250 OK message queued as {name}')
            sender, rcpts = None, []
        elif cmd == 'RSET':
            sender, rcpts = None, []; send('250 OK')
        elif cmd == 'NOOP':
            send('250 OK')
        elif cmd == 'QUIT':
            send('221 Bye'); break
        else:
            send('502 Command not implemented')
    conn.close()


if __name__ == '__main__':
    port = int(sys.argv[1]) if len(sys.argv) > 1 else 2525
    s = socket.socket(); s.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    s.bind(('', port)); s.listen(5)
    print(f'SMTP server on port {port}')
    while True:
        c, a = s.accept()
        threading.Thread(target=handle, args=(c, a), daemon=True).start()
