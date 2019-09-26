package nl.hypothermic.windesmemes.android.auth;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import nl.hypothermic.windesmemes.android.LogWrapper;
import nl.hypothermic.windesmemes.model.AuthenticationSession;
import nl.hypothermic.windesmemes.model.AuthenticationUser;
import nl.hypothermic.windesmemes.retrofit.WindesMemesAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Encapsulation of the session and user.
 *
 * Credentials are not supposed to leave this class.
 */
public class AuthenticationContext {

    private final AuthenticationSession session = new AuthenticationSession();
    private volatile AuthenticationUser user;

    /**
     * If session is invalid, this method gets a new session token and stores it in <i>session</i>.<br />
     * <br />
     * This method is also great for warming up the cache.<br />
     * <br />
     * The observer is called from the current thread if the token was cached,
     *      else it will be called from a Retrofit thread.
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
                            if (cookie != null && cookie.length() == 32) { // TODO un-hardcode
                                session.setToken(cookie);
                                return;
                            }
                        }
                    }
                    onFailure(call, new Exception("Response not successful"));
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    LogWrapper.error(this, "TODO handle exception " + (t != null ? t.getMessage() : "NO MESSAGE"));
                }

                private void reportToCallback() {
                    if (onFinishedCallback != null) {
                        onFinishedCallback.onChanged(null);
                    }
                }
            });
        } else {
            onFinishedCallback.onChanged(null);
        }
    }

    public boolean isUserAuthenticated() {
        return user != null && user.getUserToken() != null;
    }

    public void userAuthenticate(final Observer<Boolean> onFinishedCallback, String username, String password) {
        if (isUserAuthenticated()) {
            onFinishedCallback.onChanged(true);
        } else {
            WindesMemesAPI.getInstance().getAuthenticationEndpoint().getUserToken(username, password).enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    if (response.isSuccessful()) {
                        int responseCode = response.body();
                        switch (responseCode) {
                            case 900:
                                for (String header : response.headers().values("Set-Cookie")) {
                                    if (header.startsWith("token=")) {
                                        user.setUserToken(header.replace("token=", ""));
                                        LogWrapper.info(this, "Token found in response: %s", header);
                                    }
                                }
                                LogWrapper.error(this, "TOOD handle token not found in response");
                                break;
                            case 902:
                                LogWrapper.error(this, "TODO invalid credentials");
                                break;
                            case 903:

                            default:
                                LogWrapper.error(this, "TODO invalid response code %d", responseCode);
                                break;
                        }
                    } else {
                        onFailure(call, new Exception("Response not successful"));
                    }
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    LogWrapper.error(this, "TODO handle error %s", t.getMessage());
                }
            });
        }
    }
}
