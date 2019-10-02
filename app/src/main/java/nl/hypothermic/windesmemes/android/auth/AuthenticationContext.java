package nl.hypothermic.windesmemes.android.auth;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import nl.hypothermic.windesmemes.android.BuildConfig;
import nl.hypothermic.windesmemes.android.LogWrapper;
import nl.hypothermic.windesmemes.android.R;
import nl.hypothermic.windesmemes.model.AuthenticationSession;
import nl.hypothermic.windesmemes.model.AuthenticationUser;
import nl.hypothermic.windesmemes.model.Vote;
import nl.hypothermic.windesmemes.retrofit.WindesMemesAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Encapsulation of the session and user.<br />
 * <br />
 * Credentials are not supposed to leave this class.
 */

//=========== BE CAREFUL: CALLBACK HELL UP AHEAD!!! UNREADABLE CODE INCOMING!!! ================//

public class AuthenticationContext {

    private static final String SHARED_PREFERENCES_KEY = "nl.hypothermic.windesmemes.WM.AUTH_PREFS";
    private static final String PREFS_KEY_USER         = "user_token";
    private static final String PREFS_KEY_EXPIRES      = "expires";

    private static final long   EXPIRES_AFTER          = 518400000L; // = 6 days

    /**
     * If there was a user token stored in the shared prefs AND it was used within last 6 days, use it.<br />
     * <br />
     * Otherwise, clear fields mentioned above ^
     */
    // TODO doe dit async
    public static AuthenticationContext fromAndroidContext(Context appContext) {
        AuthenticationContext context = new AuthenticationContext(appContext);
        SharedPreferences preferences = appContext.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);

        String result = preferences.getString(PREFS_KEY_USER, "");
        long modified = preferences.getLong(PREFS_KEY_EXPIRES, 0L);
        long time     = System.currentTimeMillis();

        if (result != null && result.length() == 128 && modified >= time - EXPIRES_AFTER) {
            context.user.setUserToken(result);
            preferences
                    .edit()
                    .putLong(PREFS_KEY_EXPIRES, time)
                    .apply();
        } else {
            preferences
                    .edit()
                    .putString(PREFS_KEY_USER, "")
                    .putLong(PREFS_KEY_EXPIRES, 0L)
                    .apply();
        }

