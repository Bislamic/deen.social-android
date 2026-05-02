package org.joinmastodon.android.bislamic;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import org.joinmastodon.android.BuildConfig;

import io.sentry.android.core.SentryAndroid;

/**
 * Bislamic crash reporting wrapper around Sentry.
 *
 * <p>Behaviour:
 * <ul>
 *   <li><b>Default ON (opt-out):</b> Sentry initialises on first app launch
 *       unless the user has explicitly disabled it via Settings.</li>
 *   <li><b>DSN-aware:</b> if no Sentry DSN is configured the wrapper is a
 *       no-op, so the code is safe to merge before a Sentry account is set up.</li>
 *   <li><b>Centralised:</b> changing crash reporting providers
 *       (Sentry → Bugsnag, self-hosted Sentry, etc.) is a single-file edit;
 *       the rest of the codebase calls only this class.</li>
 * </ul>
 *
 * <p>To activate: paste the deen.social Sentry DSN below as {@link #DSN_PROD}
 * (and {@link #DSN_DEBUG} if you want a separate environment for debug builds).
 */
public final class BislamicCrashReporting {
    private static final String TAG = "BislamicCrashReporting";
    private static final String PREFS_NAME = "bislamic_crash_reporting";
    private static final String KEY_ENABLED = "enabled";

    /**
     * Bislamic deen-social-android Sentry project (EU region, Frankfurt).
     * DSN is safe to embed: it permits event submission only, not event reads.
     * Manage events: https://bislamic.sentry.io/issues/?project=4511317452062800
     */
    private static final String DSN_PROD =
        "https://1812b152ffa402d07cf77c5529eeb922@o4511247969943552.ingest.de.sentry.io/4511317452062800";

    /** Empty = debug builds share the production DSN, tagged via environment="debug". */
    private static final String DSN_DEBUG = "";

    private static boolean initialised = false;

    private BislamicCrashReporting() {}

    /**
     * Initialise Sentry. Must be called once from {@code MastodonApp.onCreate()}.
     * No-op if the user has opted out or no DSN is configured.
     */
    public static void initialize(Application app) {
        if (initialised) return;

        SharedPreferences prefs = app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean enabled = prefs.getBoolean(KEY_ENABLED, true);
        if (!enabled) {
            Log.i(TAG, "Crash reporting disabled by user preference; skipping Sentry init.");
            return;
        }

        String dsn = BuildConfig.DEBUG && !TextUtils.isEmpty(DSN_DEBUG) ? DSN_DEBUG : DSN_PROD;
        if (TextUtils.isEmpty(dsn)) {
            Log.i(TAG, "No Sentry DSN configured; crash reporting is a no-op until DSN is set.");
            return;
        }

        SentryAndroid.init(app, options -> {
            options.setDsn(dsn);
            options.setEnvironment(BuildConfig.DEBUG ? "debug" : "release");
            options.setRelease(
                BuildConfig.APPLICATION_ID + "@"
                + BuildConfig.VERSION_NAME + "+"
                + BuildConfig.VERSION_CODE
            );
            // Privacy posture: do not auto-attach user data, IP addresses, etc.
            options.setSendDefaultPii(false);
            options.setEnableUserInteractionTracing(false);
            options.setEnableUserInteractionBreadcrumbs(true);
            // Filter known noisy/unactionable events.
            options.setBeforeSend((event, hint) -> {
                if (event.getMessage() != null
                    && event.getMessage().getMessage() != null
                    && event.getMessage().getMessage().contains("BadParcelableException")) {
                    return null;
                }
                return event;
            });
        });
        initialised = true;
        Log.i(TAG, "Sentry initialised (env=" + (BuildConfig.DEBUG ? "debug" : "release") + ").");
    }

    /**
     * Persist the user's preference for crash reporting. Takes effect on next
     * app launch — Sentry does not officially support disabling mid-run.
     */
    public static void setEnabled(Context ctx, boolean enabled) {
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ENABLED, enabled)
            .apply();
    }

    /** Read the current opt-out state. Default {@code true} (opt-out semantics). */
    public static boolean isEnabled(Context ctx) {
        return ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLED, true);
    }
}
