#!/usr/bin/env python3
"""Experiment 8: Sliding Window protocols - Go-Back-N and Selective Repeat (simulation).
Usage: python3 sliding_window.py [gbn|sr] [num_frames] [window] [loss_prob] [seed]
Frames are lost with probability `loss_prob` (ACKs are assumed reliable); time is in discrete ticks.
"""
import random, sys


def go_back_n(total, window, loss, seed):
    rnd = random.Random(seed)
    base = nxt = 0
    tx = 0
    print(f"Go-Back-N: {total} frames, window={window}, loss={loss}")
    while base < total:
        # send everything allowed by the window
        while nxt < base + window and nxt < total:
            print(f"Sender  : send Frame {nxt}")
            nxt += 1
            tx += 1
        # receiver accepts only in-order frames
        failed = None
        for f in range(base, nxt):
            if rnd.random() < loss:
                print(f"Channel : Frame {f} LOST")
                failed = f
                break
            print(f"Receiver: Frame {f} OK -> ACK {f}")
        if failed is None:
            base = nxt
        else:
            print(f"Receiver: out-of-order frames discarded; Sender TIMEOUT for Frame {failed}"
                  f" -> go back, resend from {failed}")
            base = nxt = failed
    print(f"Done. Transmissions={tx}, retransmissions={tx - total}")


def selective_repeat(total, window, loss, seed):
    rnd = random.Random(seed)
    acked = [False] * total
    base = 0
    tx = 0
    print(f"Selective Repeat: {total} frames, window={window}, loss={loss}")
    while base < total:
        pending = [f for f in range(base, min(base + window, total)) if not acked[f]]
        for f in pending:
            print(f"Sender  : send Frame {f}")
            tx += 1
            if rnd.random() < loss:
                print(f"Channel : Frame {f} LOST")
            else:
                acked[f] = True
                print(f"Receiver: Frame {f} buffered -> ACK {f}")
        while base < total and acked[base]:           # slide window past delivered frames
            print(f"Receiver: deliver Frame {base} to upper layer; window slides")
            base += 1
        if base < total and not acked[base]:
            print(f"Sender  : TIMEOUT -> retransmit only un-ACKed frames")
    print(f"Done. Transmissions={tx}, retransmissions={tx - total}")


if __name__ == '__main__':
    a = sys.argv[1:] 
    mode = a[0] if a else 'both'
    total = int(a[1]) if len(a) > 1 else 10
    win = int(a[2]) if len(a) > 2 else 4
    loss = float(a[3]) if len(a) > 3 else 0.2
    seed = int(a[4]) if len(a) > 4 else None
    if mode in ('gbn', 'both'):
        go_back_n(total, win, loss, seed)
        print()
    if mode in ('sr', 'both'):
        selective_repeat(total, win, loss, seed)
