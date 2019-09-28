package nl.hypothermic.windesmemes.android.data;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import nl.hypothermic.windesmemes.model.Meme;
import nl.hypothermic.windesmemes.model.MemeMode;

public class MemeViewModel extends ComplexDataViewModel<List<Meme>, MemeMode> {

    @Override
    protected MutableLiveData<List<Meme>> loadData(MemeMode enumeration) {
        return ContentRepository.getInstance().getMemes(10, 0, enumeration); // TODO un-hardcode count, start
    }
}
