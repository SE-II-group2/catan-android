package com.group2.catan_android.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.group2.catan_android.R;
import com.group2.catan_android.data.model.DisplayablePlayer;
import com.group2.catan_android.databinding.GameItemBinding;
import com.group2.catan_android.databinding.PlayerInLobbyItemBinding;

import java.util.Collection;
import java.util.List;

public class PlayerListAdapter extends RecyclerView.Adapter<PlayerListAdapter.PlayerListViewHolder> {
    private List<DisplayablePlayer> players;
    private final Context context;
    public PlayerListAdapter(Context context){
        this.context = context;
        this.players = List.of();
    }
    public void setPlayers(List<DisplayablePlayer> players){
        this.players = players;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public PlayerListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PlayerInLobbyItemBinding itemBinding = PlayerInLobbyItemBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new PlayerListViewHolder(itemBinding, context);
    }
    @Override
    public void onBindViewHolder(@NonNull PlayerListViewHolder viewHolder, int position){
        viewHolder.setPlayerData(players.get(position));
    }

    @Override
    public int getItemCount(){return players.size();}

    public static class PlayerListViewHolder extends RecyclerView.ViewHolder{
        PlayerInLobbyItemBinding binding;
        Context context;
        PlayerListViewHolder(PlayerInLobbyItemBinding itemBinding, Context c){
            super(itemBinding.getRoot());
            binding = itemBinding;
            context = c;
        }

        void setPlayerData(DisplayablePlayer player){
            binding.playerName.setText(player.getDisplayName());
            binding.admin.setVisibility(player.isAdmin() ? View.VISIBLE : View.INVISIBLE);
            int color = 0;
            switch (player.getState()){
                case SOFT_JOINED:
                    color = ContextCompat.getColor(context, R.color.grey); break;
                case CONNECTED:
                    color = ContextCompat.getColor(context, R.color.green); break;
            }
            binding.connection.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }
}
