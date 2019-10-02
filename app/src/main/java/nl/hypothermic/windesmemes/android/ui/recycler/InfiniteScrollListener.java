package nl.hypothermic.windesmemes.android.ui.recycler;

import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import nl.hypothermic.windesmemes.android.LogWrapper;

public class InfiniteScrollListener extends RecyclerView.OnScrollListener {

    private final RecyclerView recyclerView;
    private final LinearLayoutManager layoutManager;
    private final Observer<ObserverData<Void>> reachedEndObserver;

    private int previousTotal = 0;
    private boolean loading = true;
    private int visibleThreshold = 1;
    private int firstVisibleItem, visibleItemCount, totalItemCount;

    public InfiniteScrollListener(RecyclerView recyclerView, LinearLayoutManager layoutManager, Observer<ObserverData<Void>> reachedEndObserver) {
        this.recyclerView = recyclerView;
        this.layoutManager = layoutManager;
        this.reachedEndObserver = reachedEndObserver;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = layoutManager.getItemCount();
        firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + visibleThreshold)) {

            reachedEndObserver.onChanged(new ObserverData<>(new Observer<Void>() {
                @Override
                public void onChanged(Void aVoid) {
                    loading = false;
                }
            }, totalItemCount));

            loading = true;
        }
    }

    public static class ObserverData<T> {

        private final Observer<T> onLoadingDoneCallback;
        private final int totalItemCount;

        public ObserverData(Observer<T> onLoadingDoneCallback, int totalItemCount) {
            this.onLoadingDoneCallback = onLoadingDoneCallback;
            this.totalItemCount = totalItemCount;
        }

        public Observer<T> getOnLoadingDoneCallback() {
            return onLoadingDoneCallback;
        }

        public int getTotalItemCount() {
            return totalItemCount;
        }
    }
}
