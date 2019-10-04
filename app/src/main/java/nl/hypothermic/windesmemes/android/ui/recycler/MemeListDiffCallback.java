package nl.hypothermic.windesmemes.android.ui.recycler;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;
import java.util.Objects;

import nl.hypothermic.windesmemes.model.Meme;

public class MemeListDiffCallback extends DiffUtil.Callback {

    public static final String KEY_VOTE  = "v",
                               KEY_KARMA = "k";

    private List<Meme> mOldList;
    private List<Meme> mNewList;

    public MemeListDiffCallback(List<Meme> oldList, List<Meme> newList) {
        this.mOldList = oldList;
        this.mNewList = newList;
    }

    @Override
    public int getOldListSize() {
        return mOldList != null ? mOldList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return mNewList != null ? mNewList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mNewList.get(newItemPosition).id == mOldList.get(oldItemPosition).id;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return mNewList.get(newItemPosition).equals(mOldList.get(oldItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        Meme newMeme = mNewList.get(newItemPosition);
        Meme oldMeme = mOldList.get(oldItemPosition);

        Bundle diffBundle = new Bundle();
        if (!Objects.equals(newMeme.vote, oldMeme.vote)) {
            diffBundle.putString(KEY_VOTE, newMeme.vote);
        }
        if (!Objects.equals(newMeme.karma, oldMeme.karma)) {
            diffBundle.putString(KEY_KARMA, newMeme.karma);
        }

        if (diffBundle.size() == 0) {
            return null;
        } else {
            return diffBundle;
        }
    }
}