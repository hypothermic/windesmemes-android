package nl.hypothermic.windesmemes.android.ui.recycler;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import nl.hypothermic.windesmemes.android.LogWrapper;
import nl.hypothermic.windesmemes.android.R;
import nl.hypothermic.windesmemes.model.Meme;
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
    public void onBindViewHolder(@NonNull MemeViewHolder holder, int position) {
        Meme meme = memes.get(position);

        int height = holder.meme.getWidth();

        if (height < 10) {
            DisplayMetrics metrics = new DisplayMetrics();
            ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
            height = metrics.widthPixels;
        }

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        holder.meme.setLayoutParams(layoutParams);
        Picasso.get().load(WindesMemesAPI.BASE_URL + meme.imageUrl).fit().centerInside().into(holder.meme);

        holder.title.setText(meme.title);
        holder.username.setText(meme.username);
        holder.date.setText(meme.date);

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogWrapper.error(this, "TODO show user profile");
            }
        });
    }

    @Override
    public int getItemCount() {
        return memes.size();
    }

}
