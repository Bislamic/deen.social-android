# Bislamic Fork — deen.social Android Client

This repository is **Bislamic's downstream fork** of [mastodon/mastodon-android](https://github.com/mastodon/mastodon-android), customized as the official Android client for the [deen.social](https://deen.social) Muslim community Mastodon instance.

## Relationship to upstream

| | |
|---|---|
| Upstream | `mastodon/mastodon-android` (GPL-3.0) |
| This fork | `Bislamic/deen.social-android` (GPL-3.0) |
| Tracking branch | `master` ← upstream `master` |
| Sync cadence | Daily watcher + automated PRs on new releases |

## How we stay in sync with upstream

A GitHub Actions workflow (`.github/workflows/upstream-sync.yml`) runs every day at 06:00 UTC and:

1. Fetches the latest upstream tags
2. Checks if we're behind
3. If yes: creates `sync/upstream-vX.Y.Z` branch, merges upstream into it
4. Opens a PR labelled with detected change types:
   - `security` — CVEs, vulnerability fixes, XSS/CSRF/injection
   - `feature` — new functionality
   - `bugfix` — bug fixes only
   - `needs-resolution` — merge conflicts requiring human review

**Manually trigger a sync** at any time:
```bash
gh workflow run upstream-sync.yml
# Or sync to a specific tag:
gh workflow run upstream-sync.yml -f target_ref=v2.11.11
```

## What we customize (the "deen layer")

The customization is intentionally minimal and lives in well-defined places so upstream merges stay clean. Roughly:

| Area | Files affected | Purpose |
|---|---|---|
| Default instance | `mastodon/src/main/java/.../api/MastodonAPIController.java` | Hardcode `deen.social` |
| Onboarding flow | `mastodon/src/main/java/.../onboarding/InstanceCatalogActivity.java` | Skip server picker |
| OAuth client | hardcoded `client_id` / `client_secret` for deen.social | Auth registration |
| App identity | `AndroidManifest.xml`, `build.gradle` | Package: `social.deen.android` |
| Branding | `mastodon/src/main/res/` (drawables, mipmaps, values) | Icons, colors, strings |
| Telemetry | New module: Sentry crash reporting (opt-out) | Bug visibility |

Patches and customization scripts live in `patches/` (to be added).

## License

This project inherits **GPL-3.0** from upstream. As required:

- All modifications are public in this repository
- You can use, modify, and redistribute under the same terms
- See [LICENSE](LICENSE) for full text

## Trademark notice

"Mastodon" is a trademark of Mastodon GmbH. This fork is **not affiliated with or endorsed by Mastodon GmbH**. The product name is "deen.social" and the app does not use the Mastodon name or logo.

## Reporting issues

| Issue type | Where to report |
|---|---|
| Bugs in deen.social-specific behavior | [Issues here](https://github.com/Bislamic/deen.social-android/issues) |
| Bugs in core Mastodon Android client | [mastodon/mastodon-android](https://github.com/mastodon/mastodon-android/issues) |
| Server-side issues (federation, posts, etc.) | Contact deen.social moderators |

## Contributing

PRs are welcome for deen.social-specific improvements. For changes that would benefit ALL Mastodon Android users, please contribute upstream first — we'll pick them up via the auto-sync.

## Build & release

### Debug builds (today)

Every push to `master` and every PR triggers
`.github/workflows/build-debug-apk.yml` which:

1. Builds `:mastodon:assembleDebug` on JDK 21 (Temurin) on `ubuntu-24.04`.
2. Asserts via `aapt2 dump badging` that the APK declares
   `package=social.deen.android` and `label='deen.social'` — if the
   Bislamic identity patches were ever lost in a future upstream-sync
   merge, the workflow fails before the merge can land.
3. Uploads the resulting APK as a 30-day GitHub Actions artifact
   named `deen-social-android-debug-<sha>`.

Testers can grab the latest build by opening the latest run in the
Actions tab and downloading the artifact. No Google Play account
required.

Locally, the same build runs as:

```bash
./gradlew :mastodon:assembleDebug
adb install -r mastodon/build/outputs/apk/debug/mastodon-debug.apk
```

See [`BUILDING.md`](BUILDING.md) for full local-setup instructions and
common errors.

### Release builds

Will mirror the structure of upstream's `build_and_deploy.yml` with
Bislamic's own signing keys and Google Play Console credentials. To be
documented once the Bislamic Play Console account is enrolled.

## Crash reporting

Crashes and unhandled exceptions are reported to the Bislamic Sentry
organisation, project `deen-social-android` (EU region, Frankfurt).

- Issues UI: https://bislamic.sentry.io/issues/?project=4511317452062800
- Default ON, opt-out via Settings → About → "Send crash reports".
- Implementation: `mastodon/src/main/java/.../bislamic/BislamicCrashReporting.java`
  initialises the SDK from `MastodonApp.onCreate()`. Privacy posture is
  `sendDefaultPii=false`; only stack traces and breadcrumbs are sent.

The DSN embedded in source is safe to publish — it permits event
submission only, not event reads. This is standard practice for every
Sentry-instrumented mobile client.
