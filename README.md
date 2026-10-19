# QuickNotes 1.8.0

Offline Android reminders with lists, sections, widgets, alarms, attachments and document sync.

Run `./install.sh` then `./build.sh` with JDK 17. The build uses Gradle 8.9 and Android SDK 35. `npm test` runs the model tests; `npm run test:ui` runs Playwright. Native Android tests are in `tests/android`.

See [spec.md](spec.md) for complete behavior and [release-notes.md](release-notes.md) for this version. Account linking and reminder text/CSV import were removed. Describe Reminder starts voice input and uses local ML Kit date/time extraction after a one-time model download. Grocery classification is bundled and offline.

The signed production APK is prepared with the original signing key held outside the repository. CI builds a temporary-key APK for native tests, and publishes only after the matching production-signed APK is committed.
