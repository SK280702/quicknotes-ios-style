"""Generate the original offline alarm fallback tone (no downloaded audio)."""
import math
import struct
import wave
from pathlib import Path
rate = 22050
segments = [(550, .32), (0, .10), (660, .32), (0, .10), (880, .32), (0, 1.04)]
output = Path(__file__).resolve().parents[1] / 'app/src/main/assets/alarm.wav'
with wave.open(str(output), 'wb') as wav:
    wav.setnchannels(1)
    wav.setsampwidth(2)
    wav.setframerate(rate)
    for freq, duration in segments:
        count = int(duration * rate)
        samples = []
        for n in range(count):
            fade = min(1, n / (rate * .02), (count - n) / (rate * .03))
            value = .28 * fade * (math.sin(2 * math.pi * freq * n / rate) + .16 * math.sin(4 * math.pi * freq * n / rate)) if freq else 0
            samples.append(struct.pack('<h', int(32767 * value)))
        wav.writeframes(b''.join(samples))
