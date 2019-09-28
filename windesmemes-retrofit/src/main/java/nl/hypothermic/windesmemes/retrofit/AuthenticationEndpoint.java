package nl.hypothermic.windesmemes.retrofit;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface AuthenticationEndpoint {

    /**
     * Manually retrieve cookies from response!
     */
    @GET("create_session")
    Call<Void> createSession();

    @FormUrlEncoded
    @POST("get_token")
    Call<Integer> getUserToken(@Field("username") String username, @Field("password") String password, @Header("Cookie") String sessionCookie);
}
