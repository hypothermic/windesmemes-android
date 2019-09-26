package nl.hypothermic.windesmemes.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface AuthenticationEndpoint {

    /**
     * Manually retrieve cookies from response!
     */
    @GET("create_session")
    Call<Void> createSession();

    @POST("get_token")
    Call<Integer> getUserToken(@Query("username") String username, @Query("password") String password);
}
