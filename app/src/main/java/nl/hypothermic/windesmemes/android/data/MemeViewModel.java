package nl.hypothermic.windesmemes.android.data;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import nl.hypothermic.windesmemes.model.Meme;
import nl.hypothermic.windesmemes.model.MemeMode;

public class MemeViewModel extends DataViewModel<List<Meme>> {

    @Override
    protected MutableLiveData<List<Meme>> loadData() {
        return ContentRepository.getInstance().getMemes(10, 0, MemeMode.FRESH); // TODO un-hardcode
    }
}
