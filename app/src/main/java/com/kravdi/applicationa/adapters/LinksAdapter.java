package com.kravdi.applicationa.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kravdi.applicationa.R;
import com.kravdi.applicationa.activities.MainActivity;
import com.kravdi.applicationa.models.Links;

import java.util.ArrayList;

public class LinksAdapter extends RecyclerView.Adapter<LinksAdapter.ViewHolder> {

    public static final String LINK_STATE = "link_state";
    public static final String LINK_ID = "_ID";

    private ArrayList<Links> links;
    private Context context;

    public LinksAdapter(ArrayList<Links> links, Context context) {
        this.links = links;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.link_item, parent, false);
        final ViewHolder vh = new ViewHolder(v);
        vh.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.kravdi.applicationb");
                int position = vh.getAdapterPosition();
                launchIntent.putExtra(MainActivity.FROM_A, "from_history");
                launchIntent.putExtra(MainActivity.LINK_TAG, links.get(position).getLink());
                launchIntent.putExtra(LINK_STATE, links.get(position).getState());
                launchIntent.putExtra(LINK_ID, links.get(position).getId());
                context.startActivity(launchIntent);
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.link.setText(links.get(position).getLink());

        switch (links.get(position).getState()) {
            case 1:
                holder.cardView.setCardBackgroundColor(Color.GREEN);
                break;
            case 2:
                holder.cardView.setCardBackgroundColor(Color.RED);
                break;
            case 3:
                holder.cardView.setCardBackgroundColor(Color.GRAY);
        }
    }

    @Override
    public int getItemCount() {
        return links.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView link;
        private CardView cardView;

        public ViewHolder(final View itemView) {
            super(itemView);
            link = (TextView) itemView.findViewById(R.id.link_text);
            cardView = (CardView) itemView.findViewById(R.id.link_background);
        }
    }
}
