package com.group2.catan_android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group2.catan_android.R;

import java.util.List;

public class DevelopmentCardListAdapter extends RecyclerView.Adapter<DevelopmentCardListAdapter.DevelopmentCardViewHolder> {
    private List<String> items; // TODO: testing purposes

    public DevelopmentCardListAdapter(List<String> items) {
        this.items = items;
    }
    @NonNull
    @Override
    public DevelopmentCardListAdapter.DevelopmentCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.development_card, parent, false);
        return new DevelopmentCardListAdapter.DevelopmentCardViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull DevelopmentCardListAdapter.DevelopmentCardViewHolder holder, int position) {
        String item = items.get(position);
        // TODO: Add real images, not just placeholder
        holder.cardImage.setImageResource(R.drawable.development_card_placeholder);
        holder.cardTitle.setText(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class DevelopmentCardViewHolder extends RecyclerView.ViewHolder {
        TextView cardTitle;
        ImageView cardImage;
        public DevelopmentCardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardTitle = (TextView) itemView.findViewById(R.id.cardTitleText);
            cardImage = (ImageView) itemView.findViewById(R.id.cardImageView);
        }
    }
}
