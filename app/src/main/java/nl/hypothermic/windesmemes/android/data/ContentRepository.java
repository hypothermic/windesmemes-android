package nl.hypothermic.windesmemes.android.data;

import androidx.lifecycle.MutableLiveData;

import java.util.Collections;
import java.util.List;

import nl.hypothermic.windesmemes.android.LogWrapper;
import nl.hypothermic.windesmemes.model.Meme;
import nl.hypothermic.windesmemes.model.MemeMode;
import nl.hypothermic.windesmemes.retrofit.WindesMemesAPI;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContentRepository {

    private static ContentRepository INSTANCE;

    public static ContentRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ContentRepository();
        }
        return INSTANCE;
    }

    private ContentRepository() {

    }

    public MutableLiveData<List<Meme>> getMemes(int count, int start, MemeMode mode) {
        final MutableLiveData<List<Meme>> memeData = new MutableLiveData<>();
        WindesMemesAPI.getInstance().getContentEndpoint().getMemes(10, 0, mode.getAsString()).enqueue(new Callback<List<Meme>>() {
            @Override
            public void onResponse(Call<List<Meme>> call, Response<List<Meme>> response) {
                if (response.isSuccessful()) {
                    memeData.setValue(response.body());
                } else {
                    LogWrapper.error(this, "Retrofit returned unsuccessful %d: %s", response.code(), response.message());
                }
            }

            @Override
            public void onFailure(Call<List<Meme>> call, Throwable t) {
                memeData.setValue(Collections.<Meme>emptyList());
                LogWrapper.error(this, "Retrofit returned failure %s", t.getMessage());
            }
        });
        return memeData;
    }
}
