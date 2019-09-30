package nl.hypothermic.windesmemes.android.auth;

import android.content.Context;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import nl.hypothermic.windesmemes.android.BuildConfig;
import nl.hypothermic.windesmemes.android.LogWrapper;

public class AuthenticationManager {

    // TODO lock context so they can't be concurrently modified. (return observer in {@link #acquire})

    private static final HashMap<WeakReference<Context>, AuthenticationContext> CONTEXT_MAPPING = new HashMap<>();

    public static AuthenticationContext acquire(Context appContext) {
        // TEMPORARY SHORTCUT
        if (CONTEXT_MAPPING.size() > 0) {
            for (AuthenticationContext context : CONTEXT_MAPPING.values()) {
                return context;
            }
        }

        // TODO verify that MainActivity appContext == LoginActivity appContext
        LogWrapper.info(appContext, "CONTEXT MAPPING SIZE: %d", CONTEXT_MAPPING.size());

        // if app context exists in mapping, return related auth context
        for (Map.Entry<WeakReference<Context>, AuthenticationContext> entry : CONTEXT_MAPPING.entrySet()) {
            Context entryContext = entry.getKey().get();
            if (entryContext != null && entryContext.equals(appContext)) {
                return entry.getValue();
            }
        }

        // else create new auth context and store in mapping for future use.
        AuthenticationContext authContext = new AuthenticationContext();
        CONTEXT_MAPPING.put(new WeakReference<>(appContext), authContext);
        return authContext;
    }
}
