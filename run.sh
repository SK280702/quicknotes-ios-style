#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
./build.sh
if command -v adb >/dev/null; then
 adb install -r dist/QuickNotes-v1.0.0.apk
 adb shell am start -n com.quicknotes.app/.MainActivity
else
 echo 'APK ready in dist/QuickNotes-v1.0.0.apk. Install on Android 8 or newer.'
fi
