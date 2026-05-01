# Android universal links (assetlinks.json)

The `<intent-filter android:autoVerify="true">` blocks in `AndroidManifest.xml`
ask Android to verify, on first install, that the app is genuinely allowed to
handle deep links to the listed domains. The verification is done by fetching
a JSON file from each domain's `.well-known` path:

- `https://deen.social/.well-known/assetlinks.json`
- `https://islamic.social/.well-known/assetlinks.json`

## What you need to deploy

Copy `assetlinks.template.json` to each domain's `.well-known/` folder, after
filling in the `sha256_cert_fingerprints` field. **Both domains can serve the
same file** — they grant the same app the same permission.

## Getting the SHA-256 fingerprint

The fingerprint is derived from the signing key used to publish the app to
Google Play. You will have two cases over the lifetime of the project:

### 1. Local debug builds (today)

While developing on your machine, Android signs APKs with a default debug
keystore at `~/.android/debug.keystore`. Read the fingerprint with:

```bash
keytool -list -v \
  -keystore "$HOME/.android/debug.keystore" \
  -alias androiddebugkey \
  -storepass android -keypass android \
  | grep "SHA256:"
```

Add this to `sha256_cert_fingerprints` if you want universal-link verification
to succeed against debug builds. **Do not ship this fingerprint in
production** — every Android developer's debug keystore differs, but anyone
can also generate matching keys, so debug-fingerprint verification is not
trustworthy.

### 2. Production builds (later, when you publish to Google Play)

Google Play "Play App Signing" generates and holds the production signing key.
After you upload your first release:

1. Open Google Play Console → your app → **Release > Setup > App integrity**
2. Copy the **SHA-256 certificate fingerprint** under "App signing key
   certificate"
3. Replace the placeholder in `assetlinks.json` and redeploy

## Verifying

Once the file is deployed, run:

```bash
curl -fsSL https://deen.social/.well-known/assetlinks.json | jq .
curl -fsSL https://islamic.social/.well-known/assetlinks.json | jq .
```

Both should return the JSON with your real SHA-256 fingerprint, served with
`Content-Type: application/json`.

Then on a device/emulator with the app installed:

```bash
adb shell pm get-app-links social.deen.android
```

You should see `verified` next to the domains. If you see `legacy` or
`unverified`, check the file is reachable (no redirects, no auth wall) and the
fingerprint matches the keystore actually signing the APK.

## Multiple fingerprints

You can list more than one fingerprint in the array, useful when transitioning
between keys or when debug + production both need to verify. Both fingerprints
must come from keys you control.
