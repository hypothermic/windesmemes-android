package nl.hypothermic.windesmemes.android.data;

import androidx.lifecycle.MutableLiveData;

import java.util.List;

import nl.hypothermic.windesmemes.model.Meme;
import nl.hypothermic.windesmemes.model.MemeMode;

public class MemeViewModel extends ComplexDataViewModel<List<Meme>, MemeMode> {

    public static final int DEFAULT_SIZE = 10;

    @Override
    protected MutableLiveData<List<Meme>> loadData(MemeMode enumeration, int start) {
        return MemeRepository.getInstance().getMemes(start + DEFAULT_SIZE, start, enumeration);
    }
}
