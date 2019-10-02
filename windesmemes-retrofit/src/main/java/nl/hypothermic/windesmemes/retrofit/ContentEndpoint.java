package nl.hypothermic.windesmemes.retrofit;

import java.util.List;

import nl.hypothermic.windesmemes.model.Meme;
import nl.hypothermic.windesmemes.model.User;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface ContentEndpoint {

    // TODO mode enum https://stackoverflow.com/questions/35793344/how-to-pass-custom-enum-in-query-via-retrofit
    @GET("get_meme?memes")
    Call<List<Meme>> getMemes(@Query("count") int count, @Query("start") int start, @Query("mode") String mode);

    @GET("get_user")
    Call<User> getUser(@Query("username") String username);

    @GET("get_user?username=")
    Call<User> getCurrentUser(@Header("Cookie") String sessionCookie);

}
