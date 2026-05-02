# Security policy

## Reporting a vulnerability

If you have found a security vulnerability in **the Bislamic patches** (instance picker bypass, donation rerouting, Sentry integration, app identity changes — anything annotated with `// Bislamic:` in this repo), please report it privately:

- **Preferred:** [Open a private security advisory](https://github.com/Bislamic/deen.social-android/security/advisories/new) on this repository.
- **Email:** contact via the form at https://islamic.social.

Please **do not** open a public GitHub issue. Public issues for security vulnerabilities expose users until a fix is shipped.

## Out of scope for this repo

This repository is a downstream fork. Vulnerabilities **in the upstream Mastodon Android client** (the bulk of the code: timeline rendering, notifications, post composer, OAuth client flow, network stack, etc.) should be reported to the upstream Mastodon project instead:

- https://github.com/mastodon/mastodon-android/security
- Their disclosure policy: https://github.com/mastodon/mastodon-android/blob/master/SECURITY.md

When upstream ships a fix, our daily auto-sync workflow opens a PR labelled `[security]` against this fork within 24 hours, and we aim to merge security PRs the same day.

Vulnerabilities in **the deen.social Mastodon server** (federation, posts, accounts, moderation) are not in this app's scope. Contact the deen.social moderators.

## What to expect

| Step | SLA |
|---|---|
| Initial response acknowledging the report | Within 72 hours |
| Triage (confirm, dismiss, or ask for details) | Within 7 days |
| Fix and disclosure | Negotiated based on severity |

We do not currently run a paid bug-bounty program. We do publish credit to reporters in release notes for any accepted report (with the reporter's permission).

## Supported versions

We ship security fixes against the **latest released version** of the deen.social-android app and the **current `master` branch**. Older releases do not receive backports.
