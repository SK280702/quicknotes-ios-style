# QuickNotes 1.5.0

[Download the latest APK and source](https://github.com/requiredtruth/quicknotes-ios-style/releases/latest)

New: reminder-level widget completion, URL image cards, five-minute alarm snooze, flagged urgent full-screen alarms, persistent flagged due notifications, typed/voice capture with draft review, relative time buttons and custom early alerts. Add List is an always-visible top-bar icon; More → Edit Lists edits/sorts lists. Widget configuration follows the app theme.

See [spec.md](spec.md) for supported capture wording, Android permission behavior and verification details.

# QuickNotes

Offline Android reminders with an iOS-inspired layout, colored lists, sections, subtasks, repeats, photos, and urgent alarms. Android 8 or newer.

Download the APK from [Releases](https://github.com/requiredtruth/quicknotes-ios-style/releases/latest). Allow notifications and **Alarms & reminders** for urgent ringing.

## Organize

Tap a list’s **… → Add Section**, name it, and drag reminders onto its section header or empty area. Others collects unassigned reminders and disappears when every reminder is assigned. Sections have stable IDs and separate order values that sync across devices. Hold a section header to reorder it, or use its **… → Move Up / Move Down** controls. Hold a list row to reorder lists; list hamburger handles are hidden. Reminder handles and long-press dragging remain available.

Tap a reminder for inline editing. Quick-bar order is Date, Time, Urgent, Repeat, Tags, Flag, Photos; the scheduling buttons appear when a date is enabled. Swipe left for Details, Flag, Delete; a full or second left swipe expands Delete and folds the reminder away. Recently Deleted appears after a deletion, retains reminders and photos for 30 days, and supports Restore or **… → Clear Recently Deleted** with confirmation.

New lists have 30 preset icons, emoji entry, 18 preset colors, a custom picker, and editable hex. Icons and exact hex colors travel with sync. **… → Settings → Theme** selects System, Light, or Dark, including the Android system bars and outer background. Location options and location permissions have been removed.

## Sync and backup

**Sync file:** Install Drive and sign in, then choose **Settings → Create Sync File**, select Drive, and save the ZIP. On other devices, **Connect Existing Sync File** selects the same file. Sync runs after edits, on return, or with **Sync Now**. Avoid simultaneous changes to this shared document; provider upload timing varies. Portable ZIP backups include photos and recently deleted content.

**Google account / private app data:** Settings provides **Sign in with Google** through Google Play services authorization, requesting only `drive.appdata`. Device snapshots merge reminders, sections, deletion records and photos, then save automatically after edits and on return. Each installation writes its own snapshot to avoid two devices overwriting one file. Tokens stay outside reminder data and the WebView.

**Developer setup required:** Google account sync is implemented but cannot authenticate until a Google Cloud project has Drive API enabled, an OAuth consent configuration, and an Android OAuth client registered for package `com.quicknotes.app` and the release certificate SHA-1 (see spec.md). No Google Cloud registration or live Google account sync was completed in this release. Existing sync-file and backup methods remain usable without developer OAuth configuration.

## Build

JDK 17+, Python 3, Node.js 20+, curl, unzip, zip, and Android SDK platform/build-tools 35. `./install.sh`, `./cli.sh test`, `./build.sh`; `./run.sh` installs and launches via adb. `ANDROID_SDK_ROOT` selects an SDK. `scripts/google_deps.py` resolves Google AuthorizationClient libraries for this standalone build. A private retained signing key is required for updates; developer builds generate a separate key. `npm ci`, `npx playwright install --with-deps chromium`, `npm run test:ui` verify the browser UI. CI runs native alarm, sync-model, retention, photo-extra, and theme checks on Android 15 before publishing.

See [spec.md](spec.md). Original app assets; not affiliated with Apple and not exhaustive iOS parity.

### 1.4.0
Add QuickNotes from your launcher widget picker. Select any smart or user list and a theme; widgets always sort by due date. Long press the app icon for New Reminder. Lists now save six sort options, and Others only houses unassigned reminders.
