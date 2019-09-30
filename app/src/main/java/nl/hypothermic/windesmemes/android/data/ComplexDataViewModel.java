package nl.hypothermic.windesmemes.android.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public abstract class ComplexDataViewModel<T, E> extends ViewModel {

    private MutableLiveData<T> cachedData;

    public LiveData<T> getData(E enumeration) {
        if (cachedData == null) {
            cachedData = loadData(enumeration);
        }
        return cachedData;
    }

    public ComplexDataViewModel<T, E> clearCache() {
        cachedData = null;
        return this;
    }

    protected abstract MutableLiveData<T> loadData(E enumeration);

}
