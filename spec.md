# QuickNotes — Android Reminders

Version: 1.0.0. Package: `com.quicknotes.app`. Minimum Android: 8.0 (API 26). Target: Android 15 (API 35).

## Product
An Android reminders app closely modeled on the familiar iOS Reminders layout: large headings, rounded smart-list cards, colored list icons, completion circles, grouped detail forms, and light/dark appearance. It uses original SVG icons and system fonts. It is not Apple software and is not a full 1:1 implementation of every iOS feature.

## Implemented behavior
- Today (including overdue), Scheduled, All, Flagged, and Completed views.
- Custom lists: name, seven icon choices, eight colors, rename, reorder, delete.
- Reminders: title, multiline notes, due date/time, none/low/medium/high priority, flag, tags, HTTP(S) link, list assignment, one level of subtasks.
- Daily, weekly, monthly, and yearly repeats. Completing a repeating reminder preserves the completed occurrence and creates the next future occurrence, including its attachment references. Completion propagates to existing subtasks; repeated occurrences currently do not clone subtasks.
- Search title, notes, and tags. Tag buttons filter reminders. Sort by manual order, title, due date, or priority. Move reminder up/down in its editor. Show/hide completed and clear completed.
- Multiple photos/files per item via Android's multi-select document picker. Files are copied into private app storage, survive original-file deletion, and open in a compatible Android viewer. Images preview in the details sheet. Removing an attachment removes its reference; unused private files are retained in 1.0.0.
- Persistent offline data saved with temporary-file rename; all file operations and bundle synchronization run on a serial background executor.
- Due-date notifications through AlarmManager and notification channels, restored after reboot. Android notification permission is requested. Alerts are inexact and subject to battery restrictions. No promise of exact-to-the-minute delivery or alerts while force-stopped. Launch the app after restarting if the system blocks boot delivery.

## Google Drive synchronization
No OAuth client ID, developer account, API key, or server is needed. Uses Android's Storage Access Framework with Google Drive's installed document provider. Select **Sync & Backup → Create Sync File**, choose **Drive** in the picker, and save `QuickNotes-sync.zip`. On another device, choose **Connect Existing Sync File** and select the same file. The provider must grant persistent read/write permission. Some providers/accounts may not support updating files; the app reports errors and retains local data.

Each bundle contains `data.json` and all referenced attachment bytes. Sync reads the selected bundle, merges by record ID and per-record modification timestamp, persists locally, then writes the merged bundle. Deleted records remain as tombstones to prevent resurrection. Equal-time conflicts use a deterministic serialized-record tie break. Clock accuracy affects conflict resolution. Simultaneous writes by two devices are not transactionally locked and may race; for dependable transfer, finish syncing one device before syncing the other.

Sync runs after saved edits (1.8-second debounce), after returning to the app, and on **Sync Now**. The Drive provider controls actual cloud upload timing and may serve cached data. This is file-based Drive sync, not Google Tasks sync, iCloud sync, continuous background network sync, or collaborative real-time editing. Google Drive must be installed and signed in. Connecting a file performs a merge, not a destructive replacement. Disconnect keeps local data.

Limits: 20 MiB per attachment; 100 MiB total uncompressed bundle; 5 MiB manifest when importing. Import rejects unsafe file paths, unsupported versions, and missing attachments. Bundle export/import includes attachments. An internal `last-sync.zip` is kept before the remote file is rewritten. Provider writes cannot be guaranteed atomic; local reminders are retained if a cloud write fails.

## Architecture and privacy
Local HTML/CSS/JavaScript rendered in Android WebView, Java native bridge, private JSON store, read-only non-exported attachment ContentProvider, AlarmManager receiver. WebView serves only packaged assets and private attachment bytes from `https://quicknotes.local`; external navigation opens the browser. No internet permission, advertising, tracking, third-party backend, broad storage permission, or Apple assets. Drive receives data only via the user-selected provider. Portable bundles are plain ZIP files and are not encrypted. Android OS app backup is disabled to avoid splitting reminder data from its private attachments.

## Scope gaps relative to iOS Reminders
No Siri/Apple Intelligence, iCloud, location/message-triggered alerts, widgets, grocery categorization, shared-list assignment, sections, or Apple-native gesture parity. The app provides the main reminders workflow and attachment-capable Drive file sync; it must not be described as exhaustive iOS parity.

## Build and release
`./install.sh` installs the Android 35 SDK/build tools (requires JDK 17, curl, unzip). `./build.sh` compiles Java/resources, packages assets, aligns and signs the APK, and verifies signatures. No Gradle or paid service is required. `./run.sh` builds, installs with adb, and launches when adb is available. `./cli.sh test` runs Node model tests; `./cli.sh build` builds.

For stable release signing, restore the privately retained QuickNotes key and set `QN_KEYSTORE` and `QN_KEYSTORE_PASSWORD`. Never commit keys. The initial key's password is `android` (key custody is private). Developer builds create their own key under ignored `build/`; a differently signed APK cannot update the published app.

Release assets: `QuickNotes-v1.0.0.apk`, `QuickNotes-source-v1.0.0.zip`, `spec.md`, and `SHA256SUMS`. A main-branch workflow builds and tests then publishes the same main commit as tag `v1.0.0`; the initial workflow uploads the already signed release APK committed under `release/` and verifies it against a fresh source build. Future releases must be signed using the retained key.

## Validation
Local: six Node model tests passed (merge/deletion/conflicts, recurring completion, monthly/leap-date handling, smart lists/search, invalid bundle rejection); APK compiled, aligned, and verified with v2/v3 signatures; manifest minimum/target SDK checked. Playwright UI test exercises list creation, escaped names, reminder editing, multiple attachment references, recurrence, search, persistence, and dark mode. Release CI installs Chromium and runs that test before publishing. This environment has no Android emulator/device; notification delivery, native picker/attachment-viewer interactions, and Google Drive round trips need Android device validation. No claim of device-tested Drive sync is made.
