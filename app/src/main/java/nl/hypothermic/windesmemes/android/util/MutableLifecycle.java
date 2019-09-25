package nl.hypothermic.windesmemes.android.util;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;

public abstract class MutableLifecycle extends Lifecycle {

    public abstract void setState(@NonNull State state);

}
