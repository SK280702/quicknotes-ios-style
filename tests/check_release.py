"""Verify the published signed APK embeds the source files from this commit."""
from pathlib import Path
from zipfile import ZipFile
with ZipFile('release/QuickNotes-v1.7.0.apk') as signed, ZipFile('build/aligned.apk') as fresh:
    for path in Path('app/src/main/assets').iterdir():
        assert signed.read('assets/' + path.name) == path.read_bytes(), path
    for name in ['classes.dex', 'AndroidManifest.xml', 'resources.arsc']:
        assert signed.read(name) == fresh.read(name), name
print('Published signed APK matches compiled source, manifest, resources, and all assets.')
