package com.group2.catan_android.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.group2.catan_android.R;
import com.group2.catan_android.gamelogic.enums.ProgressCardType;

import java.util.ArrayList;
import java.util.List;

public class DevelopmentCardListAdapter extends RecyclerView.Adapter<DevelopmentCardListAdapter.DevelopmentCardViewHolder> {
    private List<ProgressCardType> items;
    private OnDevelopmentCardClickListener listener;
    public DevelopmentCardListAdapter(OnDevelopmentCardClickListener listener) {
        this.items = new ArrayList<>();
        this.listener = listener;
    }
    public void setItems(List<ProgressCardType> items) {
        this.items = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public DevelopmentCardListAdapter.DevelopmentCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.development_card, parent, false);
        return new DevelopmentCardListAdapter.DevelopmentCardViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull DevelopmentCardListAdapter.DevelopmentCardViewHolder holder, int position) {
        ProgressCardType item = items.get(position);
        holder.cardImage.setImageResource(getImageResourceForCardType(item));
        holder.cardTitle.setText(getTextResourceForCardType(item));
        holder.itemView.setOnClickListener(v -> listener.onDevelopmentCardClick(item));
    }
    private int getImageResourceForCardType(ProgressCardType cardType) {
        switch (cardType) {
            case KNIGHT:
                return R.drawable.development_card_knight;
            case ROAD_BUILDING:
                return R.drawable.development_card_roadbuilding;
            case YEAR_OF_PLENTY:
                return R.drawable.development_card_yearofplenty;
            case MONOPOLY:
                return R.drawable.development_card_monopoly;
            case VICTORY_POINT:
                return R.drawable.development_card_victorypoint;
            default:
                return R.drawable.development_card_placeholder;
        }
    }

    private int getTextResourceForCardType(ProgressCardType cardType){
        switch(cardType) {
            case KNIGHT:
                return R.string.developmentcard_text_knight;
            case ROAD_BUILDING:
                return R.string.developmentcard_text_roadbuilding;
            case YEAR_OF_PLENTY:
                return R.string.developmentcard_text_yearofplenty;
            case MONOPOLY:
                return R.string.developmentcard_text_monopoly;
            case VICTORY_POINT:
                return R.string.developmentcard_text_victorypoint;
            default:
                return R.string.developmentcard_text_default;
        }
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
            cardTitle = itemView.findViewById(R.id.cardTitleText);
            cardImage = itemView.findViewById(R.id.cardImageView);
        }
    }
}
