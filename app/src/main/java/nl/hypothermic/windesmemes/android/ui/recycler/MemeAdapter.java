package nl.hypothermic.windesmemes.android.ui.recycler;

import android.annotation.SuppressLint;
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
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.List;

import nl.hypothermic.windesmemes.android.LogWrapper;
import nl.hypothermic.windesmemes.android.MainActivity;
import nl.hypothermic.windesmemes.android.R;
import nl.hypothermic.windesmemes.android.auth.AuthenticationManager;
import nl.hypothermic.windesmemes.android.data.VoteAction;
import nl.hypothermic.windesmemes.android.data.VoteAction.Priority;
import nl.hypothermic.windesmemes.android.data.persistance.CachedAttributesDatabase;
import nl.hypothermic.windesmemes.android.data.persistance.MemeCachedAttributes;
import nl.hypothermic.windesmemes.android.util.ImageViewUtil;
import nl.hypothermic.windesmemes.model.Meme;
import nl.hypothermic.windesmemes.model.Vote;
import nl.hypothermic.windesmemes.retrofit.WindesMemesAPI;

public class MemeAdapter extends RecyclerView.Adapter<MemeViewHolder> {

    private List<Meme> memes;
    private Context ctx;
    private View view;

    public MemeAdapter(List<Meme> memes, Context ctx, View view) {
        this.memes = memes;
        this.ctx = ctx;
        this.view = view;
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

        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        holder.meme.setLayoutParams(layoutParams);

        // If local cache database contains meme image, load from cache. Otherwise, request it via the API.
        final LiveData<MemeCachedAttributes> liveData = CachedAttributesDatabase.getInstance(ctx).getMemeCachedAttributesDao().get(meme.imageUrl);
        liveData.observe(((MainActivity) ctx), new Observer<MemeCachedAttributes>() {
            @Override
            public void onChanged(MemeCachedAttributes memeCachedAttributes) {
                liveData.removeObserver(this);
                if (memeCachedAttributes != null) {
                    // Load meme from database result
                    byte[] cachedImage = memeCachedAttributes.getCachedImage();
                    Bitmap decompressed = BitmapFactory.decodeByteArray(cachedImage, 0, cachedImage.length);

                    if (decompressed == null) {
                        LogWrapper.error(this, "Error while decompressing image. TODO show error message and return"); // TODO
                    }
                    holder.meme.setImageBitmap(decompressed);
                    ImageViewUtil.shrink(holder.meme, 16);
                } else {
                    // Retrieve meme from API.
                    Picasso.get().load(WindesMemesAPI.BASE_URL + meme.imageUrl).fit().centerInside().into(holder.meme, new Callback() {
                        @Override
                        public void onSuccess() {
                            // If landscape img, adjust imageview height
                            ImageViewUtil.shrink(holder.meme, 16);

                            // Save to local cache database
                            final Drawable drawable = holder.meme.getDrawable();
                            if (drawable instanceof BitmapDrawable) {
                                CachedAttributesDatabase.IO_THREAD.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                                        ((BitmapDrawable) drawable).getBitmap().compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                                        CachedAttributesDatabase.getInstance(ctx).getMemeCachedAttributesDao()
                                                .save(new MemeCachedAttributes(meme.imageUrl, outputStream.toByteArray(), Vote.fromIndex(meme.parseVote())));
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

        final Observer<VoteAction> voteObserver = new Observer<VoteAction>() {

            private Priority currentPriority = Priority.LOWEST;

            @SuppressLint("SetTextI18n") /* Geen internationalization omdat het een getal is */
            @Override
            public void onChanged(final VoteAction action) {
                if (!action.getPriority().isHigherThan(currentPriority)) {
                    return;
                }
                this.currentPriority = action.getPriority();

                holder.vote.setText(meme.parseKarma() + action.getVote().getWeight() + "");
                switch (action.getVote()) {
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
                if (action.isTriggeredByUser() && action.getVote() != Vote.NEUTRAL) {
                    AuthenticationManager.acquire(ctx).vote(action.getVote(), meme.id, new Observer<Integer>() {
                        @Override
                        public void onChanged(Integer integer) {
                            if (integer != null) {
                                Snackbar.make(view, ctx.getString(integer), Snackbar.LENGTH_LONG).show();
                            }
                        }
                    });
                    CachedAttributesDatabase.IO_THREAD.execute(new Runnable() {
                        @Override
                        public void run() {
                            CachedAttributesDatabase.getInstance(ctx).getMemeCachedAttributesDao().updateVote(meme.imageUrl, action.getVote());
                        }
                    });
                }
            }
        };

        // Get from database (be aware, callback hell upcoming)
        CachedAttributesDatabase.IO_THREAD.execute(new Runnable() {
            @Override
            public void run() {
                ((MainActivity) ctx).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CachedAttributesDatabase.getInstance(ctx).getMemeCachedAttributesDao().get(meme.imageUrl).observe((LifecycleOwner) ctx, new Observer<MemeCachedAttributes>() {
                            @Override
                            public void onChanged(MemeCachedAttributes memeCachedAttributes) {
                                if (memeCachedAttributes != null) {
                                    voteObserver.onChanged(new VoteAction(memeCachedAttributes.getVote(), false, Priority.CACHE));
                                }
                            }
                        });
                    }
                });
            }
        });

        // Trigger initial /// TODO REMOVE "NEUTRAL" CHECK AFTER THE GETMEMES TOKEN STUFF IS FIXED
        Vote vote = Vote.fromIndex(meme.parseVote());
        if (vote != Vote.NEUTRAL) {
            voteObserver.onChanged(new VoteAction(vote, false, Priority.LIVE));
        }

        // Trigger on user input
        View.OnClickListener upvoteListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voteObserver.onChanged(new VoteAction(Vote.UPVOTE, true, Priority.USER));
            }
        };
        holder.vote.setOnClickListener(upvoteListener);
        holder.upvote.setOnClickListener(upvoteListener);
        holder.downvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voteObserver.onChanged(new VoteAction(Vote.DOWNVOTE, true, Priority.USER));
            }
        });
    }

    @Override
    public int getItemCount() {
        return memes.size();
    }

    public List<Meme> getMemes() {
        return memes;
    }
}
