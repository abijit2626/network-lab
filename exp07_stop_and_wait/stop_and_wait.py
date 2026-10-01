#!/usr/bin/env python3
"""Experiment 7: Stop-and-Wait and Stop-and-Wait ARQ (simulation with lossy channel).
Usage: python3 stop_and_wait.py [num_frames] [loss_prob] [seed]
"""
import random, sys


def stop_and_wait(n_frames, loss=0.0, seed=None):
    """Plain Stop-and-Wait on an error-free channel: send frame, wait for ACK, send next."""
    print("\n--- Stop and Wait (ideal channel) ---")
    for i in range(n_frames):
        print(f"Sender  : send Frame {i}")
        print(f"Receiver: got  Frame {i} -> send ACK {i}")
        print(f"Sender  : got  ACK {i}")


def stop_and_wait_arq(n_frames, loss=0.3, seed=None):
    """Stop-and-Wait ARQ: 1-bit sequence number, timeout + retransmit on lost frame or ACK."""
    rnd = random.Random(seed)
    print(f"\n--- Stop and Wait ARQ (loss probability {loss}) ---")
    seq, expected = 0, 0
    sent = retx = 0
    for i in range(n_frames):
        while True:
            sent += 1
            print(f"Sender  : send Frame {i} (seq={seq})")
            if rnd.random() < loss:
                print("Channel : Frame LOST")
            else:
                if seq == expected:
                    print(f"Receiver: accepted Frame {i}")
                    expected ^= 1
                else:
                    print("Receiver: duplicate frame discarded")
                ack = expected                      # ACK carries next expected seq
                if rnd.random() < loss:
                    print(f"Channel : ACK {ack} LOST")
                else:
                    print(f"Sender  : received ACK {ack}")
                    seq ^= 1
                    break
            print("Sender  : TIMEOUT -> retransmit")
            retx += 1
    print(f"\nFrames: {n_frames}  transmissions: {sent}  retransmissions: {retx}  "
          f"efficiency: {n_frames / sent:.2%}")


if __name__ == '__main__':
    n = int(sys.argv[1]) if len(sys.argv) > 1 else 5
    p = float(sys.argv[2]) if len(sys.argv) > 2 else 0.3
    seed = int(sys.argv[3]) if len(sys.argv) > 3 else None
    stop_and_wait(n)
    stop_and_wait_arq(n, p, seed)
