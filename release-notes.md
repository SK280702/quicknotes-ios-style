# QuickNotes 1.6.0

- Widget-wide “Complete reminders in this widget” setting. Enable or disable check circles for the whole widget; reminders no longer need individual permission. Replayed taps cannot uncomplete or duplicate reminders.
- Reworked flagged urgent alarm display: audible high-priority alarm channel, Android 15 activity PendingIntent opt-in, lock-screen activity flags, and direct alarm-screen display while QuickNotes is visible. Removed the duplicate alert that competed with the alarm notification. Alarm setup now includes full-screen access.
- Add List uses a square-plus icon. Add Reminder is centered at the bottom.
- A themed hour/minute/AM–PM picker contains the interval buttons. Each tap adds to the selected time, including across midnight.
- Time intervals appear only when Time is on. All Early Reminder controls appear only when Date is on.
- Custom Repeat immediately opens its rule controls in both reminder editors; no save-and-reopen workaround.
- Completed reminders and their descendants cannot be selected as new subtask parents.

Full-screen alarm tests verify an actual resumed alarm activity with the emulator locked and with QuickNotes in the foreground, plus snooze. Android uses an expanded alert while another app is actively in use; lock-screen full-screen display requires full-screen notification access and an enabled high-priority alarm channel.

Assets: signed upgrade-compatible APK, source ZIP, spec.md and SHA256SUMS.
