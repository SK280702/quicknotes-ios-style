"""Reject APKs whose native libraries cannot be mapped by Android's installer."""
import struct
import sys
from pathlib import Path
from zipfile import ZipFile, ZIP_STORED

def check(path):
    libraries = 0
    with ZipFile(path) as archive, open(path, 'rb') as raw:
        for entry in archive.infolist():
            if not entry.filename.startswith('lib/') or not entry.filename.endswith('.so'):
                continue
            libraries += 1
            assert entry.compress_type == ZIP_STORED, f'{entry.filename}: expected uncompressed native library'
            raw.seek(entry.header_offset)
            header = raw.read(30)
            assert header[:4] == b'PK\x03\x04', 'Invalid local ZIP header'
            name_length, extra_length = struct.unpack_from('<HH', header, 26)
            offset = entry.header_offset + 30 + name_length + extra_length
            assert offset % 16384 == 0, f'{entry.filename}: offset {offset} is not 16 KB aligned'
        assert libraries > 0, 'Expected ML Kit native libraries'
    print(f'{Path(path).name}: {libraries} native libraries are 16 KB ZIP aligned')

if __name__ == '__main__':
    check(sys.argv[1])
