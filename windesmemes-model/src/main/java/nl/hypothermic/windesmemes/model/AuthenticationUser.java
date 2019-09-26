package nl.hypothermic.windesmemes.model;

public class AuthenticationUser {

    /**
     * Result van <i>get_token</i>, 128-char string.
     */
    private volatile String userToken;

    public AuthenticationUser() {
    }

    public AuthenticationUser(String userToken) {
        this.userToken = userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getUserToken() {
        return userToken;
    }
}
