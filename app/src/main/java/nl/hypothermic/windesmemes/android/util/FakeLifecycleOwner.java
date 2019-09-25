package nl.hypothermic.windesmemes.android.util;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import java.util.ArrayList;

public class FakeLifecycleOwner implements LifecycleOwner {

    private final MutableLifecycle lifecycle = new MutableLifecycle() {

        private State state = State.STARTED;

        private final ArrayList<LifecycleObserver> observers = new ArrayList<>();

        @Override
        public void addObserver(@NonNull LifecycleObserver observer) {
            observers.add(observer);
        }

        @Override
        public void removeObserver(@NonNull LifecycleObserver observer) {
            observers.remove(observer);
        }

        @NonNull
        @Override
        public State getCurrentState() {
            return state;
        }

        @Override
        public void setState(@NonNull State state) {
            this.state = state;
        }
    };

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return lifecycle;
    }

    public void die() {
        lifecycle.setState(Lifecycle.State.DESTROYED);
    }
}
