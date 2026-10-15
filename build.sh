#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
SDK="${ANDROID_SDK_ROOT:-${ANDROID_HOME:-$PWD/.android-sdk}}"
BT="$SDK/build-tools/35.0.0"
JAR="$SDK/platforms/android-35/android.jar"
[ -f "$JAR" ] || ./install.sh
mkdir -p build/classes build/dex build/generated dist
KEY="${QN_KEYSTORE:-$PWD/build/developer.jks}"
PASS="${QN_KEYSTORE_PASSWORD:-android}"
if [ ! -f "$KEY" ]; then
 if [ -n "${QN_KEYSTORE:-}" ]; then echo 'Configured signing key is missing.'; exit 1; fi
 keytool -genkeypair -keystore "$KEY" -storepass "$PASS" -keypass "$PASS" -alias quicknotes -keyalg RSA -keysize 3072 -validity 10000 -dname 'CN=QuickNotes Developer'
fi
python3 scripts/compile.py
(cd build/dex && zip -q -u ../unsigned.apk classes*.dex)
"$BT/zipalign" -f -p 4 build/unsigned.apk build/aligned.apk
"$BT/apksigner" sign --ks "$KEY" --ks-pass "pass:$PASS" --out dist/QuickNotes-v1.4.0.apk build/aligned.apk
"$BT/apksigner" verify --verbose dist/QuickNotes-v1.4.0.apk
