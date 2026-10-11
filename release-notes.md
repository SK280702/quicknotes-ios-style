QuickNotes 1.3.0

- Optional named sections with stable IDs and synced order, reminder drops, section dragging/Move Up/Down, and automatic Others for unassigned reminders.
- Recently Deleted with 30-day retention, nested reminder/photo restoration and confirmed clearing. Sync tombstones prevent resurrection.
- Full-swipe Delete expands over Details/Flag and a short fold animation precedes deletion.
- Organized Settings; System/Light/Dark themes reach the outer background and Android bars.
- Thirty preset list icons, emoji selection, eighteen colors, custom picker and exact synced hex values.
- Quick-bar Time/Urgent/Repeat follow Date. List hamburger handles hidden; long-press list drag retained.
- Location features and permissions removed. First-photo metadata is available to custom notification listeners without showing photos in standard notifications.
- Google private app-data sync implementation uses current Google authorization, separate device snapshots, section-aware merge and bundled photos. Developer Google Cloud/OAuth package-and-certificate registration is still required; live Google sign-in/Drive transfer has not been verified. Existing ZIP document sync remains available.

Validation: 14 model tests, browser UI regression suite, compiled/verified update-compatible signed APK, source-match verification, and CI Android 15 native alarm/notification/section/retention/theme checks before release publication. Source ZIP and spec.md included.