        return context;
    }

    private static void clearAuthInfo(AuthenticationContext authContext, Context appContext) {
        authContext.user.setUserToken(null);
        appContext.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
                .edit()
                .putString(PREFS_KEY_USER, "")
                .putLong(PREFS_KEY_EXPIRES, 0L)
                .apply();
    }

    private final AuthenticationSession session = new AuthenticationSession();
    private volatile AuthenticationUser user = new AuthenticationUser();

    private final Context appContext;

    private AuthenticationContext(Context appContext) {
        this.appContext = appContext;
    }

    /**
     * If session is invalid, this method gets a new session token and stores it in <i>session</i>.<br />
     * <br />
     * This method is also great for warming up the cache.<br />
     * <br />
     * The observer is called from the current thread if the token was cached,
     * else it will be called from a Retrofit thread.
     *
     * @param onFinishedCallback Nullable. Returns when completed.
     */
    public void refreshSession(@Nullable final Observer<Void> onFinishedCallback) {
        if (!session.isValid()) {
            WindesMemesAPI.getInstance().getAuthenticationEndpoint().createSession().enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        for (String cookie : response.headers().values("Set-Cookie")) {
                            if (cookie != null && cookie.startsWith("session=")) {
                                if (cookie.contains(";")) {
                                    cookie = cookie.substring(0, cookie.indexOf(";"));
                                }

                                LogWrapper.error(this, "GET %s\nHeader: %s\nResponse: %s",
                                        call.request().url(), call.request().header("Cookie"), cookie.substring(cookie.indexOf("=") + 1));
                                session.setToken(cookie.substring(cookie.indexOf("=") + 1));
                                reportToCallback();
                                return;
                            }
                        }
                    }
                    onFailure(call, new Exception("Response not successful"));
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    LogWrapper.error(this, "TODO handle exception " + (t.getMessage() != null ? t.getMessage() : "NO MESSAGE"));
                    reportToCallback();
                }

                private void reportToCallback() {
                    if (onFinishedCallback != null) {
                        onFinishedCallback.onChanged(null);
                    }
                }
            });
        } else {
            if (onFinishedCallback != null) {
                onFinishedCallback.onChanged(null);
            }
        }
    }

    /**
     * Windesmemes does not have an API endpoint to verify if user tokens are valid, so only check if null.
     */
    public boolean isUserAuthenticated() {
        return user != null && user.getUserToken() != null;
    }

    /**
     * Authenticates the user using the <i>username</i> and <i>password</i> passed into the function.<br />
     * <br />
     * If the user was already authenticated, this immediately returns true.
     */
    public void userAuthenticate(final Observer<Boolean> onFinishedCallback, final String username, final String password) {
        if (isUserAuthenticated()) {
            onFinishedCallback.onChanged(true);
        } else {
            refreshSession(new Observer<Void>() {
                @Override
                public void onChanged(Void aVoid) {
                    WindesMemesAPI.getInstance().getAuthenticationEndpoint().generateCsrf("login", BuildConfig.X_API_KEY, "session=" + session.getToken()).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful()) {
                                WindesMemesAPI.getInstance().getAuthenticationEndpoint().getUserToken(username, password, response.body(), "session=" + session.getToken()).enqueue(new Callback<Integer>() {
                                    @Override
                                    public void onResponse(Call<Integer> call, Response<Integer> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            LogWrapper.error(this, "GET %s\nHeader: %s\nResponse: %s",
                                                    call.request().url(), call.request().header("Cookie"), response.body());
                                            int responseCode = response.body();
                                            switch (responseCode) {
                                                case 900:
                                                    // TODO clean up this mess + support multiple users
                                                    for (String header : response.headers().values("Set-Cookie")) {
                                                        if (header.startsWith("token=")) {
                                                            String replaced = header.replace("token=", "");
                                                            String formatted = replaced.substring(0, replaced.indexOf(";"));

                                                            user.setUserToken(formatted);
                                                            onFinishedCallback.onChanged(true);

                                                            appContext.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
                                                                    .edit()
                                                                    .putString(PREFS_KEY_USER, formatted)
                                                                    .putLong(PREFS_KEY_EXPIRES, System.currentTimeMillis())
                                                                    .apply();
                                                            return;
                                                        }
                                                    }
                                                    LogWrapper.error(this, "TOOD handle token not found in response");
                                                    break;
                                                case 902:
                                                    LogWrapper.error(this, "TODO invalid credentials");
                                                    break;
                                                case 903:
                                                    LogWrapper.error(this, "TODO general error");
                                                    break;
                                                default:
                                                    LogWrapper.error(this, "TODO invalid response code %d", responseCode);
                                                    break;
                                            }
                                            onFailure(call, new Exception("Error code, see logcat")); // TODO
                                        } else {
                                            onFailure(call, new Exception("Response not successful"));
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<Integer> call, Throwable t) {
                                        LogWrapper.error(this, "TODO handle error %s", t.getMessage()); // TODO
                                        onFinishedCallback.onChanged(false);
                                    }
                                });
                            } else {
                                onFailure(call, new Exception("TODO response not successful"));
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            LogWrapper.error(this, "TODO handle error %s", t.getMessage()); // TODO
                            onFinishedCallback.onChanged(false);
                        }
                    });
                }
            });
        }
    }

    /**
     * Votes on a meme.<br />
     * <br />
     * Returns a StringRes integer in the callback.
     */
    public void vote(final Vote vote, final long memeId, @NonNull final Observer<Integer> onFinishedCallback) {
        if (!isUserAuthenticated()) {
            onFinishedCallback.onChanged(R.string.login_error_not_authed);
        } else {
            refreshSession(new Observer<Void>() {
                @Override
                public void onChanged(Void aVoid) {
                    WindesMemesAPI.getInstance().getAuthenticationEndpoint().generateCsrf(
                            "vote", BuildConfig.X_API_KEY, "session=" + session.getToken()).enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful()) {
                                // Gebruik tijdelijk GEEN json omdat errors niet in json-formaat worden gereturned.
//                                WindesMemesAPI.getInstance().getRatingsEndpoint().vote(vote.getWeight(), memeId, response.body(),
//                                        "session=" + session.getToken() + "; token=" + user.getUserToken()).enqueue(new Callback<ActionResult>() {
//                                    @Override
//                                    public void onResponse(Call<ActionResult> call, Response<ActionResult> response) {
//                                        if (response.isSuccessful() && response.body() != null && response.body().error.equals("ok")) {
//                                            LogWrapper.error(this, "GET %s\nHeader: %s\nResponse: %s",
//                                                    call.request().url(), call.request().header("Cookie"), response.body());
//                                            onFinishedCallback.onChanged(null);
//                                        } else {
//                                            onFailure(call, new Exception("TODO response not successful"));
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onFailure(Call<ActionResult> call, Throwable t) {
//                                        LogWrapper.error(this, "TODO handle error %s", t.getMessage()); // TODO
//                                        onFinishedCallback.onChanged(R.string.login_error_generic);
//                                    }
//                                });
                                WindesMemesAPI.getInstance().getRatingsEndpoint().vote(vote.getWeight(), memeId, response.body(),
                                        "session=" + session.getToken() + "; token=" + user.getUserToken()).enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        if (response.isSuccessful() && response.body() != null) {
                                            LogWrapper.error(this, "GET %s\nHeader: %s\nResponse: %s",
                                                    call.request().url(), call.request().header("Cookie"), response.body());
                                            if (response.body().contains("login")) {
                                                clearAuthInfo(AuthenticationContext.this, appContext);
                                                onFinishedCallback.onChanged(R.string.login_error_not_authed);
                                            } else {
                                                onFinishedCallback.onChanged(null);
                                            }
                                        } else {
                                            onFailure(call, new Exception("TODO response not successful"));
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {
                                        LogWrapper.error(this, "TODO handle error %s", t.getMessage()); // TODO
                                        onFinishedCallback.onChanged(R.string.login_error_generic);
                                    }
                                });
                            } else {
                                onFailure(call, new Exception("TODO response not successful"));
                            }
                        }

                        @Override
                        public void onFailure(Call<String> call, @NonNull Throwable t) {
                            LogWrapper.error(this, "TODO handle error %s", t.getMessage()); // TODO
                            onFinishedCallback.onChanged(R.string.login_error_generic);
                        }
                    });
                }
            });
        }
    }
}
