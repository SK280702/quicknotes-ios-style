# QuickNotes Android specification

Version 1.3.0; package `com.quicknotes.app`; versionCode 4; Android 8+ / API 26 minimum; API 35 target.

## Lists, sections and order

Lists save stable IDs, titles, lowercase `#rrggbb` colors, icon strings, timestamps, and order. Thirty original preset icons plus emoji (`emoji:<text>`) are available. Eighteen color presets, native HTML color picker, and validated hex entry preserve the exact color in sync and backup.

`data.sections` is a collection of `{id,list,title,order,updated,deleted?}`. IDs are stable UUIDs; order is an independently saved numeric position rather than encoding position into mutable IDs. Old version-1 bundles without sections migrate to an empty collection. JavaScript and Java merges include sections using the same record-ID/timestamp rule as reminders and lists.

Sections are optional. Add Section asks for its name. Unassigned reminders render in a derived Others section while any active reminder in the list remains unassigned (including hidden completed reminders). Others has no persisted ID and is always last. Dropping a reminder onto a section header or empty area assigns that section and all descendants, clearing the root's subtask parent. Dropping onto a reminder creates a subtask, inheriting its list and section. Cycle checks prevent recursive parenting. Drag sections by long-pressing the header, or use Edit Section → Move Up/Down. Deleting a section moves its reminders into Others. List rows have no hamburger handles; long-press retains drag/reorder. Reminder handles and arrow-key reorder remain.

## Reminders and scheduling

Tap a title/notes to edit inline. Quick bar: Date → Time → Urgent → Repeat → Tags → Flag → Photos. Time/Urgent/Repeat appear when Date is enabled. Date presets: None, Today, Tomorrow, Next Weekend, Next Week, Custom. Custom scheduling presents Date, Time, Urgent, Repeat, Early Reminders. Separate date/time/repeat switches and validation prevent past urgent alarms. Custom recurrence supports intervals, weekdays, month days and ordinal weekdays. Completing a repeating reminder preserves the completed occurrence and creates a new ID with copied descendants/photos; it never silently reopens the original.

Multiple image attachments use Android's document picker and private copied files. Inline and Details previews are square thumbnails; full-screen preview supports close, outside tap, and Back. Native thumbnails are downsampled and EXIF rotation corrected. URL links, notes, priority, tags, subtasks, smart views, Show Completed, and manual/date/priority/title sorting remain available. Home smart cards can be hidden and reordered in Edit mode.

Location options, map editor, native geolocation service, Maps API settings, and all location permissions are removed. Legacy location fields can remain in imported data but are not shown or monitored.

## Deletion and recovery

Swiping left reveals Details, Flag, Delete. A full swipe (72% row width) or a further left swipe on an already open row triggers deletion without another confirmation. Beyond the action width, Delete expands across Details/Flag and follows the reveal distance. A 240-ms fold/collapse occurs before trash marking. Edit/detail deletion still confirms.

Trash sets `deleted=true`, `deletedAt` to the deletion time, and advances `updated`, recursively including descendants. Recently Deleted appears on Home only while recoverable reminders exist, independently of source-list visibility. Restore reactivates the reminder and retained descendants/photos; when its original list is deleted it uses an active list or creates Recovered. Invalid/deleted sections are cleared. A missing/deleted parent is detached. Clear Recently Deleted confirms irreversible clearing. Expiration is exactly 30 × 24 hours after deletion; maintenance runs when the app renders/saves/resumes. Expired and manually cleared content becomes `{id,updated,deleted:true,purged:true,deletedAt}`. Minimal tombstones remain indefinitely to prevent older-device resurrection. Previously referenced files no longer retained by active/recoverable records are removed on native saves. Offline devices and independently exported backups cannot be remotely erased.

## Settings and appearance

Settings is grouped into Appearance, Google account sync, Sync file, Backup, Notifications & alarms. Theme choices System/Light/Dark persist per device and apply to body, outer background, dialogs, WebView, status/navigation bars and native icon contrast. System selection follows the current OS preference. Theme/UI preferences are device-local, whereas list colors/icons and section order sync.

## Sync

Portable format remains version 1 with `lists`, `items`, and `sections`. Per-record last-modified timestamps plus deterministic equal-time serialized-record comparison resolve merges; clock accuracy matters. JSON imports validate IDs, duplicate IDs, colors, priorities, and dates. ZIP paths are restricted to `data.json` and safe `attachments/<id>` entries. Old bundles gain empty sections. Deleted records retain their photos while recoverable; purged records do not reference photos.

Sync-file mode uses Android Storage Access Framework with persistent read/write permission. Install/sign in to Google Drive or another provider; create/select the same QuickNotes ZIP on each device. Reads merge into local data before writes. Provider uploads/cache timing vary and simultaneous document writes can race, so finish one device before switching. Local data remains on errors/disconnect. Before replacing a document, a local recovery ZIP is written. Export and merge-import include all referenced active/recoverable photos.

