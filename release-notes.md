QuickNotes 1.1.0

- Home Edit controls to show/hide Today, Scheduled, All, and Flagged, with touch drag handles for cards and lists.
- Reminder drag handles and visible Delete buttons in Edit mode. Fixed Android confirmation dialogs so lists and reminders can actually be deleted.
- Blue circular + button and an editor with URL under Notes, separate Date/Time switches, conditional Repeat switch, and a separate Details page.
- Square image previews instead of attachment-name rows; multiple attachments remain supported.
- Completed reminders stay checked and hidden. Show Completed reveals them. Repeating reminders keep one unique ID and reactivate at the next occurrence, without creating duplicates.
- Urgent mode: exact alarm scheduling, looping alarm sound with a bundled fallback if the system sound is unavailable, vibration, lock-screen controls, Dismiss, and Snooze 5 minutes. Android alarm/notification access must be enabled; full-screen access controls automatic lock-screen display.

Android 8+. APK uses the same signing key as 1.0.0 and installs as an update. Google Drive bundle sync retains reminders, attachments, and deletion tombstones. Read spec.md for full behavior and OS limits. The release workflow requires model/UI tests, Android 15 native alarm/confirmation tests, and matching source/APK verification.
