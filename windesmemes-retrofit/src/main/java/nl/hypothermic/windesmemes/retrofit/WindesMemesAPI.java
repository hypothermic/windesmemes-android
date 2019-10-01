package nl.hypothermic.windesmemes.retrofit;

import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class WindesMemesAPI {

    public static final String BASE_URL = "https://windesmemes.nl";
    public static final String API_URL = BASE_URL + "/api/";

    public static final int VERSION_MAJOR = 10, VERSION_MINOR = 0, VERSION_REVISION = 0, VERSION_PATCH = 1;
    public static final String VERSION_READABLE = String.format(Locale.US,
            "%d.%d.%d.%04d", VERSION_MAJOR, VERSION_MINOR, VERSION_REVISION, VERSION_PATCH);

    private static Retrofit RETROFIT;
    private static WindesMemesAPI INSTANCE;

    public static WindesMemesAPI getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WindesMemesAPI();
        }
        return INSTANCE;
    }

    private final AuthenticationEndpoint authenticationEndpoint;
    private final ContentEndpoint contentEndpoint;
    private final RatingsEndpoint ratingsEndpoint;

    private WindesMemesAPI() {
        authenticationEndpoint = getRetrofit().create(AuthenticationEndpoint.class);
        contentEndpoint = getRetrofit().create(ContentEndpoint.class);
        ratingsEndpoint = getRetrofit().create(RatingsEndpoint.class);
    }

    private Retrofit getRetrofit() {
        if (RETROFIT == null) {
            OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
            httpClientBuilder.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request.Builder requestBuilder = chain.request().newBuilder();
                    requestBuilder.header("Content-Type", "application/json");
                    requestBuilder.header("Accept", "application/json");
                    requestBuilder.header("User-Agent", "WM-API-Retrofit/" + VERSION_READABLE);
                    return chain.proceed(requestBuilder.build());
                }
            });
            RETROFIT = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClientBuilder.build())
                    .build();
        }
        return RETROFIT;
    }

    public ContentEndpoint getContentEndpoint() {
        return contentEndpoint;
    }

    public AuthenticationEndpoint getAuthenticationEndpoint() {
        return authenticationEndpoint;
    }

    public RatingsEndpoint getRatingsEndpoint() {
        return ratingsEndpoint;
    }
}
