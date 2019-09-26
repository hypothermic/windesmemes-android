package nl.hypothermic.windesmemes.model;

public class AuthenticationSession {

    /**
     * Result van <i>create_session</i>, 32-char string.
     */
    private volatile String token;

    public AuthenticationSession() {

    }

    public AuthenticationSession(String token) {
        this.token = token;
    }

    public boolean isValid() {
        return token != null;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
