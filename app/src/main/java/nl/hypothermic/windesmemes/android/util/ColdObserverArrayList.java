package nl.hypothermic.windesmemes.android.util;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Similar to cold observables in RxJava/etc, but this caches only the LATEST announced value.
 */
public class ColdObserverArrayList<T> extends ArrayList<Observer<T>> {

    private T latestAnnouncedValue;

    public void announce(T object) {
        latestAnnouncedValue = object;

        for (Observer<T> observer : this) {
            observer.onChanged(object);
        }
    }

    @Override
    public boolean add(Observer<T> element) {
        element.onChanged(latestAnnouncedValue);
        return super.add(element);
    }

    @Override
    public void add(int index, Observer<T> element) {
        element.onChanged(latestAnnouncedValue);
        super.add(index, element);
    }

    @Override
    public boolean addAll(@NonNull Collection<? extends Observer<T>> collection) {
        for (Observer<T> observer : collection) {
            observer.onChanged(latestAnnouncedValue);
        }
        return super.addAll(collection);
    }

    @Override
    public boolean addAll(int index, @NonNull Collection<? extends Observer<T>> collection) {
        for (Observer<T> observer : collection) {
            observer.onChanged(latestAnnouncedValue);
        }
        return super.addAll(index, collection);
    }
}
