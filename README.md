# QuickNotes

An offline Android reminders app with the familiar iOS-style layout, colored lists, smart views, subtasks, repeats, and multiple photo/file attachments per reminder.

Download the APK from [Releases](https://github.com/requiredtruth/quicknotes-ios-style/releases/latest). Android 8 or newer. Enable notifications for due reminders.

## Google Drive sync

1. Install Google Drive and sign in.
2. Tap the cloud button → **Create Sync File**. Choose **Drive** in Android's file picker.
3. On another device: cloud button → **Connect Existing Sync File**, then select that same ZIP.
4. Use **Sync Now** after edits when moving between devices. Sync also runs after saves and when returning to the app.

All attachments travel with the file. Local reminders work offline. Drive's provider must support persistent read/write access; upload timing depends on Drive. Avoid simultaneous edits to the same file on two devices. This is Drive document sync, not Google Tasks or iCloud integration.

## Build

Install JDK 17, Node.js 20+, curl, unzip, and zip. Run `./install.sh`, `./cli.sh test`, and `./build.sh`. `./run.sh` installs and launches via adb if available. `ANDROID_SDK_ROOT` selects an existing SDK. Stable release signing requires the private release key; developer builds use their own generated key.

Optional UI test: `npm ci`, `npx playwright install --with-deps chromium`, then `npm run test:ui`.

See [spec.md](spec.md) for features, architecture, sync rules, limits, and validation. QuickNotes uses original assets and is not affiliated with Apple. It does not implement every iOS Reminders feature.