Google account mode uses Google Play services `AuthorizationClient` 22.0.0, scope `https://www.googleapis.com/auth/drive.appdata`, consent resolution, silent reauthorization after consent, and expired-token cache invalidation. Tokens stay in native memory/Google's token cache, never in WebView or ZIPs. REST uses TLS, timeouts, bounded transfers, and visible failures. A private app-data snapshot named `quicknotes-v1-<installation UUID>.zip` is written per installation. Sync reads/merges all snapshots, merges again against current local data under the storage lock, then updates its own snapshot. Pagination is supported. This avoids simultaneous devices replacing one shared Drive object. Disconnect disables future automatic sync but retains cloud data and consent; Google account permissions can revoke access. Sync runs after saves with a 1.8-second debounce, on return, and via Sync Now; this is not guaranteed continuous background or real-time collaborative sync. Google Play services is required for account mode; the Drive app itself is not.

Limits: 20 MiB per image, 100 MiB uncompressed bundle, 5 MiB imported manifest, 1,000 Google snapshots and 200 MiB downloaded snapshot bytes per sync. These limits report errors without clearing local reminders.

### Google developer configuration prerequisite

Account-mode code is compiled and included, but no Google Cloud project/client configuration was created and no live sign-in/Drive transfer was verified for this release. Enable Drive API, configure the OAuth consent screen and allowed audience/test users, and register an Android OAuth client for `com.quicknotes.app` with the signed release SHA-1: `CC:E0:47:FC:59:3F:A8:C5:30:2D:7E:8A:63:90:B0:E4:47:9A:C3:17`. Google matches the installed package/certificate; no OAuth client secret is embedded. Existing file sync remains available without this registration. See Google's [Android authorization guide](https://developer.android.com/identity/authorization) and [Drive app-data guide](https://developers.google.com/workspace/drive/api/guides/appdata).

## Native alerts and notification photo metadata

Normal notifications include title/notes, Dismiss, and Mark As Completed. Urgent alarms use exact alarm-clock scheduling, foreground audio, wake lock, looping local alarm WAV, Dismiss/Complete/Snooze controls, and reconciliation on edits/deletion. Permission/volume/device restrictions can delay or prevent alerts; force-stop/device shutdown prevent delivery. Recurrence actions preserve original completed records and create the next independent occurrence. Boot/time/timezone changes reschedule.

The first image supplies custom notification extras `quicknotes.photoId`, `quicknotes.photoMime`, and `quicknotes.contactPhoto` (bounded thumbnail Bitmap). Notification-listener apps can read those extras after the user grants notification access. No visible large icon or BigPictureStyle is set; stock notification fronts remain title/notes. Both ordinary and urgent notification builders attach the extras.

## Build, release and validation

Standalone aapt/javac/d8/zipalign/apksigner build with JDK 17 and platform/build-tools 35. Python resolves pinned Google authorization dependency versions and highest requested transitives; only runtime classes/resources are bundled. AAR ID resources are adapted for AAPT1. Build directories are cleaned for reproducible classes. Stable signing uses the retained private keystore; no keys, tokens, private attribution, or account credentials are committed.

Release includes `QuickNotes-v1.3.0.apk`, source ZIP, this specification and SHA256SUMS. CI checks the signed APK against a fresh build and runs model/UI and native Android 15 tests before publishing from main. Model tests cover recurrence/cycles/merge/order, section migration/sync, 30-day expiration, restore and tombstones. UI tests cover inline editing, swipe actions, folding deletion, scheduling, repeats, photos/subtasks, list long-press, section drops, emoji/hex persistence, settings/themes and recovery/clear. Native tests cover real urgent audio, permission-sensitive scheduling/reconciliation, confirmation, recurrence notification actions, section merge, expiration, system bars, and hidden photo metadata. Live Google authorization/Drive transfer remains unverified pending external configuration.

Not exhaustive iOS parity: no iCloud, Siri, shared-list assignment, widgets or Apple integrations.

## Version 1.4.0 — widgets and organization

Android AppWidgetProvider with RemoteViews ListView, a RemoteViewsService, and configuration Activity. Each widget selects All, Scheduled, Today (including overdue), Flagged, or a live user-created list and chooses System, Light, or Dark. It resizes horizontally/vertically and scrolls its reminder rows. Incomplete, nondeleted reminders in live lists are sorted by due timestamp ascending, title as a tie breaker, undated last. Widgets refresh on saves, sync/import, notification completion, clock/timezone/day changes, and every 30 minutes. Widget settings remain local to each home-screen instance. Tapping a title opens its list, a row opens its editor, + opens a new reminder in the selected user list, and the gear reconfigures it.

Launcher static shortcut New Reminder and in-app empty-home long-press menu create a reminder. MainActivity consumes new-reminder and widget intents after WebView readiness, including warm launches. Sorting choices are Manual, Due date, Priority, A–Z, Flagged first, Newest first. List preferences sync as updated list records; smart-list preferences stay local. Newest uses creation order (legacy records use their existing order). Dragging manually resets the list preference to Manual. Post-drag taps do not navigate or edit lists. Others is a derived gray temporary group based on effective root section membership; it disappears when no visible unassigned reminders remain and reappears after dropping on the remove-from-section area.

Google App Actions / Gemini are omitted for this sideload release because official App Actions deployment requires Play Store publication and separate review: https://developer.android.com/develop/devices/assistant/get-started . Android launcher shortcuts do not provide Gemini conversation or list/date/urgent voice extraction.
