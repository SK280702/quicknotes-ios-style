#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")/../.."
SDK="${ANDROID_SDK_ROOT:-${ANDROID_HOME:-$PWD/.android-sdk}}"
BT="$SDK/build-tools/35.0.0"
JAR="$SDK/platforms/android-35/android.jar"
mkdir -p build/testclasses build/testdex
java com.sun.tools.javac.Main -source 8 -target 8 -bootclasspath "$JAR:$BT/core-lambda-stubs.jar" -classpath build/classes -d build/testclasses tests/android/NativeTests.java
"$BT/d8" --lib "$JAR" --classpath build/classes --min-api 26 --output build/testdex build/testclasses/com/quicknotes/app/*.class
"$BT/aapt" package -f -M tests/android/AndroidManifest.xml -I "$JAR" -F build/native-tests-unsigned.apk
(cd build/testdex && zip -q -u ../native-tests-unsigned.apk classes.dex)
"$BT/zipalign" -f -p 4 build/native-tests-unsigned.apk build/native-tests-aligned.apk
"$BT/apksigner" sign --ks "${QN_KEYSTORE:-build/developer.jks}" --ks-pass "pass:${QN_KEYSTORE_PASSWORD:-android}" --out build/native-tests.apk build/native-tests-aligned.apk

