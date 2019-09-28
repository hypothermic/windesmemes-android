package nl.hypothermic.windesmemes.android.ui.recycler;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.List;

import nl.hypothermic.windesmemes.android.LogWrapper;
import nl.hypothermic.windesmemes.android.MainActivity;
import nl.hypothermic.windesmemes.android.R;
import nl.hypothermic.windesmemes.android.data.persistance.CachedAttributesDatabase;
import nl.hypothermic.windesmemes.android.data.persistance.MemeCachedAttributes;
import nl.hypothermic.windesmemes.model.Meme;
import nl.hypothermic.windesmemes.model.Vote;
import nl.hypothermic.windesmemes.retrofit.WindesMemesAPI;

public class MemeAdapter extends RecyclerView.Adapter<MemeViewHolder> {

    private List<Meme> memes;
    private Context ctx;

    public MemeAdapter(List<Meme> memes, Context ctx) {
        this.memes = memes;
        this.ctx = ctx;
    }

    @NonNull
    @Override
    public MemeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MemeViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_meme, parent, false));
    }

    @SuppressWarnings("SuspiciousNameCombination") /* ImageView height is hetzelfde als de width */
    @Override
    public void onBindViewHolder(@NonNull final MemeViewHolder holder, int position) {
        final Meme meme = memes.get(position);

        int height = holder.meme.getWidth();

        if (height < 10) {
            DisplayMetrics metrics = new DisplayMetrics();
            ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
            height = metrics.widthPixels;
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        holder.meme.setLayoutParams(layoutParams);

        // If local cache database contains meme image, load from cache. Otherwise, request it via the API.
        final LiveData<MemeCachedAttributes> liveData = CachedAttributesDatabase.getInstance(ctx).getMemeCachedAttributesDao().get(meme.imageUrl);
        liveData.observe(((MainActivity) ctx), new Observer<MemeCachedAttributes>() {
            @Override
            public void onChanged(MemeCachedAttributes memeCachedAttributes) {
                LogWrapper.info(this, "Queried cache for attribute %s", meme.imageUrl);
                liveData.removeObserver(this);
                if (memeCachedAttributes != null) {
                    // Load meme from database result
                    byte[] cachedImage = memeCachedAttributes.getCachedImage();
                    Bitmap decompressed = BitmapFactory.decodeByteArray(cachedImage, 0, cachedImage.length);

                    if (decompressed == null) {
                        LogWrapper.error(this, "Error while decompressing image. TODO show error message and return"); // TODO
                    }
                    LogWrapper.info(this, "Loaded meme from cache %s", meme.imageUrl);
                    holder.meme.setImageBitmap(decompressed);
                } else {
                    // Retrieve meme from API.
                    Picasso.get().load(WindesMemesAPI.BASE_URL + meme.imageUrl).fit().centerInside().into(holder.meme, new Callback() {
                        @Override
                        public void onSuccess() {
                            // Save to local cache database
                            final Drawable drawable = holder.meme.getDrawable();

                            if (drawable instanceof BitmapDrawable) {
                                CachedAttributesDatabase.THREAD.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                        ((BitmapDrawable) drawable).getBitmap().compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                                        CachedAttributesDatabase.getInstance(ctx).getMemeCachedAttributesDao()
                                                .save(new MemeCachedAttributes(meme.imageUrl, outputStream.toByteArray()));
                                        LogWrapper.info(this, "Loaded meme from API %s", meme.imageUrl);
                                    }
                                });
                            } else {
                                LogWrapper.error(this, "TODO error handling"); // TODO
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            LogWrapper.error(this, "TODO error handling"); // TODO
                        }
                    });
                }
            }
        });

        holder.title.setText(meme.title);
        holder.username.setText(meme.username);
        holder.date.setText(meme.date);

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogWrapper.error(this, "TODO show user profile");
            }
        });

        final Observer<Vote> voteObserver = new Observer<Vote>() {
            @Override
            public void onChanged(Vote vote) {
                holder.vote.setText(meme.parseKarma() + vote.getWeight() + "");
                LogWrapper.info(this, "KARMA: %s (%d) [%s]", meme.karma, meme.parseKarma() + meme.parseVote(), meme.vote);
                LogWrapper.error(this, "TODO send cast vote result to server");
                switch (vote) {
                    case UPVOTE:
                        holder.upvote  .setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.ic_arrow_upward_green_24dp));
                        holder.downvote.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.ic_arrow_downward_black_24dp));
                        break;
                    case NEUTRAL:
                        holder.upvote  .setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.ic_arrow_upward_black_24dp));
                        holder.downvote.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.ic_arrow_downward_black_24dp));
                        break;
                    case DOWNVOTE:
                        holder.upvote  .setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.ic_arrow_upward_black_24dp));
                        holder.downvote.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.ic_arrow_downward_red_24dp));
                        break;
                }
            }
        };

        // Trigger initial
        voteObserver.onChanged(Vote.fromIndex(meme.parseVote()));

        // Trigger on user input
        View.OnClickListener upvoteListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voteObserver.onChanged(Vote.UPVOTE);
            }
        };
        holder.vote.setOnClickListener(upvoteListener);
        holder.upvote.setOnClickListener(upvoteListener);
        holder.downvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voteObserver.onChanged(Vote.DOWNVOTE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return memes.size();
    }
}
