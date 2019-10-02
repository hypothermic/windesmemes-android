package nl.hypothermic.windesmemes.retrofit;

import nl.hypothermic.windesmemes.model.ActionResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

public interface RatingsEndpoint {

    @GET("vote")
//    Call<ActionResult> vote(@Query("value") int voteValue, @Query("meme_id") long memeId, @Query("form_token") String formToken, @Header("Cookie") String cookies);
    Call<String> vote(@Query("value") int voteValue, @Query("meme_id") long memeId, @Query("form_token") String formToken, @Header("Cookie") String cookies);

}
