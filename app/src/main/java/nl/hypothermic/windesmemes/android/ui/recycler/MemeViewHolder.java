package nl.hypothermic.windesmemes.android.ui.recycler;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import nl.hypothermic.windesmemes.android.R;

public class MemeViewHolder extends RecyclerView.ViewHolder {

    public ImageView meme;
    public TextView title, username, date;

    public MemeViewHolder(View view) {
        super(view);
        meme = view.findViewById(R.id.card_image);
        title = view.findViewById(R.id.card_title);
        username = view.findViewById(R.id.card_user);
        date = view.findViewById(R.id.card_date);

        //noinspection SuspiciousNameCombination // Height van imageview moet width van view zijn.
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 400);
        meme.setLayoutParams(layoutParams);
    }
}