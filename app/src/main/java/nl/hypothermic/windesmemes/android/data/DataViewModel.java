package nl.hypothermic.windesmemes.android.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public abstract class DataViewModel<T> extends ViewModel {

    private MutableLiveData<T> cachedData;

    public LiveData<T> getData() {
        if (cachedData == null) {
            cachedData = loadData();
        }
        return cachedData;
    }

    protected abstract MutableLiveData<T> loadData();

}
