package nl.hypothermic.windesmemes.android.auth;

import android.content.Context;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class AuthenticationManager {

    // TODO lock context so they can't be concurrently modified. (return observer in {@link #acquire})

    private static final HashMap<WeakReference<Context>, AuthenticationContext> CONTEXT_MAPPING = new HashMap<>();

    public static AuthenticationContext acquire(Context appContext) {
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
