# QuickNotes 1.8.1

- Corrected native-library ZIP alignment in the production APK, fixing Android installation rejection, including Pixel 7.
- Preserves the existing application ID and release signing certificate; install over the previous version to retain reminders and settings.
- Build and release checks now inspect every native library's final ZIP offset and verify the final signed APK with zipalign before publication.
- Android CI also installs the actual production APK without incremental installation and tests upgrading from the previous signed 1.7.0 release.
- Includes all 1.8.0 reminder, widget, section color and grocery improvements.
