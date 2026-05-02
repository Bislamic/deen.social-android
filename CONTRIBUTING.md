# Contributing to Bislamic deen.social-android

Salam, and thank you for considering a contribution.

This repository is a **downstream fork** of [mastodon/mastodon-android](https://github.com/mastodon/mastodon-android) maintained by Bislamic for the deen.social Muslim community. The relationship to upstream is documented in [FORK.md](FORK.md). Most importantly: we **track upstream daily** and our local patches are intentionally minimal so upstream changes flow through cleanly.

## What kind of contributions are welcome here

| Kind | Where it goes |
|---|---|
| Bugs / improvements specific to **the deen.social experience** (instance picker bypass, Sentry integration, deen.social branding, donation routing to islamic.social) | Issues + PRs **here** in `Bislamic/deen.social-android`. |
| Bugs / improvements **in the core Mastodon Android client** (timeline rendering, notifications, post composer, OAuth flow) | Please contribute upstream first at [mastodon/mastodon-android](https://github.com/mastodon/mastodon-android). The auto-sync workflow will pull your fix into our fork automatically once it lands upstream. |
| Translations of the rebranded strings (`bislamic_settings_*`, `welcome_to_mastodon`, `welcome_paragraph*`) | PRs here. We do not yet have a Crowdin project for the fork; raw `values-<locale>/strings.xml` edits are fine. |
| Server / federation / moderation issues | These are **not in this repo**. Contact the deen.social moderators directly. |

## Before you open a PR

1. **Build locally.** Make sure `./gradlew :mastodon:assembleDebug` succeeds. See [BUILDING.md](BUILDING.md) for one-time setup.
2. **Identity must survive.** Our CI workflow asserts via aapt2 that the APK declares `package=social.deen.android` and `label='deen.social'`. If your patch accidentally changes either, the workflow fails — please re-check before pushing.
3. **Mark Bislamic-specific changes.** Every line we add over upstream is annotated with a `// Bislamic:` comment that explains *what* we changed and *why*. Follow the same convention so the next maintainer (or your future self) can audit our patches at a glance with `git log --grep=bislamic`.
4. **Keep the diff small.** When you can, prefer the smallest possible change that achieves the goal. Dead code from upstream methods we no longer call is left in place on purpose — see [`SplashFragment.java`](mastodon/src/main/java/org/joinmastodon/android/fragments/SplashFragment.java) for a worked example. Larger diffs mean more merge conflicts on upstream syncs.

## Code style

Follow the upstream Mastodon Android style — tabs for indentation, K&R braces, no trailing whitespace. There is no formal formatter in the repo; please match the surrounding code. Don't reformat upstream files for personal preference, even if you disagree with the style.

## Reporting security issues

Please do **not** open a public GitHub issue for security vulnerabilities. Email the maintainers directly via the contact on https://islamic.social or use [GitHub's private vulnerability reporting](https://github.com/Bislamic/deen.social-android/security/advisories/new) on this repo.

If the vulnerability is in upstream Mastodon Android (not in our patches), please follow [Mastodon's security policy](https://github.com/mastodon/mastodon-android/security) instead — we will pick up the fix via auto-sync once they ship it.

## License

By contributing, you agree that your contribution will be licensed under the same **GPL-3.0-only** license that this fork inherits from upstream. Patches that cannot be relicensed under GPL-3.0 cannot be accepted.

## Code of conduct

Please be respectful, helpful, and patient. We expect collaborators to act in good faith and to assume good faith of others. Insults, harassment, and bigoted speech of any kind have no place in this project's spaces (issues, PRs, discussions). Maintainers may remove comments or suspend access as needed to keep these spaces welcoming.
